package umc.wegg.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.converter.UserConverter;
import umc.wegg.domain.User;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;
import umc.wegg.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDTO.UserJoinResultDTO joinUser(UserRequestDTO.UserJoinDto request) {
        try {
            User user = UserConverter.toUser(request);
            user.encodePassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);

            List<UserResponseDTO.UserJoinResultDTO.ExistingUserDTO> existingUsers = new ArrayList<>();
            if (request.getContact() != null) {
                for (UserRequestDTO.ContactDto contact : request.getContact()) {
                    userRepository.findByPhone(contact.getPhone())
                            .ifPresent(existingUser -> {
                                existingUsers.add(new UserResponseDTO.UserJoinResultDTO.ExistingUserDTO(
                                        contact.getContactName(), // 요청에서 받은 contactName
                                        existingUser.getName(), // 기존 사용자 이름
                                        existingUser.getPhone() // 기존 사용자 전화번호
                                ));
                            });
                }

    @Override
    @Transactional
    public UserResponseDTO.OAuth2UserJoinResultDTO oAuth2JoinUser(UserRequestDTO.OAuth2UserJoinDto request) {

        // 1. OAuth2 인증 정보를 기반으로 사용자 식별자 가져오기
        String oauthId = request.getOauthId();

        // 2. 사용자 존재 여부 확인
        boolean isExistingUser = userRepository.existsByOauthId(oauthId);
        if (isExistingUser) {
            //exception throw
        }

        request.setPassword(passwordEncoder.encode("OAUTH_USER_" + UUID.randomUUID()));

        User user = UserConverter.toOAuthUser(request);
        userRepository.save(user);

        List<UserResponseDTO.OAuth2UserJoinResultDTO.ExistingUserDTO> existingUsers = new ArrayList<>();
        if (request.getContact() != null) {
            for (UserRequestDTO.ContactDto contact : request.getContact()) {
                userRepository.findByPhone(contact.getPhone())
                        .ifPresent(existingUser -> {
                            existingUsers.add(new UserResponseDTO.OAuth2UserJoinResultDTO.ExistingUserDTO(
                                    contact.getContactName(),
                                    existingUser.getName(),
                                    existingUser.getPhone()
                            ));
                        });
            }
        }

        return UserConverter.toOAuth2JoinResultDTO(user, existingUsers);
    }

    @Override
    public UserResponseDTO.OAuth2LoginResultDTO oAuth2LoginUser(HttpServletRequest request, OAuth2User oauth2User) {

        // oauthId로 DB에서 사용자 확인
        String oauthId = (String) oauth2User.getAttributes().get("oauthId");
        String provider = (String) oauth2User.getAttributes().get("provider");

        Optional<User> existingUser = userRepository.findByOauthId(oauthId);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // OAuth2User를 AuthenticatedUser로 변환
            AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getId(), user.getEmail());  // 예시로 existingUser를 AuthenticatedUser로 변환

            // Spring Security에서 인증된 사용자로 설정
            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new UserResponseDTO.OAuth2LoginResultDTO(true, provider,oauthId);
        } else {
            // 세션 만료
            HttpSession session = request.getSession(false); // 기존 세션 가져오기
            if (session != null) {
                session.invalidate(); // 세션 무효화
            }

            // SecurityContext에서 인증 정보 삭제
            SecurityContextHolder.clearContext();

            //exception throw
            throw new UsernameNotFoundException("Username not found");
        }
    }

        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAccountIdDuplication(String accountId) {
        return userRepository.findByAccountId(accountId).isPresent();
    }
}

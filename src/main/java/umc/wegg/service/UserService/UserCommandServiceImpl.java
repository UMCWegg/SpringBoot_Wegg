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
import umc.wegg.util.RedisUtil;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService{

    private final UserRepository userRepository;

    private final RedisUtil redisUtil;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDTO.UserJoinResultDTO joinUser(UserRequestDTO.UserJoinDto request) {
        User user = UserConverter.toUser(request);
        user.encodePassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        List<UserResponseDTO.UserJoinResultDTO.ExistingUserDTO> existingUsers = new ArrayList<>();
        if (request.getContact() != null) {
            for (UserRequestDTO.ContactDto contact : request.getContact()) {
                userRepository.findByPhone(contact.getPhone())
                        .ifPresent(existingUser -> {
                            existingUsers.add(new UserResponseDTO.UserJoinResultDTO.ExistingUserDTO(
                                    contact.getContactName(),
                                    existingUser.getName(),
                                    existingUser.getPhone()
                            ));
                        });
            }
        }

        return UserConverter.toJoinResultDTO(user, existingUsers);
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

    @Override
    public UserResponseDTO.UserDeleteResultDTO deleteUser(AuthenticatedUser authenticatedUser) {

        if (authenticatedUser == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        Long userId = authenticatedUser.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. "));

        userRepository.delete(user);

        return new UserResponseDTO.UserDeleteResultDTO(true, userId);
    }

    @Override
    public UserResponseDTO.UserUpdateResultDTO updateUser(AuthenticatedUser authenticatedUser, UserRequestDTO.UserUpdateDto request) {

        if (authenticatedUser == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        Long userId = authenticatedUser.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. "));

        // 수정된 필드를 저장할 Map
        Map<String, Object> updatedFields = new HashMap<>();

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
            updatedFields.put("name", request.getName());
        }

        if (request.getAccountId() != null && !request.getAccountId().isEmpty()) {
            user.setAccountId(request.getAccountId());
            updatedFields.put("accountId", request.getAccountId());
        }

        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            user.setProfileImage(request.getProfileImage());
            updatedFields.put("profileImage", request.getProfileImage());
        }

        userRepository.save(user);

        return new UserResponseDTO.UserUpdateResultDTO(true, updatedFields);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO.CheckAccountIdResultDTO checkAccountIdDuplication(String accountId) {
        boolean isDuplicate = userRepository.existsByAccountId(accountId);

        String message = isDuplicate
                ? "이미 사용 중인 아이디입니다."
                : "사용 가능한 아이디입니다.";

        return new UserResponseDTO.CheckAccountIdResultDTO(isDuplicate, message);
    }

    public UserResponseDTO.VerifyNumberResultDTO verityNumber(UserRequestDTO.VerifyNumberDto request) {

        if (!request.validateFormat()) {
            //exception throw
        }

        String target = request.getTarget();

        String savedNumber = redisUtil.getData(target);
        if (savedNumber == null) {
            //exception throw 하기
        }

        String number = request.getNumber();

        if (savedNumber.equals(number)) {
            return new UserResponseDTO.VerifyNumberResultDTO(true);

        } else {
            return new UserResponseDTO.VerifyNumberResultDTO(false);
        }
    }
}

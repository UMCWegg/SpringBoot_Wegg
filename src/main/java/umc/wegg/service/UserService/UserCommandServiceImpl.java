package umc.wegg.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.wegg.converter.UserConverter;
import umc.wegg.domain.User;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;
import umc.wegg.repository.UserRepository.UserRepository;

import java.util.ArrayList;
import java.util.List;

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
            }

            return UserConverter.toJoinResultDTO(user, existingUsers);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 디버깅 출력
            throw e;
        }
    }
}

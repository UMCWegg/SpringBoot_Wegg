package umc.wegg.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;

public interface UserCommandService {
    UserResponseDTO.UserJoinResultDTO joinUser(UserRequestDTO.UserJoinDto request);
    UserResponseDTO.OAuth2UserJoinResultDTO oAuth2JoinUser(UserRequestDTO.OAuth2UserJoinDto request);
    UserResponseDTO.OAuth2LoginResultDTO oAuth2LoginUser(HttpServletRequest request, OAuth2User oauth2User);
    UserResponseDTO.UserDeleteResultDTO deleteUser(AuthenticatedUser authenticatedUser);
    UserResponseDTO.UserUpdateResultDTO updateUser(AuthenticatedUser authenticatedUser, UserRequestDTO.UserUpdateDto request);
    UserResponseDTO.VerifyNumberResultDTO verityNumber(UserRequestDTO.VerifyNumberDto request);
}

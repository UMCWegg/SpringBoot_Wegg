package umc.wegg.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.multipart.MultipartFile;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;

import java.io.IOException;
import java.util.List;

public interface UserCommandService {
    UserResponseDTO.UserJoinResultDTO joinUser(UserRequestDTO.UserJoinDto request);
    UserResponseDTO.OAuth2UserJoinResultDTO oAuth2JoinUser(UserRequestDTO.OAuth2UserJoinDto request);
    UserResponseDTO.OAuth2LoginResultDTO oAuth2LoginUser(HttpServletRequest request, OAuth2User oauth2User);
    UserResponseDTO.UserDeleteResultDTO deleteUser(AuthenticatedUser authenticatedUser);
    UserResponseDTO.UserUpdateResultDTO updateUser(AuthenticatedUser authenticatedUser, UserRequestDTO.UserUpdateDto request, MultipartFile profilePicture) throws IOException;
    UserResponseDTO.CheckAccountIdResultDTO checkAccountIdDuplication(String accountId);
    UserResponseDTO.VerifyNumberResultDTO verityNumber(UserRequestDTO.VerifyNumberDto request);
    List<UserResponseDTO.UserSearchDTO> searchUsersByAccountId(String keyword);

}

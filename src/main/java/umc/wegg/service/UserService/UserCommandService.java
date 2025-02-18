package umc.wegg.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;

import java.io.IOException;
import java.util.List;

public interface UserCommandService {
    UserResponseDTO.UserJoinResultDTO joinUser(UserRequestDTO.UserJoinDto request);
    UserResponseDTO.OAuth2UserJoinResultDTO oAuth2JoinUser(UserRequestDTO.OAuth2UserJoinDto request);
    ApiResponse<UserResponseDTO.LoginResultDTO> oAuth2LoginUser(UserRequestDTO.OAuth2LoginRequestDTO request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
    UserResponseDTO.UserDeleteResultDTO deleteUser(AuthenticatedUser authenticatedUser);
    UserResponseDTO.UserUpdateResultDTO updateUser(AuthenticatedUser authenticatedUser, UserRequestDTO.UserUpdateDto request, MultipartFile profilePicture) throws IOException;
    UserResponseDTO.CheckAccountIdResultDTO checkAccountIdDuplication(String accountId);
    UserResponseDTO.CheckEmailResultDTO checkEmailDuplication(String email);
    UserResponseDTO.VerifyNumberResultDTO verityNumber(UserRequestDTO.VerifyNumberDto request);

    UserResponseDTO.ContactUpdateResultDTO updateContactList(AuthenticatedUser authenticatedUser, List<UserRequestDTO.ContactDto> newContacts);
    List<UserResponseDTO.UserSearchDTO> searchUsersByAccountId(String keyword);

}

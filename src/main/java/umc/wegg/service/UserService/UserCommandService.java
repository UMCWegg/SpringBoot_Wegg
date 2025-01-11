package umc.wegg.service.UserService;

import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;

public interface UserCommandService {
    UserResponseDTO.UserJoinResultDTO joinUser(UserRequestDTO.UserJoinDto request);
    boolean checkAccountIdDuplication(String accountId);
}

package umc.wegg.service.MailService;

import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;

public interface MailService {
    UserResponseDTO.VerificationResultDTO sendMail(UserRequestDTO.SendEmailVerificationDto request);
}

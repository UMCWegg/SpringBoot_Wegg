package umc.wegg.service.SmsService;

import umc.wegg.dto.UserRequestDTO;

public interface SmsService {
    String sendSms(UserRequestDTO.SendPhoneVerificationDto request);
}

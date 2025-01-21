package umc.wegg.service.SmsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.util.SmsCertificationUtil;

@Service
public class SmsServiceImpl implements SmsService {

    private final SmsCertificationUtil smsCertificationUtil;

    //의존성 주입
    public SmsServiceImpl(@Autowired SmsCertificationUtil smsCertificationUtil) {
        this.smsCertificationUtil = smsCertificationUtil;
    }

    @Override // SmsService 인터페이스 메서드 구현
    public String sendSms(UserRequestDTO.SendPhoneVerificationDto request) {
        String phone = request.getPhone();
        // 인증번호 생성
        String certificationCode = Integer.toString((int) (Math.random() * 900000) + 100000);
        // SMS 전송
        smsCertificationUtil.sendSMS(phone, certificationCode);

        // 메시지 반환
        return "인증번호가 전송되었습니다.";
    }
}

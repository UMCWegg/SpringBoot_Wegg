package umc.wegg.service.SmsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.util.RedisUtil;
import umc.wegg.util.SmsCertificationUtil;

@Service
public class SmsServiceImpl implements SmsService {

    private final SmsCertificationUtil smsCertificationUtil;
    private final RedisUtil redisUtil;

    //의존성 주입
    public SmsServiceImpl(@Autowired SmsCertificationUtil smsCertificationUtil, RedisUtil redisUtil) {
        this.smsCertificationUtil = smsCertificationUtil;
        this.redisUtil = redisUtil;
    }

    @Override // SmsService 인터페이스 메서드 구현
    public String sendSms(UserRequestDTO.SendPhoneVerificationDto request) {
        String phone = request.getPhone();

        if (redisUtil.existData(phone)) {
            redisUtil.deleteData(phone);
        }

        // 인증번호 생성 (6자리)
        String number = String.format("%06d", (int) (Math.random() * 1000000));
        // SMS 전송
        smsCertificationUtil.sendSMS(phone, number);

        redisUtil.setDataExpire(phone, number, 3 * 60); //3분

        // 메시지 반환
        return "인증번호가 전송되었습니다.";
    }
}

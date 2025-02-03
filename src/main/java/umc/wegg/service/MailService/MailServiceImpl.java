package umc.wegg.service.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;
import umc.wegg.util.EmailUtil;
import umc.wegg.util.RedisUtil;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final EmailUtil emailUtil;
    private final RedisUtil redisUtil;

    private static final String senderEmail = "${spring.mail.username}";

    @Override
    public UserResponseDTO.VerificationResultDTO sendMail(UserRequestDTO.SendEmailVerificationDto request) {
        // 인증번호 생성 (6자리)
        String number = String.format("%06d", (int) (Math.random() * 1000000));
        String sendEmail = request.getEmail();
        String subject = "이메일 인증";
        String body = "요청하신 인증 번호입니다.<p><h3>" + number + "</h3>";

        if (redisUtil.existData(sendEmail)) {
            redisUtil.deleteData(sendEmail);
        }

        try {
            MimeMessage message = emailUtil.createMail(senderEmail, sendEmail, subject, body);
            emailUtil.sendMail(message);

            redisUtil.setDataExpire(sendEmail, number, 3 * 60); //3분
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return new UserResponseDTO.VerificationResultDTO("인증번호가 전송되었습니다.");
    }
}

package umc.wegg.service.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;
import umc.wegg.util.EmailUtil;
import umc.wegg.util.RedisUtil;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final EmailUtil emailUtil;
    private final RedisUtil redisUtil;

    private static final String senderEmail = "${spring.mail.username}";

    @Override
    public UserResponseDTO.VerificationResultDTO sendMail(UserRequestDTO.SendEmailVerificationDto request) {
        String number = createNumber(); // 랜덤 인증번호 생성
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

    private String createNumber() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) { // 인증 코드 8자리
            int index = random.nextInt(3);

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 97)); // 소문자
                case 1 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 2 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }
}

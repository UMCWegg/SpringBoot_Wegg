package umc.wegg.service.PurchaseService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.wegg.domain.Template;
import umc.wegg.domain.User;
import umc.wegg.domain.enums.TemplateType;
import umc.wegg.domain.mapping.MyTemplate;
import umc.wegg.dto.PurchaseResponseDTO.MypointResponseDTO;
import umc.wegg.repository.MyTemplateRepository;
import umc.wegg.repository.TemplateRepository;
import umc.wegg.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PurchaseCommandServiceImpl implements PurchaseCommandService {

    private final UserRepository userRepository;
    private final TemplateRepository templateRepository;
    private final MyTemplateRepository myTemplateRepository;

    @Transactional
    public boolean purchaseTemplate(Long userId, TemplateType templateType) {
        // 1. 현재 로그인된 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. 구매할 템플릿 조회
        Template template = templateRepository.findByTemplateType(templateType)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateType));

        // 3. 유저가 해당 템플릿을 이미 소유하고 있는지 확인
        boolean alreadyOwned = myTemplateRepository.existsByUserAndTemplate(user, template);
        if (alreadyOwned) {
            throw new IllegalStateException("이미 보유한 템플릿입니다.");
        }

        // 4. 포인트가 충분한지 확인
        int templateCost = template.getCost();
        if (user.getPoints() < templateCost) {
            return false; // 포인트 부족
        }

        // 5. 템플릿 소유 목록에 추가
        MyTemplate myTemplate = MyTemplate.builder()
                .user(user)
                .template(template)
                .build();
        myTemplateRepository.save(myTemplate);

        // 6. 포인트 차감 후 저장
        user.setPoints(user.getPoints() - templateCost);
        userRepository.save(user);

        return true;
    }


    @Override
    public MypointResponseDTO getUserPoints(Long userId) {
        return userRepository.findById(userId)
                .map(user -> MypointResponseDTO.builder()
                        .userId(user.getId())
                        .points(user.getPoints())
                        .build())
                .orElse(null); // 또는 적절한 예외 처리
    }
}

package umc.wegg.service.PurchaseService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.dto.PurchaseResponseDTO.MypointResponseDTO;
import umc.wegg.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PurchaseCommandServiceImpl implements PurchaseCommandService {

    private final UserRepository userRepository;

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

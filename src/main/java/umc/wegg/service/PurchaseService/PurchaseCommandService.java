package umc.wegg.service.PurchaseService;

import umc.wegg.domain.enums.TemplateType;
import umc.wegg.dto.PurchaseResponseDTO.MypointResponseDTO;

public interface PurchaseCommandService {
    MypointResponseDTO getUserPoints(Long userId);
    boolean purchaseTemplate(Long userId, TemplateType templateType);
}

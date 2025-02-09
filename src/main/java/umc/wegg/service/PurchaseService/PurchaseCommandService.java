package umc.wegg.service.PurchaseService;

import umc.wegg.dto.PurchaseResponseDTO.MypointResponseDTO;

public interface PurchaseCommandService {
    MypointResponseDTO getUserPoints(Long userId);
}

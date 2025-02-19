package umc.wegg.service.MapService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class MapQueryServiceImpl implements MapQueryService{

    private final AddressRepository addressRepository;

    @Override
    public boolean existsById(Long id){
        return addressRepository.existsById(id);
    }
}

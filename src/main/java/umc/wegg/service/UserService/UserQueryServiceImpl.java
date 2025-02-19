package umc.wegg.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public boolean  existsById(Long id){
        return userRepository.existsById(id);
    }
}

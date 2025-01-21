package umc.wegg.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import umc.wegg.domain.User;
import umc.wegg.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 1. DB에서 username으로 Member를 찾음
        UserAuthenticationToken authenticationToken = (UserAuthenticationToken) authentication;
        String email = String.valueOf(authenticationToken.getPrincipal());

        User findUser = userRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException("아이디를 찾을 수 없습니다."));

        // 2. DB와 password가 일치하는지 검증
        String password = authenticationToken.getCredential();
        if (!passwordEncoder.matches(password, findUser.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 인증 성공 시 Authentication 토큰 생성
        AuthenticatedUser authenticatedMember = new AuthenticatedUser(findUser.getId(), findUser.getEmail());
        return UserAuthenticationToken.authenticated(authenticatedMember);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

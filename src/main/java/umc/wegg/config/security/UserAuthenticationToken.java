package umc.wegg.config.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import umc.wegg.dto.UserRequestDTO;

@Getter
public class UserAuthenticationToken extends AbstractAuthenticationToken {

    private Object principal;
    private String credential;

    private UserAuthenticationToken(String email, String password) {
        super(null);
        this.principal = email;
        this.credential = password;
        this.setAuthenticated(false);
    }

    private UserAuthenticationToken(Object principal, String credential) {
        super(null);
        this.principal = principal;
        this.credential = null;
        this.setAuthenticated(true);
    }

    // 인증처리 전
    public static UserAuthenticationToken unauthenticated(UserRequestDTO.LoginRequestDTO loginDto) {
        return new UserAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
    }

    // 인증처리 후
    public static UserAuthenticationToken authenticated(AuthenticatedUser principal) {
        return new UserAuthenticationToken(principal, null);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}

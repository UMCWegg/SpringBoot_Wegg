package umc.wegg.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * Authentication의  Principal에 저장될 Custom principal(인증객체)
 */
@Getter
@AllArgsConstructor
public class AuthenticatedUser {

    private Long userId;
    private String email;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if(obj == null || getClass() != obj.getClass()) return false;

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) obj;

        return Objects.equals(this.userId, authenticatedUser.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("userId: ").append(userId).append(", email: ").append(email);
        return sb.toString();
    }

}

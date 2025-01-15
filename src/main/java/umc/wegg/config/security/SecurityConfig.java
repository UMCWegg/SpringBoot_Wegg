package umc.wegg.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/users/**") // REST API 경로에서는 CSRF 비활성화
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/users/**",
                                "/swagger-ui/**", // Swagger UI 관련 URL
                                "/v3/api-docs/**"  // OpenAPI 명세 URL
                        ).permitAll() // 로그인 없이 접근 허용
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

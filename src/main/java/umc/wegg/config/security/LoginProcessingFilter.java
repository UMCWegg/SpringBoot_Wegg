package umc.wegg.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;
import umc.wegg.dto.UserRequestDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final SecurityContextRepository securityContextRepository;

    public LoginProcessingFilter(SecurityContextRepository securityContextRepository) {
        super(new AntPathRequestMatcher("/users/login")); // "/users/login" 요청에 Filter를 적용
        this.securityContextRepository = securityContextRepository;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 1. HTTP 요청 바디 json을 읽어 DTO로 변환
        UserRequestDTO.LoginRequestDTO loginDto = jsonToLoginDto(request);

        // 2. 인증처리 전의 Authentication 객체를 생성
        UserAuthenticationToken authRequest =
                UserAuthenticationToken.unauthenticated(loginDto);

        // 3. AuthenticationManager에게 인증처리를 위임
        return super.getAuthenticationManager().authenticate(authRequest);
    }

    private UserRequestDTO.LoginRequestDTO jsonToLoginDto(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String usernamePasswordJson = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(usernamePasswordJson, UserRequestDTO.LoginRequestDTO.class);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws ServletException, IOException {
        // 1. 비어있는 SecurityContext를 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // 2. 인증처리 완료된 Authentication 객체를 SecurityContext에 등록
        context.setAuthentication(authResult);

        // 3. Session 등록 및 성공 핸들러 호출
        this.securityContextRepository.saveContext(context, request, response);
        this.getSuccessHandler().onAuthenticationSuccess(request,response,chain,authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // 1. SecurityContextHolder 비우기
        SecurityContextHolder.clearContext();

        // 2. 실패 핸들러 호출
        this.getFailureHandler().onAuthenticationFailure(request, response, failed);
    }

}

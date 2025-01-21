package umc.wegg.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.domain.apiPayload.code.status.ErrorStatus;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        //code 및 메세지 수정
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED.getCode(), "로그인 실패", null)));
        response.flushBuffer();
    }
}

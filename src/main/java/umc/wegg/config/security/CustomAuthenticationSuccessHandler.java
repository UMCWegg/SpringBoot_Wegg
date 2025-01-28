package umc.wegg.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.UserResponseDTO;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = authenticatedUser.getUserId();

        UserResponseDTO.LoginResultDTO responseDTO = new UserResponseDTO.LoginResultDTO(true, userId);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onSuccess(responseDTO)));
        response.flushBuffer();
    }
}

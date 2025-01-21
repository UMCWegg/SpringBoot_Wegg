package umc.wegg.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Access Token 가져오기
        String accessToken = userRequest.getAccessToken().getTokenValue();

        // Null 체크 및 로깅
        if (oAuth2User == null) {
            throw new OAuth2AuthenticationException("Failed to load user information from OAuth2 provider");
        }

        // OAuth2 제공자 이름 가져오기
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 속성 가져오기
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String id = null;

        if ("kakao".equals(provider)) {
            id = String.valueOf(attributes.get("id")); // Kakao의 고유 ID
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            id = (String) response.get("id"); // Naver의 고유 ID
        }

        // ID를 Principal로 설정
        attributes.put("provider", provider);
        attributes.put("oauthId", id);
        attributes.put("accessToken", accessToken);

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "oauthId" // Principal로 사용할 필드
        );
    }
}

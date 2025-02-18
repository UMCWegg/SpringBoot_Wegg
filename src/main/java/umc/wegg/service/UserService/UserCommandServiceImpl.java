package umc.wegg.service.UserService;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.wegg.aws.s3.AmazonS3Manager;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.config.security.UserAuthenticationToken;
import umc.wegg.converter.UserConverter;
import umc.wegg.domain.ContactFriend;
import umc.wegg.domain.User;
import umc.wegg.domain.Uuid;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.domain.apiPayload.code.status.ErrorStatus;
import umc.wegg.dto.UserRequestDTO;
import umc.wegg.dto.UserResponseDTO;
import umc.wegg.repository.UserRepository;
import umc.wegg.repository.UuidRepository;
import umc.wegg.util.RedisUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService{

    @Value("${spring.google.api.client-id}")
    private String clientId;

    private final UserRepository userRepository;

    private final RedisUtil redisUtil;

    private final PasswordEncoder passwordEncoder;

    private final AmazonS3Manager s3Manager;

    private final UuidRepository uuidRepository;

    private final SecurityContextRepository securityContextRepository;

    @Override
    @Transactional
    public UserResponseDTO.UserJoinResultDTO joinUser(UserRequestDTO.UserJoinDto request) {

        boolean isExistingUser = userRepository.existsByEmail(request.getEmail());
        if (isExistingUser) {
            //exception throw
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        List<UserResponseDTO.ContactFriendDTO> contactFriendList = Optional.ofNullable(request.getContact())
                .orElse(Collections.emptyList())
                .stream()
                .map(contact -> userRepository.findByPhone(contact.getPhone())
                        .map(contactFriend -> new UserResponseDTO.ContactFriendDTO(
                                contactFriend,
                                contactFriend.getAccountId(),
                                contactFriend.getName(),
                                contactFriend.getProfileImage(),
                                contactFriend.getPhone()
                        ))
                        .orElse(null))
                .filter(Objects::nonNull) // null 값 제거
                .collect(Collectors.toList());

        User user = UserConverter.toUser(request, contactFriendList);
        user.encodePassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // 응답용 ContactFriendDto 변환
        List<UserResponseDTO.UserJoinResultDTO.ContactFriendDto> contactFriends = contactFriendList.stream()
                .map(contactFriend -> new UserResponseDTO.UserJoinResultDTO.ContactFriendDto(
                        contactFriend.getFriend().getId(),
                        contactFriend.getFriend().getAccountId(),
                        contactFriend.getFriend().getName(),
                        contactFriend.getFriend().getProfileImage(),
                        contactFriend.getFriend().getPhone()
                ))
                .collect(Collectors.toList());

        return UserConverter.toJoinResultDTO(user, contactFriends);
    }

    @Override
    @Transactional
    public UserResponseDTO.OAuth2UserJoinResultDTO oAuth2JoinUser(UserRequestDTO.OAuth2UserJoinDto request) {

        if ("google".equals(request.getType())) {
            if (!verifyGoogleToken(request.getToken())) {
                throw new IllegalArgumentException("OAuth2 로그인 검증에 실패했습니다");
            }
        }else{
            if (!verifyKakaoToken(request.getToken())){
                throw new IllegalArgumentException("OAuth2 로그인 검증에 실패했습니다");
            }
        }

        // 1. OAuth2 인증 정보를 기반으로 사용자 식별자 가져오기
        String email = request.getEmail();
        String password = passwordEncoder.encode("OAUTH_USER_" + UUID.randomUUID()); //가비지 값 생성

        // 2. 사용자 존재 여부 확인
        boolean isExistingUser = userRepository.existsByEmail(email);
        if (isExistingUser) {
            //exception throw
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        List<UserResponseDTO.ContactFriendDTO> contactFriendList = Optional.ofNullable(request.getContact())
                .orElse(Collections.emptyList())
                .stream()
                .map(contact -> userRepository.findByPhone(contact.getPhone())
                        .map(contactFriend -> new UserResponseDTO.ContactFriendDTO(
                                contactFriend,
                                contactFriend.getAccountId(),
                                contactFriend.getName(),
                                contactFriend.getProfileImage(),
                                contactFriend.getPhone()
                        ))
                        .orElse(null)) // User가 없으면 null
                .filter(Objects::nonNull) // null 값 제거
                .collect(Collectors.toList());

        User user = UserConverter.toOAuthUser(request, password, contactFriendList);
        userRepository.save(user);

        // 응답용 ContactFriendDto 변환
        List<UserResponseDTO.OAuth2UserJoinResultDTO.ContactFriendDto> contactFriends = contactFriendList.stream()
                .map(contactFriend -> new UserResponseDTO.OAuth2UserJoinResultDTO.ContactFriendDto(
                        contactFriend.getFriend().getId(), 
                        contactFriend.getFriend().getAccountId(),
                        contactFriend.getFriend().getName(),
                        contactFriend.getFriend().getProfileImage(),
                        contactFriend.getFriend().getPhone()
                ))
                .collect(Collectors.toList());

        return UserConverter.toOAuth2JoinResultDTO(user, contactFriends);
    }

    @Override
    public ApiResponse<UserResponseDTO.LoginResultDTO> oAuth2LoginUser(UserRequestDTO.OAuth2LoginRequestDTO request,
                                                          HttpServletRequest httpServletRequest,
                                                          HttpServletResponse httpServletResponse) {

        if ("google".equals(request.getType())) {
            if (!verifyGoogleToken(request.getToken())) {
                return ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED.getCode(), "로그인 실패", null);
            }
        }else{
            if (!verifyKakaoToken(request.getToken())){
                return ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED.getCode(), "로그인 실패", null);
            }
        }

        // email로 DB에서 사용자 확인
        String email = request.getEmail();

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // OAuth2User를 AuthenticatedUser로 변환
            AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getId(), user.getEmail());  // existingUser를 AuthenticatedUser로 변환

            // 1. 비어있는 SecurityContext를 생성
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            // 2. 인증처리 완료된 Authentication 객체를 SecurityContext에 등록
            Authentication authentication = UserAuthenticationToken.authenticated(authenticatedUser);
            context.setAuthentication(authentication);
            // 3. Session 등록 및 성공 핸들러 호출
            securityContextRepository.saveContext(context, httpServletRequest, httpServletResponse);

            return ApiResponse.onSuccess(new UserResponseDTO.LoginResultDTO(true, user.getId()));
        } else {
            //exception throw
            return ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED.getCode(), "로그인 실패", null);
        }
    }

    private boolean verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false; // 검증 실패
    }

    private boolean verifyKakaoToken(String accessToken) {
        try {
            URL url = new URL("https://kapi.kakao.com/v1/user/access_token_info");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken.trim());

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK; // 200 OK면 true, 아니면 false 반환
        } catch (IOException e) {
            return false; // 예외 발생 시 false 반환
        }
    }

    @Override
    public UserResponseDTO.UserDeleteResultDTO deleteUser(AuthenticatedUser authenticatedUser) {

        if (authenticatedUser == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        Long userId = authenticatedUser.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        userRepository.delete(user);

        return new UserResponseDTO.UserDeleteResultDTO(true, userId);
    }

@Override
public UserResponseDTO.UserUpdateResultDTO updateUser(AuthenticatedUser authenticatedUser, UserRequestDTO.UserUpdateDto request, MultipartFile profilePicture) throws IOException {

    if (authenticatedUser == null) {
        throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
    }

    Long userId = authenticatedUser.getUserId();
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. "));

    // 수정된 필드를 저장할 Map
    Map<String, Object> updatedFields = new HashMap<>();

    if (request != null) {
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
            updatedFields.put("name", request.getName());
        }

        if (request.getAccountId() != null && !request.getAccountId().isEmpty()) {
            user.setAccountId(request.getAccountId());
            updatedFields.put("accountId", request.getAccountId());
        }
    }

    // MultipartFile을 사용해 profilePicture 처리
    if (profilePicture != null && !profilePicture.isEmpty()) {
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        // S3에 파일 업로드 후 URL 반환
        String pictureUrl = s3Manager.upLoadFile(s3Manager.generateProfileKeyName(savedUuid), profilePicture);

        user.setProfileImage(pictureUrl);
        updatedFields.put("profileImage", pictureUrl);
    }

    userRepository.save(user);

    return new UserResponseDTO.UserUpdateResultDTO(true, updatedFields);
}


    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO.CheckAccountIdResultDTO checkAccountIdDuplication(String accountId) {
        boolean isDuplicate = userRepository.existsByAccountId(accountId);

        String message = isDuplicate
                ? "이미 사용 중인 아이디입니다."
                : "사용 가능한 아이디입니다.";

        return new UserResponseDTO.CheckAccountIdResultDTO(isDuplicate, message);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO.CheckEmailResultDTO checkEmailDuplication(String email) {
        boolean isDuplicate = userRepository.existsByEmail(email);

        String message = isDuplicate
                ? "이미 사용 중인 이메일입니다."
                : "사용 가능한 이메일입니다.";

        return new UserResponseDTO.CheckEmailResultDTO(isDuplicate, message);
    }

    public UserResponseDTO.VerifyNumberResultDTO verityNumber(UserRequestDTO.VerifyNumberDto request) {

        if (!request.validateFormat()) {
            //exception throw
        }

        String target = request.getTarget();

        String savedNumber = redisUtil.getData(target);
        if (savedNumber == null) {
            //exception throw 하기
        }

        String number = request.getNumber();

        if (savedNumber.equals(number)) {
            return new UserResponseDTO.VerifyNumberResultDTO(true);

        } else {
            return new UserResponseDTO.VerifyNumberResultDTO(false);
        }
    }

    // 계정검색 - 검색어 포함하는 계정 목록 전부 반환
    @Override
    public List<UserResponseDTO.UserSearchDTO> searchUsersByAccountId(String keyword) {
        return userRepository.findByAccountIdContaining(keyword).stream()
                .map(user -> new UserResponseDTO.UserSearchDTO(user.getId(), user.getAccountId(), user.getProfileImage()))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO.ContactUpdateResultDTO updateContactList(AuthenticatedUser authenticatedUser, List<UserRequestDTO.ContactDto> newContacts) {
        if (authenticatedUser == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        Long userId = authenticatedUser.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 기존 연락처 리스트 (현재 저장된 친구 ID 리스트)
        Set<Long> existingFriendIds = user.getContactFriendList().stream()
                .map(contact -> contact.getFriend().getId())
                .collect(Collectors.toSet());

        // 새로운 연락처 중 가입된 유저만 찾기
        List<UserResponseDTO.ContactFriendDTO> newContactFriends = newContacts.stream()
                .map(contact -> userRepository.findByPhone(contact.getPhone())
                        .map(contactFriend -> new UserResponseDTO.ContactFriendDTO(
                                contactFriend,
                                contactFriend.getAccountId(),
                                contactFriend.getName(),
                                contactFriend.getProfileImage(),
                                contactFriend.getPhone()
                        ))
                        .orElse(null))
                .filter(Objects::nonNull) // 가입된 유저만 유지
                .filter(contactFriend -> !existingFriendIds.contains(contactFriend.getFriend().getId())) // 중복 제거
                .collect(Collectors.toList());

        // 새로운 연락처가 있을 경우 추가
        if (!newContactFriends.isEmpty()) {
            List<ContactFriend> contactFriendEntities = UserConverter.toContactFriendEntities(user, newContactFriends);
            user.getContactFriendList().addAll(contactFriendEntities);
            userRepository.save(user); // 변경사항 저장
        }

        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("newContactsCount", newContactFriends.size());

        return new UserResponseDTO.ContactUpdateResultDTO(true);
    }
}

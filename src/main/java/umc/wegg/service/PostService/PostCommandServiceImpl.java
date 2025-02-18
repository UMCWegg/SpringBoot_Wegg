package umc.wegg.service.PostService;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import umc.wegg.aws.s3.AmazonS3Manager;
import umc.wegg.domain.*;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.domain.apiPayload.code.status.ErrorStatus;
import umc.wegg.domain.enums.AccountVisibility;
import umc.wegg.domain.enums.EmojiType;
import umc.wegg.domain.enums.PlanStatus;
import umc.wegg.domain.mapping.Emoji;
import umc.wegg.domain.mapping.MyTemplate;
import umc.wegg.dto.PlanResponseDTO;
import umc.wegg.dto.PostRequestDTO;
import umc.wegg.dto.PostResponseDTO;
import umc.wegg.dto.UserResponseDTO;
import umc.wegg.repository.*;
import umc.wegg.repository.UserRepository;
import umc.wegg.service.NotificationService.NotificationService;
import umc.wegg.service.PlanService.PlanCommandService;
import umc.wegg.service.PlanService.PlanQueryService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final EmojiRepository emojiRepository;
    private final PlanRepository planRepository;
    private final MyTemplateRepository myTemplateRepository;
    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;
    private final NotificationService notificationService;
    private final FollowRepository followRepository;
    private final SettingRepository settingRepository;
    private final PlanQueryService planQueryService;

    @Override
    public ApiResponse<PostResponseDTO.PostCreateResponseDTO> createPost(Long userId,PostRequestDTO.CreatePostDTO requestDTO, MultipartFile postImage) throws IOException {
        // 1. 장소 인증 확인 (성공해야지만 게시물 작성 가능)
        Plan plan = planRepository.findById(requestDTO.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + requestDTO.getPlanId()));

        PlanResponseDTO.LocationVerificationResponseDTO verificationResponse =
                planQueryService.isUserInPlan(plan.getId(), userId, requestDTO.getLatitude(), requestDTO.getLongitude(), 2); // 2는 랜덤 인증 타입

        if (!verificationResponse.getMessage().equals("장소 인증에 성공했습니다!")) {
            planQueryService.failPlan(plan);
            return ApiResponse.onFailure(ErrorStatus._BAD_REQUEST.getCode(), "장소 인증에 실패하여 포스트를 올릴 수 없습니다.", null);
        }
        else {
            // 상태 업데이트 (YET -> SUCCEEDED)
            plan.setStatus(PlanStatus.SUCCEEDED);
            planRepository.save(plan); // 변경된 상태 저장

            // 2. UUID 생성 및 저장 (필요한 경우)
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());  // UUID 저장 검토

            // 3. S3 이미지 업로드 (예외 처리 추가)
            String pictureUrl = null;
            if (postImage != null && !postImage.isEmpty()) { // 이미지가 존재하는 경우에만 업로드
                try {
                    pictureUrl = s3Manager.upLoadFile(s3Manager.generatePostKeyName(savedUuid), postImage);
                } catch (Exception e) {
                    throw new IOException("S3 파일 업로드 실패: " + e.getMessage(), e);
                }
            }

            // 4. Post 엔티티 생성
            Post post = Post.builder()
                    .imageUrl(pictureUrl) // 이미지 URL 설정 (null 허용)
                    .plan(plan) // Plan 설정
                    .build();

            // 5. Post 엔티티 저장
            Post savedPost = postRepository.save(post);

            // 5. 게시글 작성자 조회
            User postOwner = getPostOwner(post.getId()); // 현재 게시글 작성자 (이 메서드가 필요함)

            // 6. 팔로워 조회
            List<User> followers = followRepository.findFollowersByFollowee(postOwner);

            // 7. 팔로워들에게 알림 전송
            String message = postOwner.getAccountId() + "님이 새로운 포스트를 올렸습니다!";
            for (User follower : followers) {
                Setting followerSetting = settingRepository.findByUserId(follower.getId()).orElse(null); // 설정 조회

                if (followerSetting != null && followerSetting.isPostAlarm()) { // postAlarm이 true일 때만 알림 전송
                    notificationService.sendNotificationToFollower(follower, savedPost.getId(), message, "POST", postOwner.getProfileImage());
                }
            }

            // 8. DTO 변환 및 반환
            PostResponseDTO.PostCreateResponseDTO succeeded = PostResponseDTO.PostCreateResponseDTO.builder()
                    .postId(savedPost.getId())
                    .planId(savedPost.getPlan() != null ? savedPost.getPlan().getId() : null)
                    .createdAt(savedPost.getCreatedAt()) // createdAt이 null이 아닌지 확인 필요
                    .build();
            return ApiResponse.onSuccess(succeeded);
        }

    }


    // 게시글 작성자를 가져오는 메서드
    public User getPostOwner(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."))
                .getPlan()          // Post 엔티티에서 Plan을 가져옴
                .getUser();         // Plan 엔티티에서 User를 가져옴
    }


    @Override
    public void addComment(Long userId, PostRequestDTO.AddCommentDTO requestDTO) {
        // 1. 유저 조회

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. 게시물(Post) 조회
        Post post = postRepository.findById(requestDTO.getPostingId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + requestDTO.getPostingId()));

        // 3. Comment 엔티티 생성
        Comment comment = Comment.builder()
                .user(user)                          // 댓글 작성자
                .post(post)                          // 댓글이 달릴 게시물
                .parentId(requestDTO.getParentId())  // 부모댓글 Id
                .comment(requestDTO.getComment())    // 댓글 내용
                .build();

        // 4. 댓글 저장
        commentRepository.save(comment);

        // 5. 게시글 작성자 조회
        User postOwner = getPostOwner(requestDTO.getPostingId());

        // 6. 댓글 작성자 이름
        String commenterName = user.getAccountId();

        // 7. 본인이 댓글을 단 경우를 제외하고 알림 전송
        if (!postOwner.getId().equals(userId)) {
            // 알림 메시지 작성
            String message = commenterName + "님이 댓글을 달았습니다!";
            Setting ownerSetting = settingRepository.findByUserId(postOwner.getId()).orElse(null); // 설정 조회

            if (ownerSetting != null && ownerSetting.isCommentAlarm()) { // commentAlarm이 true일 때만 알림 전송
                notificationService.sendNotificationToPostOwner(postOwner, requestDTO.getPostingId(), message, "COMMENT", user.getProfileImage());
            }
        }


    }


    @Override
    public void deleteComment(Long userId,Long postId, Long commentId) {
        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        // 2. 게시물과 댓글의 관계 확인
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Comment does not belong to the post with id: " + postId);
        }

        // 3. 댓글 삭제
        commentRepository.delete(comment);
    }


    @Override
    public void addEmoji(Long userId,Long postId, String emojiType) {
        // 1. 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 3. 이모지 타입 검증
        EmojiType type;
        try {
            type = EmojiType.valueOf(emojiType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid emoji type: " + emojiType);
        }

        // 4. 중복 이모지 등록 방지
        boolean emojiExists = emojiRepository.existsByPostAndUserAndType(post, user, type);
        if (emojiExists) {
            throw new IllegalArgumentException("Emoji already exists for this post and user.");
        }

        // 5. 이모지 저장
        Emoji emoji = Emoji.builder()
                .post(post)
                .user(user)
                .type(type)
                .build();
        emojiRepository.save(emoji);

        // 6. 게시글 작성자 조회
        User postOwner = getPostOwner(postId);

        // 6. 이모지 작성자 이름
        String emojiName = user.getAccountId();

        if (!postOwner.getId().equals(userId)) {
            // 알림 메시지 작성
            String message = emojiName + "님이 " + emojiType +"을 달았습니다!";
            Setting ownerSetting = settingRepository.findByUserId(postOwner.getId()).orElse(null); // 설정 조회

            if (ownerSetting != null && ownerSetting.isCommentAlarm()) { // commentAlarm이 true일 때만 알림 전송
                notificationService.sendNotificationToPostOwner(postOwner, postId, message, "EMOJI", user.getProfileImage());
            }
        }

    }


    @Override
    public void deleteEmoji(Long userId,Long postId, String emojiType) {
        // 1. 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 3. 이모지 타입 검증
        EmojiType type;
        try {
            type = EmojiType.valueOf(emojiType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid emoji type: " + emojiType);
        }

        // 4. 해당 이모지 조회
        Emoji emoji = emojiRepository.findByPostAndUserAndType(post, user, type)
                .orElseThrow(() -> new IllegalArgumentException("Emoji not found for given post, user, and type"));

        // 5. 이모지 삭제
        emojiRepository.delete(emoji);
    }

//    @Override
//    public List<List<PostResponseDTO.PostPreviewResponseDTO>> browsePosts(int page, int size) {
//        // 1. 현재 사용자 조회
//        Long userId = 1L; // 로그인 구현 시 변경
//        User currentUser = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//
//        // 2. 페이징된 게시물 조회
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Post> postPage = postRepository.findAll(pageable);
//        List<Post> allPosts = postPage.getContent();
//
//        // 3. 현재 사용자가 팔로우하는 사용자 목록 조회
//        Set<Long> followingUserIds = currentUser.getFollowingList().stream()
//                .map(follow -> follow.getFollowee().getId())
//                .collect(Collectors.toSet());
//
//        // 4. 게시물 정렬
//        List<Post> followingPosts = new ArrayList<>();
//        List<Post> nonFollowingPosts = new ArrayList<>();
//
//        for (Post post : allPosts) {
//            if (post.getPlan() == null) continue; // Plan이 없는 게시물 제외
//            User postUser = post.getPlan().getUser();
//            if (followingUserIds.contains(postUser.getId())) {
//                followingPosts.add(post);
//            } else {
//                nonFollowingPosts.add(post);
//            }
//        }
//
//        Comparator<Post> postComparator = (post1, post2) -> {
//            User user1 = post1.getPlan().getUser();
//            User user2 = post2.getPlan().getUser();
//
//            boolean sameJob1 = user1.getJob() != null && user1.getJob().equals(currentUser.getJob());
//            boolean sameJob2 = user2.getJob() != null && user2.getJob().equals(currentUser.getJob());
//
//            int emojiCount1 = emojiRepository.countByPost(post1);
//            int emojiCount2 = emojiRepository.countByPost(post2);
//
//            if (sameJob1 && !sameJob2) return -1;
//            if (!sameJob1 && sameJob2) return 1;
//            return Integer.compare(emojiCount2, emojiCount1);
//        };
//
//        // 5. 정렬 수행
//        List<Post> sortedFollowingPosts = followingPosts.stream().sorted(postComparator).collect(Collectors.toList());
//        List<Post> sortedNonFollowingPosts = nonFollowingPosts.stream().sorted(postComparator).collect(Collectors.toList());
//
//        // 6. DTO 변환
//        List<PostResponseDTO.PostPreviewResponseDTO> followingPostDTOs = sortedFollowingPosts.stream()
//                .map(post -> convertToDTO(post)).collect(Collectors.toList());
//
//        List<PostResponseDTO.PostPreviewResponseDTO> nonFollowingPostDTOs = sortedNonFollowingPosts.stream()
//                .map(post -> convertToDTO(post)).collect(Collectors.toList());
//
//        // 7. 결과 반환 (팔로잉 게시물과 비팔로잉 게시물을 리스트로 구분)
//        return Arrays.asList(followingPostDTOs, nonFollowingPostDTOs);
//    }
//
//    private PostResponseDTO.PostPreviewResponseDTO convertToDTO(Post post) {
//        User postUser = post.getPlan().getUser();
//        return PostResponseDTO.PostPreviewResponseDTO.builder()
//                .postId(post.getId())
//                .profileImageUrl(postUser.getProfileImage())
//                .accountId(postUser.getAccountId())
//                .postImageUrl(post.getImageUrl())
//                .createdAt(post.getCreatedAt())
//                .build();
//    }
    @Override
    public List<List<PostResponseDTO.PostPreviewResponseDTO>> browsePosts(Long userId,int page, int size) {
        // 1. 현재 사용자 조회
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. 사용자의 설정 조회
        Setting userSetting = settingRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User settings not found"));

        // 3. placeCheck 또는 randomCheck가 false라면 빈 리스트 반환
        if (!userSetting.isPlaceCheck() || !userSetting.isRandomCheck()) {
            return Arrays.asList(Collections.emptyList(), Collections.emptyList());
        }

        // 4. 페이징된 게시물 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAll(pageable);
        List<Post> allPosts = postPage.getContent();

        // 5. 현재 사용자가 팔로우하는 사용자 목록 조회
        Set<Long> followingUserIds = currentUser.getFollowingList().stream()
                .map(follow -> follow.getFollowee().getId())
                .collect(Collectors.toSet());

        // 6. 게시물 분류 (팔로우한 사용자 vs 팔로우하지 않은 사용자)
        List<Post> followingPosts = new ArrayList<>();
        List<Post> nonFollowingPosts = new ArrayList<>();

        for (Post post : allPosts) {
            if (post.getPlan() == null) continue; // Plan이 없는 게시물 제외
            User postUser = post.getPlan().getUser();

            if (followingUserIds.contains(postUser.getId())) {
                followingPosts.add(post);
            } else {
                // ✅ 비공개 계정이면서 팔로우하지 않은 사용자의 게시물 제외
                if (postUser.getSetting().getAccountVisibility() == AccountVisibility.FOLLOWER_ONLY) {
                    continue;
                }
                nonFollowingPosts.add(post);
            }
        }

        // 7. 게시물 정렬 기준 설정
        Comparator<Post> postComparator = (post1, post2) -> {
            User user1 = post1.getPlan().getUser();
            User user2 = post2.getPlan().getUser();

            boolean sameJob1 = user1.getJob() != null && user1.getJob().equals(currentUser.getJob());
            boolean sameJob2 = user2.getJob() != null && user2.getJob().equals(currentUser.getJob());

            int emojiCount1 = emojiRepository.countByPost(post1);
            int emojiCount2 = emojiRepository.countByPost(post2);

            if (sameJob1 && !sameJob2) return -1;
            if (!sameJob1 && sameJob2) return 1;
            return Integer.compare(emojiCount2, emojiCount1);
        };

        // 8. 정렬 수행
        List<Post> sortedFollowingPosts = followingPosts.stream().sorted(postComparator).collect(Collectors.toList());
        List<Post> sortedNonFollowingPosts = nonFollowingPosts.stream().sorted(postComparator).collect(Collectors.toList());

        // 9. DTO 변환
        List<PostResponseDTO.PostPreviewResponseDTO> followingPostDTOs = sortedFollowingPosts.stream()
                .map(this::convertToDTO).collect(Collectors.toList());

        List<PostResponseDTO.PostPreviewResponseDTO> nonFollowingPostDTOs = sortedNonFollowingPosts.stream()
                .map(this::convertToDTO).collect(Collectors.toList());

        // 10. 결과 반환 (팔로우한 사용자 & 팔로우하지 않은 사용자의 게시물 리스트)
        return Arrays.asList(followingPostDTOs, nonFollowingPostDTOs);
    }


    private PostResponseDTO.PostPreviewResponseDTO convertToDTO(Post post) {
        User postUser = post.getPlan().getUser();
        return PostResponseDTO.PostPreviewResponseDTO.builder()
                .postId(post.getId())
                .profileImageUrl(postUser.getProfileImage())
                .accountId(postUser.getAccountId())
                .postImageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .build();
    }


    @Override
    public PostResponseDTO.PostDetailResponseDTO viewPostDetails(Long userId,Long postId, int page, int size) {
        // 1. 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 2. 페이징된 댓글 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByPost(post, pageable);

        // 3. 댓글 리스트를 DTO로 변환
        List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> commentDTOs = commentPage.getContent().stream()
                .map(comment -> PostResponseDTO.PostDetailResponseDTO.CommentDTO.builder()
                        .commentId(comment.getId())                         // 댓글 ID
                        .parentId(comment.getParentId())                    // 부모댓글 ID
                        .userId(comment.getUser().getId())                  // 댓글 작성자 ID
                        .username(comment.getUser().getName())              // 댓글 작성자 닉네임
                        .content(comment.getComment())                      // 댓글 내용
                        .commenterProfileUrl(comment.getUser().getProfileImage()) // 댓글 작성자 프로필 이미지
                        .createdAt(comment.getCreatedAt())                  // 댓글 작성 시간
                        .build())
                .collect(Collectors.toList());

        // 4. 이모지 데이터 조회
        List<Emoji> emojis = emojiRepository.findByPost(post);

        // 5. 모든 이모지 타입의 개수를 계산
        Map<EmojiType, Long> emojiCounts = emojis.stream()
                .collect(Collectors.groupingBy(Emoji::getType, Collectors.counting()));

        // 6. 모든 이모지 타입을 포함하여 기본값 0 설정
        List<PostResponseDTO.EmojiResponseDTO.EmojiCountDTO> allEmojiCounts = Arrays.stream(EmojiType.values())
                .map(type -> PostResponseDTO.EmojiResponseDTO.EmojiCountDTO.builder()
                        .emojiType(type.name())                           // 이모지 타입 이름
                        .count(emojiCounts.getOrDefault(type, 0L).intValue()) // 개수 (기본값 0)
                        .build())
                .collect(Collectors.toList());

        // 7. 사용자 선택 이모지 리스트 구성
        List<String> userSelectedEmojis = emojis.stream()
                .map(emoji -> emoji.getType().name())
                .distinct()
                .collect(Collectors.toList());

        // 8. PostDetailResponseDTO 생성 및 반환
        return PostResponseDTO.PostDetailResponseDTO.builder()
                .postId(post.getId())                                    // 게시물 ID
                .postImageUrl(post.getImageUrl())                        // 게시물 이미지 URL
                .userId(post.getPlan().getUser().getId())                // 유저 아이디
                .profileImage(post.getPlan().getUser().getProfileImage())// 작성자 프로필 이미지
                .accountId(post.getPlan().getUser().getAccountId())      // 작성자 계정 아이디
                .createdAt(post.getCreatedAt())                          // 게시 시간
                .comments(commentDTOs)                                   // 댓글 리스트 (페이징 적용됨)
                .emojiCounts(allEmojiCounts)                             // 모든 이모지의 개수 리스트
                .userSelectedEmojis(userSelectedEmojis)                  // 사용자 선택 이모지 리스트
                .currentPage(commentPage.getNumber())                    // 현재 댓글 페이지 번호
                .totalPages(commentPage.getTotalPages())                 // 총 댓글 페이지 수
                .totalComments(commentPage.getTotalElements())           // 총 댓글 개수
                .build();
    }


    @Override
    public List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> getComments(Long userId,Long postId, int page, int size) {
        // 1. 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 2. 페이징 처리를 위한 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. 페이징된 댓글 조회
        Page<Comment> commentPage = commentRepository.findByPost(post, pageable);

        // 4. 댓글 리스트를 DTO로 변환
        return commentPage.getContent().stream()
                .map(comment -> PostResponseDTO.PostDetailResponseDTO.CommentDTO.builder()
                        .commentId(comment.getId())                         // 댓글 ID
                        .parentId(comment.getParentId())                    // 부모댓글 ID
                        .userId(comment.getUser().getId())                  // 작성자 ID
                        .username(comment.getUser().getName())              // 작성자 닉네임
                        .content(comment.getComment())                      // 댓글 내용
                        .commenterProfileUrl(comment.getUser().getProfileImage()) // 작성자 프로필 이미지
                        .createdAt(comment.getCreatedAt())                  // 작성 시간
                        .build())
                .collect(Collectors.toList());
    }



    @Override
    public PostResponseDTO.EmojiResponseDTO getEmojis(Long userId,Long postId) {
        // 1. 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 2. 이모지 조회
        List<Emoji> emojis = emojiRepository.findByPost(post);

        // 3. 이모지 카운트 계산 (24개의 모든 이모지)
        Map<EmojiType, Long> emojiCounts = emojis.stream()
                .collect(Collectors.groupingBy(Emoji::getType, Collectors.counting()));

        // 모든 이모지 타입의 카운트 기본값을 0으로 설정
        Map<EmojiType, Long> allEmojiCounts = Arrays.stream(EmojiType.values())
                .collect(Collectors.toMap(type -> type, type -> 0L));

        // 실제 카운트로 업데이트
        allEmojiCounts.putAll(emojiCounts);

        // 4. 사용자별 선택된 이모지 리스트
        List<String> userSelectedEmojis = emojis.stream()
                .map(emoji -> emoji.getType().name()) // 이모지 타입 이름
                .distinct() // 중복 제거
                .collect(Collectors.toList());

        // 5. EmojiResponseDTO 생성 및 반환
        return PostResponseDTO.EmojiResponseDTO.builder()
                .postId(postId) // 게시물 ID
                .emojiCounts(allEmojiCounts.entrySet().stream()
                        .map(entry -> PostResponseDTO.EmojiResponseDTO.EmojiCountDTO.builder()
                                .emojiType(entry.getKey().name()) // EmojiType을 문자열로 변환
                                .count(entry.getValue().intValue()) // Long을 int로 변환
                                .build())
                        .collect(Collectors.toList())) // Map -> List<EmojiCountDTO> 변환
                .userSelectedEmojis(userSelectedEmojis) // 사용자 선택 이모지 리스트
                .build();
    }

    @Override
    public List<PostResponseDTO.TemplateDTO> getUserTemplates(Long userId) {
        // 마이템플릿 데이터베이스에서 userId를 기반으로 사용자 템플릿 목록을 조회
        List<MyTemplate> myTemplates = myTemplateRepository.findByUserId(userId);

        return myTemplates.stream()
                .map(myTemplate -> PostResponseDTO.TemplateDTO.builder()
                        .templateId(myTemplate.getTemplate().getId())
                        .type(myTemplate.getTemplate().getTemplateType().name())
                        .build())
                .collect(Collectors.toList());
    }

}


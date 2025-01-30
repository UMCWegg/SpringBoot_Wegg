package umc.wegg.service.PostService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import umc.wegg.domain.*;
import umc.wegg.domain.enums.EmojiType;
import umc.wegg.domain.mapping.Emoji;
import umc.wegg.dto.PostRequestDTO;
import umc.wegg.dto.PostResponseDTO;
import umc.wegg.repository.*;
import umc.wegg.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final EmojiRepository emojiRepository;
    private final PlanRepository planRepository;
    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;



    @Override
    public PostResponseDTO.PostCreateResponseDTO createPost(PostRequestDTO.CreatePostDTO requestDTO) {
        // 1. Template 엔티티 조회
        Template template = templateRepository.findById(requestDTO.getTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("Template not found with id: " + requestDTO.getTemplateId()));

        // 2. Plan 엔티티 조회
        Plan plan = planRepository.findById(requestDTO.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + requestDTO.getPlanId()));

        // 3. Post 엔티티 생성
        Post post = Post.builder()
                .imageUrl(requestDTO.getImageUrl()) // 요청에서 받은 이미지 URL 설정
                .comment(requestDTO.getComment())  // 요청에서 받은 댓글 설정
                .template(template)                // 조회한 Template 엔티티 설정
                .plan(plan)                        // 조회한 Plan 엔티티 설정
                .build();                          // Post 객체 생성

        // 4. Post 엔티티 저장
        Post savedPost = postRepository.save(post);

        // 5. DTO 변환 및 반환
        return PostResponseDTO.PostCreateResponseDTO.builder()
                .postId(savedPost.getId())
                .imageUrl(savedPost.getImageUrl())
                .templateId(savedPost.getTemplate() != null ? savedPost.getTemplate().getId() : null)
                .planId(savedPost.getPlan() != null ? savedPost.getPlan().getId() : null)
                .createdAt(savedPost.getCreatedAt())
                .build();
    }



    @Override
    public void addComment(PostRequestDTO.AddCommentDTO requestDTO) {
        // 1. 유저 조회
        Long userId = 1L;// 포스트맨 쓰기전까지 1로 두기!! 이후 바꿔야 함!!
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. 게시물(Post) 조회
        Post post = postRepository.findById(requestDTO.getPostingId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + requestDTO.getPostingId()));

        // 3. Comment 엔티티 생성
        Comment comment = Comment.builder()
                .user(user)                          // 댓글 작성자
                .post(post)                          // 댓글이 달릴 게시물
                .comment(requestDTO.getComment())    // 댓글 내용
                .build();

        // 4. 댓글 저장
        commentRepository.save(comment);
    }


    @Override
    public void deleteComment(Long postId, Long commentId) {
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
    public void addEmoji(Long postId, String emojiType) {
        // 1. 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 2. 사용자 조회
        Long userId = 1L;// 포스트맨 쓰기전까지 1로 두기!! 이후 바꿔야 함!!
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
    }


    @Override
    public void deleteEmoji(Long postId, String emojiType) {
        // 1. 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 2. 사용자 조회
        Long userId = 1L;// 포스트맨 쓰기전까지 1로 두기!! 이후 바꿔야 함!!
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


    @Override
    public List<PostResponseDTO.PostPreviewResponseDTO> browsePosts(int page, int size) {
        // 1. 현재 사용자 조회
        Long userId = 1L; // 로그인 구현 시 변경
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. 페이징 처리를 위한 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. 페이징된 게시물 조회
        Page<Post> postPage = postRepository.findAll(pageable);

        // 4. 게시물 데이터를 정렬
        List<Post> sortedPosts = postPage.getContent().stream()
                .filter(post -> post.getPlan() != null) // Plan이 없는 게시물 필터링
                .sorted((post1, post2) -> {
                    // 작성자 가져오기
                    User user1 = post1.getPlan().getUser();
                    User user2 = post2.getPlan().getUser();

                    // 같은 Job인지 확인
                    boolean sameJob1 = user1.getJob() != null && user1.getJob().equals(currentUser.getJob());
                    boolean sameJob2 = user2.getJob() != null && user2.getJob().equals(currentUser.getJob());

                    if (sameJob1 && !sameJob2) {
                        return -1; // 같은 Job이면 우선순위 높게
                    } else if (!sameJob1 && sameJob2) {
                        return 1; // 다른 Job이면 우선순위 낮게
                    } else {
                        // 같은 Job끼리는 이모지 개수로 정렬
                        int emojiCount1 = emojiRepository.countByPost(post1);
                        int emojiCount2 = emojiRepository.countByPost(post2);
                        return Integer.compare(emojiCount2, emojiCount1); // 이모지 개수가 많은 순서
                    }
                })
                .collect(Collectors.toList());

        // 5. 정렬된 게시물을 DTO로 변환하여 반환
        return sortedPosts.stream()
                .map(post -> {
                    User postUser = post.getPlan().getUser(); // 작성자 가져오기
                    return PostResponseDTO.PostPreviewResponseDTO.builder()
                            .postId(post.getId())
                            .profileImageUrl(postUser.getProfileImage()) // 작성자 프로필 이미지
                            .nickname(postUser.getName()) // 작성자 닉네임
                            .postImageUrl(post.getImageUrl()) // 게시물 이미지 URL
                            .build();
                })
                .collect(Collectors.toList());
    }



//    @Override
//    public List<PostResponseDTO.PostPreviewResponseDTO> browsePosts() {
//        // 1. 현재 사용자 조회
//        Long userId = 1L; // 예: 인증된 사용자 ID (로그인 구현 시 변경)
//        User currentUser = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//
//        // 2. 모든 게시물 조회
//        List<Post> allPosts = postRepository.findAll();
//
//        // 3. 게시물 이모지 개수를 계산하고 정렬 기준 적용
//        List<PostResponseDTO.PostPreviewResponseDTO> sortedPosts = allPosts.stream()
//                // 게시물을 DTO로 변환
//                .filter(post -> post.getPlan() != null) // Plan이 없는 게시물 필터링
//                .map(post -> {
//                    int emojiCount = emojiRepository.countByPost(post); // 이모지 개수 계산
//                    User postUser = post.getPlan().getUser(); // 작성자 가져오기
//
//                    return PostResponseDTO.PostPreviewResponseDTO.builder()
//                            .postId(post.getId())
//                            .profileImageUrl(postUser.getProfileImage()) // 작성자 프로필 이미지
//                            .nickname(postUser.getName()) // 작성자 닉네임
//                            .postImageUrl(post.getImageUrl()) // 게시물 이미지 URL
//                            .build();
//                })
//                // 정렬 로직
//                .sorted((dto1, dto2) -> {
//                    Post post1 = postRepository.findById(dto1.getPostId())
//                            .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + dto1.getPostId()));
//                    Post post2 = postRepository.findById(dto2.getPostId())
//                            .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + dto2.getPostId()));
//
//                    User user1 = post1.getPlan().getUser();
//                    User user2 = post2.getPlan().getUser();
//
//                    boolean sameJob1 = user1.getJob().equals(currentUser.getJob());
//                    boolean sameJob2 = user2.getJob().equals(currentUser.getJob());
//
//                    if (sameJob1 && !sameJob2) {
//                        return -1; // 같은 job인 경우 우선순위 높게
//                    } else if (!sameJob1 && sameJob2) {
//                        return 1; // 다른 job인 경우 우선순위 낮게
//                    } else {
//                        // 같은 job끼리는 이모지 개수로 정렬
//                        int emojiCount1 = emojiRepository.countByPost(post1);
//                        int emojiCount2 = emojiRepository.countByPost(post2);
//                        return Integer.compare(emojiCount2, emojiCount1); // 이모지 개수가 많은 순서
//                    }
//                })
//                .collect(Collectors.toList());
//
//        // 4. 정렬된 게시물 리스트 반환
//        return sortedPosts;
//    }


    @Override
    public PostResponseDTO.PostDetailResponseDTO viewPostDetails(Long postId, int page, int size) {
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
                .postId(post.getId())                                     // 게시물 ID
                .postImageUrl(post.getImageUrl())                        // 게시물 이미지 URL
                .profileImage(post.getPlan().getUser().getProfileImage()) // 작성자 프로필 이미지
                .name(post.getPlan().getUser().getName())                // 작성자 이름
                .createdAt(post.getCreatedAt())                          // 게시 시간
                .comments(commentDTOs)                                   // 댓글 리스트 (페이징 적용됨)
                .emojiCounts(allEmojiCounts)                             // 모든 이모지의 개수 리스트
                .userSelectedEmojis(userSelectedEmojis)                  // 사용자 선택 이모지 리스트
                .currentPage(commentPage.getNumber())                    // 현재 댓글 페이지 번호
                .totalPages(commentPage.getTotalPages())                 // 총 댓글 페이지 수
                .totalComments(commentPage.getTotalElements())           // 총 댓글 개수
                .build();
    }

//    @Override
//    public PostResponseDTO.PostDetailResponseDTO viewPostDetails(Long postId) {
//        // 1. 게시물 조회
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));
//
//        // 2. 댓글 리스트 조회
//        List<Comment> comments = commentRepository.findByPost(post);
//
//        // 3. 댓글 리스트를 DTO로 변환
//        List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> commentDTOs = comments.stream()
//                .map(comment -> PostResponseDTO.PostDetailResponseDTO.CommentDTO.builder()
//                        .commentId(comment.getId())                         // 댓글 ID
//                        .userId(comment.getUser().getId())                  // 댓글 작성자 ID
//                        .username(comment.getUser().getName())              // 댓글 작성자 닉네임
//                        .content(comment.getComment())                      // 댓글 내용
//                        .commenterProfileUrl(comment.getUser().getProfileImage()) // 댓글 작성자 프로필 이미지
//                        .createdAt(comment.getCreatedAt())                  // 댓글 작성 시간
//                        .build())
//                .collect(Collectors.toList());
//
//        // 4. 이모지 데이터 조회
//        List<Emoji> emojis = emojiRepository.findByPost(post);
//
//        // 5. 모든 이모지 타입의 개수를 계산
//        Map<EmojiType, Long> emojiCounts = emojis.stream()
//                .collect(Collectors.groupingBy(Emoji::getType, Collectors.counting()));
//
//        // 6. 모든 이모지 타입을 포함하여 기본값 0 설정
//        List<PostResponseDTO.EmojiResponseDTO.EmojiCountDTO> allEmojiCounts = Arrays.stream(EmojiType.values())
//                .map(type -> PostResponseDTO.EmojiResponseDTO.EmojiCountDTO.builder()
//                        .emojiType(type.name())                           // 이모지 타입 이름
//                        .count(emojiCounts.getOrDefault(type, 0L).intValue()) // 개수 (기본값 0)
//                        .build())
//                .collect(Collectors.toList());
//
//        // 7. 사용자 선택 이모지 리스트 구성
//        List<String> userSelectedEmojis = emojis.stream()
//                .map(emoji -> emoji.getType().name())
//                .distinct()
//                .collect(Collectors.toList());
//
//        // 8. PostDetailResponseDTO 생성 및 반환
//        return PostResponseDTO.PostDetailResponseDTO.builder()
//                .postId(post.getId())                                     // 게시물 ID
//                .postImageUrl(post.getImageUrl())                        // 게시물 이미지 URL
//                .profileImage(post.getPlan().getUser().getProfileImage()) // 작성자 프로필 이미지
//                .name(post.getPlan().getUser().getName())                // 작성자 이름
//                .createdAt(post.getCreatedAt())                          // 게시 시간
//                .comments(commentDTOs)                                   // 댓글 리스트
//                .emojiCounts(allEmojiCounts)                             // 모든 이모지의 개수 리스트
//                .userSelectedEmojis(userSelectedEmojis)                  // 사용자 선택 이모지 리스트
//                .build();
//    }


    @Override
    public List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> getComments(Long postId, int page, int size) {
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
                        .userId(comment.getUser().getId())                  // 작성자 ID
                        .username(comment.getUser().getName())              // 작성자 닉네임
                        .content(comment.getComment())                      // 댓글 내용
                        .commenterProfileUrl(comment.getUser().getProfileImage()) // 작성자 프로필 이미지
                        .createdAt(comment.getCreatedAt())                  // 작성 시간
                        .build())
                .collect(Collectors.toList());
    }

//    @Override
//    public List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> getComments(Long postId) {
//        // 1. 게시물 조회
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));
//
//        // 2. 댓글 조회 (parentId를 기준으로 기본 댓글과 대댓글 포함)
//        List<Comment> comments = commentRepository.findByPost(post);
//
//        // 3. 댓글 리스트를 DTO로 변환
//        return comments.stream()
//                .map(comment -> PostResponseDTO.PostDetailResponseDTO.CommentDTO.builder()
//                        .commentId(comment.getId())                         // 댓글 ID
//                        .userId(comment.getUser().getId())                  // 작성자 ID
//                        .username(comment.getUser().getName())          // 작성자 닉네임
//                        .content(comment.getComment())                      // 댓글 내용
//                        .commenterProfileUrl(comment.getUser().getProfileImage()) // 작성자 프로필 이미지
//                        .createdAt(comment.getCreatedAt())                  // 작성 시간
//                        .build())
//                .collect(Collectors.toList());
//    }



    @Override
    public PostResponseDTO.EmojiResponseDTO getEmojis(Long postId) {
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


}


package umc.wegg.service.postService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.wegg.domain.*;
import umc.wegg.domain.enums.EmojiType;
import umc.wegg.domain.mapping.Emoji;
import umc.wegg.dto.PostRequestDTO;
import umc.wegg.dto.PostResponseDTO;
import umc.wegg.repository.*;
import umc.wegg.repository.UserRepository.UserRepository;

import java.util.List;
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
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + requestDTO.getUserId()));

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
    public void addEmoji(Long postId, String emojiType, Long userId) {
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
    }


    @Override
    public void deleteEmoji(Long postId, String emojiType, Long userId) {
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


    @Override
    public List<PostResponseDTO.PostPreviewResponseDTO> browsePosts() {
        // 게시물 둘러보기 로직
        return null;
    }

    @Override
    public PostResponseDTO.PostDetailResponseDTO viewPostDetails(Long postId) {
        // 작성물 상세보기 로직
        return null;
    }

    @Override
    public List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> getComments(Long postId) {
        // 1. 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        // 2. 댓글 조회 (parentId를 기준으로 기본 댓글과 대댓글 포함)
        List<Comment> comments = commentRepository.findByPost(post);

        // 3. 댓글 리스트를 DTO로 변환
        return comments.stream()
                .map(comment -> PostResponseDTO.PostDetailResponseDTO.CommentDTO.builder()
                        .commentId(comment.getId())                         // 댓글 ID
                        .userId(comment.getUser().getId())                  // 작성자 ID
                        .username(comment.getUser().getName())          // 작성자 닉네임
                        .content(comment.getComment())                      // 댓글 내용
                        .commenterProfileUrl(comment.getUser().getProfileImage()) // 작성자 프로필 이미지
                        .createdAt(comment.getCreatedAt())                  // 작성 시간
                        .build())
                .collect(Collectors.toList());
    }



    @Override
    public PostResponseDTO.EmojiResponseDTO getEmojis(Long postId) {
        // 이모지 조회 로직
        return null;
    }
}


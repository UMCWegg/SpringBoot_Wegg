package umc.wegg.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.wegg.domain.*;
import umc.wegg.domain.enums.EmojiType;
import umc.wegg.dto.PostRequestDTO;
import umc.wegg.dto.PostResponseDTO;
import umc.wegg.repository.PlanRepository;
import umc.wegg.repository.TemplateRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostConverter {


    private final TemplateRepository templateRepository;
    private final PlanRepository planRepository;


    // 1. CreatePostDTO -> Post (게시물 등록 요청 -> 엔티티 변환)
    public Post toPost(PostRequestDTO.CreatePostDTO dto, User user) {

        // Plan 엔티티 조회
        Plan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + dto.getPlanId()));

        // Post 엔티티 생성
        return Post.builder()
                //.template(template) // 조회한 Template 엔티티 설정
                .plan(plan)         // 조회한 Plan 엔티티 설정
                //.user(user)         // 게시물 작성자
                //.createdAt(LocalDateTime.now()) // base엔티티에서 생성 시점과 수정 시점을 자동으로 설정
                .build();
    }

    // 2. Post -> PostCreateResponseDTO (게시물 등록 응답 DTO)
    public static PostResponseDTO.PostCreateResponseDTO toPostCreateResponseDTO(Post post) {
        return PostResponseDTO.PostCreateResponseDTO.builder()
                .postId(post.getId())
                //.imageUrl(post.getImageUrl())
                //.templateId(post.getTemplate() != null ? post.getTemplate().getId() : null) // Template의 ID 가져오기
                .planId(post.getPlan() != null ? post.getPlan().getId() : null) // Plan의 ID 가져오기
                .createdAt(post.getCreatedAt())
                .build();
    }


    // 3. Post -> PostDetailResponseDTO (작성글 상세보기 DTO)
    public static PostResponseDTO.PostDetailResponseDTO toPostDetailResponseDTO(
            Post post,
            List<Comment> comments,
            Map<EmojiType, Long> emojiCountsMap, // 모든 이모지 타입과 개수
            List<String> userSelectedEmojis
    ) {
        // 댓글 리스트 변환 (Comment -> CommentDTO)
        List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> commentDTOs = comments.stream()
                .map(comment -> PostResponseDTO.PostDetailResponseDTO.CommentDTO.builder()
                        .commentId(comment.getId())
                        .content(comment.getComment()) // Comment 엔티티의 content 필드
                        .username(comment.getUser() != null ? comment.getUser().getName() : "Unknown") // User의 닉네임
                        .commenterProfileUrl(comment.getUser() != null ? comment.getUser().getProfileImage() : null) // User의 프로필 URL
                        .createdAt(comment.getCreatedAt()) // 댓글 생성 시간
                        .build())
                .collect(Collectors.toList());

        // 모든 이모지 타입을 EmojiCountDTO 리스트로 변환
        List<PostResponseDTO.EmojiResponseDTO.EmojiCountDTO> emojiCounts = Arrays.stream(EmojiType.values())
                .map(type -> PostResponseDTO.EmojiResponseDTO.EmojiCountDTO.builder()
                        .emojiType(type.name()) // 이모지 타입 이름
                        .count(emojiCountsMap.getOrDefault(type, 0L).intValue()) // 이모지 개수 (기본값 0)
                        .build())
                .collect(Collectors.toList());

        // Post -> PostDetailResponseDTO 변환
        return PostResponseDTO.PostDetailResponseDTO.builder()
                .postId(post.getId()) // 게시물 ID
                .postImageUrl(post.getImageUrl()) // 게시물 이미지 URL
                .profileImage(post.getPlan().getUser() != null ? post.getPlan().getUser().getProfileImage() : null) // 작성자 프로필 이미지
                .name(post.getPlan().getUser() != null ? post.getPlan().getUser().getName() : "Unknown") // 작성자 닉네임
                .createdAt(post.getCreatedAt()) // 게시물 작성 시간
                .comments(commentDTOs) // 댓글 리스트
                .emojiCounts(emojiCounts) // 모든 이모지 타입과 개수 리스트
                .userSelectedEmojis(userSelectedEmojis) // 사용자가 선택한 이모지
                .build();
    }



    // 4. Post -> PostPreviewResponseDTO (게시물 둘러보기 응답 DTO)
    public static PostResponseDTO.PostPreviewResponseDTO toPostPreviewResponseDTO(Post post) {
        return PostResponseDTO.PostPreviewResponseDTO.builder()
                .postId(post.getId())
                .profileImageUrl(post.getPlan().getUser().getProfileImage())
                .nickname(post.getPlan().getUser().getName())
                .postImageUrl(post.getImageUrl())
                .build();
    }
    // 게시물 둘러보기 RESPONSEBODY 구성에 쓰이는 DTO로 변환
    private PostResponseDTO.PostPreviewResponseDTO convertToDTO(Post post) {
        User postUser = post.getPlan().getUser();
        return PostResponseDTO.PostPreviewResponseDTO.builder()
                .postId(post.getId())
                .profileImageUrl(postUser.getProfileImage())
                .nickname(postUser.getName())
                .postImageUrl(post.getImageUrl())
                .build();
    }
}

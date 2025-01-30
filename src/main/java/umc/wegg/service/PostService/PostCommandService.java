package umc.wegg.service.PostService;

import umc.wegg.dto.PostRequestDTO;
import umc.wegg.dto.PostResponseDTO;
import java.util.List;

public interface PostCommandService {
    // 게시물 생성
    PostResponseDTO.PostCreateResponseDTO createPost(PostRequestDTO.CreatePostDTO requestDTO);

    // 댓글 등록
    void addComment(PostRequestDTO.AddCommentDTO requestDTO);

    // 댓글 삭제
    void deleteComment(Long postId, Long commentId);

    // 이모지 등록
    void addEmoji(Long postId, String emojiType);

    // 이모지 삭제
    void deleteEmoji(Long postId, String emojiType);

    // 게시물 둘러보기
    List<PostResponseDTO.PostPreviewResponseDTO> browsePosts();

    // 작성물 상세보기
    PostResponseDTO.PostDetailResponseDTO viewPostDetails(Long postId);

    // 댓글 조회
    List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> getComments(Long postId);

    // 이모지 조회
    PostResponseDTO.EmojiResponseDTO getEmojis(Long postId);
}


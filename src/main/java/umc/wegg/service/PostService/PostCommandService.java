package umc.wegg.service.PostService;

import org.springframework.web.multipart.MultipartFile;
import umc.wegg.dto.PostRequestDTO;
import umc.wegg.dto.PostResponseDTO;
import umc.wegg.dto.UserResponseDTO;

import java.io.IOException;
import java.util.List;

public interface PostCommandService {
    // 게시물 생성
    PostResponseDTO.PostCreateResponseDTO createPost(PostRequestDTO.CreatePostDTO requestDTO, MultipartFile postImage) throws IOException;

    // 댓글 등록
    void addComment(PostRequestDTO.AddCommentDTO requestDTO);

    // 댓글 삭제
    void deleteComment(Long postId, Long commentId);

    // 이모지 등록
    void addEmoji(Long postId, String emojiType);

    // 이모지 삭제
    void deleteEmoji(Long postId, String emojiType);

    // 게시물 둘러보기
    //List<List<PostResponseDTO.PostPreviewResponseDTO>> browsePosts(int page, int size);
    PostResponseDTO.BrowsePostsResponse browsePosts(int page, int size);

    // 작성물 상세보기
    PostResponseDTO.PostDetailResponseDTO viewPostDetails(Long postId, int page, int size);

    // 댓글 조회
    List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> getComments(Long postId, int page, int size);

    // 이모지 조회
    PostResponseDTO.EmojiResponseDTO getEmojis(Long postId);

    //유저 보유 템플릿 조회
    public List<PostResponseDTO.TemplateDTO> getUserTemplates();

}


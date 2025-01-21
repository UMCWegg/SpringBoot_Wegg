package umc.wegg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.PostRequestDTO;
import umc.wegg.dto.PostResponseDTO;
import umc.wegg.service.PostService.PostCommandService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostRestController {

    private final PostCommandService postCommandService;

    /**
     * 랜덤 인증(게시물 등록)
     * @param requestDTO 게시물 등록 요청 데이터를 담은 DTO
     * @return 등록된 게시물 정보 (PostCreateResponseDTO)
     */
    @PostMapping
    public ApiResponse<PostResponseDTO.PostCreateResponseDTO> createPost(
            @RequestBody PostRequestDTO.CreatePostDTO requestDTO) {
        PostResponseDTO.PostCreateResponseDTO responseDTO = postCommandService.createPost(requestDTO);
        return ApiResponse.onSuccess(responseDTO);
    }

    /**
     * 댓글 등록
     * @param requestDTO 댓글 등록 요청 데이터를 담은 DTO
     * @return 성공 메시지
     */
    @PostMapping("/comment")
    public ApiResponse<String> addComment(@RequestBody PostRequestDTO.AddCommentDTO requestDTO) {
        postCommandService.addComment(requestDTO);
        return ApiResponse.onSuccess("Comment added successfully.");
    }

    /**
     * 댓글 조회
     * @param postId 댓글이 속한 게시물의 ID
     * @return 해당 게시물의 댓글 리스트 (List<CommentDTO>)
     */
    @GetMapping("/{post_id}/comment")
    public ApiResponse<List<PostResponseDTO.PostDetailResponseDTO.CommentDTO>> getComments(
            @PathVariable("post_id") Long postId) {
        List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> comments = postCommandService.getComments(postId);
        return ApiResponse.onSuccess(comments);
    }

    /**
     * 댓글 삭제
     * @param postId 댓글이 속한 게시물의 ID
     * @param commentId 삭제할 댓글의 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/{post_id}/comment/{comment_id}")
    public ApiResponse<String> deleteComment(
            @PathVariable("post_id") Long postId,
            @PathVariable("comment_id") Long commentId) {
        postCommandService.deleteComment(postId, commentId);
        return ApiResponse.onSuccess("Comment " + commentId + " deleted from post: " + postId);
    }

    /**
     * 이모지 등록
     * @param postId 이모지를 등록할 게시물의 ID
     * @param requestDTO 이모지 등록 요청 데이터를 담은 DTO
     * @return 성공 메시지
     */
    @PostMapping("/{post_id}/emoji")
    public ApiResponse<String> addEmoji(
            @PathVariable("post_id") Long postId,
            @RequestBody PostRequestDTO.AddEmojiDTO requestDTO) {
        postCommandService.addEmoji(postId, requestDTO.getType(), requestDTO.getUserId());
        return ApiResponse.onSuccess("Emoji added to post: " + postId);
    }

    /**
     * 이모지 조회
     * @param postId 이모지를 조회할 게시물의 ID
     * @return 해당 게시물의 이모지 정보 (EmojiResponseDTO)
     */
    @GetMapping("/{post_id}/emoji")
    public ApiResponse<PostResponseDTO.EmojiResponseDTO> getEmojis(@PathVariable("post_id") Long postId) {
        PostResponseDTO.EmojiResponseDTO responseDTO = postCommandService.getEmojis(postId);
        return ApiResponse.onSuccess(responseDTO);
    }

    /**
     * 이모지 삭제
     * @param postId 이모지를 삭제할 게시물의 ID
     * @param requestDTO 삭제할 이모지 정보가 담긴 DTO
     * @return 성공 메시지
     */
    @DeleteMapping("/{post_id}/emoji")
    public ApiResponse<String> deleteEmoji(
            @PathVariable("post_id") Long postId,
            @RequestBody PostRequestDTO.DeleteEmojiDTO requestDTO) {
        postCommandService.deleteEmoji(postId, requestDTO.getType(), requestDTO.getUserId());
        return ApiResponse.onSuccess("Emojis deleted for post: " + postId);
    }

    /**
     * 게시물 둘러보기
     * @return 게시물 프리뷰 리스트 (List<PostPreviewResponseDTO>)
     */
    @GetMapping("/view")
    public ApiResponse<List<PostResponseDTO.PostPreviewResponseDTO>> browsePosts() {
        List<PostResponseDTO.PostPreviewResponseDTO> responseDTOs = postCommandService.browsePosts();
        return ApiResponse.onSuccess(responseDTOs);
    }

    /**
     * 작성물 상세보기
     * @param postId 상세 정보를 조회할 게시물의 ID
     * @return 게시물 상세 정보 (PostDetailResponseDTO)
     */
    @GetMapping("/{post_id}/view")
    public ApiResponse<PostResponseDTO.PostDetailResponseDTO> viewPostDetails(@PathVariable("post_id") Long postId) {
        PostResponseDTO.PostDetailResponseDTO responseDTO = postCommandService.viewPostDetails(postId);
        return ApiResponse.onSuccess(responseDTO);
    }
}

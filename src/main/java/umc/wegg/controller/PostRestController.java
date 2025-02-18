package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.PostRequestDTO;
import umc.wegg.dto.PostResponseDTO;
import umc.wegg.dto.UserResponseDTO;
import umc.wegg.service.PostService.PostCommandService;

import java.io.IOException;
import java.util.List;

//@Tag(name = "Post API", description = "게시물 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostRestController {

    private final PostCommandService postCommandService;

    @Operation(summary = "게시물 등록", description = "랜덤 인증을 통해 게시물을 등록하는 API")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponseDTO.PostCreateResponseDTO> createPost(
            @RequestPart PostRequestDTO.CreatePostDTO requestDTO,  // @RequestBody 제거
            @RequestPart(value = "postImage", required = false) MultipartFile postImage) throws IOException {
        PostResponseDTO.PostCreateResponseDTO responseDTO = postCommandService.createPost(requestDTO, postImage);
        return ApiResponse.onSuccess(responseDTO);
    }

    @Operation(summary = "댓글 등록", description = "게시물에 댓글을 등록하는 API")
    @PostMapping("/comment")
    public ApiResponse<String> addComment(@RequestBody PostRequestDTO.AddCommentDTO requestDTO) {
        postCommandService.addComment(requestDTO);
        return ApiResponse.onSuccess("댓글 등록에 성공하였습니다.");
    }

    @Operation(summary = "댓글 조회", description = "특정 게시물에 대한 댓글 리스트를 페이징하여 조회하는 API")
    @GetMapping("/{post_id}/comments")
    public ApiResponse<List<PostResponseDTO.PostDetailResponseDTO.CommentDTO>> getComments(
            @PathVariable("post_id") Long postId,
            @RequestParam(defaultValue = "0") int page, // 기본값 페이지 0
            @RequestParam(defaultValue = "15") int size // 기본값 크기 15
    ) {
        List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> comments = postCommandService.getComments(postId, page, size);
        return ApiResponse.onSuccess(comments);
    }

    @Operation(summary = "댓글 삭제", description = "특정 게시물의 특정 댓글을 삭제하는 API")
    @DeleteMapping("/{post_id}/comment/{comment_id}")
    public ApiResponse<String> deleteComment(
            @PathVariable("post_id") Long postId,
            @PathVariable("comment_id") Long commentId) {
        postCommandService.deleteComment(postId, commentId);
        return ApiResponse.onSuccess("댓글 " + commentId + "이 포스트 " + postId+"번에서 삭제되었습니다.");
    }

    @Operation(summary = "이모지 등록", description = "게시물에 이모지를 등록하는 API")
    @PostMapping("/{post_id}/emoji")
    public ApiResponse<String> addEmoji(
            @PathVariable("post_id") Long postId,
            @RequestBody PostRequestDTO.AddEmojiDTO requestDTO) {
        postCommandService.addEmoji(postId, requestDTO.getType());
        return ApiResponse.onSuccess("이모지가 " + postId+ "번 포스트에 등록되었습니다.");
    }

    @Operation(summary = "이모지 조회", description = "특정 게시물에 대한 이모지 정보를 조회하는 API")
    @GetMapping("/{post_id}/emoji")
    public ApiResponse<PostResponseDTO.EmojiResponseDTO> getEmojis(@PathVariable("post_id") Long postId) {
        PostResponseDTO.EmojiResponseDTO responseDTO = postCommandService.getEmojis(postId);
        return ApiResponse.onSuccess(responseDTO);
    }

    @Operation(summary = "이모지 삭제", description = "게시물에 등록된 특정 이모지를 삭제하는 API")
    @DeleteMapping("/{post_id}/emoji")
    public ApiResponse<String> deleteEmoji(
            @PathVariable("post_id") Long postId,
            @RequestBody PostRequestDTO.DeleteEmojiDTO requestDTO) {
        postCommandService.deleteEmoji(postId, requestDTO.getType());
        return ApiResponse.onSuccess("Emojis deleted for post: " + postId);
    }

    @Operation(summary = "게시물 둘러보기", description = "팔로우한 사용자와 팔로우하지 않은 사용자의 게시물을 각각 리스트로 반환하는 API")
    @GetMapping("/view")
    public ApiResponse<PostResponseDTO.BrowsePostsResponse> browsePosts(
            @RequestParam(defaultValue = "0") int page, // 기본값 페이지 0
            @RequestParam(defaultValue = "20") int size // 기본값 크기 20
    ) {
        PostResponseDTO.BrowsePostsResponse responseDTOs = postCommandService.browsePosts(page, size);
        return ApiResponse.onSuccess(responseDTOs);
    }

    @Operation(summary = "게시물 상세보기", description = "특정 게시물의 모든 정보를 조회하는 API (댓글 15개씩 페이징 포함)")
    @GetMapping("/{post_id}/view")
    public ApiResponse<PostResponseDTO.PostDetailResponseDTO> viewPostDetails(
            @PathVariable("post_id") Long postId,
            @RequestParam(defaultValue = "0") int page, // 기본값 페이지 0
            @RequestParam(defaultValue = "15") int size // 기본값 크기 15
    ) {
        PostResponseDTO.PostDetailResponseDTO responseDTO = postCommandService.viewPostDetails(postId, page, size);
        return ApiResponse.onSuccess(responseDTO);
    }


    @Operation(summary = "사용자 템플릿 목록 조회", description = "사용자가 보유한 템플릿의 목록을 반환하는 API")
    @GetMapping("/templates")
    public ApiResponse<List<PostResponseDTO.TemplateDTO>> getUserTemplates() {
        List<PostResponseDTO.TemplateDTO> templates = postCommandService.getUserTemplates();
        return ApiResponse.onSuccess(templates);
    }



}

package umc.wegg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.Setting;
import umc.wegg.domain.User;
import umc.wegg.domain.apiPayload.ApiResponse;
import umc.wegg.dto.PostRequestDTO;
import umc.wegg.dto.PostResponseDTO;
import umc.wegg.dto.UserResponseDTO;
import umc.wegg.repository.SettingRepository;
import umc.wegg.repository.UserRepository;
import umc.wegg.service.PostService.PostCommandService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

//@Tag(name = "Post API", description = "게시물 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostRestController {

    private final PostCommandService postCommandService;
    private final UserRepository userRepository;
    private final SettingRepository settingRepository;

    @Operation(summary = "게시물 등록", description = "랜덤 인증을 통해 게시물을 등록하는 API")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponseDTO.PostCreateResponseDTO> createPost(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestPart PostRequestDTO.CreatePostDTO requestDTO,  // @RequestBody 제거
            @RequestPart(value = "postImage", required = false) MultipartFile postImage) throws IOException {
        ApiResponse<PostResponseDTO.PostCreateResponseDTO> responseDTO = postCommandService.createPost(authenticatedUser.getUserId(), requestDTO, postImage);
        return responseDTO;
    }

    @Operation(summary = "댓글 등록", description = "게시물에 댓글을 등록하는 API")
    @PostMapping("/comment")
    public ApiResponse<String> addComment(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,@RequestBody PostRequestDTO.AddCommentDTO requestDTO) {
        postCommandService.addComment(authenticatedUser.getUserId(), requestDTO);
        return ApiResponse.onSuccess("댓글 등록에 성공하였습니다.");
    }

    @Operation(summary = "댓글 조회", description = "특정 게시물에 대한 댓글 리스트를 페이징하여 조회하는 API")
    @GetMapping("/{post_id}/comments")
    public ApiResponse<List<PostResponseDTO.PostDetailResponseDTO.CommentDTO>> getComments(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("post_id") Long postId,
            @RequestParam(defaultValue = "0") int page, // 기본값 페이지 0
            @RequestParam(defaultValue = "15") int size // 기본값 크기 15
    ) {
        List<PostResponseDTO.PostDetailResponseDTO.CommentDTO> comments = postCommandService.getComments(authenticatedUser.getUserId(), postId, page, size);
        return ApiResponse.onSuccess(comments);
    }

    @Operation(summary = "댓글 삭제", description = "특정 게시물의 특정 댓글을 삭제하는 API")
    @DeleteMapping("/{post_id}/comment/{comment_id}")
    public ApiResponse<String> deleteComment(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("post_id") Long postId,
            @PathVariable("comment_id") Long commentId) {
        postCommandService.deleteComment(authenticatedUser.getUserId(), postId, commentId);
        return ApiResponse.onSuccess("댓글 " + commentId + "이 포스트 " + postId+"번에서 삭제되었습니다.");
    }

    @Operation(summary = "이모지 등록", description = "게시물에 이모지를 등록하는 API")
    @PostMapping("/{post_id}/emoji")
    public ApiResponse<String> addEmoji(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("post_id") Long postId,
            @RequestBody PostRequestDTO.AddEmojiDTO requestDTO) {
        postCommandService.addEmoji(authenticatedUser.getUserId(), postId, requestDTO.getType());
        return ApiResponse.onSuccess("이모지가 " + postId+ "번 포스트에 등록되었습니다.");
    }

    @Operation(summary = "이모지 조회", description = "특정 게시물에 대한 이모지 정보를 조회하는 API")
    @GetMapping("/{post_id}/emoji")
    public ApiResponse<PostResponseDTO.EmojiResponseDTO> getEmojis(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,@PathVariable("post_id") Long postId) {
        PostResponseDTO.EmojiResponseDTO responseDTO = postCommandService.getEmojis(authenticatedUser.getUserId(),postId);
        return ApiResponse.onSuccess(responseDTO);
    }

    @Operation(summary = "이모지 삭제", description = "게시물에 등록된 특정 이모지를 삭제하는 API")
    @DeleteMapping("/{post_id}/emoji")
    public ApiResponse<String> deleteEmoji(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("post_id") Long postId,
            @RequestBody PostRequestDTO.DeleteEmojiDTO requestDTO) {
        postCommandService.deleteEmoji(authenticatedUser.getUserId(),postId, requestDTO.getType());
        return ApiResponse.onSuccess("Emojis deleted for post: " + postId);
    }

//    @Operation(summary = "게시물 둘러보기", description = "팔로우한 사용자와 팔로우하지 않은 사용자의 게시물을 각각 리스트로 반환하는 API")
//    @GetMapping("/view")
//    public ApiResponse<List<List<PostResponseDTO.PostPreviewResponseDTO>>> browsePosts(
//            @RequestParam(defaultValue = "0") int page, // 기본값 페이지 0
//            @RequestParam(defaultValue = "20") int size // 기본값 크기 20
//    ) {
//        List<List<PostResponseDTO.PostPreviewResponseDTO>> responseDTOs = postCommandService.browsePosts(page, size);
//        return ApiResponse.onSuccess(responseDTOs);
//    }

    @Operation(summary = "게시물 둘러보기", description = "팔로우한 사용자와 팔로우하지 않은 사용자의 게시물을 각각 리스트로 반환하는 API")
    @GetMapping("/view")
    public ApiResponse<PostResponseDTO.PostBrowseResponseDTO> browsePosts(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(defaultValue = "0") int page, // 기본값 페이지 0
            @RequestParam(defaultValue = "20") int size // 기본값 크기 20
    ) {
        // 1. 현재 사용자 조회
        Long userId = 1L; // 로그인 구현 시 변경
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. 사용자의 설정 조회
        Setting userSetting = settingRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User settings not found"));

        // 3. placeCheck 또는 randomCheck가 false라면 접근 불가 처리
        if (!userSetting.isPlaceCheck() || !userSetting.isRandomCheck()) {
            return ApiResponse.onSuccess(PostResponseDTO.PostBrowseResponseDTO.builder()
                    .canBrowse(false)
                    .followingPosts(Collections.emptyList())
                    .nonFollowingPosts(Collections.emptyList())
                    .build());
        }

        // 4. 게시물 조회
        List<List<PostResponseDTO.PostPreviewResponseDTO>> responseDTOs = postCommandService.browsePosts(authenticatedUser.getUserId(), page, size);

        // 5. 정상 응답 반환
        return ApiResponse.onSuccess(PostResponseDTO.PostBrowseResponseDTO.builder()
                .canBrowse(true)
                .followingPosts(responseDTOs.get(0))
                .nonFollowingPosts(responseDTOs.get(1))
                .build());
    }


    @Operation(summary = "게시물 상세보기", description = "특정 게시물의 모든 정보를 조회하는 API (댓글 15개씩 페이징 포함)")
    @GetMapping("/{post_id}/view")
    public ApiResponse<PostResponseDTO.PostDetailResponseDTO> viewPostDetails(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("post_id") Long postId,
            @RequestParam(defaultValue = "0") int page, // 기본값 페이지 0
            @RequestParam(defaultValue = "15") int size // 기본값 크기 15
    ) {
        PostResponseDTO.PostDetailResponseDTO responseDTO = postCommandService.viewPostDetails(authenticatedUser.getUserId(), postId, page, size);
        return ApiResponse.onSuccess(responseDTO);
    }


    @Operation(summary = "사용자 템플릿 목록 조회", description = "사용자가 보유한 템플릿의 목록을 반환하는 API")
    @GetMapping("/templates")
    public ApiResponse<List<PostResponseDTO.TemplateDTO>> getUserTemplates(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        List<PostResponseDTO.TemplateDTO> templates = postCommandService.getUserTemplates(authenticatedUser.getUserId());
        return ApiResponse.onSuccess(templates);
    }



}

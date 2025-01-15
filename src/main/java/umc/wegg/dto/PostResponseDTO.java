package umc.wegg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;


public class PostResponseDTO {

    //1.게시성공시 ResponseBody (/posts 에 대한 응답)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCreateResponseDTO {
        private Long postId;         // 게시물 ID
        private String imageUrl;     // 게시물 이미지URL
        //private String comment;      // 게시물 코멘트 **게시한 직후 본인게시물이 화면에 렌더링된다고 가정하고, 게시한 직후라 댓글이 없는데 댓글을 반환해줄 필요가 있을지 의문.
        private Long templateId;     // 템플릿 ID
        private Long planId;         // 계획 ID
        private LocalDateTime createdAt; // 생성 시간

        // 추가로 만약 템플릿(글자)을 정한 후 글자의 이동/크기조정/색변경(배경,글자색)이 가능하다면,
        // 글자중심좌표, 배경,글자색 색상코드or색상id, 글자크기 이렇게 리퀘스트바디에 더 받고 리스폰스바디에도 넣어서 줘야함.
    }

    //2.댓글 등록
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentAddResponseDTO {
        private Long commentId;      // 댓글 고유 ID
        private Long postingId;      // 게시물 ID
        private Long userId;         // 댓글 작성자 ID
        private String content;      // 방금 단 댓글 내용
        private LocalDateTime createdAt; // 댓글 생성 시간
    }

    //3.댓글 조회
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponseDTO {
        private Long commentId;         // 댓글 ID
        private Long userId;            // 댓글 작성자 ID
        private String username;        // 댓글 작성자 닉네임
        private String content;         // 댓글 내용
        private String commenterProfileUrl; // 댓글 작성자 프로필 이미지 URL
        private LocalDateTime createdAt; // 댓글 작성 시간
    }

    //4.이모지 조회
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmojiResponseDTO {
        private Long postId; // 게시물 ID
        private int heartCount;    // 하트 이모지 개수
        private int smileCount;    // 웃는 이모지 개수
        private int thumbUpCount;  // 좋아요 이모지 개수
        private List<String> userSelectedEmojis; // 사용자가 선택한 이모지 타입 리스트(프론트 ui에서 본인이 하트 눌렀으면 빨갛게 채운다던지 스마일이 회색에서 색칠해진다던지 할때 필요할 것)
    }

    //5.게시물 둘러보기
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreviewResponseDTO {
        private Long postId;             // 게시물 ID
        private String profileImageUrl;  // 작성자 프로필 사진 URL
        private String nickname;         // 작성자 닉네임
        private String postImageUrl;     // 게시물 이미지 URL
    }

    //6.작성글 상세보기
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetailResponseDTO {
        private Long postId;                   // 게시물 ID
        private String postImageUrl;           // 게시물 이미지 URL
        private String profileImage;        // 작성자 프로필 사진 URL
        private String name;               // 작성자 이름
        private LocalDateTime createdAt;       // 게시 시간
        private List<CommentDTO> comments;     // 댓글 리스트
        private int heartCount;                // 하트 이모지 개수
        private int smileCount;                // 웃는 이모지 개수
        private int thumbUpCount;              // 좋아요 이모지 개수
        private List<String> userSelectedEmojis; // 사용자가 선택한 이모지 타입 리스트

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CommentDTO {
            private Long commentId;         // 댓글 ID
            private Long userId;            // 댓글 작성자 ID
            private String username;        // 댓글 작성자 닉네임
            private String content;         // 댓글 내용
            private String commenterProfileUrl; // 댓글 작성자 프로필 이미지 URL
            private LocalDateTime createdAt; // 댓글 작성 시간
        }
    }


    //7.댓글삭제api에 대한 response는 성공여부만 알려줄 것
    //8.이모지등록api에 대한 response는 성공여부만 알려줄 것
    //9.이모지삭제api에 대한 response는 성공여부만 알려줄 것
}



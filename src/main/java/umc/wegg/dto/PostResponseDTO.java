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
        //private String imageUrl;     // 게시물 이미지URL
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

    // 4. 이모지 조회
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmojiResponseDTO {
        private Long postId;                            // 게시물 ID
        private List<String> userSelectedEmojis;        // 사용자가 선택한 이모지 타입 리스트
        private List<EmojiCountDTO> emojiCounts;        // 모든 이모지 타입과 개수 리스트

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class EmojiCountDTO {
            private String emojiType;                  // 이모지 타입 (예: SMILE, LAUGH)
            private int count;                         // 해당 이모지의 개수
        }
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
        private LocalDateTime createdAt; // 게시물 생성시간
    }

    // 6. 작성글 상세보기
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetailResponseDTO {
        private Long postId;                                   // 게시물 ID
        private String postImageUrl;                           // 게시물 이미지 URL
        private Long userId;                                   // user 아이디
        private String profileImage;                           // 작성자 프로필 사진 URL
        private String accountId;                              // 작성자 계정아이디
        private LocalDateTime createdAt;                       // 게시 시간
        private List<CommentDTO> comments;                     // 댓글 리스트 (페이징 적용)
        private List<EmojiResponseDTO.EmojiCountDTO> emojiCounts; // 모든 이모지 타입과 개수 리스트
        private List<String> userSelectedEmojis;               // 사용자 선택한 이모지 리스트
        private int currentPage;                               // 현재 댓글 페이지 번호
        private int totalPages;                                // 총 댓글 페이지 수
        private long totalComments;                            // 총 댓글 개수

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CommentDTO {
            private Long commentId;                            // 댓글 ID
            private Long userId;                               // 댓글 작성자 ID
            private String username;                           // 댓글 작성자 닉네임
            private String content;                            // 댓글 내용
            private String commenterProfileUrl;               // 댓글 작성자 프로필 이미지
            private LocalDateTime createdAt;                  // 댓글 작성 시간
        }
    }




    //7.댓글삭제api에 대한 response는 성공여부만 알려줄 것
    //8.이모지등록api에 대한 response는 성공여부만 알려줄 것
    //9.이모지삭제api에 대한 response는 성공여부만 알려줄 것
}



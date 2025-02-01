package umc.wegg.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.wegg.domain.Template;

public class PostRequestDTO {

    @Getter
    @NoArgsConstructor
    public static class CreatePostDTO { // 랜덤 인증(게시물 등록)
        private String imageUrl;   // 게시물 이미지 URL
        private String comment;    // 게시물 코멘트
        private Long templateId;   // 템플릿 ID
        private Long planId;   // 계획 ID
    }

    @Getter
    @NoArgsConstructor
    public static class AddCommentDTO { // 댓글 등록
        //private Long userId;       // 댓글 단 사용자 ID
        private Long postingId;    // 게시물 ID
        private String comment;    // 댓글 내용
    }

    @Getter
    @NoArgsConstructor
    public static class DeleteCommentDTO { // 댓글 삭제
        private Long userId;       // 댓글 삭제를 요청한 사용자 ID
        private Long commentId;    // 삭제할 댓글의 ID
    }

    @Getter
    @NoArgsConstructor
    public static class AddEmojiDTO { // 이모지 등록
        //private Long postingId;    // 게시물 ID는 url에 포함되어서 옴.
        //private Long userId;       // 이모지 단 사용자 ID
        private String type;       // 이모지 타입 (예: "heart","smile")
    }

    @Getter
    @NoArgsConstructor
    public static class DeleteEmojiDTO { // 이모지 삭제
        //private Long postingId;    // 게시물 ID는 url에 포함되어서 옴. dto로 한번 더 받을 필요 없음
        //private Long userId;       // 이모지 삭제한 사용자 ID
        private String type;       // 삭제할 이모지 타입
    }
}



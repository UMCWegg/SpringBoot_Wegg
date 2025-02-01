package umc.wegg.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.wegg.domain.common.BaseEntity;
import umc.wegg.domain.enums.Job;
import umc.wegg.domain.enums.ReasonType;
import umc.wegg.domain.mapping.Emoji;
import umc.wegg.domain.mapping.Follow;
import umc.wegg.domain.mapping.MyTemplate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 고유 ID

    @Column(nullable = false)
    private String email; // 사용자 이메일

    @Column(nullable = false, length = 10)
    private String accountId;  // 사용자 계정 ID

    @Column(nullable = false)
    private String password;  // 비밀번호

    @Column(nullable = false, length = 10)
    private String name; // 사용자 이름
    private String profileImage; // 프로필 이미지 URL

    @ColumnDefault("0")
    @Builder.Default
    @Column(nullable = false)
    private int points = 0; // 포인트

    @ColumnDefault("0")
    @Builder.Default
    @Column(nullable = false)
    private int successCount = 0; // 연속 인증 성공 횟수

    @ColumnDefault("0")
    @Builder.Default
    @Column(nullable = false)
    private int successPoint = 0; // 받을 수 있는 포인트

    @Column(nullable = true)
    private Float currentLat; // 현재 위치 위도
    @Column(nullable = true)
    private Float currentLon; // 현재 위치 경도

    @Enumerated(EnumType.STRING)
    private Job job; // 사용자 신분

    @Enumerated(EnumType.STRING)
    private ReasonType reason; // 이 앱을 시작한 이유

    @Column(nullable = false, length = 100)
    private String phone; // 사용자 전화번호

    private String oauthId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notificationList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoList> todoList = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followerList = new ArrayList<>();

    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followingList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Emoji> emojiList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Time> timeList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plan> planList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Egg> eggList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyTemplate> myTemplateList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactFriend> contactUserList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactFriend> contactFriendList = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Setting setting;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactFriend> contactFriends = new ArrayList<>();

    public void setSetting(Setting setting) {
        this.setting = setting;
        if (setting != null) {
            setting.setUser(this); // 양방향 관계 설정
        }
    }

    //암호화된 password
    public void encodePassword(String password) {
        this.password = password;
    }
}

package sparta.barointern.onboarding.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.barointern.onboarding.domain.model.BaseEntity;

@Getter
@Entity
@Table(name = "tb_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티는 기본 생성자가 필수라서 접근 제어자 최소화
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public static User create(String username, String password, String nickname, UserRole role) {
        User user = new User();

        user.username = username;
        user.password = password;
        user.nickname = nickname;
        user.role = role;

        return user;
    }
}

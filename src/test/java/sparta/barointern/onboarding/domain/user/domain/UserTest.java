package sparta.barointern.onboarding.domain.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sparta.barointern.onboarding.domain.user.domain.UserRole.Authority;

class UserTest {

    @Test
    @DisplayName("생성 테스트")
    void create_user_test() {
        // given & when
        User user = User.create("user01", "12345678", "nickname01", UserRole.USER);

        // then
        assertThat(user.getUsername()).isEqualTo("user01");
        assertThat(user.getNickname()).isEqualTo("nickname01");
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getRole().getAuthority()).isEqualTo(Authority.USER);
    }
}
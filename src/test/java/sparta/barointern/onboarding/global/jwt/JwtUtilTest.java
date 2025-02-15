package sparta.barointern.onboarding.global.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void createAccessToken() {
        // given & when
        String accessToken = jwtUtil.createAccessToken("username01", "ROLE_USER");

        // then
        assertThat(accessToken).isNotBlank();
    }

    @Test
    void createRefreshToken() {
        // given & when
        String refreshToken = jwtUtil.createRefreshToken("username01", "ROLE_USER");

        // then
        assertThat(refreshToken).isNotBlank();
    }

    @Test
    void getTokenWithoutBearer() {
        // given
        String accessToken = jwtUtil.createAccessToken("username01", "ROLE_USER");
        String accessTokenWithBearer = jwtUtil.setTokenWithBearer(accessToken);

        // when
        String filteredAccessToken = jwtUtil.getTokenWithoutBearer(accessTokenWithBearer);

        // then
        assertThat(filteredAccessToken).isEqualTo(accessToken);
    }

    @Test
    void getUsernameFromToken() {
        // given
        String accessToken = jwtUtil.createAccessToken("username01", "ROLE_USER");

        // when
        String username = jwtUtil.getUsernameFromToken(accessToken);

        // then
        assertThat(username).isEqualTo("username01");
    }

    @Test
    void getRoleFromToken() {
        // given
        String accessToken = jwtUtil.createAccessToken("username01", "ROLE_USER");

        // when
        String role = jwtUtil.getRoleFromToken(accessToken);

        // then
        assertThat(role).isEqualTo("ROLE_USER");
    }

    @Test
    void validateToken() {
        // given
        String accessToken = jwtUtil.createAccessToken("username01", "ROLE_USER");

        // when
        JwtStatus jwtStatus = jwtUtil.validateToken(accessToken);

        // then
        assertThat(jwtStatus).isEqualTo(JwtStatus.VALID);
    }
}
package sparta.barointern.onboarding.domain.user.dto.res;

import java.util.List;

public record UserSignUpRes(
    String username,
    String nickname,
    List<UserAuthority> authorities
) {

}

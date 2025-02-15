package sparta.barointern.onboarding.domain.user.dto.req;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserLoginReq(
    @Pattern(
        regexp = "^[a-zA-Z0-9가-힣]{1,20}$",
        message = "유저네임은 한글 및 숫자와 영어로 1자 이상 20자 이하만 가능합니다."
    )
    String username,

    @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하여야 합니다.")
    String password
) {

}

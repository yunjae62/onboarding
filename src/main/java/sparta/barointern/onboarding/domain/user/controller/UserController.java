package sparta.barointern.onboarding.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sparta.barointern.onboarding.domain.user.dto.req.UserLoginReq;
import sparta.barointern.onboarding.domain.user.dto.req.UserSignUpReq;
import sparta.barointern.onboarding.domain.user.dto.res.UserLoginRes;
import sparta.barointern.onboarding.domain.user.dto.res.UserSignUpRes;
import sparta.barointern.onboarding.domain.user.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignUpRes> signup(@Valid @RequestBody UserSignUpReq request) {
        UserSignUpRes response = userService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginRes> login(@Valid @RequestBody UserLoginReq request) {
        UserLoginRes response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}

package sparta.barointern.onboarding.domain.user.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sparta.barointern.onboarding.domain.user.domain.User;
import sparta.barointern.onboarding.domain.user.domain.UserRepository;
import sparta.barointern.onboarding.domain.user.domain.UserRole;
import sparta.barointern.onboarding.domain.user.dto.req.UserLoginReq;
import sparta.barointern.onboarding.domain.user.dto.req.UserSignUpReq;
import sparta.barointern.onboarding.domain.user.dto.res.UserAuthority;
import sparta.barointern.onboarding.domain.user.dto.res.UserLoginRes;
import sparta.barointern.onboarding.domain.user.dto.res.UserSignUpRes;
import sparta.barointern.onboarding.global.exception.GlobalException;
import sparta.barointern.onboarding.global.jwt.JwtUtil;
import sparta.barointern.onboarding.global.redis.RedisUtil;
import sparta.barointern.onboarding.global.response.ErrorCase;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSignUpRes signup(UserSignUpReq request) {
        boolean existsByUsername = userRepository.existsByUsername(request.username());

        if (existsByUsername) {
            throw new GlobalException(ErrorCase.DUPLICATED_USERNAME);
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = userRepository.save(User.create(request.username(), encodedPassword, request.nickname(), UserRole.USER));

        return new UserSignUpRes(user.getUsername(), user.getNickname(), List.of(new UserAuthority(UserRole.USER.getAuthority())));
    }

    @Transactional
    public UserLoginRes login(UserLoginReq request) {
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new GlobalException(ErrorCase.USER_NOT_FOUND));

        // JWT 생성
        String accessToken = jwtUtil.createAccessToken(user.getNickname(), user.getRole().getAuthority());
        String refreshToken = jwtUtil.createRefreshToken(user.getNickname(), user.getRole().getAuthority());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();

        // 응답 헤더에 Access token 추가
        response.addHeader(JwtUtil.ACCESS_TOKEN_HEADER, jwtUtil.setTokenWithBearer(accessToken));

        // 응답 쿠키에 Refresh token 추가
        Cookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(refreshToken);
        response.addCookie(refreshTokenCookie);

        redisUtil.set(user.getUsername(), refreshToken);

        return new UserLoginRes(accessToken);
    }
}

package sparta.barointern.onboarding.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sparta.barointern.onboarding.global.exception.GlobalException;
import sparta.barointern.onboarding.global.redis.RedisUtil;
import sparta.barointern.onboarding.global.response.ErrorCase;

/**
 * JWT 검증하는 인증 필터
 */
@Slf4j(topic = "JwtAuthFilter")
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = jwtUtil.getTokenWithoutBearer(request.getHeader(JwtUtil.ACCESS_TOKEN_HEADER));
        log.info("accessToken : {}", accessToken);

        // access token 비어있으면 인증 미처리 후 필터 넘기기
        if (!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        JwtStatus accessTokenStatus = jwtUtil.validateToken(accessToken);

        switch (accessTokenStatus) {
            case VALID -> authenticateLoginUser(accessToken);
            case EXPIRED -> authenticateWithRefreshToken(request, response);
            case INVALID -> throw new GlobalException(ErrorCase.INVALID_TOKEN);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 로그아웃 여부 확인 후 인증 처리
     */
    private void authenticateLoginUser(String accessToken) {

        String username = jwtUtil.getUsernameFromToken(accessToken);

        // 로그아웃 여부 체크
        if (isUserLogout(username)) {
            throw new GlobalException(ErrorCase.LOGIN_REQUIRED);
        }

        setAuthentication(accessToken); // 인증 처리
    }

    private boolean isUserLogout(String username) {
        return redisUtil.get(username, String.class).isEmpty();
    }

    private void authenticateWithRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request.getCookies());

        if (refreshToken == null) {
            throw new GlobalException(ErrorCase.LOGIN_REQUIRED);
        }

        JwtStatus refreshTokenStatus = jwtUtil.validateToken(refreshToken);

        switch (refreshTokenStatus) {
            case VALID -> setAuthWithRenewAccessToken(response, refreshToken);
            case EXPIRED -> throw new GlobalException(ErrorCase.LOGIN_REQUIRED);
            case INVALID -> throw new GlobalException(ErrorCase.INVALID_TOKEN);
        }
    }

    /**
     * 재발급한 토큰들을 추가 후 인증 처리
     */
    private void setAuthWithRenewAccessToken(HttpServletResponse response, String refreshToken) {

        // 요청 리프레쉬 토큰과 레디스의 리프레쉬 토큰이 같은지 검증
        validateSameRefreshToken(refreshToken);

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);

        // Access token & Refresh token 재발급
        String newAccessToken = jwtUtil.createAccessToken(username, role);
        String newRefreshToken = jwtUtil.createRefreshToken(username, role);

        // Refresh token 담은 쿠키 생성
        Cookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(newRefreshToken);

        // 레디스에 새로 발급한 Refresh token 저장
        setUserLogin(username, newRefreshToken);

        // 응답 객체에 담기
        response.addHeader(JwtUtil.ACCESS_TOKEN_HEADER, jwtUtil.setTokenWithBearer(newAccessToken));
        response.addCookie(refreshTokenCookie);

        // 인증 처리
        setAuthentication(refreshToken);
    }

    private void validateSameRefreshToken(String refreshToken) {

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String refreshTokenInRedis = getRefreshToken(username);

        if (isRefreshTokenNotSame(refreshToken, refreshTokenInRedis)) {
            log.error("[서로 다른 리프레쉬 토큰] 닉네임 : {}", username);
            setUserLogout(username);
            throw new GlobalException(ErrorCase.LOGIN_REQUIRED);
        }
    }

    private void setUserLogin(String username, String newRefreshToken) {
        redisUtil.set(username, newRefreshToken);
    }

    private String getRefreshToken(String username) {
        return redisUtil.get(username, String.class).orElse("");
    }

    private void setUserLogout(String username) {
        redisUtil.delete(username);
    }

    private boolean isRefreshTokenNotSame(String refreshToken, String refreshTokenInRedis) {
        return StringUtils.hasText(refreshTokenInRedis) && !refreshToken.equals(refreshTokenInRedis);
    }

    /**
     * 인증 처리
     */
    private void setAuthentication(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(username));
    }

    /**
     * 인증 객체 생성
     */
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}

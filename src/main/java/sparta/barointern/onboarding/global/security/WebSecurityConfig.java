package sparta.barointern.onboarding.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import sparta.barointern.onboarding.global.exception.ExceptionHandlerFilter;
import sparta.barointern.onboarding.global.jwt.JwtAuthFilter;
import sparta.barointern.onboarding.global.jwt.JwtUtil;
import sparta.barointern.onboarding.global.redis.RedisUtil;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final List<String> ALLOWED_ORIGINS = List.of(
        "http://localhost:3000"
    );
    private static final List<String> ALLOWED_METHODS = List.of(
        HttpMethod.GET.name(),
        HttpMethod.POST.name(),
        HttpMethod.PUT.name(),
        HttpMethod.PATCH.name(),
        HttpMethod.DELETE.name(),
        HttpMethod.OPTIONS.name()
    );

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;

    /**
     * 비밀번호 암호화 설정 (BCrypt)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public ExceptionHandlerFilter exceptionHandlerFilter() {
        return new ExceptionHandlerFilter(objectMapper);
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil, redisUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CSRF 비활성화 설정
        http.csrf(AbstractHttpConfigurer::disable);

        // CORS 설정
        http.cors(getCorsConfigurerCustomizer());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Filter 순서 설정
        settingFilterOrder(http);

        // 요청 URL 접근 설정
        settingRequestAuthorization(http);

        return http.build();
    }

    /**
     * CORS 설정
     */
    private Customizer<CorsConfigurer<HttpSecurity>> getCorsConfigurerCustomizer() {
        return corsConfigurer -> corsConfigurer.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(ALLOWED_ORIGINS);
            config.setAllowedMethods(ALLOWED_METHODS);
            config.setAllowedHeaders(List.of("")); // preflight 요청에 대한 응답 헤더 허용
            config.setExposedHeaders(List.of(JwtUtil.REFRESH_TOKEN_HEADER)); // 브라우저가 접근할 수 있는 응답 헤더 허용
            return config;
        });
    }

    /**
     * Filter 설정
     */
    private void settingFilterOrder(HttpSecurity http) {
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionHandlerFilter(), LogoutFilter.class);
    }

    /**
     * 요청 URL 접근 설정
     */
    private void settingRequestAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz ->
            authz
                // 정적 파일
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // Swagger UI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                // user 도메인
                .requestMatchers("/signup", "/login").permitAll()
                // 그 외
                .anyRequest().authenticated()
        );
    }
}


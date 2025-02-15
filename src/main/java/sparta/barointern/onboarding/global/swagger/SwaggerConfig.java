package sparta.barointern.onboarding.global.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sparta.barointern.onboarding.global.jwt.JwtUtil;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Components components = new Components()
            .addSecuritySchemes(JwtUtil.ACCESS_TOKEN_HEADER, accessTokenSecurityScheme())
            .addSecuritySchemes(JwtUtil.REFRESH_TOKEN_HEADER, refreshTokenSecurityScheme());

        SecurityRequirement accessTokenRequirement = new SecurityRequirement().addList(JwtUtil.ACCESS_TOKEN_HEADER);

        return new OpenAPI()
            .info(info())
            .components(components)
            .addSecurityItem(accessTokenRequirement);
    }

    private SecurityScheme accessTokenSecurityScheme() {
        return new SecurityScheme()
            .name(JwtUtil.ACCESS_TOKEN_HEADER) // Authorization 을 키로 함
            .type(SecurityScheme.Type.APIKEY) // name 값을 키로 함. (HTTP 설정 시 Authorization 고정이지만 확장 차)
            .in(In.HEADER) // 액세스 토큰은 헤더에 속함
            .scheme(JwtUtil.BEARER_PREFIX) // Bearer 접두사 붙음
            .bearerFormat("JWT"); // 유형은 JWT
    }

    private SecurityScheme refreshTokenSecurityScheme() {
        return new SecurityScheme()
            .name(JwtUtil.REFRESH_TOKEN_HEADER) // Refresh-Token 을 키로 함
            .type(Type.APIKEY) // name 값을 키로 하기 위해 설정
            .in(In.COOKIE); // 리프레쉬 토큰은 쿠키에 속함
    }

    private Info info() {
        return new Info()
            .title("바로인턴 9기 과제 API 명세서")
            .version("0.0.1");
    }
}

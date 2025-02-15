package sparta.barointern.onboarding.global.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import sparta.barointern.onboarding.global.exception.GlobalException;
import sparta.barointern.onboarding.global.response.ErrorCase;

@Slf4j(topic = "RedisUtil")
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    public <T> Optional<T> get(String key, Class<T> clazz) {
        try {
            String value = redisTemplate.opsForValue().get(key);

            if (value == null) {
                return Optional.empty();
            }

            return Optional.of(objectMapper.readValue(value, clazz));
        } catch (JsonProcessingException e) {
            log.error("redis json parsing error", e);
            throw new GlobalException(ErrorCase.SYSTEM_ERROR);
        }
    }

    public <T> void set(String key, T value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (JsonProcessingException e) {
            log.error("redis json serialization error", e);
            throw new GlobalException(ErrorCase.SYSTEM_ERROR);
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}

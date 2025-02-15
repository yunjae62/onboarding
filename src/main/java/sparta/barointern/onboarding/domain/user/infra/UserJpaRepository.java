package sparta.barointern.onboarding.domain.user.infra;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sparta.barointern.onboarding.domain.user.domain.User;
import sparta.barointern.onboarding.domain.user.domain.UserRepository;

public interface UserJpaRepository extends UserRepository, JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}

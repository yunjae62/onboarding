package sparta.barointern.onboarding.global.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sparta.barointern.onboarding.domain.user.domain.User;
import sparta.barointern.onboarding.domain.user.domain.UserRole;

public record UserDetailsImpl(User user) implements UserDetails {

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole role = user.getRole();
        return List.of(role::getAuthority);
    }
}
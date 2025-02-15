package sparta.barointern.onboarding.global.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sparta.barointern.onboarding.domain.user.domain.User;
import sparta.barointern.onboarding.domain.user.domain.UserRepository;
import sparta.barointern.onboarding.global.exception.GlobalException;
import sparta.barointern.onboarding.global.response.ErrorCase;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new GlobalException(ErrorCase.USER_NOT_FOUND));

        return new UserDetailsImpl(user);
    }
}

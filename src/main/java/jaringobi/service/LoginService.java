package jaringobi.service;

import jaringobi.dto.request.LoginRequest;
import jaringobi.dto.response.LoginResponse;
import jaringobi.domain.user.User;
import jaringobi.domain.user.UserRepository;
import jaringobi.exception.user.UserNotFoundException;
import jaringobi.exception.user.PasswordNotMatchedException;
import jaringobi.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = findUser(loginRequest);

        if (!user.matchesPassword(passwordEncoder, loginRequest.getPassword())) {
            throw new PasswordNotMatchedException();
        }

        String accessToken = tokenProvider.issueAccessToken(user.getId());
        String refreshToken = tokenProvider.issueRefreshToken(user.getId());
        return LoginResponse.from(accessToken, refreshToken);
    }

    private User findUser(LoginRequest loginRequest) {
        return userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(UserNotFoundException::new);
    }
}

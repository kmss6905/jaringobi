package jaringobi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jaringobi.dto.request.LoginRequest;
import jaringobi.dto.response.LoginResponse;
import jaringobi.domain.user.User;
import jaringobi.domain.user.UserRepository;
import jaringobi.exception.user.UserNotFoundException;
import jaringobi.exception.user.PasswordNotMatchedException;
import jaringobi.jwt.TokenProvider;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@MockitoSettings
@DisplayName("로그인 서비스 레이어 테스트")
class LoginServiceTest {

    @InjectMocks
    LoginService loginService;

    @Mock
    UserRepository userRepository;

    @Spy
    PasswordEncoder passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    @Mock
    TokenProvider tokenProvider;

    @Test
    @DisplayName("유저 로그인 성공")
    void successUserLogin() throws NoSuchFieldException, IllegalAccessException {
        LoginRequest loginRequest = new LoginRequest("username123", "password123!");
        User user = User.builder()
                .username("username123")
                .password(passwordEncoder.encode("password123!"))
                .build();
        setId(user);

        // Given
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(tokenProvider.issueAccessToken(1L)).thenReturn("mockAccessToken");
        when(tokenProvider.issueRefreshToken(1L)).thenReturn("mockRefreshToken");

        // When
        LoginResponse loginResponse = loginService.login(loginRequest);

        // Then
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getAccessToken()).isEqualTo("mockAccessToken");
        assertThat(loginResponse.getRefreshToken()).isEqualTo("mockRefreshToken");

        // Verify
        verify(userRepository, times(1)).findByUsername("username123");
        verify(tokenProvider, times(1)).issueAccessToken(1L);
        verify(tokenProvider, times(1)).issueRefreshToken(1L);
    }

    private void setId(User user) throws NoSuchFieldException, IllegalAccessException {
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);
    }

    @Test
    @DisplayName("유저를 찾기못하면 예외를 던진다.")
    void failLoginWhenUserNotExisted() {
        LoginRequest loginRequest = new LoginRequest("username", "password");

        // When
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> loginService.login(loginRequest))
                .isInstanceOf(UserNotFoundException.class);

        // Verify
        verify(tokenProvider, times(0)).issueAccessToken(1L);
        verify(tokenProvider, times(0)).issueRefreshToken(1L);
    }

    @Test
    @DisplayName("비밀번호가 다르면 예외를 던진다.")
    void failLoginWhenNotMatchPassword() {
        LoginRequest loginRequest = new LoginRequest("username123", "password123!");
        User user = User.builder()
                .username("username123")
                .password(passwordEncoder.encode("password123!@"))
                .build();

        // Given
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));

        // Then
        assertThatThrownBy(() -> loginService.login(loginRequest))
                .isInstanceOf(PasswordNotMatchedException.class);

        // Verify
        verify(tokenProvider, times(0)).issueAccessToken(1L);
        verify(tokenProvider, times(0)).issueRefreshToken(1L);
    }
}
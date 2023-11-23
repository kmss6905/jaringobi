package jaringobi.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jaringobi.dto.request.AddUserRequest;
import jaringobi.domain.user.User;
import jaringobi.domain.user.UserRepository;
import jaringobi.exception.user.UsernameDuplicatedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("회원가입 서비스 레이어 테스트")
@MockitoSettings
class JoinServiceTest {

    @Mock
    UserRepository userRepository;

    @Spy
    PasswordEncoder passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    @InjectMocks
    JoinService joinService;
    
    @Test
    @DisplayName("성공 - 유저 가입")
    void join_ShouldSaveUser_WhenUserIsNotDuplicated() {
        // given
        when(userRepository.existsByUsername("newUser")).thenReturn(false);

        // when
        AddUserRequest addUserRequest = new AddUserRequest("newUser", "password");
        joinService.join(addUserRequest);

        // verify
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).existsByUsername("newUser");
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("실패 - 중복 유저의 경우 DuplicatedUserException 예외 발생")
    void join_ShouldThrowDuplicatedUserException_WhenUserIsDuplicated() {
        // given
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        // when
        AddUserRequest addUserRequest = new AddUserRequest("existingUser", "password");

        // then
        assertThrows(UsernameDuplicatedException.class, () -> joinService.join(addUserRequest));

        // verify
        verify(userRepository, never()).save(Mockito.any(User.class));
        verify(userRepository, times(1)).existsByUsername("existingUser");
        verify(passwordEncoder, never()).encode(Mockito.anyString());
    }
}
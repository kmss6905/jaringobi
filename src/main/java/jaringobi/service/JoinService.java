package jaringobi.service;

import jaringobi.dto.request.AddUserRequest;
import jaringobi.domain.user.User;
import jaringobi.domain.user.UserRepository;
import jaringobi.exception.user.UsernameDuplicatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(AddUserRequest addUserRequest) {
        verifyNotDuplicatedUser(addUserRequest.getUsername());

        User user = addUserRequest.toUser();
        user.encryptPassword(passwordEncoder);

        userRepository.save(user);
    }

    private void verifyNotDuplicatedUser(String username) {
        boolean existed = userRepository.existsByUsername(username);
        if (existed) {
            throw new UsernameDuplicatedException();
        }
    }
}

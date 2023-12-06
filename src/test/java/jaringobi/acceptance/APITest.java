package jaringobi.acceptance;

import static java.util.Arrays.stream;

import io.restassured.RestAssured;
import jaringobi.domain.expense.ExpenseRepository;
import jaringobi.domain.user.User;
import jaringobi.domain.user.UserRepository;
import jaringobi.jwt.TokenProvider;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class APITest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected ExpenseRepository expenseRepository;

    @Autowired
    private TokenProvider tokenProvider;

    protected String accessToken;
    protected String anotherUserAccessToken;

    @BeforeEach
    void setUp() throws NotFoundException {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleaner.afterPropertiesSet();
        }
        databaseCleaner.execute();

        // setup Authentication
        saveUsersByIds(1L, 2L);
        accessToken = tokenProvider.issueAccessToken(findUser(1L).getId());
        anotherUserAccessToken = tokenProvider.issueAccessToken(findUser(2L).getId());
    }

    private void saveUsersByIds(Long... ids) {
        List<User> users = stream(ids).map(this::createUser).collect(Collectors.toList());
        userRepository.saveAll(users);
    }

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .username("testuser123" + id)
                .password("testuser123!" + id)
                .build();
    }

    private User findUser(Long id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }
}

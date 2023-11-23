package jaringobi.controller.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jaringobi.dto.request.AddUserRequest;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("회원가입 요청 값 유효성 테스트")
class AddRequestUserTest extends ValidatorTest {

    @Nested
    @DisplayName("계정명(username)")
    class userNameFieldTest {

        @DisplayName("실패 - username 이 null 이거나 empty 이면 안된다.")
        @ParameterizedTest
        @ValueSource(strings = {"", "        "})
        @NullSource
        void usernameIsNull(String username) {
            // when
            var addUserRequest = new AddUserRequest(username, "password123!");

            Set<ConstraintViolation<AddUserRequest>> constraintViolations = validator.validate(addUserRequest);
            boolean existedUsername = constraintViolations.stream()
                    .anyMatch(it -> it.getPropertyPath().toString().equals("username"));

            // then
            assertThat(existedUsername).isTrue();
        }

        @DisplayName("실패 - username 의 길이는 6자 이상 15자 이하 이어야 한다.")
        @ParameterizedTest
        @ValueSource(strings = {"u1", "user2", "username12345678"})
        void usernameNotTooShortOrLong(String username) {
            // when
            var addUserRequest = new AddUserRequest(username, "password123!");

            Set<ConstraintViolation<AddUserRequest>> constraintViolations = validator.validate(addUserRequest);

            // then
            assertThat(constraintViolations).hasSize(1);
            assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(
                    "계정의 길이는 최소 6 이상 최대 15 이하 이어야 합니다.");
        }

        @DisplayName("실패 - username 적어도 하나의 소문자와 하나의 숫자를 포함해야 한다.")
        @ParameterizedTest
        @ValueSource(strings = {"minshij", "username", "123123123"})
        void usernameMustHave(String username) {
            // when
            var addUserRequest = new AddUserRequest(username, "password123!");

            Set<ConstraintViolation<AddUserRequest>> constraintViolations = validator.validate(addUserRequest);

            // then
            assertThat(constraintViolations).hasSize(1);
            assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(
                    "계정은 적어도 하나의 소문자와 하나의 숫자를 포함해야 합니다.");
        }

    }

    @Nested
    @DisplayName("비밀번호(password)")
    class passwordNameFieldTest {

        @DisplayName("실패 - password 이 null 이거나 empty 이면 안된다.")
        @ParameterizedTest
        @ValueSource(strings = {"", "        "})
        @NullSource
        void passwordIsNullOrEmpty(String password) {
            // when
            var addUserRequest = new AddUserRequest("username123", password);

            Set<ConstraintViolation<AddUserRequest>> constraintViolations = validator.validate(addUserRequest);
            boolean existedUsername = constraintViolations.stream()
                    .anyMatch(it -> it.getPropertyPath().toString().equals("password"));

            // then
            assertThat(existedUsername).isTrue();
        }

        @DisplayName("실패 - password 의 길이는 10자 이상 30자 이하 이어야 한다.")
        @ParameterizedTest
        @ValueSource(strings = {"passwod1!", "passwordpasswordpasswordpassword1!"})
        void usernameNotTooShortOrLong(String password) {
            // when
            var addUserRequest = new AddUserRequest("username123", password);

            Set<ConstraintViolation<AddUserRequest>> constraintViolations = validator.validate(addUserRequest);

            // then
            Optional<ConstraintViolation<AddUserRequest>> constraintViolation = constraintViolations.stream()
                    .filter(it -> it.getPropertyPath().toString().equals("password"))
                    .findFirst();

            assertThat(constraintViolation.isPresent()).isTrue();
            assertThat(constraintViolation.get().getMessage()).isEqualTo(
                    "패스워드의 길이는 최소 10 이상 최대 30 이하 이어야 합니다.");
        }

        @DisplayName("실패 - password 적어도 하나의 소문자와 하나의 숫자를 포함해야 한다.")
        @ParameterizedTest
        @ValueSource(strings = {"passwordpaa", "passwordpaa112"})
        void usernameMustHave(String password) {
            // when
            var addUserRequest = new AddUserRequest("username123", password);

            Set<ConstraintViolation<AddUserRequest>> constraintViolations = validator.validate(addUserRequest);

            // then
            assertThat(constraintViolations).hasSize(1);
            assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(
                    "비밀번호는 적어도 하나의 소문자, 하나의 숫자, 하나의 특수문자를 포함해야 합니다.");
        }

    }


}
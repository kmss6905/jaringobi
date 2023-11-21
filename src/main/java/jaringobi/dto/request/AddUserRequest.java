package jaringobi.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jaringobi.domain.user.User;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
public class AddUserRequest {

    @NotBlank(message = "계정은 필수입니다.")
    @Length(min = 6, max = 15, message = "계정의 길이는 최소 {min} 이상 최대 {max} 이하 이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d).+$", message = "계정은 적어도 하나의 소문자와 하나의 숫자를 포함해야 합니다.")
    private String username;

    @NotBlank(message = "패스워드는 필수입니다.")
    @Length(min = 10, max = 30, message = "패스워드의 길이는 최소 {min} 이상 최대 {max} 이하 이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&+=]).+$", message = "비밀번호는 적어도 하나의 소문자, 하나의 숫자, 하나의 특수문자를 포함해야 합니다.")
    private String password;

    public User toUser() {
        return User.builder()
                .username(username)
                .password(password)
                .build();
    }

    public String getUsername() {
        return username;
    }
}

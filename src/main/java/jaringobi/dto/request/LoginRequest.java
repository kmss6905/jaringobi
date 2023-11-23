package jaringobi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "필수입니다.")
    private String username;

    @NotEmpty(message = "필수입니다.")
    private String password;
}

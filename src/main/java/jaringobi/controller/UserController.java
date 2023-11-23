package jaringobi.controller;

import jakarta.validation.Valid;
import jaringobi.common.response.ApiResponse;
import jaringobi.dto.request.AddUserRequest;
import jaringobi.dto.request.LoginRequest;
import jaringobi.dto.response.LoginResponse;
import jaringobi.service.JoinService;
import jaringobi.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final JoinService joinService;
    private final LoginService loginService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody AddUserRequest addUserRequest) {
        joinService.join(addUserRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.ok(loginService.login(loginRequest));
    }
}

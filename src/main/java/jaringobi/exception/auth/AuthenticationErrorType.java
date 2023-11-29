package jaringobi.exception.auth;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthenticationErrorType {

    FORBIDDEN("AUTH_00", "인증이 필요한 접근입니다.", AuthenticationException.class),
    EXPIRED_AT("AUTH_01", "엑세스 토큰이 만료되었습니다.", AccessTokenExpiredException.class),
    INVALID_TOKEN("AUTH_02", "유효하지 않은 토큰입니다.", InvalidTokenException.class),
    NO_PERMISSION("AUTH_03", "해당 작업을 수행할 권한이 없습니다.", NoPermissionException.class);


    private final String code;
    private final String message;
    private final Class<? extends AuthenticationException> classType;
    private static final List<AuthenticationErrorType> errorTypes = Arrays.stream(AuthenticationErrorType.values()).collect(
            Collectors.toList());

    public static AuthenticationErrorType of(Class<? extends AuthenticationException> classType) {
        return errorTypes.stream()
                .filter(it -> it.getClassType().equals(classType))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}

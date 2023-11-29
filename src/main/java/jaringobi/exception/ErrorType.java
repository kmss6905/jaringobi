package jaringobi.exception;


import jaringobi.exception.budget.BudgetNotFoundException;
import jaringobi.exception.budget.InvalidBudgetException;
import jaringobi.exception.budget.LowBudgetException;
import jaringobi.exception.category.CategoryNotFoundException;
import jaringobi.exception.expense.ExpenseNotFoundException;
import jaringobi.exception.expense.ExpenseNullArgumentException;
import jaringobi.exception.expense.ExpenseNullUserException;
import jaringobi.exception.expense.ExpenseSearchDateNotNullException;
import jaringobi.exception.user.PasswordNotMatchedException;
import jaringobi.exception.user.UserNotFoundException;
import jaringobi.exception.user.UsernameDuplicatedException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {

    U001("U001", "이미 존재하는 계정명입니다.", UsernameDuplicatedException.class, HttpStatus.CONFLICT),
    U002("U002", "존재하지 않는 계정입니다.", UserNotFoundException.class, HttpStatus.NOT_FOUND),
    U003("U003", "비밀번호가 일치하지 않습니다.", PasswordNotMatchedException.class, HttpStatus.BAD_REQUEST),

    B001("B001", "올바르지 않은 예산 정보입니다.", InvalidBudgetException.class, HttpStatus.BAD_REQUEST),
    B002("B002", "지정 예산은 0원을 넘을 수 없습니다.", LowBudgetException.class, HttpStatus.BAD_REQUEST),
    B003("B003", "존재하지 않는 예산입니다.", BudgetNotFoundException.class, HttpStatus.NOT_FOUND),

    E001("E001", "필수 지출 정보를 입력바랍니다. 정보 생성 시 (지출 금액, 지출 일, 지출 카테고리)", ExpenseNullArgumentException.class, HttpStatus.BAD_REQUEST),
    E002("E002", "지출 추가 시 유저 정보는 필수입니다.", ExpenseNullUserException.class, HttpStatus.BAD_REQUEST),
    E003("E003", "존재하지 않는 지출 정보입니다.", ExpenseNotFoundException.class, HttpStatus.NOT_FOUND),

    C001("C001", "존재하지 않는 카테고리 입니다.", CategoryNotFoundException.class, HttpStatus.BAD_REQUEST),

    S001("S001", "검색 날짜 입력은 필수입니다.", ExpenseSearchDateNotNullException.class, HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final Class<? extends BudgetGlobalException> classType;
    private final HttpStatus httpStatus;
    private static final List<ErrorType> errorTypes = Arrays.stream(ErrorType.values()).collect(Collectors.toList());

    public static ErrorType of(Class<? extends BudgetGlobalException> classType) {
        return errorTypes.stream()
                .filter(it -> it.classType.equals(classType))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}

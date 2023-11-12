package jaringobi.domain.budget;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jaringobi.exception.budget.InvalidBudgetException;
import jaringobi.exception.budget.LowBudgetException;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BudgetTest {

    @Test
    @DisplayName("Builder 로 Budget 객체 생성 성공")
    void successCreateBudget() {
        assertThatCode(() -> Budget.builder()
                .amount(new Money(10000))
                .categoryId(1L)
                .budgetMonth(Date.from(Instant.now()))
                .build())
        .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Money 가 Null 일 경우 Budget 객체 생성 시 예외를 던진다.")
    void throwExceptionCreateBudgetWhenMoneyIsNull() {
        assertThatThrownBy(() -> Budget.builder()
                .amount(null)
                .categoryId(1L)
                .budgetMonth(Date.from(Instant.now()))
                .build())
        .isInstanceOf(InvalidBudgetException.class);
    }

    @Test
    @DisplayName("categoryId 가 Null 일 경우 Budget 객체 생성 시 예외를 던진다.")
    void throwExceptionCreateBudgetWhenCategoryIdIsNull() {
        assertThatThrownBy(() -> Budget.builder()
                .amount(new Money(100))
                .categoryId(null)
                .budgetMonth(Date.from(Instant.now()))
                .build())
        .isInstanceOf(InvalidBudgetException.class);
    }

    @Test
    @DisplayName("Date 가 Null 일 경우 Budget 객체 생성 시 예외를 던진다.")
    void throwExceptionCreateBudgetWhenIsNull() {
        assertThatThrownBy(() -> Budget.builder()
                .amount(new Money(100))
                .categoryId(1L)
                .budgetMonth(null)
                .build())
        .isInstanceOf(InvalidBudgetException.class);
    }

    @Test
    @DisplayName("Money 가 0원 일 경우 Budget 객체 생성 시 예외를 던진다.")
    void throwExceptionCreateBudgetWhenMoneyIsZero() {
        assertThatThrownBy(() -> Budget.builder()
                .amount(new Money(0))
                .categoryId(1L)
                .budgetMonth(Date.from(Instant.now()))
                .build())
        .isInstanceOf(LowBudgetException.class);
    }

}
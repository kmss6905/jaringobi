package jaringobi.domain.budget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jaringobi.domain.user.AppUser;
import jaringobi.domain.user.User;
import jaringobi.exception.budget.BudgetCategoryDuplicatedException;
import jaringobi.exception.budget.InvalidBudgetException;
import jaringobi.exception.budget.LowBudgetException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BudgetTest {

    @Test
    @DisplayName("Builder 로 CategoryBudget 객체 생성 성공")
    void successCreateBudget() {
        assertThatCode(() -> CategoryBudget.builder()
                .amount(new Money(10000))
                .categoryId(1L)
                .build())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Money 가 Null 일 경우 CategoryBudget 객체 생성 시 예외를 던진다.")
    void throwExceptionCreateBudgetWhenMoneyIsNull() {
        assertThatThrownBy(() -> CategoryBudget.builder()
                .amount(null)
                .categoryId(1L)
                .build())
                .isInstanceOf(InvalidBudgetException.class);
    }

    @Test
    @DisplayName("categoryId 가 Null 일 경우 CategoryBudget 객체 생성 시 예외를 던진다.")
    void throwExceptionCreateBudgetWhenCategoryIdIsNull() {
        assertThatThrownBy(() -> CategoryBudget.builder()
                .amount(new Money(100))
                .categoryId(null)
                .build())
                .isInstanceOf(InvalidBudgetException.class);
    }

    @Test
    @DisplayName("Money 가 0원 일 경우 CategoryBudget 객체 생성 시 예외를 던진다.")
    void throwExceptionCreateBudgetWhenMoneyIsZero() {
        assertThatThrownBy(() -> CategoryBudget.builder()
                .amount(new Money(0))
                .categoryId(1L)
                .build())
                .isInstanceOf(LowBudgetException.class);
    }

    @Test
    @DisplayName("Budget 객체 생성 성공")
    void createBudgetWithValidData() {
        // Given
        BudgetYearMonth yearMonth = BudgetYearMonth.fromString("2023-10");
        User user = User.builder()
                .username("username")
                .password("password")
                .build();

        List<CategoryBudget> categoryBudgets = List.of(
                CategoryBudget.builder().amount(new Money(1000)).categoryId(1L).build(),
                CategoryBudget.builder().amount(new Money(2000)).categoryId(2L).build()
        );

        // When
        Budget budget = Budget.builder()
                .yearMonth(yearMonth)
                .user(user)
                .categoryBudgets(categoryBudgets)
                .build();

        // Then
        assertThat(budget.getYearMonth()).isEqualTo(yearMonth);
        assertThat(budget.getCategoryBudgets()).hasSize(2);
    }

    @Test
    @DisplayName("Budget 만들 때 Null 인 YearMonth 가 오면 예외를 던진다.")
    void createBudgetWithNullYearMonth() {
        // When, Then
        assertThrows(InvalidBudgetException.class, () ->
                Budget.builder()
                        .yearMonth(null)
                        .user(User.builder()
                                .username("username")
                                .password("pssword")
                                .build())
                        .categoryBudgets(List.of())
                        .build());
    }

    @Test
    @DisplayName("이미 만들어진 예산에 새로운 유저로 설정 하면 예외를 던진다.")
    void setUserTwice() {
        // Given
        User user = User.builder()
                .password("password")
                .username("username")
                .build();

        Budget budget = Budget.builder()
                .yearMonth(BudgetYearMonth.fromString("2023-10"))
                .user(user)
                .categoryBudgets(List.of())
                .build();

        // When, Then
        assertThatThrownBy(() -> budget.setUser(user))
                .isInstanceOf(InvalidBudgetException.class);
    }

    @Test
    @DisplayName("이미 있는 예산 카테고리에 카테고리를 다시 설정하려고 하면 예외를 던진다.")
    void setCategoryBudgetsTwice() {
        // Given
        List<CategoryBudget> categoryBudgets = List.of(
                CategoryBudget.builder().amount(new Money(1000)).categoryId(1L).build(),
                CategoryBudget.builder().amount(new Money(2000)).categoryId(2L).build()
        );

        // When
        Budget budget = Budget.builder()
                .yearMonth(BudgetYearMonth.fromString("2023-10"))
                .user(User.builder()
                        .username("username")
                        .password("password")
                        .build())
                .categoryBudgets(categoryBudgets)
                .build();

        // Then
        assertThat(budget.getCategoryBudgets()).hasSize(2);

        // When, Then
        assertThatThrownBy(() -> budget.setCategoryBudgets(categoryBudgets))
                .isInstanceOf(InvalidBudgetException.class);
    }

    @Test
    @DisplayName("예산 소유자 여부 확인 isOwner")
    void isOwnerTest() {
        List<CategoryBudget> categoryBudgets = List.of(
                CategoryBudget.builder().amount(new Money(1000)).categoryId(1L).build(),
                CategoryBudget.builder().amount(new Money(2000)).categoryId(2L).build()
        );

        Budget budget = Budget.builder()
                .yearMonth(BudgetYearMonth.fromString("2023-10"))
                .user(User.builder()
                        .id(1L)
                        .username("username")
                        .password("password")
                        .build())
                .categoryBudgets(categoryBudgets)
                .build();

        AppUser appUser = new AppUser(1L);
        AppUser appUser2 = new AppUser(2L);

        // When, Then
        assertThat(budget.isOwner(appUser)).isTrue();
        assertThat(budget.isOwner(appUser2)).isFalse();
    }

    @Test
    @DisplayName("예산 추가 ")
    void addBudgetCategory() {
        Budget budget = Budget.builder()
                .yearMonth(BudgetYearMonth.fromString("2023-10"))
                .user(User.builder()
                        .username("username")
                        .password("password")
                        .build())
                .categoryBudgets(List.of(
                        CategoryBudget.builder()
                                .amount(new Money(1000))
                                .categoryId(1L)
                                .build()))
                .build();

        CategoryBudget newCategoryBudget = CategoryBudget.builder()
                .amount(new Money(2000))
                .categoryId(2L)
                .build();

        // When
        budget.addBudgetCategory(newCategoryBudget);

        assertThat(budget.getCategoryBudgets()).hasSize(2);
        assertThat(budget.getCategoryBudgets()).extracting("categoryId").containsExactly(1L, 2L);
        assertThat(budget.getCategoryBudgets())
                .extracting(CategoryBudget::getAmount)
                .extracting(Money::getAmount)
                .containsExactly(1000, 2000);
    }

    @Test
    @DisplayName("예산 추가 시 중복 된 카테고리를 추가하는 경우 예외 던진다.")
    void throwExceptionWhenDuplicatedCatefory() {
        // Given
        Budget budget = Budget.builder()
                .yearMonth(BudgetYearMonth.fromString("2023-10"))
                .user(User.builder()
                        .username("username")
                        .password("password")
                        .build())
                .categoryBudgets(List.of(
                        CategoryBudget.builder()
                                .amount(new Money(1000))
                                .categoryId(1L)
                                .build()))
                .build();

        CategoryBudget newCategoryBudget = CategoryBudget.builder()
                .amount(new Money(2000))
                .categoryId(1L)
                .build();

        // When, Then
        assertThatThrownBy(() -> budget.addBudgetCategory(newCategoryBudget))
                .isInstanceOf(BudgetCategoryDuplicatedException.class);
    }
}

package jaringobi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jaringobi.domain.budget.Budget;
import jaringobi.domain.budget.BudgetRepository;
import jaringobi.domain.budget.BudgetYearMonth;
import jaringobi.domain.user.AppUser;
import jaringobi.domain.user.User;
import jaringobi.domain.user.UserRepository;
import jaringobi.dto.request.AddBudgetRequest;
import jaringobi.dto.request.BudgetByCategoryRequest;
import jaringobi.dto.response.AddBudgetResponse;
import jaringobi.exception.auth.NoPermissionException;
import jaringobi.exception.budget.BudgetNotFoundException;
import jaringobi.exception.user.UserNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
public class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BudgetService budgetService;

    private final User user = User.builder()
            .id(1L)
            .username("username")
            .password("password")
            .build();

    private final AddBudgetRequest addBudgetRequest = AddBudgetRequest.builder()
            .budgetByCategories(
                    List.of(BudgetByCategoryRequest.builder()
                            .categoryId(1L)
                            .money(1000).build()))
            .month("2023-10")
            .build();

    private final Budget savedBudget = Budget.builder()
            .id(1L)
            .user(user)
            .categoryBudgets(addBudgetRequest.categoryBudgets())
            .yearMonth(BudgetYearMonth.fromString("2023-10"))
            .build();

    @Test
    @DisplayName("카테고리 서비스 테스트 - 성공")
    void budgetServiceTest() {
        AppUser appUser = new AppUser(1L);

        // Given
        when(userRepository.findById(appUser.userId())).thenReturn(Optional.of(user));
        when(budgetRepository.save(any())).thenReturn(savedBudget);

        // When
        AddBudgetResponse addBudgetResponse = budgetService.addBudget(appUser, addBudgetRequest);

        // Then
        assertThat(addBudgetResponse).isNotNull();
        assertThat(addBudgetResponse.getBudgetNo()).isEqualTo(1L);

        // Verify
        verify(budgetRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("사용자를 찾을 수 없을 경우 예외 던진다. - 실패")
    void throwExceptionWhenNotFoundUser() {
        AppUser appUser = new AppUser(1L);
        var addBudgetRequest = AddBudgetRequest.builder()
                .budgetByCategories(
                        List.of(BudgetByCategoryRequest.builder()
                                .categoryId(1L)
                                .money(1000).build()))
                .month("2023-10")
                .build();

        // Given
        when(userRepository.findById(appUser.userId())).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> budgetService.addBudget(appUser, addBudgetRequest))
                .isInstanceOf(UserNotFoundException.class);

        // Verify
        verify(budgetRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("예산 삭제 - 성공")
    void successDelete() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(savedBudget));
        AppUser appUser = new AppUser(1L);

        // When
        budgetService.deleteBudget(appUser, 1L);

        // Verify
        verify(budgetRepository, times(1)).delete(savedBudget);
    }

    @Test
    @DisplayName("삭제할 예산이 없는 경우 예외 던진다. - 실패")
    void throwExceptionNotExistedBudget() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());
        AppUser appUser = new AppUser(1L);

        // When
        assertThatThrownBy(() -> budgetService.deleteBudget(appUser, 1L))
                .isInstanceOf(BudgetNotFoundException.class);

        // Verify
        verify(budgetRepository, times(0)).delete(savedBudget);
    }

    @Test
    @DisplayName("삭제할 권한이 없는 경우 예외 던진다. - 실패")
    void throwExceptionNoPermission() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(savedBudget));
        AppUser appUser = new AppUser(2L);

        // When
        assertThatThrownBy(() -> budgetService.deleteBudget(appUser, 1L))
                .isInstanceOf(NoPermissionException.class);

        // Verify
        verify(budgetRepository, times(0)).delete(savedBudget);
    }
}

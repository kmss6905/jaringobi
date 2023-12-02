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
import jaringobi.domain.budget.CategoryBudget;
import jaringobi.domain.budget.Money;
import jaringobi.domain.user.AppUser;
import jaringobi.domain.user.User;
import jaringobi.domain.user.UserRepository;
import jaringobi.dto.request.AddBudgetRequest;
import jaringobi.dto.request.BudgetByCategoryRequest;
import jaringobi.dto.request.ModifyBudgetCategory;
import jaringobi.dto.response.AddBudgetResponse;
import jaringobi.exception.auth.NoPermissionException;
import jaringobi.exception.budget.BudgetCategoryDuplicatedException;
import jaringobi.exception.budget.BudgetCategoryNotFoundException;
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
    @DisplayName("예산 추가 - 성공")
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
    @DisplayName("예산 추가 시 사용자를 찾을 수 없을 경우 예외 던진다. - 실패")
    void throwExceptionWhenNotFoundUser() {
        // Given
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
        // Given
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
        // Given
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
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(savedBudget));
        AppUser appUser = new AppUser(2L);

        // When
        assertThatThrownBy(() -> budgetService.deleteBudget(appUser, 1L))
                .isInstanceOf(NoPermissionException.class);

        // Verify
        verify(budgetRepository, times(0)).delete(savedBudget);
    }

    @Test
    @DisplayName("예산 카테고리 추가 성공")
    void successAddBudgetCategory() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(savedBudget));
        AppUser appUser = new AppUser(1L);

        BudgetByCategoryRequest categoryRequest = BudgetByCategoryRequest.builder()
                .categoryId(2L)
                .money(2000)
                .build();

        // When
        budgetService.addBudgetCategory(appUser, 1, categoryRequest);

        // Then
        assertThat(savedBudget.getCategoryBudgets()).hasSize(2);
        assertThat(savedBudget.getCategoryBudgets()).extracting("categoryId").containsExactly(1L, 2L);

        assertThat(savedBudget.getCategoryBudgets())
                .extracting(CategoryBudget::getAmount)
                .extracting(Money::getAmount)
                .containsExactly(1000, 2000);
    }

    @Test
    @DisplayName("카테고리 예산 추가 시 Budget 예산이 없는 경우 예외 던진다. - 실패")
    void throwExceptionNoBudget() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());
        AppUser appUser = new AppUser(1L);

        BudgetByCategoryRequest categoryRequest = BudgetByCategoryRequest.builder()
                .categoryId(2L)
                .money(2000)
                .build();

        // When,  Then
        assertThatThrownBy(() -> budgetService.addBudgetCategory(appUser, 1, categoryRequest))
                .isInstanceOf(BudgetNotFoundException.class);
    }

    @Test
    @DisplayName("카테고리 예산 추가 시 이미 존재하는 카테고리 인 경우 예외 던진다. - 실패")
    void throwExceptionDuplicatedCategory() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(savedBudget));
        AppUser appUser = new AppUser(1L);

        BudgetByCategoryRequest categoryRequest = BudgetByCategoryRequest.builder()
                .categoryId(1L)
                .money(2000)
                .build();

        // When,  Then
        assertThatThrownBy(() -> budgetService.addBudgetCategory(appUser, 1, categoryRequest))
                .isInstanceOf(BudgetCategoryDuplicatedException.class);
    }

    @Test
    @DisplayName("카테고리 예산 추가 시 권한이 없는 경우 예외 던진다. - 실패")
    void throwExceptionWhenAddBudgetCategoryNoPermission() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(savedBudget));
        AppUser appUser = new AppUser(2L);

        BudgetByCategoryRequest categoryRequest = BudgetByCategoryRequest.builder()
                .categoryId(1L)
                .money(2000)
                .build();

        // When,  Then
        assertThatThrownBy(() -> budgetService.addBudgetCategory(appUser, 1, categoryRequest))
                .isInstanceOf(NoPermissionException.class);
    }

    @Test
    @DisplayName("예산 카테고리 수정 성공")
    void successModifyBudgetCategory() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(savedBudget));
        AppUser appUser = new AppUser(1L);

        ModifyBudgetCategory modifyBudgetCategory = ModifyBudgetCategory.builder()
                .money(2000)
                .build();

        // When
        budgetService.modifyBudgetCategory(appUser, 1, 1, modifyBudgetCategory);

        // Then
        assertThat(savedBudget.getCategoryBudgets()).extracting("categoryId").containsExactly(1L);
        assertThat(savedBudget.getCategoryBudgets())
                .extracting(CategoryBudget::getAmount)
                .extracting(Money::getAmount)
                .containsExactly(2000);
    }

    @Test
    @DisplayName("카테고리 예산 수정 시 Budget 예산이 없는 경우 예외 던진다. - 실패")
    void throwExceptionWhenModifyBudgetNotExisted() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());
        AppUser appUser = new AppUser(1L);

        ModifyBudgetCategory modifyBudgetCategory = ModifyBudgetCategory.builder()
                .money(2000)
                .build();

        // When
        assertThatThrownBy(() -> budgetService.modifyBudgetCategory(appUser, 1, 1, modifyBudgetCategory))
                .isInstanceOf(BudgetNotFoundException.class);

        // Verify
        verify(budgetRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("카테고리 예산 수정 시 수정하려는 BudgetCategory 가 없는 경우 예외 던진다. - 실패")
    void throwExceptionWhenModifyCategoryBudgetNotExisted() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(savedBudget));
        AppUser appUser = new AppUser(1L);

        ModifyBudgetCategory modifyBudgetCategory = ModifyBudgetCategory.builder()
                .money(2000)
                .build();

        // When
        assertThatThrownBy(() -> budgetService.modifyBudgetCategory(appUser, 1, 2, modifyBudgetCategory))
                .isInstanceOf(BudgetCategoryNotFoundException.class);

        // Verify
        verify(budgetRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("예산 카테고리 삭제 성공")
    void successDeleteBudgetCategory() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(savedBudget));
        AppUser appUser = new AppUser(1L);

        // When
        budgetService.removeBudgetCategory(appUser, 1, 1);

        // Then
        assertThat(savedBudget.getCategoryBudgets()).hasSize(0);
    }

    @Test
    @DisplayName("예산 카테고리 삭제 시 예산이 존재하지 않으면 예외 던진다 - 실패")
    void throwExceptionNoExistedBudgetWhenDeletingBudgetCategory() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());
        AppUser appUser = new AppUser(1L);

        // When, Then
        assertThatThrownBy(() -> budgetService.removeBudgetCategory(appUser, 1, 1))
                .isInstanceOf(BudgetNotFoundException.class);
    }

    @Test
    @DisplayName("예산 카테고리 삭제 시 삭제하려는 예산 카테고리가 존재하지 않으면 예외 던진다 - 실패")
    void throwExceptionNoExistedBudgetCategoryWhenDeletingBudgetCategory() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(savedBudget));
        AppUser appUser = new AppUser(1L);

        // When, Then
        assertThatThrownBy(() -> budgetService.removeBudgetCategory(appUser, 1, 4))
                .isInstanceOf(BudgetCategoryNotFoundException.class);
    }
}

package jaringobi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jaringobi.dto.request.AddExpenseRequest;
import jaringobi.dto.request.ModifyExpenseRequest;
import jaringobi.dto.response.AddExpenseNoResponse;
import jaringobi.domain.budget.Money;
import jaringobi.domain.category.Category;
import jaringobi.domain.category.CategoryRepository;
import jaringobi.domain.expense.Expense;
import jaringobi.domain.expense.ExpenseRepository;
import jaringobi.domain.user.AppUser;
import jaringobi.domain.user.User;
import jaringobi.domain.user.UserRepository;
import jaringobi.exception.auth.NoPermissionException;
import jaringobi.exception.category.CategoryNotFoundException;
import jaringobi.exception.expense.ExpenseNotFoundException;
import jaringobi.exception.user.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
public class ExpenseServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ExpenseRepository expenseRepository;

    @InjectMocks
    ExpenseService expenseService;

    private final Category category = Category.builder()
            .id(1L)
            .name("식품")
            .build();

    private final Category modifyCategory = Category.builder()
            .id(100L)
            .name("생필품")
            .build();

    private final AppUser appUser = new AppUser(1L);
    private final AppUser anotherUser = new AppUser(2L);

    private final User user = User.builder()
            .id(1L)
            .username("username")
            .password("password")
            .build();

    private final AddExpenseRequest addExpenseRequest = AddExpenseRequest.builder()
            .categoryId(1L)
            .expenseMount(10000)
            .memo("memo")
            .expenseDateTime(LocalDateTime.now())
            .excludeTotalExpense(false)
            .build();

    private final Expense mockExpense = Expense.builder()
            .expenseAt(addExpenseRequest.getExpenseDateTime())
            .money(new Money(addExpenseRequest.getExpenseMount()))
            .memo(addExpenseRequest.getMemo())
            .user(user)
            .category(category)
            .id(1L)
            .build();

    private final ModifyExpenseRequest modifyExpenseRequest = ModifyExpenseRequest.builder()
            .excludeTotalExpense(true)
            .expenseDateTime(LocalDateTime.of(2025, 12, 25, 0, 0))
            .categoryId(100L)
            .expenseMount(100_000)
            .memo("memo2")
            .build();

    @Nested
    @DisplayName("지출 추가")
    class CreateExpense {

        @Test
        @DisplayName("성공")
        void addExpense() throws NoSuchFieldException, IllegalAccessException {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(expenseRepository.save(any())).thenReturn(mockExpense);

            // When
            AddExpenseNoResponse addExpenseNoResponse = expenseService.addExpense(addExpenseRequest, appUser);

            // Then
            Assertions.assertThat(addExpenseNoResponse.expenseNo()).isEqualTo(1L);

            // Verity
            verify(expenseRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저의 경우")
        void throwExceptionAddExpenseNullUser() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // When, Then
            assertThatThrownBy(() -> expenseService.addExpense(addExpenseRequest, appUser))
                    .isInstanceOf(UserNotFoundException.class);

            // Verify
            verify(categoryRepository, times(0)).findById(any());
            verify(expenseRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리의 경우")
        void throwExceptionNullCategory() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // When, Then
            assertThatThrownBy(() -> expenseService.addExpense(addExpenseRequest, appUser))
                    .isInstanceOf(CategoryNotFoundException.class);

            // Verify
            verify(userRepository, times(1)).findById(any());
            verify(expenseRepository, times(0)).save(any());
        }

    }

    @Nested
    @DisplayName("지출 수정")
    class Modify {

        @Test
        @DisplayName("성공")
        void modifyExpense() {
            // Given
            when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));
            when(categoryRepository.findById(100L)).thenReturn(Optional.of(modifyCategory));

            // When
            expenseService.modifyExpense(modifyExpenseRequest, 1L, appUser);

            // Then
            assertThat(mockExpense.getMemo()).isEqualTo(modifyExpenseRequest.getMemo());
            assertThat(mockExpense.getCategory().getId()).isEqualTo(modifyExpenseRequest.getCategoryId());
            assertThat(mockExpense.getMoney().getAmount()).isEqualTo(modifyExpenseRequest.getExpenseMount());
            assertThat(mockExpense.getExpenseAt()).isEqualTo(modifyExpenseRequest.getExpenseDateTime());
            assertThat(mockExpense.isExcludeInTotal()).isEqualTo(modifyExpenseRequest.getExcludeTotalExpense());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리의 경우")
        void throwExceptionNullCategoryWhenModifyExpense() {
            // Given
            when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));
            when(categoryRepository.findById(100L)).thenReturn(Optional.empty());

            // When, Then
            assertThatThrownBy(() -> expenseService.modifyExpense(modifyExpenseRequest, 1L, appUser))
                    .isInstanceOf(CategoryNotFoundException.class);

            // Verify
            verify(expenseRepository, times(1)).findById(any());
            verify(categoryRepository, times(1)).findById(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 지출의 경우")
        void throwExceptionNullExpenseWhenModifyExpense() {
            // Given
            when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

            // When, Then
            assertThatThrownBy(() -> expenseService.modifyExpense(modifyExpenseRequest, 1L, appUser))
                    .isInstanceOf(ExpenseNotFoundException.class);

            // Verify
            verify(expenseRepository, times(1)).findById(any());
            verify(categoryRepository, times(0)).findById(any());
        }

        @Test
        @DisplayName("실패 - 다른 유저가 수정하는 경우 지출 ")
        void throwExceptionNullPermissionWhenModifyExpense() {
            // Given
            when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));

            // When, Then
            assertThatThrownBy(() -> expenseService.modifyExpense(modifyExpenseRequest, 1L, anotherUser))
                    .isInstanceOf(NoPermissionException.class);

            // Verify
            verify(expenseRepository, times(1)).findById(any());
            verify(categoryRepository, times(0)).findById(any());
        }
    }

    @Nested
    @DisplayName("지출 삭제")
    class DeleteExpense {

        @Test
        @DisplayName("성공")
        void successDeleteExpense() {
            // Given
            when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));

            // When, Then
            expenseService.deleteExpense(1L, appUser);

            // Verify
            verify(expenseRepository, times(1)).delete(any());
        }

        @Test
        @DisplayName("실패 - 다른 유저가 삭제시도")
        void failDeleteNoExistedExpense() {
            // Given
            when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

            // When, Then
            assertThatThrownBy(() -> expenseService.deleteExpense(1L, appUser))
                    .isInstanceOf(ExpenseNotFoundException.class);

            // Verify
            verify(expenseRepository, times(0)).delete(any());
        }

        @Test
        @DisplayName("실패 - 다른 유저가 삭제시도")
        void failDeleteExpenseNoPermission() {
            // Given
            when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpense));

            // When, Then
            assertThatThrownBy(() -> expenseService.deleteExpense(1L, anotherUser))
                    .isInstanceOf(NoPermissionException.class);

            // Verify
            verify(expenseRepository, times(0)).delete(any());
        }
    }
}

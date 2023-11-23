package jaringobi.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public AddExpenseNoResponse addExpense(AddExpenseRequest addExpenseRequest, final AppUser appUser) {
        final User user = findUser(appUser);
        final Category category = findCategory(addExpenseRequest.getCategoryId());
        Expense expense = convertToExpense(addExpenseRequest, user, category);
        Expense savedExpense = expenseRepository.save(expense);
        return AddExpenseNoResponse.of(savedExpense);
    }

    @Transactional
    public void modifyExpense(ModifyExpenseRequest modifyExpenseRequest, final Long expenseId, final AppUser appUser) {
        Expense expense = findExpenseOwnerOf(appUser, expenseId);
        final Category category = findCategory(modifyExpenseRequest.getCategoryId());
        expense.modify(modifyExpenseRequest.toExpenseWithCategory(category));
    }

    private Expense findExpenseOwnerOf(AppUser appUser, Long expenseId) {
        final Expense expense = findExpense(expenseId);
        if (!expense.isOwnerOf(appUser)) {
            throw new NoPermissionException();
        }
        return expense;
    }

    private Expense findExpense(Long expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(ExpenseNotFoundException::new);
    }

    private Expense convertToExpense(AddExpenseRequest addExpenseRequest, User user, Category category) {
        final LocalDateTime expandAt = addExpenseRequest.getExpenseDateTime();
        final Money money = new Money(addExpenseRequest.getExpenseMount());
        final String memo = addExpenseRequest.getMemo();
        return Expense.builder()
                .expenseAt(expandAt)
                .money(money)
                .memo(memo)
                .category(category)
                .user(user)
                .build();
    }

    private Category findCategory(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
    }

    private User findUser(AppUser appUser) {
        return userRepository.findById(appUser.userId())
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void deleteExpense(Long expenseId, AppUser appUser) {
        Expense expense = findExpenseOwnerOf(appUser, expenseId);
        expenseRepository.delete(expense);
    }
}

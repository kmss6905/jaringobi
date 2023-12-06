package jaringobi.service.search;

import jaringobi.controller.query.expense.response.TodayExpensePerCategory;
import jaringobi.controller.query.expense.response.TodayExpenseResponse;
import jaringobi.controller.search.CategoryExpenseSum;
import jaringobi.controller.search.ExpenseSearchResponse;
import jaringobi.domain.budget.CategoryBudget;
import jaringobi.domain.expense.Expense;
import jaringobi.domain.expense.ExpenseQueryRepositoryImpl;
import jaringobi.domain.user.AppUser;
import jaringobi.dto.request.ExpenseSearchCondition;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseSearchService {

    private final ExpenseQueryRepositoryImpl expenseQueryRepository;

    @Transactional(readOnly = true)
    public ExpenseSearchResponse searchExpense(AppUser appUser, ExpenseSearchCondition condition) {
        Page<Expense> expenseWithPage = expenseQueryRepository.searchByCondition(appUser, condition);
        List<Expense> expenses = expenseWithPage.toList();
        List<CategoryExpenseSum> categoriesSum = expenseQueryRepository.totalSumOfCategoriesExpense(appUser, condition);
        return ExpenseSearchResponse.from(expenses, categoriesSum, expenseWithPage.isLast());
    }

    @Transactional(readOnly = true)
    public TodayExpenseResponse searchTodayExpense(AppUser appUser) {
        List<TodayExpensePerCategory> todayExpensePerCategories = expenseQueryRepository.todayTotalExpense(appUser);
        List<CategoryBudget> budgetsPerCategory = expenseQueryRepository.getBudgetsPerCategory(appUser);

        if (budgetsPerCategory.isEmpty()) {
            return TodayExpenseResponse.builder()
                    .expensesPerCategory(todayExpensePerCategories)
                    .totalExpenseAmount(todayExpensePerCategories.stream()
                            .mapToInt(TodayExpensePerCategory::getPaidAmount)
                            .sum())
                    .hasBudget(false)
                    .build();
        }

        return TodayExpenseResponse.from(todayExpensePerCategories.stream()
                .map(it -> it.toWithBudgetSum(budgetsPerCategory))
                .collect(Collectors.toList()), budgetsPerCategory);
    }
}

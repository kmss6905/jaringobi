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

    /**
     * 1. 카테고리 별로 오늘 지출한 총액을 조회함
     *
     * 2. 월 별 예산에 포함된 카테고리 별 예산 설정 금액들을 조회함
     *
     * 2.1 만약 월 별 예산이 없을 경우 1번에서 조회함 오늘 지출한 총액을 반환함
     *
     * 2.2 월 별 예산은 있지만 오늘 지출한 총액에 포함될 수 있는 카테고리 예산 항목이 설정되어 있지 않은 경우1번에서 조회한 오늘 지출한 총액을 반환함
     *
     * 3. 1번과 2번에서 조회한 결과를 바탕으로,
     * 오늘 지출한 총액에 카테고리별 설정한 예산을 바탕으로 위험도를 계산하여 응답값에 위험도와 현재 해당 지출 카테고리에 설정된 카테고리 예산 금액을 추가하여 응답함.
     * @param appUser
     * @return
     */
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

package jaringobi.service.search;

import jaringobi.controller.search.CategoryExpenseSum;
import jaringobi.controller.search.ExpenseSearchResponse;
import jaringobi.domain.expense.Expense;
import jaringobi.domain.expense.ExpenseQueryRepositoryImpl;
import jaringobi.domain.user.AppUser;
import jaringobi.dto.request.ExpenseSearchCondition;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseSearchService {

    private final ExpenseQueryRepositoryImpl expenseQueryRepository;

    public ExpenseSearchResponse searchExpense(AppUser appUser, ExpenseSearchCondition condition) {
        Page<Expense> expenseWithPage = expenseQueryRepository.searchByCondition(appUser, condition);
        List<Expense> expenses = expenseWithPage.toList();
        List<CategoryExpenseSum> categoriesSum = expenseQueryRepository.totalSumOfCategoriesExpense(appUser, condition);
        return ExpenseSearchResponse.from(expenses, categoriesSum, expenseWithPage.isLast());
    }
}

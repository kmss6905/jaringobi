package jaringobi.domain.expense;

import jaringobi.controller.query.expense.response.TodayExpensePerCategory;
import jaringobi.domain.budget.CategoryBudget;
import jaringobi.domain.user.AppUser;
import jaringobi.dto.request.ExpenseSearchCondition;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ExpenseQueryRepository {

    Page<Expense> searchByCondition(AppUser appUser, ExpenseSearchCondition expenseSearchCondition);
    List<TodayExpensePerCategory> todayTotalExpense(AppUser appUser);
    List<CategoryBudget> getBudgetsPerCategory(AppUser appUser);
}

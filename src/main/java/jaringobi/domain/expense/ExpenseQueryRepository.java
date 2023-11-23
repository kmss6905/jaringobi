package jaringobi.domain.expense;

import jaringobi.domain.user.AppUser;
import jaringobi.dto.request.ExpenseSearchCondition;
import org.springframework.data.domain.Page;

public interface ExpenseQueryRepository {

    Page<Expense> searchByCondition(AppUser appUser, ExpenseSearchCondition expenseSearchCondition);
}

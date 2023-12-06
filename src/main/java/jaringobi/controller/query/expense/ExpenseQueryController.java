package jaringobi.controller.query.expense;

import jaringobi.auth.AuthenticationPrincipal;
import jaringobi.common.response.ApiResponse;
import jaringobi.controller.query.expense.response.TodayExpenseResponse;
import jaringobi.domain.user.AppUser;
import jaringobi.service.search.ExpenseSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/expenditures")
public class ExpenseQueryController {

    private final ExpenseSearchService expenseSearchService;

    public ExpenseQueryController(ExpenseSearchService expenseSearchService) {
        this.expenseSearchService = expenseSearchService;
    }

    @GetMapping("/today")
    public ApiResponse<TodayExpenseResponse> query(@AuthenticationPrincipal AppUser appUser) {
        return ApiResponse.ok(expenseSearchService.searchTodayExpense(appUser));
    }
}

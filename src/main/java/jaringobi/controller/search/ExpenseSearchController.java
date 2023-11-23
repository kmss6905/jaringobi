package jaringobi.controller.search;

import jakarta.validation.Valid;
import jaringobi.auth.AuthenticationPrincipal;
import jaringobi.common.response.ApiResponse;
import jaringobi.domain.user.AppUser;
import jaringobi.dto.request.ExpenseSearchCondition;
import jaringobi.service.search.ExpenseSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/expenditures")
public class ExpenseSearchController {

    public final ExpenseSearchService expenseSearchService;

    @GetMapping
    public ApiResponse<ExpenseSearchResponse> searchExpenditures(
            @AuthenticationPrincipal AppUser appUser,
            @Valid ExpenseSearchParameter expenseSearchParameter
    ) {
        ExpenseSearchCondition condition = expenseSearchParameter.toCondition();
        ExpenseSearchResponse expenseSearchResponse = expenseSearchService.searchExpense(appUser, condition);
        return ApiResponse.ok(expenseSearchResponse);
    }
}

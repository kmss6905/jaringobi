package jaringobi.controller;

import jakarta.validation.Valid;
import jaringobi.auth.AuthenticationPrincipal;
import jaringobi.common.response.ApiResponse;
import jaringobi.dto.request.AddExpenseRequest;
import jaringobi.dto.request.ModifyExpenseRequest;
import jaringobi.dto.response.AddExpenseNoResponse;
import jaringobi.domain.user.AppUser;
import jaringobi.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/expenditures")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ApiResponse<AddExpenseNoResponse> addExpense(
            @AuthenticationPrincipal AppUser appUser,
            @Valid @RequestBody AddExpenseRequest addExpenseRequest) {
        return ApiResponse.ok(expenseService.addExpense(addExpenseRequest, appUser));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> modifyExpense(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser appUser,
            @Valid @RequestBody ModifyExpenseRequest modifyExpenseRequest
    ) {

        expenseService.modifyExpense(modifyExpenseRequest, id, appUser);
        return ApiResponse.noContent();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteExpense(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser appUser
    ) {
        expenseService.deleteExpense(id, appUser);
        return ApiResponse.noContent();
    }

}

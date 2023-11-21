package jaringobi.controller.request;

import jakarta.validation.Valid;
import jaringobi.controller.request.validator.NotDuplicated;
import jaringobi.controller.request.validator.YearMonthPattern;
import jaringobi.domain.budget.Budget;
import jaringobi.domain.budget.BudgetYearMonth;
import jaringobi.domain.budget.CategoryBudget;
import jaringobi.domain.budget.Money;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AddBudgetRequest {

    @NotDuplicated
    private List<@Valid BudgetByCategoryRequest> budgetByCategories;

    @YearMonthPattern
    private String month;

    @Builder
    public AddBudgetRequest(List<BudgetByCategoryRequest> budgetByCategories, String month) {
        this.budgetByCategories = budgetByCategories;
        this.month = month;
    }

    public Budget toBudget() {
        return Budget.builder()
                .yearMonth(BudgetYearMonth.fromString(month))
                .build();
    }

    public List<CategoryBudget> categoryBudgets() {
        return budgetByCategories.stream()
                .map(it -> CategoryBudget
                        .builder()
                        .categoryId(it.getCategoryId())
                        .amount(new Money(it.getMoney()))
                        .build())
                .collect(Collectors.toList());
    }
}

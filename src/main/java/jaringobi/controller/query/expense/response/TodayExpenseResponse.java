package jaringobi.controller.query.expense.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jaringobi.domain.budget.CategoryBudget;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
public class TodayExpenseResponse {

    private final List<TodayExpensePerCategory> expensesPerCategory;
    private final long totalExpenseAmount;
    private final boolean hasBudget;
    private Long budgetAmount;
    private Integer dangerPercent;

    @Builder
    public TodayExpenseResponse(List<TodayExpensePerCategory> expensesPerCategory, long totalExpenseAmount,
            boolean hasBudget, long budgetAmount, Integer dangerPercent) {
        this.expensesPerCategory = expensesPerCategory;
        this.totalExpenseAmount = totalExpenseAmount;
        this.hasBudget = hasBudget;
        this.budgetAmount = budgetAmount;
        this.dangerPercent = dangerPercent;
    }

    public static TodayExpenseResponse from(List<TodayExpensePerCategory> todayExpensePerCategories,
            List<CategoryBudget> budgetsPerCategory) {

        int dailyBudget = budgetsPerCategory.stream()
                .mapToInt(it -> it.getAmount().getAmount())
                .sum() / 30;

        int dangerPercent = (todayExpensePerCategories.stream()
                .mapToInt(TodayExpensePerCategory::getPaidAmount)
                .sum() / dailyBudget) * 100;

        return TodayExpenseResponse.builder()
                .expensesPerCategory(todayExpensePerCategories)
                .totalExpenseAmount(todayExpensePerCategories.stream()
                        .mapToInt(TodayExpensePerCategory::getPaidAmount)
                        .sum())
                .budgetAmount(budgetsPerCategory.stream()
                        .mapToInt(it -> it.getAmount().getAmount())
                        .sum())
                .hasBudget(true)
                .dangerPercent(dangerPercent)
                .build();
    }
}

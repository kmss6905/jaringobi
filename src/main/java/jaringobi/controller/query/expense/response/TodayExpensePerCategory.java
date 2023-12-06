package jaringobi.controller.query.expense.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jaringobi.domain.budget.CategoryBudget;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TodayExpensePerCategory {
    private long categoryId;
    private int paidAmount;
    private Integer availableAmount;
    private Integer dangerPercent;
    private boolean hasCategoryBudget;

    @Builder
    public TodayExpensePerCategory(long categoryId, int paidAmount, Integer availableAmount, Integer dangerPercent, boolean hasCategoryBudget) {
        this.categoryId = categoryId;
        this.paidAmount = paidAmount;
        this.availableAmount = availableAmount;
        this.dangerPercent = dangerPercent;
        this.hasCategoryBudget = hasCategoryBudget;
    }

    public static TodayExpensePerCategory from(
            TodayExpensePerCategory todayExpensePerCategory,
            List<CategoryBudget> categoryBudget
    ) {
        Optional<CategoryBudget> optionalCategoryBudget = categoryBudget.stream()
                .filter(it -> it.getCategoryId().equals(todayExpensePerCategory.categoryId))
                .findFirst();
        if (optionalCategoryBudget.isEmpty()) {
            return todayExpensePerCategory;
        }
        int dailyCategoryBudget = optionalCategoryBudget.get().getAmount().getAmount() / 30;
        return TodayExpensePerCategory.builder()
                .categoryId(todayExpensePerCategory.categoryId)
                .paidAmount(todayExpensePerCategory.paidAmount)
                .availableAmount(optionalCategoryBudget.get().getAmount().getAmount())
                .dangerPercent((todayExpensePerCategory.paidAmount / dailyCategoryBudget) * 100)
                .hasCategoryBudget(true)
                .build();
    }

    public TodayExpensePerCategory toWithBudgetSum(List<CategoryBudget> budgetsPerCategory) {
        CategoryBudget categoryBudget = budgetsPerCategory.stream()
                .filter(it -> it.getCategoryId() == categoryId)
                .findFirst()
                .orElse(null);
        if (categoryBudget == null) {
            return this;
        }
        return TodayExpensePerCategory.from(this, budgetsPerCategory);
    }
}

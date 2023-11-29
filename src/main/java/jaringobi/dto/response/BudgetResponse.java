package jaringobi.dto.response;

import jaringobi.domain.budget.Budget;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record BudgetResponse(
        List<BudgetByCategoryResponse> budgetByCategories,
        String month,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BudgetResponse of(Budget budget) {
        return BudgetResponse.builder()
                .budgetByCategories(budget.getCategoryBudgets()
                        .stream()
                        .map(BudgetByCategoryResponse::of)
                        .collect(Collectors.toList()))
                .month(budget.getYearMonth().getMonth().toString())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
}

package jaringobi.dto.response;

import jaringobi.domain.budget.CategoryBudget;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BudgetByCategoryResponse(
        long id, long categoryId, int money, LocalDateTime createdAt, LocalDateTime updatedAt
) {

    public static BudgetByCategoryResponse of(CategoryBudget categoryBudget) {
        return BudgetByCategoryResponse.builder()
                .id(categoryBudget.getId())
                .categoryId(categoryBudget.getCategoryId())
                .money(categoryBudget.getAmount().getAmount())
                .createdAt(categoryBudget.getCreatedAt())
                .updatedAt(categoryBudget.getUpdatedAt())
                .build();
    }
}

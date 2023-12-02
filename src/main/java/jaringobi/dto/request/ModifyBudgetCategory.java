package jaringobi.dto.request;

import jakarta.validation.constraints.Min;
import jaringobi.domain.budget.CategoryBudget;
import jaringobi.domain.budget.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyBudgetCategory {

    @Min(value = 1, message = "{value} 이상 이어야 합니다.")
    private int money;

    public CategoryBudget toCategoryBudgetWithCategory(long categoryId) {
        return CategoryBudget.builder()
                .categoryId(categoryId)
                .amount(new Money(money))
                .build();
    }
}

package jaringobi.dto.response;

import jaringobi.domain.budget.Budget;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AddBudgetResponse {

    private Long budgetNo;

    public static AddBudgetResponse of(Budget budget) {
        return new AddBudgetResponse(budget.getId());
    }
}

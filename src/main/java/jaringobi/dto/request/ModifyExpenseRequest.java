package jaringobi.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jaringobi.domain.budget.Money;
import jaringobi.domain.category.Category;
import jaringobi.domain.expense.Expense;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ModifyExpenseRequest {

    @NotNull(message = "지출 금액은 필수입니다.")
    @Positive(message = "지출 금액은 최소 0원 보다 커야합니다.")
    private Integer expenseMount;

    @NotNull(message = "지출일은 필수입니다.")
    private LocalDateTime expenseDateTime;

    @NotNull(message = "카테고리는 필수입니다.")
    @Positive(message = "잘못된 카테고리 번호입니다.")
    private Long categoryId;

    private String memo;

    @NotNull(message = "합계 제외 여부는 필수입니다.")
    private Boolean excludeTotalExpense;

    public Expense toExpenseWithCategory(Category category) {
        return Expense.builder()
                .exclude(excludeTotalExpense)
                .category(category)
                .expenseAt(expenseDateTime)
                .money(new Money(expenseMount))
                .memo(memo)
                .build();
    }

    @Builder
    public ModifyExpenseRequest(final Integer expenseMount, final LocalDateTime expenseDateTime, final Long categoryId, final String memo,
            final Boolean excludeTotalExpense) {
        this.expenseMount = expenseMount;
        this.expenseDateTime = expenseDateTime;
        this.categoryId = categoryId;
        this.memo = memo;
        this.excludeTotalExpense = excludeTotalExpense;
    }
}

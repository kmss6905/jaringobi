package jaringobi.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddExpenseRequest {

    @NotNull(message = "지출 금액은 필수입니다.")
    @Positive(message = "지출 금액은 최소 0원 보다 커야합니다.")
    private Integer expenseMount;

    @NotNull(message = "지출일은 필수입니다.")
    private LocalDateTime expenseDateTime;

    @NotNull(message = "카테고리는 필수입니다.")
    @Positive(message = "잘못된 카테고리 번호입니다.")
    private Long categoryId;

    private String memo;
    private Boolean excludeTotalExpense;

    @Builder
    public AddExpenseRequest(String memo, int expenseMount, LocalDateTime expenseDateTime, Boolean excludeTotalExpense,
            Long categoryId) {
        this.memo = memo;
        this.expenseMount = expenseMount;
        this.expenseDateTime = expenseDateTime;
        this.excludeTotalExpense = excludeTotalExpense;
        this.categoryId = categoryId;
    }
}

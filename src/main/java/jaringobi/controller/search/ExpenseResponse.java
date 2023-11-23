package jaringobi.controller.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jaringobi.domain.expense.Expense;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse implements Serializable {
    private long expenseId;

    @JsonInclude(Include.NON_NULL)
    private String memo;
    private int expenseMount;
    private LocalDateTime expenseAt;
    private long categoryId;

    public static List<ExpenseResponse> listOf(List<Expense> expenses) {
        return expenses.stream()
                .map(ExpenseResponse::of)
                .collect(Collectors.toList());
    }

    public static ExpenseResponse of(Expense expense) {
        return ExpenseResponse.builder()
                .expenseId(expense.getId())
                .expenseAt(expense.getExpenseAt())
                .expenseMount(expense.getMoney().getAmount())
                .memo(expense.getMemo())
                .categoryId(expense.getCategory().getId())
                .build();
    }
}

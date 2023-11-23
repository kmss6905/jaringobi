package jaringobi.controller.search;

import jaringobi.domain.expense.Expense;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSearchResponse implements Serializable {

    private List<ExpenseResponse> expenseResponses;
    private boolean isEnd;
    private int totalExpenditure;
    private List<CategoryExpenseSum> categoryExpenseSums;

    public static ExpenseSearchResponse from(
            List<Expense> expenses,
            List<CategoryExpenseSum> categoryExpenseSums,
            boolean isEnd
    ) {
        return ExpenseSearchResponse.builder()
                .expenseResponses(ExpenseResponse.listOf(expenses))
                .categoryExpenseSums(categoryExpenseSums)
                .totalExpenditure(toSum(categoryExpenseSums))
                .isEnd(isEnd)
                .build();
    }

    private static int toSum(List<CategoryExpenseSum> categoryExpenseSums) {
        return categoryExpenseSums.stream()
                .mapToInt(CategoryExpenseSum::getTotalExpenseAmount)
                .sum();
    }
}

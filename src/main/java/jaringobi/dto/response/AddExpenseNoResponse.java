package jaringobi.controller.response;

import jaringobi.domain.expense.Expense;

public record AddExpenseNoResponse(Long expenseNo) {

    public static AddExpenseNoResponse of(Expense expense) {
        return new AddExpenseNoResponse(expense.getId());
    }
}

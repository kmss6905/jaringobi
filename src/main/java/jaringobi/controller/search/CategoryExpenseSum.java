package jaringobi.controller.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryExpenseSum {
    private Long categoryId;
    private Integer totalExpenseAmount;
}

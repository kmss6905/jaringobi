package jaringobi.controller.search;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jaringobi.dto.SearchOrder;
import jaringobi.dto.SearchSort;
import jaringobi.dto.request.ExpenseSearchCondition;
import jaringobi.dto.request.validator.DateFormat;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;


@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSearchParameter implements Serializable {

    @NotNull
    @DateFormat
    private String start;

    @NotNull
    @DateFormat
    private String end;

    @Positive
    private Integer min;

    @Positive
    private Integer max;

    @Min(value = 1)
    private Integer page;

    @Range(min = 10, max = 100)
    private Integer size;

    private SearchOrder order;
    private SearchSort sort;
    private List<Long> cids;

    public ExpenseSearchCondition toCondition() {
        return ExpenseSearchCondition.builder()
                .start(start)
                .end(end)
                .categoryIds(cids)
                .max(max)
                .min(min)
                .order(order)
                .sort(sort)
                .size(size)
                .build();
    }
}

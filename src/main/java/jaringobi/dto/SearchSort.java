package jaringobi.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public enum SearchSort implements Serializable {
    EXPENSE_DATE("expense_date"),
    CREATED("created"),
    AMOUNT("amount");

    private String value;

    SearchSort(String value) { this.value = value; }

    private static final List<SearchSort> sorts = List.of(values());

    public static SearchSort of(String value) {
        if (Objects.isNull(value)) {
            return EXPENSE_DATE;
        }

        return sorts.stream()
                .filter(searchSort -> searchSort.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(EXPENSE_DATE);
    }

}

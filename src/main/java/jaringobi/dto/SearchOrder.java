package jaringobi.dto;

import java.util.List;
import java.util.Objects;

public enum SearchOrder {
    ASC,
    DESC;

    private static final List<SearchOrder> orders = List.of(values());

    public static SearchOrder of(String searchOrder) {
        if (Objects.isNull(searchOrder)) {
            return DESC;
        }
        return orders.stream()
                .filter(order -> order.name().equalsIgnoreCase(searchOrder))
                .findFirst()
                .orElse(DESC);
    }
}

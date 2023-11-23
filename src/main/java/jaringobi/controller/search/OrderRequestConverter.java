package jaringobi.controller.search;

import jaringobi.dto.SearchOrder;
import org.springframework.core.convert.converter.Converter;

public class OrderRequestConverter implements Converter<String, SearchOrder> {

    @Override
    public SearchOrder convert(String searchOrder) {
        return SearchOrder.of(searchOrder);
    }
}

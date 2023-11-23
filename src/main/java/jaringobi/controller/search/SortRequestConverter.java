package jaringobi.controller.search;

import jaringobi.dto.SearchSort;
import org.springframework.core.convert.converter.Converter;

public class SortRequestConverter implements Converter<String, SearchSort> {

    @Override
    public SearchSort convert(String searchSort) {
        return SearchSort.of(searchSort);
    }
}

package jaringobi.controller.search;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class CategoryIds {

    private final List<Long> ids;

    private CategoryIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            this.ids = new ArrayList<>();
            return;
        }
        this.ids = ids;
    }

    public boolean isEmpty() {
        return this.ids.isEmpty();
    }

    public static CategoryIds of(List<Long> ids) {
        return new CategoryIds(ids);
    }
}

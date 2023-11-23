package jaringobi.controller.query.category.response;

import jaringobi.domain.category.Category;

public record CategoryResponse(long id, String name) {

    public static CategoryResponse of(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}

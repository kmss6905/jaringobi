package jaringobi.controller.category;

import jaringobi.common.response.ApiResponse;
import jaringobi.common.response.Payload;
import jaringobi.controller.category.response.CategoryResponse;
import jaringobi.domain.category.CategoryQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryQuery categoryQuery;

    public CategoryController(CategoryQuery categoryQuery) {
        this.categoryQuery = categoryQuery;
    }

    @GetMapping
    public ApiResponse<Payload<CategoryResponse>> getCategory() {
        return ApiResponse.ok(Payload.of(categoryQuery.findAll()
                .stream().map(CategoryResponse::of)
                .toList()));
    }
}

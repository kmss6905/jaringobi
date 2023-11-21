package jaringobi.controller.request.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jaringobi.controller.request.BudgetByCategoryRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotDuplicatedCategoryValidator implements ConstraintValidator<NotDuplicated, List<BudgetByCategoryRequest>> {

    @Override
    public void initialize(NotDuplicated constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<BudgetByCategoryRequest> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        Set<Long> categorySet = new HashSet<>();
        for (BudgetByCategoryRequest request : value) {
            if (alreadyInRequests(categorySet, request)) {
                return false;
            }
        }
        return true;
    }

    private static boolean alreadyInRequests(Set<Long> categorySet, BudgetByCategoryRequest request) {
        return !categorySet.add(request.getCategoryId());
    }
}

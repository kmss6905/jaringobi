package jaringobi.dto.request.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class YearMonthValidator implements ConstraintValidator<YearMonthPattern, String> {

    private static final String PATTERN = "^(\\d{4})-(0[1-9]|1[0-2])$";

    @Override
    public void initialize(YearMonthPattern constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && Pattern.matches(PATTERN, value);
    }
}
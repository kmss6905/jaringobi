package jaringobi.dto.request.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {

    // yyyy-MM-dd
    private static final String PATTERN = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && Pattern.matches(PATTERN, value);
    }
}

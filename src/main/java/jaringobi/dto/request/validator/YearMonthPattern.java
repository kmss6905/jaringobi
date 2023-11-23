package jaringobi.dto.request.validator;

import static java.lang.annotation.ElementType.FIELD;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = YearMonthValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface YearMonthPattern {

    String message() default "예산 날짜는 'yyyy-MM' 형식이어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
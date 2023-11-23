package jaringobi.dto.request.validator;

import static java.lang.annotation.ElementType.FIELD;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NotDuplicatedCategoryValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotDuplicated {

    String message() default "중복 없이 적어도 하나의 카테고리별 예산이 포함되어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

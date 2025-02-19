package umc.wegg.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.wegg.validation.validator.LoginUserValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LoginUserValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUser {

    String message() default "잘못된 user입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package umc.wegg.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.wegg.validation.validator.AddressExistValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddressExistValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistAddress {

    String message() default "잘못된 Address입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

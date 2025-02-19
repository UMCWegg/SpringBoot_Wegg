package umc.wegg.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.wegg.domain.apiPayload.code.status.ErrorStatus;
import umc.wegg.service.MapService.MapQueryService;
import umc.wegg.validation.annotation.ExistAddress;

@Component
@RequiredArgsConstructor
public class AddressExistValidator implements ConstraintValidator<ExistAddress, Long> {

    private final MapQueryService mapQueryService;

    @Override
    public void initialize(ExistAddress constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = mapQueryService.existsById(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus._NOT_FOUND.toString()).addConstraintViolation();
        }

        return isValid;

    }
}

package umc.wegg.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.wegg.config.security.AuthenticatedUser;
import umc.wegg.domain.apiPayload.code.status.ErrorStatus;
import umc.wegg.service.UserService.UserQueryService;
import umc.wegg.validation.annotation.ValidUser;

@Component
@RequiredArgsConstructor
public class LoginUserValidator implements ConstraintValidator<ValidUser, AuthenticatedUser> {

    private final UserQueryService userQueryService;

    @Override
    public void initialize(ValidUser constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(AuthenticatedUser authenticatedUser, ConstraintValidatorContext context) {
        // 유저 정보가 null이면 검증 실패
        if (authenticatedUser == null || authenticatedUser.getUserId() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus._UNAUTHORIZED.toString()).addConstraintViolation();
            return false;
        }

        // userId가 DB에 존재하는지 검증
        boolean exists = userQueryService.existsById(authenticatedUser.getUserId());

        if (!exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus._UNAUTHORIZED.toString()).addConstraintViolation();
        }

        return exists;
    }
}

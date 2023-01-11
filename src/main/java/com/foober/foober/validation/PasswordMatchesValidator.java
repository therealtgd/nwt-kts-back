package com.foober.foober.validation;

import com.foober.foober.dto.ClientRegistration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        if (obj instanceof ClientRegistration dto) {
            return passwordsMatch(dto.getPassword(), dto.getConfirmPassword());
        }
        return false;
    }

    private boolean passwordsMatch(String password, String confirmation) {
        if (password == null || confirmation == null)
            return false;
        return password.equals(confirmation);
    }
}
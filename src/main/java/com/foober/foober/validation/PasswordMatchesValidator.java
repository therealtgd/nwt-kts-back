package com.foober.foober.validation;

import com.foober.foober.dto.ClientSignUpRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, ClientSignUpRequest> {

	@Override
	public boolean isValid(final ClientSignUpRequest user, final ConstraintValidatorContext context) {
		return user.getPassword().equals(user.getConfirmPassword());
	}

}

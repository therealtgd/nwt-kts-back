package com.foober.foober.validation;

import com.foober.foober.dto.ClientSignUpRequest;
import com.foober.foober.dto.PasswordResetRequest;
import com.foober.foober.dto.PasswordUpdateRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

	@Override
	public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
		if (obj instanceof ClientSignUpRequest dto) {
			return passwordsMatch(dto.getPassword(), dto.getConfirmPassword());
		}
		else if (obj instanceof PasswordUpdateRequest dto) {
			return passwordsMatch(dto.getPassword(), dto.getConfirmPassword());
		}
		else if (obj instanceof PasswordResetRequest dto) {
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

package org.egov.echallan.service.validator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.stereotype.Component;

@Component
public class CustomBeanValidatorImpl implements CustomBeanValidator {

	ValidatorFactory valdiatorFactory = null;

	public CustomBeanValidatorImpl() {
		valdiatorFactory = Validation.buildDefaultValidatorFactory();
	}

	@Override
	public <T> void validateFields(T object) {
		Validator validator = valdiatorFactory.getValidator();
		Set<ConstraintViolation<T>> failedValidations = validator.validate(object);

		if (!failedValidations.isEmpty()) {
			List<String> allErrors = failedValidations.stream().map(failure -> failure.getMessageTemplate())
					.collect(Collectors.toList());
			throw new ValidationsFatalException(allErrors.toString(), new Exception());
		}
	}
}

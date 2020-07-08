package org.egov.echallan.service.validator;

public interface CustomBeanValidator {
	public <T> void validateFields(T object);
}

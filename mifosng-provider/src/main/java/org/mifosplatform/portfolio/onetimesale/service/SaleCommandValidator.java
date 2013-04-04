package org.mifosplatform.portfolio.onetimesale.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.portfolio.onetimesale.command.OneTimeSaleCommand;


public class SaleCommandValidator {

	private final OneTimeSaleCommand command;

	public SaleCommandValidator(final OneTimeSaleCommand command) {
		this.command=command;
	}


	public void validateForCreate() {
         List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("onetimesales");
		baseDataValidator.reset().parameter("quantity").value(command.getQuantity()).integerGreaterThanZero().notNull().notBlank();
		baseDataValidator.reset().parameter("itemId").value(command.getItemId()).notBlank().notNull();

		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.", dataValidationErrors);
		}
	}
}

package org.mifosplatform.portfolio.item.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.portfolio.item.command.ItemCommand;


public class ItemCommandValidator {

	private final ItemCommand command;

	public ItemCommandValidator(final ItemCommand command) {
		this.command=command;
	}


	public void validateForCreate() {
         List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("item");
		baseDataValidator.reset().parameter("chargeCode").value(command.getChargeCode()).notNull();
		baseDataValidator.reset().parameter("itemClass").value(command.getItemClass()).notBlank();
		baseDataValidator.reset().parameter("itemCode").value(command.getItemCode()).notNull().notBlank();
		baseDataValidator.reset().parameter("itemDescription").value(command.getItemDescription()).notBlank();
		baseDataValidator.reset().parameter("unitPrice").value(command.getUnitPrice()).notNull();
		baseDataValidator.reset().parameter("units").value(command.getUnits()).notBlank();
		baseDataValidator.reset().parameter("warranty").value(command.getWarranty()).notBlank();

		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.", dataValidationErrors);
		}
	}
}

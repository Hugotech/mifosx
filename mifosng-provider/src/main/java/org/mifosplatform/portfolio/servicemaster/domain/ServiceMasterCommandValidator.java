package org.mifosplatform.portfolio.servicemaster.domain;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.portfolio.servicemaster.commands.ServiceMasterCommand;

public class ServiceMasterCommandValidator {

		private final ServiceMasterCommand command;

		public ServiceMasterCommandValidator(final ServiceMasterCommand
				command) {
			this.command=command;
		}


		public void validateForCreate() {
	         List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("subscription");
			baseDataValidator.reset().parameter("serviceCode").value(command.getServiceCode()).notBlank().notNull().notExceedingLengthOf(10);
			//baseDataValidator.reset().parameter("subscription_type").value(command.getSubscription_type()).notBlank().notNull();
			baseDataValidator.reset().parameter("serviceDescription").value(command.getServiceDescription()).notBlank().notNull();
			baseDataValidator.reset().parameter("serviceType").value(command.getServiceType()).notBlank().notNull();

			if (!dataValidationErrors.isEmpty()) {
				throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.", dataValidationErrors);
			}
		}
	}

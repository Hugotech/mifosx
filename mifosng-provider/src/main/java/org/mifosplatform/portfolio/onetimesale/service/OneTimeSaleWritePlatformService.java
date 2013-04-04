package org.mifosplatform.portfolio.onetimesale.service;

import java.math.BigDecimal;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.onetimesale.command.OneTimeSaleCommand;

public interface OneTimeSaleWritePlatformService {

	CommandProcessingResult createOneTimeSale(OneTimeSaleCommand command,Long clientId);

	BigDecimal calculatePrice(Long itemId, Long quantity);

}

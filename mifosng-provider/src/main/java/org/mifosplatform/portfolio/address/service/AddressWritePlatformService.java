package org.mifosplatform.portfolio.address.service;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.address.command.AddressCommand;
import org.mifosplatform.portfolio.address.data.EntityTypecommand;

public interface AddressWritePlatformService {
	
	CommandProcessingResult createAddress(AddressCommand command);

	CommandProcessingResult updateAddress(Long addrId, AddressCommand command);

	CommandProcessingResult createNewRecord(EntityTypecommand command,String entityType);

}

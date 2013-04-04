package org.mifosplatform.portfolio.item.service;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.item.command.ItemCommand;

public interface ItemWritePltformService {

	CommandProcessingResult creatItem(ItemCommand command);

	CommandProcessingResult updateItem(ItemCommand command, Long itemId);

	CommandProcessingResult deleteItem(Long itemId);

}

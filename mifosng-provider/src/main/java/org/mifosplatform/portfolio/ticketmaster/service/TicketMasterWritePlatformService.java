package org.mifosplatform.portfolio.ticketmaster.service;

import java.io.InputStream;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;
import org.mifosplatform.portfolio.ticketmaster.command.TicketMasterCommand;

public interface TicketMasterWritePlatformService {

	CommandProcessingResult createTicketMaster(TicketMasterCommand command,
			Long clieniId);


	CommandProcessingResult upDateTicketDetails(
			TicketMasterCommand ticketMasterCommand,
			DocumentCommand documentCommand, Long ticketId,
			InputStream inputStream);

	void closeTicket(Long ticketId, TicketMasterCommand command);


	String retrieveTicketProblems(Long ticketId);
}

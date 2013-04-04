package org.mifosplatform.portfolio.ticketmaster.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.ticketmaster.data.ClientTicketData;
import org.mifosplatform.portfolio.ticketmaster.data.ProblemsData;
import org.mifosplatform.portfolio.ticketmaster.data.TicketMasterData;
import org.mifosplatform.portfolio.ticketmaster.data.UsersData;

public interface TicketMasterReadPlatformService {

	List<ProblemsData> retrieveProblemData();


	List<UsersData> retrieveUsers();

	List<TicketMasterData> retrieveClientTicketDetails(Long clientId);

	TicketMasterData retrieveSingleTicketDetails(Long ticketId, Long ticketId2);

	List<TicketMasterData> retrieveTicketStatusData();


	List<EnumOptionData> retrievePriorityData();


	List<TicketMasterData> retrieveTicketCloseStatusData();


	List<TicketMasterData> retrieveClientTicketHistory(Long ticketId);


	List<ClientTicketData> retrieveAssignedTickets();

	
	
}

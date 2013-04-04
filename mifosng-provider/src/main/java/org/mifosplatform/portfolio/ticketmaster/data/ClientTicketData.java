package org.mifosplatform.portfolio.ticketmaster.data;

import org.joda.time.LocalDate;

public class ClientTicketData {
	
	private final Long id;
    private final String priority;
    private final String status;
    private final Long userId;
    private final LocalDate ticketDate;
    private final String lastComment;
    private final String problemDescription;
    private final String userName;
    private final Long clientId;

	
	public ClientTicketData( Long id, String priority, String status, Long assignedTo, LocalDate ticketDate,
			String lastComment,String problemDescription,String userName, Long clientId) {
	this.id=id;
	this.priority=priority;
	this.status=status;
	this.userId=assignedTo;
	this.ticketDate=ticketDate;
	this.lastComment=lastComment;
	this.problemDescription=problemDescription;
	this.userName=userName;
	this.clientId=clientId;
	
}

}

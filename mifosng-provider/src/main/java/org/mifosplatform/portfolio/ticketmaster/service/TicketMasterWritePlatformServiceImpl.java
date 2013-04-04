package org.mifosplatform.portfolio.ticketmaster.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;
import org.mifosplatform.infrastructure.documentmanagement.exception.DocumentManagementException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.savingsdepositproduct.data.TicketDetailsRepository;
import org.mifosplatform.portfolio.savingsdepositproduct.data.TicketMasterRepository;
import org.mifosplatform.portfolio.ticketmaster.command.TicketMasterCommand;
import org.mifosplatform.portfolio.ticketmaster.domain.TicketDetail;
import org.mifosplatform.portfolio.ticketmaster.domain.TicketMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TicketMasterWritePlatformServiceImpl implements TicketMasterWritePlatformService{
	
	private PlatformSecurityContext context;
	private TicketMasterRepository repository;
	private TicketDetailsRepository ticketDetailsRepository;


	@Autowired
	public TicketMasterWritePlatformServiceImpl(final PlatformSecurityContext context,
			final TicketMasterRepository repository,final TicketDetailsRepository ticketDetailsRepository) {
		this.context = context;
		this.repository = repository;
		this.ticketDetailsRepository=ticketDetailsRepository;

	}

    @Transactional
	@Override
	public CommandProcessingResult createTicketMaster(
			TicketMasterCommand command,Long clieniId) {
		
    	try {


			this.context.authenticatedUser();

			TicketMasterCommandValidator validator=new TicketMasterCommandValidator(command);
			validator.validateForCreate();
			
			TicketMaster ticketMaster=new TicketMaster(clieniId,command.getPriority(),command.getTicketDate(),
					command.getProblemCode(),command.getDescription(),command.getStatus(),command.getResolutionDescription(),
					command.getAssignedTo());

                   this.repository.save(ticketMaster);
			return new CommandProcessingResult(Long.valueOf(ticketMaster.getId()));

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
    	
	}

	private void handleDataIntegrityIssues(TicketMasterCommand command,
			DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CommandProcessingResult upDateTicketDetails(
			TicketMasterCommand ticketMasterCommand,
			DocumentCommand documentCommand, Long ticketId,InputStream inputStream) {
		
	 	try {
		 String fileUploadLocation = FileUtils.generateFileParentDirectory(documentCommand.getParentEntityType(),
                 documentCommand.getParentEntityId());

         /** Recursively create the directory if it does not exist **/
         if (!new File(fileUploadLocation).isDirectory()) {
             new File(fileUploadLocation).mkdirs();
         }
         String fileLocation=null;
         if(documentCommand.getFileName()!=null){
          fileLocation = FileUtils.saveToFileSystem(inputStream, fileUploadLocation, documentCommand.getFileName());
         }
         TicketDetail detail=new TicketDetail(ticketId,ticketMasterCommand.getComments(),fileLocation,ticketMasterCommand.getAssignedTo());
         
         this.ticketDetailsRepository.save(detail);
         return new CommandProcessingResult(detail.getId());

	 	}
catch (DataIntegrityViolationException dve) {
		handleDataIntegrityIssues(ticketMasterCommand, dve);
		return new CommandProcessingResult(Long.valueOf(-1));
	
		
		
		
		
		
	} catch (IOException e) {
         throw new DocumentManagementException(documentCommand.getName());
}
		

	
	
	}

	@Override
	public void closeTicket(Long ticketId, TicketMasterCommand command) {
		try {


			this.context.authenticatedUser();

		//	TicketDetail ticketDetail=this.ticketDetailsRepository.findOne(ticketId);
			TicketMaster ticketMaster=this.repository.findOne(ticketId);
			
			ticketMaster.update(command);
			this.repository.save(ticketMaster);
			
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
				}
		
	}

	@Override
	public String retrieveTicketProblems(Long ticketId) {
		try {
		TicketMaster master=this.repository.findOne(ticketId);
		String description=master.getDescription();
		return description;
		
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(null, dve);
			return "";
				}
	}
}


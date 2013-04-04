package org.mifosplatform.portfolio.servicemaster.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositAccountCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.service.DepositAccountWritePlatformServiceJpaRepositoryImpl;
import org.mifosplatform.portfolio.servicemaster.commands.ServiceMasterCommand;
import org.mifosplatform.portfolio.servicemaster.domain.ServiceMaster;
import org.mifosplatform.portfolio.servicemaster.domain.ServiceMasterCommandValidator;
import org.mifosplatform.portfolio.servicemaster.domain.ServiceMasterRepository;
import org.mifosplatform.portfolio.servicemaster.exceptions.ServiceCodeExist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ServiceMasterWritePlatformServiceImpl  implements ServiceMasterWritePlatformService{
	 private final static Logger logger = LoggerFactory.getLogger(ServiceMasterWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final ServiceMasterRepository serviceMasterRepository;

@Autowired
 public ServiceMasterWritePlatformServiceImpl(final PlatformSecurityContext context,final ServiceMasterRepository serviceMasterRepository)
{
	this.context=context;
	this.serviceMasterRepository=serviceMasterRepository;
}
    @Transactional
	@Override
	public CommandProcessingResult createNewService(ServiceMasterCommand command) {
		// TODO Auto-generated method stub
		try {
			context.authenticatedUser();

			this.context.authenticatedUser();
			ServiceMasterCommandValidator validator=new ServiceMasterCommandValidator(command);
			validator.validateForCreate();

	
			ServiceMaster serviceMaster = ServiceMaster.create(command.getServiceCode(),command.getServiceDescription(),command.getServiceType());

			this.serviceMasterRepository.save(serviceMaster);

			return new CommandProcessingResult(serviceMaster.getId());

		} catch (DataIntegrityViolationException dve) {
			   handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
    
    private void handleDataIntegrityIssues(final ServiceMasterCommand command, final DataIntegrityViolationException dve) {

        Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("service_code_key")) { throw new PlatformDataIntegrityException(
                "error.msg.service.duplicate.code", "service with code " + command.getServiceCode()
                        + " already exists", "serviceCode", command.getServiceCode()); }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.service.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
    
	@Override
	public CommandProcessingResult updateService(ServiceMasterCommand command,Long id) {
		try
		{
		ServiceMaster master=this.serviceMasterRepository.findOne(id);

		master.update(command);
		this.serviceMasterRepository.save(master);


		return new CommandProcessingResult(master.getId());
	} catch (DataIntegrityViolationException dve) {
		return new CommandProcessingResult(Long.valueOf(-1));
	}
	}
	@Override
	public void deleteService(Long serviceId) {
		ServiceMaster master=this.serviceMasterRepository.findOne(serviceId);
		master.delete();
		this.serviceMasterRepository.save(master);

	}



}

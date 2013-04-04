package org.mifosplatform.portfolio.address.service;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.address.command.AddressCommand;
import org.mifosplatform.portfolio.address.domain.Address;
import org.mifosplatform.portfolio.address.domain.AddressRepository;
import org.mifosplatform.portfolio.savingsdepositaccount.exception.DepositAccountNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public class AddressWritePlatformServiceImpl implements AddressWritePlatformService {
	  private final static Logger logger = LoggerFactory.getLogger(AddressWritePlatformServiceImpl.class);
	private PlatformSecurityContext context;
	private AddressRepository addressRepository;


	@Autowired
	public AddressWritePlatformServiceImpl(final PlatformSecurityContext context,
			final AddressRepository addressRepository) {
		this.context = context;
		this.addressRepository = addressRepository;
		

	}

	@Override
	public CommandProcessingResult createAddress(AddressCommand command) {

		try {


			this.context.authenticatedUser();

		
		
			Address data = new Address(command.getClientId(),command.getAddressKey(),command.getAddressNo(),command.getStreet(),
					command.getCity(),command.getState(),command.getCountry(),command.getZip());
	    
         this.addressRepository.save(data);


			return new CommandProcessingResult(Long.valueOf(-1));

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private void handleDataIntegrityIssues(AddressCommand command,
			DataIntegrityViolationException dve) {
		  logger.error(dve.getMessage(), dve);
		
	}

	@Override
	public CommandProcessingResult updateAddress(Long addrId,
			AddressCommand command) {
		try {

		this.context.authenticatedUser();
		Address address=this.addressRepository.findOne(addrId);
		if(address == null){
			
			throw new DepositAccountNotFoundException(addrId);
		}
		address.update(command);
		
		this.addressRepository.save(address);
		
		return new CommandProcessingResult(addrId);
		
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}
}


package org.mifosplatform.portfolio.onetimesale.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.itemmaster.domain.ItemMaster;
import org.mifosplatform.portfolio.itemmaster.domain.ItemMasterRepository;
import org.mifosplatform.portfolio.onetimesale.command.OneTimeSaleCommand;
import org.mifosplatform.portfolio.onetimesale.domain.OneTimeSale;
import org.mifosplatform.portfolio.onetimesale.domain.OneTimeSaleRepository;
import org.mifosplatform.portfolio.plan.service.PlanCommandValidator;
import org.mifosplatform.portfolio.savingsdepositaccount.exception.DepositAccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OneTimeSaleWritePlatformServiceImpl implements OneTimeSaleWritePlatformService{
	
	private PlatformSecurityContext context;
	private OneTimeSaleRepository  oneTimeSaleRepository;
	private ItemMasterRepository itemMasterRepository;
	@Autowired
	public OneTimeSaleWritePlatformServiceImpl(final PlatformSecurityContext context,final OneTimeSaleRepository oneTimeSaleRepository,
			final ItemMasterRepository itemMasterRepository)
	{
		this.context=context;
		this.oneTimeSaleRepository=oneTimeSaleRepository;
		this.itemMasterRepository=itemMasterRepository;
	}

	 @Transactional
	@Override
	public CommandProcessingResult createOneTimeSale(
			OneTimeSaleCommand command, Long clientId) {
		try{
			
			this.context.authenticatedUser();
		SaleCommandValidator validator=new SaleCommandValidator(command);
			validator.validateForCreate();
			ItemMaster itemMaster=this.itemMasterRepository.findOne(command.getItemId());
			OneTimeSale oneTimeSale=new OneTimeSale(clientId,command.getItemId(),itemMaster.getUnits(),command.getQuantity(),command.getChargeCode(),
					command.getUnitPrice(),command.getTotalPrice());
			
			this.oneTimeSaleRepository.save(oneTimeSale);
			return new CommandProcessingResult(Long.valueOf(oneTimeSale.getId()));
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private void handleDataIntegrityIssues(OneTimeSaleCommand command,
			DataIntegrityViolationException dve) {
		
		
	}

	@Override
	public BigDecimal calculatePrice(Long itemId,Long quantity) {
try{
			
			this.context.authenticatedUser();
			  List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			  DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("onetimesales");
				baseDataValidator.reset().parameter("quantity").value(quantity).notNull().inValidValue(null, null);
			
			
			ItemMaster itemMaster=this.itemMasterRepository.findOne(itemId);
			if(itemMaster == null)
			{
				throw new DepositAccountNotFoundException(itemId);
			}
			
			BigDecimal TotalPrice=itemMaster.getUnitPrice().multiply(new BigDecimal(quantity));
			
			
			
			
			
			return TotalPrice;
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(null, dve);
			return null;
		}
	}
	 

}

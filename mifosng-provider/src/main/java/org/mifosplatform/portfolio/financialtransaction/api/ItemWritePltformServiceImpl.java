package org.mifosplatform.portfolio.financialtransaction.api;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.item.command.ItemCommand;
import org.mifosplatform.portfolio.item.domain.ItemRepository;
import org.mifosplatform.portfolio.item.exception.ItemNotFoundException;
import org.mifosplatform.portfolio.item.service.ItemCommandValidator;
import org.mifosplatform.portfolio.item.service.ItemWritePltformService;
import org.mifosplatform.portfolio.itemmaster.domain.ItemMaster;
import org.mifosplatform.portfolio.plan.exceptions.PlanNotFundException;
import org.mifosplatform.portfolio.savingsdepositaccount.service.DepositAccountWritePlatformServiceJpaRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemWritePltformServiceImpl implements ItemWritePltformService{
	  private final static Logger logger = LoggerFactory.getLogger(DepositAccountWritePlatformServiceJpaRepositoryImpl.class);

 private final PlatformSecurityContext context;
 private final ItemRepository itemRepository;
 
 @Autowired
 public ItemWritePltformServiceImpl(final PlatformSecurityContext context,
		 final ItemRepository itemrepository){
	 this.context=context;
	 this.itemRepository=itemrepository;
 }
	
    @Transactional
	@Override
	public CommandProcessingResult creatItem(ItemCommand command) {
  try
  {
	  
	  this.context.authenticatedUser();
	  ItemCommandValidator validator=new ItemCommandValidator(command);
	  validator.validateForCreate();
	  ItemMaster itemMaster=new ItemMaster(command.getItemCode(),command.getItemDescription(),command.getItemClass(),
			  command.getUnitPrice(),command.getUnits(),command.getWarranty(),command.getChargeCode()); 
	  
	  this.itemRepository.save(itemMaster);
	  return new CommandProcessingResult(itemMaster.getId());
  }catch (DataIntegrityViolationException dve) {
      handleDataIntegrityIssues(command, dve);
      return new CommandProcessingResult(Long.valueOf(-1));
  }

    	
    	
    
	}

	private void handleDataIntegrityIssues(ItemCommand command,
			DataIntegrityViolationException dve) {
	    logger.error(dve.getMessage(), dve);
		
	}

	@Override
	public CommandProcessingResult updateItem(ItemCommand command, Long itemId) {
   try{
	   this.context.authenticatedUser();
	   ItemMaster itemMaster=this.itemRepository.findOne(itemId);
	   if(itemMaster == null){
		   throw new ItemNotFoundException();
	   }
	    itemMaster.update(command);   
      this.itemRepository.save(itemMaster);
		
		return new CommandProcessingResult(itemId);
		
	}catch (DataIntegrityViolationException dve) {
	      handleDataIntegrityIssues(command, dve);
	      return new CommandProcessingResult(Long.valueOf(-1));
	  }

}

	@Override
	public CommandProcessingResult deleteItem(Long itemId) {
		try{
			this.context.authenticatedUser();
			ItemMaster itemMaster=this.itemRepository.findOne(itemId);
			if(itemMaster == null){
				throw new ItemNotFoundException();
			}
			itemMaster.delete();
			this.itemRepository.save(itemMaster);
			return new CommandProcessingResult(itemId);
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}
}

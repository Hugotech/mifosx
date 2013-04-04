package org.mifosplatform.portfolio.pricing.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.pricing.commands.PricingCommand;
import org.mifosplatform.portfolio.pricing.domain.Price;
import org.mifosplatform.portfolio.pricing.domain.PriceRepository;
import org.mifosplatform.portfolio.pricing.domain.PricingCommandValidator;
import org.mifosplatform.portfolio.pricing.exceptions.ChargeCOdeExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PriceWritePlatformServiceImpl implements PriceWritePlatformService {

	private PlatformSecurityContext context;

	private PriceRepository priceRepository;

	private PriceReadPlatformService priceReadPlatformService;

	@Autowired
	public PriceWritePlatformServiceImpl(final PlatformSecurityContext context,
			final PriceRepository priceRepository) {
		this.context = context;
		this.priceRepository = priceRepository;

	}

	@Transactional
	@Override
	public CommandProcessingResult createPricing(PricingCommand command,
			List<ServiceData> serviceData, Long planId) {
		try {
		//	this.context.authenticatedUser().validateHasReadPermission("price");
					this.context.authenticatedUser();
			PricingCommandValidator validator=new PricingCommandValidator(command);
			validator.validateForCreate();

			for (ServiceData data : serviceData) {

				if (data.getChargeCode() != null) {
					if ((data.getPlanId() == planId))
						if(data.getServiceCode().equalsIgnoreCase(
									command.getServiceCode())){
									if(data.getChargeCode().equalsIgnoreCase(
									command.getChargeCode())) {

						throw new ChargeCOdeExists(data.getChargeDescription());
					}
				}
			}
			}
			Price price = new Price(planId, command.getChargeCode(),
					command.getServiceCode(), command.getChargingVariant(),
					command.getPrice(), command.getdiscountId());
			this.priceRepository.save(price);

			return new CommandProcessingResult(price.getId());
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));

		}

	}

	private void handleDataIntegrityIssues(PricingCommand command,
			DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub

	}

	@Override
	public CommandProcessingResult updatePrice(PricingCommand command,Long priceId) {
	try
	{

		this.context.authenticatedUser();
		PricingCommandValidator validator=new PricingCommandValidator(command);
		validator.validateForCreate();
	Price price=this.priceRepository.findOne(priceId);
	if(price!= null)
	    price.update(command);
	    this.priceRepository.save(price);
	    return new CommandProcessingResult(price.getId());
	} catch (DataIntegrityViolationException dve) {
		handleDataIntegrityIssues(command, dve);
		return new CommandProcessingResult(Long.valueOf(-1));

	}
	}

	 public void deletePrice(Long priceId) {
		  try {
		 Price price=this.priceRepository.findOne(priceId);
		 if(price!= null){
		  price.delete();
		 }
		     this.priceRepository.save(price);
		  } catch (DataIntegrityViolationException dve) {
		  }
	 }
}

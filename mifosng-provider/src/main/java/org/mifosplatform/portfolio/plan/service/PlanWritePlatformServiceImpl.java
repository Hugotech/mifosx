package org.mifosplatform.portfolio.plan.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.commands.PlansCommand;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.plan.domain.ServiceDescription;
import org.mifosplatform.portfolio.plan.domain.ServiceDescriptionRepository;
import org.mifosplatform.portfolio.plan.domain.ServiceDetails;
import org.mifosplatform.portfolio.plan.domain.ServiceRepository;
import org.mifosplatform.portfolio.plan.exceptions.PlanCodeExist;
import org.mifosplatform.portfolio.servicemaster.exceptions.ServiceCodeExist;
import org.mifosplatform.portfolio.servicemaster.service.ServiceMasterWritePlatformServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PlanWritePlatformServiceImpl implements PlanWritePlatformService {
	 private final static Logger logger = LoggerFactory.getLogger(PlanWritePlatformServiceImpl.class);
	private PlatformSecurityContext context;
	private PlanRepository planRepository;
	private ServiceDescriptionRepository serviceDescriptionRepository;
	private ServiceRepository serviceRepository;


	@Autowired
	public PlanWritePlatformServiceImpl(final PlatformSecurityContext context,
			final PlanRepository planRepository,
			final ServiceDescriptionRepository serviceDescriptionRepository,
			final ServiceRepository serviceRepository) {
		this.context = context;
		this.planRepository = planRepository;
		this.serviceDescriptionRepository = serviceDescriptionRepository;
		this.serviceRepository=serviceRepository;

	}

	@Override
	public CommandProcessingResult createPlan(PlansCommand command) {

		try {


			this.context.authenticatedUser();

			PlanCommandValidator validator=new PlanCommandValidator(command);
			validator.validateForCreate();
		
			
			@SuppressWarnings("unchecked")
			String[] services=command.getServices();
			 List<ServiceDetails> serviceDetails = new ArrayList<ServiceDetails>();
			Plan data = new Plan(command.getplanCode(),
					command.getplanDescription(), command.getStartDate(),
					command.getEndDate(), command.getBillRule(),command.getStatus(),command.getContractPeriod(),serviceDetails);
			 for (String clientId : services) {
	                final Long id = Long.valueOf(clientId);
	                ServiceDescription service1 = this.serviceDescriptionRepository.findOne(id);
	                ServiceDetails detail=new ServiceDetails(service1.getService_code());
	             //  serviceDetails.add(detail);
	               data.addServieDetails(detail);
			  }
         this.planRepository.save(data);


			return new CommandProcessingResult(Long.valueOf(-1));

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private void handleDataIntegrityIssues(PlansCommand command,
			DataIntegrityViolationException dve) {
		 Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("plan_code_key")) { throw new PlatformDataIntegrityException(
	                "error.msg.plan.duplicate.code", "plan with code " + command.getplanCode()
	                        + " already exists", "planCode", command.getplanCode()); }

	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.deposit.account.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource.");

	}
	 @Transactional
	@Override
	public CommandProcessingResult updatePlan(PlansCommand command,
			PlanData serviceData, List<ServiceData> services,Long planId) {

		try

		{

			PlanCommandValidator validator=new PlanCommandValidator(command);
			validator.validateForCreate();
		Plan plan=this.planRepository.findOne(planId);

		// ServiceDetails serviceDetails = new ArrayList<ServiceDetails>();

			for(ServiceData data:services)
			{

				 ServiceDetails details=new ServiceDetails(data.getServiceCode());
				 plan.addServieDetails(details);

				if(command.isServicesChanged())
				{
					plan.getDetails().clear();
					String[] service=command.getServices();
					 for (String clientId : service) {
			                final Long id = Long.valueOf(clientId);
			                ServiceDescription service1 = this.serviceDescriptionRepository.findOne(id);
			                ServiceDetails detail=new ServiceDetails(service1.getService_code());
			             //  serviceDetails.add(detail);
			               plan.addServieDetails(detail);
					  }
				}
					 else
					 {
					ServiceDetails serviceDetails=new ServiceDetails(data.getServiceCode());
					  plan.addServieDetails(serviceDetails);
				}


			}


		plan.update(command, serviceData,services);
		 this.planRepository.save(plan);
			return new CommandProcessingResult(Long.valueOf(serviceData.getId()));
	} catch (DataIntegrityViolationException dve) {
		 handleDataIntegrityIssues(command, dve);
		 return new CommandProcessingResult(Long.valueOf(-1));
	}
	}

 @Transactional
@Override
public void deletePlan(Long planCode, PlanData serviceData,
		List<ServiceData> services) {
	 /*if(serviceData==null){
			throw new DepositProductNotFoundException(planCode);
		}*/


//	 Plan plan=new Plan(serviceData.getPlan_code(),serviceData.getPlan_description(),
//			 serviceData.getStartDate(),serviceData.getEndDate(),serviceData.getBillRule(),
//			 serviceData.getStatus(),serviceData.isBillingcycle(),
//			 "contract",serviceDetails);

	 Plan plan=this.planRepository.findOne(planCode);
 for(ServiceData data:services)
		{

			 ServiceDetails details=new ServiceDetails(data.getPlanCode());
			 plan.addServieDetails(details);
			 details.delete();

		}




	 plan.delete(serviceData);



	 this.planRepository.save(plan);

}



}

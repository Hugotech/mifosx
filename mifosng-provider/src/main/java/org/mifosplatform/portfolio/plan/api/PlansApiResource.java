package org.mifosplatform.portfolio.plan.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.billingorder.data.BillRuleData;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiDataBillingConversionService;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiJsonBillingSerializerService;
import org.mifosplatform.portfolio.plan.commands.PlansCommand;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.portfolio.plan.service.PlanWritePlatformService;
import org.mifosplatform.portfolio.pricing.service.PriceReadPlatformService;
import org.mifosplatform.portfolio.subscription.data.SubscriptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/plans")
@Component
@Scope("singleton")
public class PlansApiResource {

	@Autowired
	private PortfolioApiDataBillingConversionService apiDataConversionService;

	@Autowired
	private PortfolioApiJsonBillingSerializerService apiJsonSerializerService;

	@Autowired
	private PlanWritePlatformService planWritePlatformService;

	@Autowired
	private PlanReadPlatformService planReadPlatformService;

	@Autowired
	private PriceReadPlatformService priceReadPlatformService;

	@Autowired
	private PlanRepository planRepository;

	@Autowired
	private PlatformSecurityContext context;

	private final String entityType = "PLAN";

	private static final Set<String> typicalResponseParameters = new HashSet<String>(
			Arrays.asList("id", "status", "planCode", "planDescription",
					"charge_code", "serviceCode", "serviceDescription",
					"endDate", "billRule"));

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createPlan(final String jsonRequestBody) {

		PlansCommand command = this.apiDataConversionService
				.convertJsonToPlansCommand(null, jsonRequestBody);

		CommandProcessingResult userId = this.planWritePlatformService.createPlan(command);
		return Response.ok().entity(userId).build();
	}

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveClientAccount(
			@QueryParam("plan_code") final String planCode,
			@Context final UriInfo uriInfo) {


		final Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());

		List<ServiceData> data = this.planReadPlatformService
				.retrieveAllServices();
		List<BillRuleData> billData = this.planReadPlatformService
				.retrievebillRules();
		List<SubscriptionData> contractPeriod = this.planReadPlatformService
				.retrieveSubscriptionData();
		List<EnumOptionData> status = this.planReadPlatformService
				.retrieveNewStatus();

		PlanData planData = new PlanData(data, billData, contractPeriod, status);

		return this.apiJsonSerializerService.serializePlanDataToJson(
				prettyPrint, responseParameters, planData);
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllSubscription(@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(entityType);

		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("id", "planCode", "service_description",
						"status", "startDate", "endDate", "bill_rule",
						"planstatus"));

		Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());

		List<PlanData> products = this.planReadPlatformService
				.retrievePlanData();
		return this.apiJsonSerializerService.serializePlanDataToJson(
				prettyPrint, responseParameters, products);
	}

	@GET
	@Path("{planId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePlanDetails(
			@PathParam("planId") final Long planId,
			@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(entityType);

		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("id", "planCode", "startDate", "endDate",
						"status", "plan_description", "billiingcycle",
						"contractPeriod", "servicedata", "service_code",
						"selectedservice", "services", "statusname",
						"planstatus"));

		Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());

		boolean template = ApiParameterHelper.template(uriInfo
				.getQueryParameters());

		PlanData singlePlandata = this.planReadPlatformService
				.retrievePlanData(planId);





		responseParameters.addAll(Arrays.asList("servicedata"));

		List<ServiceData> data = this.planReadPlatformService
				.retrieveAllServices();
		List<BillRuleData> billData = this.planReadPlatformService
				.retrievebillRules();
		List<SubscriptionData> contractPeriod = this.planReadPlatformService
				.retrieveSubscriptionData();
		List<EnumOptionData> status = this.planReadPlatformService
				.retrieveNewStatus();
		if (template) {
			List<ServiceData> services = this.priceReadPlatformService
					.retrievePrcingDetails(planId);
			List<ServiceData> data1 = new ArrayList<ServiceData>();

			int size = data.size();
			int selectedsize = services.size();

			for (int i = 0; i < selectedsize; i++)

			{

				Long selected = services.get(i).getId();

				for (int j = 0; j < size; j++) {
					Long avialble = data.get(j).getId();

					if (selected == avialble) {
						data.remove(j);
						size--;
					}

				}

			}

			// services.remove(data);
			singlePlandata = new PlanData(data, billData, contractPeriod,
					status, singlePlandata, services);

		}

		return this.apiJsonSerializerService.serializePlanDataToJson(
				prettyPrint, responseParameters, singlePlandata);
	}

	@PUT
	@Path("{planCode}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updatePlan(@PathParam("planCode") final Long planCode,
			final String jsonRequestBody) {

		PlansCommand command = this.apiDataConversionService
				.convertJsonToPlansCommand(null, jsonRequestBody);
		PlanData serviceData = this.planReadPlatformService
				.retrievePlanData(planCode);
		List<ServiceData> services = this.priceReadPlatformService
				.retrievePrcingDetails(planCode);
		CommandProcessingResult entityIdentifier = this.planWritePlatformService
				.updatePlan(command, serviceData, services,planCode);
		return Response.ok().entity(entityIdentifier).build();
	}

	@DELETE
	@Path("{planCode}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteSubscription(
			@PathParam("planCode") final Long planCode) {
		List<ServiceData> services = this.priceReadPlatformService
				.retrievePrcingDetails(planCode);

		PlanData serviceData = this.planReadPlatformService
				.retrievePlanData(planCode);

		this.planWritePlatformService.deletePlan(planCode, serviceData,
				services);

		return Response.ok().build();
	}

}
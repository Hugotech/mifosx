package org.mifosplatform.portfolio.servicemaster.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.infrastructure.configuration.data.PeriodData;
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiDataBillingConversionService;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiJsonBillingSerializerService;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.servicemaster.commands.ServiceMasterCommand;
import org.mifosplatform.portfolio.servicemaster.data.SericeMasterOptionsData;
import org.mifosplatform.portfolio.servicemaster.service.ServiceMasterReadPlatformService;
import org.mifosplatform.portfolio.servicemaster.service.ServiceMasterWritePlatformService;
import org.mifosplatform.portfolio.services.data.ServiceMasterData;
import org.mifosplatform.portfolio.subscription.commands.SubscriptionCommand;
import org.mifosplatform.portfolio.subscription.data.SubscriptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/servicemasters")
@Component
@Scope("singleton")
public class ServiceMasterApiResource {

	@Autowired
	private ServiceMasterWritePlatformService serviceMasterWritePlatformService;
	@Autowired
	private PortfolioApiDataBillingConversionService apiDataConversionService;
	@Autowired
	private PortfolioApiJsonBillingSerializerService apiJsonSerializerService;
	@Autowired
	private ServiceMasterReadPlatformService serviceMasterReadPlatformService;

	@Autowired
	private PlatformSecurityContext context;

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createService(final String jsonRequestBody) {

		final ServiceMasterCommand command = this.apiDataConversionService.convertJsonToServiceMasterCommand(null, jsonRequestBody);
		CommandProcessingResult id=serviceMasterWritePlatformService.createNewService(command);
		 return Response.ok().entity(id).build();
	}

    @GET
    @Path("template")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveTempleteInfo(@Context final UriInfo uriInfo) {
	Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList(""));
		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
	final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
        Collection<SericeMasterOptionsData> optionsData=new ArrayList<SericeMasterOptionsData>();
		return this.apiJsonSerializerService.serializeServiceMasterDataToJson(prettyPrint, responseParameters, optionsData);
    }

    @GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllService(@Context final UriInfo uriInfo) {



		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("id","serviceCode","serviceDescription" ));

		Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());

		List<SericeMasterOptionsData> services = this.serviceMasterReadPlatformService
				.retrieveServices();
		return this.apiJsonSerializerService.serializeServiceMasterDataToJson(
				prettyPrint, responseParameters, services);
	}

	@GET
	@Path("{serviceId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveSingleServiceDetails(@PathParam("serviceId") final Long serviceId, @Context final UriInfo uriInfo) {


		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("id","serviceCode","serviceDescription","serviceType" ));

		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

		SericeMasterOptionsData productData = this.serviceMasterReadPlatformService.retrieveIndividualService(serviceId);

		boolean template = ApiParameterHelper.template(uriInfo.getQueryParameters());
		if (template) {


		}

		return this.apiJsonSerializerService.serializeServiceDataToJson(prettyPrint, responseParameters, productData);
	}

	@PUT
	@Path("{serviceId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response updateService(@PathParam("serviceId") final Long serviceId, final String jsonRequestBody){

		final ServiceMasterCommand command = this.apiDataConversionService.convertJsonToServiceMasterCommand(null, jsonRequestBody);
		CommandProcessingResult entityIdentifier=this.serviceMasterWritePlatformService.updateService(command,serviceId);
		return Response.ok().entity(entityIdentifier).build();
	}

	@DELETE
	@Path("{serviceId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteSubscription(@PathParam("serviceId") final Long serviceId) {

		this.serviceMasterWritePlatformService.deleteService(serviceId);

		return Response.ok(serviceId).build();
	}

}

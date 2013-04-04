package org.mifosplatform.portfolio.ticketmaster.api;

import java.io.File;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiDataBillingConversionService;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiJsonBillingSerializerService;
import org.mifosplatform.portfolio.billmaster.domain.BillMaster;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.pricing.service.PriceReadPlatformService;
import org.mifosplatform.portfolio.savingsdepositproduct.data.TicketDetailsRepository;
import org.mifosplatform.portfolio.ticketmaster.command.TicketMasterCommand;
import org.mifosplatform.portfolio.ticketmaster.data.ClientTicketData;
import org.mifosplatform.portfolio.ticketmaster.data.ProblemsData;
import org.mifosplatform.portfolio.ticketmaster.data.TicketMasterData;
import org.mifosplatform.portfolio.ticketmaster.data.UsersData;
import org.mifosplatform.portfolio.ticketmaster.domain.TicketDetail;
import org.mifosplatform.portfolio.ticketmaster.service.TicketMasterReadPlatformService;
import org.mifosplatform.portfolio.ticketmaster.service.TicketMasterWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/ticketmasters")
@Component
@Scope("singleton")
public class TicketMasterApiResource {

		@Autowired
		private PortfolioApiDataBillingConversionService apiDataConversionService;

		@Autowired
		private PortfolioApiJsonBillingSerializerService apiJsonSerializerService;

		@Autowired
		private TicketMasterWritePlatformService ticketMasterWritePlatformService;

		@Autowired
		private TicketMasterReadPlatformService ticketMasterReadPlatformService ;

		@Autowired
		private PriceReadPlatformService priceReadPlatformService;

		@Autowired
		private PlanRepository planRepository;

		@Autowired
		private TicketDetailsRepository detailsRepository;

		
		@Autowired
		private PlatformSecurityContext context;

		private final String entityType = "TICKETMASTER";

		

		@POST
        @Path("{clientId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public Response createTicketMaster(@PathParam("clientId") final Long clientId,final String jsonRequestBody) {

			TicketMasterCommand command = this.apiDataConversionService
					.convertJsonToTicketMasterCommand(null, jsonRequestBody);
			CommandProcessingResult userId = this.ticketMasterWritePlatformService.createTicketMaster(command,clientId);
			return Response.ok().entity(userId).build();
		}

		@GET
		@Path("template")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveTicketMasterTemplateData(@Context final UriInfo uriInfo) {

			// context.authenticatedUser().validateHasReadPermission("CLIENT");

			context.authenticatedUser().validateHasReadPermission(entityType);

			Set<String> typicalResponseParameters = new HashSet<String>(
					Arrays.asList("statusType"));

			Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
			if (responseParameters.isEmpty()) {
				responseParameters.addAll(typicalResponseParameters);
			}
			boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

			responseParameters.addAll(Arrays.asList("id","priorityType","problemsDatas","usersData","status","assignedTo","userName","ticketDate","lastComment"));
			TicketMasterData templateData = handleTemplateRelatedData(responseParameters,null);

			return this.apiJsonSerializerService.serializeTicketMasterToJson(prettyPrint, responseParameters, templateData);
		}


		private TicketMasterData handleTemplateRelatedData(final Set<String> responseParameters,TicketMasterData singleTicketData) {
           
			List<EnumOptionData> priorityData = this.ticketMasterReadPlatformService.retrievePriorityData();
			 List<TicketMasterData> closedStatusdata = this.ticketMasterReadPlatformService.retrieveTicketCloseStatusData();
	     
			List<ProblemsData> datas=this.ticketMasterReadPlatformService.retrieveProblemData();
			List<UsersData>  userData=this.ticketMasterReadPlatformService.retrieveUsers();
			singleTicketData= new TicketMasterData(closedStatusdata,datas,userData,singleTicketData,priorityData);
			return  singleTicketData;

	}
		
		    @GET
		    @Path("{clientId}")
		    @Consumes({MediaType.APPLICATION_JSON})
		    @Produces({MediaType.APPLICATION_JSON})
		    public String retrieveSingleClientTicketDetails(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {


		        final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		        final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

		        final List<TicketMasterData> data = this.ticketMasterReadPlatformService.retrieveClientTicketDetails(clientId);

		        return this.apiJsonSerializerService.serializeTicketMasterToJson(prettyPrint, responseParameters,
		                data);
		    }
		   
		    @GET
		    @Path("{clientId}/update/{ticketId}")
		    @Consumes({MediaType.APPLICATION_JSON})
		    @Produces({MediaType.APPLICATION_JSON})
		    public String retrieveClientSingleTicketDetails(@PathParam("clientId") final Long clientId,@PathParam("ticketId") final Long ticketId, @Context final UriInfo uriInfo) {


		        final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		        final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

		         TicketMasterData data = this.ticketMasterReadPlatformService.retrieveSingleTicketDetails(clientId,ticketId);
		        
		         data = handleTemplateRelatedData(responseParameters,data);
		         List<TicketMasterData> Statusdata = this.ticketMasterReadPlatformService.retrieveTicketStatusData();
		         data.setStatusData(Statusdata);
		        return this.apiJsonSerializerService.serializeTicketMasterToJson(prettyPrint, responseParameters,
		                data);
		    }
		   
			@PUT
			@Path("{ticketId}")
			@Consumes({ MediaType.APPLICATION_JSON })
			@Produces({ MediaType.APPLICATION_JSON })
			public Response closeTicket(
					@PathParam("ticketId") final Long ticketId,final String jsonRequestBody) {

				TicketMasterCommand command = this.apiDataConversionService
						.convertJsonToTicketMasterCommand(null, jsonRequestBody);

				this.ticketMasterWritePlatformService.closeTicket(ticketId,command);

				return Response.ok().build();
			}
			
			@GET
		    @Path("{ticketId}/history")
		    @Consumes({MediaType.APPLICATION_JSON})
		    @Produces({MediaType.APPLICATION_JSON})
		    public String ticketHistory(@PathParam("ticketId") final Long ticketId, @Context final UriInfo uriInfo) {


		        final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		        final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
		         
		        String description=this.ticketMasterWritePlatformService.retrieveTicketProblems(ticketId);
		         final List<TicketMasterData> data = this.ticketMasterReadPlatformService.retrieveClientTicketHistory(ticketId);
		        
		        TicketMasterData masterData=new TicketMasterData(description,data);
                 
		        return this.apiJsonSerializerService.serializeTicketMasterToJson(prettyPrint, responseParameters,
		        		masterData);
		    }
			
			@GET
			@Path("{ticketId}/print")
			@Consumes({ MediaType.APPLICATION_JSON })
			@Produces({ MediaType.APPLICATION_JSON })
			public Response downloadFile(@PathParam("ticketId") final Long ticketId) {

				TicketDetail ticketDetail = this.detailsRepository.findOne(ticketId);

				String printFileName = ticketDetail.getAttachments();

				File file = new File(printFileName);
				ResponseBuilder response = Response.ok(file);
				response.header("Content-Disposition", "attachment; filename=\""
						+ printFileName + "\"");
				response.header("Content-Type", "application/pdf");
				return response.build();
			}
			@GET
			@Path("assignedTickets")
			@Consumes({ MediaType.APPLICATION_JSON })
			@Produces({ MediaType.APPLICATION_JSON })
			public String assignedTicketsForUser(@Context final UriInfo uriInfo){
				final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
			    final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
			    final List<ClientTicketData> data = this.ticketMasterReadPlatformService.retrieveAssignedTickets();
		        return this.apiJsonSerializerService.serializeClientTicketDataToJson(prettyPrint, responseParameters, data);
			}
		
}


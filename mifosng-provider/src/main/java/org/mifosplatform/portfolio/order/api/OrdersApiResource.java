package org.mifosplatform.portfolio.order.api;


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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.billingorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiDataBillingConversionService;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiJsonBillingSerializerService;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.order.command.OrdersCommand;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.payterms.data.PaytermData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.portfolio.pricing.service.PriceReadPlatformService;
import org.mifosplatform.portfolio.subscription.data.SubscriptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/orders")
@Component
@Scope("singleton")
public class OrdersApiResource {
	@Autowired
	private PortfolioApiDataBillingConversionService apiDataConversionService;

	@Autowired
	private PortfolioApiJsonBillingSerializerService apiJsonSerializerService;

	@Autowired
	private OrderWritePlatformService orderWritePlatformService;

	@Autowired
	private OrderReadPlatformService orderReadPlatformService;

	@Autowired
	private PriceReadPlatformService priceReadPlatformService;

	@Autowired
	private PlanReadPlatformService planReadPlatformService;


	@Autowired
	private ClientReadPlatformService clientReadPlatformService;

	@Autowired
	private PlatformSecurityContext context;

	private final String entityType = "ORDER";

	@POST
	  @Path("{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response createPlan(@PathParam("clientId") final Long clientId, final String jsonRequestBody) {

		OrdersCommand command = this.apiDataConversionService.convertJsonToOrderCommand(null,clientId, jsonRequestBody);

CommandProcessingResult userId = this.orderWritePlatformService.createOrder(command);
		return Response.ok().entity(userId).build();
	}
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveNewDepositProductDetails(@Context final UriInfo uriInfo) {

	context.authenticatedUser().validateHasReadPermission(entityType);

		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("allowedtypes","data","service_code","startDate"));

		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

		responseParameters.addAll(Arrays.asList("id","data","allowedtypes","startDate"));
		OrderData orderData = handleTemplateRelatedData(responseParameters);

		return this.apiJsonSerializerService.serializeOrderToJson(prettyPrint, responseParameters, orderData);
	}


	private OrderData handleTemplateRelatedData(final Set<String> responseParameters) {

		List<PlanCodeData> allowedtypes = this.orderReadPlatformService.retrieveAllPlatformData();
		List<PaytermData> data=new ArrayList<PaytermData>();//this.orderReadPlatformService.retrieveAllPaytermData();
		//this.orderReadPlatformService.retrieveInvoice();
		 List<SubscriptionData> contractPeriod=this.planReadPlatformService.retrieveSubscriptionData();


		return new OrderData(allowedtypes,data,contractPeriod);
	}

	@GET
	@Path("{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePlanDetails(
			@PathParam("orderId") final Long orderId,
			@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(entityType);

		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("id","start_date","billing_frequency","contract_period","endDate","price"));

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

		OrderData data = this.orderReadPlatformService
				.retrieveOrderData(orderId);
		responseParameters.addAll(Arrays.asList("servicedata"));


		if (template) {


			List<PlanCodeData> allowedtypes = this.orderReadPlatformService.retrieveAllPlatformData();
			List<PaytermData> data1=this.orderReadPlatformService.retrieveAllPaytermData();
			 List<SubscriptionData> contractPeriod=this.planReadPlatformService.retrieveSubscriptionData();

	      data= new OrderData(allowedtypes,data1,contractPeriod,data);


		}

		return this.apiJsonSerializerService.serializeOrderToJson(prettyPrint, responseParameters,data);
	}


	@PUT
	@Path("{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateOrder(@PathParam("orderId") final Long orderId
			) {

		//OrdersCommand command = this.apiDataConversionService.convertJsonToOrderCommand(null,null,jsonRequestBody);
		CommandProcessingResult entityIdentifier = this.orderWritePlatformService
				.updateOrder(orderId);
		return Response.ok().entity(entityIdentifier).build();
	}


	@GET
	 @Path("{planCode}/template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getBillingFrequency(@PathParam("planCode") final Long planCode,@Context final UriInfo uriInfo
			) {

	context.authenticatedUser().validateHasReadPermission(entityType);

		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("allowedtypes","data","service_code"));

		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

		responseParameters.addAll(Arrays.asList("id","data","allowedtypes"));
		OrderData orderData = handleTemplateRelatedData(responseParameters);
		List<PaytermData> datas  = this.orderReadPlatformService
				.getChargeCodes(planCode);
	
		if(datas.size()==0){
			throw new BillingOrderNoRecordsFoundException(planCode);
		}
		orderData.setPaytermData(datas);
		return this.apiJsonSerializerService.serializeOrderToJson(prettyPrint, responseParameters, orderData);
	}

	@DELETE
	@Path("{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteOrder(
			@PathParam("orderId") final Long orderId) {

List<OrderData> orederData=this.orderReadPlatformService.retrieveOrderLineData(orderId);
List<OrderPriceData> orderPrice=this.orderReadPlatformService.retrieveOrderPriceData(orderId);


CommandProcessingResult entityIdentifier=  this.orderWritePlatformService.deleteOrder(orderId,orederData,orderPrice);

return Response.ok().entity(entityIdentifier).build();
	}

	 @GET
	    @Path("{clientId}/orders")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveOrderDetails(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {

	        context.authenticatedUser().validateHasReadPermission("CLIENTORDER");

	        final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
	        final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

	        final List<OrderData> clientAccount = this.clientReadPlatformService.retrieveClientOrderDetails(clientId);

	        return this.apiJsonSerializerService.serializeClientOrderDataToJson(prettyPrint, responseParameters,
	                clientAccount);
	    }
	 
	 @GET
	    @Path("{orderId}/orderprice/{clientId}")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveOrderPriceDetails(@PathParam("orderId") final Long orderId, @PathParam("clientId") final Long clientId,
	    		@Context final UriInfo uriInfo) {

	        context.authenticatedUser().validateHasReadPermission("CLIENTORDER");

	        final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
	        final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

	        final List<OrderPriceData> priceDatas = this.orderReadPlatformService.retrieveOrderPriceDetails(orderId,clientId);
	        
	        OrderData orderData=new OrderData(priceDatas);

	        return this.apiJsonSerializerService.serializeClientOrderPriceDataToJson(prettyPrint, responseParameters,clientId,
	        		orderData);
	    }

	 @PUT
		@Path("{orderId}/orderprice")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public Response updateOrderPrice(@PathParam("orderId") final Long orderId,final String jsonRequestBody
				) {

			OrdersCommand command = this.apiDataConversionService.convertJsonToOrderCommand(null,null,jsonRequestBody);
			CommandProcessingResult entityIdentifier = this.orderWritePlatformService
					.updateOrderPrice(orderId,command);
			return Response.ok().entity(entityIdentifier).build();
		}

}

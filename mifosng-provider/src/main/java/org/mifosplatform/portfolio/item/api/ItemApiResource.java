package org.mifosplatform.portfolio.item.api;

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
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiDataBillingConversionService;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiJsonBillingSerializerService;
import org.mifosplatform.portfolio.charge.data.ChargesData;
import org.mifosplatform.portfolio.item.command.ItemCommand;
import org.mifosplatform.portfolio.item.service.ItemReadPltformService;
import org.mifosplatform.portfolio.item.service.ItemWritePltformService;
import org.mifosplatform.portfolio.onetimesale.data.ItemData;
import org.mifosplatform.portfolio.pricing.service.PriceReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/items")
@Component
@Scope("singleton")
public class ItemApiResource {
	
	@Autowired
	private PortfolioApiDataBillingConversionService apiDataConversionService;

	@Autowired
	private PortfolioApiJsonBillingSerializerService apiJsonSerializerService;
	
	@Autowired
	private PlatformSecurityContext context;
	
	private final String entityType = "ITEM";
 
	@Autowired
	private ItemReadPltformService itemReadPlatformService;
	
	@Autowired
	private ItemWritePltformService itemWritePlatformService;
	
	@Autowired
	private PriceReadPlatformService priceReadPlatformService;
	
	
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveItemTemplateData(@Context final UriInfo uriInfo) {


		final Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());
		
		ItemData itemData =handleTemplateData(responseParameters);

		

		return this.apiJsonSerializerService.serializeItemDataToJson(prettyPrint, responseParameters, itemData);
	}


	private ItemData handleTemplateData(Set<String> responseParameters) {
		List<EnumOptionData> itemClassdata = this.itemReadPlatformService
				.retrieveItemClassType();
		List<EnumOptionData> unitTypeData = this.itemReadPlatformService
				.retrieveUnitTypes();
		List<ChargesData> chargeDatas = this.itemReadPlatformService
				.retrieveChargeCode();
	

		 return new ItemData(itemClassdata, unitTypeData, chargeDatas);
	}
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createNewItem(final String jsonRequestBody) {

		ItemCommand command = this.apiDataConversionService
				.convertJsonToItemCommand(null, jsonRequestBody);
		
		CommandProcessingResult userId = this.itemWritePlatformService
				.creatItem(command);
		return Response.ok().entity(userId).build();
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllItems(@Context final UriInfo uriInfo) {


		final Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());
		
	List<ItemData> itemData=this.itemReadPlatformService.retrieveAllItems();

		

		return this.apiJsonSerializerService.serializeItemDataToJson(prettyPrint, responseParameters, itemData);
	}

	@GET
	@Path("{itemId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSingletemData(@PathParam("itemId") final Long itemId, @Context final UriInfo uriInfo) {


		final Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());
	
          ItemData itemData=this.itemReadPlatformService.retrieveSingleItemDetails(itemId);
           List<EnumOptionData> itemClassdata = this.itemReadPlatformService
   				.retrieveItemClassType();
   		List<EnumOptionData> unitTypeData = this.itemReadPlatformService
   				.retrieveUnitTypes();
   		List<ChargesData> chargeDatas = this.itemReadPlatformService
   				.retrieveChargeCode();
                itemData=new ItemData(itemData,itemClassdata,unitTypeData,chargeDatas);
              
		return this.apiJsonSerializerService.serializeItemDataToJson(prettyPrint, responseParameters, itemData);
	}

	@PUT
	@Path("{itemId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateItem(@PathParam("itemId") final Long itemId,final String jsonRequestBody) {

		ItemCommand command = this.apiDataConversionService
				.convertJsonToItemCommand(null, jsonRequestBody);
		
		CommandProcessingResult userId = this.itemWritePlatformService
				.updateItem(command,itemId);
		return Response.ok().entity(userId).build();
	}
	
	@DELETE
	@Path("{itemId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteItem(@PathParam("itemId") final Long itemId) {

				
		CommandProcessingResult userId = this.itemWritePlatformService
				.deleteItem(itemId);
		return Response.ok().entity(userId).build();
	}
	
}

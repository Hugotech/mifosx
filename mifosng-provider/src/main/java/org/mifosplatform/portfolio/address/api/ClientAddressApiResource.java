package org.mifosplatform.portfolio.address.api;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
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
import org.mifosplatform.portfolio.address.command.AddressCommand;
import org.mifosplatform.portfolio.address.data.AddressData;
import org.mifosplatform.portfolio.address.data.EntityTypecommand;
import org.mifosplatform.portfolio.address.service.AddressReadPlatformService;
import org.mifosplatform.portfolio.address.service.AddressWritePlatformService;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiDataBillingConversionService;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiJsonBillingSerializerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@Path("/address")
@Component
@Scope("singleton")
public class ClientAddressApiResource {
	
	@Autowired
	private PortfolioApiDataBillingConversionService apiDataConversionService;
	@Autowired
	private PortfolioApiJsonBillingSerializerService apiJsonSerializerService;
	@Autowired
	private AddressReadPlatformService addressReadPlatformService;
	@Autowired
	private AddressWritePlatformService addressWritePlatformService;
	
	
	private final String entityType = "ADDRESS";
    @Autowired
    private PlatformSecurityContext context;

	
	@GET
    @Path("template/{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAddressTemplateInfo( @PathParam("clientId") final Long clientId ,@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(entityType);

        Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
       
        boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

    List<AddressData> addressdata = this.addressReadPlatformService.retrieveAddressDetails(clientId);
   // List<AddressData> addressdata = this.addressReadPlatformService.retrieveAddressDetails();
    List<String> countryData = this.addressReadPlatformService.retrieveCountryDetails();
    List<String> statesData = this.addressReadPlatformService.retrieveStateDetails();
    List<String> citiesData = this.addressReadPlatformService.retrieveCityDetails();
    

    AddressData data=new AddressData(addressdata,countryData,statesData,citiesData);
    
        return this.apiJsonSerializerService.serializeDepositAddressDataToJson(prettyPrint, responseParameters, data);
    }
	
	@GET
    @Path("{selectedname}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAddressdetails( @Context final UriInfo uriInfo,	@PathParam("selectedname") final String selectedname ) {

        context.authenticatedUser().validateHasReadPermission(entityType);

        Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
       
        boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

       // List<AddressData> addressdata = this.addressReadPlatformService.retrieveSelectedAddressDetails(selectedname);
        List<AddressData> citiesData = this.addressReadPlatformService.retrieveCityDetails(selectedname);
          
        AddressData data=new AddressData(citiesData, null, null, null);
        
        return this.apiJsonSerializerService.serializeDepositAddressDataToJson(prettyPrint, responseParameters, data);
    }
	@GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveClientAddress( @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(entityType);

        Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
       
        boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
      
       
        List<String> countryData = this.addressReadPlatformService.retrieveCountryDetails();
        List<String> statesData = this.addressReadPlatformService.retrieveStateDetails();
        List<String> citiesData = this.addressReadPlatformService.retrieveCityDetails();
        
    
        AddressData data=new AddressData(null,countryData,statesData,citiesData);
        
    

        return this.apiJsonSerializerService.serializeDepositAddressDataToJson(prettyPrint, responseParameters, data);
    }
	
	@POST
	@Path("{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response createAddress(@PathParam("clientId") final Long clientId, final String jsonRequestBody) {

		AddressCommand command = this.apiDataConversionService.convertJsonToAddressCommand(null,clientId, jsonRequestBody);

CommandProcessingResult userId = this.addressWritePlatformService.createAddress(command);
		return Response.ok().entity(userId).build();
	}
	
	@PUT
	@Path("{addrId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response updateClientAddress(@PathParam("addrId") final Long addrId, final String jsonRequestBody){

		final AddressCommand command = this.apiDataConversionService.convertJsonToAddressCommand(null, addrId, jsonRequestBody);
		CommandProcessingResult entityIdentifier=this.addressWritePlatformService.updateAddress(addrId,command);
		return Response.ok().entity(entityIdentifier).build();
	}
	
	@POST
	@Path("{entityType}/new")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response NewRecord(@PathParam("entityType") final String entityType, final String jsonRequestBody) {

		 EntityTypecommand command= this.apiDataConversionService.convertJsonToEntityTypeCommand(null,jsonRequestBody);

CommandProcessingResult userId = this.addressWritePlatformService.createNewRecord(command,entityType);
		return Response.ok().entity(userId).build();
	}
}

package org.mifosplatform.portfolio.financialtransaction.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiDataBillingConversionService;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiJsonBillingSerializerService;
import org.mifosplatform.portfolio.billmaster.service.BillMasterReadPlatformService;
import org.mifosplatform.portfolio.financialtransaction.data.FinancialTransactionsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;




@Path("/financialTransactions")
@Component
@Scope("singleton")
public class FinancialTransactionApiResource {

	@Autowired
	private PortfolioApiDataBillingConversionService apiDataConversionService;

	@Autowired
	private PortfolioApiJsonBillingSerializerService apiJsonSerializerService;

	@Autowired
	private BillMasterReadPlatformService billMasterReadPlatformService;

	@Autowired
	private PlatformSecurityContext context;

	private final String entityType = "FINANCIALTRANSACTION";


	@GET
	@Path("{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTransactionalData(
			@PathParam("clientId") final Long clientId,
			@Context final UriInfo uriInfo) {

	context.authenticatedUser().validateHasReadPermission(entityType);

		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("transactionId","transactionDate","transactionType","amount","transaction"));

		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

		List<FinancialTransactionsData> transactionData = this.billMasterReadPlatformService.retrieveInvoiceFinancialData(clientId);
    
		
		


		return this.apiJsonSerializerService.serializeTransactionalDataToJson(prettyPrint, responseParameters, transactionData);
	}
	
	@GET
	@Path("{invoiceId}/invoice")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveInvoiceData(
			@PathParam("invoiceId") final Long invoiceId,
			@Context final UriInfo uriInfo) {

	context.authenticatedUser().validateHasReadPermission(entityType);

		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("invoiceId","chrageAmount","taxAmount","chargeType","amount","chargeStartDate","chargeEndDate"));

		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

		List<FinancialTransactionsData> transactionData = this.billMasterReadPlatformService.retrieveSingleInvoiceData(invoiceId);

		FinancialTransactionsData data=new FinancialTransactionsData(transactionData);

		return this.apiJsonSerializerService.serializeTransactionalDataToJson(prettyPrint, responseParameters, data);
	}

}

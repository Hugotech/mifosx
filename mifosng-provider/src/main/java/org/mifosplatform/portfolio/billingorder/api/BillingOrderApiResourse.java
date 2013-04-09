package org.mifosplatform.portfolio.billingorder.api;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.billingorder.data.GenerateInvoiceData;
import org.mifosplatform.portfolio.billingorder.data.InvoiceAmountIdentifier;
import org.mifosplatform.portfolio.billingorder.service.BillingOrderReadPlatformService;
import org.mifosplatform.portfolio.billingorder.service.InvoiceClient;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiDataBillingConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/billingorder")
@Component
@Scope("singleton")
public class BillingOrderApiResourse {

	private InvoiceClient invoiceClient;
	private PortfolioApiDataBillingConversionService apiDataConversionService;
	private final BillingOrderReadPlatformService billingOrderReadPlatformService;
	
	@Autowired
	BillingOrderApiResourse(InvoiceClient invoiceClient,PortfolioApiDataBillingConversionService apiDataConversionService,
			BillingOrderReadPlatformService billingOrderReadPlatformService){
		this.invoiceClient = invoiceClient;
		this.apiDataConversionService = apiDataConversionService;
		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
	}
	
	
	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response retrieveBillingProducts(@PathParam("clientId") final Long clientId,final String jsonRequestBody) {

		 	LocalDate  processDate = this.apiDataConversionService.convertJsonToBillingProductCommand(null, jsonRequestBody);
		 	
		 	BigDecimal invoiceAmount=invoiceClient.invoicingSingleClient(clientId, processDate);

		return Response.ok().entity(new InvoiceAmountIdentifier(invoiceAmount)).build();
	}
	
	@POST
	@Path("generateInvoice")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response generateInvoices(final String jsonRequestBody) {
		
		LocalDate  processDate = this.apiDataConversionService.convertJsonToBillingProductCommand(null, jsonRequestBody);

		List<GenerateInvoiceData> invoiceDatas = this.billingOrderReadPlatformService.retrieveClientsWithOrders();
		if (invoiceDatas == null || invoiceDatas.size() == 0) {
			throw new RuntimeException();
		}else {
			for(GenerateInvoiceData invoiceData : invoiceDatas){
				invoiceClient.invoicingSingleClient(invoiceData.getClientId(), processDate);
			}
			
		}
		
		return Response.ok().entity(1l).build();
	}

}

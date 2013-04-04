package org.mifosplatform.portfolio.billingmaster.api;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.billingmaster.command.BillMasterCommand;
import org.mifosplatform.portfolio.billingorder.data.BillDetailsData;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiDataBillingConversionService;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiJsonBillingSerializerService;
import org.mifosplatform.portfolio.billmaster.domain.BillDetail;
import org.mifosplatform.portfolio.billmaster.domain.BillMaster;
import org.mifosplatform.portfolio.billmaster.domain.BillMasterRepository;
import org.mifosplatform.portfolio.billmaster.service.BillMasterReadPlatformService;
import org.mifosplatform.portfolio.billmaster.service.BillMasterWritePlatformService;
import org.mifosplatform.portfolio.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/billmaster")
@Component
@Scope("singleton")
public class BillingMasterApiResourse {

	@Autowired
	private BillMasterReadPlatformService billMasterReadPlatformService;

	@Autowired
	private BillMasterWritePlatformService billMasterWritePlatformService;

	@Autowired
	private OrderReadPlatformService orderReadPlatformService;

	@Autowired
	private PlatformSecurityContext context;

	@Autowired
	private PortfolioApiDataBillingConversionService apiDataConversionService;

	@Autowired
	private BillMasterRepository billMasterRepository;

	@Autowired
	private PortfolioApiJsonBillingSerializerService apiJsonSerializerService;

	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response retrieveBillingProducts(
			@PathParam("clientId") final Long clientId,
			final String jsonRequestBody) {

		BillMasterCommand command = this.apiDataConversionService
				.convertJsonToBillMasterCommand(null, jsonRequestBody);

		List<FinancialTransactionsData> financialTransactionsDatas = billMasterReadPlatformService
				.retrieveFinancialData(clientId);
		BillMaster billMaster = null;
		BigDecimal previousBal = BigDecimal.ZERO;
		List<BillMaster> billMasters = this.billMasterRepository.findAll();

		for (BillMaster data : billMasters) {

			if (data.getClientId().compareTo(clientId)==0) {
				previousBal = this.billMasterReadPlatformService.retrieveClientBalance(clientId);
			}

		}
		billMaster = billMasterWritePlatformService.createBillMaster(
				financialTransactionsDatas, command, clientId);
		List<BillDetail> billDetail = billMasterWritePlatformService
				.createBillDetail(financialTransactionsDatas, billMaster);

		billMasterWritePlatformService.updateBillMaster(billDetail, billMaster,
				previousBal);

		billMasterWritePlatformService.updateBillId(financialTransactionsDatas,
				billMaster.getId());

		BillDetailsData billDetails = this.billMasterReadPlatformService
				.retrievebillDetails(billMaster.getId());
		// List<FinancialTransactionsData>
		// data=this.billMasterReadPlatformService.getFinancialTransactionData(billDetails.getId());
      this.billMasterWritePlatformService.generatePdf(billDetails,financialTransactionsDatas);
	
    //this.billMasterWritePlatformService.ireportPdf(billMaster.getId());	

		return Response.ok().entity(1).build();
	}

	@GET
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveBillStatements(
			@PathParam("clientId") final Long clientId,
			@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission("CLIENTORDER");

		final Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());

		final List<FinancialTransactionsData> data = this.billMasterReadPlatformService
				.retrieveStatments(clientId);

		return this.apiJsonSerializerService.serializeTransactionalDataToJson(
				prettyPrint, responseParameters, data);
	}

	@GET
	@Path("{billId}/print")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response printInvoice(@PathParam("billId") final Long billId) {

		BillMaster billMaster = this.billMasterRepository.findOne(billId);

		String printFileName = billMaster.getFileName();

		File file = new File(printFileName);
		ResponseBuilder response = Response.ok(file);
		response.header("Content-Disposition", "attachment; filename=\""
				+ printFileName + "\"");
		response.header("Content-Type", "application/pdf");
		return response.build();
	}

}

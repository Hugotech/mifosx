package org.mifosplatform.portfolio.billingorder.service;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.adjustment.service.AdjustmentReadPlatformService;
import org.mifosplatform.portfolio.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.portfolio.billingorder.commands.InvoiceCommand;
import org.mifosplatform.portfolio.billingorder.data.BillingOrderData;
import org.mifosplatform.portfolio.billingorder.domain.BillingOrder;
import org.mifosplatform.portfolio.billingorder.domain.Invoice;
import org.mifosplatform.portfolio.billingorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiDataBillingConversionService;
import org.mifosplatform.portfolio.billingproduct.PortfolioApiJsonBillingSerializerService;
import org.mifosplatform.portfolio.clientbalance.data.ClientBalanceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceClient {

	private BillingOrderReadPlatformService billingOrderReadPlatformService;

	private GenerateBillingOrderService generateBillingOrderService;

	private PortfolioApiJsonBillingSerializerService apiJsonSerializerService;

	private BillingOrderWritePlatformService billingOrderWritePlatformService;

	private PortfolioApiDataBillingConversionService apiDataConversionService;

	private AdjustmentReadPlatformService adjustmentReadPlatformService;
	
	
	@Autowired
	InvoiceClient(
			BillingOrderReadPlatformService billingOrderReadPlatformService,
			GenerateBillingOrderService generateBillingOrderService,
			PortfolioApiJsonBillingSerializerService apiJsonSerializerService,
			BillingOrderWritePlatformService billingOrderWritePlatformService,
			PortfolioApiDataBillingConversionService apiDataConversionService,
			AdjustmentReadPlatformService adjustmentReadPlatformService) {
		

		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
		this.generateBillingOrderService = generateBillingOrderService;
		this.apiJsonSerializerService = apiJsonSerializerService;
		this.billingOrderWritePlatformService = billingOrderWritePlatformService;
		this.apiDataConversionService = apiDataConversionService;
		this.adjustmentReadPlatformService = adjustmentReadPlatformService;
	}

	public BigDecimal invoicingSingleClient(Long clientId, LocalDate processDate) {

		// Get list of qualified orders
		//List<Long> orderIds = billingOrderReadPlatformService.retrieveOrderIds(clientId, processDate);
		List<BillingOrderData> billingOrderIds = billingOrderReadPlatformService.retrieveOrderIds(clientId, processDate);
		if (billingOrderIds.size() == 0) {
			throw new BillingOrderNoRecordsFoundException();
		}
		   BigDecimal invoiceAmount=BigDecimal.ZERO;
		for (BillingOrderData billingOrderId  : billingOrderIds) {
			LocalDate billStartDate = new LocalDate(billingOrderId.getStartDate());
			int qualifiedNumberOfTimes = getQualifiedNumberOfTimes(billStartDate, processDate, billingOrderId.getDurationType());
			int noOfTimes = 0;
			while(noOfTimes!=qualifiedNumberOfTimes){
	        BigDecimal invoicePrice=invoiceServices(billingOrderId.getOrderId(),clientId,processDate);
	               invoiceAmount=invoiceAmount.add(invoicePrice);
				noOfTimes++;
			}
		}
		return invoiceAmount;
	}
	
	public BigDecimal invoiceServices(Long orderId,Long clientId,LocalDate processDate){
		

			// Charges
			List<BillingOrderData> products = this.billingOrderReadPlatformService.retrieveBillingOrderData(clientId, processDate,orderId);

			List<BillingOrderCommand> billingOrderCommands = this.generateBillingOrderService.generatebillingOrder(products);
			List<BillingOrder> listOfBillingOrders = billingOrderWritePlatformService.createBillingProduct(billingOrderCommands);

			// Invoice
			InvoiceCommand invoiceCommand = this.generateBillingOrderService.generateInvoice(billingOrderCommands);
			List<ClientBalanceData> clientBalancesDatas = adjustmentReadPlatformService.retrieveAllAdjustments(clientId);
			Invoice invoice = billingOrderWritePlatformService.createInvoice(invoiceCommand, clientBalancesDatas);

			// Update invoice-tax
			billingOrderWritePlatformService.updateInvoiceTax(invoice, billingOrderCommands, listOfBillingOrders);

			// Update charge
			billingOrderWritePlatformService.updateInvoiceCharge(invoice,
					listOfBillingOrders);

			// Update orders
			billingOrderWritePlatformService
					.updateBillingOrder(billingOrderCommands);

			// Update order-price
			@SuppressWarnings("unused")
			CommandProcessingResult entityIdentifier = billingOrderWritePlatformService.updateOrderPrice(billingOrderCommands);
               if(invoiceCommand.getInvoiceAmount() == null){
            	   return BigDecimal.ZERO;
               }
		return invoiceCommand.getInvoiceAmount();
	}
	
	public Integer getQualifiedNumberOfTimes(LocalDate billStartDate , LocalDate processDate,String durationType ){
		int qualifiedNumberOfTimes = 0;
		int qualifiedBillingDays = Days.daysBetween(billStartDate, processDate).getDays()+1;
		
		if(durationType.equals("Week(s)")){
			if(qualifiedBillingDays<=7){
				qualifiedNumberOfTimes = 1;
			}else{
				qualifiedNumberOfTimes = (qualifiedBillingDays / 7)+1;
			}
			
		}else if(durationType.equals("Month(s)")){
			if(qualifiedBillingDays<=30){
				qualifiedNumberOfTimes = 1;
			}else{
				qualifiedNumberOfTimes = (qualifiedBillingDays / 30)+1;
			}
		}
		return qualifiedNumberOfTimes;
	}

}

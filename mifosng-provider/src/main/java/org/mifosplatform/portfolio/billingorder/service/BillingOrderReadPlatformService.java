package org.mifosplatform.portfolio.billingorder.service;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.billingorder.data.BillingOrderData;
import org.mifosplatform.portfolio.billingorder.data.GenerateInvoiceData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;
import org.mifosplatform.portfolio.taxmaster.data.TaxMappingRateData;

public interface BillingOrderReadPlatformService {

	
	List<TaxMappingRateData> retrieveTaxMappingDate(String chargeCode);

	List<OrderPriceData> retrieveInvoiceTillDate(Long clientOrderId);

	List<BillingOrderData> retrieveBillingOrderData(Long clientId,LocalDate localDate, Long planId);

	List<BillingOrderData> retrieveOrderIds(Long clientId, LocalDate processDate);

	List<GenerateInvoiceData> retrieveClientsWithOrders();




}

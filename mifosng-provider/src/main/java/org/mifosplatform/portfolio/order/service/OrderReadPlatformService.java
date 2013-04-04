package org.mifosplatform.portfolio.order.service;

import java.util.List;

import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;
import org.mifosplatform.portfolio.payterms.data.PaytermData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;

public interface OrderReadPlatformService {

	List<PlanCodeData>  retrieveAllPlatformData();
	List<PaytermData>  retrieveAllPaytermData();

	List<OrderData> retrieveOrderLineData(Long orderId);
	List<OrderPriceData> retrieveOrderPriceData(Long orderId);

OrderData retrieveOrderData(Long orderId);
void retrieveInvoice(Long clientId);
List<PaytermData> getChargeCodes(Long planCode);
List<OrderPriceData> retrieveOrderPriceDetails(Long orderId, Long clientId);

}

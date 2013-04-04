package org.mifosplatform.portfolio.order.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.order.command.OrdersCommand;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;

public interface OrderWritePlatformService {

	CommandProcessingResult createOrder(final OrdersCommand command);

	CommandProcessingResult deleteOrder(Long orderId, List<OrderData> orederData, List<OrderPriceData> orderPrice);

	CommandProcessingResult updateOrder(Long orderId);

	CommandProcessingResult updateOrderPrice(Long orderId, OrdersCommand command);

}

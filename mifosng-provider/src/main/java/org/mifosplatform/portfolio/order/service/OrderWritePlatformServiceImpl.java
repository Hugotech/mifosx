package org.mifosplatform.portfolio.order.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.order.command.OrdersCommand;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderCommanValidator;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.domain.OrderPriceRepository;
import org.mifosplatform.portfolio.order.domain.OrderReadPlatformImpl;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.plan.domain.ServiceDetailsRepository;
import org.mifosplatform.portfolio.pricing.data.PriceData;
import org.mifosplatform.portfolio.pricing.domain.PriceRepository;
import org.mifosplatform.portfolio.subscription.domain.Subscription;
import org.mifosplatform.portfolio.subscription.domain.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

//import org.mifosng.platform.savingorder.domain.OrderLineRepository;

@Service
public class OrderWritePlatformServiceImpl implements OrderWritePlatformService {

	private PlatformSecurityContext context;
	private OrderRepository orderRepository;
	// private OrderLineRepository OrderLineRepository;
	private ServiceDetailsRepository serviceDetailsRepository;
	private PlanRepository planRepository;
	private PriceRepository planPriceRepository;
	private SubscriptionRepository subscriptionRepository;
	private OrderPriceRepository OrderPriceRepository;
	private final JdbcTemplate jdbcTemplate;

	// private final TenantAwareRoutingDataSource dataSource;
	@Autowired
	public OrderWritePlatformServiceImpl(final PlatformSecurityContext context,
			final OrderRepository orderRepository,
			final PlanRepository planRepository,
			final OrderPriceRepository OrderPriceRepository,
			final ServiceDetailsRepository serviceDetailsRepository,
			final TenantAwareRoutingDataSource dataSource,
			final PriceRepository priceRepository,
			final SubscriptionRepository subscriptionRepository) {
		this.context = context;
		this.orderRepository = orderRepository;
		// this.OrderLineRepository = OrderLineRepository;
		this.OrderPriceRepository = OrderPriceRepository;
		this.planPriceRepository = priceRepository;
		this.planRepository = planRepository;
		this.serviceDetailsRepository = serviceDetailsRepository;
		this.subscriptionRepository = subscriptionRepository;

		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override
	public CommandProcessingResult createOrder(OrdersCommand command) {

		try {
			this.context.authenticatedUser();
			OrderCommanValidator validator = new OrderCommanValidator(command);
			validator.validateForCreate();
			List<OrderLine> serviceDetails = new ArrayList<OrderLine>();
			List<OrderPrice> orderprice = new ArrayList<OrderPrice>();

			OrderReadPlatformImpl obj = new OrderReadPlatformImpl(context,
					jdbcTemplate);
			// PlanData plan = obj.retrievePlanData(command.getPlan_id());

			Plan plan = this.planRepository.findOne(command.getPlanid());
			List<ServiceData> details = obj.retrieveAllServices(command
					.getPlanid());
			List<PriceData> datas = obj.retrieveAllPrices(command.getPlanid(),
					command.getBillingFrequency());
			LocalDate endDate = null;
			Subscription subscriptionData = this.subscriptionRepository
					.findOne(command.getContractPeriod());

			if (subscriptionData.getSubscriptionType().equalsIgnoreCase(
					"DAY(s)")) {
				endDate = command.getStartDate().plusDays(
						subscriptionData.getUnits().intValue() - 1);

			} else if (subscriptionData.getSubscriptionType()
					.equalsIgnoreCase("MONTH(s)")) {
				endDate = command.getStartDate()
						.plusMonths(subscriptionData.getUnits().intValue())
						.minusDays(1);

			} else if (subscriptionData.getSubscriptionType()
					.equalsIgnoreCase("YEAR(s)")) {
				endDate = command.getStartDate()
						.plusYears(subscriptionData.getUnits().intValue())
						.minusDays(1);
			} else if (subscriptionData.getSubscriptionType()
					.equalsIgnoreCase("WEEK(s)")) {

				endDate = command.getStartDate()
						.plusWeeks(subscriptionData.getUnits().intValue())
						.minusDays(1);
			}

			else if (subscriptionData.getSubscriptionType().equalsIgnoreCase(
					"HOUR(s)")) {

				DateTime startDate = command.getStartDate().toDateTime(null,
						null);
				endDate = startDate.plusHours(
						subscriptionData.getUnits().intValue()).toLocalDate();

			}

			char billAlign=command.isBillAlign()?'y':'n';
			Order order = new Order(command.getClientId(), command.getPlanid(),
					plan.getStatus(), command.getDurationType(),
					command.getBillingFrequency(), command.getStartDate(),
					endDate, command.getContractPeriod(), serviceDetails,
					orderprice, billAlign);

			for (PriceData data : datas) {

				Date billstartDate = command.getStartDate().toDate();
				Date billEndDate = null;
				// end date is null for rc
				if (data.getChagreType().equalsIgnoreCase("RC")
						&& endDate != null) {
					billEndDate = endDate.toDate();

				} else if(data.getChagreType().equalsIgnoreCase("NRC")) 			{

					billEndDate = billstartDate;
				}


				OrderPrice price = new OrderPrice(data.getServiceId(),
						data.getChargeCode(), data.getCharging_variant(),
						data.getPrice(), null, data.getChagreType(),
						data.getChargeDuration(), data.getDurationType(),
						billstartDate, billEndDate);
				order.addOrderDeatils(price);

			}

			// for( PriceData datas:data)
			// {
			// orderprice = new OrderPrice(datas.getId(),
			// datas.getChargeCode(), datas.getCharging_variant(),
			// datas.getPrice(),order);
			//
			// order.addOrderDeatils(orderprice);
			// }

			for (ServiceData data : details) {
				OrderLine orderdetails = new OrderLine(data.getPlanId(),
						data.getCode(), plan.getStatus(), 'n');
				order.addServiceDeatils(orderdetails);
			}

			// order.addOrderDeatils(orderprice);
			this.orderRepository.save(order);

			// this.OrderPriceRepository.save(orderprice);

			return new CommandProcessingResult(command.getPlanid());

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	private void handleDataIntegrityIssues(OrdersCommand command,
			DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub

	}

	@Override
	public CommandProcessingResult deleteOrder(Long orderId, List<OrderData> orederData,
			List<OrderPriceData> data) {
		try {

			Order order = this.orderRepository.findOne(orderId);
			// OrderPrice orderPrice=order.getPrice();
			List<OrderLine> orderline = order.getServices();
			List<OrderPrice> orderPrices=order.getPrice();
			
			for(OrderPrice orderPrice:orderPrices){
				orderPrice.delete();
			}

			for (OrderLine orderData : orderline) {
				orderData.delete();
			}

			// orderPrice.delete();
			order.delete();
			this.orderRepository.save(order);
			

			return new CommandProcessingResult(order.getId());

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	private void handleDataIntegrityIssues(DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub

	}

	@Override
	public CommandProcessingResult updateOrder(Long orderId) {

		try {

			Order order = this.orderRepository.findOne(orderId);

			LocalDate currentDate = new LocalDate();
			currentDate.toDate();

			// if (order==null || order.getStatus() == 3) {
			// throw new ProductNotFoundException(order.getId());
			// }

			List<OrderPrice> orderPrices=order.getPrice();
			for(OrderPrice price:orderPrices){
				
				price.updateDates(new LocalDate());
			}
			
			order.update(currentDate);
			
			this.orderRepository.save(order);
			return new CommandProcessingResult(Long.valueOf(order.getId()));
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	@Override
	public CommandProcessingResult updateOrderPrice(Long orderId,
			OrdersCommand command) {
		try
		{
			OrderPrice orderPrice=this.OrderPriceRepository.findOne(orderId);
			if(orderPrice!=null){
				orderPrice.setPrice(command);
				this.OrderPriceRepository.save(orderPrice);
			}
			return new CommandProcessingResult(Long.valueOf(orderPrice.getId()));
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		
	}

}
}

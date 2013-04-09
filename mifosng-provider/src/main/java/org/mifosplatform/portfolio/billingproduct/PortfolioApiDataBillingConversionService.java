package org.mifosplatform.portfolio.billingproduct;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.address.command.AddressCommand;
import org.mifosplatform.portfolio.address.data.EntityTypecommand;
import org.mifosplatform.portfolio.adjustment.commands.AdjustmentCommand;
import org.mifosplatform.portfolio.billingcycle.command.BillingCycleCommand;
import org.mifosplatform.portfolio.billingmaster.command.BillMasterCommand;
import org.mifosplatform.portfolio.charge.commands.ChargeCodeCommand;
import org.mifosplatform.portfolio.discountmaster.commands.DiscountMasterCommand;
import org.mifosplatform.portfolio.item.command.ItemCommand;
import org.mifosplatform.portfolio.onetimesale.command.OneTimeSaleCommand;
import org.mifosplatform.portfolio.order.command.OrdersCommand;
import org.mifosplatform.portfolio.payment.command.Paymentcommand;
import org.mifosplatform.portfolio.paymodes.commands.PaymodeCommand;
import org.mifosplatform.portfolio.payterms.commands.PaytermsCommand;
import org.mifosplatform.portfolio.plan.commands.PlansCommand;
import org.mifosplatform.portfolio.pricing.commands.PricingCommand;
import org.mifosplatform.portfolio.servicemaster.commands.ServiceMasterCommand;
import org.mifosplatform.portfolio.servicemaster.commands.ServicesCommand;
import org.mifosplatform.portfolio.subscription.commands.SubscriptionCommand;
import org.mifosplatform.portfolio.taxmaster.commands.TaxMappingRateCommand;
import org.mifosplatform.portfolio.taxmaster.commands.TaxMasterCommand;
import org.mifosplatform.portfolio.ticketmaster.command.TicketMasterCommand;

public interface PortfolioApiDataBillingConversionService {

	SubscriptionCommand convertJsonToSubscriptionCommand(
			Long resourceIdentifier, String json);

	PaytermsCommand convertJsonToPaytermsCommand(Long resourceIdentifier,
			String json);

	PaymodeCommand convertJsonToPaymodeCommand(Long resourceIdentifier,
			String json);

	BillingCycleCommand convertJsonToBillingCycleCommand(
			Long resourceIdentifier, String json);

	PlansCommand convertJsonToPlansCommand(Long resourceIdentifier, String json);

	Paymentcommand convertJsonToPaymentCommand(Long resourceIdentifier,
			String json);

	PricingCommand convertJsonToPricingCommand(Long resourceIdentifier,
			String json);

	OrdersCommand convertJsonToOrderCommand(Object resourceIdentifier,
			Long clientId, String jsonRequestBody);

	// Madhav
	DiscountMasterCommand convertJsonToDiscountMasterCommand(
			Long resourceIdentifier, String json);

	TaxMasterCommand convertJsonToTaxMasterCommand(Long resourceIdentifier,
			String json);

	TaxMappingRateCommand convertJsonToTaxMappingRateCommand(
			Long resourceIdentifier, String json);

	ChargeCodeCommand convertJsonToChargeCodeCommand(Long resourceIdentifier,
			String json);

	AdjustmentCommand convertJsonToAdjustmentCommand(Long resourceIdentifier,
			String json);

	ServiceMasterCommand convertJsonToServiceMasterCommand(
			Long resourceIdentifier, String json);

	ServicesCommand convertJsonToServiceCommand(Long resourceIdentifier,
			String json);

	LocalDate convertJsonToBillingProductCommand(Long resourceIdentifier,String jsonRequestBody);

	BillMasterCommand convertJsonToBillMasterCommand(Long resourceIdentifier,String json);

	TicketMasterCommand convertJsonToTicketMasterCommand(Object object,String jsonRequestBody);

	AddressCommand convertJsonToAddressCommand(Object object, Long clientId,
			String jsonRequestBody);

	OneTimeSaleCommand convertJsonToSalesCommand(Object object,	String jsonRequestBody);

	ItemCommand convertJsonToItemCommand(Object object, String jsonRequestBody);

	EntityTypecommand convertJsonToEntityTypeCommand(Object object, String jsonRequestBody);

}
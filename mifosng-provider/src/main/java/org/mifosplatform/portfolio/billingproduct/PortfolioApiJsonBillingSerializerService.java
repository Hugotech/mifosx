package org.mifosplatform.portfolio.billingproduct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mifosplatform.portfolio.address.data.AddressData;
import org.mifosplatform.portfolio.adjustment.data.AdjustmentCodeData;
import org.mifosplatform.portfolio.billingcycle.data.BillingCycleData;
import org.mifosplatform.portfolio.discountmaster.commands.Discount;
import org.mifosplatform.portfolio.discountmaster.data.DiscountMasterData;
import org.mifosplatform.portfolio.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.portfolio.loanaccount.data.LoanBasicDetailsData;
import org.mifosplatform.portfolio.onetimesale.data.ItemData;
import org.mifosplatform.portfolio.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;
import org.mifosplatform.portfolio.paymodes.data.PaymodeData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.pricing.data.PricingData;
import org.mifosplatform.portfolio.servicemaster.data.SericeMasterOptionsData;
import org.mifosplatform.portfolio.subscription.data.SubscriptionData;
import org.mifosplatform.portfolio.taxmaster.data.TaxMappingRateOptionsData;
import org.mifosplatform.portfolio.taxmaster.data.TaxMasterDataOptions;
import org.mifosplatform.portfolio.ticketmaster.data.ClientTicketData;
import org.mifosplatform.portfolio.ticketmaster.data.TicketMasterData;

public interface PortfolioApiJsonBillingSerializerService {



String serializeSubscriptionToJson(boolean prettyPrint,	Set<String> responseParameters,Collection<SubscriptionData> products);

String serializeSubscriptionToJson(boolean prettyPrint,
Set<String> responseParameters, SubscriptionData products);

String serializePaytermsToJson(boolean prettyPrint,Set<String> responseParameters, SubscriptionData products);

//String serializePaymodeToJson(boolean prettyPrint,Set<String> responseParameters,List<EnumOptionData> categorytypes);

String serializePaymodeToJson(boolean prettyPrint,Set<String> responseParameters, Collection<PaymodeData> data);

String serializeBillingCycleToJson(boolean prettyPrint,	Set<String> responseParameters,	BillingCycleData products);

String serializePlanDataToJson(boolean prettyPrint,	Set<String> responseParameters, PlanData planData);


String serializePaymentToJson(boolean prettyPrint,Set<String> responseParameters, LoanBasicDetailsData loanAccount, PaymodeData data);



String serializePlanDataToJson(boolean prettyPrint,Set<String> responseParameters, List<PlanData> products);

String serializeOrderToJson(boolean prettyPrint,Set<String> responseParameters, OrderData datas);

//Madhav
public String serializeDiscountMasterDataToJson(boolean prettyPrint,Set<String> responseParameters, DiscountMasterData discountMasterData);
public String serializeDiscountMasterDataToJson(boolean prettyPrint,Set<String> responseParameters, Collection<DiscountMasterData> discountMasterData);
public String serializeDiscountMasterDataToJsonTemplete(boolean prettyPrint,Set<String> responseParameters,AdjustmentCodeData datas);
public String serializeServiceMasterDataToJson(boolean prettyPrint,Set<String> responseParameters, Collection<SericeMasterOptionsData> serviceMasterData);
String serializeTaxMasterDataToJson(boolean prettyPrint,Set<String> responseParameters, Collection<TaxMasterDataOptions> taxMasterData);
public String serializeTaxMappingRateTemplateDataToJson(boolean prettyPrint,Set<String> responseParameters, Collection<TaxMappingRateOptionsData> taxMappingRateOptionsData);

String serializeServiceToJson(boolean prettyPrint,Set<String> responseParameters, ServiceData products);


String serializeClientOrderDataToJson(boolean prettyPrint,Set<String> responseParameters, List<OrderData> clientAccount);

String serializeDiscountMasterDataToJsonTemplete(boolean prettyPrint,Set<String> responseParameters, Collection<Discount> datass);

String serializeOrderToJson(boolean prettyPrint,
Set<String> responseParameters, List<OrderData> data);

String serializeTransactionalDataToJson(boolean prettyPrint,Set<String> responseParameters,	FinancialTransactionsData data);

String serializePricingDataToJson(boolean prettyPrint,Set<String> responseParameters, PricingData clientAccount);


String serializeServiceDataToJson(boolean prettyPrint,	Set<String> responseParameters, SericeMasterOptionsData productData);

String serializeTicketMasterToJson(boolean prettyPrint,	Set<String> responseParameters, TicketMasterData templateData);

String serializeTicketMasterToJson(boolean prettyPrint,Set<String> responseParameters, List<TicketMasterData> data);

String serializePaymodeToJson(boolean prettyPrint,Set<String> responseParameters, PaymodeData data);

String serializePaymodeToJson(boolean prettyPrint,Set<String> responseParameters, LoanBasicDetailsData loanBasicDetails,
		Collection<PaymodeData> data);

String serializePaymodeToJson(boolean prettyPrint,
		Set<String> responseParameters, LoanBasicDetailsData loanBasicDetails,
		PaymodeData datas);

String serializeServiceMasterDataToJson(boolean prettyPrint,	Set<String> responseParameters, SericeMasterOptionsData productData);

String serializeClientOrderPriceDataToJson(boolean prettyPrint,	Set<String> responseParameters, Long clientId, OrderData orderData);

String serializeTransactionalDataToJson(boolean prettyPrint,Set<String> responseParameters,List<FinancialTransactionsData> transactionData);

String serializeClientTicketDataToJson(boolean prettyPrint,	Set<String> responseParameters, List<ClientTicketData> data);

String serializeDepositAddressDataToJson(boolean prettyPrint,Set<String> responseParameters, List<AddressData> addressdata);

String serializeDepositAddressDataToJson(boolean prettyPrint,Set<String> responseParameters, AddressData addressdata);

String serializeOneTimeSaleDataToJson(boolean prettyPrint,Set<String> responseParameters, OneTimeSaleData data);

String serializeOneTimeSaleDataToJson(boolean prettyPrint,Set<String> responseParameters, List<OneTimeSaleData> salesData);

String serializeItemDataToJson(boolean prettyPrint,	Set<String> responseParameters, ItemData itemData);

String serializeItemDataToJson(boolean prettyPrint,
		Set<String> responseParameters, List<ItemData> itemData);






}
package org.mifosplatform.portfolio.billingproduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifosplatform.infrastructure.core.serialization.GoogleGsonSerializerHelper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class GoogleGsonPortfolioApiJsonBillingSerializerService implements PortfolioApiJsonBillingSerializerService {


	    private static final Set<String> SUBSCRIPTION_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id",
	           "subscriptionPeriod","subscriptionType","units","allowedtypes","subscriptionTypeId"));

	    private static final Set<String> SERVICE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id","serviceCode","serviceDescription","serviceType","serviceData"));

	    private static final Set<String> ORDER_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id","cancelledStatus","status","contractPeriod","nextBillDate","flag",
	           "currentDate","plan_code","units","service_code","allowedtypes","data","servicedata","billing_frequency", "start_date", "contract_period","startDate","invoiceTillDate"));

	    private static final Set<String> BILLINGCYCLE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id",
	            "billing_code","description","frequency","every","day_name","day_num","allowedtypes"));

	    private static final Set<String> PAYMODE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("data","id",
	            "paymodeCode","paymodeDescription"));
	    
	    private static final Set<String> ADDRESS_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("addressid","clientId",
	            "addressNo","street","zipCode","city","state","country","datas","countryData","stateData","cityData"));

	    private static final Set<String> ONETIMESALE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("itemId","chargedatas","itemDatas",
	            "units","unitPrice","saleDate","totalprice","quantity"));
	    
	    private static final Set<String> ITEM_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("itemId","chargedatas","unitData",
	            "itemclassData","chargeCode","unit","warranty","itemDescription","itemCode","unitPrice"));
	    


	    private static final Set<String> PAYMENT_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id","paymentTypes"));




	    private static final Set<String> PLAN_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "planCode", "plan_description", "startDate",
	            "endDate", "status", "service_code", "service_description", "charge_code", "charge_description","servicedata","contractPeriod",
	            "service_type", "charge_type", "allowedtypes","selectedservice","bill_rule","billiingcycle","servicedata","services","statusname","planstatus"));

	    private static final Set<String> PRICING_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("planCode","planId","serviceId","chargeId","price",
			"chargeVariantId","discountId","planCode","id", "serviceData","chargeData","data", "chargeCode","chargeVaraint","price"));



	    private static final Set<String> DISCOUNT_MASTER_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id","discountCode","discountDescription","discounType","discountValue"));
	    private static final Set<String> DISCOUNT_MASTER_DATA_PARAMETERS_TEMPLETE=new HashSet<String>(Arrays.asList("discountCode","discountOptions"));
	private static final Set<String> SERVICE_MASTER_DATA_PARAMETERS_TEMPLETE=new HashSet<String>(Arrays.asList("id","serviceType","serviceCode","serviceDescription"));
	private static final Set<String> TAX_MASTER_DATA_PARAMETERS_TEMPLETE=new HashSet<String>(Arrays.asList("id","taxType","taxMasterOptions"));
	private static final Set<String> TRANSACTIONAL_DATA_PARAMETERS_TEMPLETE=new HashSet<String>(Arrays.asList("transactionId","transactionDate","transactionType","amount",
			"invoiceId","chrageAmount","taxAmount","chargeType","amount","billDate","dueDate","id","transaction","chargeStartDate","chargeEndDate"));
	private static final Set<String> TAX_MAPPING_RATE_DATA_PARAMETERS_TEMPLETE=new HashSet<String>(Arrays.asList("id","taxCode","taxMasterOptions"));
	
	private static final Set<String> TICKET_MASTER_DATA_PARAMETERS_TEMPLETE=new HashSet<String>(Arrays.asList("id","priority","statusType","priorityType",
			"status","assignedTo","ticketDate","problemsDatas","usersData","userName","lastComment","masterData"));
	    private final GoogleGsonSerializerHelper helper;

	    @Autowired
	    public GoogleGsonPortfolioApiJsonBillingSerializerService(final GoogleGsonSerializerHelper helper) {
	        this.helper = helper;
	    }



	    @Override
	    public String serializeSubscriptionToJson(final boolean prettyPrint, final Set<String> responseParameters,
	            final Collection<SubscriptionData> products) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(
	                SUBSCRIPTION_DATA_PARAMETERS, prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, products.toArray(new SubscriptionData[products.size()]));
	    }

	    @Override
	    public String serializeServiceToJson(final boolean prettyPrint, final Set<String> responseParameters,
	            final ServiceData products) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(
	         SERVICE_DATA_PARAMETERS, prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, products);
	    }

	    @Override
	    public String serializeOrderToJson(final boolean prettyPrint, final Set<String> responseParameters,
	           OrderData products) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(
	                ORDER_DATA_PARAMETERS, prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, products);
	    }

	    @Override
	    public String serializeBillingCycleToJson(final boolean prettyPrint, final Set<String> responseParameters,
	            final BillingCycleData products) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(
	         BILLINGCYCLE_DATA_PARAMETERS, prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, products);
	    }

	    @Override public String serializePaymodeToJson(final boolean prettyPrint, final Set<String> responseParameters,
	            final PaymodeData data) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(
	         PAYMODE_DATA_PARAMETERS, prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, data);
	    }

	    @Override
	    public String serializeSubscriptionToJson(final boolean prettyPrint, final Set<String> responseParameters,
	            final SubscriptionData products) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(
	                SUBSCRIPTION_DATA_PARAMETERS, prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, products);
	    }

	    @Override
	    public String serializePaytermsToJson(final boolean prettyPrint, final Set<String> responseParameters,
	            final SubscriptionData products) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(
	                SUBSCRIPTION_DATA_PARAMETERS, prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, products);
	    }



	    @Override
	    public String serializeClientOrderDataToJson(final boolean prettyPrint, final Set<String> responseParameters,
	            final List<OrderData> clientAccount) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(ORDER_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, clientAccount.toArray());
	    }

	    @Override
	    public String serializePricingDataToJson(final boolean prettyPrint, final Set<String> responseParameters,
	            final PricingData clientAccount) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(PRICING_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, clientAccount);
	    }

	    @Override
	public String serializePlanDataToJson(boolean prettyPrint,	Set<String> responseParameters, List<PlanData> products)
	    {
	     final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(PLAN_DATA_PARAMETERS,
	                 prettyPrint, responseParameters);
	         return helper.serializedJsonFrom(gsonDeserializer, products);

	    }







	    @Override
	    public String serializePlanDataToJson(boolean prettyPrint, Set<String> responseParameters, PlanData account) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(PLAN_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, account);
	    }



	    @Override
	public String serializeDiscountMasterDataToJson(boolean prettyPrint,Set<String> responseParameters, DiscountMasterData discountMasterData)
	{
	final Gson gsonDeserializer = helper
	.createGsonBuilderWithParameterExclusionSerializationStrategy(
	DISCOUNT_MASTER_DATA_PARAMETERS, prettyPrint, responseParameters);
	return helper.serializedJsonFrom(gsonDeserializer, discountMasterData);
	}
	@Override
	public String serializeDiscountMasterDataToJson(boolean prettyPrint,Set<String> responseParameters, Collection<DiscountMasterData> discountMasterData)
	{
	final Gson gsonDeserializer = helper
	.createGsonBuilderWithParameterExclusionSerializationStrategy(
	DISCOUNT_MASTER_DATA_PARAMETERS, prettyPrint, responseParameters);
	return helper.serializedJsonFrom(gsonDeserializer, discountMasterData.toArray(new DiscountMasterData[discountMasterData.size()]));
	}
	@Override
	public String serializeDiscountMasterDataToJsonTemplete(boolean prettyPrint,Set<String> responseParameters, AdjustmentCodeData discountMasterData)
	{
	final Gson gsonDeserializer = helper
	.createGsonBuilderWithParameterExclusionSerializationStrategy(
	DISCOUNT_MASTER_DATA_PARAMETERS_TEMPLETE, prettyPrint, responseParameters);
	return helper.serializedJsonFrom(gsonDeserializer, discountMasterData);
	}

	@Override
	public String serializeDiscountMasterDataToJsonTemplete(boolean prettyPrint,Set<String> responseParameters, Collection<Discount> datass)
	{
	final Gson gsonDeserializer = helper
	.createGsonBuilderWithParameterExclusionSerializationStrategy(
	DISCOUNT_MASTER_DATA_PARAMETERS_TEMPLETE, prettyPrint, responseParameters);
	return helper.serializedJsonFrom(gsonDeserializer, datass);
	}

	@Override
	public String serializeServiceMasterDataToJson(boolean prettyPrint,Set<String> responseParameters, Collection<SericeMasterOptionsData> serviceMasterData)
	{
	final Gson gsonDeserializer = helper
	.createGsonBuilderWithParameterExclusionSerializationStrategy(
	SERVICE_MASTER_DATA_PARAMETERS_TEMPLETE, prettyPrint, responseParameters);
	return helper.serializedJsonFrom(gsonDeserializer, serviceMasterData.toArray(new SericeMasterOptionsData[serviceMasterData.size()]));
	}

	@Override
	public String serializeTaxMasterDataToJson(boolean prettyPrint,Set<String> responseParameters, Collection<TaxMasterDataOptions> taxMasterData)
	{
	final Gson gsonDeserializer = helper
	.createGsonBuilderWithParameterExclusionSerializationStrategy(
	TAX_MASTER_DATA_PARAMETERS_TEMPLETE, prettyPrint, responseParameters);
	return helper.serializedJsonFrom(gsonDeserializer, taxMasterData.toArray(new TaxMasterDataOptions[taxMasterData.size()]));
	}

	@Override
	public String serializeTaxMappingRateTemplateDataToJson(boolean prettyPrint,Set<String> responseParameters, Collection<TaxMappingRateOptionsData> taxMappingRateOptionsData)
	{
	final Gson gsonDeserializer = helper
	.createGsonBuilderWithParameterExclusionSerializationStrategy(
	TAX_MAPPING_RATE_DATA_PARAMETERS_TEMPLETE, prettyPrint, responseParameters);
	return helper.serializedJsonFrom(gsonDeserializer, taxMappingRateOptionsData.toArray(new TaxMappingRateOptionsData[taxMappingRateOptionsData.size()]));
	}

	@Override
	public String serializeOrderToJson(boolean prettyPrint,
	Set<String> responseParameters, List<OrderData> data) {
	final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(
	                ORDER_DATA_PARAMETERS, prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, data);
	}

	@Override
	public String serializeTransactionalDataToJson(boolean prettyPrint,
	Set<String> responseParameters,
	FinancialTransactionsData data) {
	final Gson gsonDeserializer = helper
	.createGsonBuilderWithParameterExclusionSerializationStrategy(
	TRANSACTIONAL_DATA_PARAMETERS_TEMPLETE, prettyPrint, responseParameters);
	return helper.serializedJsonFrom(gsonDeserializer, data);
	}



	 @Override
	    public String serializePaymentToJson(final boolean prettyPrint, final Set<String> responseParameters,
	            final LoanBasicDetailsData loanAccount,PaymodeData paymentypes) {
	        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(PAYMENT_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, paymentypes);
	    }



	@Override
	public String serializeServiceDataToJson(boolean prettyPrint,
			Set<String> responseParameters, SericeMasterOptionsData productData) {
		 final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(SERVICE_MASTER_DATA_PARAMETERS_TEMPLETE,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, productData);
	}



	@Override
	public String serializeTicketMasterToJson(boolean prettyPrint,
			Set<String> responseParameters, TicketMasterData templateData) {
		 final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(TICKET_MASTER_DATA_PARAMETERS_TEMPLETE,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, templateData);
	}



	@Override
	public String serializeTicketMasterToJson(boolean prettyPrint,
			Set<String> responseParameters, List<TicketMasterData> data) {
		  final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(TICKET_MASTER_DATA_PARAMETERS_TEMPLETE,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, data.toArray());
	}



	@Override
	public String serializePaymodeToJson(boolean prettyPrint,
			Set<String> responseParameters, Collection<PaymodeData> data) {
		 final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(PAYMODE_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, data.toArray());
	}



	@Override
	public String serializePaymodeToJson(boolean prettyPrint,
			Set<String> responseParameters,
			LoanBasicDetailsData loanBasicDetails, Collection<PaymodeData> data) {
		  final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(PAYMENT_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, data.toArray());
	}



	@Override
	public String serializePaymodeToJson(boolean prettyPrint,
			Set<String> responseParameters,
			LoanBasicDetailsData loanBasicDetails, PaymodeData datas) {
		  final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(PAYMENT_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, datas);
	}



	@Override
	public String serializeServiceMasterDataToJson(boolean prettyPrint,
			Set<String> responseParameters, SericeMasterOptionsData productData) {
		  final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(SERVICE_MASTER_DATA_PARAMETERS_TEMPLETE,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, productData);
	}



	@Override
	public String serializeClientOrderPriceDataToJson(boolean prettyPrint,
			Set<String> responseParameters, Long clientId, OrderData data) {

        final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(ORDER_DATA_PARAMETERS,
                prettyPrint, responseParameters);
        return helper.serializedJsonFrom(gsonDeserializer, data);
    
	}



	@Override
	public String serializeTransactionalDataToJson(boolean prettyPrint,
			Set<String> responseParameters,
			List<FinancialTransactionsData> transactionData) {
		
			  final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(TRANSACTIONAL_DATA_PARAMETERS_TEMPLETE,
		                prettyPrint, responseParameters);
		        return helper.serializedJsonFrom(gsonDeserializer, transactionData.toArray());
	}



	@Override
	public String serializeClientTicketDataToJson(boolean prettyPrint,
			Set<String> responseParameters, List<ClientTicketData> data) {
		 final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(TICKET_MASTER_DATA_PARAMETERS_TEMPLETE,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, data.toArray());
	}



	@Override
	public String serializeDepositAddressDataToJson(boolean prettyPrint,
			Set<String> responseParameters, List<AddressData> addressdata) {
		 final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(ADDRESS_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, addressdata.toArray());
	}



	@Override
	public String serializeDepositAddressDataToJson(boolean prettyPrint,
			Set<String> responseParameters, AddressData addressdata) {
		 final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(ADDRESS_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, addressdata);
	}



	@Override
	public String serializeOneTimeSaleDataToJson(boolean prettyPrint,
			Set<String> responseParameters, OneTimeSaleData data) {
		 final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(ONETIMESALE_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, data);
	}



	@Override
	public String serializeOneTimeSaleDataToJson(boolean prettyPrint,
			Set<String> responseParameters, List<OneTimeSaleData> salesData) {
		 final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(ONETIMESALE_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, salesData.toArray());
	}



	@Override
	public String serializeItemDataToJson(boolean prettyPrint,
			Set<String> responseParameters, ItemData itemData) {
			 final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(ITEM_DATA_PARAMETERS,
		                prettyPrint, responseParameters);
		        return helper.serializedJsonFrom(gsonDeserializer,itemData);
	}



	@Override
	public String serializeItemDataToJson(boolean prettyPrint,
			Set<String> responseParameters, List<ItemData> itemData) {
		 final Gson gsonDeserializer = helper.createGsonBuilderWithParameterExclusionSerializationStrategy(ITEM_DATA_PARAMETERS,
	                prettyPrint, responseParameters);
	        return helper.serializedJsonFrom(gsonDeserializer, itemData.toArray());
	}



	






	








	}

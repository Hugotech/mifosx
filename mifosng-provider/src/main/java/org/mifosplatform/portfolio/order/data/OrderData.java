package org.mifosplatform.portfolio.order.data;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.payterms.data.PaytermData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.subscription.data.SubscriptionData;



public class OrderData {
	private  Long id;
	private  Long pdid;
	private  Long pcid;
	private Long clientId;
	private  String service_code;
	private  String plan_code;
	private String chargeCode;
	private  double price;
	private String variant;
	private  String status;
	private  Long period;
	private LocalDate startDate;
	private LocalDate currentDate;
	private LocalDate endDate;
	private String billingFrequency;
	private  List<PlanCodeData> plandata;
	private  List<PaytermData> paytermdata;
	private  List<SubscriptionData> subscriptiondata;
	private List<OrderPriceData> orderPriceData;
	private String cancelledStatus;
	private String contractPeriod;
	private boolean flag;
	public OrderData(List<PlanCodeData> allowedtypes, List<PaytermData> data, List<SubscriptionData> subscription)
	{
		this.id=null;
		this.plan_code=null;
		this.pcid=null;
		this.pdid=null;
		this.service_code=null;
	 this.startDate=new LocalDate();
		this.variant=null;
		this.chargeCode=null;
		this.paytermdata=data;
		this.plandata=allowedtypes;
		this.subscriptiondata=subscription;
		this.status=null;
		this.period=null;
	}


	public OrderData(List<PlanCodeData> allowedtypes, List<PaytermData> data1,
			List<SubscriptionData> contractPeriod, OrderData data) {

		this.id=data.getId();
		this.pdid=data.getPdid();
		this.plan_code=data.getPlan_code();
		this.status=null;
		this.period=data.getPeriod();
		this.pcid=data.getPcid();
		this.service_code=null;

		this.startDate=data.getStartDate();
		this.variant=null;
		this.chargeCode=null;
		this.paytermdata=data1;
		this.plandata=allowedtypes;
		this.subscriptiondata=contractPeriod;

	}

	public OrderData(Long id, Long planId, String plancode,
			String status, LocalDate startDate, LocalDate endDate,
			double price, String contractPeriod) {
		this.id=id;
		this.pdid=planId;
		this.plan_code=plancode;
		this.status=status;
		this.cancelledStatus="CANCELLED";
		this.period=null;
		this.startDate=startDate;
		this.currentDate=new LocalDate();
		this.endDate=endDate;
		this.pcid=null;
		this.service_code=null;
		this.price=price;
		this.variant=null;
		this.chargeCode=null;
		this.paytermdata=null;
		this.plandata=null;
		this.subscriptiondata=null;
		this.contractPeriod=contractPeriod;
		
		if(startDate.equals(currentDate))
		{
			this.flag=true;
		}
		
	}

	public OrderData(Long id, Long orderId, Long serviceId, Long status,String serviceType) {

		this.id=id;
		this.pdid=orderId;
		this.plan_code=null;
		this.status=null;
		this.period=status;
		this.startDate=null;
		this.endDate=null;
		this.pcid=serviceId;
		this.service_code=serviceType;
		this.billingFrequency=serviceType;
		this.variant=null;
		this.chargeCode=null;
		this.paytermdata=null;
		this.plandata=null;
		this.subscriptiondata=null;

	}

	public OrderData(List<OrderPriceData> priceDatas) {
		this.orderPriceData=priceDatas;
	
	}

	public Long getId() {
		return id;
	}

	public Long getPdid() {
		return pdid;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public List<OrderPriceData> getOrderPriceData() {
		return orderPriceData;
	}

	public Long getPcid() {
		return pcid;
	}

	public String getService_code() {
		return service_code;
	}



	public LocalDate getEndDate() {
		return endDate;
	}

	public String getStatus() {
		return status;
	}

	public Long getPeriod() {
		return period;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public String getPlan_code() {
		return plan_code;
	}

	public double getPrice() {
		return price;
	}

	public String getVariant() {
		return variant;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public List<PlanCodeData> getPlandata() {
		return plandata;
	}

	public List<PaytermData> getPaytermdata() {
		return paytermdata;
	}

	public List<SubscriptionData> getSubscriptiondata() {
		return subscriptiondata;
	}
public void setPaytermData(List<PaytermData> data)
{
this.paytermdata=data;
}
}

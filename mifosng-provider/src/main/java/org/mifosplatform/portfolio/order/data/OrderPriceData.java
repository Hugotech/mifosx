package org.mifosplatform.portfolio.order.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDate;

public class OrderPriceData {

	private final Long id;
	private final Long orderId;
	private final Long serviceId;
	private final String chargeCode;
	private final String chargeType;
	private final String chargeDuration;
	private final String durationType;
	private final BigDecimal price;

	private LocalDate invoiceTillDate;
	private Long createdBy;
	private Date createdDate;
	private Date lastModifiedDate;
	private Long lastModifiedBy;
	private Long clientId;
	private LocalDate billStartDate;
	private LocalDate billEndDate;
	private LocalDate nextBillDate;

	public OrderPriceData(Long id, Long clientId, Long serviceId,
			String chargeCode, String chargeType, String chargeDuration,
			String durationtype, BigDecimal price, LocalDate billStartDate, LocalDate billEndDate, LocalDate nextBillDate, LocalDate invoiceTillDate) {

		this.id=id;
		this.orderId=clientId;
		this.clientId=serviceId;
		this.chargeCode=chargeCode;
		this.chargeType=chargeType;
		this.chargeDuration=chargeDuration;
		this.durationType=durationtype;
		this.price=price;
		this.billStartDate=billStartDate;
		this.billEndDate=billEndDate;
		this.nextBillDate=nextBillDate;
		this.serviceId=null;
		this.invoiceTillDate=invoiceTillDate;


	}



	public OrderPriceData(Long id, Long orderId, Long serviceId,
			String chargeCode, String chargeType, String chargeDuration,
			String durationType, Date invoiceTillDate, BigDecimal price,
			Long createdbyId, Date createdDate, Date lastModefiedDate,
			Long lastModefiedId) {
		this.id=id;
		this.orderId=orderId;
		this.serviceId=serviceId;
		this.chargeCode=chargeCode;
		this.chargeType=chargeType;
		this.chargeDuration=chargeDuration;
		this.durationType=durationType;
		this.price=price;
	}



	



	



	public Long getId() {
		return id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public String getChargeType() {
		return chargeType;
	}

	public String getChargeDuration() {
		return chargeDuration;
	}

	public String getDurationType() {
		return durationType;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public LocalDate getInvoiceTillDate() {
		return invoiceTillDate;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public Long getLastModifiedBy() {
		return lastModifiedBy;
	}


}

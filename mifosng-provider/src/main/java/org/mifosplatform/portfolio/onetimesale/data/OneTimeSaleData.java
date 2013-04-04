package org.mifosplatform.portfolio.onetimesale.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.charge.data.ChargesData;

public class OneTimeSaleData {
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	private List<ChargesData> chargesDatas;
	private List<ItemData> itemDatas;
	private Long itemId;
	private Long id;
	private String units;
	private String itemCode;
	private String chargeCode;
	private String quantity;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	private LocalDate saleDate;
	private Long clientId;
	private String isInvoiced;
	

	public OneTimeSaleData(List<ChargesData> chargeDatas,
			List<ItemData> itemData) {
		
		this.chargesDatas=chargeDatas;
		this.itemDatas=itemData;
		
	}


	public OneTimeSaleData(Long id, LocalDate saleDate, String itemCode,
			String chargeCode, String quantity, BigDecimal totalPrice) {
		this.id=id;
		this.saleDate=saleDate;
		this.itemCode=itemCode;
		this.chargeCode=chargeCode;
		this.quantity=quantity;
		this.totalPrice=totalPrice;
		
	}


	public OneTimeSaleData(Long oneTimeSaleId, Long clientId, String units,
			String chargeCode, BigDecimal unitPrice, String quantity,
			BigDecimal totalPrice, String isInvoiced, Long itemId) {
		this.id = oneTimeSaleId;
		this.clientId = clientId;
		this.units = units;
		this.chargeCode = chargeCode;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.isInvoiced = isInvoiced;
		this.itemId = itemId;
	}


	public List<ChargesData> getChargesDatas() {
		return chargesDatas;
	}


	public List<ItemData> getItemDatas() {
		return itemDatas;
	}


	public Long getItemId() {
		return itemId;
	}


	public String getUnits() {
		return units;
	}


	public String getChargeCode() {
		return chargeCode;
	}


	public String getQuantity() {
		return quantity;
	}


	public BigDecimal getUnitPrice() {
		return unitPrice;
	}


	public BigDecimal getTotalPrice() {
		return totalPrice;
	}


	public LocalDate getSaleDate() {
		return saleDate;
	}


	public String getIsInvoiced() {
		return isInvoiced;
	}


	public void setIsInvoiced(String isInvoiced) {
		this.isInvoiced = isInvoiced;
	}


	public Long getClientId() {
		return clientId;
	}


	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	

}

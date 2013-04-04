package org.mifosplatform.portfolio.item.command;

import java.math.BigDecimal;
import java.util.Set;

public class ItemCommand {

	private Long itemId;
	private String itemCode;
	private String itemDescription;
	private String itemClass;
	private String chargeCode;
	private String units;
	private Long warranty;
	private BigDecimal unitPrice;
	private Set<String> modifiedParameters;

	public ItemCommand(Set<String> modifiedParameters, String itemCode,
			String itemDescription, String itemClass, String chargeCode,
			BigDecimal unitPrice, Long warranty,String units) {
		this.modifiedParameters=modifiedParameters;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.itemClass = itemClass;
		this.chargeCode = chargeCode;
		this.warranty = warranty;
		this.unitPrice = unitPrice;
		this.units=units;

	}

	public Long getItemId() {
		return itemId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public String getUnits() {
		return units;
	}

	public String getItemClass() {
		return itemClass;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public Long getWarranty() {
		return warranty;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public boolean isItemCodeChanged() {
		return this.modifiedParameters.contains("itemCode");
	}

	public boolean isItemDescriptionChanged() {
		return this.modifiedParameters.contains("itemDescription");
	}
	public boolean isItemClassChanged() {
		return this.modifiedParameters.contains("itemClass");
	}
	public boolean ischargeCodeChanged() {
		return this.modifiedParameters.contains("chargeCode");
	}

	public boolean isWarrantyChanged() {
		return this.modifiedParameters.contains("warranty");
	}

	public boolean isunitPriceChanged() {
		return this.modifiedParameters.contains("unitPrice");
	}

	public boolean isUnitsChanged() {
		return this.modifiedParameters.contains("units");
	}

	
}

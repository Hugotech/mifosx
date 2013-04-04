package org.mifosplatform.portfolio.itemmaster.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.portfolio.item.command.ItemCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "item_master")
public class ItemMaster extends AbstractPersistable<Long>{


	@Column(name = "item_code")
	private String itemCode;

	@Column(name = "unit_price")
	private BigDecimal unitPrice;
	
	@Column(name = "item_description")
	private String itemDescription;

	@Column(name = "item_class")
	private String itemClass;
	
	@Column(name = "units")
	private String units;
	
	@Column(name = "charge_code")
	private String chargeCode;

	
	@Column(name = "warranty")
	private Long warranty;

	@Column(name = "is_deleted", nullable = false)
	private char deleted = 'n';
	
	public ItemMaster(){}
	
	public ItemMaster(String itemCode, String itemDescription,
			String itemClass, BigDecimal unitPrice, String units,
			Long warranty, String chargeCode) {
             this.itemCode=itemCode;
             this.itemDescription=itemDescription;
             this.itemClass=itemClass;
             this.chargeCode=chargeCode;
             this.units=units;
             this.warranty=warranty;
             this.unitPrice=unitPrice;
             
	
	
	}

	public String getItemCode() {
		return itemCode;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public String getItemClass() {
		return itemClass;
	}

	public String getUnits() {
		return units;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	
	public Long getWarranty() {
		return warranty;
	}

	public char getDeleted() {
		return deleted;
	}

	

	public void update(ItemCommand command) {
		if(command.isItemCodeChanged())
			this.itemCode=command.getItemCode();
		if(command.isItemDescriptionChanged())
			this.itemDescription=command.getItemDescription();
		if(command.isItemClassChanged())
			this.itemClass=command.getItemClass();
		if(command.isUnitsChanged())
			this.units=command.getUnits();
		if(command.isunitPriceChanged())
			this.unitPrice=command.getUnitPrice();
		if(command.ischargeCodeChanged())
			this.chargeCode=command.getChargeCode();
		if(command.isWarrantyChanged())
			this.warranty=command.getWarranty();
		
	}

	public void delete() {
		this.deleted='Y';
		
	}
	
	
	
	

}

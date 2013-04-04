package org.mifosplatform.portfolio.address.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mifosplatform.portfolio.address.command.AddressCommand;

@Entity
@Table(name = "client_address")
public class Address{


	@Id
	@GeneratedValue
	@Column(name="address_id")
	private Long id;

	@Column(name = "client_id", length = 65536)
	private Long clientId;

	@Column(name = "address_no")
	private String addressNo;
	
	@Column(name = "address_key")
	private String addressKey;

	@Column(name = "street")
	private String street;
	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(name = "country")
	private String country;

	@Column(name = "zip")
	private String zip;

	@Column(name = "is_deleted", nullable = false)
	private char deleted='n';

	

	public Address() {
         
          
	}



	public Address(Long clientId,String addressKey, String addressNo, String street,
			String city, String state, String country, String zip) {
		
		
		 this.clientId=clientId;
         this.addressNo=addressNo;
         this.street=addressKey;
         this.addressKey="addr1";
         this.city=city;
         this.state=state;
         this.country=country;
         this.zip=zip;
	}



	public void update(AddressCommand command) {
	   if(command.isAddressKeyChanged())
		   this.addressKey=command.getAddressKey();
	   if(command.isAddressNOChanged())
		   this.addressNo=command.getAddressNo();
	   if(command.isStreetChanged())
		   this.street=command.getStreet();
	   if(command.isCityChanged())
		   this.city=command.getCity();
	   if(command.isStateChanged())
		   this.state=command.getState();
	   if(command.isCountryChanged())
		   this.country=command.getCountry();
	   if(command.isZipChanged())
		   this.zip=command.getZip();
		
		
		
	}
	
	}
	
	
		// TODO Auto-generated constructor stub
	

	



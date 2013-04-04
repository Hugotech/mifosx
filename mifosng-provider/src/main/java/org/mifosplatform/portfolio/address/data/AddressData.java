package org.mifosplatform.portfolio.address.data;

import java.util.List;

public class AddressData {
	
	private Long id;
	private Long clientId;
	private String addressKey;
	private String addressNo;
	private String street;
	private String zip;
	private String city;
	private String state;
	private String country;
	private List<AddressData> datas;
	private List<String> countryData,stateData,cityData; 
	
	

	public AddressData(Long addressId, Long clientId, String addressKey,
			String addressNo, String street, String zip, String city,
			String state, String country) {
     
		this.id=addressId;
		this.addressKey=addressKey;
		this.clientId=clientId;
		this.addressNo=addressNo;
		this.street=street;
		this.zip=zip;
		this.city=city;
		this.state=state;
		this.country=country;
	
	
	}



	public AddressData(List<AddressData> addressdata, List<String> countryData, List<String> statesData, List<String> citiesData) {
	this.datas=addressdata;
	this.countryData=countryData;
	this.stateData=statesData;
	this.cityData=citiesData;
	}



	public Long getAddressId() {
		return id;
	}



	public Long getClientId() {
		return clientId;
	}



	public String getAddressKey() {
		return addressKey;
	}



	public String getAddressNo() {
		return addressNo;
	}



	public String getStreet() {
		return street;
	}



	public String getZip() {
		return zip;
	}



	public String getCity() {
		return city;
	}



	public String getState() {
		return state;
	}



	public String getCountry() {
		return country;
	}
	
	

}

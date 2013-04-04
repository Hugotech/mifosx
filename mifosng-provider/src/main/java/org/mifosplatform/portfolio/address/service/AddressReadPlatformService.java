package org.mifosplatform.portfolio.address.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.address.command.AddressCommand;
import org.mifosplatform.portfolio.address.data.AddressData;

public interface AddressReadPlatformService {


	List<AddressData> retrieveSelectedAddressDetails(String selectedname);

	List<AddressData> retrieveAddressDetails(Long clientId);

	List<AddressData> retrieveAddressDetails();

	List<String> retrieveCountryDetails();

	List<String> retrieveStateDetails();

	List<String> retrieveCityDetails();

	

	
	

}


package org.mifosplatform.portfolio.onetimesale.service;

import java.util.List;

import org.mifosplatform.portfolio.onetimesale.data.ItemData;
import org.mifosplatform.portfolio.onetimesale.data.OneTimeSaleData;

public interface OneTimeSaleReadPlatformService {

	List<ItemData> retrieveItemData();

	List<OneTimeSaleData> retrieveClientOneTimeSalesData(Long clientId);

	List<OneTimeSaleData> retrieveOnetimeSaleDate(Long clientId);

	OneTimeSaleData retrieveSingleOneTimeSaleDetails(Long saleId);


}

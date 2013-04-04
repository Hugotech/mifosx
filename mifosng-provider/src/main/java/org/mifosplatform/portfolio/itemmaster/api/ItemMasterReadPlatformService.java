package org.mifosplatform.portfolio.itemmaster.api;

import java.util.List;

import org.mifosplatform.portfolio.onetimesale.data.ItemData;
import org.mifosplatform.portfolio.onetimesale.data.OneTimeSaleData;

public interface ItemMasterReadPlatformService {

	ItemData retrieveSingleItem(Long itemId);

}

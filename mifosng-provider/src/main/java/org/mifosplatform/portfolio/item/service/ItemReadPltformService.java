package org.mifosplatform.portfolio.item.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.charge.data.ChargesData;
import org.mifosplatform.portfolio.onetimesale.data.ItemData;

public interface ItemReadPltformService {

	List<EnumOptionData> retrieveItemClassType();

	List<EnumOptionData> retrieveUnitTypes();

	List<ChargesData> retrieveChargeCode();

	List<ItemData> retrieveAllItems();

	ItemData retrieveSingleItemDetails(Long itemId);

}

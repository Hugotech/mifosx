package org.mifosplatform.portfolio.itemmaster.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.itemmaster.api.ItemMasterReadPlatformService;
import org.mifosplatform.portfolio.itemmaster.domain.ItemMaster;
import org.mifosplatform.portfolio.itemmaster.domain.ItemMasterRepository;
import org.mifosplatform.portfolio.onetimesale.data.ItemData;
import org.mifosplatform.portfolio.onetimesale.domain.OneTimeSale;
import org.mifosplatform.portfolio.onetimesale.domain.OneTimeSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class ItemMasterReadPlatformServiceImpl implements ItemMasterReadPlatformService{
	
	 private final JdbcTemplate jdbcTemplate;
	    private final PlatformSecurityContext context;
	    private final ItemMasterRepository itemMasterRepository;
	 
	    @Autowired
	    public ItemMasterReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource,
	    		final ItemMasterRepository itemMasterRepository) {
	        this.context = context;
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	        this.itemMasterRepository=itemMasterRepository;
	      

	    }

	@Override
	public ItemData retrieveSingleItem(Long itemId) {
		
		try
		{
			this.context.authenticatedUser();
			ItemMaster oneTimeSale=this.itemMasterRepository.findOne(itemId);
			
			return new ItemData(oneTimeSale.getId(),oneTimeSale.getChargeCode(),oneTimeSale.getUnits(),null, null, null, 0, oneTimeSale.getUnitPrice());
			
		}
		
	 catch (DataIntegrityViolationException dve) {
		handleDataIntegrityIssues(null, dve);
		return null;
	}
			}

	private void handleDataIntegrityIssues(Object object,
			DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub
		
	}

}


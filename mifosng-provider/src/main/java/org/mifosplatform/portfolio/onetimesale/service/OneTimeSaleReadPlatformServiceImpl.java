package org.mifosplatform.portfolio.onetimesale.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.onetimesale.data.ItemData;
import org.mifosplatform.portfolio.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.portfolio.ticketmaster.data.TicketMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class OneTimeSaleReadPlatformServiceImpl implements
		OneTimeSaleReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public OneTimeSaleReadPlatformServiceImpl(
			final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override
	public List<ItemData> retrieveItemData() {
		context.authenticatedUser();

		TicketDataMapper mapper = new TicketDataMapper();

		String sql = "select " + mapper.schema() + " where i.is_deleted='n'";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class TicketDataMapper implements RowMapper<ItemData> {

		public String schema() {
			return "i.id AS id,i.item_description AS itemCode,i.units AS units,i.unit_price AS unitPrice FROM item_master i  ";

		}

		@Override
		public ItemData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			String itemCode = rs.getString("itemCode");
			String units = rs.getString("units");
			BigDecimal unitPrice = rs.getBigDecimal("unitPrice");

			return new ItemData(id, itemCode, units, units, units, null, 0,
					unitPrice);

		}

	}

	@Override
	public List<OneTimeSaleData> retrieveClientOneTimeSalesData(Long clientId) {
		context.authenticatedUser();

		SalesDataMapper mapper = new SalesDataMapper();

		String sql = "select " + mapper.schema()
				+ " where o.item_id=i.id  and o.client_id=? ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}

	private static final class SalesDataMapper implements
			RowMapper<OneTimeSaleData> {

		public String schema() {
			return "o.id AS id,i.item_code AS itemCode,o.sale_date as saleDate,o.charge_code AS chargeCode,"
					+ "o.quantity as quantity,o.total_price as totalPrice  FROM item_master i,onetime_sale o";

		}

		@Override
		public OneTimeSaleData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			LocalDate saleDate = JdbcSupport.getLocalDate(rs, "saleDate");
			String itemCode = rs.getString("itemCode");
			String chargeCode = rs.getString("chargeCode");
			String quantity = rs.getString("quantity");
			BigDecimal totalPrice = rs.getBigDecimal("totalPrice");

			return new OneTimeSaleData(id, saleDate, itemCode, chargeCode,
					quantity, totalPrice);

		}

	}
	
	
	@Override
	public List<OneTimeSaleData> retrieveOnetimeSaleDate(Long clientId) {
		context.authenticatedUser();

		OneTimeSalesDataMapper mapper = new OneTimeSalesDataMapper();

		String sql = "select " + mapper.schema()
				+ " where ots.client_id = ? ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}

	private static final class OneTimeSalesDataMapper implements
			RowMapper<OneTimeSaleData> {

		public String schema() {
			return " ots.id as oneTimeSaleId, ots.client_id AS clientId,ots.units AS units,ots.charge_code AS chargeCode,ots.unit_price AS unitPrice,"
				 + " ots.quantity AS quantity,ots.total_price AS totalPrice,ots.is_invoiced as isInvoiced,ots.item_id as itemId FROM onetime_sale ots ";

		}

		@Override
		public OneTimeSaleData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long oneTimeSaleId = rs.getLong("oneTimeSaleId");
			Long clientId = rs.getLong("clientId");
			String units = rs.getString("units");
			String chargeCode = rs.getString("chargeCode");
			BigDecimal unitPrice = rs.getBigDecimal("unitPrice");
			String quantity = rs.getString("quantity");
			BigDecimal totalPrice = rs.getBigDecimal("totalPrice");
			String isInvoiced = rs.getString("isInvoiced");
			Long itemId = rs.getLong("itemId");
			return new OneTimeSaleData(oneTimeSaleId,clientId, units, chargeCode, unitPrice,quantity, totalPrice,isInvoiced,itemId);

		}

	}
}

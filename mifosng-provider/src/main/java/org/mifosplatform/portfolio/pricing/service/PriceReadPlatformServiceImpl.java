package org.mifosplatform.portfolio.pricing.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.data.ChargesData;
import org.mifosplatform.portfolio.discountmaster.data.DiscountMasterData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.service.ChargeVariant;
import org.mifosplatform.portfolio.pricing.data.PricingData;
import org.mifosplatform.portfolio.pricing.data.SavingChargeVaraint;
import org.mifosplatform.portfolio.subscription.data.SubscriptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class PriceReadPlatformServiceImpl implements PriceReadPlatformService{


	 private final JdbcTemplate jdbcTemplate;
	    private final PlatformSecurityContext context;

	    @Autowired
	    public PriceReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
	        this.context = context;
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	    }


	@Override
	public List<PlanData> retrievePlanDetails() {

		  context.authenticatedUser();

	        String sql = "select s.id as id,s.plan_code as plan_code from plan_master s ";


	        RowMapper<PlanData> rm = new PlanMapper();

	        return this.jdbcTemplate.query(sql, rm, new Object[] { });
	}


	 private static final class PlanMapper implements RowMapper<PlanData> {

	        @Override
	        public PlanData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

	        Long id = rs.getLong("id");
	            String plan_code = rs.getString("plan_code");


	            return new PlanData(id,plan_code);
	        }
}


		@Override
		public List<SubscriptionData> retrievePaytermData() {

			context.authenticatedUser();

			SubscriptionDataMapper mapper = new SubscriptionDataMapper();

			String sql = "select " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		}

		private static final class SubscriptionDataMapper implements
				RowMapper<SubscriptionData> {

			public String schema() {
				return " sb.id as id,sb.payterm_type as payterm_type,sb.units as units "
						+ " from m_payments sb ";

			}

			@Override
			public SubscriptionData mapRow(ResultSet rs, int rowNum)
					throws SQLException {

				Long id = rs.getLong("id");
				String payterm_type = rs.getString("payterm_type");
				String units = rs.getString("units");
				String contractPeriod = units.concat(payterm_type);
				SubscriptionData data = new SubscriptionData(id, contractPeriod);

				return data;

			}

		}

	 @Override
		public List<ServiceData> retrievePrcingDetails(Long planId) {

			  context.authenticatedUser();

		        String sql = "SELECT sm.id AS id,sm.service_description AS service_description,p.plan_code as planCode,"
				     +" pm.service_code AS service_code   FROM plan_detail pm, service sm,plan_master p"
					 +" WHERE pm.service_code = sm.service_code AND p.id = pm.plan_id and sm.is_deleted ='n' and  pm.plan_id=?";


		        RowMapper<ServiceData> rm = new PeriodMapper();

		        return this.jdbcTemplate.query(sql, rm, new Object[] { planId });
		}


		 private static final class PeriodMapper implements RowMapper<ServiceData> {

		        @Override
		        public ServiceData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

		        Long id = rs.getLong("id");
		            String planCode = rs.getString("planCode");
		            String serviceCode = rs.getString("service_code");
		            String serviceDescription = rs.getString("service_description");
		            return new ServiceData(id,null,planCode,serviceCode,serviceDescription,null);
		        }
	}

		 @Override
			public List<ServiceData> retrievePriceDetails(String planCode) {

				  context.authenticatedUser();

			        String sql = "SELECT  p.plan_code as plan_code,pm.id as id,pm.service_code as service_code,se.service_description as service_description," +
						"c.charge_description as charge_description,pm.charge_code as charge_code,pm.charging_variant as charging_variant," +
						"pm.price as price from  plan_master p,plan_pricing  pm,service se,charge_codes c where p.id=pm.plan_code and pm.service_code = se.service_code and" +
						" pm.charge_code=c.charge_code and pm.is_deleted='n' and se.is_deleted='n' and pm.plan_code ='"+planCode+"'";


			        RowMapper<ServiceData> rm = new PriceMapper();

			        return this.jdbcTemplate.query(sql, rm, new Object[] { });
			}


			 private static final class PriceMapper implements RowMapper<ServiceData> {

			        @Override
			        public ServiceData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			        Long id = rs.getLong("id");
			            String plan_code = rs.getString("plan_code");
			            String planDescription=null;
			            String service_code = rs.getString("service_description");
			            String charge_code = rs.getString("charge_description");
			           String charging_variant=rs.getString("charging_variant");
			            BigDecimal price=rs.getBigDecimal("price");
			            int chargingVariant = new Integer(charging_variant);
			           EnumOptionData chargingvariant = SavingChargeVaraint.interestCompoundingPeriodType(chargingVariant);
			           String chargeValue=chargingvariant.getValue();
			            return new ServiceData(id,plan_code,service_code,planDescription,charge_code,chargeValue,price);
			        }
		}


	@Override
	public List<DiscountMasterData> retrieveDiscountDetails() {

		  context.authenticatedUser();

	        String sql = "select s.id as id,s.discount_code as discountcode,s.discount_description as discount_description from discount_master s";


	        RowMapper<DiscountMasterData> rm = new DiscountMapper();

	        return this.jdbcTemplate.query(sql, rm, new Object[] {});
	}


	 private static final class DiscountMapper implements RowMapper<DiscountMasterData> {

	        @Override
	        public DiscountMasterData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

	        Long id = rs.getLong("id");
	            String discountcode = rs.getString("discountcode");
	            String discountdesc = rs.getString("discount_description");


	            return new DiscountMasterData(id,discountcode,discountdesc);
	        }

	 }

	@Override
	public List<ChargesData> retrieveChargeCode() {
		 String sql = "select s.id as id,s.charge_code as charge_code,s.charge_description as charge_description from charge_codes s";


		 RowMapper<ChargesData> rm = new ChargeMapper();

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
}


 private static final class ChargeMapper implements RowMapper<ChargesData> {

        @Override
        public ChargesData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

        Long id = rs.getLong("id");
            String chargeCode = rs.getString("charge_code");
            String chargeDesc= rs.getString("charge_description");

            return new ChargesData(id,chargeCode,chargeDesc);
        }
}


@Override
public List<EnumOptionData> retrieveChargeVariantData() {



	EnumOptionData base = SavingChargeVaraint.interestCompoundingPeriodType(ChargeVariant.BASE);

	List<EnumOptionData> categotyType = Arrays.asList(base);
	return categotyType;
}

@Override

public List<ServiceData> retrieveServiceCodeDetails(Long planCode) {

	  context.authenticatedUser();

        String sql = "SELECT p.id AS planId, pm.id AS id,ch.charge_description AS chargeDescription, pm.plan_code AS plan_code,"
			+"pm.service_code AS service_code,pm.charge_code AS charge_code  FROM plan_master p, plan_pricing pm,charge_codes ch"
                 +" WHERE p.id = pm.plan_code AND  ch.charge_code = pm.charge_code and pm.is_deleted='n' and pm.plan_code="+planCode;


        RowMapper<ServiceData> rm = new ServiceMapper();

        return this.jdbcTemplate.query(sql, rm, new Object[] {  });
}


 private static final class ServiceMapper implements RowMapper<ServiceData> {

        @Override
        public ServiceData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

        Long id = rs.getLong("id");
        Long planId = rs.getLong("planId");
            String planCode = rs.getString("plan_code");
           String serviceCode = rs.getString("service_code");
            String chargeCode = rs.getString("charge_code");
            String chargeDescription = rs.getString("chargeDescription");

            return new ServiceData(id,planId,planCode,chargeCode,serviceCode,chargeDescription);
        }
}


@Override
public PricingData retrieveSinglePriceDetails(String priceId) {
	 context.authenticatedUser();

     String sql = "SELECT p.plan_code as planId,s.id AS serviceId,c.id AS chargeId,p.charging_variant AS chargeVariant, p.price AS price, p.discount_id AS discountId"
                +" FROM plan_pricing p, service s,charge_codes c, discount_master d  where  p.charge_code = c.charge_code" +
                " and p.service_code = s.service_code and p.id ="+priceId;


     RowMapper<PricingData> rm = new PricingMapper();

     return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {  });
}


private static final class PricingMapper implements RowMapper<PricingData> {

     @Override
     public PricingData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

	  Long planId = rs.getLong("planId");
     Long serviceId = rs.getLong("serviceId");
         Long chargeId = rs.getLong("chargeId");
        BigDecimal price = rs.getBigDecimal("price");
         Long discountId = rs.getLong("discountId");
         String chargeVariant = rs.getString("chargeVariant");
            int chargeVariantId=new Integer(chargeVariant);
         return new PricingData(planId,serviceId,chargeId,price,discountId,chargeVariantId);
     }
}



}

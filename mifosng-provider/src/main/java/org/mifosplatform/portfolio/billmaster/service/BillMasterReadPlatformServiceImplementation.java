package org.mifosplatform.portfolio.billmaster.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.adjustment.domain.ClientBalance;
import org.mifosplatform.portfolio.billingorder.data.BillDetailsData;
import org.mifosplatform.portfolio.billingorder.data.BillingData;
import org.mifosplatform.portfolio.financialtransaction.data.FinancialTransactionsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class BillMasterReadPlatformServiceImplementation implements
		BillMasterReadPlatformService {

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public BillMasterReadPlatformServiceImplementation(
			final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {

		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override
	public List<FinancialTransactionsData> retrieveFinancialData(Long clientId) {

		FinancialTransactionsMapper financialTransactionsMapper = new FinancialTransactionsMapper();
		String sql = "select " + financialTransactionsMapper.financialTransactionsSchema();
		return this.jdbcTemplate.query(sql, financialTransactionsMapper,
				new Object[] { clientId,clientId,clientId,clientId });

	}

	private static final class FinancialTransactionsMapper implements
			RowMapper<FinancialTransactionsData> {

		@Override
		public FinancialTransactionsData mapRow(ResultSet rs, int rowNum)

				throws SQLException {
			Long transactionId = rs.getLong("transId");
			Date transactionDate = rs.getDate("transDate");
			String transactionType = rs.getString("transType");
			BigDecimal amount = rs.getBigDecimal("amount");
			LocalDate transDate=JdbcSupport.getLocalDate(rs,"transDate");

			return new FinancialTransactionsData(transactionId,transDate,transactionType,amount);
		}

		public String financialTransactionsSchema() {

			return  	 " a.id as transId,Date_format(invoice_date,'%Y-%m-%d') transDate,'CHARGES' AS transType, charge_amount AS amount"
						+" from charge a,invoice b where a.invoice_id=b.id"
						+" and a.bill_id IS NULL AND invoice_date <= NOW() AND b.client_id = ?"
						+" union all"
						+" Select  a.id as transId,Date_format(invoice_date,'%Y-%m-%d') transDate,'TAXES' AS transType, a.tax_amount AS amount"
						+" from charge_tax a,invoice b where a.invoice_id=b.id"
						+" and a.bill_id IS NULL AND invoice_date <= NOW() AND b.client_id = ?"
						+" UNION ALL"
						+" SELECT id as transId,Date_format(adjustment_date,'%Y-%m-%d') transDate,'ADJUSTMENT' AS transType,"
						+" CASE adjustment_type"
						+" WHEN 'DEBIT' THEN adjustment_amount"
						+" WHEN 'CREDIT' THEN -adjustment_amount"
						+" END"
						+" AS amount"
						+" FROM adjustment"
						+" WHERE bill_id IS NULL AND adjustment_date <= NOW() AND client_id = ?"
						+" UNION ALL"
						+" SELECT pa.id as transId,Date_format(pa.payment_date,'%Y-%m-%d') transDate,concat('PAYMENT',' - ',p.paymode_description) AS transType,"
						+" pa.amount_paid AS invoiceAmount  "
						+"FROM payments pa,paymodes p"
						+" WHERE bill_id IS NULL AND payment_date <= NOW() AND client_id =? and pa.paymode_code = p.paymode_code";

//			return " 'INVOICE' AS billType, invocie_amount as invoiceAmount FROM invoice"
//					+ " WHERE bill_id IS NULL AND invocie_date <= NOW() AND client_id = ?"
//					+ " UNION ALL"
//					+ " SELECT 'ADJUSTMENT' AS billType,"
//					+ " CASE adjustment_type"
//					+ " WHEN 'DEBIT' THEN adjustment_amount"
//					+ " WHEN 'CREDIT' THEN -adjustment_amount"
//					+ " END AS invoiceAmount"
//					+ " FROM adjustment WHERE bill_id IS NULL AND adjustment_date <= NOW() AND client_id = ?"
//					+ " UNION ALL"
//					+ " SELECT 'PAYMENT' AS billType, amount_paid as invoiceAmount"
//					+ " FROM payments WHERE bill_id IS NULL AND payment_date <= NOW() AND client_id = ?";


		}
	}

	@Override
	public List<FinancialTransactionsData> retrieveInvoiceFinancialData(
			Long clientId) {
		FinancialInvoiceTransactionsMapper financialTransactionsMapper = new FinancialInvoiceTransactionsMapper();
		String sql = "select " + financialTransactionsMapper.financialTransactionsSchema();
		return this.jdbcTemplate.query(sql, financialTransactionsMapper,
				new Object[] { clientId,clientId,clientId });

	}

	private static final class FinancialInvoiceTransactionsMapper implements
			RowMapper<FinancialTransactionsData> {

		@Override
		public FinancialTransactionsData mapRow(ResultSet rs, int rowNum)

				throws SQLException {
			Long transactionId = rs.getLong("transId");
			Date transactionDate = rs.getDate("transDate");
			String transactionType = rs.getString("transType");
			BigDecimal amount = rs.getBigDecimal("amount");
			LocalDate transDate=JdbcSupport.getLocalDate(rs,"transDate");

			return new FinancialTransactionsData(transactionId,transDate,transactionType,amount);
		}

		public String financialTransactionsSchema() {

			return  " id as transId,Date_format(invoice_date,'%Y-%m-%d') transDate,'INVOICE' AS transType, invoice_amount AS amount "
					+"from invoice  where  invoice_date <= NOW() AND client_id = ? union all "
					+"SELECT id as transId,Date_format(adjustment_date,'%Y-%m-%d') transdate,'ADJUSTMENT' AS transType,CASE adjustment_type "
					 +" WHEN 'DEBIT' THEN adjustment_amount  WHEN 'CREDIT' THEN -adjustment_amount  END   AS amount  FROM adjustment " +
					 " WHERE adjustment_date <= NOW() AND client_id = ? UNION ALL " +
					 " SELECT pa.id as transId,Date_format(pa.payment_date,'%Y-%m-%d') transDate,CONCAT('PAYMENT',' - ',p.paymode_description) AS transType,"
					+"  -pa.amount_paid AS amount  FROM payments pa,paymodes p WHERE payment_date <= NOW()  and pa.paymode_code=p.paymode_code AND client_id = ? order by 2";


		}
	}

	@Override
	public BillDetailsData retrievebillDetails(Long clientId) {

		BillMapper mapper = new BillMapper();
	        String sql = "select " + mapper.billSchema() + " and b.id ="+clientId;

	         return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {  });
	    }

	private static final class BillMapper implements RowMapper<BillDetailsData> {

        public String billSchema() {
            return " *from bill_master b, m_client mc left join client_address c on c.client_id = mc.id  WHERE b.client_id = mc.id ";
        }



        @Override
        public BillDetailsData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {

            Long id = rs.getLong("id");
            Long clientId = rs.getLong("Client_id");
            String addrNo = rs.getString("address_no");
            String clientName = rs.getString("display_name");
            String billPeriod = rs.getString("bill_Period");

            String street = rs.getString("street");
            String zip = rs.getString("zip");
            String city = rs.getString("country");
            String state = rs.getString("state");
            String country = rs.getString("country");

            Double previousBal=rs.getDouble("Previous_balance");
            Double chargeAmount=rs.getDouble("Charges_amount");
            Double adjustmentAmount=rs.getDouble("Adjustment_amount");
            Double taxAmount=rs.getDouble("Tax_amount");
            Double paidAmount=rs.getDouble("Paid_amount");
            Double adjustAndPayment=rs.getDouble("Due_amount");
            String message=rs.getString("Promotion_description");


            LocalDate billDate = JdbcSupport.getLocalDate(rs, "Bill_date");
            LocalDate dueDate = JdbcSupport.getLocalDate(rs, "Due_date");

            return new BillDetailsData(id,clientId,addrNo,clientName,billPeriod,street,zip,city,
			state,country,previousBal,chargeAmount,adjustmentAmount,taxAmount,
			paidAmount,adjustAndPayment,billDate,dueDate,message);
			}


	}
	@Override

	public List<FinancialTransactionsData> getFinancialTransactionData(Long id) {

				TransactionDataMapper mapper = new TransactionDataMapper();
		        String sql = "select " + mapper.billSchema() + " and b.id ="+id;

		        return   this.jdbcTemplate.query(sql, mapper, new Object[] {  });


		    }

		private static final class TransactionDataMapper implements RowMapper<FinancialTransactionsData> {

	        public String billSchema() {
	            return "be.id,be.transaction_id as transaction_id,be.Transaction_type as Transaction_type, " +
				"be.description as description,be.Amount as Amount,be.Transaction_date as Transaction_date" +
				" from bill_master b,bill_details be where b.id = be.bill_id";
	        }



	        @Override
	        public FinancialTransactionsData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {

	            Long id = rs.getLong("id");
	            Long transctionId = rs.getLong("transaction_id");
	            String transactionType = rs.getString("Transaction_type");
	            String description = rs.getString("description");
	            BigDecimal amount = rs.getBigDecimal("Amount");




	            LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "Transaction_date");
				return new FinancialTransactionsData(transctionId,transactionType,transactionDate,amount);


				}
			}

		@Override
		public List<FinancialTransactionsData> retrieveStatments(Long clientId) {


			BillStatmentMapper mapper = new BillStatmentMapper();
			String sql = "select " + mapper.billStatemnetSchema();
			return this.jdbcTemplate.query(sql, mapper,
					new Object[] { clientId });

		}

		private static final class BillStatmentMapper implements
				RowMapper<FinancialTransactionsData> {

			@Override
			public FinancialTransactionsData mapRow(ResultSet rs, int rowNum)

					throws SQLException {
				Long id = rs.getLong("id");

				BigDecimal amount = rs.getBigDecimal("dueAmount");
				LocalDate billDate=JdbcSupport.getLocalDate(rs,"billDate");
				LocalDate dueDate=JdbcSupport.getLocalDate(rs,"dueDate");

				return new FinancialTransactionsData(id,billDate,dueDate,amount);
			}

			public String billStatemnetSchema() {

				return  "b.id as id,b.bill_date as billDate,b.due_date as dueDate,b.Due_amount as dueAmount from bill_master b where b.Client_id=? ";


			}
		}

		@Override
		public BigDecimal retrieveClientBalance(Long clientId) {

			ClientBalanceMapper mapper = new ClientBalanceMapper();
			String sql = "select " + mapper.billStatemnetSchema();

			BigDecimal previousBalance =  this.jdbcTemplate.queryForObject(sql, mapper,new Object[] { clientId });
			if( previousBalance == null ){
				previousBalance = BigDecimal.ZERO;
			}
			return previousBalance;
		}

		private static final class ClientBalanceMapper implements
				RowMapper<BigDecimal> {

			@Override
			public BigDecimal mapRow(ResultSet rs, int rowNum)

					throws SQLException {


				BigDecimal amount = rs.getBigDecimal("dueAmount");


				return amount;
			}

			public String billStatemnetSchema() {

				String result =   " IFNULL(b.Due_amount,0) as dueAmount  from bill_master b  where b.Client_id=? and id=(Select max(id) from bill_master a where a.client_id=b.client_id)";

				return result;
			}
}

		@Override
		public List<FinancialTransactionsData> retrieveSingleInvoiceData(
				Long invoiceId) {
			InvoiceDataMapper mapper = new InvoiceDataMapper();
			String sql = "select " + mapper.invoiceSchema();
			return this.jdbcTemplate.query(sql, mapper,
					new Object[] { invoiceId });

		}

		private static final class InvoiceDataMapper implements
				RowMapper<FinancialTransactionsData> {

			@Override
			public FinancialTransactionsData mapRow(ResultSet rs, int rowNum)

					throws SQLException {
				
				   String chargeType=rs.getString("chargeType");
				   String chargeDescription=rs.getString("chargeDescription");
				   BigDecimal chargeAmount = rs.getBigDecimal("chargeAmount");   
				   LocalDate chargeStartDate =JdbcSupport.getLocalDate(rs,"chargeStartDate");
				   LocalDate chargeEndDate =JdbcSupport.getLocalDate(rs,"chargeEndDate");
				BigDecimal taxAmount = rs.getBigDecimal("taxAmount");
				

				return new FinancialTransactionsData(chargeType,chargeDescription,chargeAmount,taxAmount,chargeStartDate,chargeEndDate);
			}

			public String invoiceSchema() {

				return  " c.charge_type AS chargeType, ch.charge_description AS chargeDescription,c.charge_start_date as chargeStartDate,"
						+"c.charge_end_date as chargeEndDate,c.charge_amount AS chargeAmount,t.tax_amount AS taxAmount"
						+" FROM charge_codes ch,charge c LEFT JOIN  charge_tax t  ON t.invoice_id = c.invoice_id"
						+" WHERE c.charge_code = ch.charge_code AND c.invoice_id =?";


			}
		}
}

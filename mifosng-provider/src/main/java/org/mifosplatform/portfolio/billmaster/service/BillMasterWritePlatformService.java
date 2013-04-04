package org.mifosplatform.portfolio.billmaster.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.billingmaster.command.BillMasterCommand;
import org.mifosplatform.portfolio.billingorder.data.BillDetailsData;
import org.mifosplatform.portfolio.billmaster.domain.BillDetail;
import org.mifosplatform.portfolio.billmaster.domain.BillMaster;
import org.mifosplatform.portfolio.financialtransaction.data.FinancialTransactionsData;

public interface BillMasterWritePlatformService {

	BillMaster createBillMaster(List<FinancialTransactionsData> financialTransactionsDatas,BillMasterCommand command, Long clientId);
	List<BillDetail> createBillDetail(List<FinancialTransactionsData> financialTransactionsDatas,BillMaster master);
	CommandProcessingResult updateBillMaster(List<BillDetail> billDetails,BillMaster billMaster, BigDecimal previousBal);
	String generatePdf(BillDetailsData billDetails,List<FinancialTransactionsData> data);
	void updateBillId(List<FinancialTransactionsData> financialTransactionsDatas, Long billId);
	void ireportPdf(Long billId);


}

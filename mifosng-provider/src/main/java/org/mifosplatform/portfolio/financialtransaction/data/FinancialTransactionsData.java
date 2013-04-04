package org.mifosplatform.portfolio.financialtransaction.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;

public class FinancialTransactionsData {

	private Long id;
	private Long transactionId;
	private Date transactionDate;
	private String transactionType;
	private BigDecimal amount;
	private LocalDate transDate;
	private LocalDate transactionalDate;
	private LocalDate billDate;
	private LocalDate dueDate;
	private String transaction;
	private String chargeType;
	private String chargeDescription;
	private BigDecimal chargeAmount;
	private BigDecimal taxAmount;
	private LocalDate chargeStartDate;
	private LocalDate chargeEndDate;
	private List<FinancialTransactionsData> transactionsDatas;
	

	public FinancialTransactionsData(final Long transactionId,final Date transactionDate,String transactionType,BigDecimal amount) {
		this.transactionId = transactionId;
		this.transactionDate = transactionDate;
		this.transactionType = transactionType;
		this.amount = amount;
	}

	public FinancialTransactionsData(Long transactionId, LocalDate transDate,
			String transactionType, BigDecimal amount) {

		this.transactionId = transactionId;
		this.transDate = transDate;
		this.transactionType = transactionType;
		this.amount = amount;
		this.transaction="INVOICE";

	}



	public FinancialTransactionsData(Long transctionId,
			String transactionType, LocalDate transactionDate, BigDecimal amount) {
		this.transactionId = transctionId;
		this.transactionalDate = transactionDate;
		this.transactionType = transactionType;
		this.amount = amount;
	}

	public FinancialTransactionsData(Long id, LocalDate billDate,
			LocalDate dueDate, BigDecimal amount) {
		this.id=id;
		this.billDate=billDate;
		this.dueDate=dueDate;
		this.amount=amount;
	}

	public FinancialTransactionsData(String chargeType,
			String chargeDescription, BigDecimal chargeAmount,
			BigDecimal taxAmount, LocalDate chargeStartDate, LocalDate chargeEndDate) {
		this.chargeType=chargeType;
		this.chargeDescription=chargeDescription;
		this.chargeAmount=chargeAmount;
		this.taxAmount=taxAmount;
		this.chargeStartDate=chargeStartDate;
		this.chargeEndDate=chargeEndDate;
	}

	public FinancialTransactionsData(
			List<FinancialTransactionsData> transactionData) {
		this.transactionsDatas=transactionData;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDate getTransDate() {
		return transDate;
	}

	public LocalDate getTransactionalDate() {
		return transactionalDate;
	}

	public Long getId() {
		return id;
	}

	public LocalDate getBillDate() {
		return billDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}


}

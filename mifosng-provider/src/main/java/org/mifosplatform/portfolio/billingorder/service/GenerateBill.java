package org.mifosplatform.portfolio.billingorder.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.portfolio.billingorder.commands.InvoiceTaxCommand;
import org.mifosplatform.portfolio.billingorder.data.BillingOrderData;
import org.mifosplatform.portfolio.billingorder.domain.InvoiceTax;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.taxmaster.data.TaxMappingRateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenerateBill {

	private final BillingOrderReadPlatformService billingOrderReadPlatformService;
	private final BillingOrderWritePlatformService billingOrderWritePlatformService;
	private final OrderRepository orderRepository;

	@Autowired
	public GenerateBill(
			BillingOrderReadPlatformService billingOrderReadPlatformService,
			BillingOrderWritePlatformService billingOrderWritePlatformService,
			final OrderRepository orderRepository) {
		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
		this.billingOrderWritePlatformService = billingOrderWritePlatformService;
		this.orderRepository = orderRepository;
	}

	BigDecimal pricePerMonth = null;
	LocalDate startDate = null;
	LocalDate endDate = null;
	BigDecimal price = null;
	LocalDate invoiceTillDate = null;
	LocalDate nextbillDate = null;
	BillingOrderCommand billingOrderCommand = null;

	public boolean isChargeTypeNRC(BillingOrderData billingOrderData) {
		boolean chargeType = false;
		if (billingOrderData.getChargeType().equals("NRC")) {
			chargeType = true;
		}
		return chargeType;
	}

	public boolean isChargeTypeRC(BillingOrderData billingOrderData) {
		boolean chargeType = false;
		if (billingOrderData.getChargeType().equals("RC")) {
			chargeType = true;
		}
		return chargeType;
	}

	public boolean isChargeTypeUC(BillingOrderData billingOrderData) {
		boolean chargeType = false;
		if (billingOrderData.getChargeType().equals("UC")) {
			chargeType = true;
		}
		return chargeType;
	}

	// pro rata monthly bill
	public BillingOrderCommand getProrataMonthlyFirstBill( 
			BillingOrderData billingOrderData) {

		startDate = new LocalDate(billingOrderData.getBillStartDate());

		endDate = startDate.dayOfMonth().withMaximumValue();

		if (endDate.toDate().before(billingOrderData.getBillEndDate())) {
			int currentDay = startDate.getDayOfMonth();
			int endOfMonth = startDate.dayOfMonth().withMaximumValue()
					.getDayOfMonth();
			int totalDays = endOfMonth - currentDay + 1;
			//price = billingOrderData.getPrice();
			price = billingOrderData.getPrice();
			BigDecimal pricePerDay = price.divide(new BigDecimal(30), 2,
					RoundingMode.HALF_UP);
			
			
			if(totalDays < endOfMonth){
				price = pricePerDay.multiply(new BigDecimal(totalDays));
			}
				
			

			
		} else if (endDate.toDate().after(billingOrderData.getBillEndDate())) {
			endDate = new LocalDate(billingOrderData.getBillEndDate());
			price = getDisconnectionCredit(startDate, endDate,
					billingOrderData.getPrice(),
					billingOrderData.getDurationType());
		} 

		
		invoiceTillDate = endDate;
		nextbillDate = invoiceTillDate.plusDays(1);

		

		List<TaxMappingRateData> taxMappingRateDatas = billingOrderReadPlatformService
				.retrieveTaxMappingDate(billingOrderData.getChargeCode());
		List<InvoiceTaxCommand> invoiceTaxCommand = generateInvoiceTax(
				taxMappingRateDatas, price, billingOrderData.getClientId());
		List<InvoiceTax> listOfTaxes = billingOrderWritePlatformService
				.createInvoiceTax(invoiceTaxCommand);

		billingOrderCommand = new BillingOrderCommand(
				billingOrderData.getClientOrderId(),
				billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), startDate.toDate(),
				nextbillDate.toDate(), endDate.toDate(),
				billingOrderData.getBillingFrequency(),
				billingOrderData.getChargeCode(),
				billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),
				billingOrderData.getDurationType(), invoiceTillDate.toDate(),
				price, billingOrderData.getBillingAlign(), listOfTaxes,
				billingOrderData.getStartDate(), billingOrderData.getEndDate());
		return billingOrderCommand;

	}

	public BillingOrderCommand getNextMonthBill(
			BillingOrderData billingOrderData) {

		startDate = new LocalDate(billingOrderData.getNextBillableDate());
		endDate = new LocalDate(billingOrderData.getInvoiceTillDate())
				.plusMonths(billingOrderData.getChargeDuration()).dayOfMonth()
				.withMaximumValue();

		if (endDate.toDate().before(billingOrderData.getBillEndDate())) {
			price = billingOrderData.getPrice();
		} else if (endDate.toDate().after(billingOrderData.getBillEndDate())) {
			endDate = new LocalDate(billingOrderData.getBillEndDate());
			price = getDisconnectionCredit(startDate, endDate,
					billingOrderData.getPrice(),
					billingOrderData.getDurationType());
		}

		invoiceTillDate = endDate;
		nextbillDate = invoiceTillDate.plusDays(1);
		List<TaxMappingRateData> taxMappingRateDatas = billingOrderReadPlatformService
				.retrieveTaxMappingDate(billingOrderData.getChargeCode());
		List<InvoiceTaxCommand> invoiceTaxCommand = generateInvoiceTax(
				taxMappingRateDatas, price, billingOrderData.getClientId());
		List<InvoiceTax> listOfTaxes = billingOrderWritePlatformService
				.createInvoiceTax(invoiceTaxCommand);

		billingOrderCommand = new BillingOrderCommand(
				billingOrderData.getClientOrderId(),
				billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), startDate.toDate(),
				nextbillDate.toDate(), endDate.toDate(),
				billingOrderData.getBillingFrequency(),
				billingOrderData.getChargeCode(),
				billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),
				billingOrderData.getDurationType(), invoiceTillDate.toDate(),
				price, billingOrderData.getBillingAlign(), listOfTaxes,
				billingOrderData.getStartDate(), billingOrderData.getEndDate());
		return billingOrderCommand;

	}

	public List<InvoiceTaxCommand> generateInvoiceTax(
			List<TaxMappingRateData> taxMappingRateDatas, BigDecimal price,
			Long clientId) {

		BigDecimal taxPercentage = null;
		String taxCode = null;
		BigDecimal taxAmount = null;
		List<InvoiceTaxCommand> invoiceTaxCommands = new ArrayList<InvoiceTaxCommand>();
		InvoiceTaxCommand invoiceTaxCommand = null;
		if (taxMappingRateDatas != null) {

			for (TaxMappingRateData taxMappingRateData : taxMappingRateDatas) {

				taxPercentage = taxMappingRateData.getRate();
				taxCode = taxMappingRateData.getTaxCode();
				taxAmount = price.multiply(taxPercentage.divide(new BigDecimal(
						100)));

				invoiceTaxCommand = new InvoiceTaxCommand(clientId, null, null,
						taxCode, null, taxPercentage, taxAmount);
				invoiceTaxCommands.add(invoiceTaxCommand);
			}

		}
		return invoiceTaxCommands;

	}

	public BillingOrderCommand getMonthyBill(BillingOrderData billingOrderData) {

		if (billingOrderData.getInvoiceTillDate() == null) {
			startDate = new LocalDate(billingOrderData.getBillStartDate());
			endDate = startDate
					.plusMonths(billingOrderData.getChargeDuration())
					.minusDays(1);
			price = billingOrderData.getPrice();
		} else if (billingOrderData.getInvoiceTillDate() != null) {

			startDate = new LocalDate(billingOrderData.getNextBillableDate());
			endDate = startDate
					.plusMonths(billingOrderData.getChargeDuration())
					.minusDays(1);

			if (endDate.toDate().before(billingOrderData.getBillEndDate())) {
				price = billingOrderData.getPrice();
			} else if (endDate.toDate()
					.after(billingOrderData.getBillEndDate())) {
				endDate = new LocalDate(billingOrderData.getBillEndDate());
				price = getDisconnectionCredit(startDate, endDate,
						billingOrderData.getPrice(),
						billingOrderData.getDurationType());
			}
			else if(billingOrderData.getOrderStatus() == 3){
				 
				
			}
		}

		invoiceTillDate = endDate;
		nextbillDate = invoiceTillDate.plusDays(1);

		List<TaxMappingRateData> taxMappingRateDatas = billingOrderReadPlatformService
				.retrieveTaxMappingDate(billingOrderData.getChargeCode());
		List<InvoiceTaxCommand> invoiceTaxCommand = generateInvoiceTax(
				taxMappingRateDatas, price, billingOrderData.getClientId());
		List<InvoiceTax> listOfTaxes = billingOrderWritePlatformService
				.createInvoiceTax(invoiceTaxCommand);

		billingOrderCommand = new BillingOrderCommand(
				billingOrderData.getClientOrderId(),
				billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), startDate.toDate(),
				nextbillDate.toDate(), endDate.toDate(),
				billingOrderData.getBillingFrequency(),
				billingOrderData.getChargeCode(),
				billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),
				billingOrderData.getDurationType(), invoiceTillDate.toDate(),
				price, billingOrderData.getBillingAlign(), listOfTaxes,
				billingOrderData.getStartDate(), billingOrderData.getEndDate());
		return billingOrderCommand;

	}

	public BillingOrderCommand getProrataWeeklyFirstBill(
			BillingOrderData billingOrderData) {

		startDate = new LocalDate(billingOrderData.getBillStartDate());
		endDate = startDate.dayOfWeek().withMaximumValue();

		int startDateOfWeek = startDate.getDayOfMonth();

		int endDateOfWeek = startDate.dayOfWeek().withMaximumValue()
				.getDayOfMonth();

		int totalDays = 0;

		// int diff = Math.abs(endDateOfWeek - startDateOfWeek);
		totalDays = Days.daysBetween(startDate, endDate).getDays()+1;
//		int numberOfdaysOfMonth = startDate.dayOfMonth().withMaximumValue()
//				.getDayOfMonth();
//		if (diff >= 7) {
//			totalDays = numberOfdaysOfMonth - diff + 1;
//			
//		} else {
//			totalDays = endDateOfWeek - startDateOfWeek + 1;
//		}
		

		BigDecimal pricePerDay = billingOrderData.getPrice().divide(
				new BigDecimal(7), 2, RoundingMode.HALF_UP);
		
		if(totalDays < 7){
			price = pricePerDay.multiply(new BigDecimal(totalDays));
		}else if(totalDays ==7){
			price = billingOrderData.getPrice();
		}

		

		invoiceTillDate = endDate;
		nextbillDate = endDate.plusDays(1);

		List<TaxMappingRateData> taxMappingRateDatas = billingOrderReadPlatformService
				.retrieveTaxMappingDate(billingOrderData.getChargeCode());
		List<InvoiceTaxCommand> invoiceTaxCommand = generateInvoiceTax(
				taxMappingRateDatas, price, billingOrderData.getClientId());
		List<InvoiceTax> listOfTaxes = billingOrderWritePlatformService
				.createInvoiceTax(invoiceTaxCommand);

		billingOrderCommand = new BillingOrderCommand(
				billingOrderData.getClientOrderId(),
				billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), startDate.toDate(),
				nextbillDate.toDate(), endDate.toDate(),
				billingOrderData.getBillingFrequency(),
				billingOrderData.getChargeCode(),
				billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),
				billingOrderData.getDurationType(), invoiceTillDate.toDate(),
				price, billingOrderData.getBillingAlign(), listOfTaxes,
				billingOrderData.getStartDate(), billingOrderData.getEndDate());
		return billingOrderCommand;

	}

	public BillingOrderCommand getNextWeeklyBill(
			BillingOrderData billingOrderData) {

		startDate = new LocalDate(billingOrderData.getNextBillableDate());

		endDate = startDate.dayOfWeek().withMaximumValue();

		if (endDate.toDate().before(billingOrderData.getBillEndDate())) {
			price = billingOrderData.getPrice();
		} else if (endDate.toDate().after(billingOrderData.getBillEndDate())) {
			endDate = new LocalDate(billingOrderData.getBillEndDate());
			price = getDisconnectionCredit(startDate, endDate,
					billingOrderData.getPrice(),
					billingOrderData.getDurationType());
		}

		invoiceTillDate = endDate;
		nextbillDate = endDate.plusDays(1);

		List<TaxMappingRateData> taxMappingRateDatas = billingOrderReadPlatformService
				.retrieveTaxMappingDate(billingOrderData.getChargeCode());
		List<InvoiceTaxCommand> invoiceTaxCommand = generateInvoiceTax(
				taxMappingRateDatas, price, billingOrderData.getClientId());
		List<InvoiceTax> listOfTaxes = billingOrderWritePlatformService
				.createInvoiceTax(invoiceTaxCommand);

		billingOrderCommand = new BillingOrderCommand(
				billingOrderData.getClientOrderId(),
				billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), startDate.toDate(),
				nextbillDate.toDate(), endDate.toDate(),
				billingOrderData.getBillingFrequency(),
				billingOrderData.getChargeCode(),
				billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),
				billingOrderData.getDurationType(), invoiceTillDate.toDate(),
				price, billingOrderData.getBillingAlign(), listOfTaxes,
				billingOrderData.getStartDate(), billingOrderData.getEndDate());
		return billingOrderCommand;

	}

	public BillingOrderCommand getWeeklyBill(BillingOrderData billingOrderData) {

		if (billingOrderData.getInvoiceTillDate() == null) {
			
			// please consider the contract start date over here
			startDate = new LocalDate(billingOrderData.getBillStartDate());
 			endDate = startDate.plusWeeks(1).minusDays(1);
			price = billingOrderData.getPrice();
		} else if (billingOrderData.getInvoiceTillDate() != null) {

			startDate = new LocalDate(billingOrderData.getNextBillableDate());
			endDate = startDate.plusWeeks(1).minusDays(1);

			if (endDate.toDate().before(billingOrderData.getBillEndDate())) {
				price = billingOrderData.getPrice();
			} else if (endDate.toDate()
					.after(billingOrderData.getBillEndDate())) {
				endDate = new LocalDate(billingOrderData.getBillEndDate());
				price = getDisconnectionCredit(startDate, endDate,
						billingOrderData.getPrice(),
						billingOrderData.getDurationType());
			}
		}

		invoiceTillDate = endDate;
		nextbillDate = invoiceTillDate.plusDays(1);

		price = billingOrderData.getPrice();

		List<TaxMappingRateData> taxMappingRateDatas = billingOrderReadPlatformService
				.retrieveTaxMappingDate(billingOrderData.getChargeCode());
		List<InvoiceTaxCommand> invoiceTaxCommand = generateInvoiceTax(
				taxMappingRateDatas, price, billingOrderData.getClientId());
		List<InvoiceTax> listOfTaxes = billingOrderWritePlatformService
				.createInvoiceTax(invoiceTaxCommand);

		billingOrderCommand = new BillingOrderCommand(
				billingOrderData.getClientOrderId(),
				billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), startDate.toDate(),
				nextbillDate.toDate(), endDate.toDate(),
				billingOrderData.getBillingFrequency(),
				billingOrderData.getChargeCode(),
				billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),
				billingOrderData.getDurationType(), invoiceTillDate.toDate(),
				price, billingOrderData.getBillingAlign(), listOfTaxes,
				billingOrderData.getStartDate(), billingOrderData.getEndDate());
		return billingOrderCommand;
	}

	public BillingOrderCommand getOneTimeBill(BillingOrderData billingOrderData) {

		LocalDate startDate = new LocalDate(billingOrderData.getBillStartDate());
		LocalDate endDate = startDate;
		LocalDate invoiceTillDate = startDate;
		LocalDate nextbillDate = invoiceTillDate.plusDays(1);
		BigDecimal price = billingOrderData.getPrice();

		List<TaxMappingRateData> taxMappingRateDatas = billingOrderReadPlatformService
				.retrieveTaxMappingDate(billingOrderData.getChargeCode());
		List<InvoiceTaxCommand> invoiceTaxCommand = generateInvoiceTax(
				taxMappingRateDatas, price, billingOrderData.getClientId());
		List<InvoiceTax> listOfTaxes = billingOrderWritePlatformService
				.createInvoiceTax(invoiceTaxCommand);

		billingOrderCommand = new BillingOrderCommand(
				billingOrderData.getClientOrderId(),
				billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), startDate.toDate(),
				nextbillDate.toDate(), endDate.toDate(),
				billingOrderData.getBillingFrequency(),
				billingOrderData.getChargeCode(),
				billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),
				billingOrderData.getDurationType(), invoiceTillDate.toDate(),
				price, billingOrderData.getBillingAlign(), listOfTaxes,
				billingOrderData.getBillStartDate(),
				billingOrderData.getBillEndDate());
		return billingOrderCommand;
	}

	private BigDecimal getDisconnectionCredit(LocalDate startDate,
			LocalDate endDate, BigDecimal amount, String durationType) {

		int currentDay = startDate.getDayOfMonth();
		//int endDay = endDate.getDayOfMonth();
		//int totalDays = endDay - currentDay + 1;
		int totalDays =0;
		if(startDate.isEqual(endDate)){
			   totalDays=0;
			  }else{
			   totalDays = Days.daysBetween(startDate, endDate).getDays()+1;
			  }
		pricePerMonth = amount;
		BigDecimal pricePerDay = BigDecimal.ZERO;

		if (durationType.equalsIgnoreCase("month(s)")) {
			pricePerDay = pricePerMonth.divide(new BigDecimal(30), 2,
					RoundingMode.HALF_UP);

		} else if (durationType.equalsIgnoreCase("week(s)")) {
			pricePerDay = pricePerMonth.divide(new BigDecimal(7), 2,
					RoundingMode.HALF_UP);
		}

		return pricePerDay.multiply(new BigDecimal(totalDays));

	}

	public BillingOrderCommand getCancelledOrderBill(BillingOrderData billingOrderData) {
	
		if(billingOrderData.getInvoiceTillDate() == null)
			 startDate=new LocalDate(billingOrderData.getStartDate());
		else
		startDate=new LocalDate(billingOrderData.getNextBillableDate());
		
		endDate=new LocalDate(billingOrderData.getBillEndDate());
		
		price=getDisconnectionCredit(startDate, endDate, billingOrderData.getPrice(),billingOrderData.getDurationType());
		

		List<TaxMappingRateData> taxMappingRateDatas = billingOrderReadPlatformService
				.retrieveTaxMappingDate(billingOrderData.getChargeCode());
		List<InvoiceTaxCommand> invoiceTaxCommand = generateInvoiceTax(
				taxMappingRateDatas, price, billingOrderData.getClientId());
		List<InvoiceTax> listOfTaxes = billingOrderWritePlatformService
				.createInvoiceTax(invoiceTaxCommand);

		billingOrderCommand = new BillingOrderCommand(
				billingOrderData.getClientOrderId(),
				billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), startDate.toDate(),
				new LocalDate().plusYears(1000).toDate(), endDate.toDate(),
				billingOrderData.getBillingFrequency(),
				billingOrderData.getChargeCode(),
				billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),
				billingOrderData.getDurationType(), endDate.toDate(),
				price, billingOrderData.getBillingAlign(), listOfTaxes,
				billingOrderData.getStartDate(), billingOrderData.getEndDate());
		return billingOrderCommand;
		
		
	}
}

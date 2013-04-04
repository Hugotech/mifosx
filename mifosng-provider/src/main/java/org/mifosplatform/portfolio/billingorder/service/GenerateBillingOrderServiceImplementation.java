package org.mifosplatform.portfolio.billingorder.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.portfolio.billingorder.commands.InvoiceCommand;
import org.mifosplatform.portfolio.billingorder.commands.InvoiceTaxCommand;
import org.mifosplatform.portfolio.billingorder.data.BillingOrderData;
import org.mifosplatform.portfolio.billingorder.domain.InvoiceTax;
import org.mifosplatform.portfolio.billingorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.taxmaster.data.TaxMappingRateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenerateBillingOrderServiceImplementation implements
		GenerateBillingOrderService {

	private final GenerateBill generateBill;
	private final OrderRepository orderRepository;

	@Autowired
	public GenerateBillingOrderServiceImplementation(GenerateBill generateBill,
			final OrderRepository orderRepository) {
		this.generateBill = generateBill;
		this.orderRepository = orderRepository;
	}

	@Override
	public List<BillingOrderCommand> generatebillingOrder(
			List<BillingOrderData> products) {

		BillingOrderCommand billingOrderCommand = null;
		List<BillingOrderCommand> billingOrderCommands = new ArrayList<BillingOrderCommand>();

		if (products.size() != 0) {

			for (BillingOrderData billingOrderData : products) {

				if(billingOrderData.getOrderStatus() ==3){
					billingOrderCommand=generateBill.getCancelledOrderBill(billingOrderData);	
					billingOrderCommands.add(billingOrderCommand);
				}
				
				else if (generateBill.isChargeTypeNRC(billingOrderData)) {
						
						System.out.println("---- NRC ---");
							billingOrderCommand = generateBill.getOneTimeBill(billingOrderData);
							billingOrderCommands.add(billingOrderCommand);

					} else if (generateBill.isChargeTypeRC(billingOrderData)) {

						System.out.println("---- RC ----");

						// monthly
						if (billingOrderData.getDurationType()
								.equalsIgnoreCase("month(s)") ) {
							if (billingOrderData.getBillingAlign()
									.equalsIgnoreCase("N")) {

								billingOrderCommand = generateBill
										.getMonthyBill(billingOrderData);
								billingOrderCommands.add(billingOrderCommand);

							} else if (billingOrderData.getBillingAlign().equalsIgnoreCase("Y")) {

								if (billingOrderData.getInvoiceTillDate() == null) {

									billingOrderCommand = generateBill
											.getProrataMonthlyFirstBill(billingOrderData);
									billingOrderCommands
											.add(billingOrderCommand);

								} else if (billingOrderData
										.getInvoiceTillDate() != null) {

									billingOrderCommand = generateBill
											.getNextMonthBill(billingOrderData);
									billingOrderCommands
											.add(billingOrderCommand);

								}
							}

						// weekly
						} else if (billingOrderData.getDurationType()
								.equalsIgnoreCase("week(s)")) {

							if (billingOrderData.getBillingAlign()
									.equalsIgnoreCase("N")) {

								billingOrderCommand = generateBill
										.getWeeklyBill(billingOrderData);
								billingOrderCommands.add(billingOrderCommand);

							} else if (billingOrderData.getBillingAlign()
									.equalsIgnoreCase("Y")) {

								if (billingOrderData.getInvoiceTillDate() == null) {

									billingOrderCommand = generateBill
											.getProrataWeeklyFirstBill(billingOrderData);
									billingOrderCommands
											.add(billingOrderCommand);

								} else if (billingOrderData
										.getInvoiceTillDate() != null) {

									billingOrderCommand = generateBill
											.getNextWeeklyBill(billingOrderData);
									billingOrderCommands
											.add(billingOrderCommand);
								}
							}

						// daily
						} else if (billingOrderData.getDurationType()
								.equalsIgnoreCase("daily")) {
							// To be developed latter
						}
					}
				
			}
		} else if (products.size() == 0) {
			throw new BillingOrderNoRecordsFoundException();
		}
		// return billingOrderCommand;
		return billingOrderCommands;
	}

	@Override
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
	@Override
	public InvoiceCommand generateInvoice(
			List<BillingOrderCommand> billingOrderCommands) {
		BigDecimal totalChargeAmountForServices = BigDecimal.ZERO;
		BigDecimal totalTaxAmountForServices = BigDecimal.ZERO;
		BigDecimal invoiceAmount = BigDecimal.ZERO;
		LocalDate invoiceDate = new LocalDate();
		for (BillingOrderCommand billingOrderCommand : billingOrderCommands) {
			totalChargeAmountForServices = billingOrderCommand.getPrice().add(
					totalChargeAmountForServices);
			List<InvoiceTax> listOfTaxes = billingOrderCommand.getListOfTax();
			BigDecimal netTaxForService = BigDecimal.ZERO;
			for (InvoiceTax invoiceTax : listOfTaxes) {
				netTaxForService = invoiceTax.getTaxAmount().add(
						netTaxForService);
			}
			totalTaxAmountForServices = totalTaxAmountForServices
					.add(netTaxForService);
		}
		invoiceAmount = totalChargeAmountForServices
				.add(totalTaxAmountForServices);

		return new InvoiceCommand(billingOrderCommands.get(0).getClientId(),
				invoiceDate.toDate(), invoiceAmount,
				totalChargeAmountForServices, totalTaxAmountForServices,
				"active", null, null, null, null);

		// invoice amount as zero
		// List<InvoiceCommand> invoiceCommands = new
		// ArrayList<InvoiceCommand>();
		// List<Long> orderIds = new ArrayList<Long>();
		// for(BillingOrderCommand billingOrderCommand : billingOrderCommands){
		// Long orderId = billingOrderCommand.getClientOrderId();
		// orderIds.add(orderId);
		// }
		//
		// Set<Long> clientOrderIds = toSet(orderIds);

		// for(Long orderId : clientOrderIds){
		// BigDecimal totalChargeAmountForServices = BigDecimal.ZERO;
		// BigDecimal totalTaxAmountForServices = BigDecimal.ZERO;
		// BigDecimal invoiceAmount = BigDecimal.ZERO;
		// LocalDate invoiceDate = new LocalDate();
		// for(BillingOrderCommand billingOrderCommand : billingOrderCommands){
		// if(billingOrderCommand.getClientOrderId()== orderId){
		// // our main logic goes here
		// totalChargeAmountForServices =
		// billingOrderCommand.getPrice().add(totalChargeAmountForServices);
		// BigDecimal netTaxForService = BigDecimal.ZERO;
		// List<InvoiceTax> listOfTaxes = billingOrderCommand.getListOfTax();
		// for(InvoiceTax invoiceTax : listOfTaxes){
		// netTaxForService = invoiceTax.getTaxAmount().add(netTaxForService);
		// }
		// totalTaxAmountForServices =
		// totalTaxAmountForServices.add(netTaxForService);
		// }
		// }
		// invoiceAmount =
		// totalChargeAmountForServices.add(totalTaxAmountForServices);
		// InvoiceCommand invoiceCommand = new
		// InvoiceCommand(billingOrderCommands.get(0).getClientId(),
		// invoiceDate.toDate(),
		// invoiceAmount, totalChargeAmountForServices,
		// totalTaxAmountForServices, "active", null,
		// null, null, null);
		//
		// invoiceCommands.add(invoiceCommand);
		// }
		// return invoiceCommands;

		// for(BillingOrderCommand billingOrderCommand : billingOrderCommands){
		// totalChargeAmountForServices =
		// billingOrderCommand.getPrice().add(totalChargeAmountForServices);
		// BigDecimal netTaxForService = BigDecimal.ZERO;
		// List<InvoiceTax> listOfTaxes = billingOrderCommand.getListOfTax();
		// for(InvoiceTax invoiceTax : listOfTaxes){
		// netTaxForService = invoiceTax.getTaxAmount().add(netTaxForService);
		// }
		// totalTaxAmountForServices =
		// totalTaxAmountForServices.add(netTaxForService);
		//
		// }
		//
		// invoiceAmount =
		// totalChargeAmountForServices.add(totalTaxAmountForServices);

		// for(BillingOrder billingOrder : listOfBillingOrders){
		// BigDecimal netChargeAmount = billingOrder.getChargeAmount();
		// BigDecimal netTaxAmount = BigDecimal.ZERO;
		// for(List<InvoiceTax> tax : listOfListOfTaxes){
		// BigDecimal taxAmount = BigDecimal.ZERO;
		// if (tax.size() != 0) {
		// for (InvoiceTax invoiceTax : tax) {
		// taxAmount = invoiceTax.getTaxAmount();
		// netTaxAmount = taxAmount.add(netTaxAmount);
		// }
		// }
		// }
		// totalTaxAmount = netTaxAmount.add(totalTaxAmount);
		// totalChargeAmount = netChargeAmount.add(totalChargeAmount);
		//
		// }
		// LocalDate invoiceDate = new LocalDate();
		// BigDecimal netChargeAmount = command.getPrice();
		// BigDecimal taxAmount = null;
		// BigDecimal totalTaxAmount = BigDecimal.ZERO;

		// if (tax.size() != 0) {
		// for (InvoiceTax invoiceTax : tax) {
		// taxAmount = invoiceTax.getTaxAmount();
		// totalTaxAmount = taxAmount.add(totalTaxAmount);
		// }
		// }

		// BigDecimal invoiceAmount = totalChargeAmount.add(totalTaxAmount);
		// return new InvoiceCommand(billingOrderCommands.get(0).getClientId(),
		// invoiceDate.toDate(),
		// invoiceAmount, totalChargeAmountForServices,
		// totalTaxAmountForServices, "active", null,
		// null, null, null);
	}
}

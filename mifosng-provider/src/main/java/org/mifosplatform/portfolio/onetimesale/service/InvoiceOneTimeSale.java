package org.mifosplatform.portfolio.onetimesale.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.adjustment.service.AdjustmentReadPlatformService;
import org.mifosplatform.portfolio.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.portfolio.billingorder.commands.InvoiceCommand;
import org.mifosplatform.portfolio.billingorder.commands.InvoiceTaxCommand;
import org.mifosplatform.portfolio.billingorder.data.BillingOrderData;
import org.mifosplatform.portfolio.billingorder.domain.BillingOrder;
import org.mifosplatform.portfolio.billingorder.domain.Invoice;
import org.mifosplatform.portfolio.billingorder.domain.InvoiceTax;
import org.mifosplatform.portfolio.billingorder.service.BillingOrderReadPlatformService;
import org.mifosplatform.portfolio.billingorder.service.BillingOrderWritePlatformService;
import org.mifosplatform.portfolio.billingorder.service.GenerateBill;
import org.mifosplatform.portfolio.billingorder.service.GenerateBillingOrderService;
import org.mifosplatform.portfolio.clientbalance.data.ClientBalanceData;
import org.mifosplatform.portfolio.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.portfolio.onetimesale.domain.OneTimeSale;
import org.mifosplatform.portfolio.onetimesale.domain.OneTimeSaleRepository;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.taxmaster.data.TaxMappingRateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceOneTimeSale {

	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;

	private final OneTimeSaleWritePlatformService oneTimeSaleWritePlatformService;

	private final GenerateBill generateBill;

	private final BillingOrderReadPlatformService billingOrderReadPlatformService;

	private final BillingOrderWritePlatformService billingOrderWritePlatformService;

	private final OrderRepository orderRepository;

	private GenerateBillingOrderService generateBillingOrderService;

	private AdjustmentReadPlatformService adjustmentReadPlatformService;

	private OneTimeSaleRepository oneTimeSaleRepository;

	@Autowired
	public InvoiceOneTimeSale(
			OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,
			OneTimeSaleWritePlatformService oneTimeSaleWritePlatformService,
			GenerateBill generateBill,
			BillingOrderReadPlatformService billingOrderReadPlatformService,
			BillingOrderWritePlatformService billingOrderWritePlatformService,
			OrderRepository orderRepository,
			GenerateBillingOrderService generateBillingOrderService,
			AdjustmentReadPlatformService adjustmentReadPlatformService,
			OneTimeSaleRepository oneTimeSaleRepository) {

		this.oneTimeSaleReadPlatformService = oneTimeSaleReadPlatformService;
		this.oneTimeSaleWritePlatformService = oneTimeSaleWritePlatformService;
		this.generateBill = generateBill;
		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
		this.billingOrderWritePlatformService = billingOrderWritePlatformService;
		this.orderRepository = orderRepository;
		this.generateBillingOrderService = generateBillingOrderService;
		this.adjustmentReadPlatformService = adjustmentReadPlatformService;
		this.oneTimeSaleRepository = oneTimeSaleRepository;
	}

	public void invoiceOneTimeSale(Long clientId) {
		List<BillingOrderCommand> billingOrderCommands = new ArrayList<BillingOrderCommand>();
		List<OneTimeSaleData> oneTimeSaleDatas = oneTimeSaleReadPlatformService.retrieveOnetimeSaleDate(clientId);
		for (OneTimeSaleData oneTimeSaleData : oneTimeSaleDatas) {
			// check whether one time sale is invoiced
			// N - not invoiced
			// y - invoiced
			if (oneTimeSaleData.getIsInvoiced().equalsIgnoreCase("N")) {

				BillingOrderData billingOrderData = new BillingOrderData(
						oneTimeSaleData.getId(),
						oneTimeSaleData.getClientId(),
						new LocalDate().toDate(),
						oneTimeSaleData.getChargeCode(), "NRC",
						oneTimeSaleData.getTotalPrice());


				List<InvoiceTax> listOfTaxes = calculateTax(billingOrderData);

				BillingOrderCommand billingOrderCommand = new BillingOrderCommand(
						billingOrderData.getClientOrderId(),
						new Long(0),
						billingOrderData.getClientId(),
						new LocalDate().toDate(), null,
						new LocalDate().toDate(), null,
						billingOrderData.getChargeCode(),
						billingOrderData.getChargeType(), null,
						billingOrderData.getDurationType(), null,
						billingOrderData.getPrice(), null, listOfTaxes,
						new LocalDate().toDate(), new LocalDate().toDate());
				billingOrderCommands.add(billingOrderCommand);

				List<BillingOrder> listOfBillingOrders = billingOrderWritePlatformService.createBillingProduct(billingOrderCommands);

				// calculation of invoice
				InvoiceCommand invoiceCommand = this.generateBillingOrderService
						.generateInvoice(billingOrderCommands);

				// To fetch record from client_balance table
				List<ClientBalanceData> clientBalancesDatas = adjustmentReadPlatformService
						.retrieveAllAdjustments(clientId);

				// Insertion into invoice table
				Invoice invoice = billingOrderWritePlatformService
						.createInvoice(invoiceCommand, clientBalancesDatas);

				// Updation of invoice id in invoice_tax table
				billingOrderWritePlatformService.updateInvoiceTax(invoice,
						billingOrderCommands, listOfBillingOrders);

				// Updation of invoice id in charge table
				billingOrderWritePlatformService.updateInvoiceCharge(invoice,
						listOfBillingOrders);
				
				// update column is_invoiceed of one-time-sale
				
				updateOneTimeSale(oneTimeSaleData);

			} else {

			}
		}
	}

	public void updateOneTimeSale(OneTimeSaleData oneTimeSaleData) {
		
		OneTimeSale oneTimeSale = oneTimeSaleRepository.findOne(oneTimeSaleData.getId());
		oneTimeSale.setDeleted('y');
		oneTimeSaleRepository.save(oneTimeSale);
		
	}

	public List calculateTax(BillingOrderData billingOrderData) {

		List<TaxMappingRateData> taxMappingRateDatas = billingOrderReadPlatformService.retrieveTaxMappingDate(billingOrderData.getChargeCode());
		List<InvoiceTaxCommand> invoiceTaxCommand = generateBill.generateInvoiceTax(taxMappingRateDatas,
						billingOrderData.getPrice(),
						billingOrderData.getClientId());
		List<InvoiceTax> listOfTaxes = billingOrderWritePlatformService.createInvoiceTax(invoiceTaxCommand);

		return listOfTaxes;
	}

}

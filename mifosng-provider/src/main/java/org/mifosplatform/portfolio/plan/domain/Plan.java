package org.mifosplatform.portfolio.plan.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.plan.commands.PlansCommand;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;

@Entity
@Table(name = "plan_master", uniqueConstraints = @UniqueConstraint(name = "plan_code_key", columnNames = { "plan_code" }))
public class Plan{


	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@Column(name = "plan_code", length = 65536)
	private String planCode;

	@Column(name = "plan_description")
	private String description;

	@Column(name = "plan_status")
	private Long status;
	@Column(name = "contract_period")
	private String contractPeriod;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "bill_rule")
	private Long billRule;

	@Column(name = "is_deleted", nullable = false)
	private char deleted='n';

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "plan", orphanRemoval = true)
	private List<ServiceDetails> details = new ArrayList<ServiceDetails>();

	public Plan() {
		// TODO Auto-generated constructor stub
	}

	public Plan(final String code, final String description,
			final LocalDate start_date, final LocalDate endDate,
			final Long bill_rule, final Long status,
			 final String contractPeriod,
			final List<ServiceDetails> details) {
		this.planCode = code;
		this.description = description;
		if (endDate != null)
			this.endDate = endDate.toDate();
		this.startDate = start_date.toDate();
		this.status = status;
		this.contractPeriod = contractPeriod;
		this.billRule = bill_rule;
		this.details = details;

	}

	public List<ServiceDetails> getDetails() {
		return details;
	}

	public String getCode() {
		return planCode;
	}

	public String getDescription() {
		return description;
	}

	public Long getStatus() {
		return status;
	}

	public Date getStart_date() {
		return startDate;
	}

	public Date getEnd_date() {
		return endDate;
	}



	public Long getId() {
		return id;
	}

	public String getPlanCode() {
		return planCode;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Long getBillRule() {
		return billRule;
	}

	public char isDeleted() {
		return deleted;
	}

	public String getContractPeriod() {
		return contractPeriod;
	}

	public void addServieDetails(ServiceDetails serviceDetail) {

		serviceDetail.update(this);
		this.details.add(serviceDetail);

	}

	public Long getBill_rule() {
		return billRule;
	}

	public void update(PlansCommand command, PlanData serviceData,
			List<ServiceData> services) {

		this.planCode = command.isplanCodeChanged() ? command.getplanCode()
				: this.planCode;

		this.description = command.isplanDescriptionChanged() ? command
				.getplanDescription() :  this.description;

		this.startDate = command.isStartDateChanged() ? command
				.getStartDate().toDate() :this.startDate;
		if (command.getEndDate() == null) {
			this.endDate = null;

		} else {
			this.endDate = command.isendDateChanged() ? command.getEndDate()
					.toDate() : this.endDate;
		}
		this.billRule = command.isBillingRuleChanged() ? command.getBillRule()
				: this.billRule;
		this.contractPeriod = command.isContractPeriodChanged() ? command
				.getContractPeriod() : this.contractPeriod;

		this.status = command.isPlanStatusChanged() ? command.getStatus()
				:this.status;

	}

	public void delete(PlanData data) {

		/*this.code = data.getPlan_code();
		this.description = data.getPlan_description();

		if (data.getEndDate() != null)
			this.end_date = data.getEndDate().toDate();
		this.start_date = data.getStartDate().toDate();
		this.status = data.getStatus();
		this.contract_period = data.getPeriod();
		this.bill_rule = data.getBillRule();*/

		if (deleted =='y') {

		} else {
			this.deleted = 'y';
			this.planCode=this.planCode+"_"+this.getId()+"_DELETED";

		}
	}

}

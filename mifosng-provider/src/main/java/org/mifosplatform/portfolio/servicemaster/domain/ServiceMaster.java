package org.mifosplatform.portfolio.servicemaster.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.portfolio.servicemaster.commands.ServiceMasterCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "service", uniqueConstraints = @UniqueConstraint(name = "service_code_key", columnNames = { "service_code" }))
public class ServiceMaster extends AbstractPersistable<Long> {

	@Column(name = "service_code", nullable = false, length = 20)
	private String serviceCode;

	@Column(name = "service_description", nullable = false, length = 100)
	private String serviceDescription;

	@Column(name = "type", nullable = false, length = 100)
	private Long serviceType;

	@Column(name = "is_deleted")
	private String isDeleted="n";


public ServiceMaster()
{}


	public ServiceMaster(String serviceCode, String serviceDescription,
			Long serviceType) {
		this.serviceCode = serviceCode;
		this.serviceDescription = serviceDescription;
		this.serviceType = serviceType;
	}

	public static ServiceMaster create(String serviceCode,
			String serviceDescription, Long serviceType) {
		return new ServiceMaster(serviceCode, serviceDescription, serviceType);
					}

	public String getServiceCode() {
		return serviceCode;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public Long getServiceType() {
		return serviceType;
	}


	public String getIsDeleted() {
		return isDeleted;
	}


	public void update(ServiceMasterCommand command) {
		if(command.isServiceCodeChanged())
		{
			this.serviceCode=command.getServiceCode();
		}
		if(command.isServiceDescriptionChanged())
		{
			this.serviceDescription=command.getServiceDescription();
		}
		if(command.isServiceTypeChanged())
		{
			this.serviceType=command.getServiceType();
		}


	}

	public void delete() {
		if(isDeleted.equalsIgnoreCase("y"))
		{}
		else
		{
			this.serviceCode=this.serviceCode+"_"+this.getId()+"DELETED";
			isDeleted="y";
		}
	}



}

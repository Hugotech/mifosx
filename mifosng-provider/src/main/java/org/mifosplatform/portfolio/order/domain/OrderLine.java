package org.mifosplatform.portfolio.order.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "order_line")
public class OrderLine extends AbstractAuditableCustom<AppUser, Long>  {

/*@Id
@GeneratedValue
@Column(name="id")
private Long id;
*/

@ManyToOne
@JoinColumn(name="order_id")
	private Order orders;

	@Column(name = "service_id")
	private Long serviceId;

	@Column(name = "service_type")
	private String serviceType;

	@Column(name = "service_status")
	private Long serviceStatus;

	@Column(name = "is_deleted")
	private char isDeleted;

	public OrderLine()
	{}

	public OrderLine(final Long service_id,final String service_type,final Long service_status,final char isdeleted )
	{
		this.orders=null;
		this.serviceId=service_id;
		this.serviceStatus=service_id;
		this.isDeleted=isdeleted;
		this.serviceType=service_type;

	}
public OrderLine(final String service_code)
{
	this.serviceType=service_code;
	}

	public Order getOrder_id() {
		return orders;
	}


	public Long getService_id() {
		return serviceId;
	}


	public String getServiceType() {
		return serviceType;
	}


	public Long getService_status() {
		return serviceStatus;
	}


	public char isIs_deleted() {
		return isDeleted;
	}
	public  void update(Order order2)
	{
		this.orders=order2;

	}

	public void delete() {

		this.isDeleted='y';


	}



}

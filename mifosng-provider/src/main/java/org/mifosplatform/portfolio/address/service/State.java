package org.mifosplatform.portfolio.address.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractAuditable;


@Entity
@Table(name="state",uniqueConstraints=@UniqueConstraint( name="state_code",columnNames ={ "state_code" }))
public class State extends AbstractAuditable<AppUser,Long>{

@Column(name="state_code")
private String stateCode;

@Column(name="state_name")
private String stateName;

@Column(name ="parent_code")
private Long parentCode;


public State(String entityCode, String entityName, Long parentEntityId) {
	  this.stateCode=entityCode;
	  this.stateName=entityName;
	  this.parentCode=parentEntityId;


	}

}

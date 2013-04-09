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
@Table(name="country",uniqueConstraints=@UniqueConstraint( name ="country_code",columnNames={"country_code"}))
public class Country extends AbstractAuditable<AppUser,Long>{
	

@Column(name="country_code")
private String countryCode;

@Column(name="country_name")
private String countryName;

@Column(name="is_active")
private String isActive;


public Country(String entityCode, String entityName) {
this.countryCode=entityCode;
this.countryName=entityName;
this.isActive="Y";


}
}

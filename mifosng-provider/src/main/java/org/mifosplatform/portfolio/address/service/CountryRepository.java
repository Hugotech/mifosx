package org.mifosplatform.portfolio.address.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CountryRepository extends JpaRepository<Country,Long>,JpaSpecificationExecutor<Country>{

}

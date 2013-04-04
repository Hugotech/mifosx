package org.mifosplatform.portfolio.itemmaster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemMasterRepository extends JpaRepository<ItemMaster, Long>,
JpaSpecificationExecutor<ItemMaster>{

}

/**
 * 
 */
package org.mifosplatform.portfolio.item.domain;

import org.mifosplatform.portfolio.itemmaster.domain.ItemMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author hugo
 *
 */
public interface ItemRepository extends JpaRepository<ItemMaster,Long >,JpaSpecificationExecutor<ItemMaster>{

}

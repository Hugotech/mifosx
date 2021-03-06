package org.mifosplatform.useradministration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_permission")
public class Permission extends AbstractPersistable<Long> {

    @Column(name = "grouping", nullable = false, length = 45)
    private final String grouping;

    @Column(name = "code", nullable = false, length = 100)
    private final String code;

    @SuppressWarnings("unused")
    @Column(name = "entity_name", nullable = true, length = 100)
    private final String entityName;

    @SuppressWarnings("unused")
    @Column(name = "action_name", nullable = true, length = 100)
    private final String actionName;

    @Column(name = "can_maker_checker", nullable = false)
    private boolean canMakerChecker;

    protected Permission() {
        this.grouping = null;
        this.code = null;
        this.entityName = null;
        this.actionName = null;
        this.canMakerChecker = false;
    }

    public boolean hasCode(final String checkCode) {
        return this.code.equalsIgnoreCase(checkCode);
    }

    public String getCode() {
        return this.code;
    }

    public boolean hasMakerCheckerEnabled() {
        return this.canMakerChecker;
    }

    public String getGrouping() {
        return this.grouping;
    }

    public boolean enableMakerChecker(final boolean canMakerChecker) {
        final boolean isUpdatedValueSame = (this.canMakerChecker == canMakerChecker);
        this.canMakerChecker = canMakerChecker;
        
        return !isUpdatedValueSame;
    }
}
package org.gs4tr.termmanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.gs4tr.foundation.modules.entities.model.Identifiable;

/**
 * Created by emisia on 6/14/17.
 */
@Entity
@Table(name = "VERSION_REINDEX_INFO")
public class TmVersionReindexInfo implements Identifiable<Long> {

    private static final long serialVersionUID = 1L;

    private Boolean _enableReindex = Boolean.FALSE;

    private Boolean _firstReindex = Boolean.FALSE;

    private Long _id;

    @Column(name = "ENABLE_REINDEX", nullable = true, updatable = true)
    public Boolean getEnableReindex() {
	return _enableReindex;
    }

    @Column(name = "FIRST_REINDEX", nullable = true, updatable = true)
    public Boolean getFirstReindex() {
	return _firstReindex;
    }

    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
	return _id;
    }

    @Transient
    @Override
    public Long getIdentifier() {
	return getId();
    }

    public void setEnableReindex(Boolean enableReindex) {
	_enableReindex = enableReindex;
    }

    public void setFirstReindex(Boolean firstReindex) {
	_firstReindex = firstReindex;
    }

    public void setId(Long id) {
	_id = id;
    }

    @Override
    public void setIdentifier(Long identifier) {

    }
}

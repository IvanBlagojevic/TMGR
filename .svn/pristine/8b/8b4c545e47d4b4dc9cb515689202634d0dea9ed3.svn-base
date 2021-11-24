package org.gs4tr.termmanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.gs4tr.foundation.modules.entities.model.Identifiable;

@Entity
@Table(name = "VERSION")
public class TmVersion implements Identifiable<Long> {

    private static final long serialVersionUID = -1046407519185678813L;

    private Long _versionId;

    private String _versionLabel;

    @Transient
    @Override
    public Long getIdentifier() {
	return getVersionId();
    }

    @Id
    @Column(name = "VERSION_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getVersionId() {
	return _versionId;
    }

    @Column(name = "VERSION_LABEL", length = 30, updatable = true, nullable = true)
    public String getVersionLabel() {
	return _versionLabel;
    }

    @Override
    public void setIdentifier(Long versionId) {
	setVersionId(versionId);
    }

    public void setVersionId(Long versionId) {
	_versionId = versionId;
    }

    public void setVersionLabel(String versionLabel) {
	_versionLabel = versionLabel;
    }

    @Override
    public String toString() {
	return getVersionLabel();
    }
}

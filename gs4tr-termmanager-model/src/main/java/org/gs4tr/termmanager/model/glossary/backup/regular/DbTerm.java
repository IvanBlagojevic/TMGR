package org.gs4tr.termmanager.model.glossary.backup.regular;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.gs4tr.termmanager.model.glossary.backup.DbBaseTerm;

@Entity
@Table(name = "TERM")
public class DbTerm extends DbBaseTerm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7175177042104055083L;

    private Set<DbTermDescription> _descriptions;

    private Boolean _disabled = Boolean.FALSE;

    private Long _userLatestChange;

    public DbTerm() {
	super();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!super.equals(obj))
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DbTerm other = (DbTerm) obj;
	if (_descriptions == null) {
	    if (other._descriptions != null)
		return false;
	} else if (!_descriptions.equals(other._descriptions))
	    return false;
	if (_disabled == null) {
	    return other._disabled == null;
	} else
	    return _disabled.equals(other._disabled);
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERM_ID")
    public Set<DbTermDescription> getDescriptions() {
	return _descriptions;
    }

    @Column(name = "DISABLED", nullable = false)
    public Boolean getDisabled() {
	return _disabled;
    }

    @Column(name = "USER_LATEST_CHANGE", length = 10, nullable = true, updatable = true)
    public Long getUserLatestChange() {
	return _userLatestChange;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + ((_descriptions == null) ? 0 : _descriptions.hashCode());
	return prime * result + ((_disabled == null) ? 0 : _disabled.hashCode());
    }

    public void setDescriptions(Set<DbTermDescription> descriptions) {
	_descriptions = descriptions;
    }

    public void setDisabled(Boolean disabled) {
	_disabled = disabled;
    }

    public void setUserLatestChange(Long userLatestChange) {
	_userLatestChange = userLatestChange;
    }

    @Override
    public String toString() {
	return "DbTerm [_descriptions=" + _descriptions + ", _disabled=" + _disabled + ", _userLatestChange="
		+ _userLatestChange + ", toString()=" + super.toString() + "]";
    }
}

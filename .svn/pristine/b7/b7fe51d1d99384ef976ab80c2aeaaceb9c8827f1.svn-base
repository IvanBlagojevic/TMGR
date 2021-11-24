package org.gs4tr.termmanager.model.glossary.backup.regular;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.gs4tr.termmanager.model.glossary.backup.DbBaseTermEntry;

@NamedQueries({ @NamedQuery(name = "DbTermEntry.findById", query = "select entry from DbTermEntry entry "
	+ "where entry.uuId = :entityId"),

	@NamedQuery(name = "DbTermEntry.findByIds", query = "select entry from DbTermEntry entry "
		+ "where entry.uuId in (:entityIds)") })
@Entity
@Table(name = "TERMENTRY", uniqueConstraints = { @UniqueConstraint(name = "IDX_RT_UUID", columnNames = { "UUID" }) })
public class DbTermEntry extends DbBaseTermEntry implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6112580373144842259L;

    private Set<DbTermEntryDescription> _descriptions;

    private Set<DbTermEntryHistory> _history;

    private Set<DbTerm> _terms;

    public DbTermEntry() {
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
	DbTermEntry other = (DbTermEntry) obj;
	if (_descriptions == null) {
	    if (other._descriptions != null)
		return false;
	} else if (!_descriptions.equals(other._descriptions))
	    return false;
	if (_terms == null) {
	    if (other._terms != null)
		return false;
	} else if (!_terms.equals(other._terms))
	    return false;
	return true;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERMENTRY_ID")
    public Set<DbTermEntryDescription> getDescriptions() {
	return _descriptions;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERMENTRY_ID")
    public Set<DbTermEntryHistory> getHistory() {
	return _history;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERMENTRY_ID")
    public Set<DbTerm> getTerms() {
	return _terms;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + ((_descriptions == null) ? 0 : _descriptions.hashCode());
	result = prime * result + ((_terms == null) ? 0 : _terms.hashCode());
	return result;
    }

    public void setDescriptions(Set<DbTermEntryDescription> descriptions) {
	_descriptions = descriptions;
    }

    public void setHistory(Set<DbTermEntryHistory> history) {
	_history = history;
    }

    public void setTerms(Set<DbTerm> terms) {
	_terms = terms;
    }

    @Override
    public String toString() {
	return "DbTermEntry [_descriptions=" + _descriptions + ", _history=" + _history + ", _terms=" + _terms
		+ ", toString()=" + super.toString() + "]";
    }
}

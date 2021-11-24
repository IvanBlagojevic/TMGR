package org.gs4tr.termmanager.model.glossary.backup.regular;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.gs4tr.termmanager.model.glossary.backup.DbBaseTermEntryHistory;

@Entity
@Table(name = "TERMENTRY_HISTORY")
public class DbTermEntryHistory extends DbBaseTermEntryHistory implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5963487263714484155L;

    private Set<DbTermHistory> _history;

    public DbTermEntryHistory() {

    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!super.equals(obj))
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DbTermEntryHistory other = (DbTermEntryHistory) obj;
	if (_history == null) {
	    return other._history == null;
	} else
	    return _history.equals(other._history);
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERMENTRY_HISTORY_ID")
    public Set<DbTermHistory> getHistory() {
	return _history;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	return prime * result + ((_history == null) ? 0 : _history.hashCode());
    }

    public void setHistory(Set<DbTermHistory> history) {
	_history = history;
    }

    @Override
    public String toString() {
	return "DbTermEntryHistory [_history=" + _history + "]";
    }
}

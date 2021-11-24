package org.gs4tr.termmanager.model.glossary.backup.submission;

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
@Table(name = "SUBMISSION_TERMENTRY_HISTORY")
public class DbSubmissionTermEntryHistory extends DbBaseTermEntryHistory implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5963487263714484155L;

    private Set<DbSubmissionTermHistory> _history;

    public DbSubmissionTermEntryHistory() {

    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!super.equals(obj))
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DbSubmissionTermEntryHistory other = (DbSubmissionTermEntryHistory) obj;
	if (_history == null) {
	    if (other._history != null)
		return false;
	} else if (!_history.equals(other._history))
	    return false;
	return true;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERMENTRY_HISTORY_ID")
    public Set<DbSubmissionTermHistory> getHistory() {
	return _history;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + ((_history == null) ? 0 : _history.hashCode());
	return result;
    }

    public void setHistory(Set<DbSubmissionTermHistory> history) {
	_history = history;
    }

    @Override
    public String toString() {
	return "DbSubmissionTermEntryHistory [_history=" + _history + "]";
    }

}

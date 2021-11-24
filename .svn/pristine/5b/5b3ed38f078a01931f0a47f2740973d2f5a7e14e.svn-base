package org.gs4tr.termmanager.model.glossary.backup.submission;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.gs4tr.termmanager.model.glossary.backup.DbBaseTermEntry;

@NamedQueries({
	@NamedQuery(name = "DbSubmissionTermEntry.findById", query = "select distinct entry from DbSubmissionTermEntry entry "
		+ "where entry.uuId = :entityId"),
	@NamedQuery(name = "DbSubmissionTermEntry.findByIds", query = "select distinct entry from DbSubmissionTermEntry entry "
		+ "where entry.uuId in (:entityIds)"),
	@NamedQuery(name = "DbSubmissionTermEntry.updateSubmissionTermLanguages", query = "update DbSubmissionTerm st "
		+ "set st.languageId =:languageTo where st.languageId =:languageFrom and st.termEntryUuid "
		+ "in(select ste.uuId from DbSubmissionTermEntry ste where ste.projectId =:projectId)") })
@Entity
@Table(name = "SUBMISSION_TERMENTRY", uniqueConstraints = {
	@UniqueConstraint(name = "IDX_ST_UUID", columnNames = { "UUID" }) })
public class DbSubmissionTermEntry extends DbBaseTermEntry implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4476874349355571535L;

    private boolean _actionRollback;

    private Set<DbSubmissionTermEntryDescription> _descriptions;

    private Set<DbSubmissionTermEntryHistory> _history;

    // link between termEntry and termEntry in submission
    private String _parentUuId;

    private Set<DbSubmissionTerm> _submissionTerms;

    public DbSubmissionTermEntry() {
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
	DbSubmissionTermEntry other = (DbSubmissionTermEntry) obj;
	if (_submissionTerms == null) {
	    if (other._submissionTerms != null)
		return false;
	} else if (!_submissionTerms.equals(other._submissionTerms))
	    return false;
	return true;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERMENTRY_ID")
    public Set<DbSubmissionTermEntryDescription> getDescriptions() {
	return _descriptions;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERMENTRY_ID")
    public Set<DbSubmissionTermEntryHistory> getHistory() {
	return _history;
    }

    @Column(name = "PARENT_ID", nullable = false, length = 128)
    public String getParentUuId() {
	return _parentUuId;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TERMENTRY_ID")
    public Set<DbSubmissionTerm> getSubmissionTerms() {
	return _submissionTerms;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + ((_submissionTerms == null) ? 0 : _submissionTerms.hashCode());
	return result;
    }

    @Column(name = "ROLLBACK_ACTION")
    public boolean isActionRollback() {
	return _actionRollback;
    }

    public void setActionRollback(boolean actionRollback) {
	_actionRollback = actionRollback;
    }

    public void setDescriptions(Set<DbSubmissionTermEntryDescription> descriptions) {
	_descriptions = descriptions;
    }

    public void setHistory(Set<DbSubmissionTermEntryHistory> history) {
	_history = history;
    }

    public void setParentUuId(String parentUuId) {
	_parentUuId = parentUuId;
    }

    public void setSubmissionTerms(Set<DbSubmissionTerm> submissionTerms) {
	_submissionTerms = submissionTerms;
    }

    @Override
    public String toString() {
	return "SubmissionTermEntry [_parentUuId=" + _parentUuId + ", _submissionTerms=" + _submissionTerms
		+ ", toString()=" + super.toString() + "]";
    }
}

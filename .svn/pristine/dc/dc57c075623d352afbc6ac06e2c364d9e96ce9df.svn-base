package org.gs4tr.termmanager.model.glossary.backup.submission;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.gs4tr.termmanager.model.glossary.PriorityEnum;

@Embeddable
public class DbPriority implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5630294357779226194L;

    private long _assigneePriority;

    private long _submitterPriority;

    public DbPriority() {
	_assigneePriority = PriorityEnum.HIGH.getValue();
	_submitterPriority = PriorityEnum.NORMAL.getValue();
    }

    public DbPriority(long assigneePriority, long submitterPriority) {
	_assigneePriority = assigneePriority;
	_submitterPriority = submitterPriority;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DbPriority other = (DbPriority) obj;
	if (_assigneePriority != other._assigneePriority)
	    return false;
	if (_submitterPriority != other._submitterPriority)
	    return false;
	return true;
    }

    @Column(name = "ASSIGNEE_PRIORITY", nullable = true, updatable = true)
    public long getAssigneePriority() {
	return _assigneePriority;
    }

    @Column(name = "SUBMITTER_PRIORITY", nullable = true, updatable = true)
    public long getSubmitterPriority() {
	return _submitterPriority;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (_assigneePriority ^ (_assigneePriority >>> 32));
	result = prime * result + (int) (_submitterPriority ^ (_submitterPriority >>> 32));
	return result;
    }

    public void setAssigneePriority(long assigneePriority) {
	_assigneePriority = assigneePriority;
    }

    public void setSubmitterPriority(long submitterPriority) {
	_submitterPriority = submitterPriority;
    }

    @Override
    public String toString() {
	return "DbPriority [_assigneePriority=" + _assigneePriority + ", _submitterPriority=" + _submitterPriority
		+ "]";
    }
}

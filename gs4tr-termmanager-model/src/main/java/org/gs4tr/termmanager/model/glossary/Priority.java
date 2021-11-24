package org.gs4tr.termmanager.model.glossary;

import java.io.Serializable;

public class Priority implements Serializable {

    private static final long serialVersionUID = 270152384132579417L;

    private long _assigneePriority;

    private long _submitterPriority;

    public Priority() {
	_assigneePriority = PriorityEnum.HIGH.getValue();
	_submitterPriority = PriorityEnum.NORMAL.getValue();
    }

    public Priority(long assigneePriority, long submitterPriority) {
	_assigneePriority = assigneePriority;
	_submitterPriority = submitterPriority;
    }

    public long getAssigneePriority() {
	return _assigneePriority;
    }

    public long getSubmitterPriority() {
	return _submitterPriority;
    }

    public void setAssigneePriority(long assigneePriority) {
	_assigneePriority = assigneePriority;
    }

    public void setSubmitterPriority(long submitterPriority) {
	_submitterPriority = submitterPriority;
    }
}

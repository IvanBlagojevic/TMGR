package org.gs4tr.termmanager.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Embeddable
public class EntityStatusPriority implements Serializable {

    private static final long serialVersionUID = 8762684620166821856L;

    private Date _dateCompleted;

    private long _priority;

    private long _priorityAssignee;

    private ItemStatusType _status;

    public EntityStatusPriority() {
	_priority = Priority.NORMAL.getValue();
	_priorityAssignee = Priority.HIGH.getValue();
	_status = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;
    }

    public EntityStatusPriority(long priority, long priorityAssignee, ItemStatusType status) {
	_priority = priority;
	_priorityAssignee = priorityAssignee;
	_status = status;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	EntityStatusPriority other = (EntityStatusPriority) obj;
	if (_status == null) {
	    return other._status == null;
	} else
	    return _status.equals(other._status);
    }

    @Column(name = "DATE_COMPLETED", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateCompleted() {
	return _dateCompleted;
    }

    @Column(name = "PRIORITY", nullable = false)
    public long getPriority() {
	return _priority;
    }

    @Column(name = "PRIORITY_ASSIGNEE", nullable = false)
    public long getPriorityAssignee() {
	return _priorityAssignee;
    }

    @Column(name = "STATUS", length = 30, nullable = false)
    @Type(type = "org.gs4tr.foundation.modules.dao.hibernate.usertype.ItemStatusTypeUserType", parameters = @Parameter(name = "typeInstanceClassName", value = "org.gs4tr.foundation.modules.entities.model.types.ItemStatusType"))
    public ItemStatusType getStatus() {
	return _status;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	return prime * result + ((_status == null) ? 0 : _status.hashCode());
    }

    public void setDateCompleted(Date dateCompleted) {
	_dateCompleted = dateCompleted;
    }

    public void setPriority(long priority) {
	_priority = priority;
    }

    public void setPriorityAssignee(long priorityAssignee) {
	_priorityAssignee = priorityAssignee;
    }

    public void setStatus(ItemStatusType status) {
	_status = status;
    }
}

package org.gs4tr.termmanager.model;

public class StatisticsUpdateCommand {

    private long _addedApproved = 0;

    private long _addedPending = 0;

    private long _approved = 0;

    private long _deleted = 0;

    private long _demoted = 0;

    private long _onHold = 0;

    private long _updated = 0;

    public long getAddedApproved() {
	return _addedApproved;
    }

    public long getAddedPending() {
	return _addedPending;
    }

    public long getApproved() {
	return _approved;
    }

    public long getDeleted() {
	return _deleted;
    }

    public long getDemoted() {
	return _demoted;
    }

    public long getOnHold() {
	return _onHold;
    }

    public long getUpdated() {
	return _updated;
    }

    public void incrementAddedApproved() {
	long newValue = getAddedApproved() + 1;
	setAddedApproved(newValue);
    }

    public void incrementAddedPending() {
	long newValue = getAddedPending() + 1;
	setAddedPending(newValue);
    }

    public void incrementApproved() {
	long newValue = getApproved() + 1;
	setApproved(newValue);
    }

    public void incrementDeleted() {
	long newValue = getDeleted() + 1;
	setDeleted(newValue);
    }

    // TODO: Statistic should be updated for all needed statuses
    public void incrementDemoted() {
	long newValue = getDemoted() + 1;
	setDemoted(newValue);
    }

    public void incrementOnHold() {
	long newValue = getOnHold() + 1;
	setOnHold(newValue);
    }

    public void incrementUpdated() {
	long newValue = getUpdated() + 1;
	setUpdated(newValue);
    }

    public void setAddedApproved(long addedApproved) {
	_addedApproved = addedApproved;
    }

    public void setAddedPending(long addedPending) {
	_addedPending = addedPending;
    }

    public void setApproved(long approved) {
	_approved = approved;
    }

    public void setDeleted(long deleted) {
	_deleted = deleted;
    }

    public void setDemoted(long demoted) {
	_demoted = demoted;
    }

    public void setOnHold(long onHold) {
	_onHold = onHold;
    }

    public void setUpdated(long updated) {
	_updated = updated;
    }
}

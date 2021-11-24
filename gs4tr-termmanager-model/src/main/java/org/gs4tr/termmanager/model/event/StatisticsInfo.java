package org.gs4tr.termmanager.model.event;

import java.util.concurrent.atomic.AtomicLong;

public class StatisticsInfo {

    private final AtomicLong _addedApprovedCount;

    private final AtomicLong _addedBlacklistedCount;

    private final AtomicLong _addedOnHoldCount;

    private final AtomicLong _addedPendingApprovalCount;

    private final AtomicLong _approvedCount;

    private final AtomicLong _blackListedCount;

    private final AtomicLong _deletedCount;

    private final String _languageId;

    private final AtomicLong _onHoldCount;

    private final AtomicLong _pendingApprovalCount;

    private final Long _projectId;

    private final AtomicLong _updatedCount;

    public StatisticsInfo(Long projectId, String languageId) {

	_projectId = projectId;
	_languageId = languageId;

	_addedApprovedCount = new AtomicLong();
	_approvedCount = new AtomicLong();
	_addedPendingApprovalCount = new AtomicLong();
	_pendingApprovalCount = new AtomicLong();
	_addedOnHoldCount = new AtomicLong();
	_onHoldCount = new AtomicLong();
	_addedBlacklistedCount = new AtomicLong();
	_blackListedCount = new AtomicLong();
	_deletedCount = new AtomicLong();
	_updatedCount = new AtomicLong();

    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o == null || getClass() != o.getClass())
	    return false;

	StatisticsInfo that = (StatisticsInfo) o;

	if (!_languageId.equals(that._languageId))
	    return false;
	return _projectId.equals(that._projectId);
    }

    public AtomicLong getAddedApprovedCount() {
	return _addedApprovedCount;
    }

    public AtomicLong getAddedBlacklistedCount() {
	return _addedBlacklistedCount;
    }

    public AtomicLong getAddedOnHoldCount() {
	return _addedOnHoldCount;
    }

    public AtomicLong getAddedPendingApprovalCount() {
	return _addedPendingApprovalCount;
    }

    public AtomicLong getApprovedCount() {
	return _approvedCount;
    }

    public AtomicLong getBlackListedCount() {
	return _blackListedCount;
    }

    public AtomicLong getDeletedCount() {
	return _deletedCount;
    }

    public String getLanguageId() {
	return _languageId;
    }

    public AtomicLong getOnHoldCount() {
	return _onHoldCount;
    }

    public AtomicLong getPendingApprovalCount() {
	return _pendingApprovalCount;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public AtomicLong getUpdatedCount() {
	return _updatedCount;
    }

    @Override
    public int hashCode() {
	int result = _languageId.hashCode();
	result = 31 * result + _projectId.hashCode();
	return result;
    }

    public long incrementAddedApprovedCount() {
	return getAddedApprovedCount().incrementAndGet();
    }

    public long incrementAddedBlacklistedCount() {
	return getAddedBlacklistedCount().incrementAndGet();
    }

    public long incrementAddedOnHoldCount() {
	return getAddedOnHoldCount().incrementAndGet();
    }

    public long incrementAddedPendingCount() {
	return getAddedPendingApprovalCount().incrementAndGet();
    }

    public long incrementApprovedCount() {
	return getApprovedCount().incrementAndGet();
    }

    public long incrementBlacklistedCount() {
	return getBlackListedCount().incrementAndGet();
    }

    public long incrementDeletedCount() {
	return getDeletedCount().incrementAndGet();
    }

    public long incrementOnHoldCount() {
	return getOnHoldCount().incrementAndGet();
    }

    public long incrementPendingCount() {
	return getPendingApprovalCount().incrementAndGet();
    }

    public long incrementUpdatedCount() {
	return getUpdatedCount().incrementAndGet();
    }

}

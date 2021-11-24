package org.gs4tr.termmanager.model.reindex;

import java.util.List;

public class BackupSearchCommand {

    private boolean _fetchHistory = false;

    private List<Long> _projectIds;

    private List<String> _shortCodes;

    public BackupSearchCommand(List<Long> projectIds, List<String> shortCodes) {
	_projectIds = projectIds;
	_shortCodes = shortCodes;
    }

    public BackupSearchCommand(List<Long> projectIds, List<String> shortCodes, boolean fetchHistory) {
	_projectIds = projectIds;
	_shortCodes = shortCodes;
	_fetchHistory = fetchHistory;
    }

    public List<Long> getProjectIds() {
	return _projectIds;
    }

    public List<String> getShortCodes() {
	return _shortCodes;
    }

    public boolean isFetchHistory() {
	return _fetchHistory;
    }
}

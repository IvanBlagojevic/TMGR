package org.gs4tr.termmanager.service.impl;

import java.util.Collection;

import org.gs4tr.termmanager.dao.backup.RegularBackupCleanerDAO;
import org.gs4tr.termmanager.service.RegularBackupCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegularBackupCleanerImpl implements RegularBackupCleaner {

    @Autowired
    private RegularBackupCleanerDAO _regularBackupCleanerDAO;

    @Override
    public void clearCountsAndStatistics(Collection<Long> projectIds, int chunkSize) {
	getRegularBackupCleanerDAO().clearCountsAndStatistics(projectIds, chunkSize);
    }

    @Override
    public void deleteByProjectIds(Collection<Long> projectIds, int chunkSize) {
	getRegularBackupCleanerDAO().deleteByProjectIds(projectIds, chunkSize);
    }

    @Override
    public void deleteHiddenTerms(int chunkSize) {
	getRegularBackupCleanerDAO().deleteHiddenTerms(chunkSize);
    }

    private RegularBackupCleanerDAO getRegularBackupCleanerDAO() {
	return _regularBackupCleanerDAO;
    }
}

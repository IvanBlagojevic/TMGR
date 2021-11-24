package org.gs4tr.termmanager.service.impl;

import java.util.Collection;

import org.gs4tr.termmanager.dao.backup.SubmissionBackupCleanerDAO;
import org.gs4tr.termmanager.service.SubmissionBackupCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmissionBackupCleanerImpl implements SubmissionBackupCleaner {

    @Autowired
    private SubmissionBackupCleanerDAO _submissionBackupCleanerDAO;

    @Override
    public void deleteSubmissionsByProjectIds(Collection<Long> projectIds, int chunkSize) {
	getSubmissionBackupCleanerDAO().deleteSubmissionsByProjectIds(projectIds, chunkSize);
    }

    private SubmissionBackupCleanerDAO getSubmissionBackupCleanerDAO() {
	return _submissionBackupCleanerDAO;
    }
}

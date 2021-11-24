package org.gs4tr.termmanager.service;

import java.util.Collection;

public interface SubmissionBackupCleaner {

    void deleteSubmissionsByProjectIds(Collection<Long> projectIds, int chunkSize);
}

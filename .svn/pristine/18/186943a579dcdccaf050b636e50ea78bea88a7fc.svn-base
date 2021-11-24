package org.gs4tr.termmanager.service;

import java.util.Collection;

public interface RegularBackupCleaner {

    void clearCountsAndStatistics(Collection<Long> projectIds, int chunkSize);

    void deleteByProjectIds(Collection<Long> projectIds, int chunkSize);

    void deleteHiddenTerms(int chunkSize);
}

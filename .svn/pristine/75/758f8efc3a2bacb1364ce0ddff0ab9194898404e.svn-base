package org.gs4tr.termmanager.dao.backup;

import java.util.Collection;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;

public interface RegularBackupCleanerDAO extends GenericDao<DbTermEntry, String> {

    void clearCountsAndStatistics(Collection<Long> projectIds, int chunkSize);

    void deleteByProjectIds(Collection<Long> projectIds, int chunkSize);

    void deleteHiddenTerms(int chunkSize);

}

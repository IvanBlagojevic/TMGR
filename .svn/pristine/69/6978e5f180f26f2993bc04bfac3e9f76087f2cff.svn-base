package org.gs4tr.termmanager.dao.backup;

import java.util.Collection;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;

public interface SubmissionBackupCleanerDAO extends GenericDao<DbSubmissionTermEntry, String> {

    void deleteSubmissionsByProjectIds(Collection<Long> projectIds, int chunkSize);
}

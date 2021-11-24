package org.gs4tr.termmanager.dao.backup;

import java.util.Collection;
import java.util.List;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;

public interface DbSubmissionTermEntryDAO extends GenericDao<DbSubmissionTermEntry, String> {

    DbSubmissionTermEntry findByUuid(String uuid, boolean fetchChilds);

    DbSubmissionTermEntry findByUuid(String uuid);

    List<DbSubmissionTermEntry> findByUuids(Collection<String> uuids, boolean fetchChilds);

    PagedList<DbSubmissionTermEntry> getDbSubmissionTermEntries(PagedListInfo info, BackupSearchCommand command);

    PagedList<DbSubmissionTermEntry> getDbSubmissionTermEntriesForRecode(PagedListInfo info,
	    BackupSearchCommand command);

    long getTotalCount(List<Long> projectIds);

    void saveOrUpdateLocked(Collection<DbSubmissionTermEntry> incoming) throws BackupException;

    int updateSubmissionTermLanguagesByProjectId(String languageFrom, String languageTo, Long projectId);
}

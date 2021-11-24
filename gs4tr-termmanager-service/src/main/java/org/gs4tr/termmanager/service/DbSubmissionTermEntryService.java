package org.gs4tr.termmanager.service;

import java.util.Collection;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;

public interface DbSubmissionTermEntryService {

    DbSubmissionTermEntry findById(String id, boolean fetchChilds);

    List<DbSubmissionTermEntry> findByIds(Collection<String> ids, boolean fetchChilds);

    PagedList<DbSubmissionTermEntry> getDbSubmissionTermEntries(PagedListInfo info, BackupSearchCommand command);

    long getTotalCount(List<Long> projectIds);

    void updateSubmissionTermLanguagesByProjectId(String languageFrom, String languageTo, Long projectId);
}

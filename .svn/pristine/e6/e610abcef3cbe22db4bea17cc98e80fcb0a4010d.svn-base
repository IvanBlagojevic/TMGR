package org.gs4tr.termmanager.service;

import java.util.Collection;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;

public interface DbTermEntryService {

    DbTermEntry findById(String id, boolean fetchChilds);

    List<DbTermEntry> findByIds(Collection<String> ids, boolean fetchChilds);

    PagedList<DbTermEntry> getDbTermEntries(PagedListInfo info, BackupSearchCommand command);

    long getTotalCount(List<Long> projectIds);
}

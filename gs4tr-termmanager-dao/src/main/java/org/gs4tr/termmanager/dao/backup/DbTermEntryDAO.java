package org.gs4tr.termmanager.dao.backup;

import java.util.Collection;
import java.util.List;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;

public interface DbTermEntryDAO extends GenericDao<DbTermEntry, String> {

    DbTermEntry findByUUID(String uuid);

    DbTermEntry findByUuid(String uuid, boolean fetchChilds);

    List<DbTermEntry> findByUuids(Collection<String> uuids, boolean fetchChilds);

    PagedList<DbTermEntry> getDbTermEntries(PagedListInfo info, BackupSearchCommand command);

    PagedList<DbTermEntry> getDbTermEntriesForRecode(PagedListInfo info, BackupSearchCommand command);

    long getTotalCount(List<Long> projectIds);

    void saveOrUpdateLocked(Collection<DbTermEntry> incoming);

    <T> void updateEntitiesForRecodeOrClone(Collection<T> entities);

    void updateTermLanguage(Long projectId, String languageFrom, String languageTo);
}

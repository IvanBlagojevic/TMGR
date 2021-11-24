package org.gs4tr.termmanager.service.impl;

import java.util.Collection;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.backup.DbTermEntryDAO;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;
import org.gs4tr.termmanager.service.DbTermEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DbTermEntryServiceImpl implements DbTermEntryService {

    @Autowired
    private DbTermEntryDAO _dbTermEntryDAO;

    @Transactional(readOnly = true)
    @Override
    public DbTermEntry findById(String id, boolean fetchChilds) {
	return getDbTermEntryDAO().findByUuid(id, fetchChilds);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DbTermEntry> findByIds(Collection<String> ids, boolean fetchChilds) {
	return getDbTermEntryDAO().findByUuids(ids, fetchChilds);
    }

    @Override
    public PagedList<DbTermEntry> getDbTermEntries(PagedListInfo info, BackupSearchCommand command) {
	return getDbTermEntryDAO().getDbTermEntries(info, command);
    }

    @Transactional(readOnly = true)
    @Override
    public long getTotalCount(List<Long> projectIds) {
	return getDbTermEntryDAO().getTotalCount(projectIds);
    }

    private DbTermEntryDAO getDbTermEntryDAO() {
	return _dbTermEntryDAO;
    }
}

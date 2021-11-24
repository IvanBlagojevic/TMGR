package org.gs4tr.termmanager.service.impl;

import java.util.Collection;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.backup.DbSubmissionTermEntryDAO;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;
import org.gs4tr.termmanager.service.DbSubmissionTermEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DbSubmissionTermEntryServiceImpl implements DbSubmissionTermEntryService {

    @Autowired
    private DbSubmissionTermEntryDAO _dbSubmissionTermEntryDAO;

    @Transactional(readOnly = true)
    @Override
    public DbSubmissionTermEntry findById(String id, boolean fetchChilds) {
	return getDbSubmissionTermEntryDAO().findByUuid(id, fetchChilds);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DbSubmissionTermEntry> findByIds(Collection<String> ids, boolean fetchChilds) {
	return getDbSubmissionTermEntryDAO().findByUuids(ids, fetchChilds);
    }

    @Override
    public PagedList<DbSubmissionTermEntry> getDbSubmissionTermEntries(PagedListInfo info,
	    BackupSearchCommand command) {
	return getDbSubmissionTermEntryDAO().getDbSubmissionTermEntries(info, command);
    }

    @Transactional(readOnly = true)
    @Override
    public long getTotalCount(List<Long> projectIds) {
	return getDbSubmissionTermEntryDAO().getTotalCount(projectIds);
    }

    @Transactional
    @Override
    public void updateSubmissionTermLanguagesByProjectId(String languageFrom, String languageTo, Long projectId) {
	getDbSubmissionTermEntryDAO().updateSubmissionTermLanguagesByProjectId(languageFrom, languageTo, projectId);
    }

    private DbSubmissionTermEntryDAO getDbSubmissionTermEntryDAO() {
	return _dbSubmissionTermEntryDAO;
    }
}

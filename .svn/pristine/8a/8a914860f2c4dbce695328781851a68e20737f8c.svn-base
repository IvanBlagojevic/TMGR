package org.gs4tr.termmanager.io.edd.handler;

import java.util.List;

import org.gs4tr.termmanager.dao.backup.DbSubmissionTermEntryDAO;
import org.gs4tr.termmanager.dao.backup.DbTermEntryDAO;
import org.gs4tr.termmanager.io.config.IndexConfiguration;
import org.gs4tr.termmanager.io.edd.api.Handler;
import org.gs4tr.termmanager.io.edd.event.ProcessDataEvent;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbSubmissionTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbTermEntryConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BackupUpdateHandler extends AbstractHandler implements Handler<ProcessDataEvent> {

    @Autowired
    private DbSubmissionTermEntryDAO _dbSubmissionTermEntryDAO;

    @Autowired
    private DbTermEntryDAO _dbTermEntryDAO;

    @Autowired
    private IndexConfiguration _indexConfiguration;

    @Override
    public void onEvent(ProcessDataEvent event) throws EventException {
	validate(event);
	logMessage(String.format("Writing [%d] entities into db backup is STARTED.", event.getData().size()));
	updateBackup(event);
	logMessage(String.format("Writing [%d] entities into db backup is FINISHED.", event.getData().size()));
    }

    private DbSubmissionTermEntryDAO getDbSubmissionTermEntryDAO() {
	return _dbSubmissionTermEntryDAO;
    }

    private DbTermEntryDAO getDbTermEntryDAO() {
	return _dbTermEntryDAO;
    }

    private IndexConfiguration getIndexConfiguration() {
	return _indexConfiguration;
    }

    private boolean isRegular(ProcessDataEvent event) {
	return getIndexConfiguration().getRegular().equals(event.getCollection());
    }

    private void updateBackup(ProcessDataEvent event) {
	List<TermEntry> termEntries = event.getData();
	if (isRegular(event)) {
	    getDbTermEntryDAO().saveOrUpdateLocked(DbTermEntryConverter.convertToDbTermEntries(termEntries));
	} else {
	    getDbSubmissionTermEntryDAO()
		    .saveOrUpdateLocked(DbSubmissionTermEntryConverter.convertToDbTermEntries(termEntries));
	}
    }

    @Override
    protected void logMessage(String message) {
	LOGGER.info(message);
    }
}

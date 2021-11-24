package org.gs4tr.termmanager.io.edd.handler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.gs4tr.termmanager.dao.backup.DbSubmissionTermEntryDAO;
import org.gs4tr.termmanager.dao.backup.DbTermEntryDAO;
import org.gs4tr.termmanager.io.config.IndexConfiguration;
import org.gs4tr.termmanager.io.config.IndexConnectionHandler;
import org.gs4tr.termmanager.io.edd.api.Handler;
import org.gs4tr.termmanager.io.edd.event.RevertDataEvent;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbSubmissionTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.tm3.api.TmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SolrRevertHandler extends AbstractHandler implements Handler<RevertDataEvent> {

    @Autowired
    private IndexConnectionHandler _connectionHandler;

    @Autowired
    private DbSubmissionTermEntryDAO _dbSubmissionTermEntryDAO;

    @Autowired
    @Qualifier("dbTermEntryDAO")
    private DbTermEntryDAO _dbTermEntryDAO;

    @Autowired
    private IndexConfiguration _indexConfiguration;

    public DbSubmissionTermEntryDAO getDbSubmissionTermEntryDAO() {
	return _dbSubmissionTermEntryDAO;
    }

    public DbTermEntryDAO getDbTermEntryDAO() {
	return _dbTermEntryDAO;
    }

    public IndexConfiguration getIndexConfiguration() {
	return _indexConfiguration;
    }

    @Override
    public void onEvent(RevertDataEvent event) throws EventException {
	validate(event);
	logMessage(String.format("Reverting [%d] entities from solr.", event.getData().size()));

	try {
	    revertSolr(event);
	} catch (TmException e) {
	    throw new EventException(e.getMessage(), e);
	}
	logMessage(String.format("Reverting [%d] entities from solr is FINISHED.", event.getData().size()));
    }

    private void deleteOrUpdateRegular(String termEntryId, TermEntry termEntry) throws TmException {
	getRegularUpdater().delete(termEntry);

	DbTermEntry dbTermEntry = getDbTermEntryDAO().findByUUID(termEntryId);

	if (Objects.nonNull(dbTermEntry)) {
	    TermEntry updatedTermEntry = DbTermEntryConverter.convertToTermEntry(dbTermEntry);
	    getRegularUpdater().update(updatedTermEntry);
	}
    }

    private void deleteOrUpdateSubmission(String termEntryId, TermEntry termEntry) throws TmException {
	getSubmissionUpdater().delete(termEntry);
	DbSubmissionTermEntry dbSubmission = getDbSubmissionTermEntryDAO().findByUuid(termEntryId);

	if (Objects.nonNull(dbSubmission)) {
	    TermEntry updatedSubTermEntry = DbSubmissionTermEntryConverter.convertToTermEntry(dbSubmission);
	    getSubmissionUpdater().update(updatedSubTermEntry);
	}
    }

    private IndexConnectionHandler getConnectionHandler() {
	return _connectionHandler;
    }

    private ITmgrGlossaryBrowser getRegularBrowser() throws TmException {
	String regularCollection = getIndexConfiguration().getRegular();

	ITmgrGlossaryConnector connector = getConnectionHandler().connect(regularCollection);

	return connector.getTmgrBrowser();
    }

    private ITmgrGlossaryUpdater getRegularUpdater() throws TmException {
	String regularCollection = getIndexConfiguration().getRegular();

	ITmgrGlossaryConnector connector = getConnectionHandler().connect(regularCollection);

	return connector.getTmgrUpdater();
    }

    private ITmgrGlossaryBrowser getSubmissionBrowser() throws TmException {
	String submissionCollection = getIndexConfiguration().getSubmission();

	ITmgrGlossaryConnector connector = getConnectionHandler().connect(submissionCollection);

	return connector.getTmgrBrowser();
    }

    private ITmgrGlossaryUpdater getSubmissionUpdater() throws TmException {
	String submissionCollection = getIndexConfiguration().getSubmission();

	ITmgrGlossaryConnector connector = getConnectionHandler().connect(submissionCollection);

	return connector.getTmgrUpdater();
    }

    private boolean isRegular(String collection) {
	return getIndexConfiguration().getRegular().equals(collection);
    }

    private void revertSolr(RevertDataEvent event) throws TmException {

	String collection = event.getCollection();

	List<TermEntry> termEntries = event.getData();

	Long projectId = termEntries.get(0).getProjectId();

	List<String> termEntryIds = termEntries.stream().map(TermEntry::getUuId).collect(Collectors.toList());

	for (String termEntryId : termEntryIds) {

	    if (isRegular(collection)) {
		TermEntry regularTermEntry = getRegularBrowser().findById(termEntryId, projectId);

		deleteOrUpdateRegular(termEntryId, regularTermEntry);

	    } else {
		TermEntry subTermEntry = getSubmissionBrowser().findById(termEntryId, projectId);
		deleteOrUpdateSubmission(termEntryId, subTermEntry);
	    }
	}
    }

    @Override
    protected void logMessage(String message) {
	LOGGER.info(message);
    }
}

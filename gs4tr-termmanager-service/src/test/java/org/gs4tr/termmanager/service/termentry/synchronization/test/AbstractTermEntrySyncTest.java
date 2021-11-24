package org.gs4tr.termmanager.service.termentry.synchronization.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation3.callback.AbstractValidationCallback;
import org.gs4tr.foundation3.callback.ImportCallback;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.model.ImportErrorAction;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.TmgrSolrConnector;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.GetTermEntryCallback;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.AbstractSolrGlossaryTest;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.persistence.importer.ITmgrGlossaryImporter;
import org.gs4tr.termmanager.service.persistence.importer.impl.TmgrGlossaryImporter;
import org.gs4tr.tm3.api.TmException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Created by emisia on 6/8/17.
 */
public abstract class AbstractTermEntrySyncTest extends AbstractSolrGlossaryTest {

    protected static final String BLACKLISTED = ItemStatusTypeHolder.BLACKLISTED.getName();

    protected static final String DE = "de";

    protected static final String DE_LANGUAGE_ID = "de-DE";

    protected static final String EN = "en";

    protected static final String EN_LANGUAGE_ID = "en-US";

    protected static final String FR_LANGUAGE_ID = "fr-FR";

    protected static final String IN_FINAL_REVIEW = ItemStatusTypeHolder.IN_FINAL_REVIEW.getName();

    protected static final String IN_TRANSLATION_REVIEW = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName();

    protected static final boolean IS_FORBIDDEN = false;

    protected static final String PROCESSED = "PROCESSED";

    protected static final Long PROJECT_ID = 1L;

    protected static final String PROJECT_NAME = "Skype";

    protected static final String SHORT_CODE = "SKY000001";

    protected static final String TERM_ENTRY_ID = "xlsx-import-007-007";

    protected static final String USER_MODIFIED = "donnie brasko";

    protected static final String USER_NAME = "super";

    protected static final String WAITING = ItemStatusTypeHolder.WAITING.getName();

    protected ImportOptionsModel _importModel;

    protected ImportProgressInfo _importProgressInfo;

    protected TermEntryReader _reader;

    protected TermEntry _solrTermEntry;

    protected ITermEntrySynchronizer _synchronizer;

    protected GetTermEntryCallback _termEntryCallback;

    @Mock
    protected Consumer<Long> _timeConsumer;

    protected ITmgrGlossaryImporter _tmgrImporter;

    protected TermEntry _xlsTermEntry;

    @Before
    public void beforeTermEntryOverwriteTests() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();
	connector.getTmgrUpdater().deleteAll();

	_tmgrImporter = new TmgrGlossaryImporter((TmgrSolrConnector) connector);

	_solrTermEntry = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	_solrTermEntry.setProjectName(PROJECT_NAME);
	_solrTermEntry.setUuId(TERM_ENTRY_ID);

	Term solrEnTerm = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);

	_solrTermEntry.addTerm(solrEnTerm);

	_xlsTermEntry = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	_xlsTermEntry.setProjectName(PROJECT_NAME);
	_xlsTermEntry.setUuId(TERM_ENTRY_ID);

	_importModel = new ImportOptionsModel();
	_importModel.setSyncLanguageId(EN_LANGUAGE_ID);
	_importModel.setProjectId(PROJECT_ID);
	_importModel.setProjectName(PROJECT_NAME);
	_importModel.setProjectShortCode(SHORT_CODE);
	_importModel.setImportErrorAction(ImportErrorAction.SKIP);
	_importModel.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);

	_synchronizer = initTermEntrySynchronizer();
	_synchronizer.initialize(connector, _importModel);
    }

    // TERII-4703
    @Test
    public void syncTermAttributeIgnoreCase() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();

	final String termEntryId = UUID.randomUUID().toString();

	TermEntry existing = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	existing.setProjectName(PROJECT_NAME);
	existing.setUuId(termEntryId);

	TermEntry incoming = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	incoming.setProjectName(PROJECT_NAME);
	incoming.setUuId(termEntryId);

	String existingType = "type";
	Term existingEnTerm = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	existingEnTerm.addDescription(new Description(existingType, "value"));

	String newValue = "new value";
	Term incomingEnTerm = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	incomingEnTerm.addDescription(new Description("TYPE", newValue));

	prepareTermEntry(existing, new Term[] { existingEnTerm }, true);
	prepareTermEntry(incoming, new Term[] { incomingEnTerm }, false);

	Set<String> allowedDesc = new HashSet<>();
	allowedDesc.add(existingType);

	Map<String, Set<String>> allowedDescMap = new HashMap<>();
	allowedDescMap.put(Description.ATTRIBUTE, allowedDesc);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID }));
	_importModel.setAllowedTermDescriptions(allowedDescMap);

	setUpConfiguration(incoming);

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	assertOverwriteTermAttributeIgnoreCase(termEntryId, newValue, existingType);
    }

    // TERII-4703
    @Test
    public void syncTermEntryAttributeIgnoreCase() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();

	final String termEntryId = UUID.randomUUID().toString();

	String existingType = "type";

	TermEntry existing = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	existing.setProjectName(PROJECT_NAME);
	existing.setUuId(termEntryId);
	existing.addDescription(new Description(existingType, "value"));

	String newValue = "new value";

	TermEntry incoming = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	incoming.setProjectName(PROJECT_NAME);
	incoming.setUuId(termEntryId);
	incoming.addDescription(new Description("TYPE", newValue));

	Term enTerm = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);

	prepareTermEntry(existing, new Term[] { enTerm }, true);
	prepareTermEntry(incoming, new Term[] { enTerm }, false);

	Set<String> allowedDesc = new HashSet<>();
	allowedDesc.add(existingType);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID }));
	_importModel.setAllowedTermEntryAttributes(allowedDesc);

	setUpConfiguration(incoming);

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	assertOverwriteTermEntryAttributeIgnoreCase(termEntryId, newValue, existingType);
    }

    protected abstract void assertOverwriteTermAttributeIgnoreCase(String termEntryId, String newValue,
	    String existingType) throws TmException;

    protected abstract void assertOverwriteTermEntryAttributeIgnoreCase(String termEntryId, String newValue,
	    String existingType) throws TmException;

    protected Description createDescription(String type, String value) {
	Description description = new Description();
	description.setType(type);
	description.setValue(value);
	description.setUuid(UUID.randomUUID().toString());
	return description;
    }

    protected Term createTerm(String languageId, String name, boolean forbidden, String status, String user) {
	Term term = new Term();
	term.setLanguageId(languageId);
	term.setName(name);
	term.setForbidden(forbidden);
	term.setStatus(status);
	term.setUserCreated(user);
	term.setUserModified(user);
	term.setUuId(UUID.randomUUID().toString());
	term.setDateCreated(new Date().getTime());
	term.setDateModified(new Date().getTime());
	return term;
    }

    protected Term createTermWithDesc(String languageId, String name, boolean forbidden, String status, String userName,
	    Description description) {
	Term term = createTerm(languageId, name, forbidden, status, userName);
	term.addDescription(description);
	return term;
    }

    protected void deleteTermEntries(List<TermEntry> termEntries) throws TmException {
	if (CollectionUtils.isEmpty(termEntries)) {
	    return;
	}
	for (final TermEntry termEntry : termEntries) {
	    Set<Map.Entry<String, Set<Term>>> entries = termEntry.getLanguageTerms().entrySet();
	    for (final Map.Entry<String, Set<Term>> entry : entries) {
		final Date date = new Date();
		Set<Term> terms = entry.getValue();
		for (final Term term : terms) {
		    term.setDisabled(Boolean.TRUE);
		    term.setDateModified(date.getTime());
		    term.setUserModified(USER_MODIFIED);
		}
	    }
	}
	getRegularConnector().getTmgrUpdater().update(termEntries);
    }

    protected ITmgrGlossaryBrowser getBrowser() throws TmException {
	return getRegularConnector().getTmgrBrowser();
    }

    protected ImportOptionsModel getImportModel() {
	return _importModel;
    }

    protected ImportProgressInfo getImportProgressInfo() {
	return _importProgressInfo;
    }

    protected TermEntryReader getReader() {
	return _reader;
    }

    protected ITmgrGlossarySearcher getSearcher() throws TmException {
	return getRegularConnector().getTmgrSearcher();
    }

    protected TermEntry getSolrTermEntry() {
	return _solrTermEntry;
    }

    protected ITermEntrySynchronizer getSynchronizer() {
	return _synchronizer;
    }

    protected GetTermEntryCallback getTermEntryCallback() {
	return _termEntryCallback;
    }

    protected Consumer<Long> getTimeConsumer() {
	return _timeConsumer;
    }

    protected TermEntry getXlsTermEntry() {
	return _xlsTermEntry;
    }

    protected abstract ITermEntrySynchronizer initTermEntrySynchronizer();

    protected TermEntryReader prepareReader(TermEntry... termEntries) {
	TermEntryReader reader = new TermEntryReader() {
	    @Override
	    public void readTermEntries(ImportCallback importCallback) {
		for (TermEntry termEntry : termEntries) {
		    importCallback.handleTermEntry(termEntry);
		}
		importCallback.handlePercentage(100);
	    }

	    @Override
	    public void validate(AbstractValidationCallback callback) {
	    }
	};
	return reader;
    }

    protected void prepareTermEntry(TermEntry termEntry, Term[] languageTerms, boolean saveTermEntry)
	    throws TmException {
	for (int i = 0; i < languageTerms.length; i++) {
	    termEntry.addTerm(languageTerms[i]);
	}

	if (saveTermEntry) {
	    ITmgrGlossaryConnector connector = getRegularConnector();
	    ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	    updater.save(termEntry);
	}
    }

    protected void setTermEntriesToBeIdentical(List<TermEntry> termEntries) {
	assertEquals(2, termEntries.size());
	Iterator<TermEntry> iterator = termEntries.iterator();
	TermEntry existing = iterator.next();
	TermEntry incoming = iterator.next();
	incoming.setUuId(existing.getUuId());
    }

    protected void setUpConfiguration(final TermEntry xlsTermEntry) {
	_termEntryCallback = item -> (TermEntry) item;

	_reader = new TermEntryReader() {
	    @Override
	    public void readTermEntries(ImportCallback importCallback) {
		importCallback.handleTermEntry(xlsTermEntry);
		importCallback.handlePercentage(100);
	    }

	    @Override
	    public void validate(AbstractValidationCallback callback) {
	    }
	};

	_importProgressInfo = new ImportProgressInfo(1);
    }

    protected void validateTermEntries(List<TermEntry> filteredEntries) {
	assertNotNull(filteredEntries);
	assertEquals(1, filteredEntries.size());

	// Validate that all terms are enabled.
	for (TermEntry termEntry : filteredEntries) {
	    Set<Map.Entry<String, Set<Term>>> entries = termEntry.getLanguageTerms().entrySet();
	    for (Map.Entry<String, Set<Term>> entry : entries) {
		Set<Term> terms = entry.getValue();
		for (Term term : terms) {
		    assertFalse(term.isDisabled());
		}
	    }
	}
    }
}

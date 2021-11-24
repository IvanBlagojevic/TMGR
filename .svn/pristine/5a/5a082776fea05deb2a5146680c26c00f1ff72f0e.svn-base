package org.gs4tr.termmanager.service.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

import org.gs4tr.foundation3.callback.AbstractValidationCallback;
import org.gs4tr.foundation3.callback.ImportCallback;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.solr.TmgrSolrConnector;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.GetTermEntryCallback;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.AbstractSolrGlossaryTest;
import org.gs4tr.termmanager.service.persistence.importer.ITmgrGlossaryImporter;
import org.gs4tr.termmanager.service.persistence.importer.impl.TmgrGlossaryImporter;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

public class TmgrGlossaryImporterTest extends AbstractSolrGlossaryTest {

    private Long _projectId = 1L;

    private String _shortCode = "TES000001";

    private String _syncLanguageId = "sr";

    @Mock
    private Consumer<Long> _timeConsumer;

    private String _user = "super";

    @Test
    public void testHandleImport() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossaryImporter importer = new TmgrGlossaryImporter((TmgrSolrConnector) connector);

	TermEntryReader reader = new TermEntryReader() {
	    @Override
	    public void readTermEntries(ImportCallback importCallback) {
		Term term = new Term(getSyncLanguageId(), "term text", false, "PROCESSED", getUser());
		term.setUuId(UUID.randomUUID().toString());
		term.addDescription(new Description("context", "term context description"));
		term.setDateModified(new Date().getTime());

		TermEntry item = new TermEntry(getProjectId(), getShortCode(), getUser());
		item.addDescription(new Description("definition", "term entry attribute definition value"));
		item.addTerm(term);
		importCallback.handleTermEntry(item);
		importCallback.handlePercentage(50);
	    }

	    @Override
	    public void validate(AbstractValidationCallback callback) {

	    }
	};

	GetTermEntryCallback getTermEntryCallback = item -> (TermEntry) item;

	ImportOptionsModel importModel = new ImportOptionsModel();
	importModel.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);
	importModel.setProjectId(getProjectId());
	importModel.setSyncLanguageId(getSyncLanguageId());

	ImportSummary importSummary = new ImportSummary(1);

	importer.handleImport(reader, importModel, importSummary, getTermEntryCallback, null, getTimeConsumer(), null,
		null, null, null, getImportTransactionLogHandler(), getRegularCollection());

	Assert.assertNotNull(importSummary);
	Assert.assertEquals(1, importSummary.getNoImportedTermEntries().intValue());
	Assert.assertEquals(1, importSummary.getNoImportedTermEntryAttributes().intValue());
	Assert.assertEquals(1, importSummary.getNoImportedTerms().intValue());
	Assert.assertEquals(1, importSummary.getNoImportedTermAttributes().intValue());
	Assert.assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
    }

    @Test
    public void testHandleImportWithSync() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossaryImporter importer = new TmgrGlossaryImporter((TmgrSolrConnector) connector);

	TermEntryReader reader = new TermEntryReader() {
	    @Override
	    public void readTermEntries(ImportCallback importCallback) {
		TermEntry newTermEntry = null;
		try {
		    Term newTerm = createTerm("de", "german term", false, "PROCESSED", getUser());
		    newTerm.addDescription(new Description("context", "german context"));
		    newTermEntry = addNewTermEntry();
		    newTermEntry.addTerm(newTerm);
		} catch (TmException e) {
		    e.printStackTrace();
		}
		importCallback.handleTermEntry(newTermEntry);
		importCallback.handlePercentage(100);
	    }

	    @Override
	    public void validate(AbstractValidationCallback callback) {

	    }
	};

	GetTermEntryCallback getTermEntryCallback = item -> {
	    return (TermEntry) item;
	};

	ImportOptionsModel importModel = new ImportOptionsModel();
	importModel.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);
	importModel.setProjectId(getProjectId());
	importModel.setSyncLanguageId(getSyncLanguageId());

	ITermEntrySynchronizer dummySynchronizer = new ITermEntrySynchronizer() {
	    @Override
	    public void clearCache() {
	    }

	    @Override
	    public void initialize(ITmgrGlossaryConnector connector, ImportOptionsModel importOptions)
		    throws TmException {

	    }

	    @Override
	    public TermEntry synchronizeTermEntries(TermEntry incoming, TermEntry existing,
		    ImportOptionsModel importModel, ImportSummary importSummary) throws TmException {
		importSummary.getNoImportedTerms().increment();
		importSummary.getNoImportedTermAttributes().increment();
		importSummary.getNoDuplicatedTerms().add(2);
		return getRegularConnector().getTmgrBrowser().findById(TE_ID, getProjectId());
	    }
	};

	ImportSummary importSummary = new ImportSummary(1);

	importer.handleImport(reader, importModel, importSummary, getTermEntryCallback, null, getTimeConsumer(), null,
		null, dummySynchronizer, null, getImportTransactionLogHandler(), getRegularCollection());

	Assert.assertNotNull(importSummary);
	Assert.assertEquals(0, importSummary.getNoImportedTermEntries().intValue());
	Assert.assertEquals(0, importSummary.getNoImportedTermEntryAttributes().intValue());
	Assert.assertEquals(1, importSummary.getNoImportedTerms().intValue());
	Assert.assertEquals(1, importSummary.getNoImportedTermAttributes().intValue());
	Assert.assertEquals(2, importSummary.getNoDuplicatedTerms().intValue());

	TermEntry termEntry = getRegularConnector().getTmgrBrowser().findById(TE_ID, getProjectId());
	assertEquals(Action.IMPORTED, termEntry.getAction());
    }

    /*
     * TERII-5271 Project List - Import - Import file breaks project, term counts,
     * summary and export
     */
    @Test
    public void testHandleImportWithTermEntryWithoutTerms() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossaryImporter importer = new TmgrGlossaryImporter((TmgrSolrConnector) connector);

	TermEntryReader reader = new TermEntryReader() {
	    @Override
	    public void readTermEntries(ImportCallback importCallback) {
		Term term = new Term(getSyncLanguageId(), "term text", false, "PROCESSED", getUser());
		term.setUuId(UUID.randomUUID().toString());
		term.addDescription(new Description("context", "term context description"));
		term.setDateModified(new Date().getTime());

		TermEntry item = new TermEntry(getProjectId(), getShortCode(), getUser());
		item.addDescription(new Description("definition", "term entry attribute definition value"));

		boolean nullPointerExceprionCatched = false;

		/* We are adding Term Entry without terms */
		try {
		    importCallback.handleTermEntry(item);
		    importCallback.handlePercentage(50);
		} catch (Exception e) {
		    nullPointerExceprionCatched = true;
		}
		assertFalse(nullPointerExceprionCatched);
	    }

	    @Override
	    public void validate(AbstractValidationCallback callback) {

	    }
	};

	GetTermEntryCallback getTermEntryCallback = item -> (TermEntry) item;

	ImportOptionsModel importModel = new ImportOptionsModel();
	importModel.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);
	importModel.setProjectId(getProjectId());
	importModel.setSyncLanguageId(getSyncLanguageId());

	ImportSummary importSummary = new ImportSummary(1);

	importer.handleImport(reader, importModel, importSummary, getTermEntryCallback, null, getTimeConsumer(), null,
		null, null, null, getImportTransactionLogHandler(), getRegularCollection());
    }

    private String getShortCode() {
	return _shortCode;
    }

    private String getSyncLanguageId() {
	return _syncLanguageId;
    }

    private Consumer<Long> getTimeConsumer() {
	return _timeConsumer;
    }

    private String getUser() {
	return _user;
    }
}

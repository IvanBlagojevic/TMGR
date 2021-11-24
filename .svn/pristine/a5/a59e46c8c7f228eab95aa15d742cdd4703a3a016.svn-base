package org.gs4tr.termmanager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmissionTermServiceTest extends AbstractSolrGlossaryTest {

    private static final Long PROJECT_ID = 1L;

    @Autowired
    private SubmissionTermService _submissionTermService;

    @Test
    public void addNewDescriptionTest_01() throws TmException {
	Long projectId = PROJECT_ID;

	getSubmissionTermService().addNewDescription("description", "description text", Description.NOTE,
		SUB_TERM_ID_01, projectId);

	Term termAfter = getSubmissionConnector().getTmgrBrowser().findTermById(SUB_TERM_ID_01, projectId);

	Set<Description> descriptions = termAfter.getDescriptions();
	Assert.assertNotNull(descriptions);
	Assert.assertEquals(1, descriptions.size());
	Description description = descriptions.iterator().next();

	Assert.assertEquals("description text", description.getValue());
    }

    @Test
    public void tempTextTest() throws TmException {
	Long projectId = PROJECT_ID;

	String subTermId = SUB_TERM_ID_01;

	String newTempTermText1 = "new temp text";

	getSubmissionTermService().updateTempTermText(subTermId, newTempTermText1, projectId);

	Term subTerm = getSubmissionConnector().getTmgrBrowser().findTermById(subTermId, projectId);
	Assert.assertEquals(newTempTermText1, subTerm.getTempText());
    }

    @Test
    public void testHistoryDateModified() throws TmException {
	Long projectId = PROJECT_ID;

	ITmgrGlossaryBrowser browser = getRegularConnector().getTmgrBrowser();
	ITmgrGlossaryUpdater updater = getRegularConnector().getTmgrUpdater();

	TermEntry termEntry = browser.findById(TERM_ENTRY_ID_01, projectId);
	Term term = termEntry.ggetTermById(TERM_ID_01);
	Assert.assertNotNull(term.getDateModified());

	term.addDescription(new Description("definition", "definition text"));

	getTermEntryService().updateRegularTermEntries(projectId, Arrays.asList(termEntry));

	List<TermEntry> termEntries = getTermEntryService().findHistoryByTermEntryId(TERM_ENTRY_ID_01);

	Assert.assertTrue(CollectionUtils.isNotEmpty(termEntries));

	for (TermEntry te : termEntries) {
	    Assert.assertNotNull(te.getDateModified());
	    for (Term t : te.ggetTerms()) {
		Assert.assertNotNull(t.getDateModified());
	    }
	}
    }

    @Ignore
    @Test
    public void testUndoTermTranslation() throws TmException {
	Long projectId = PROJECT_ID;

	String subTermId = SUB_TERM_ID_01;

	String newTempTermText1 = "new temp text 1";
	getSubmissionTermService().updateTempTermText(subTermId, newTempTermText1, projectId);

	TermEntry submissionTermEntry = getSubmissionConnector().getTmgrBrowser().findByTermId(subTermId, projectId);

	Term subTerm = submissionTermEntry.ggetTermById(subTermId);
	Assert.assertEquals(newTempTermText1, subTerm.getTempText());

	String newTempTermText2 = "new temp text 2";
	getSubmissionTermService().updateTempTermText(subTermId, newTempTermText2, projectId);

	submissionTermEntry = getSubmissionConnector().getTmgrBrowser().findByTermId(subTermId, projectId);
	subTerm = submissionTermEntry.ggetTermById(subTermId);
	Assert.assertEquals(newTempTermText2, subTerm.getTempText());

	List<String> subTermIds = new ArrayList<String>();
	subTermIds.add(subTermId);

	Assert.assertEquals(newTempTermText2, subTerm.getTempText());

	// Performing undo translation
	Map<String, String> undoResults = getSubmissionTermService().undoTermTranslation(subTermIds, projectId);
	Assert.assertNotNull(undoResults);
	submissionTermEntry = getSubmissionConnector().getTmgrBrowser().findByTermId(subTermId, projectId);
	subTerm = submissionTermEntry.ggetTermById(subTermId);
	Assert.assertEquals(newTempTermText1, subTerm.getTempText());

	undoResults = getSubmissionTermService().undoTermTranslation(subTermIds, projectId);
	submissionTermEntry = getSubmissionConnector().getTmgrBrowser().findByTermId(subTermId, projectId);
	subTerm = submissionTermEntry.ggetTermById(subTermId);

	// FIXME need to see how to clear history
	// Assert.assertNull(subTerm.getTempText());
    }

    @Test
    public void testUpdateTermTermText() throws TmException {
	Long projectId = PROJECT_ID;

	String subTermId = SUB_TERM_ID_01;

	String newTempTermText = "new temp text";

	getSubmissionTermService().updateTempTermText(subTermId, newTempTermText, projectId);

	Term subTerm = getSubmissionTermService().findById(subTermId, projectId);

	Assert.assertEquals(newTempTermText, subTerm.getTempText());

    }

    private SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }
}

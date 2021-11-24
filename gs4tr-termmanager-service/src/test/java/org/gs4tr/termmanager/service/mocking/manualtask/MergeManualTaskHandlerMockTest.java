package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.manualtask.MergeManualTaskHandler;
import org.gs4tr.termmanager.service.manualtask.Messages;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.MergeCommands;
import org.gs4tr.termmanager.service.model.command.MultipleTermEntriesMergeCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class MergeManualTaskHandlerMockTest extends AbstractManualtaskTest {

    @Autowired
    private MergeManualTaskHandler _handler;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TermService _termService;

    @Test
    @TestCase("mergeTermEntries")
    public void mergeTermEntryFailedTermIsInWorkflow() {

	MergeCommands mergeCommand = getModelObject("mergeCommandInWorkflow", MergeCommands.class);

	MultipleTermEntriesMergeCommand multipleMergeCommand = mergeCommand.getMultipleTermEntriesMergeCommand();
	List<String> termEntryIds = multipleMergeCommand.getTermEntryIds();
	long projectId = multipleMergeCommand.getProjectId();

	List<TermEntry> termEntries = getModelObject("termEntriesInWorkflow", List.class);
	assertEquals(4, termEntries.size());

	TermEntry entryWithTermsInWorkflow = getModelObject("termEntry04", TermEntry.class);
	List<Term> termsInWorkflow = entryWithTermsInWorkflow.ggetTerms();
	assertEquals(2, termsInWorkflow.size());
	assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), termsInWorkflow.get(0).getStatus());
	assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), termsInWorkflow.get(1).getStatus());

	when(getTermEntryService().findTermentriesByIds(termEntryIds, projectId)).thenReturn(termEntries);

	boolean isExceptionThrown = false;

	try {
	    getHandler().processTasks(null, null, mergeCommand, null);
	} catch (Exception e) {
	    assertEquals(e.getClass(), UserException.class);
	    assertEquals(e.getMessage(), Messages.getString("TermInWorkflowError"));
	    isExceptionThrown = true;
	}

	assertTrue(isExceptionThrown);
    }

    @Test
    @TestCase("mergeTermEntries")
    public void mergeTermEntrySuccess() {

	MergeCommands mergeCommand = getModelObject("mergeCommand", MergeCommands.class);

	MultipleTermEntriesMergeCommand multipleMergeCommand = mergeCommand.getMultipleTermEntriesMergeCommand();
	List<String> termEntryIds = multipleMergeCommand.getTermEntryIds();
	long projectId = multipleMergeCommand.getProjectId();

	List<TermEntry> termEntries = getModelObject("termEntries", List.class);
	assertEquals(3, termEntries.size());

	TermEntry existing = getModelObject("termEntry01", TermEntry.class);
	assertTrue(termEntries.contains(existing));

	when(getTermEntryService().findTermentriesByIds(termEntryIds, projectId)).thenReturn(termEntries);

	getHandler().processTasks(null, null, mergeCommand, null);

	assertEquals(2, termEntries.size());
	assertFalse(termEntries.contains(existing));

    }

    /*
     * *****************************************************************************
     * TERII-5303 Merge Term: after merging an existing approved source term with
     * newly added pending approval or on hold term, the saved term status is not
     * proper
     * *****************************************************************************
     */

    @Test
    @TestCase("mergeTermEntries")
    public void mergeTermEntryTermDuplicates() {

	MergeCommands mergeCommand = getModelObject("mergeCommandInWorkflow", MergeCommands.class);

	MultipleTermEntriesMergeCommand multipleMergeCommand = mergeCommand.getMultipleTermEntriesMergeCommand();
	List<String> termEntryIds = multipleMergeCommand.getTermEntryIds();
	long projectId = multipleMergeCommand.getProjectId();

	multipleMergeCommand.setSourceLanguage("en-US");

	TermEntry termEntryExisting = getModelObject("termEntry05", TermEntry.class);
	TermEntry termEntryIncoming = getModelObject("termEntry06", TermEntry.class);

	// Test statuses before merge
	assertEquals(1, termEntryExisting.ggetAllTerms().size());
	assertEquals(1, termEntryIncoming.ggetAllTerms().size());
	assertEquals("PROCESSING", termEntryExisting.ggetAllTerms().get(0).getStatus());
	assertEquals("ONHOLD", termEntryIncoming.ggetAllTerms().get(0).getStatus());

	List<TermEntry> termEntries = new ArrayList<>();
	termEntries.add(termEntryIncoming);
	termEntries.add(termEntryExisting);

	when(getTermEntryService().findTermentriesByIds(termEntryIds, projectId)).thenReturn(termEntries);

	getHandler().processTasks(null, null, mergeCommand, null);

	assertEquals(1, termEntries.size());

	assertEquals(termEntryIncoming.ggetAllTerms().get(0).getStatus(),
		termEntries.get(0).ggetAllTerms().get(0).getStatus());

	assertEquals(termEntryIncoming.getUuId(), termEntries.get(0).getUuId());
    }

    @Before
    public void resetMocks() {
	Mockito.reset(getTermService());
	Mockito.reset(getTermEntryService());
    }

    private MergeManualTaskHandler getHandler() {
	return _handler;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private TermService getTermService() {
	return _termService;
    }
}

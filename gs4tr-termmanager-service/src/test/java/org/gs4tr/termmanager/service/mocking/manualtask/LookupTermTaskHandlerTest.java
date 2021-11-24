package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.manualtask.LookupTermTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.LookupTermCommand;
import org.gs4tr.termmanager.service.model.command.LookupTermCommands;
import org.gs4tr.tm3.api.Page;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class LookupTermTaskHandlerTest extends AbstractManualtaskTest {

    @Autowired
    private LookupTermTaskHandler _handler;

    @Autowired
    private TermEntryService _termEntryService;

    @Test
    @TestCase("lookupTerm")
    @SuppressWarnings("unchecked")
    public void lookupTermTest() {

	LookupTermCommands getCmd = getModelObject("getLookupTermCommands", LookupTermCommands.class);

	List<TermEntry> termEntries = getModelObject("termentries", List.class);
	Page<TermEntry> page = new Page<TermEntry>(2, 0, 50, termEntries);

	when(getTermEntryervice().searchTermEntries(Matchers.any(TmgrSearchFilter.class))).thenReturn(page);

	TaskModel[] taskInfos = getHandler().getTaskInfos(new Long[] {}, "lookup term", getCmd);

	Object matches = taskInfos[0].getModel().get("matches");

	assertEquals(Boolean.class, matches.getClass());
	assertTrue((Boolean) matches);

    }

    @Test
    @TestCase("lookupTerm")
    public void lookupTermTestFindsMatchesForEditedTermEntrty() {

	LookupTermCommands commands = getModelObject("lookupTermCommandsForEditedTermEntries",
		LookupTermCommands.class);
	LookupTermCommand command = commands.getCommands().get(0);
	assertTrue(CollectionUtils.isNotEmpty(command.getTermEntryIds()));

	List<TermEntry> termEntries = getModelObject("matchedTermEntries", List.class);
	Page<TermEntry> page = new Page<TermEntry>(2, 0, 50, termEntries);

	when(getTermEntryervice().searchTermEntries(Matchers.any(TmgrSearchFilter.class))).thenReturn(page);

	TaskModel[] taskInfos = getHandler().getTaskInfos(new Long[] {}, "lookup term", commands);

	Object matches = taskInfos[0].getModel().get("matches");

	assertEquals(Boolean.class, matches.getClass());
	assertTrue((Boolean) matches);

    }

    @Test
    @TestCase("lookupTerm")
    @SuppressWarnings("unchecked")
    public void lookupTermTest_negative_case1() {

	LookupTermCommands getCmd = getModelObject("invalidGetLookupTermCommands", LookupTermCommands.class);

	List<TermEntry> termEntries = getModelObject("termentries", List.class);
	Page<TermEntry> page = new Page<TermEntry>(2, 0, 50, termEntries);

	when(getTermEntryervice().searchTermEntries(Matchers.any(TmgrSearchFilter.class))).thenReturn(page);

	TaskModel[] taskInfos = getHandler().getTaskInfos(new Long[] {}, "lookup term", getCmd);
	assertNotNull(taskInfos);
	assertNotNull(taskInfos[0]);

	boolean matches = (Boolean) taskInfos[0].getModel().get("matches");
	assertFalse(matches);
    }

    // matched is in workflow
    @Test
    @TestCase("lookupTerm")
    @SuppressWarnings("unchecked")
    public void lookupTermTest_negative_case2() {

	LookupTermCommands getCmd = getModelObject("getLookupTermCommands", LookupTermCommands.class);

	List<TermEntry> termEntries = getModelObject("termentriesInWf", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 50, termEntries);

	when(getTermEntryervice().searchTermEntries(Matchers.any(TmgrSearchFilter.class))).thenReturn(page);

	TaskModel[] taskInfos = getHandler().getTaskInfos(new Long[] {}, "lookup term", getCmd);
	assertNotNull(taskInfos);
	assertNotNull(taskInfos[0]);

	boolean matches = (Boolean) taskInfos[0].getModel().get("matches");
	assertFalse(matches);
    }

    private LookupTermTaskHandler getHandler() {
	return _handler;
    }

    private TermEntryService getTermEntryervice() {
	return _termEntryService;
    }
}

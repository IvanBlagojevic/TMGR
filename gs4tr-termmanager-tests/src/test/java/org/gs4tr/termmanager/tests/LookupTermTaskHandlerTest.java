package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("lookup_term")
public class LookupTermTaskHandlerTest extends AbstractSolrGlossaryTest {

    @Test
    @TestCase("get_task_infos")
    public void lookupBlacklistTermTest() throws Exception {
	String uuid = TERM_ID_13;

	Term term = getTermService().findTermById(uuid, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.BLACKLISTED.getName(), term.getStatus());

	String taskName = "lookup term";
	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "lookupTermGet.json",
		new String[] { "$languageId", term.getLanguageId() },
		new String[] { "$projectTicket", IdEncrypter.encryptGenericId(term.getProjectId()) },
		new String[] { "$termName1", term.getName() });

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1), new Long(1) }, taskName, command);
	Assert.assertTrue(ArrayUtils.isNotEmpty(taskInfos));

	TaskModel taskModel = taskInfos[0];

	Map<String, Object> model = taskModel.getModel();
	Assert.assertTrue(MapUtils.isNotEmpty(model));

	boolean matches = (Boolean) model.get("matches");
	Assert.assertTrue(matches);

    }

    @Test
    @TestCase("get_task_infos")
    public void lookupOnHoldTermTest() throws Exception {
	String uuid = TERM_ID_12;

	Term term = getTermService().findTermById(uuid, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.ON_HOLD.getName(), term.getStatus());

	String taskName = "lookup term";
	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "lookupTermGet.json",
		new String[] { "$languageId", term.getLanguageId() },
		new String[] { "$projectTicket", IdEncrypter.encryptGenericId(term.getProjectId()) },
		new String[] { "$termName1", term.getName() });

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1), new Long(1) }, taskName, command);
	Assert.assertTrue(ArrayUtils.isNotEmpty(taskInfos));

	TaskModel taskModel = taskInfos[0];

	Map<String, Object> model = taskModel.getModel();
	Assert.assertTrue(MapUtils.isNotEmpty(model));

	boolean matches = (Boolean) model.get("matches");
	Assert.assertTrue(matches);

    }

    @Test
    @TestCase("get_task_infos")
    public void lookupPendingTermTest() throws Exception {
	String uuid = TERM_ID_05;

	Term term = getTermService().findTermById(uuid, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.WAITING.getName(), term.getStatus());

	String taskName = "lookup term";
	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "lookupTermGet.json",
		new String[] { "$languageId", term.getLanguageId() },
		new String[] { "$projectTicket", IdEncrypter.encryptGenericId(term.getProjectId()) },
		new String[] { "$termName1", term.getName() });

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1), new Long(1) }, taskName, command);
	Assert.assertTrue(ArrayUtils.isNotEmpty(taskInfos));

	TaskModel taskModel = taskInfos[0];

	Map<String, Object> model = taskModel.getModel();
	Assert.assertTrue(MapUtils.isNotEmpty(model));

	boolean matches = (Boolean) model.get("matches");
	Assert.assertTrue(matches);
    }

    @Test
    @TestCase("get_task_infos")
    public void lookupTermTest() throws Exception {
	String uuid = TERM_ID_04;

	Term term = getTermService().findTermById(uuid, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());

	String taskName = "lookup term";
	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "lookupTermGet.json",
		new String[] { "$languageId", term.getLanguageId() },
		new String[] { "$projectTicket", IdEncrypter.encryptGenericId(term.getProjectId()) },
		new String[] { "$termName1", term.getName() });

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1), new Long(1) }, taskName, command);

	Assert.assertTrue(ArrayUtils.isNotEmpty(taskInfos));

	TaskModel taskModel = taskInfos[0];

	Map<String, Object> model = taskModel.getModel();
	Assert.assertTrue(MapUtils.isNotEmpty(model));

	boolean matches = (Boolean) model.get("matches");
	Assert.assertTrue(matches);

    }

    @Test
    @TestCase("get_task_infos")
    public void lookupTwoTermsWhereNeitherHasMatches() {

	String taskName = "lookup term";
	ManualTaskHandler taskHandler = getHandler(taskName);

	TermEntryService service = getTermEntryService();

	// Before lookup
	TermEntry expectedSearchResult1 = service.findTermEntryById(TERM_ENTRY_ID_12, PROJECT_ID);
	TermEntry expectedSearchResult2 = service.findTermEntryById(TERM_ENTRY_ID_13, PROJECT_ID);

	Term termWithoutMatch = expectedSearchResult1.ggetTermById(TERM_ID_28);
	Term anotherTermWithoutMatch = expectedSearchResult2.ggetTermById(TERM_ID_29);

	assertNull(termWithoutMatch.getUserLatestChange());
	assertNull(anotherTermWithoutMatch.getUserLatestChange());

	// Lookup
	Object command = getTaskHandlerCommand(taskHandler, "lookupTermGet.json",
		new String[] { "$languageId", "en-US" },
		new String[] { "$projectTicket", IdEncrypter.encryptGenericId(PROJECT_ID) },
		new String[] { "$termName1", "termWithoutMatch1" }, new String[] { "$termName2", "termWithoutMatch2" });

	TaskModel[] taskInfos = taskHandler.getTaskInfos(null, taskName, command);
	assertNotNull(taskInfos);

	// After lookup
	Long currentUserId = TmUserProfile.getCurrentUserProfile().getUserProfileId();
	assertNotNull(currentUserId);

	TermEntry expectedSearchResultAfterLookup1 = service.findTermEntryById(TERM_ENTRY_ID_12, PROJECT_ID);
	TermEntry expectedSearchResultAfterLookup2 = service.findTermEntryById(TERM_ENTRY_ID_13, PROJECT_ID);

	Term termWithoutMatchAfterLookup = expectedSearchResultAfterLookup1.ggetTermById(TERM_ID_28);
	Term anotherTermWithoutMatchAfterLookup = expectedSearchResultAfterLookup2.ggetTermById(TERM_ID_29);

	// Terms will bi shown in grid
	assertEquals(currentUserId, termWithoutMatchAfterLookup.getUserLatestChange());
	assertEquals(currentUserId, anotherTermWithoutMatchAfterLookup.getUserLatestChange());

	TaskModel taskModel = taskInfos[0];

	Map<String, Object> model = taskModel.getModel();
	Assert.assertTrue(MapUtils.isNotEmpty(model));

	// Terms doesn't have matches
	boolean matches = (Boolean) model.get("matches");
	Assert.assertFalse(matches);

    }

    @Test
    @TestCase("get_task_infos")
    public void lookupTwoTermsWhereOnlyOneMatches() {

	String taskName = "lookup term";
	ManualTaskHandler taskHandler = getHandler(taskName);

	TermEntryService service = getTermEntryService();

	// Before lookup
	TermEntry expectedSearchResult1 = service.findTermEntryById(TERM_ENTRY_ID_04, PROJECT_ID);
	TermEntry expectedSearchResult2 = service.findTermEntryById(TERM_ENTRY_ID_07, PROJECT_ID);
	TermEntry expectedSearchResult3 = service.findTermEntryById(TERM_ENTRY_ID_08, PROJECT_ID);
	TermEntry expectedSearchResult4 = service.findTermEntryById(TERM_ENTRY_ID_09, PROJECT_ID);
	TermEntry expectedSearchResult5 = service.findTermEntryById(TERM_ENTRY_ID_12, PROJECT_ID);
	TermEntry unexpectedSearchResult = service.findTermEntryById(TERM_ENTRY_ID_11, PROJECT_ID);

	Term englishTerm = expectedSearchResult1.ggetTermById(TERM_ID_10);
	Term englishTermMatch1 = expectedSearchResult2.ggetTermById(TERM_ID_17);
	Term englishTermMatch2 = expectedSearchResult3.ggetTermById(TERM_ID_20);
	Term englishTermMatch3 = expectedSearchResult4.ggetTermById(TERM_ID_23);
	Term termWithoutMatch = expectedSearchResult5.ggetTermById(TERM_ID_28);
	Term unexpectedEnglishTermMatch = unexpectedSearchResult.ggetTermById(TERM_ID_27);

	assertNull(englishTerm.getUserLatestChange());
	assertNull(englishTermMatch1.getUserLatestChange());
	assertNull(englishTermMatch2.getUserLatestChange());
	assertNull(englishTermMatch3.getUserLatestChange());

	assertNull(termWithoutMatch.getUserLatestChange());
	assertNull(unexpectedEnglishTermMatch.getUserLatestChange());

	// Lookup
	Object command = getTaskHandlerCommand(taskHandler, "lookupTermGet.json",
		new String[] { "$languageId", "en-US" },
		new String[] { "$projectTicket", IdEncrypter.encryptGenericId(PROJECT_ID) },
		new String[] { "$termName1", "englishTerm" }, new String[] { "$termName2", "termWithoutMatch1" });

	TaskModel[] taskInfos = taskHandler.getTaskInfos(null, taskName, command);
	assertNotNull(taskInfos);

	// After Lookup
	Long currentUserId = TmUserProfile.getCurrentUserProfile().getUserProfileId();
	assertNotNull(currentUserId);

	TermEntry expectedSearchResult1AfterLookup = service.findTermEntryById(TERM_ENTRY_ID_04, PROJECT_ID);
	TermEntry expectedSearchResult2AfterLookup = service.findTermEntryById(TERM_ENTRY_ID_07, PROJECT_ID);
	TermEntry expectedSearchResult3AfterLookup = service.findTermEntryById(TERM_ENTRY_ID_08, PROJECT_ID);
	TermEntry expectedSearchResult4AfterLookup = service.findTermEntryById(TERM_ENTRY_ID_09, PROJECT_ID);
	TermEntry expectedSearchResult5AfterLookup = service.findTermEntryById(TERM_ENTRY_ID_12, PROJECT_ID);
	TermEntry unexpectedSearchResultAfterLookup = service.findTermEntryById(TERM_ENTRY_ID_11, PROJECT_ID);

	Term englishTermAfterLookup = expectedSearchResult1AfterLookup.ggetTermById(TERM_ID_10);
	Term englishTermMatch1AfterLookup = expectedSearchResult2AfterLookup.ggetTermById(TERM_ID_17);
	Term englishTermMatch2AfterLookup = expectedSearchResult3AfterLookup.ggetTermById(TERM_ID_20);
	Term englishTermMatch3AfterLookup = expectedSearchResult4AfterLookup.ggetTermById(TERM_ID_23);
	Term termWithoutMatchAfterLookup = expectedSearchResult5AfterLookup.ggetTermById(TERM_ID_28);
	Term unexpectedEnglishTermMatchAfterLookup = unexpectedSearchResultAfterLookup.ggetTermById(TERM_ID_27);

	// Terms will bi shown in grid
	assertEquals(currentUserId, englishTermAfterLookup.getUserLatestChange());
	assertEquals(currentUserId, englishTermMatch1AfterLookup.getUserLatestChange());
	assertEquals(currentUserId, englishTermMatch2AfterLookup.getUserLatestChange());
	assertEquals(currentUserId, englishTermMatch3AfterLookup.getUserLatestChange());
	assertEquals(currentUserId, termWithoutMatchAfterLookup.getUserLatestChange());
	assertNull(unexpectedEnglishTermMatchAfterLookup.getUserLatestChange());

	TaskModel taskModel = taskInfos[0];

	Map<String, Object> model = taskModel.getModel();
	Assert.assertTrue(MapUtils.isNotEmpty(model));

	// Terms have matches
	boolean matches = (Boolean) model.get("matches");
	Assert.assertTrue(matches);

    }

}

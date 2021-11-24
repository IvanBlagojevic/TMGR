package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.dto.LanguageModifications;
import org.gs4tr.termmanager.model.dto.TermEntryDifferences;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.manualtask.GetTermEntryHistoryTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.TermEntryHistoryCommand;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class GetTermEntryHistoryTaskHandlerTest extends AbstractManualtaskTest {

    private static final Long PROJECT_ID = 1L;

    private static final String TASK_NAME = "get term entry history";

    private static final String TERM_ENTRY_HISTORY = "revisions";

    private static final String USER_NAME = "beko";

    @Autowired
    private GetTermEntryHistoryTaskHandler _taskHandler;

    @Autowired
    private TermEntryService _termEntryService;

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("getTermEntryHistory")
    public void getTermEntryHistoryNoRevisionsTest() {
	TermEntryHistoryCommand cmd = getModelObject("termEntryHistoryCommand1", TermEntryHistoryCommand.class);

	when(getTermEntryService().findHistoryByTermEntryId(anyString())).thenReturn(Collections.emptyList());

	TaskModel[] taskInfos = getTaskHandler().getTaskInfos(new Long[] { PROJECT_ID }, TASK_NAME, cmd);

	verify(getTermEntryService()).findHistoryByTermEntryId(anyString());

	Map<String, Object> model = taskInfos[0].getModel();

	assertTrue(((List<TermEntryDifferences>) model.get(TERM_ENTRY_HISTORY)).isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("getTermEntryHistory")
    public void getTermEntryHistoryWithMultipleRevisionsTest() {
	TermEntryHistoryCommand cmd = getModelObject("termEntryHistoryCommand3", TermEntryHistoryCommand.class);

	List<TermEntry> history = getModelObject("history3", List.class);

	when(getTermEntryService().findHistoryByTermEntryId(anyString())).thenReturn(history);

	TaskModel[] taskInfos = getTaskHandler().getTaskInfos(new Long[] { PROJECT_ID }, TASK_NAME, cmd);

	verify(getTermEntryService()).findHistoryByTermEntryId(anyString());

	Map<String, Object> taskModel = taskInfos[0].getModel();

	List<TermEntryDifferences> revisions = (List<TermEntryDifferences>) taskModel.get(TERM_ENTRY_HISTORY);
	assertEquals(3, revisions.size());

	TermEntryDifferences firstRevision = revisions.get(0);
	assertNotNull(firstRevision.getAction());
	assertNotNull(firstRevision.getModificationDate());
	assertEquals(USER_NAME, firstRevision.getModificationUser());

	// Correct, there are no changes in term entry attributes
	assertEquals(0, firstRevision.getTermEntryModifications().getAttributesDifferences().size());

	Collection<LanguageModifications> lms1 = firstRevision.getLanguagesModifications();
	assertEquals(2, lms1.size());

	Iterator<LanguageModifications> each1 = lms1.iterator();

	LanguageModifications lm1 = each1.next();
	assertEquals(Locale.FRANCE.getCode(), lm1.getLanguage().getLanguageId());

	LanguageModifications lm2 = each1.next();
	assertEquals(Locale.GERMANY.getCode(), lm2.getLanguage().getLanguageId());

	TermEntryDifferences secondRevision = revisions.get(1);
	assertNotNull(secondRevision.getAction());
	assertNotNull(secondRevision.getModificationDate());
	assertEquals(USER_NAME, secondRevision.getModificationUser());

	// Correct, there are no changes in term entry attributes
	assertEquals(0, secondRevision.getTermEntryModifications().getAttributesDifferences().size());

	Collection<LanguageModifications> lms2 = secondRevision.getLanguagesModifications();

	Iterator<LanguageModifications> each2 = lms2.iterator();
	assertEquals(2, lms2.size());

	LanguageModifications lm3 = each2.next();
	assertEquals(Locale.US.getCode(), lm3.getLanguage().getLanguageId());

	LanguageModifications lm4 = each2.next();
	assertEquals(Locale.GERMANY.getCode(), lm4.getLanguage().getLanguageId());

	/*
	 * This revision represents differences between fist added and empty
	 * term enty.
	 */
	TermEntryDifferences thirdRevision = revisions.get(2);
	assertNotNull(thirdRevision.getAction());
	assertNotNull(thirdRevision.getModificationDate());
	assertEquals(USER_NAME, thirdRevision.getModificationUser());

	assertEquals(1, thirdRevision.getTermEntryModifications().getAttributesDifferences().size());
	Collection<LanguageModifications> lms3 = thirdRevision.getLanguagesModifications();
	assertEquals(2, lms3.size());

	Iterator<LanguageModifications> each3 = lms3.iterator();

	LanguageModifications lm5 = each3.next();
	assertEquals(Locale.US.getCode(), lm5.getLanguage().getLanguageId());

	LanguageModifications lm6 = each3.next();
	assertEquals(Locale.FRANCE.getCode(), lm6.getLanguage().getLanguageId());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("getTermEntryHistory")
    public void getTermEntryHistoryWithOneRevisionTest() {
	TermEntryHistoryCommand cmd = getModelObject("termEntryHistoryCommand1", TermEntryHistoryCommand.class);

	List<TermEntry> history = getModelObject("history1", List.class);

	when(getTermEntryService().findHistoryByTermEntryId(anyString())).thenReturn(history);

	TaskModel[] taskInfos = getTaskHandler().getTaskInfos(new Long[] { PROJECT_ID }, TASK_NAME, cmd);

	verify(getTermEntryService()).findHistoryByTermEntryId(anyString());

	Map<String, Object> taskModel = taskInfos[0].getModel();

	List<TermEntryDifferences> revisions = (List<TermEntryDifferences>) taskModel.get(TERM_ENTRY_HISTORY);
	assertEquals(1, revisions.size());

	TermEntryDifferences revision = revisions.get(0);
	assertNotNull(revision.getAction());
	assertNotNull(revision.getModificationDate());
	assertEquals(USER_NAME, revision.getModificationUser());
	assertEquals(1, revision.getTermEntryModifications().getAttributesDifferences().size());

	Collection<LanguageModifications> lms = revision.getLanguagesModifications();
	assertEquals(1, lms.size());

	Iterator<LanguageModifications> each = lms.iterator();
	/*
	 * language modifications should be sorted in same order as languages in
	 * grid (gridLanguages: [fr-FR, de-DE]). In this test case, there are no
	 * other lanaguages becase showAllLanguages = false.
	 * 
	 * NOTE: de-DE is not in the results because that language currently
	 * does not exist. Also, en-US is not in the result because that
	 * language is not selected in the grid and showAllLanguages = false
	 */
	LanguageModifications lm1 = each.next();
	assertEquals(Locale.FRANCE.getCode(), lm1.getLanguage().getLanguageId());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("getTermEntryHistory")
    public void getTermEntryHistoryWithTwoRevisionsTest() {
	TermEntryHistoryCommand cmd = getModelObject("termEntryHistoryCommand2", TermEntryHistoryCommand.class);

	List<TermEntry> history = getModelObject("history2", List.class);

	when(getTermEntryService().findHistoryByTermEntryId(anyString())).thenReturn(history);

	TaskModel[] taskInfos = getTaskHandler().getTaskInfos(new Long[] { PROJECT_ID }, TASK_NAME, cmd);

	verify(getTermEntryService()).findHistoryByTermEntryId(anyString());

	Map<String, Object> taskModel = taskInfos[0].getModel();

	List<TermEntryDifferences> revisions = (List<TermEntryDifferences>) taskModel.get(TERM_ENTRY_HISTORY);
	assertEquals(2, revisions.size());

	TermEntryDifferences firstRevision = revisions.get(0);
	assertNotNull(firstRevision.getAction());
	assertNotNull(firstRevision.getModificationDate());
	assertEquals(USER_NAME, firstRevision.getModificationUser());

	// Correct, there are no changes in term entry attributes
	assertEquals(0, firstRevision.getTermEntryModifications().getAttributesDifferences().size());

	Collection<LanguageModifications> lms1 = firstRevision.getLanguagesModifications();
	assertEquals(2, lms1.size());

	Iterator<LanguageModifications> each1 = lms1.iterator();
	/*
	 * If showAllLanguages = true, below grid (filtered) languages (in this
	 * case: [fr-FR, de-DE]) there should be project user languages sorted
	 * by display name (in this case only en-US).
	 * 
	 * NOTE: fr-FR is not in the results because there are no changes for
	 * that language.
	 */
	LanguageModifications lm1 = each1.next();
	assertEquals(Locale.GERMANY.getCode(), lm1.getLanguage().getLanguageId());

	LanguageModifications lm2 = each1.next();
	assertEquals(Locale.US.getCode(), lm2.getLanguage().getLanguageId());

	/*
	 * This is first revision which represents differences between fist
	 * added and empty term enty.
	 */
	TermEntryDifferences secondRevision = revisions.get(1);
	assertNotNull(secondRevision.getAction());
	assertNotNull(secondRevision.getModificationDate());
	assertEquals(USER_NAME, secondRevision.getModificationUser());

	assertEquals(1, secondRevision.getTermEntryModifications().getAttributesDifferences().size());
	Collection<LanguageModifications> lms2 = secondRevision.getLanguagesModifications();
	assertEquals(2, lms2.size());

	Iterator<LanguageModifications> each2 = lms2.iterator();

	LanguageModifications lm3 = each2.next();
	assertEquals(Locale.FRANCE.getCode(), lm3.getLanguage().getLanguageId());

	LanguageModifications lm4 = each2.next();
	assertEquals(Locale.US.getCode(), lm4.getLanguage().getLanguageId());
    }

    @Before
    public void setUp() throws Exception {
	reset(getTermEntryService());
    }

    private GetTermEntryHistoryTaskHandler getTaskHandler() {
	return _taskHandler;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }
}

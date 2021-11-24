package org.gs4tr.termmanager.tests;

import java.util.Arrays;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.ChangeTermStatusCommands;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("change_term_status")
public class ChangeTermStatusTaskHandlerTest extends AbstractSolrGlossaryTest {

    /*
     * ======================================================================
     * Ticket: Implement a single status change button and dialogue with source,
     * target and synonym options | TERII-4918 | 5.1.0 Release
     * ======================================================================
     */

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TermService _termService;

    /* Approve action button is removed | TERII-4818 */
    @Test
    @TestCase("process_tasks")
    public void approveTeGetAndPost() throws Exception {
	String uuid = TERM_ID_05;

	String termEntryUuId = TERM_ENTRY_ID_02;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	Term term = getTermService().findTermById(uuid, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.WAITING.getName(), term.getStatus());

	String taskName = "change term status";
	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.PROCESSED.getName();

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", uuid }, new String[] { "$termEntryTicket1", termEntryUuId },
		new String[] { "$sourceLanguage", "es-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$termStatus", newStatus },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	term = getTermService().findTermById(uuid, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
    }

    @Override
    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    @Override
    public TermService getTermService() {
	return _termService;
    }

    @Test
    @TestCase("process_tasks")
    public void processTaskChangeSourceAndTargetTermStatusByTermIdNonSynonymTest() throws TmException {

	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "change term status";

	/* Add new non synonym de-DE term to TermEntry */

	String TERM_ID_122 = "c502608e-uuid-term-0122";
	Term newTerm = createTerm(TERM_ID_122, "de-DE", "house 2", false, ItemStatusTypeHolder.PROCESSED.getName(),
		"pm", false);
	addNewTermToTermEntry(termEntryId, newTerm);

	String termTickets = TERM_ID_01 + "\",\"" + TERM_ID_122;

	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.ON_HOLD.getName();

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", termTickets }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$termStatus", newStatus },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	Term term01Before = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Term term02Before = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Term term03Before = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Term term12Before = getTermService().findTermById(TERM_ID_122, PROJECT_ID);

	taskHandler.processTasks(projectIds, null, command, null);

	/*
	 * Only target terms de-DE | TERM_ID_01 and TERM_ID_02 status should be changed,
	 * because only that id is added to command
	 */

	Term term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term01Before.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term02Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term03Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_122, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term12Before.getDateModified().longValue() < term.getDateModified().longValue());
    }

    @Test
    @TestCase("process_tasks")
    public void processTaskChangeSourceByLanguageIdAndTargetByTermIdTest() throws TmException {

	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "change term status";

	/* Add new non synonym de-DE term to TermEntry */

	String TERM_ID_12 = "c502608e-uuid-term-0122";
	Term newTerm = createTerm(TERM_ID_12, "de-DE", "house 2", false, ItemStatusTypeHolder.PROCESSED.getName(), "pm",
		false);
	addNewTermToTermEntry(termEntryId, newTerm);

	String targetTermId = TERM_ID_02;

	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.ON_HOLD.getName();

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", targetTermId }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "true" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$termStatus", newStatus },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	Term term01Before = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Term targetTermIdBefore = getTermService().findTermById(targetTermId, PROJECT_ID);
	Term term03Before = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Term term12Before = getTermService().findTermById(TERM_ID_12, PROJECT_ID);

	taskHandler.processTasks(projectIds, null, command, null);

	/*
	 * Source terms statuses should be changed because source synonym is checked,
	 * and only one target language selected by term Id
	 */

	Term term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term01Before.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term03Before.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(targetTermId, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(targetTermIdBefore.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_12, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term12Before.getDateModified().longValue() == term.getDateModified().longValue());
    }

    @Test
    @TestCase("process_tasks")
    public void processTaskChangeSourceByTermIdAndTargetByLanguageIdTest() throws TmException {

	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "change term status";

	/* Add new non synonym de-DE term to TermEntry */

	String TERM_ID_122 = "c502608e-uuid-term-0122";
	Term newTerm = createTerm(TERM_ID_122, "de-DE", "house 2", false, ItemStatusTypeHolder.PROCESSED.getName(),
		"pm", false);
	addNewTermToTermEntry(termEntryId, newTerm);

	String sourceTermId = TERM_ID_01;

	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.ON_HOLD.getName();

	Term sourceTermIdBefore = getTermService().findTermById(sourceTermId, PROJECT_ID);
	Term term02Before = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Term term03Before = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Term term12Before = getTermService().findTermById(TERM_ID_122, PROJECT_ID);

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", sourceTermId }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "true" }, new String[] { "$termStatus", newStatus },
		new String[] { "$targetLanguage1", "de-DE" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	Term term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term02Before.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_122, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term12Before.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term03Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(sourceTermId, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(sourceTermIdBefore.getDateModified().longValue() < term.getDateModified().longValue());
    }

    @Test
    @TestCase("process_tasks")
    public void processTaskChangeSourceTermStatusByLanguageSynonymBLACKLISTEDTest() {

	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "change term status";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.BLACKLISTED.getName();

	Term termBefore = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertFalse(termBefore.isForbidden());

	termBefore = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertFalse(termBefore.isForbidden());

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "true" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$termStatus", newStatus },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	Term term01Before = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Term term02Before = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Term term03Before = getTermService().findTermById(TERM_ID_03, PROJECT_ID);

	taskHandler.processTasks(projectIds, null, command, null);

	Term term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term01Before.getDateModified().longValue() < term.getDateModified().longValue());
	Assert.assertTrue(term.isForbidden());

	/* Target term should not be changed */
	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term02Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term03Before.getDateModified().longValue() < term.getDateModified().longValue());
	Assert.assertTrue(term.isForbidden());
    }

    @Test
    @TestCase("process_tasks")
    public void processTaskChangeSourceTermStatusByLanguageSynonymTest() {

	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "change term status";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.ON_HOLD.getName();

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "true" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$termStatus", newStatus },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	Term term01Before = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Term term02Before = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Term term03Before = getTermService().findTermById(TERM_ID_03, PROJECT_ID);

	taskHandler.processTasks(projectIds, null, command, null);

	Term term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term01Before.getDateModified().longValue() < term.getDateModified().longValue());

	/* Target term should not be changed */
	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term02Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term03Before.getDateModified().longValue() < term.getDateModified().longValue());
    }

    /* WAITING status test */
    @Test
    @TestCase("process_tasks")
    public void processTaskChangeSourceTermStatusByLanguageSynonymWAITINGTest() {

	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "change term status";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.WAITING.getName();

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "true" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$termStatus", newStatus },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	Term term01Before = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Term term02Before = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Term term03Before = getTermService().findTermById(TERM_ID_03, PROJECT_ID);

	taskHandler.processTasks(projectIds, null, command, null);

	Term term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term01Before.getDateModified().longValue() < term.getDateModified().longValue());

	/* Target term should not be changed */
	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term02Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term03Before.getDateModified().longValue() < term.getDateModified().longValue());
    }

    @Test
    @TestCase("process_tasks")
    public void processTaskChangeSourceTermStatusByTermIdNonSynonymTest() {

	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String termTickets = TERM_ID_01;

	String taskName = "change term status";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.ON_HOLD.getName();

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", termTickets }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$termStatus", newStatus },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	Term term01Before = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Term term02Before = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Term term03Before = getTermService().findTermById(TERM_ID_03, PROJECT_ID);

	taskHandler.processTasks(projectIds, null, command, null);

	Term term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term01Before.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term02Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term03Before.getDateModified().longValue() == term.getDateModified().longValue());
    }

    @Test
    @TestCase("process_tasks")
    public void processTaskChangeTargetTermStatusByLanguageSynonymTest() throws TmException {

	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "change term status";

	/* Add new non synonym de-DE term to TermEntry */

	String TERM_ID_122 = "c502608e-uuid-term-0122";
	Term newTerm = createTerm(TERM_ID_122, "de-DE", "house 2", false, ItemStatusTypeHolder.PROCESSED.getName(),
		"pm", false);
	addNewTermToTermEntry(termEntryId, newTerm);

	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.ON_HOLD.getName();

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "true" }, new String[] { "$termStatus", newStatus },
		new String[] { "$targetLanguage1", "de-DE" });

	Long[] projectIds = { PROJECT_ID };

	Term term01Before = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Term term02Before = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Term term03Before = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Term term12Before = getTermService().findTermById(TERM_ID_122, PROJECT_ID);

	taskHandler.processTasks(projectIds, null, command, null);

	Term term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term01Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term03Before.getDateModified().longValue() == term.getDateModified().longValue());

	/* All de-DE target terms (and synonyms) should have be changed */
	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term02Before.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_122, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term12Before.getDateModified().longValue() < term.getDateModified().longValue());
    }

    @Test
    @TestCase("process_tasks")
    public void processTaskChangeTargetTermStatusByTermIdNonSynonymTest() throws TmException {

	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "change term status";

	/* Add new non synonym de-DE term to TermEntry */

	String TERM_ID_122 = "c502608e-uuid-term-0122";
	Term newTerm = createTerm(TERM_ID_122, "de-DE", "house 2", false, ItemStatusTypeHolder.PROCESSED.getName(),
		"pm", false);
	addNewTermToTermEntry(termEntryId, newTerm);

	String termId = TERM_ID_01;

	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.ON_HOLD.getName();

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", termId }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$termStatus", newStatus },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	Term termIdBefore = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Term term02Before = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Term term03Before = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Term term12Before = getTermService().findTermById(TERM_ID_122, PROJECT_ID);

	taskHandler.processTasks(projectIds, null, command, null);

	/*
	 * Only term de-DE | TERM_ID_01 status should be changed, because only that id
	 * is added to command
	 */

	Term term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(termIdBefore.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term02Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term03Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_122, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term12Before.getDateModified().longValue() == term.getDateModified().longValue());
    }

    @Test
    @TestCase("process_tasks")
    public void processTaskChangeTermStatusInMultipleTermEntriesByTermIds() throws TmException {
	String termEntryId1 = TERM_ENTRY_ID_01;
	String termEntryId2 = TERM_ENTRY_ID_03;

	createTermEntry03();

	String multipleTermEntryIds = termEntryId1 + "\",\"" + termEntryId2;

	String termTickets = TERM_ID_01 + "\",\"" + TERM_ID_08;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "change term status";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String newStatus = ItemStatusTypeHolder.ON_HOLD.getName();

	ChangeTermStatusCommands command = (ChangeTermStatusCommands) getTaskHandlerCommand(taskHandler,
		"changeTermStatus.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", termTickets },
		new String[] { "$termEntryTicket1", multipleTermEntryIds }, new String[] { "$sourceLanguage", "" },
		new String[] { "$includeSourceSynonyms", "false" }, new String[] { "$includeTargetSynonyms", "false" },
		new String[] { "$termStatus", newStatus }, new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	Term term01Before = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Term term02Before = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Term term03Before = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Term term08Before = getTermService().findTermById(TERM_ID_08, PROJECT_ID);
	Term term09Before = getTermService().findTermById(TERM_ID_09, PROJECT_ID);

	taskHandler.processTasks(projectIds, null, command, null);

	Term term = getTermService().findTermById(TERM_ID_08, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term08Before.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_09, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term09Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertEquals(newStatus, term.getStatus());
	Assert.assertTrue(term01Before.getDateModified().longValue() < term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term02Before.getDateModified().longValue() == term.getDateModified().longValue());

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
	Assert.assertTrue(term03Before.getDateModified().longValue() == term.getDateModified().longValue());
    }

    private void addNewTermToTermEntry(String termTicket, Term term) throws TmException {
	TermEntry termEntry = getTermEntryService().findTermEntryById(termTicket, PROJECT_ID);
	termEntry.addTerm(term);
	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

}

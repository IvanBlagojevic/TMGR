package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SortDirection;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.command.ProjectLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.DeleteTermCommands;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("disable_term")
public class DeleteTermTaskHandlerTest extends AbstractSolrGlossaryTest {

    private static final String USER = "pm";

    @Test
    @TestCase("process_tasks")
    public void deleteMultipleTermEntries() throws TmException {

	createTermEntry03();

	String termEntryId1 = TERM_ENTRY_ID_01;
	String termEntryId2 = TERM_ENTRY_ID_03;

	String multipleTermEntryIds = termEntryId1 + "\",\"" + termEntryId2;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", multipleTermEntryIds },
		new String[] { "$sourceLanguage", "" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "true" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId1, PROJECT_ID);
	Assert.assertNotNull(termEntry);

	List<Term> deletedTerms = termEntry.ggetTerms();
	Assert.assertEquals(0, deletedTerms.size());

	termEntry = getTermEntryService().findTermEntryById(termEntryId2, PROJECT_ID);
	Assert.assertNotNull(termEntry);

	deletedTerms = termEntry.ggetTerms();
	Assert.assertEquals(0, deletedTerms.size());
    }

    @Test
    @TestCase("process_tasks")
    public void deleteTermThatDoesntExists() {

	String termEntryId = TERM_ENTRY_ID_03;
	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	assertNotNull(termEntry);
	assertTrue(CollectionUtils.isEmpty(termEntry.getLanguageTerms().get("fr-FR")));

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", TERM_ENTRY_ID_03 },
		new String[] { "$sourceLanguage", "fr-FR" }, new String[] { "$includeSourceSynonyms", "true" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

    }

    @Test
    @TestCase("process_tasks")
    public void deleteWholeTermEntry() {
	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	Assert.assertNotNull(termEntry);
	List<Term> terms = termEntry.ggetTerms();
	Assert.assertNotNull(terms);
	Assert.assertTrue(CollectionUtils.isNotEmpty(terms));

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", TERM_ENTRY_ID_01 },
		new String[] { "$sourceLanguage", "" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "true" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	Assert.assertNotNull(termEntry);

	List<Term> deletedTerms = termEntry.ggetTerms();
	Assert.assertEquals(0, deletedTerms.size());
    }

    @Test
    @TestCase("get_task_infos")
    public void getTaskInfosTest() {
	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Long[] projectIds = { PROJECT_ID };

	TaskModel[] taskModel = taskHandler.getTaskInfos(projectIds, taskName, null);

	Assert.assertNotNull(taskModel);
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteApprovedTermCountTest() throws TmException {

	String termEntryId = TERM_ENTRY_ID_01;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	TmUserProfile user = new TmUserProfile();

	ProjectLanguageDetailRequest projectLangcommand = new ProjectLanguageDetailRequest();
	projectLangcommand.setProjectDetailId(1L);
	projectLangcommand.setLanguageIds(new HashSet<>(Arrays.asList("en-US")));
	projectLangcommand.setUser(user);

	PagedListInfo pagedListInfo = new PagedListInfo();
	pagedListInfo.setSize(200);
	pagedListInfo.setSortDirection(SortDirection.ASCENDING);
	pagedListInfo.setSortProperty("dateModified");

	TaskPagedList<ProjectLanguageDetailView> pagedListBefore = getProjectLanguageDetailService()
		.search(projectLangcommand, pagedListInfo);

	Assert.assertEquals(4, pagedListBefore.getElements()[0].getApprovedTermCount());

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", TERM_ID_01 }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	/* Testic Term count before test */
	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(PROJECT_ID,
		ProjectLanguageDetail.class);
	Assert.assertEquals(8, projectDetailBefore.getTermCount());

	taskHandler.processTasks(projectIds, null, command, null);

	TaskPagedList<ProjectLanguageDetailView> pagedListAfter = getProjectLanguageDetailService()
		.search(projectLangcommand, pagedListInfo);

	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(PROJECT_ID,
		ProjectLanguageDetail.class);

	/* Term count should be decreased by 1 */
	Assert.assertEquals(7, projectDetailAfter.getTermCount());

	Assert.assertEquals(3, pagedListAfter.getElements()[0].getApprovedTermCount());

	Assert.assertEquals(2, pagedListAfter.getElements()[0].getPendingApprovalTermCount());
	Assert.assertEquals(1, pagedListAfter.getElements()[0].getOnHoldTermCount());
	Assert.assertEquals(3, pagedListAfter.getElements()[0].getForbiddenTermCount());
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteForbiddenTermCountTest() throws TmException {

	String termEntryId = TERM_ENTRY_ID_01;

	String TERM_ID_12 = "c502608e-uuid-term-0122";

	addTermWithNewStatus(TERM_ID_12, ItemStatusTypeHolder.BLACKLISTED);

	Term newTerm = getTermService().findTermById(TERM_ID_12, PROJECT_ID);
	Assert.assertNotNull(newTerm);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	TmUserProfile user = new TmUserProfile();

	ProjectLanguageDetailRequest projectLangcommand = new ProjectLanguageDetailRequest();
	projectLangcommand.setProjectDetailId(1L);
	projectLangcommand.setLanguageIds(new HashSet<>(Arrays.asList("en-US")));
	projectLangcommand.setUser(user);

	PagedListInfo pagedListInfo = new PagedListInfo();
	pagedListInfo.setSize(200);
	pagedListInfo.setSortDirection(SortDirection.ASCENDING);
	pagedListInfo.setSortProperty("dateModified");

	TaskPagedList<ProjectLanguageDetailView> pagedListBefore = getProjectLanguageDetailService()
		.search(projectLangcommand, pagedListInfo);

	Assert.assertEquals(3, pagedListBefore.getElements()[0].getForbiddenTermCount());

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", TERM_ID_12 }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	/* Testic Term count before test */
	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(PROJECT_ID,
		ProjectLanguageDetail.class);
	Assert.assertEquals(8, projectDetailBefore.getTermCount());

	taskHandler.processTasks(projectIds, null, command, null);

	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(PROJECT_ID,
		ProjectLanguageDetail.class);

	/* Term count should be decreased by 1 */
	Assert.assertEquals(7, projectDetailAfter.getTermCount());

	TaskPagedList<ProjectLanguageDetailView> pagedListAfter = getProjectLanguageDetailService()
		.search(projectLangcommand, pagedListInfo);

	/* Forbidden term count should be decreased by one */
	Assert.assertEquals(2, pagedListAfter.getElements()[0].getForbiddenTermCount());

	/* Rest counts should stay the same */
	Assert.assertEquals(4, pagedListAfter.getElements()[0].getApprovedTermCount());
	Assert.assertEquals(1, pagedListAfter.getElements()[0].getOnHoldTermCount());
	Assert.assertEquals(2, pagedListAfter.getElements()[0].getPendingApprovalTermCount());
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteOnHoldTermCountTest() throws TmException {

	String termEntryId = TERM_ENTRY_ID_01;

	String TERM_ID_122 = "c502608e-uuid-term-0122";

	addTermWithNewStatus(TERM_ID_122, ItemStatusTypeHolder.ON_HOLD);

	Term newTerm = getTermService().findTermById(TERM_ID_122, PROJECT_ID);
	Assert.assertNotNull(newTerm);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	TmUserProfile user = new TmUserProfile();

	ProjectLanguageDetailRequest projectLangcommand = new ProjectLanguageDetailRequest();
	projectLangcommand.setProjectDetailId(1L);
	projectLangcommand.setLanguageIds(new HashSet<>(Arrays.asList("en-US")));
	projectLangcommand.setUser(user);

	PagedListInfo pagedListInfo = new PagedListInfo();
	pagedListInfo.setSize(200);
	pagedListInfo.setSortDirection(SortDirection.ASCENDING);
	pagedListInfo.setSortProperty("dateModified");

	TaskPagedList<ProjectLanguageDetailView> pagedListBefore = getProjectLanguageDetailService()
		.search(projectLangcommand, pagedListInfo);

	Assert.assertEquals(1, pagedListBefore.getElements()[0].getOnHoldTermCount());

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", TERM_ID_122 }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	TaskPagedList<ProjectLanguageDetailView> pagedListAfter = getProjectLanguageDetailService()
		.search(projectLangcommand, pagedListInfo);

	/* On Hold term count should be decreased by one */
	Assert.assertEquals(0, pagedListAfter.getElements()[0].getOnHoldTermCount());

	/* Rest counts should stay the same */
	Assert.assertEquals(4, pagedListAfter.getElements()[0].getApprovedTermCount());
	Assert.assertEquals(2, pagedListAfter.getElements()[0].getPendingApprovalTermCount());
	Assert.assertEquals(3, pagedListAfter.getElements()[0].getForbiddenTermCount());
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeletePendingApprovalTermCountTest() throws TmException {

	String termEntryId = TERM_ENTRY_ID_01;

	String TERM_ID_122 = "c502608e-uuid-term-0122";

	addTermWithNewStatus(TERM_ID_122, ItemStatusTypeHolder.WAITING);

	Term newTerm = getTermService().findTermById(TERM_ID_122, PROJECT_ID);
	Assert.assertNotNull(newTerm);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	TmUserProfile user = new TmUserProfile();

	ProjectLanguageDetailRequest projectLangcommand = new ProjectLanguageDetailRequest();
	projectLangcommand.setProjectDetailId(1L);
	projectLangcommand.setLanguageIds(new HashSet<>(Arrays.asList("en-US")));
	projectLangcommand.setUser(user);

	PagedListInfo pagedListInfo = new PagedListInfo();
	pagedListInfo.setSize(200);
	pagedListInfo.setSortDirection(SortDirection.ASCENDING);
	pagedListInfo.setSortProperty("dateModified");

	TaskPagedList<ProjectLanguageDetailView> pagedListBefore = getProjectLanguageDetailService()
		.search(projectLangcommand, pagedListInfo);

	Assert.assertEquals(2, pagedListBefore.getElements()[0].getPendingApprovalTermCount());

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", TERM_ID_122 }, new String[] { "$termEntryTicket1", termEntryId },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	/* Testic Term count before test */
	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(PROJECT_ID,
		ProjectLanguageDetail.class);
	Assert.assertEquals(8, projectDetailBefore.getTermCount());

	taskHandler.processTasks(projectIds, null, command, null);

	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(PROJECT_ID,
		ProjectLanguageDetail.class);

	/* Term count should be decreased by 1 */
	Assert.assertEquals(7, projectDetailAfter.getTermCount());

	TaskPagedList<ProjectLanguageDetailView> pagedListAfter = getProjectLanguageDetailService()
		.search(projectLangcommand, pagedListInfo);

	/* Pending Approval term count should be decreased by one */
	Assert.assertEquals(1, pagedListAfter.getElements()[0].getPendingApprovalTermCount());

	/* Rest counts should stay the same */
	Assert.assertEquals(4, pagedListAfter.getElements()[0].getApprovedTermCount());
	Assert.assertEquals(1, pagedListAfter.getElements()[0].getOnHoldTermCount());
	Assert.assertEquals(3, pagedListAfter.getElements()[0].getForbiddenTermCount());
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteSourceAndSourceSynonymOnlyByLanguageIdTest() {
	String termId = TERM_ID_01;

	Term term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNotNull(term);
	Assert.assertTrue(term.isFirst());

	Term synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(!synonym.isFirst());

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", TERM_ENTRY_ID_01 },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "true" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNull(term);

	synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNull(synonym);

	Term germanTerm = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertNotNull(germanTerm);
	Assert.assertTrue(germanTerm.isFirst());
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteSourceOnlyByTermByIdNonSynonymTest() {
	String termId = TERM_ID_01;

	Term term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNotNull(term);
	Assert.assertTrue(term.isFirst());

	Term synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(!synonym.isFirst());

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", termId }, new String[] { "$termEntryTicket1", TERM_ENTRY_ID_01 },
		new String[] { "$sourceLanguage", "" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNull(term);

	synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(synonym.isFirst());
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteSourceTermByIdAndTargetByLanguageIdNonSynonymsTest() {
	String termId = TERM_ID_01;

	Term term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNotNull(term);
	Assert.assertTrue(term.isFirst());

	Term synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(!synonym.isFirst());

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", termId }, new String[] { "$termEntryTicket1", TERM_ENTRY_ID_01 },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "true" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "de-DE" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNull(term);

	synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(synonym.isFirst());

	// German term should be deleted because target language is set as de-DE
	Term germanTerm = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertNull(germanTerm);
    }

    /*
     * Same source term is selected by termId and languageId(source synonym should
     * not be deleted)
     */
    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteSourceTermByTermIdAndLanguageIdNonSourceSynonymsTest() {
	String termId = TERM_ID_01;

	Term term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNotNull(term);
	Assert.assertTrue(term.isFirst());

	Term synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(!synonym.isFirst());

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", termId }, new String[] { "$termEntryTicket1", TERM_ENTRY_ID_01 },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNull(term);

	synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(synonym.isFirst());

	// German term should be deleted because target language is set as de-DE
	Term germanTerm = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertNotNull(germanTerm);
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteSourceTermByTermIdAndSourceLanguageNonSynonymsTest() {
	String termId = TERM_ID_01;

	Term term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNotNull(term);
	Assert.assertTrue(term.isFirst());

	Term synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(!synonym.isFirst());

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", termId }, new String[] { "$termEntryTicket1", TERM_ENTRY_ID_01 },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNull(term);

	synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(synonym.isFirst());

	Term germanTerm = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertNotNull(germanTerm);
	Assert.assertTrue(germanTerm.isFirst());
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteTargetAndTargetSynonymOnlyByLanguageIdTest() throws TmException {
	String termId = TERM_ID_01;

	final String TERM_ID_122 = "c502608e-uuid-term-0122";

	/* Add new synonym for german language */
	addnewLanguageToTermEntry(TERM_ID_122);

	Term term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNotNull(term);
	Assert.assertTrue(term.isFirst());

	Term synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(!synonym.isFirst());

	Term germanSynonym = getTermService().findTermById(TERM_ID_122, PROJECT_ID);
	Assert.assertNotNull(germanSynonym);
	Assert.assertFalse(germanSynonym.isFirst());

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", TERM_ENTRY_ID_01 },
		new String[] { "$sourceLanguage", "de-DE" }, new String[] { "$includeSourceSynonyms", "true" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNotNull(term);

	synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);

	Term germanTerm = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertNull(germanTerm);

	Term germanSynonymTerm = getTermService().findTermById(TERM_ID_122, PROJECT_ID);
	Assert.assertNull(germanSynonymTerm);

    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteTargetSynonymsWhenTargetDontExistsTest() throws TmException {

	TermEntry termEntry = new TermEntry();
	String TERM_ENTRY_ID_08 = "c502608e-uuid-term-entry-008";
	termEntry.setUuId("c502608e-uuid-term-entry-008");
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName("projectName");
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	String TERM_ID_15 = "c502608e-uuid-term-015";

	Term sourceTermSynonym = createTerm(TERM_ID_15, "en-US", "german synonym", false,
		ItemStatusTypeHolder.PROCESSED.getName(), USER, true);

	termEntry.addTerm(sourceTermSynonym);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", TERM_ENTRY_ID_08 },
		new String[] { "$sourceLanguage", "" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "true" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "de-DE" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

    }

    /*
     * Target term is selected by termId and languageId, source term should not be
     * deleted(synonym and regular term)
     */
    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteTargetTermByTermIdAndSourceNonSynonymsTest() throws TmException {
	String termId = TERM_ID_02;

	String TERM_ID_122 = "c502608e-uuid-term-0122";

	/* Add new synonym for german language */
	addnewLanguageToTermEntry(TERM_ID_122);

	Term term = getTermService().findTermById(termId, PROJECT_ID);
	Assert.assertNotNull(term);
	Assert.assertTrue(term.isFirst());

	Term synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertTrue(!synonym.isFirst());

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", termId }, new String[] { "$termEntryTicket1", TERM_ENTRY_ID_01 },
		new String[] { "$sourceLanguage", "" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "de-DE" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertNull(term);

	/* Non synonyms in de-DE should not be deleted */
	term = getTermService().findTermById(TERM_ID_122, PROJECT_ID);
	Assert.assertNotNull(term);

	synonym = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(synonym);
	Assert.assertFalse(synonym.isFirst());

	Term germanTerm = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertNotNull(germanTerm);
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteTermsInMultipleTermEntriesBySourceLanguageAndSynonymTest() throws TmException {
	createTermEntry03();

	String termEntryId1 = TERM_ENTRY_ID_01;
	String termEntryId2 = TERM_ENTRY_ID_03;

	String multipleTermEntryIds = termEntryId1 + "\",\"" + termEntryId2;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", multipleTermEntryIds },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "true" },
		new String[] { "$includeTargetSynonyms", "false" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	Term term = getTermService().findTermById(TERM_ID_08, PROJECT_ID);
	Assert.assertNull(term);

	term = getTermService().findTermById(TERM_ID_09, PROJECT_ID);
	Assert.assertNotNull(term);

	term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertNull(term);

	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertNotNull(term);

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNull(term);
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteTermsInMultipleTermEntriesBySourceLanguageTest() throws TmException {
	createTermEntry03();

	String termEntryId1 = TERM_ENTRY_ID_01;
	String termEntryId2 = TERM_ENTRY_ID_03;

	String multipleTermEntryIds = termEntryId1 + "\",\"" + termEntryId2;

	String termTickets = TERM_ID_01 + "\",\"" + TERM_ID_08;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", termTickets },
		new String[] { "$termEntryTicket1", multipleTermEntryIds }, new String[] { "$sourceLanguage", "en-US" },
		new String[] { "$includeSourceSynonyms", "false" }, new String[] { "$includeTargetSynonyms", "false" },
		new String[] { "$deleteTermEntries", "false" }, new String[] { "$targetLanguage1", "" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	Term term = getTermService().findTermById(TERM_ID_08, PROJECT_ID);
	Assert.assertNull(term);

	term = getTermService().findTermById(TERM_ID_09, PROJECT_ID);
	Assert.assertNotNull(term);

	term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertNull(term);

	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertNotNull(term);

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(term);
    }

    @Test
    @TestCase("process_tasks")
    public void processTasksDeleteTermsInMultipleTermEntriesByTargetLanguageTest() throws TmException {
	createTermEntry03();

	String termEntryId1 = TERM_ENTRY_ID_01;
	String termEntryId2 = TERM_ENTRY_ID_03;

	String multipleTermEntryIds = termEntryId1 + "\",\"" + termEntryId2;

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler,
		"deleteTermOrTermEntryByIdsOrLanguage.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$termTicket1", "" }, new String[] { "$termEntryTicket1", multipleTermEntryIds },
		new String[] { "$sourceLanguage", "en-US" }, new String[] { "$includeSourceSynonyms", "false" },
		new String[] { "$includeTargetSynonyms", "true" }, new String[] { "$deleteTermEntries", "false" },
		new String[] { "$targetLanguage1", "de-DE" });

	Long[] projectIds = { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	Term term = getTermService().findTermById(TERM_ID_08, PROJECT_ID);
	Assert.assertNotNull(term);

	term = getTermService().findTermById(TERM_ID_09, PROJECT_ID);
	Assert.assertNull(term);

	term = getTermService().findTermById(TERM_ID_01, PROJECT_ID);
	Assert.assertNotNull(term);

	term = getTermService().findTermById(TERM_ID_02, PROJECT_ID);
	Assert.assertNull(term);

	term = getTermService().findTermById(TERM_ID_03, PROJECT_ID);
	Assert.assertNotNull(term);
    }

    private void addTermWithNewStatus(String termId, ItemStatusType statusType) throws TmException {
	Term germanTermSynonym = createTerm(termId, "en-US", "new term", false, statusType.getName(), USER, false);

	TermEntry termEntry = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_01, PROJECT_ID);
	termEntry.addTerm(germanTermSynonym);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void addnewLanguageToTermEntry(String termId) throws TmException {
	Term germanTermSynonym = createTerm(termId, "de-DE", "german synonym", false,
		ItemStatusTypeHolder.PROCESSED.getName(), USER, false);

	TermEntry termEntry = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_01, PROJECT_ID);
	termEntry.addTerm(germanTermSynonym);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

}

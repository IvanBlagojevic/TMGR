package org.gs4tr.termmanager.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.dao.ProjectUserLanguageDAO;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Priority;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.ProjectCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.IMap;

@TestSuite("edit_project")
public class EditProjectTaskHandlerTest extends AbstractSolrGlossaryTest {

    private static final String PROJECT_NAME = "projectName";

    @Autowired
    private ProjectUserLanguageDAO _projectUserLanguageDAO;

    public ProjectUserLanguageDAO getProjectUserLanguageDAO() {
	return _projectUserLanguageDAO;
    }

    @Test
    @TestCase("get_task_infos")
    public void testEditProjectLanguage_Case1() throws TmException {
	clearSolr();
	/*
	 * If we have one user who is assigned on (de-DE,en-US) and one user who is
	 * assigned on (fr-FR,en-US) after admin removes en-US from project then de-DE
	 * and fr-FR become non removable
	 */

	String taskName = "edit project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	List<String> languagesIds = Arrays.asList("en-US", "fr-FR");

	List<Long> userIds = Arrays.asList(3L);

	getProjectService().addOrUpdateProjectUserLanguages(3L, 1L, languagesIds, userIds, false);

	Object command = getTaskHandlerCommand(taskHandler, "editProjectLang.json");

	ProjectCommand projectCommand = (ProjectCommand) command;
	List<ProjectLanguage> projectLanguages1 = projectCommand.getProjectLanguages();

	Optional<ProjectLanguage> o = projectLanguages1.stream()
		.filter(projectLanguage -> projectLanguage.getLanguage().equals("en-US")).findFirst();
	o.get().setLanguage("fr-FR");

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, taskName, command);
	Assert.assertNotNull(taskInfos);

	Map<String, Object> model = taskInfos[0].getModel();

	List<Map<String, Object>> projectLanguages = (List<Map<String, Object>>) model.get("projectLanguages");
	Assert.assertNotNull(projectLanguages);

	projectLanguages.forEach(pl -> {
	    Map<String, Object> language = (Map<String, Object>) pl.get("language");
	    Assert.assertFalse((Boolean) language.get("canBeRemoved"));
	});
    }

    @Test
    @TestCase("get_task_infos")
    public void testEditProjectLanguage_Case2() throws TmException {
	clearSolr();
	/*
	 * if we try to remove fr-FR language from project and we have user who is
	 * assigned only for en-US language then en-US language will still be non
	 * removable
	 */

	String taskName = "edit project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "editProjectLang.json");

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, taskName, command);
	Assert.assertNotNull(taskInfos);

	Map<String, Object> model = taskInfos[0].getModel();

	List<Map<String, Object>> projectLanguages = (List<Map<String, Object>>) model.get("projectLanguages");
	Assert.assertNotNull(projectLanguages);

	projectLanguages.forEach(pl -> {
	    Map<String, Object> language = (Map<String, Object>) pl.get("language");
	    if (language.get("languageLocale").equals("en-US")) {
		Assert.assertFalse((Boolean) language.get("canBeRemoved"));
	    } else
		// de-DE is removable because there is no users assigned only on this lang
		Assert.assertTrue((Boolean) language.get("canBeRemoved"));
	});
    }

    @Test
    @TestCase("get_task_infos")
    public void testEditProjectLanguage_Case3() throws TmException {
	clearSolr();
	/*
	 * if admin deletes the languages so that only en-US is left then en-US become
	 * non removable
	 */
	String taskName = "edit project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "editProjectLang.json");
	ProjectCommand projectCommand = (ProjectCommand) command;
	List<ProjectLanguage> projectLanguages1 = projectCommand.getProjectLanguages();

	Optional<ProjectLanguage> o = projectLanguages1.stream()
		.filter(projectLanguage -> projectLanguage.getLanguage().equals("de-DE")).findFirst();
	o.ifPresent(projectLanguage -> projectLanguages1.remove(projectLanguage));

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, taskName, command);
	Assert.assertNotNull(taskInfos);

	Map<String, Object> model = taskInfos[0].getModel();

	List<Map<String, Object>> projectLanguages = (List<Map<String, Object>>) model.get("projectLanguages");
	Assert.assertNotNull(projectLanguages);

	projectLanguages.forEach(pl -> {
	    Map<String, Object> language = (Map<String, Object>) pl.get("language");
	    Assert.assertFalse((Boolean) language.get("canBeRemoved"));
	});
    }

    @Test
    @TestCase("get_task_infos")
    public void testEditProjectLanguages_RegularTerminology() {
	/*
	 * Terminology(regular collection) for en don't exists, for en-US exists so "en"
	 * is removable
	 */
	String taskName = "edit project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	TmProject projectById = getProjectService().findProjectById(1L, TmProject.class);

	ProjectLanguage projectLanguage = new ProjectLanguage();
	projectLanguage.setProject(projectById);
	projectLanguage.setLanguage("en");

	ProjectLanguage projectLanguage1 = new ProjectLanguage();
	projectLanguage1.setProject(projectById);
	projectLanguage1.setLanguage("en-US");

	getProjectService().addOrUpdateProjectLanguages(1L, Arrays.asList(projectLanguage, projectLanguage1));

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, taskName, null);

	Map<String, Object> model = taskInfos[0].getModel();

	List<Map<String, Object>> projectLanguages = (List<Map<String, Object>>) model.get("projectLanguages");
	Assert.assertNotNull(projectLanguages);

	projectLanguages.forEach(pl -> {
	    Map<String, Object> language = (Map<String, Object>) pl.get("language");
	    if (language.get("languageLocale").equals("en")) {
		Assert.assertTrue((Boolean) language.get("canBeRemoved"));
	    } else
		Assert.assertFalse((Boolean) language.get("canBeRemoved"));
	});
    }

    @Test
    @TestCase("get_task_infos")
    public void testEditProjectLanguages_SubmissionTerminology() throws TmException {
	clearSolr();
	createSubTermEntry();
	/*
	 * Terminology(submission collection) for fr-FR don't exists, for de-DE exists
	 * so "fr-FR" is removable and de-DE isn't
	 */
	String taskName = "edit project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, taskName, null);

	Map<String, Object> model = taskInfos[0].getModel();
	Assert.assertNotNull(model);

	List<Map<String, Object>> projectLanguages = (List<Map<String, Object>>) model.get("projectLanguages");
	Assert.assertNotNull(projectLanguages);

	projectLanguages.forEach(pl -> {
	    Map<String, Object> language = (Map<String, Object>) pl.get("language");
	    if (language.get("languageLocale").equals("de-DE")) {
		Assert.assertFalse((Boolean) language.get("canBeRemoved"));
	    } else if (language.get("languageLocale").equals("fr-FR")) {
		Assert.assertTrue((Boolean) language.get("canBeRemoved"));
	    }
	});
    }

    @Test
    @TestCase("get_task_infos")
    public void testEditProjectWithOnlyOneLanguage() throws Exception {
	/* Only one language on project("en") which means it is not removable */

	String taskName = "edit project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	TmProject projectById = getProjectService().findProjectById(1L, TmProject.class);

	ProjectLanguage projectLanguage = new ProjectLanguage();
	projectLanguage.setProject(projectById);
	projectLanguage.setLanguage("en");

	getProjectService().addOrUpdateProjectLanguages(1L, Arrays.asList(projectLanguage));

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, taskName, null);

	Map<String, Object> model = taskInfos[0].getModel();

	List<Map<String, Object>> projectLanguages = (List<Map<String, Object>>) model.get("projectLanguages");
	Assert.assertNotNull(projectLanguages);

	projectLanguages.forEach(pl -> {
	    Map<String, Object> language = (Map<String, Object>) pl.get("language");
	    Assert.assertFalse((Boolean) language.get("canBeRemoved"));
	});
    }

    @Test
    @TestCase("get_task_infos")
    public void testGetTaskInfos() throws Exception {

	String taskName = "edit project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, taskName, null);

	String result = JsonUtils.writeValueAsString(taskInfos);

	assertJSONResponse(result, "editProjectValidation.json");
    }

    @Test
    @TestCase("process_tasks")
    public void testProcessTasks() throws TmException {
	clearSolr();

	Long projectId = new Long(1);

	TmProject project = getProjectService().findById(projectId);

	Assert.assertNotNull(project);

	Assert.assertEquals("testProject", project.getProjectInfo().getName());

	String taskName = "edit project";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "editProject.json");

	TaskResponse response = taskHandler.processTasks(null, new Long[] { projectId }, command, null);

	Assert.assertNotNull(response);

	TmProject changedProject = getProjectService().findById(projectId);

	Assert.assertNotNull(changedProject);

	Assert.assertEquals("project", changedProject.getProjectInfo().getName());

	ProjectCommand commandEditLangs = (ProjectCommand) getTaskHandlerCommand(taskHandler,
		"editProjectUserLanguage.json");
	response = taskHandler.processTasks(null, new Long[] { projectId }, commandEditLangs, null);

	Assert.assertNotNull(response);

	TmProject changedProjectLanguages = getProjectService().findProjectById(projectId, ProjectLanguage.class);

	Assert.assertNotNull(changedProjectLanguages);

	Set<ProjectLanguage> projectLanguages = changedProjectLanguages.getProjectLanguages();

	Assert.assertTrue(CollectionUtils.isNotEmpty(projectLanguages));

	boolean flag = false;
	for (ProjectLanguage projectLanguage : projectLanguages) {
	    if (projectLanguage.getLanguage().equals("en-US")) {
		flag = true;
		break;
	    }
	}

	Assert.assertTrue(flag);
    }

    @Test
    @TestCase("process_tasks")
    public void testProcessTasksSharePendingTerms() {
	Long projectId = new Long(1);

	TmProject projectBefore = getProjectService().findById(projectId);

	Assert.assertNotNull(projectBefore);

	Assert.assertTrue(projectBefore.getSharePendingTerms());

	String taskName = "edit project";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "editProject.json");

	ProjectCommand dtoProjectCommand = (ProjectCommand) command;

	dtoProjectCommand.setSharePendingTerms(Boolean.FALSE);

	TaskResponse response = taskHandler.processTasks(null, new Long[] { projectId }, dtoProjectCommand, null);

	Assert.assertNotNull(response);

	TmProject projectAfter = getProjectService().findProjectById(projectId, ProjectLanguage.class);

	Assert.assertFalse(projectAfter.getSharePendingTerms());

    }

    /*
     * TERII-5025 After adding additional languages to the project application error
     * appears
     */
    @Test
    @TestCase("process_tasks")
    public void testProcessTasksSharePendingTermsFieldIsAbsent() {
	Long projectId = new Long(1);

	TmProject projectBefore = getProjectService().findById(projectId);

	Assert.assertNotNull(projectBefore);

	Assert.assertTrue(projectBefore.getSharePendingTerms());

	String taskName = "edit project";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "editProject.json");

	ProjectCommand dtoProjectCommand = (ProjectCommand) command;

	/*
	 * Set Share Pending Terms to null because request on edit project don't have
	 * sharePendingTerms field
	 */
	dtoProjectCommand.setSharePendingTerms(null);

	TaskResponse response = taskHandler.processTasks(null, new Long[] { projectId }, dtoProjectCommand, null);

	Assert.assertNotNull(response);

	TmProject projectAfter = getProjectService().findProjectById(projectId, ProjectLanguage.class);

	/*
	 * Share pending terms status on project should not be changed is request field
	 * is absent
	 */
	Assert.assertTrue(projectAfter.getSharePendingTerms());

    }

    /*
     * TERII-4174 TMGR should break connection if it becomes invalid
     */
    @Test
    @TestCase("process_tasks")
    public void testRemoveSessionFromCashOn() throws TmException {
	clearSolr();

	Long projectId = 1L;

	TmProject project = getProjectService().findById(projectId);

	TmUserProfile userProfile1 = getUserProfileService().findById(3L);
	TmUserProfile userProfile2 = getUserProfileService().findById(6L);
	Assert.assertNotNull(userProfile1);
	Assert.assertNotNull(userProfile2);

	List<ProjectLanguage> projectLanguages = getProjectService().getProjectLanguages(projectId);

	Assert.assertNotNull(project);
	Assert.assertNotNull(projectLanguages);
	projectLanguages.remove(2);

	ConnectionInfoHolder connectionInfoHolder1 = new ConnectionInfoHolder();
	connectionInfoHolder1.setProjectId(projectId);
	connectionInfoHolder1.setUserProfile(userProfile1);
	connectionInfoHolder1.setSource("en-US");
	connectionInfoHolder1.setTarget("fr-FR");

	ConnectionInfoHolder connectionInfoHolder2 = new ConnectionInfoHolder();
	connectionInfoHolder2.setProjectId(projectId);
	connectionInfoHolder2.setUserProfile(userProfile2);
	connectionInfoHolder2.setSource("en-US");
	connectionInfoHolder2.setTarget("de-DE");

	String session1 = "session1";
	String session2 = "session2";

	IMap<String, ConnectionInfoHolder> sessionCache = getCacheGateway()
		.findCacheByName(CacheName.V2_GLOSSARY_SESSIONS);

	// Login users
	sessionCache.put(session1, connectionInfoHolder1);
	sessionCache.put(session2, connectionInfoHolder2);

	Assert.assertEquals("testProject", project.getProjectInfo().getName());

	String taskName = "edit project";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "editProject.json");

	ProjectCommand projCommand = (ProjectCommand) command;

	projCommand.setProjectLanguages(projectLanguages);

	TaskResponse response = taskHandler.processTasks(null, new Long[] { projectId }, projCommand, null);

	Assert.assertNotNull(response);

	waitServiceThreadPoolThreads();

	/*
	 * This User should be Logged out because he has fr-FR language which is removed
	 * from the project
	 */
	Assert.assertNull(sessionCache.get(session1));

	/*
	 * This User should not be Logged out because he don't have fr-FR which is
	 * removed from the project
	 */
	Assert.assertNotNull(sessionCache.get(session2));
    }

    private void createSubTermEntry() {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_01);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);

	Term term1 = createTerm(TERM_ID_01, "en-US", "house", false, STATUS_APPROVED, "pm", true);
	Term term2 = createTerm(TERM_ID_02, "de-DE", "germanHouse", false,
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), "pm", true);

	termEntry.addTerm(term1);
	termEntry.addTerm(term2);

	// Create submission term entry
	TermEntry subTermEntry = new TermEntry();
	subTermEntry.setParentUuId(TERM_ENTRY_ID_01);
	subTermEntry.setUuId(SUB_TERM_ENTRY_ID_01);
	subTermEntry.setProjectId(PROJECT_ID);
	subTermEntry.setProjectName(PROJECT_NAME);
	subTermEntry.setSubmissionId(1L);

	Term subTerm1 = createTerm(SUB_TERM_ID_01, "de-DE", "Maus", false,
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), "pm", true);
	subTerm1.setParentUuId(term2.getUuId());
	subTerm1.setAssignee("pm");
	subTerm1.setSubmitter("pm");
	subTerm1.setCanceled(Boolean.FALSE);
	subTerm1.setReviewRequired(Boolean.FALSE);
	subTerm1.setTempText("Big Maus");
	subTerm1.setSubmissionId(1L);
	subTerm1.setSubmissionName("FirstJob");
	subTerm1.setPriority(new Priority(2, 2));
	subTerm1.setCommited(Boolean.FALSE);
	subTerm1.setTermEntryId(subTermEntry.getUuId());
	subTerm1.setInTranslationAsSource(Boolean.FALSE);
	subTerm1.setStatusOld(ItemStatusTypeHolder.WAITING.getName());
	subTerm1.setDateSubmitted(System.currentTimeMillis());

	Term subTerm2 = createTerm(SUB_TERM_ID_02, "en-US", "house", false, STATUS_APPROVED, "pm", true);
	subTerm2.setParentUuId(term1.getUuId());
	subTerm2.setAssignee("pm");
	subTerm2.setSubmitter("pm");
	subTerm2.setCanceled(Boolean.FALSE);
	subTerm2.setReviewRequired(Boolean.TRUE);
	subTerm2.setTempText("house");
	subTerm2.setSubmissionId(1L);
	subTerm2.setSubmissionName("FirstJob");
	subTerm2.setPriority(new Priority(2, 2));
	subTerm2.setCommited(Boolean.FALSE);
	subTerm2.setTermEntryId(subTermEntry.getUuId());
	subTerm2.setInTranslationAsSource(Boolean.FALSE);
	subTerm2.setStatusOld(STATUS_PENDING);
	subTerm2.setDateSubmitted(System.currentTimeMillis());

	subTermEntry.addTerm(subTerm1);
	subTermEntry.addTerm(subTerm2);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
	getTermEntryService().updateSubmissionTermEntries(PROJECT_ID, Arrays.asList(subTermEntry));
    }
}

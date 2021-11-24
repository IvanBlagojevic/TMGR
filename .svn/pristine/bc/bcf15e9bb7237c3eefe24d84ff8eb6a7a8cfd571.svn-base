package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.termmanager.model.MultilingualTerm;
import org.gs4tr.termmanager.model.TermHolder;
import org.gs4tr.termmanager.model.TermSearchRequest;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.search.TypeSearchEnum;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.MultilingualViewModelService;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.solr.GlossaryConnectionManager;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class MultilingualViewModelServiceImplTest extends AbstractServiceTest {

    @Autowired
    private GlossaryConnectionManager _connectionManager;

    @Autowired
    private MultilingualViewModelService _multilingualViewModelService;

    @Override
    public GlossaryConnectionManager getConnectionManager() {
	return _connectionManager;
    }

    public MultilingualViewModelService getMultilingualViewModelService() {
	return _multilingualViewModelService;
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("multilingualViewModelService")
    public void multilingualSearchTestActionButtonsOrder() throws TmException {
	TermSearchRequest command = getModelObject("command", TermSearchRequest.class);

	command.setTypeSearch(TypeSearchEnum.TERM);
	command.setFolder(ItemFolderEnum.TERM_LIST);

	PagedListInfo pagedListInfo = getModelObject("pagedListInfo1", PagedListInfo.class);

	List<TermEntry> termEntries = getModelObject("termEntries2", List.class);

	Set<String> userPolices = getModelObject("userPolices", Set.class);

	Page<TermEntry> page = new Page<TermEntry>(50, 0, 50, termEntries);

	when(getGlossarySearcher().concordanceSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	// Set new user polices
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	user.getUserInfo().setUserType(UserTypeEnum.POWER_USER);

	Set<String> polices = user.getContextPolicies(1L);
	polices.clear();
	polices.addAll(userPolices);

	TaskPagedList<MultilingualTerm> taskPagedList = getMultilingualViewModelService().search(command,
		pagedListInfo);

	Assert.assertNotNull(taskPagedList);

	Task[] tasks = taskPagedList.getTasks();
	Assert.assertNotNull(tasks);

	List<Task> taskList = Arrays.asList(tasks);

	sortTasks(taskList);

	/* Test order after sorting by property */

	Assert.assertEquals("add term", taskList.get(0).getName());
	Assert.assertEquals("save changes", taskList.get(2).getName());
	Assert.assertEquals("undo changes", taskList.get(4).getName());
	Assert.assertEquals("send to translation", taskList.get(5).getName());
	Assert.assertEquals("change term status", taskList.get(6).getName());
	Assert.assertEquals("disable term", taskList.get(14).getName());
	Assert.assertEquals("import tbx", taskList.get(15).getName());
	Assert.assertEquals("export tbx", taskList.get(16).getName());

    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("multilingualViewModelService")
    public void multilingualSearchTest_01() throws TmException {
	TermSearchRequest termSearchRequest = getModelObject("termSearchRequest", TermSearchRequest.class);

	PagedListInfo pagedListInfo = getModelObject("pagedListInfo", PagedListInfo.class);

	List<TermEntry> termentries = getModelObject("termentries", List.class);
	Page<TermEntry> page = new Page<TermEntry>(50, 0, 50, termentries);

	when(getGlossarySearcher().concordanceSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	PagedList<MultilingualTerm> result = getMultilingualViewModelService().search(termSearchRequest, pagedListInfo);

	verify(getGlossarySearcher(), times(1)).concordanceSearch(any(TmgrSearchFilter.class));

	MultilingualTerm[] elements = result.getElements();

	MultilingualTerm multilingualTerm = elements[0];

	assertEquals(2, multilingualTerm.getTerms().size());
	assertEquals("en-US", multilingualTerm.getSourceLanguage());
    }

    /* TERII-2932: "Term that is not part of the submission is not editable". */
    @SuppressWarnings("unchecked")
    @Test
    @TestCase("multilingualViewModelService")
    public void multilingualSearchTest_02() throws TmException {
	TermSearchRequest command = getModelObject("command", TermSearchRequest.class);
	PagedListInfo pagedListInfo = getModelObject("pagedListInfo1", PagedListInfo.class);

	List<TermEntry> termEntries = getModelObject("termEntries1", List.class);

	Page<TermEntry> page = new Page<TermEntry>(50, 0, 50, termEntries);

	when(getGlossarySearcher().concordanceSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	PagedList<MultilingualTerm> taskPagedList = getMultilingualViewModelService().search(command, pagedListInfo);

	verify(getGlossarySearcher(), times(1)).concordanceSearch(any(TmgrSearchFilter.class));

	MultilingualTerm[] multilingualTerms = taskPagedList.getElements();

	MultilingualTerm multilingualTerm = multilingualTerms[0];

	assertNotNull(multilingualTerms);

	assertEquals(1, multilingualTerms.length);
	assertEquals("en-US", multilingualTerm.getSourceLanguage());

	assertFalse(multilingualTerm.isCanceled());
	assertFalse(multilingualTerm.isInFinalReview());
	assertTrue(multilingualTerm.isInTranslation());

	assertTrue(multilingualTerm.getTerms().size() == 3);

	for (TermHolder termHolder : multilingualTerm.getTerms()) {
	    assertNotNull(termHolder.getLanguageId());
	    assertNotNull(termHolder.getAlignment());
	    assertNotNull(termHolder.getTerms());

	    if (termHolder.getLanguageId().equals("fr-FR")) {
		assertNull(termHolder.getSubmissionId());
		assertNull(termHolder.getSubmissionName());
		assertNull(termHolder.getSubmitter());
		assertNull(termHolder.getTermTooltip());
	    } else {
		assertNotNull(termHolder.getSubmissionId());
		assertNotNull(termHolder.getSubmissionName());
		assertNotNull(termHolder.getSubmitter());
		assertNotNull(termHolder.getTermTooltip());
	    }
	}
    }

    /*
     * ***************************************************************************
     * Note: If command don't have projectIds, they should be added from user
     * default project and if user don't have it we are adding first projectId from
     * project user language.
     * ***************************************************************************
     */

    /*
     * TERII-5280 TDC & TPT Merge Tool: application error appears after project is
     * removed from filters
     */
    @SuppressWarnings("unchecked")
    @Test
    @TestCase("multilingualViewModelService")
    public void multilingualTestSearchCommandWithoutProjectIds() throws TmException {
	TermSearchRequest command = getModelObject("command", TermSearchRequest.class);

	command.setTypeSearch(TypeSearchEnum.TERM);
	command.setFolder(ItemFolderEnum.TERM_LIST);

	// Empty projectIds from command
	command.setProjectIds(null);

	PagedListInfo pagedListInfo = getModelObject("pagedListInfo1", PagedListInfo.class);

	List<TermEntry> termEntries = getModelObject("termEntries2", List.class);

	Set<String> userPolices = getModelObject("userPolices", Set.class);

	Page<TermEntry> page = new Page<TermEntry>(50, 0, 50, termEntries);

	when(getGlossarySearcher().concordanceSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	// Set new user polices
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	user.getUserInfo().setUserType(UserTypeEnum.POWER_USER);

	Set<String> polices = user.getContextPolicies(1L);
	polices.clear();
	polices.addAll(userPolices);

	TaskPagedList<MultilingualTerm> taskPagedList = getMultilingualViewModelService().search(command,
		pagedListInfo);

	Long commandProjectId = command.getProjectId();
	Long expectedProjectId = 1L;
	// Check if default user's project is added to command.
	Assert.assertEquals(expectedProjectId, commandProjectId);

	Assert.assertNotNull(taskPagedList);

	Task[] tasks = taskPagedList.getTasks();
	Assert.assertNotNull(tasks);

	List<Task> taskList = Arrays.asList(tasks);

	sortTasks(taskList);

	/* Test order after sorting by property */

	Assert.assertEquals("add term", taskList.get(0).getName());
	Assert.assertEquals("save changes", taskList.get(2).getName());
	Assert.assertEquals("undo changes", taskList.get(4).getName());
	Assert.assertEquals("send to translation", taskList.get(5).getName());
	Assert.assertEquals("change term status", taskList.get(6).getName());
	Assert.assertEquals("disable term", taskList.get(14).getName());
	Assert.assertEquals("import tbx", taskList.get(15).getName());
	Assert.assertEquals("export tbx", taskList.get(16).getName());

    }

    /*
     * TERII-5280 TDC & TPT Merge Tool: application error appears after project is
     * removed from filters
     */

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("multilingualViewModelService")
    public void multilingualTestSearchCommandWithoutProjectIdsAndUserDefaultProject() throws TmException {
	TermSearchRequest command = getModelObject("command", TermSearchRequest.class);

	command.setTypeSearch(TypeSearchEnum.TERM);
	command.setFolder(ItemFolderEnum.TERM_LIST);

	// Empty projectIds from command
	command.setProjectIds(null);

	PagedListInfo pagedListInfo = getModelObject("pagedListInfo1", PagedListInfo.class);

	List<TermEntry> termEntries = getModelObject("termEntries2", List.class);

	Set<String> userPolices = getModelObject("userPolices", Set.class);

	Page<TermEntry> page = new Page<TermEntry>(50, 0, 50, termEntries);

	when(getGlossarySearcher().concordanceSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	// Set new user polices
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	user.getUserInfo().setUserType(UserTypeEnum.POWER_USER);
	// We are removing default project for this user
	user.getPreferences().setDefaultProjectId(null);

	Set<String> polices = user.getContextPolicies(1L);
	polices.clear();
	polices.addAll(userPolices);

	TaskPagedList<MultilingualTerm> taskPagedList = getMultilingualViewModelService().search(command,
		pagedListInfo);

	Long commandProjectId = command.getProjectId();
	Long expectedProjectId = 1L;
	// Check if default user's project is added to command.
	Assert.assertEquals(expectedProjectId, commandProjectId);

	Assert.assertNotNull(taskPagedList);

	Task[] tasks = taskPagedList.getTasks();
	Assert.assertNotNull(tasks);

	List<Task> taskList = Arrays.asList(tasks);

	sortTasks(taskList);

	/* Test order after sorting by property */

	Assert.assertEquals("add term", taskList.get(0).getName());
	Assert.assertEquals("save changes", taskList.get(2).getName());
	Assert.assertEquals("undo changes", taskList.get(4).getName());
	Assert.assertEquals("send to translation", taskList.get(5).getName());
	Assert.assertEquals("change term status", taskList.get(6).getName());
	Assert.assertEquals("disable term", taskList.get(14).getName());
	Assert.assertEquals("import tbx", taskList.get(15).getName());
	Assert.assertEquals("export tbx", taskList.get(16).getName());

    }

    @Before
    public void setUp() throws Exception {
    }

    private static void sortTasks(List<Task> tasks) {
	Collections.sort(tasks, new Comparator<Task>() {
	    @Override
	    public int compare(Task task1, Task task2) {
		if (task1 != null && task2 != null) {
		    return task1.getPriority().getValue() < task2.getPriority().getValue() ? -1 : 1;
		}
		return 0;
	    }
	});
    }
}
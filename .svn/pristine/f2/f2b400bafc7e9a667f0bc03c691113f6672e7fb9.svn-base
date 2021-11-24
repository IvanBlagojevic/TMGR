package org.gs4tr.termmanager.service.mocking;

import static org.gs4tr.termmanager.persistence.solr.query.AbstractPageRequest.DEFAULT_SIZE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anySetOf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.command.UserProjectSearchRequest;
import org.gs4tr.termmanager.model.view.ProjectDetailView;
import org.gs4tr.termmanager.model.view.ProjectReport;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.ProjectDetailService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class ProjectDetailServiceTest extends AbstractServiceTest {

    private static final String ERROR_MESSAGE = "Invalid project ID.";

    private static final long EXPECTED_ZERO = 0l;

    private static final String ILLEGAL_ARGUMENT_MESSAGE = "Project ids must not be empty";

    private static final String NIKON = "Nikon";

    private static final String NIKON_SHORT_CODE = "NIK000001";

    private static final String SKYPE = "Skype";

    private static final String SKYPE_SHORT_CODE = "SKY000001";

    @Captor
    private ArgumentCaptor<List<Long>> _captor;

    private PagedListInfo _pagedListInfo;

    @Autowired
    @Mock
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    @InjectMocks
    private ProjectDetailService _projectDetailService;

    @Autowired
    @Mock
    private ProjectService _projectService;

    private UserProjectSearchRequest _searchRequest;

    public void assertParamsEqualExpected(long expected, long... params) {
	Arrays.stream(params).forEach(param -> assertEquals(expected, param));
    }

    /*
     * TERII-5678 Generate string button is not on the last place in home folder
     * when we search project that don't exist.
     */
    @Test
    @TestCase("projectDetailService")
    public void emptyProjectDetailListSortTasksTest() {

	/* Related to Skype project */
	Map<Long, Long> termEntriesCount = new HashMap<Long, Long>();
	termEntriesCount.put(1L, 11L);

	Set<String> contextPolicies = TmUserProfile.getCurrentUserProfile().getContextPolicies(1L);

	/* add send connection string (context) policy to user */
	List<String> sendConnectionPolicy = getModelObject("sendConnectionPolicy", List.class);
	contextPolicies.addAll(sendConnectionPolicy);

	/* use doReturn() for stubbing on the spy object */
	doReturn(termEntriesCount).when(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));

	when(getProjectDetailDAO().searchProjectDetails(any(), any())).thenReturn(new ArrayList<>());

	TaskPagedList<ProjectDetailView> taskPagedList = getProjectDetailService().search(_searchRequest,
		_pagedListInfo);
	assertNotNull(taskPagedList);

	Task[] tasks = taskPagedList.getTasks();
	Assert.assertNotNull(tasks);

	List<Task> taskList = Arrays.asList(tasks);

	sortTasks(taskList);

	/* Task with name="send connection string" must be on last place */
	Task task = taskList.get(taskList.size() - 1);

	Assert.assertEquals(task.getName(), "send connection string");
    }

    @Test
    @TestCase("projectDetailService")
    public void getAllProjectsReportgroupByLanguagesTest() throws Exception {
	boolean groupByLanguages = true;
	@SuppressWarnings("unchecked")
	List<ProjectReport> reportList = getModelObject("reportList", List.class);

	when(getProjectDetailDAO().getAllProjectsReport(anyBoolean(), anyBoolean(), anySetOf(Long.class),
		anySetOf(String.class))).thenReturn(reportList);

	List<ProjectReport> result = getProjectDetailService().getAllProjectsReport(groupByLanguages);

	verify(getProjectDetailDAO()).getAllProjectsReport(anyBoolean(), anyBoolean(), anySetOf(Long.class),
		anySetOf(String.class));

	assertNotNull(result);
	assertEquals(reportList, result);
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

    // Test case: user does not have terminology on Meddra project yet.
    @Test
    @SuppressWarnings({ "unchecked" })
    @TestCase("projectDetailService")
    public void projectDetailServiceSearchTest_01() throws Exception {

	Map<Long, Long> emptyEntriesCount = new HashMap<Long, Long>();
	emptyEntriesCount.put(1L, 0L);

	List<ProjectDetail> projectDetails = getModelObject("meddraProjectDetails", List.class);

	// use doReturn() for stubbing on the spy object
	doReturn(emptyEntriesCount).when(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));

	when(getProjectDetailDAO().searchProjectDetails(any(), any())).thenReturn(projectDetails);

	TaskPagedList<ProjectDetailView> taskPagedList = getProjectDetailService().search(_searchRequest,
		_pagedListInfo);

	// times(1) is the default and can be omitted
	verify(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));
	verify(getProjectDetailDAO()).searchProjectDetails(_searchRequest, _pagedListInfo);

	// First perform basic validation and then validate every single count
	ProjectDetailView[] elements = validateTaskPageList(taskPagedList, 1);
	for (ProjectDetailView projectDetailView : elements) {
	    assertProjectDetailViewEmpty(projectDetailView);
	}
    }

    // Test case: User has one project and two term entries with [en-US, de-DE]
    // languages. One term entry is in submission.
    @Test
    @SuppressWarnings({ "unchecked" })
    @TestCase("projectDetailService")
    public void projectDetailServiceSearchTest_02() throws Exception {

	// Related to Skype project
	Map<Long, Long> termEntriesCount = new HashMap<Long, Long>();
	termEntriesCount.put(1L, 11L);

	List<ProjectDetail> projectDetails = getModelObject("skypeProjectDetails", List.class);

	// use doReturn() for stubbing on the spy object
	doReturn(termEntriesCount).when(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));

	when(getProjectDetailDAO().searchProjectDetails(any(), any())).thenReturn(projectDetails);

	TaskPagedList<ProjectDetailView> taskPagedList = getProjectDetailService().search(_searchRequest,
		_pagedListInfo);

	// times(1) is the default and can be omitted
	verify(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));
	verify(getProjectDetailDAO()).searchProjectDetails(_searchRequest, _pagedListInfo);
	verify(getProjectService(), never()).findProjectByIds(anyList());

	// First perform basic validation and then validate every single count
	ProjectDetailView[] elements = validateTaskPageList(taskPagedList, 1);

	for (ProjectDetailView projectDetailView : elements) {
	    validateNotNull(projectDetailView, projectDetailView.getDateModified(), projectDetailView.getName());
	    assertParamsEqualExpected(12l, projectDetailView.getApprovedTermCount());
	    assertParamsEqualExpected(30l, projectDetailView.getTermCount());
	    assertParamsEqualExpected(8l, projectDetailView.getForbiddenTermCount());
	    assertParamsEqualExpected(10l, projectDetailView.getTermInSubmissionCount());
	    assertParamsEqualExpected(EXPECTED_ZERO, projectDetailView.getCompletedSubmissionCount());
	    assertParamsEqualExpected(5, projectDetailView.getActiveSubmissionCount());
	    assertParamsEqualExpected(2l, projectDetailView.getLanguageCount());
	    assertParamsEqualExpected(11l, projectDetailView.getTermEntryCount());
	    assertEquals(projectDetailView.getShortCode(), SKYPE_SHORT_CODE);
	    assertEquals(projectDetailView.getName(), SKYPE);
	    assertEquals(projectDetailView.getProjectId().longValue(), 1l);
	}
    }

    // Test case: User has two projects. One project is empty and has 2
    // languages. Second project has terminology and 4 languages.
    @Test
    @SuppressWarnings("unchecked")
    @TestCase("projectDetailService")
    public void projectDetailServiceSearchTest_03() throws Exception {

	Map<Long, Long> termEntriesCount = new HashMap<Long, Long>();
	termEntriesCount.put(1L, 0L);
	termEntriesCount.put(2L, 10L);

	List<ProjectDetail> projectDetails = getModelObject("projectDetails", List.class);
	UserProjectSearchRequest searchRequest = getModelObject("searchRequest1", UserProjectSearchRequest.class);

	// use doReturn() for stubbing on the spy object
	doReturn(termEntriesCount).when(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));

	when(getProjectDetailDAO().searchProjectDetails(any(), any())).thenReturn(projectDetails);

	TaskPagedList<ProjectDetailView> taskPagedList = getProjectDetailService().search(searchRequest,
		_pagedListInfo);

	// times(1) is the default and can be omitted
	verify(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));
	verify(getProjectDetailDAO()).searchProjectDetails(searchRequest, _pagedListInfo);

	// First perform basic validation and then validate every single count
	ProjectDetailView[] elements = validateTaskPageList(taskPagedList, 2);

	for (ProjectDetailView projectDetailView : elements) {
	    switch (projectDetailView.getProjectId().intValue()) {
	    case 1:
		assertProjectDetailViewEmpty(projectDetailView);
		break;
	    case 2:
		validateNotNull(projectDetailView, projectDetailView.getDateModified(), projectDetailView.getName());
		assertParamsEqualExpected(5l, projectDetailView.getForbiddenTermCount(),
			projectDetailView.getTermInSubmissionCount());
		assertParamsEqualExpected(3l, projectDetailView.getActiveSubmissionCount());
		assertParamsEqualExpected(2l, projectDetailView.getLanguageCount());
		assertParamsEqualExpected(10l, projectDetailView.getTermEntryCount());
		assertParamsEqualExpected(5l, projectDetailView.getApprovedTermCount());
		assertEquals(projectDetailView.getShortCode(), NIKON_SHORT_CODE);
		assertEquals(projectDetailView.getName(), NIKON);
		assertEquals(projectDetailView.getProjectId().longValue(), 2l);
		break;
	    default:
		throw new IllegalArgumentException(ERROR_MESSAGE);
	    }
	}
    }

    // TERII-3932: TPT4 | Application error occurs in an attempt to log in as
    // 'termreserve' user
    @Test
    @SuppressWarnings("unchecked")
    @TestCase("projectDetailService")
    public void projectDetailServiceSearchTest_04() throws Exception {

	Map<Long, Long> termEntriesCount = new HashMap<>();
	termEntriesCount.put(1L, 11L);

	List<ProjectDetail> projectDetails = getModelObject("skypeProjectDetails", List.class);

	// We need to set user without project user detail, so i will reuse
	// current user
	_searchRequest.setUser(TmUserProfile.getCurrentUserProfile());

	doReturn(termEntriesCount).when(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));

	when(getProjectDetailDAO().searchProjectDetails(any(), any())).thenReturn(projectDetails);

	TaskPagedList<ProjectDetailView> taskPagedList = getProjectDetailService().search(_searchRequest,
		_pagedListInfo);

	verify(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));
	verify(getProjectDetailDAO()).searchProjectDetails(_searchRequest, _pagedListInfo);
	verify(getProjectService(), never()).findProjectByIds(anyList());

	ProjectDetailView[] elements = validateTaskPageList(taskPagedList, 1);

	for (ProjectDetailView projectDetailView : elements) {
	    assertParamsEqualExpected(EXPECTED_ZERO, projectDetailView.getCompletedSubmissionCount());
	    assertParamsEqualExpected(EXPECTED_ZERO, projectDetailView.getActiveSubmissionCount());
	}
    }

    @Test
    @TestCase("projectDetailService")
    public void projectDetailSortTasksTest() {

	// Related to Skype project
	Map<Long, Long> termEntriesCount = new HashMap<Long, Long>();
	termEntriesCount.put(1L, 11L);

	Set<String> contextPolicies = TmUserProfile.getCurrentUserProfile().getContextPolicies(1L);

	/* add send connection string (context) policy to user */
	List<String> sendConnectionPolicy = getModelObject("sendConnectionPolicy", List.class);
	contextPolicies.addAll(sendConnectionPolicy);

	List<ProjectDetail> projectDetails = getModelObject("skypeProjectDetails", List.class);

	// use doReturn() for stubbing on the spy object
	doReturn(termEntriesCount).when(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));

	when(getProjectDetailDAO().searchProjectDetails(any(), any())).thenReturn(projectDetails);

	TaskPagedList<ProjectDetailView> taskPagedList = getProjectDetailService().search(_searchRequest,
		_pagedListInfo);
	assertNotNull(taskPagedList);

	Task[] tasks = taskPagedList.getTasks();
	Assert.assertNotNull(tasks);

	List<Task> taskList = Arrays.asList(tasks);

	sortTasks(taskList);

	// Task with name="send connection string" must be on last place
	Task task = taskList.get(taskList.size() - 1);

	Assert.assertEquals(task.getName(), "send connection string");
    }

    // TERII-3920: Filter by non existing project causes application error
    @Test
    @TestCase("projectDetailService")
    public void searchByNonExistingProjectNameTest() throws Exception {

	List<ProjectDetail> emptyList = new ArrayList<ProjectDetail>();

	when(getProjectDetailDAO().searchProjectDetails(any(), any())).thenReturn(emptyList);

	doThrow(new IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE)).when(getTermEntryService())
		.getNumberOfTermEntries(any(TmgrSearchFilter.class));

	TaskPagedList<ProjectDetailView> taskPagedList = getProjectDetailService().search(_searchRequest,
		_pagedListInfo);

	verify(getProjectDetailDAO()).searchProjectDetails(_searchRequest, _pagedListInfo);

	ProjectDetailView[] page = validateTaskPageList(taskPagedList, 0);

	// Expected result: Search works and there should be no project on grid.
	assertTrue(ArrayUtils.isEmpty(page));
    }

    public void setProjectDetailDAO(ProjectDetailDAO projectDetailDAO) {
	_projectDetailDAO = projectDetailDAO;
    }

    @Before
    public void setup() {
	MockitoAnnotations.initMocks(this);

	_pagedListInfo = getModelObject("pagedListInfo", PagedListInfo.class);
	_searchRequest = getModelObject("searchRequest", UserProjectSearchRequest.class);
    }

    @Test
    @TestCase("projectDetailService")
    public void userDoesNotHaveSendConnectionPolicyTest() {

	// Related to Skype project
	Map<Long, Long> termEntriesCount = new HashMap<Long, Long>();
	termEntriesCount.put(1L, 11L);

	List<ProjectDetail> projectDetails = getModelObject("skypeProjectDetails", List.class);

	// use doReturn() for stubbing on the spy object
	doReturn(termEntriesCount).when(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));

	when(getProjectDetailDAO().searchProjectDetails(any(), any())).thenReturn(projectDetails);

	TaskPagedList<ProjectDetailView> taskPagedList = getProjectDetailService().search(_searchRequest,
		_pagedListInfo);
	assertNotNull(taskPagedList);

	List<Task> taskList = Arrays.asList(taskPagedList.getTasks());
	Assert.assertNotNull(taskList);

	List<String> taskNames = new ArrayList<>();

	taskList.forEach(task -> taskNames.add(task.getName()));

	Assert.assertTrue(!taskNames.contains("send connection string"));

    }

    public void validateNotNull(Object... params) {
	Arrays.stream(params).forEach(Assert::assertNotNull);
    }

    private void assertProjectDetailViewEmpty(ProjectDetailView projectDetailView) {
	validateNotNull(projectDetailView, projectDetailView.getDateModified(), projectDetailView.getName());

	assertParamsEqualExpected(EXPECTED_ZERO, projectDetailView.getApprovedTermCount(),
		projectDetailView.getForbiddenTermCount(), projectDetailView.getActiveSubmissionCount(),
		projectDetailView.getCompletedSubmissionCount(), projectDetailView.getTermInSubmissionCount(),
		projectDetailView.getTermCount(), projectDetailView.getTermEntryCount());

	assertParamsEqualExpected(2, projectDetailView.getLanguageCount());

	assertEquals(projectDetailView.getName(), "Meddra");
	assertEquals(projectDetailView.getShortCode(), "MEDD000001");
	assertEquals(projectDetailView.getProjectId().longValue(), 1l);
    }

    private ProjectDetailService getProjectDetailService() {
	return _projectDetailService;
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

    private ProjectDetailView[] validateTaskPageList(TaskPagedList<ProjectDetailView> taskPagedList,
	    int expectedCount) {

	validateNotNull(taskPagedList, taskPagedList.getPagedListInfo(), taskPagedList.getElements());

	assertEquals(expectedCount, taskPagedList.getTotalCount().intValue());

	PagedListInfo pagedListInfo = taskPagedList.getPagedListInfo();

	ProjectDetailView[] elements = taskPagedList.getElements();

	assertEquals(DEFAULT_SIZE, pagedListInfo.getSize().intValue());

	assertEquals(expectedCount, elements.length);

	return elements;
    }
}

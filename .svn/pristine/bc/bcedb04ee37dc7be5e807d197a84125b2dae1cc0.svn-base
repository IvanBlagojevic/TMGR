package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SortDirection;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.search.ProjectSearchRequest;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

@TestSuite("projectSearchController")
public class ProjectSearchControllerTest extends AbstractSearchGridControllerTest {

    private static final String PROJECT_FOLDER_METADATA_JSON = "projectFolderMetadata.json";

    private static final String URL = UrlConstants.PROJECT_SEARCH;

    @ClientBean
    private ProjectService _projectService;

    public ProjectService getProjectService() {
	return _projectService;
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("projectSearch")
    @TestUser(roleName = RoleNameEnum.ADMIN)
    public void projectSearchTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	TaskPagedList<TmProject> taskPagedList = getModelObject("taskPagedList", TaskPagedList.class);

	when(getProjectService().search(any(), any())).thenReturn(taskPagedList);

	String inputJson = getJsonData("projectSearch.json");

	Map<String, String> parameters = new HashMap<>();
	parameters.put(JSON_DATA_KEY, inputJson);

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getProjectService()).search(any(ProjectSearchRequest.class), any(PagedListInfo.class));

	assertGridConfigContent(new JSONValidator(response.getContent()));
    }

    @Override
    protected void assertAllProjectsSearch(JSONValidator responseContent) {
	responseContent.assertProperty("allProjectsSearch", "false");
    }

    @Override
    protected void assertDtoEntities(JSONValidator dtoEntities) {
	final String encryptGenericId = IdEncrypter.encryptGenericId(1);
	JSONValidator project = dtoEntities.getObjectFromArray("ticket", encryptGenericId);
	project.assertProperty("organizationName", "Emisia").assertPropertyNull("readOnly")
		.assertPropertyNull("languages").getObject("projectInfo").assertProperty("name", "Skype")
		.assertProperty("shortCode", "SKY000001").assertProperty("enabled", String.valueOf(true));
    }

    @Override
    protected void assertDtoUnionTasks(JSONValidator unionTasks) {
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "add project"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "edit project"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "check project name uniqueness"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "generate short code"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "get project user languages"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "assign project attributes"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "assign project users"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "send connection string"));
    }

    @Override
    protected void assertGridConfig(JSONValidator gridConfig) {

	JSONValidator projectName = gridConfig.getObjectFromArray("dataIndex", "projectInfo.name");
	projectName.assertProperty("sortProperty", "entity.projectInfo.name").assertProperty("header", "name")
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "334");

	JSONValidator projectEnabled = gridConfig.getObjectFromArray("dataIndex", "projectInfo.enabled");
	projectEnabled.assertProperty("header", "enabled").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "330")
		.assertProperty("sortProperty", "organization.organizationInfo.name");

	JSONValidator organizationName = gridConfig.getObjectFromArray("dataIndex", "organizationName");
	organizationName.assertProperty("header", "organization").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "100")
		.assertProperty("sortProperty", "organization.organizationInfo.name");

	JSONValidator defaultSortProperty = gridConfig.getObjectFromArray("dataIndex", "defaultSortProperty");
	defaultSortProperty.assertProperty("header", "defaultSortProperty").assertProperty("systemHidden", "true")
		.assertProperty("hidden", "true").assertProperty("sortable", "false").assertProperty("width", "70")
		.assertProperty("sortProperty", "entity.projectInfo.name");
    }

    @Override
    protected void assertGridConfigFromMetadata(JSONValidator responseContent) {
	responseContent.assertProperty("gridConfigFromMetadata", "true");
    }

    @Override
    protected void assertGridContentInfo(JSONValidator gridContentInfo) {
	gridContentInfo.assertProperty("hasPrevious", String.valueOf(false));
	gridContentInfo.assertProperty("hasNext", String.valueOf(false));
	gridContentInfo.assertProperty("totalCount", String.valueOf(1));
	gridContentInfo.assertProperty("totalPageCount", String.valueOf(1));

	gridContentInfo.assertPropertyNull("totalCountPerSource");
	gridContentInfo.assertPropertyNull("totalCountPerTarget");

	JSONValidator pagedList = gridContentInfo.getObject("pagedListInfo");

	pagedList.assertProperty("ascending", String.valueOf(false));
	pagedList.assertProperty("index", String.valueOf(0));
	pagedList.assertProperty("indexesSize", String.valueOf(5));
	pagedList.assertProperty("size", String.valueOf(50));
	pagedList.assertProperty("sortDirection", SortDirection.DESCENDING.name());
	pagedList.assertProperty("sortProperty", "entity.projectInfo.name");
    }

    @Override
    protected void assertReaderColumns(JSONValidator readerColumns) {
	assertNotNull(readerColumns.getObjectFromArray("name", "projectInfo.name"));
	assertNotNull(readerColumns.getObjectFromArray("name", "organizationName"));
	assertNotNull(readerColumns.getObjectFromArray("name", "projectInfo.enabled"));
	assertNotNull(readerColumns.getObjectFromArray("name", "defaultSortProperty"));
    }

    @Override
    protected String getMetadataFolderPath() {
	return PROJECT_FOLDER_METADATA_JSON;
    }

    @Override
    protected String getMetadataKey() {
	return ItemFolderEnum.PROJECTS.name().toLowerCase();
    }
}

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
import org.gs4tr.foundation.modules.entities.model.search.OrganizationSearchRequest;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

@TestSuite("organizationSearchController")
public class OrganizationSearchControllerTest extends AbstractSearchGridControllerTest {

    private static final String ORGANIZATION_FOLDER_METADATA_JSON = "organizationFolderMetadata.json";

    private static final String URL = UrlConstants.ORGANIZATION_SEARCH;

    @ClientBean
    private OrganizationService _organizationService;

    public OrganizationService getOrganizationService() {
	return _organizationService;
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("organizationSearch")
    @TestUser(roleName = RoleNameEnum.ADMIN)
    public void organizationSearchTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	TaskPagedList<TmOrganization> taskPagedList = getModelObject("taskPagedList", TaskPagedList.class);

	when(getOrganizationService().search(any(), any())).thenReturn(taskPagedList);

	String inputJson = getJsonData("organizationSearch.json");

	Map<String, String> parameters = new HashMap<>();
	parameters.put(JSON_DATA_KEY, inputJson);

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getOrganizationService()).search(any(OrganizationSearchRequest.class), any(PagedListInfo.class));

	assertGridConfigContent(new JSONValidator(response.getContent()));
    }

    @Override
    protected void assertAllProjectsSearch(JSONValidator responseContent) {
	responseContent.assertProperty("allProjectsSearch", "false");
    }

    @Override
    protected void assertDtoEntities(JSONValidator dtoEntities) {

	JSONValidator organization = dtoEntities.getObjectFromArray("ticket", IdEncrypter.encryptGenericId(1));

	organization.assertPropertyNull("parentOrganization");

	JSONValidator organizationInfo = organization.getObject("organizationInfo");
	organizationInfo.assertProperty("theme", "THEME").assertProperty("domain", "DOMAIN")
		.assertProperty("name", "Emisia").assertProperty("enabled", "true");
    }

    @Override
    protected void assertDtoUnionTasks(JSONValidator unionTasks) {
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "enable organization"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "add organization"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "edit organization"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "get organization users"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "assign organization project"));
    }

    @Override
    protected void assertGridConfig(JSONValidator gridConfig) {

	JSONValidator organizationName = gridConfig.getObjectFromArray("dataIndex", "organizationInfo.name");
	organizationName.assertProperty("sortProperty", "entity.organizationInfo.name").assertProperty("header", "name")
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "277");

	JSONValidator organizationEnabled = gridConfig.getObjectFromArray("dataIndex", "organizationInfo.enabled");
	organizationEnabled.assertProperty("header", "enabled").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "50")
		.assertProperty("sortProperty", "entity.organizationInfo.enabled");

	JSONValidator defaultSortProperty = gridConfig.getObjectFromArray("dataIndex", "defaultSortProperty");
	defaultSortProperty.assertProperty("header", "defaultSortProperty").assertProperty("systemHidden", "true")
		.assertProperty("hidden", "true").assertProperty("sortable", "false").assertProperty("width", "70")
		.assertProperty("sortProperty", "entity.organizationInfo.name");

	JSONValidator parentOrganization = gridConfig.getObjectFromArray("dataIndex",
		"parentOrganization.organizationInfo.name");
	parentOrganization.assertProperty("header", "parentOrganization").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "274")
		.assertProperty("sortProperty", "parentOrganization.organizationInfo.name");
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

	pagedList.assertProperty("index", String.valueOf(0));
	pagedList.assertProperty("indexesSize", String.valueOf(5));
	pagedList.assertProperty("size", String.valueOf(50));
	pagedList.assertProperty("sortDirection", SortDirection.ASCENDING.name());
	pagedList.assertProperty("sortProperty", "entity.organizationInfo.name");
    }

    @Override
    protected void assertReaderColumns(JSONValidator readerColumns) {
	assertNotNull(readerColumns.getObjectFromArray("name", "organizationInfo.name"));
	assertNotNull(readerColumns.getObjectFromArray("name", "parentOrganization.organizationInfo.name"));
	assertNotNull(readerColumns.getObjectFromArray("name", "organizationInfo.enabled"));
	assertNotNull(readerColumns.getObjectFromArray("name", "defaultSortProperty"));
    }

    @Override
    protected String getMetadataFolderPath() {
	return ORGANIZATION_FOLDER_METADATA_JSON;
    }

    @Override
    protected String getMetadataKey() {
	return ItemFolderEnum.ORGANIZATIONS.name().toLowerCase();
    }
}

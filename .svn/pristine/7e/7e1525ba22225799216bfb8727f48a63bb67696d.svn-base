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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.search.UserProfileSearchRequest;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

@TestSuite("userProfileSearchController")
public class UserProfileSearchControllerTest extends AbstractSearchGridControllerTest {

    private static final String URL = UrlConstants.USER_PROFILE_SEARCH;

    private static final String USER_FOLDER_METADATA_JSON = "userFolderMetadata.json";

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("userProfileSearch")
    @TestUser(roleName = RoleNameEnum.ADMIN)
    public void userProfileSearchTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	TaskPagedList<TmUserProfile> taskPagedList = getModelObject("taskPagedList", TaskPagedList.class);

	UserProfileService userProfileService = getUserProfileService();
	when(userProfileService.search(any(UserProfileSearchRequest.class), any(PagedListInfo.class)))
		.thenReturn(taskPagedList);

	String inputJson = getJsonData("userProfileSearch.json");

	Map<String, String> parameters = new HashMap<>();
	parameters.put(JSON_DATA_KEY, inputJson);

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(userProfileService).search(any(UserProfileSearchRequest.class), any(PagedListInfo.class));

	assertGridConfigContent(new JSONValidator(response.getContent()));
    }

    @Override
    protected void assertAllProjectsSearch(JSONValidator responseContent) {
	responseContent.assertProperty("allProjectsSearch", "false");
    }

    @Override
    protected void assertDtoEntities(JSONValidator dtoEntities) {

	JSONValidator admin_user = dtoEntities.getObjectFromArray("ticket", IdEncrypter.encryptGenericId(1));

	admin_user.assertProperty("organizationName", "Emisia").assertProperty("generic", "false")
		.assertPropertyNull("availableTasks").assertPropertyNull("tasks").assertPropertyNull("systemRoles");

	JSONValidator adminUserInfo = admin_user.getObject("userInfo");
	adminUserInfo.assertProperty("accountNonExpired", "true").assertProperty("claimMultipleJobTasks", "false")
		.assertProperty("isSamlUser", "false").assertProperty("accountLocked", "false")
		.assertProperty("autoClaimMultipleTasks", "false").assertProperty("credentialsNonExpired", "true")
		.assertProperty("userName", "admin").assertProperty("userType", "ORGANIZATION")
		.assertProperty("unsuccessfulAuthCount", "0").assertProperty("enabled", "true")
		.assertPropertyNull("timeZone").assertPropertyNull("emailAddress")
		.assertPropertyNull("emailNotification").assertPropertyNull("firstName").assertPropertyNull("lastName");

	JSONValidator super_user = dtoEntities.getObjectFromArray("ticket", IdEncrypter.encryptGenericId(2));

	super_user.assertPropertyNull("organizationName").assertProperty("generic", "false")
		.assertPropertyNull("availableTasks").assertPropertyNull("tasks").assertPropertyNull("systemRoles");

	JSONValidator superUserInfo = super_user.getObject("userInfo");
	superUserInfo.assertProperty("accountNonExpired", "true").assertProperty("claimMultipleJobTasks", "false")
		.assertProperty("isSamlUser", "false").assertProperty("accountLocked", "false")
		.assertProperty("autoClaimMultipleTasks", "false").assertProperty("credentialsNonExpired", "true")
		.assertProperty("userName", "dbrasko").assertProperty("userType", "ORGANIZATION")
		.assertProperty("unsuccessfulAuthCount", "0").assertProperty("enabled", "true")
		.assertProperty("timeZone", "America/Argentina/Buenos_Aires")
		.assertProperty("emailAddress", "dbrasko@emisia.net").assertProperty("emailNotification", "true")
		.assertProperty("firstName", "Donnie").assertProperty("lastName", "Brasko");
    }

    @Override
    protected void assertDtoUnionTasks(JSONValidator unionTasks) {
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "add user"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "edit user"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "assign role"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "enable user"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "assign organization"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "assign languages"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "assign project"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "change password"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "clear user metadata"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "check user uniqueness"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "unlock user account"));
    }

    @Override
    protected void assertGridConfig(JSONValidator gridConfig) {

	JSONValidator username = gridConfig.getObjectFromArray("dataIndex", "userInfo.userName");
	username.assertProperty("sortProperty", "entity.userInfo.userName").assertProperty("header", "username")
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "243");

	JSONValidator firstname = gridConfig.getObjectFromArray("dataIndex", "userInfo.firstName");
	firstname.assertProperty("sortProperty", "entity.userInfo.firstName").assertProperty("header", "firstname")
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "400");

	JSONValidator surname = gridConfig.getObjectFromArray("dataIndex", "userInfo.lastName");
	surname.assertProperty("sortProperty", "entity.userInfo.lastName").assertProperty("header", "surname")
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "200");

	JSONValidator enable = gridConfig.getObjectFromArray("dataIndex", "userInfo.enabled");
	enable.assertProperty("sortProperty", "entity.userInfo.enabled").assertProperty("header", "enabled")
		.assertProperty("systemHidden", "false").assertProperty("hidden", "false")
		.assertProperty("sortable", "true").assertProperty("width", "50");

	JSONValidator lastLogin = gridConfig.getObjectFromArray("dataIndex", "userInfo.dateLastLogin");
	lastLogin.assertProperty("header", "lastLogin").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "200")
		.assertProperty("sortProperty", "entity.userInfo.dateLastLogin");

	JSONValidator locked = gridConfig.getObjectFromArray("dataIndex", "userInfo.accountLocked");
	locked.assertProperty("header", "locked").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "false").assertProperty("width", "70")
		.assertProperty("sortProperty", StringUtils.EMPTY);

	JSONValidator organization = gridConfig.getObjectFromArray("dataIndex", "organizationName");
	organization.assertProperty("header", "organization").assertProperty("systemHidden", "false")
		.assertProperty("hidden", "false").assertProperty("sortable", "true").assertProperty("width", "100")
		.assertProperty("sortProperty", "organization.organizationInfo.name");

	JSONValidator defaultSortProperty = gridConfig.getObjectFromArray("dataIndex", "defaultSortProperty");
	defaultSortProperty.assertProperty("header", "defaultSortProperty").assertProperty("systemHidden", "true")
		.assertProperty("hidden", "true").assertProperty("sortable", "false").assertProperty("width", "70")
		.assertProperty("sortProperty", "entity.userInfo.userName");
    }

    @Override
    protected void assertGridConfigFromMetadata(JSONValidator responseContent) {
	responseContent.assertProperty("gridConfigFromMetadata", "true");
    }

    @Override
    protected void assertGridContentInfo(JSONValidator gridContentInfo) {
	gridContentInfo.assertProperty("hasPrevious", "false").assertProperty("hasNext", "false")
		.assertProperty("totalCount", "1").assertProperty("totalPageCount", "1")
		.assertPropertyNull("totalCountPerSource").assertPropertyNull("totalCountPerTarget");

	JSONValidator pagedListInfo = gridContentInfo.getObject("pagedListInfo");
	pagedListInfo.assertProperty("ascending", "true").assertProperty("index", "0")
		.assertProperty("indexesSize", "5").assertProperty("size", "50")
		.assertProperty("sortDirection", "ASCENDING")
		.assertProperty("sortProperty", "entity.userInfo.userName");
    }

    @Override
    protected void assertReaderColumns(JSONValidator readerColumns) {
	assertNotNull(readerColumns.getObjectFromArray("name", "userInfo.userName"));
	assertNotNull(readerColumns.getObjectFromArray("name", "userInfo.firstName"));
	assertNotNull(readerColumns.getObjectFromArray("name", "userInfo.lastName"));
	assertNotNull(readerColumns.getObjectFromArray("name", "userInfo.enabled"));
	assertNotNull(readerColumns.getObjectFromArray("name", "userInfo.dateLastLogin"));
	assertNotNull(readerColumns.getObjectFromArray("name", "userInfo.accountLocked"));
	assertNotNull(readerColumns.getObjectFromArray("name", "organizationName"));
	assertNotNull(readerColumns.getObjectFromArray("name", "defaultSortProperty"));
    }

    @Override
    protected String getMetadataFolderPath() {
	return USER_FOLDER_METADATA_JSON;
    }

    @Override
    protected String getMetadataKey() {
	return ItemFolderEnum.USERS.name().toLowerCase();
    }
}
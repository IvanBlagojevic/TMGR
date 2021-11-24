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
import org.gs4tr.foundation.modules.entities.model.SortDirection;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.search.RoleSearchRequest;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

@TestSuite("roleSearchController")
public class RoleSearchControllerTest extends AbstractSearchGridControllerTest {

    private static final String URL = UrlConstants.ROLE_SEARCH;

    @ClientBean
    private RoleService _roleService;

    public RoleService getRoleService() {
	return _roleService;
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("roleSearch")
    @TestUser(roleName = RoleNameEnum.ADMIN)
    public void roleSearchTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	TaskPagedList<Role> taskPagedList = getModelObject("taskPagedList", TaskPagedList.class);

	when(getRoleService().search(any(), any())).thenReturn(taskPagedList);

	String inputJson = getJsonData("roleSearch.json");

	Map<String, String> parameters = new HashMap<>();
	parameters.put(JSON_DATA_KEY, inputJson);

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getRoleService()).search(any(RoleSearchRequest.class), any(PagedListInfo.class));

	assertGridConfigContent(new JSONValidator(response.getContent()));
    }

    private void assertContextUserPolicies(JSONValidator policies) {
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_CANCEL_TRANSLATION"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_ADD_COMMENT"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERMENTRY_IMPORT"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_VIEW_TERM_HISTORY_USERS"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_PROJECT_REPORT"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_APPROVE_TERM_STATUS"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERMENTRY_FORBID_TERMENTRY"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_DISABLE_TERM"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERMENTRY_ASSIGN_ATTRIBUTES"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_COMMIT_TRANSLATION_CHANGES"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_AUTO_SAVE_TRANSLATION"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_DEMOTE_TERM_STATUS"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_UNDO_TRANSLATION_CHANGES"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_SEND_TO_TRANSLATION_REVIEW"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_READ"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_VIEW_TERM_HISTORY"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_ADD_PENDING_TERM"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERMENTRY_EXPORT"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_ADD_APPROVED_TERM"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_USER_SEND_CONNECTION"));
    }

    @Override
    protected void assertAllProjectsSearch(JSONValidator responseContent) {
	responseContent.assertProperty("allProjectsSearch", "false");
    }

    @Override
    protected void assertDtoEntities(JSONValidator dtoEntities) {

	JSONValidator admin = dtoEntities.getObjectFromArray("ticket", "admin");

	JSONValidator roleType = admin.getObject("roleType");
	roleType.assertProperty("value", String.valueOf(1));

	JSONValidator policies = admin.getObject("policies");

	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_ORGANIZATION_ADD"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_SECURITY_ROLE_VIEW"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_PROJECT_ADD"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_ORGANIZATION_ENABLEDISABLE"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_PROJECT_ENABLEDISABLE"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_SECURITY_ROLE_ADD"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_PROJECT_VIEW"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_USERPROFILE_EDIT"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_PROJECT_SEND_CONNECTION"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_USERPROFILE_VIEW"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_ORGANIZATION_VIEW"));

	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_USERPROFILE_ADD"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_SECURITY_ROLE_EDIT"));

	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_USERPROFILE_ENABLEDISABLE"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_PROJECT_EDIT"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_USERPROFILE_NON_EXPIRING_USER"));

	JSONValidator generic_user = dtoEntities.getObjectFromArray("ticket", "generic_user");

	roleType = generic_user.getObject("roleType");
	roleType.assertProperty("value", String.valueOf(0));

	policies = generic_user.getObject("policies");
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_READ"));

	JSONValidator power_user = dtoEntities.getObjectFromArray("ticket", "power_user");

	roleType = power_user.getObject("roleType");
	roleType.assertProperty("value", String.valueOf(0));

	policies = power_user.getObject("policies");
	assertContextUserPolicies(policies);

	JSONValidator super_user = dtoEntities.getObjectFromArray("ticket", "super_user");

	roleType = super_user.getObject("roleType");
	roleType.assertProperty("value", String.valueOf(0));

	policies = super_user.getObject("policies");
	assertContextUserPolicies(policies);

	JSONValidator system_power_user = dtoEntities.getObjectFromArray("ticket", "system_power_user");

	roleType = system_power_user.getObject("roleType");
	roleType.assertProperty("value", String.valueOf(1));

	policies = system_power_user.getObject("policies");

	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_PROJECT_ADD"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_PROJECT_EDIT"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_PROJECT_ENABLEDISABLE"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_VIEW_TRANSLATOR_INBOX"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_MENU_TERMS"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_USERPROFILE_EDIT"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_FOUNDATION_PROJECT_VIEW"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_PROJECT_SEND_CONNECTION"));

	JSONValidator system_translator_user = dtoEntities.getObjectFromArray("ticket", "system_translator_user");

	roleType = system_translator_user.getObject("roleType");
	roleType.assertProperty("value", String.valueOf(1));

	policies = system_translator_user.getObject("policies");
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_VIEW_TRANSLATOR_INBOX"));

	JSONValidator translator_user = dtoEntities.getObjectFromArray("ticket", "translator_user");

	roleType = translator_user.getObject("roleType");
	roleType.assertProperty("value", String.valueOf(0));

	policies = translator_user.getObject("policies");

	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_COMMIT_TRANSLATION_CHANGES"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_UNDO_TRANSLATION_CHANGES"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_ADD_COMMENT"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_VIEW_TERM_HISTORY"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_TERM_AUTO_SAVE_TRANSLATION"));
    }

    @Override
    protected void assertDtoUnionTasks(JSONValidator unionTasks) {
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "add role"));
	assertNotNull(unionTasks.getObjectFromArray(TASK_NAME, "edit role"));
    }

    @Override
    protected void assertGridConfig(JSONValidator gridConfig) {
	JSONValidator roleId = gridConfig.getObjectFromArray("dataIndex", "roleId");
	roleId.assertProperty("sortable", "false").assertProperty("hidden", "false")
		.assertProperty("systemHidden", "false").assertProperty("width", "400").assertProperty("header", "name")
		.assertProperty("sortProperty", StringUtils.EMPTY);
    }

    @Override
    protected void assertGridConfigFromMetadata(JSONValidator responseContent) {
	responseContent.assertProperty("gridConfigFromMetadata", "false");
    }

    @Override
    protected void assertGridContentInfo(JSONValidator gridContentInfo) {
	gridContentInfo.assertProperty("hasPrevious", String.valueOf(false));
	gridContentInfo.assertProperty("hasNext", String.valueOf(false));
	gridContentInfo.assertProperty("totalCount", String.valueOf(8));
	gridContentInfo.assertProperty("totalPageCount", String.valueOf(1));

	gridContentInfo.assertPropertyNull("totalCountPerSource");
	gridContentInfo.assertPropertyNull("totalCountPerTarget");

	JSONValidator pagedList = gridContentInfo.getObject("pagedListInfo");

	pagedList.assertProperty("ascending", String.valueOf(true));
	pagedList.assertProperty("index", String.valueOf(0));
	pagedList.assertProperty("indexesSize", String.valueOf(5));
	pagedList.assertProperty("size", String.valueOf(50));
	pagedList.assertProperty("sortDirection", SortDirection.ASCENDING.name());
	pagedList.assertProperty("sortProperty", "null");
    }

    @Override
    protected void assertReaderColumns(JSONValidator readerColumns) {
	assertNotNull(readerColumns.getObjectFromArray("name", "roleId"));
    }

    @Override
    protected String getMetadataFolderPath() {
	return null;
    }

    @Override
    protected String getMetadataKey() {
	return ItemFolderEnum.SECURITY.name().toLowerCase();
    }
}

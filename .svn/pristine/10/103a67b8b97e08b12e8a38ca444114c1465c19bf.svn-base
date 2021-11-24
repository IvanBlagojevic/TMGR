package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

@TestSuite("getProjectDefaultStatusController")
public class GetProjectDefaultStatusControllerTest extends AbstractMvcTest {

    private static final String PROJECT_COMBO_BOX = "projectComboBox";

    private static final Long PROJECT_ID = 1L;

    private static final String URL = "projectDefaultTermStatus.ter";

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("projectDefaultStatusProcessed")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void getProjectDefaultStatusControllerTest_1() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	List<TmProject> tmProjects = getModelObject("tmProjects", List.class);

	when(getProjectService().findProjectByIds(anyListOf(Long.class))).thenReturn(tmProjects);

	List<String> projectComboBox = getModelObject(PROJECT_COMBO_BOX, List.class);

	Map<String, String> parameters = new HashMap<String, String>();

	parameters.put(PROJECT_COMBO_BOX, OBJECT_MAPPER.writeValueAsString(projectComboBox));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getProjectService()).findProjectByIds(anyListOf(Long.class));

	validateContentWithValidator(response.getContent(), projectComboBox, ItemStatusTypeHolder.PROCESSED);

    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("projectDefaultStatusWaiting")
    @TestUser(roleName = RoleNameEnum.SYSTEM_POWER_USER)
    public void getProjectDefaultStatusControllerTest_2() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	List<TmProject> tmProjects = getModelObject("tmProjects", List.class);

	when(getProjectService().findProjectByIds(anyListOf(Long.class))).thenReturn(tmProjects);

	List<String> projectComboBox = getModelObject(PROJECT_COMBO_BOX, List.class);

	Map<String, String> parameters = new HashMap<String, String>();

	parameters.put(PROJECT_COMBO_BOX, OBJECT_MAPPER.writeValueAsString(projectComboBox));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getProjectService()).findProjectByIds(anyListOf(Long.class));

	validateContentWithValidator(response.getContent(), projectComboBox, ItemStatusTypeHolder.WAITING);
    }

    /*
     * TERII-5697 On add term on hold is offered as the first available term status
     * in multi-editor
     */
    @SuppressWarnings("unchecked")
    @Test
    @TestCase("projectDefaultStatusProcessed")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void getProjectDefaultStatusControllerTest_TERII_5697() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	List<TmProject> tmProjects = getModelObject("tmProjects", List.class);

	when(getProjectService().findProjectByIds(anyListOf(Long.class))).thenReturn(tmProjects);

	/* setting project policies by ticket condition */
	Set<String> contextPolicies = getCurrentUserProfile().getContextPolicies(PROJECT_ID);
	contextPolicies.remove("POLICY_TM_TERM_APPROVE_TERM_STATUS");
	contextPolicies.remove("POLICY_TM_TERM_ADD_APPROVED_TERM");

	List<String> projectComboBox = getModelObject(PROJECT_COMBO_BOX, List.class);

	Map<String, String> parameters = new HashMap<String, String>();

	parameters.put(PROJECT_COMBO_BOX, OBJECT_MAPPER.writeValueAsString(projectComboBox));

	Request request = createPostRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getProjectService()).findProjectByIds(anyListOf(Long.class));

	String responseContentJson = response.getContent();

	JSONValidator responseContent = new JSONValidator(responseContentJson);

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	JSONValidator projectInfos = responseContent.getObject("projectsInfos");

	/* default term status is pendingApproval(WAITING) */
	assertNotNull(projectInfos.getObjectFromArray("defaultTermStatus", ItemStatusTypeHolder.WAITING.getName()));

    }

    private void validateContentWithValidator(String responseContentJson, List<String> projectComboBox,
	    ItemStatusType type) {

	JSONValidator responseContent = new JSONValidator(responseContentJson);

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	JSONValidator projectInfos = responseContent.getObject("projectsInfos");

	assertNotNull(projectInfos.getObjectFromArray("defaultTermStatus", type.getName()));
	assertNotNull(projectInfos.getObjectFromArray("projectTicket", projectComboBox.get(0)));

	assertNotNull(projectInfos.getObjectFromArray("deleteAvailable", String.valueOf(true)));
	assertNotNull(projectInfos.getObjectFromArray("addEditApproved", String.valueOf(true)));
	assertNotNull(projectInfos.getObjectFromArray("addEditPending", String.valueOf(true)));
	assertNotNull(projectInfos.getObjectFromArray("addEditBlacklist", String.valueOf(true)));
	assertNotNull(projectInfos.getObjectFromArray("addEditOnHold", String.valueOf(true)));
	assertNotNull(projectInfos.getObjectFromArray("addEditAttributes", String.valueOf(true)));

	JSONValidator actionsAvailable = projectInfos.getObjectFromArray("actionsAvailable", null);

	actionsAvailable.assertProperty("demote", String.valueOf(true)).assertProperty("approve", String.valueOf(true))
		.assertProperty("onHold", String.valueOf(true)).assertProperty("blacklist", String.valueOf(true))
		.assertObjectFinish();
    }

}

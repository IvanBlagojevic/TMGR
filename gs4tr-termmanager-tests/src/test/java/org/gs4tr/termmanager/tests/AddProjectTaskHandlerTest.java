package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.ProjectDetailService;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.ProjectCommand;
import org.gs4tr.termmanager.service.utils.IdEncrypterUtils;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("add_project")
public class AddProjectTaskHandlerTest extends AbstractSpringServiceTests {

    @Autowired
    private ProjectDetailService _projectDetailService;

    @Autowired
    private RoleService _roleService;

    public ProjectDetailService getProjectDetailService() {
	return _projectDetailService;
    }

    public RoleService getRoleService() {
	return _roleService;
    }

    @Test
    @TestCase("get_task_infos")
    public void testAddProjectGet() throws Exception {
	String taskName = "add project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	TaskModel[] taskInfos = taskHandler.getTaskInfos(null, taskName, null);

	String result = JsonUtils.writeValueAsString(taskInfos);

	assertJSONResponse(result, "addProjectValidation.json");
    }

    @Test
    @TestCase("process_tasks")
    public void testAddProjectPost() {
	String taskName = "add project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	String shortCode = RandomStringUtils.randomAlphanumeric(10);

	Object command = getTaskHandlerCommand(taskHandler, "addProject.json",
		new String[] { "$shortCode", shortCode });

	TaskResponse response = taskHandler.processTasks(null, null, command, null);

	assertNotNull(response);

	String projectTicket = response.getResponseTicket().getTicketId();

	TmProject project = getProjectService().findById(IdEncrypterUtils.decryptGenericId(projectTicket));
	assertNotNull(project);
	Assert.assertEquals("test1", project.getProjectInfo().getName());

	// getProjectDetailService().search(arg0, arg1)

	TmUserProfile userProfile = getUserProfileService().findUserProfileByUsername("reader2");

	assertNotNull(userProfile);
	assertEquals(userProfile.getGeneric(), Boolean.TRUE);

	Set<Role> systemRoles = userProfile.getSystemRoles();

	assertTrue(CollectionUtils.isNotEmpty(systemRoles));
	assertEquals(systemRoles.size(), 1);
	assertEquals(systemRoles.iterator().next().getRoleId(), ServiceUtils.SYSTEM_GENERIC_USER_ROLE_NAME);

	List<Role> userProjectRoles = getRoleService().getUserProjectRoles(userProfile.getUserProfileId(),
		project.getProjectId());

	Assert.assertEquals(1, userProjectRoles.size());

    }

    /*
     * TERII-4916 Server - Edit Project dialog | User should be able to configure
     * sync statuses
     */

    @Test
    @TestCase("process_tasks")
    public void testAddProjectWithDisabledSync() {
	String taskName = "add project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	String shortCode = RandomStringUtils.randomAlphanumeric(10);

	ProjectCommand command = (ProjectCommand) getTaskHandlerCommand(taskHandler, "addProject.json",
		new String[] { "$shortCode", shortCode });

	command.setSharePendingTerms(Boolean.FALSE);

	TaskResponse response = taskHandler.processTasks(null, null, command, null);

	assertNotNull(response);

	String projectTicket = response.getResponseTicket().getTicketId();

	TmProject project = getProjectService().findById(IdEncrypterUtils.decryptGenericId(projectTicket));
	assertNotNull(project);

	/* Check if project has disabled Sync */
	Assert.assertFalse(project.getSharePendingTerms());
    }

    /*
     * TERII-4916 Server - Edit Project dialog | User should be able to configure
     * sync statuses
     */

    @Test
    @TestCase("process_tasks")
    public void testAddProjectWithEnabledSync() {
	String taskName = "add project";
	ManualTaskHandler taskHandler = getHandler(taskName);

	String shortCode = RandomStringUtils.randomAlphanumeric(10);

	ProjectCommand command = (ProjectCommand) getTaskHandlerCommand(taskHandler, "addProject.json",
		new String[] { "$shortCode", shortCode });

	command.setSharePendingTerms(Boolean.TRUE);

	TaskResponse response = taskHandler.processTasks(null, null, command, null);

	assertNotNull(response);

	String projectTicket = response.getResponseTicket().getTicketId();

	TmProject project = getProjectService().findById(IdEncrypterUtils.decryptGenericId(projectTicket));
	assertNotNull(project);

	/* Check if project has enabled Sync */
	assertTrue(project.getSharePendingTerms());
    }
}

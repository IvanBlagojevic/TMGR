package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.termmanager.model.TaskModel;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSearchTests extends AbstractSpringServiceTests {
    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private RoleService _roleService;

    @Autowired
    private UserProfileService _userProfileService;

    public OrganizationService getOrganizationService() {
	return _organizationService;
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

    public RoleService getRoleService() {
	return _roleService;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @Override
    public void setUp() throws Exception {
	super.setUp();
    }

    protected void assertAREPTaskNames(TaskPagedList<?> taskPagedList, EntityType entityType) {
	String entityName = entityType.getName().toLowerCase();

	assertTaskNames(new String[] { "add " + entityName, "edit " + entityName }, taskPagedList.getTasks());
    }

    protected void assertTaskNames(String[] refTaskNames, Task[] tasks) {
	Set<String> taskNames = new HashSet<String>();

	for (Task task : tasks) {
	    taskNames.add(task.getName());
	}

	for (String refTaskName : refTaskNames) {
	    assertTrue("Unknown taskName " + refTaskName, taskNames.contains(refTaskName));
	}
    }

    protected void validateTaskModel(TaskModel taskInfo) {
	assertNotNull(taskInfo.getTaskTicket());
    }

}

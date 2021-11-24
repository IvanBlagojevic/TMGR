package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.model.command.RoleCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoRoleCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class EditRoleTaskHandler extends AbstractManualTaskHandler {

    private static final String ALL_CONTEXT_ROLES_NAME_KEY = "allProjectRoles";

    private static final String ALL_SYSTEM_ROLES_NAME_KEY = "allSystemRoles";

    private static final String CATEGORY_NAME_KEY = "category";

    private static final String POLICIES_KEY = "policies";

    private static final String POLICY_ID_KEY = "policyId";

    private static final String ROLE_TYPE_KEY = "roleType";

    @Autowired
    private RoleService _roleService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {

	return DtoRoleCommand.class;
    }

    public RoleService getRoleService() {
	return _roleService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	String id = ((RoleCommand) command).getRoleId();

	Role role = getRoleService().findById(id);

	Set<Policy> policies = role.getPolicies();

	TaskModel newTaskModel = new TaskModel(null, new Ticket(role.getRoleId()));

	newTaskModel.addObject(ROLE_TYPE_KEY, role.getRoleType().toString());

	List<Object> policiesData = new ArrayList<Object>();

	for (Policy policy : policies) {

	    Map<String, String> data = new LinkedHashMap<String, String>();
	    data.put(CATEGORY_NAME_KEY, policy.getCategory());

	    data.put(POLICY_ID_KEY, policy.getPolicyId());

	    policiesData.add(data);

	}

	newTaskModel.addObject(POLICIES_KEY, policiesData);

	List<Role> allSystemRoles = getRoleService().findAllSystemRoles();

	List<Role> allProjectRoles = getRoleService().findAllContextRoles();

	List<String> allSystemRoleIds = new ArrayList<String>();
	for (Role allSystemRole : allSystemRoles) {
	    allSystemRoleIds.add(allSystemRole.getRoleId());
	}

	newTaskModel.addObject(ALL_SYSTEM_ROLES_NAME_KEY, allSystemRoleIds);

	List<String> allProjectRoleIds = new ArrayList<String>();
	for (Role allProjectRole : allProjectRoles) {
	    allProjectRoleIds.add(allProjectRole.getRoleId());
	}

	newTaskModel.addObject(ALL_CONTEXT_ROLES_NAME_KEY, allProjectRoleIds);

	return new TaskModel[] { newTaskModel };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {

	RoleCommand editCommand = (RoleCommand) command;

	String roleId = editCommand.getRoleId();

	String id = getRoleService().updateRole(roleId, editCommand.getRoleType(), null);

	id = getRoleService().updateRolePolicies(roleId, editCommand.getPolicies());
	return new TaskResponse(new Ticket(id));
    }
}

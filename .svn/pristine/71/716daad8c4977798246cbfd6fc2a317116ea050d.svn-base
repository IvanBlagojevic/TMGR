package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.model.command.RoleCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoRoleCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class AddRoleTaskHandler extends AbstractManualTaskHandler {

    private static final String ALL_CONTEXT_ROLES_NAME_KEY = "allProjectRoles"; //$NON-NLS-1$

    private static final String ALL_SYSTEM_ROLES_NAME_KEY = "allSystemRoles"; //$NON-NLS-1$

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
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	List<Role> allSystemRoles = getRoleService().findAllSystemRoles();

	List<Role> allProjectRoles = getRoleService().findAllContextRoles();

	List<TaskModel> taskModels = new ArrayList<TaskModel>();

	TaskModel newAllSystemRoleTaskModel = new TaskModel(null, null);

	List<String> allSystemRoleIds = new ArrayList<String>();
	for (Role allSystemRole : allSystemRoles) {
	    allSystemRoleIds.add(allSystemRole.getRoleId());
	}

	newAllSystemRoleTaskModel.addObject(ALL_SYSTEM_ROLES_NAME_KEY, allSystemRoleIds);

	taskModels.add(newAllSystemRoleTaskModel);

	TaskModel newAllProjectRoleTaskModel = new TaskModel(null, null);

	List<String> allProjectRoleIds = new ArrayList<String>();
	for (Role allProjectRole : allProjectRoles) {
	    allProjectRoleIds.add(allProjectRole.getRoleId());
	}

	newAllProjectRoleTaskModel.addObject(ALL_CONTEXT_ROLES_NAME_KEY, allProjectRoleIds);

	taskModels.add(newAllProjectRoleTaskModel);

	return taskModels.toArray(new TaskModel[taskModels.size()]);
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {

	RoleCommand addCommand = (RoleCommand) command;

	String roleName = addCommand.getRoleId();

	if (getRoleService().findRoleByName(roleName) != null) {
	    throw new UserException(MessageResolver.getMessage("AddRoleTaskHandler.3"), //$NON-NLS-1$
		    String.format(MessageResolver.getMessage("AddRoleTaskHandler.2"), roleName)); //$NON-NLS-1$
	}

	String roleId = getRoleService().createRole(addCommand.getRoleId(), addCommand.getRoleType());

	roleId = getRoleService().updateRolePolicies(roleId, addCommand.getPolicies());

	return new TaskResponse(new Ticket(roleId));
    }
}

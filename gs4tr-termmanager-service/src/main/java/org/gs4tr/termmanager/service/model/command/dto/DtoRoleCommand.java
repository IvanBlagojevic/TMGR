package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Set;

import org.gs4tr.termmanager.model.dto.Policy;
import org.gs4tr.termmanager.model.dto.RoleTypeEnum;
import org.gs4tr.termmanager.model.dto.converter.PolicyConverter;
import org.gs4tr.termmanager.model.dto.converter.RoleTypeEnumConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.RoleCommand;

public class DtoRoleCommand implements DtoTaskHandlerCommand<RoleCommand> {

    private String _name;

    private Policy[] _policies;

    private String _roleId;

    private RoleTypeEnum _roleType;

    private String _userTicket;

    public RoleCommand convertToInternalTaskHandlerCommand() {

	RoleCommand roleCommand = new RoleCommand();

	roleCommand.setRoleType(RoleTypeEnumConverter.fromDtoToInternal(getRoleType()));
	Set<org.gs4tr.foundation.modules.security.model.Policy> policies = PolicyConverter
		.fromDtoToInternal(getPolicies());

	roleCommand.setPolicies(policies);

	roleCommand.setName(getRoleId());
	roleCommand.setRoleId(getRoleId());
	roleCommand.setUserId(TicketConverter.fromDtoToInternal(getUserTicket(), Long.class));

	return roleCommand;
    }

    public String getName() {
	return _name;
    }

    public Policy[] getPolicies() {
	return _policies;
    }

    public String getRoleId() {
	return _roleId;
    }

    public RoleTypeEnum getRoleType() {
	return _roleType;
    }

    public String getUserTicket() {
	return _userTicket;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setPolicies(Policy[] policies) {
	_policies = policies;
    }

    public void setRoleId(String roleId) {
	_roleId = roleId;
    }

    public void setRoleType(RoleTypeEnum roleType) {
	_roleType = roleType;
    }

    public void setUserTicket(String userTicket) {
	this._userTicket = userTicket;
    }

}
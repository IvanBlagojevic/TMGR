package org.gs4tr.termmanager.webmvc.controllers;

import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.security.model.search.RoleSearchRequest;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.dto.converter.RoleConverter;
import org.gs4tr.termmanager.model.dto.converter.RoleTypeEnumConverter;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = UrlConstants.ROLE_SEARCH, method = RequestMethod.POST)
public class RoleSearchController extends
	AbstractSearchGridController<Role, org.gs4tr.termmanager.model.dto.Role, RoleSearchRequest, SearchCommand> {

    @Autowired
    private RoleService _roleService;

    public RoleSearchController() {
	super(EntityTypeHolder.ADMIN, Role.class, org.gs4tr.termmanager.model.dto.Role.class);
    }

    public RoleService getRoleService() {
	return _roleService;
    }

    @Override
    protected org.gs4tr.termmanager.model.dto.Role createDtoEntityFromEntity(Role entity) {

	org.gs4tr.termmanager.model.dto.Role roleDTO = RoleConverter.fromInternalToDto(entity);

	return roleDTO;
    }

    @Override
    protected String createGridConfigKey(SearchCommand searchGridCommand) {
	return ItemFolderEnum.SECURITY.name().toLowerCase();
    }

    @Override
    protected RoleSearchRequest createSearchRequestFromSearchCommand(SearchCommand command) {
	RoleSearchRequest roleSearchRequest = new RoleSearchRequest();
	roleSearchRequest.setRoleId(command.getRoleNameInputText());
	return roleSearchRequest;
    }

    @Override
    protected TaskPagedList<Role> search(RoleSearchRequest searchRequest, SearchCommand command,
	    PagedListInfo pagedListInfo) {
	return (TaskPagedList<Role>) getRoleService().search(searchRequest, pagedListInfo);
    }

    @Override
    protected void setCommandFields(SearchCommand command) {

    }

    @Override
    protected void setTaskHolderFields(org.gs4tr.termmanager.model.dto.Role dtoEntity, Role entity,
	    Map<String, Integer> dtoUnionTaskMap) {
	dtoEntity.setRoleId(entity.getRoleId());
	dtoEntity.setRoleType(RoleTypeEnumConverter.fromInternalToDto(entity.getRoleType()));
    }
}

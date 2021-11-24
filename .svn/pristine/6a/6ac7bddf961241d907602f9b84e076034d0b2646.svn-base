package org.gs4tr.termmanager.webmvc.controllers;

import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.search.ProjectSearchRequest;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.converter.ProjectConverter;
import org.gs4tr.termmanager.model.dto.converter.ProjectInfoConverter;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = UrlConstants.PROJECT_SEARCH, method = RequestMethod.POST)
public class ProjectSearchController extends
	AbstractSearchGridController<TmProject, org.gs4tr.termmanager.model.dto.Project, ProjectSearchRequest, SearchCommand> {

    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private ProjectService _projectService;

    public ProjectSearchController() {
	super(EntityTypeHolder.ADMIN, TmProject.class, org.gs4tr.termmanager.model.dto.Project.class);
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

    private String getCurrentUserOrganizationName() {
	return getOrganizationService().getOrganizationNameByUserId(TmUserProfile.getCurrentUserId());
    }

    private OrganizationService getOrganizationService() {
	return _organizationService;
    }

    @Override
    protected org.gs4tr.termmanager.model.dto.Project createDtoEntityFromEntity(TmProject entity) {
	return ProjectConverter.fromInternalToDto(entity);
    }

    @Override
    protected String createGridConfigKey(SearchCommand searchGridCommand) {
	return ItemFolderEnum.PROJECTS.name().toLowerCase();
    }

    @Override
    protected ProjectSearchRequest createSearchRequestFromSearchCommand(SearchCommand command) {
	ProjectSearchRequest request = new ProjectSearchRequest();
	request.setName(command.getProjectNameInputText());
	request.setOrganizationName(getCurrentUserOrganizationName());
	return request;
    }

    @Override
    protected TaskPagedList<TmProject> search(ProjectSearchRequest searchRequest, SearchCommand command,
	    PagedListInfo pagedListInfo) {
	return (TaskPagedList<TmProject>) getProjectService().search(searchRequest, pagedListInfo);
    }

    @Override
    protected void setCommandFields(SearchCommand command) {

    }

    @Override
    protected void setTaskHolderFields(org.gs4tr.termmanager.model.dto.Project dtoEntity, TmProject entity,
	    Map<String, Integer> dtoUnionTaskMap) {
	ProjectInfo projectInfo = entity.getProjectInfo();
	dtoEntity.setProjectInfo(ProjectInfoConverter.fromInternalToDto(projectInfo));
    }

}

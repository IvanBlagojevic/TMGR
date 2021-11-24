package org.gs4tr.termmanager.webmvc.controllers;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.DtoProjectDetailView;
import org.gs4tr.termmanager.model.dto.converter.ProjectDetailViewConverter;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.search.command.UserProjectSearchRequest;
import org.gs4tr.termmanager.model.view.ProjectDetailView;
import org.gs4tr.termmanager.service.ProjectDetailService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = UrlConstants.PROJECT_DETAIL_SEARCH, method = RequestMethod.POST)
public class ProjectDetailSearchController extends
	AbstractSearchGridController<ProjectDetailView, DtoProjectDetailView, UserProjectSearchRequest, SearchCommand> {

    @Autowired
    private ProjectDetailService _projectDetailService;

    protected ProjectDetailSearchController() {
	super(EntityTypeHolder.PROJECTDETAIL, ProjectDetailView.class, DtoProjectDetailView.class);
    }

    private ProjectDetailService getProjectDetailService() {
	return _projectDetailService;
    }

    @Override
    protected DtoProjectDetailView createDtoEntityFromEntity(ProjectDetailView entity) {
	return ProjectDetailViewConverter.fromInternalToDto(entity);
    }

    @Override
    protected String createGridConfigKey(SearchCommand searchGridCommand) {
	return ItemFolderEnum.PROJECTDETAILS.name().toLowerCase();
    }

    @Override
    protected UserProjectSearchRequest createSearchRequestFromSearchCommand(SearchCommand command) {
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	Map<Long, Set<String>> projectUserLanguages = user.getProjectUserLanguages();

	Set<Long> projectIds = projectUserLanguages.keySet();

	Set<String> languageIds = new HashSet<String>();
	for (Entry<Long, Set<String>> entry : projectUserLanguages.entrySet()) {
	    languageIds.addAll(entry.getValue());
	}

	UserProjectSearchRequest request = new UserProjectSearchRequest();
	request.setName(command.getProjectNameInputText());
	request.setShortCode(command.getProjectShortCodeInputText());
	request.setFolder(ItemFolderEnum.PROJECTDETAILS);
	request.setProjectIds(projectIds);
	request.setLanguageIds(languageIds);
	request.setUser(user);

	return request;
    }

    @Override
    protected TaskPagedList<ProjectDetailView> search(UserProjectSearchRequest searchRequest, SearchCommand command,
	    PagedListInfo pagedListInfo) {
	return getProjectDetailService().search(searchRequest, pagedListInfo);
    }

    @Override
    protected void setCommandFields(SearchCommand command) {

    }
}

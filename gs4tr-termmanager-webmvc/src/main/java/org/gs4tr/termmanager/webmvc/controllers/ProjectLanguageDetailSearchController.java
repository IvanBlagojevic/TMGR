package org.gs4tr.termmanager.webmvc.controllers;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.dto.DtoProjectLanguageDetailView;
import org.gs4tr.termmanager.model.dto.converter.ProjectLanguageDetailViewConverter;
import org.gs4tr.termmanager.model.search.ItemPaneEnum;
import org.gs4tr.termmanager.model.search.command.ProjectLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;
import org.gs4tr.termmanager.service.ProjectLanguageDetailService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = UrlConstants.PROJECT_LANGUAGE_DETAIL_SEARCH, method = RequestMethod.POST)
public class ProjectLanguageDetailSearchController extends
	AbstractSearchGridController<ProjectLanguageDetailView, DtoProjectLanguageDetailView, ProjectLanguageDetailRequest, SearchCommand> {

    @Autowired
    private ProjectLanguageDetailService _projectLanguageDetailService;

    protected ProjectLanguageDetailSearchController() {
	super(EntityTypeHolder.PROJECTLANGUAGEDETAIL, ProjectLanguageDetailView.class,
		DtoProjectLanguageDetailView.class);
    }

    private ProjectLanguageDetailService getProjectLanguageDetailService() {
	return _projectLanguageDetailService;
    }

    @Override
    protected DtoProjectLanguageDetailView createDtoEntityFromEntity(ProjectLanguageDetailView entity) {
	return ProjectLanguageDetailViewConverter.fromInternalToDto(entity);
    }

    @Override
    protected String createGridConfigKey(SearchCommand searchGridCommand) {
	return ItemPaneEnum.PROJECTLANGUAGEDETAILS.name().toLowerCase();
    }

    @Override
    protected ProjectLanguageDetailRequest createSearchRequestFromSearchCommand(SearchCommand command) {

	ProjectLanguageDetailRequest request = new ProjectLanguageDetailRequest();

	request.setProjectDetailId(IdEncrypter.decryptGenericId(command.getTicket()));

	return request;
    }

    @Override
    protected TaskPagedList<ProjectLanguageDetailView> search(ProjectLanguageDetailRequest searchRequest,
	    SearchCommand command, PagedListInfo pagedListInfo) {

	return (TaskPagedList<ProjectLanguageDetailView>) getProjectLanguageDetailService().search(searchRequest,
		pagedListInfo);
    }

    @Override
    protected void setCommandFields(SearchCommand command) {

    }
}

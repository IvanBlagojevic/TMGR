package org.gs4tr.termmanager.webmvc.controllers;

import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.OrganizationInfo;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.search.OrganizationSearchRequest;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.dto.converter.OrganizationConverter;
import org.gs4tr.termmanager.model.dto.converter.OrganizationInfoConverter;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = UrlConstants.ORGANIZATION_SEARCH, method = RequestMethod.POST)
public class OrganizationSearchController extends
	AbstractSearchGridController<TmOrganization, org.gs4tr.termmanager.model.dto.Organization, OrganizationSearchRequest, SearchCommand> {

    @Autowired
    private OrganizationService _organizationService;

    public OrganizationSearchController() {
	super(EntityTypeHolder.ADMIN, TmOrganization.class, org.gs4tr.termmanager.model.dto.Organization.class);
    }

    public OrganizationService getOrganizationService() {
	return _organizationService;
    }

    @Override
    protected org.gs4tr.termmanager.model.dto.Organization createDtoEntityFromEntity(TmOrganization entity) {
	org.gs4tr.termmanager.model.dto.Organization organizationDTO = OrganizationConverter.fromInternalToDto(entity);

	return organizationDTO;
    }

    @Override
    protected String createGridConfigKey(SearchCommand searchGridCommand) {
	return ItemFolderEnum.ORGANIZATIONS.name().toLowerCase();
    }

    @Override
    protected OrganizationSearchRequest createSearchRequestFromSearchCommand(SearchCommand command) {

	OrganizationSearchRequest request = new OrganizationSearchRequest();
	request.setName(command.getOrganizationNameInputText());
	return request;
    }

    @Override
    protected TaskPagedList<TmOrganization> search(OrganizationSearchRequest searchRequest, SearchCommand command,
	    PagedListInfo pagedListInfo) {
	return (TaskPagedList<TmOrganization>) getOrganizationService().search(searchRequest, pagedListInfo);
    }

    @Override
    protected void setCommandFields(SearchCommand command) {

    }

    @Override
    protected void setTaskHolderFields(org.gs4tr.termmanager.model.dto.Organization dtoEntity, TmOrganization entity,
	    Map<String, Integer> dtoUnionTaskMap) {
	OrganizationInfo organizationInfo = entity.getOrganizationInfo();
	dtoEntity.setOrganizationInfo(OrganizationInfoConverter.fromInternalToDto(organizationInfo));
    }

}

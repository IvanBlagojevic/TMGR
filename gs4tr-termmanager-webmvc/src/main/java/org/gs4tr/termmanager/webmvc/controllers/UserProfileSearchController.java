package org.gs4tr.termmanager.webmvc.controllers;

import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.search.UserProfileSearchRequest;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.dto.converter.UserInfoConverter;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = UrlConstants.USER_PROFILE_SEARCH)
public class UserProfileSearchController extends
	AbstractSearchGridController<TmUserProfile, org.gs4tr.termmanager.model.dto.UserProfile, UserProfileSearchRequest, SearchCommand> {

    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private UserProfileService _userProfileService;

    public UserProfileSearchController() {
	super(EntityTypeHolder.ADMIN, TmUserProfile.class, org.gs4tr.termmanager.model.dto.UserProfile.class);
    }

    public OrganizationService getOrganizationService() {
	return _organizationService;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private Long getCurrentOrganizationId() {
	return getOrganizationService().getOrganizationIdByUserId(TmUserProfile.getCurrentUserId());
    }

    @Override
    protected org.gs4tr.termmanager.model.dto.UserProfile createDtoEntityFromEntity(TmUserProfile entity) {

	org.gs4tr.termmanager.model.dto.UserProfile userProfileDTO = new org.gs4tr.termmanager.model.dto.UserProfile();

	TmOrganization organization = entity.getOrganization();
	if (organization != null) {
	    userProfileDTO.setOrganizationName(organization.getOrganizationInfo().getName());
	}
	userProfileDTO.setUserInfo(UserInfoConverter.fromInternalToDto(entity.getUserInfo()));
	userProfileDTO.setTicket(TicketConverter.fromInternalToDto(entity.getUserProfileId()));
	userProfileDTO.setGeneric(entity.getGeneric());

	return userProfileDTO;
    }

    @Override
    protected String createGridConfigKey(SearchCommand searchGridCommand) {
	return ItemFolderEnum.USERS.name().toLowerCase();
    }

    @Override
    protected UserProfileSearchRequest createSearchRequestFromSearchCommand(SearchCommand command) {

	UserProfileSearchRequest request = new UserProfileSearchRequest();
	request.setUsername(command.getUserNameInputText());
	request.setFirstname(command.getFirstNameInputText());
	request.setLastname(command.getLastNameInputText());
	request.setEmailAddress(command.getEmailAddressInputText());
	request.setOrganizationId(getCurrentOrganizationId());

	return request;
    }

    @Override
    protected TaskPagedList<TmUserProfile> search(UserProfileSearchRequest searchRequest, SearchCommand command,
	    PagedListInfo pagedListInfo) {
	return (TaskPagedList<TmUserProfile>) getUserProfileService().search(searchRequest, pagedListInfo);
    }

    @Override
    protected void setCommandFields(SearchCommand command) {

    }

    @Override
    protected void setTaskHolderFields(org.gs4tr.termmanager.model.dto.UserProfile dtoEntity, TmUserProfile entity,
	    Map<String, Integer> dtoUnionTaskMap) {
	UserInfo userInfo = entity.getUserInfo();
	dtoEntity.setUserInfo(UserInfoConverter.fromInternalToDto(userInfo));
    }
}

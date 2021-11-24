package org.gs4tr.termmanager.webmvc.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.model.commands.CustomFilterCommand;
import org.gs4tr.termmanager.webmvc.model.response.UiDetailItem;
import org.gs4tr.termmanager.webmvc.model.response.UiMenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "saveCustomFilterController")
@RequestMapping(value = "saveCustomFilter.ter")
public class SaveCustomFilterController extends AbstractController {

    @Value("#{adminSearchCriterias}")
    private Map<ItemFolderEnum, List<String>> _adminSearchCriterias;

    @Value("#{detailsConfig}")
    private Map<ItemFolderEnum, List<UiDetailItem>> _detailsConfig;

    @Autowired
    private UserProfileService _userProfileService;

    @Value("#{userSearchCriterias}")
    private Map<ItemFolderEnum, List<String>> _userSearchCriterias;

    public Map<ItemFolderEnum, List<String>> getAdminSearchCriterias() {
	return _adminSearchCriterias;
    }

    public Map<ItemFolderEnum, List<UiDetailItem>> getDetailsConfig() {
	return _detailsConfig;
    }

    public Map<ItemFolderEnum, List<String>> getUserSearchCriterias() {
	return _userSearchCriterias;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse handle(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute CustomFilterCommand command) throws Exception {

	boolean adminFolder = false;
	CustomFilterCommand customFilterCommand = command;
	String customFolder = customFilterCommand.getCustomFolder();
	String originalFolder = customFilterCommand.getOriginalFolder().toUpperCase();
	String url = customFilterCommand.getUrl();
	String jsonSearch = customFilterCommand.getJsonSearch();
	TmUserProfile currentUser = TmUserProfile.getCurrentUserProfile();

	checkFolderNames(customFilterCommand, currentUser);

	if (customFilterCommand.getInsert()) {
	    checkCustomFilterNames(customFilterCommand, currentUser);
	}

	List<ItemFolderEnum> adminFolders = currentUser.getAdminFolders();
	if (adminFolders != null) {
	    for (ItemFolderEnum folderEnum : adminFolders) {
		String folderEnumValue = folderEnum.toString().toUpperCase();
		if (folderEnumValue.equals(originalFolder)) {
		    adminFolder = true;
		    break;
		}
	    }
	}

	getUserProfileService().addOrUpdateCustomSearchFolder(currentUser, customFolder, originalFolder, url,
		jsonSearch, adminFolder);

	ItemFolderEnum originalFolderEnum = ItemFolderEnum.valueOf(originalFolder);

	List<String> searchCriterias = null;
	if (adminFolder) {
	    searchCriterias = getAdminSearchCriterias().get(originalFolderEnum);
	} else {
	    searchCriterias = getUserSearchCriterias().get(originalFolderEnum);
	}

	UiMenuItem uiMenu = new UiMenuItem(customFolder, url, originalFolder, jsonSearch,
		createDtoSearchBar(searchCriterias), false);

	List<UiDetailItem> detailConfig = getDetailsConfig().get(originalFolderEnum);
	if (detailConfig != null) {
	    uiMenu.setDetailsUrl(detailConfig.toArray(new UiDetailItem[detailConfig.size()]));
	}

	ModelMapResponse modelMap = new ModelMapResponse();
	modelMap.put("menuConfig", uiMenu); //$NON-NLS-1$
	return modelMap;
    }

    public void setAdminSearchCriterias(Map<ItemFolderEnum, List<String>> adminSearchCriterias) {
	_adminSearchCriterias = adminSearchCriterias;
    }

    public void setDetailsConfig(Map<ItemFolderEnum, List<UiDetailItem>> detailsConfig) {
	_detailsConfig = detailsConfig;
    }

    public void setUserSearchCriterias(Map<ItemFolderEnum, List<String>> userSearchCriterias) {
	_userSearchCriterias = userSearchCriterias;
    }

    private void checkCustomFilterNames(CustomFilterCommand customFilterCommand, TmUserProfile currentUser) {
	String customFolder = customFilterCommand.getCustomFolder();

	if (getUserProfileService().getCustomSearchFolder(currentUser, customFolder.toLowerCase()) != null) {
	    throw new UserException(MessageResolver.getMessage("SaveCustomFilterController.0")); //$NON-NLS-1$
	}
    }

    private void checkFolderNames(CustomFilterCommand customFilterCommand, TmUserProfile currentUser) {
	String customFolder = customFilterCommand.getCustomFolder();

	for (ItemFolderEnum folderEnum : ItemFolderEnum.values()) {
	    if (customFolder.equalsIgnoreCase(folderEnum.toString())) {
		throw new UserException(MessageResolver.getMessage("SaveCustomFilterController.0")); //$NON-NLS-1$
	    }
	}
    }

    private String[] createDtoSearchBar(List<String> folderSearchBar) {
	String[] result = new String[folderSearchBar.size()];

	for (int i = 0; i < result.length; i++) {
	    result[i] = folderSearchBar.get(i);
	}

	return result;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }
}

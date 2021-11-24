package org.gs4tr.termmanager.webmvc.controllers;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "removeCustomFilterController")
@RequestMapping(value = "removeCustomFilter.ter")
public class RemoveCustomFilterController extends AbstractController {

    @Autowired
    private UserProfileService _userProfileService;

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @RequestMapping
    @ResponseBody
    public ModelMapResponse handle(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute CustomFilterCommand command) throws Exception {

	CustomFilterCommand customFilterCommand = command;
	String customFolder = customFilterCommand.getCustomFolder();

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	for (ItemFolderEnum folderEnum : ItemFolderEnum.values()) {
	    if (customFolder.equalsIgnoreCase(folderEnum.toString())) {
		throw new UserException(MessageResolver.getMessage("RemoveCustomFilterController.0")); //$NON-NLS-1$
	    }
	}

	if (!getUserProfileService().removeCustomSearchFolder(userProfile, customFolder)) {
	    throw new UserException(MessageResolver.getMessage("RemoveCustomFilterController.1")); //$NON-NLS-1$
	}

	return new ModelMapResponse();
    }
}

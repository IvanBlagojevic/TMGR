package org.gs4tr.termmanager.webmvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gs4tr.foundation.modules.entities.model.Metadata;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.model.commands.FolderViewUpdateCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FolderViewUpdateController extends AbstractController {

    @Autowired
    private SessionService _sessionService;

    @Autowired
    private UserProfileService _userProfileService;

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @RequestMapping(value = "folderViewUpdate.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse handle(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute FolderViewUpdateCommand folderViewUpdateCommand) throws Exception {

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	String key = folderViewUpdateCommand.getFolder().name().toLowerCase();

	Metadata metadata = userProfile.getMetadataByKey(key);

	String newMetadata = folderViewUpdateCommand.getMetadata();
	if ((metadata == null && newMetadata != null)
		|| (metadata != null && !metadata.getValue().equals(newMetadata))) {
	    getUserProfileService().addOrUpdateMetadata(key, newMetadata);
	    refreshUserProfileContext(request, userProfile);
	}

	return new ModelMapResponse();
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    @SuppressWarnings("unchecked")
    private void refreshUserProfileContext(HttpServletRequest request, TmUserProfile userProfile) {
	HttpSession session = request.getSession(false);

	if (session != null) {
	    SecurityContext securityContext = (SecurityContext) session
		    .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

	    // check if secured session is in progress
	    if (securityContext != null) {
		Authentication authentication = securityContext.getAuthentication();

		if (authentication.isAuthenticated()) {
		    getSessionService().registerAuthentication(authentication);

		    // update session
		    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
			    SecurityContextHolder.getContext());
		}
	    }
	}
    }
}

package org.gs4tr.termmanager.webmvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProjectWriteConfigurationController extends AbstractController {

    @RequestMapping(value = "projectWriteConfiguration.ter", method = RequestMethod.GET)
    @ResponseBody
    public ModelMapResponse handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	ModelMapResponse mapResponse = new ModelMapResponse();

	String[] policies = { ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.toString(),
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM.toString() };
	mapResponse.put(ControllerConstants.PROJECT_CONFIGURATION,
		ControllerUtils.isProjectPolicyEnabled(userProfile, policies));

	return mapResponse;
    }

}

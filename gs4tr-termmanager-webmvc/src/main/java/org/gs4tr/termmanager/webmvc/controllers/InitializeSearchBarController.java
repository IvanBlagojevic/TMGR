package org.gs4tr.termmanager.webmvc.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.model.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("initializeSearchBarController")
public class InitializeSearchBarController extends AbstractController {

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private SubmissionService _submissionService;

    @Autowired
    private UserProfileService _userProfileService;

    @RequestMapping(value = "initializeSearchBar.ter", method = RequestMethod.GET)
    @ResponseBody
    public ModelMapResponse handle(HttpServletRequest request, HttpServletResponse response) throws Exception {

	Map<SearchCriteria, Object> searchBar = ControllerUtils.createSearchBarElements(getProjectService(),
		getSubmissionService(), getUserProfileService());

	ModelMapResponse mapResponse = new ModelMapResponse();
	mapResponse.put("searchBar", searchBar); //$NON-NLS-1$

	return mapResponse;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }
}

package org.gs4tr.termmanager.webmvc.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.webmvc.rest.AbstractRestController;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.Project;
import org.gs4tr.termmanager.model.dto.converter.ProjectConverter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.webmvc.rest.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FindUserProjectsController extends AbstractRestController {

    private static Log _logger = LogFactory.getLog(FindUserProjectsController.class);

    @Autowired
    private ProjectService _projectService;

    @RequestMapping(value = "/rest/userProjects", method = RequestMethod.GET, produces = "application/xml;charset=UTF-8")
    @ResponseBody
    public String doGet(@RequestParam(value = "userId") String userId) {

	LogHelper.debug(_logger, String.format(Messages.getString("FindUserProjectsController.0"), //$NON-NLS-1$
		TmUserProfile.getCurrentUserName()));

	Project[] projects = ProjectConverter.fromInternalToDto(getProjectService()
		.getUserProjects(TmUserProfile.getCurrentUserProfile().getUserProfileId(), TmOrganization.class));

	return SerializationUtils.toXML(projects);
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

}

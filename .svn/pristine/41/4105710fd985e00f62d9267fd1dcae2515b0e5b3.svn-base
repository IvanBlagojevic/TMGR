package org.gs4tr.termmanager.webmvc.rest;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.webmvc.rest.AbstractRestController;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
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
public class FindProjectByShortcodeController extends AbstractRestController {

    private static final Log LOGGER = LogFactory.getLog(FindProjectByShortcodeController.class);

    @Autowired
    private ProjectService _projectService;

    @RequestMapping(value = "/rest/projectByShortcode", method = RequestMethod.GET, produces = "application/xml;charset=UTF-8")
    @ResponseBody
    public String doGet(@RequestParam String projectShortcode, @RequestParam(value = "userId") String userId,
	    @RequestParam(required = false) String fetchLanguages) {
	Boolean isFetchLanguages = Boolean.valueOf(fetchLanguages);

	LogHelper.debug(LOGGER, String.format(Messages.getString("FindProjectByShortcodeController.0"), //$NON-NLS-1$
		TmUserProfile.getCurrentUserName(), projectShortcode, isFetchLanguages));

	Project project = findProjectByShortcode(projectShortcode, isFetchLanguages, userId);

	try {
	    return SerializationUtils.toXML(project);
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public Project findProjectByShortcode(String projectShortcode, Boolean fetchLanguages, String userId) {

	TmProject project = getProjectService().findProjectByShortCode(projectShortcode);
	if (project == null) {
	    return null;
	}

	Long projectId = project.getProjectId();

	Set<String> languageIds = null;

	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	if (project != null && fetchLanguages) {
	    Map<Long, Set<String>> projectUserLanguages = user.getProjectUserLanguages();
	    languageIds = projectUserLanguages.get(projectId);
	}

	Project dtoProject = ProjectConverter.fromInternalToDtoWS(project, languageIds);
	if (dtoProject == null) {
	    return null;
	}

	String[] addTermPolicies = new String[] { ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.toString(),
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM.toString(),
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM.toString() };

	boolean canAddTerm = user.containsContextPolicies(projectId, addTermPolicies);

	dtoProject.setReadOnly(!canAddTerm);

	return dtoProject;
    }

    public Project[] findUserProjects(String userId) {
	return ProjectConverter.fromInternalToDto(getProjectService()
		.getUserProjects(TmUserProfile.getCurrentUserProfile().getUserProfileId(), TmOrganization.class));
    }

    public ProjectService getProjectService() {
	return _projectService;
    }
}
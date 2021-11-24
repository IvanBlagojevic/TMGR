package org.gs4tr.termmanager.webmvc.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.webmvc.rest.AbstractRestController;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.ExportTermModel;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AddTermController extends AbstractRestController {

    private static final Log LOGGER = LogFactory.getLog(AddTermController.class);

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    @RequestMapping(value = "/rest/addTerm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @LogEvent(action = TMGREventActionConstants.ACTION_ADD_TERM, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST)
    public String doPost(@RequestParam(value = "sourceTerm") String sourceTerm,
	    @RequestParam(required = false, value = "sourceDescription") String sourceDescription,
	    @RequestParam(value = "targetTerm") String targetTerm,
	    @RequestParam(required = false, value = "targetDescription") String targetDescription,
	    @RequestParam(value = "sourceLocale") String sourceLocale,
	    @RequestParam(value = "targetLocale") String targetLocale,
	    @RequestParam(value = "projectTicket") String projectTicket,
	    @RequestParam(value = "userId") String userId) {

	EventLogger.addProperty(EventContextConstants.SOURCE_LANGUAGE, sourceLocale);
	EventLogger.addProperty(EventContextConstants.TARGET_LANGUAGE, targetLocale);

	String sourceLocaleCode = Locale.makeLocale(sourceLocale).getCode();
	String targetLocaleCode = Locale.makeLocale(targetLocale).getCode();

	// projectTicket = URLDecoder.decode(projectTicket, "UTF-8");
	// //$NON-NLS-1$
	Long projectId = TicketConverter.fromDtoToInternal(projectTicket, Long.class);
	EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);

	TmProject project = getProjectService().load(projectId);
	EventLogger.addProperty(EventContextConstants.PROJECT_NAME, project.getProjectInfo().getName());
	EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());

	if (LOGGER.isDebugEnabled()) {
	    String projectName = project.getProjectInfo().getName();
	    LOGGER.debug(String.format(Messages.getString("AddTermController.2"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), projectName, sourceLocaleCode, targetLocaleCode));
	}

	List<String> errors = new ArrayList<String>();

	ExportTermModel exportTermModel = doAddTerm(projectId, sourceLocaleCode, targetLocaleCode, sourceTerm,
		sourceDescription, targetTerm, targetDescription, errors);

	if (exportTermModel == null) {
	    throw new RuntimeException(errors.get(0));
	}

	return JsonUtils.writeValueAsString(exportTermModel);

    }

    private ExportTermModel doAddTerm(Long projectId, String sourceLocale, String targetLocale, String sourceTerm,
	    String sourceDescription, String targetTerm, String targetDescription, List<String> errorsHolder) {

	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	boolean canAddTerm = user.containsContextPolicies(projectId,
		new String[] { ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.toString(),
			ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM.toString(),
			ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM.toString() });
	if (!canAddTerm) {
	    errorsHolder.add(Messages.getString("AddTermController.1")); //$NON-NLS-1$
	    return null;
	}

	Map<Long, Set<String>> projectUserLanguages = user.getProjectUserLanguages();

	Set<String> locales = projectUserLanguages.get(projectId);
	if (locales == null || !locales.contains(sourceLocale) || !locales.contains(targetLocale)) {
	    errorsHolder.add(Messages.getString("AddTermController.0")); //$NON-NLS-1$
	    return null;
	}

	return getTermEntryService().addTermWS(projectId, sourceLocale, targetLocale, sourceTerm, sourceDescription,
		targetTerm, targetDescription);
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }
}
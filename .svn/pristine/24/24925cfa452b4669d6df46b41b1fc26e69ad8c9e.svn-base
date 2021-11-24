package org.gs4tr.termmanager.webmvc.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BrowseSubmissionlanguagesController extends AbstractController {

    @Autowired
    private SubmissionService _submissionService;

    @RequestMapping(value = "browseSubmissionLanguages.ter", method = RequestMethod.GET)
    @ResponseBody
    public ModelMapResponse handle(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute SearchCommand command) throws Exception {
	ModelMapResponse mapResponse = new ModelMapResponse();

	String submissionTicket = command.getSubmissionTicket();
	Long submissionId = TicketConverter.fromDtoToInternal(submissionTicket, Long.class);

	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	String userName = user.getUserName();

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	String submitter = submission.getSubmitter();
	TmProject project = submission.getProject();
	Long projectId = project.getProjectId();

	boolean isPowerUser = user.isPowerUser();
	boolean isSubmitter = submitter.equals(userName);
	boolean submitterView = ServiceUtils.isSubmitterUser(user, projectId);

	List<SubmissionLanguage> submissionLanguages = getSubmissionService().findSubmissionLanguages(submissionId);

	Map<String, String> sorted = new LinkedHashMap<String, String>();

	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    boolean isAssignee = userName.equals(submissionLanguage.getAssignee());
	    String languageId = submissionLanguage.getLanguageId();
	    if (isSubmitter || isAssignee || isPowerUser) {
		ItemStatusType status = submitterView ? submissionLanguage.getEntityStatusPriority().getStatus()
			: submissionLanguage.getStatusAssignee();
		sorted.put(languageId, status.getName());
	    }
	}

	mapResponse.addObject("languagesStatus", sorted);

	return mapResponse;
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }
}

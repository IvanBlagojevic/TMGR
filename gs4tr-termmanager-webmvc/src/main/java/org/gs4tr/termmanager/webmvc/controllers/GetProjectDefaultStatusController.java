package org.gs4tr.termmanager.webmvc.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.gs4tr.termmanager.webmvc.model.response.ProjectInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GetProjectDefaultStatusController extends AbstractController {

    private static final String APPROVE = "approve"; //$NON-NLS-1$

    private static final String BLACKLIST = "blacklist"; //$NON-NLS-1$

    private static final String DEMOTE = "demote"; //$NON-NLS-1$

    private static final String ON_HOLD = "onHold"; //$NON-NLS-1$

    @Autowired
    private ProjectService _projectService;

    @RequestMapping(value = "projectDefaultTermStatus.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse handle(@ModelAttribute SearchCommand command) {
	List<ProjectInfoModel> projectInfos = new ArrayList<>();

	List<String> projectTickets = command.getProjectComboBox();

	if (CollectionUtils.isNotEmpty(projectTickets)) {

	    List<TmProject> projects = getProjectService()
		    .findProjectByIds(TicketConverter.fromDtoToInternal(projectTickets));

	    for (TmProject project : projects) {
		Long projectId = project.getProjectId();

		ItemStatusType defaultItemStatus = ServiceUtils.decideTermStatus(project);

		ProjectInfoModel model = new ProjectInfoModel();

		model.setProjectTicket(TicketConverter.fromInternalToDto(projectId));

		model.setDefaultTermStatus(defaultItemStatus.getName());

		findAvailableActionsOnProject(model, projectId);

		projectInfos.add(model);
	    }
	}

	ModelMapResponse mapResponse = new ModelMapResponse();
	mapResponse.put("projectsInfos", projectInfos); //$NON-NLS-1$

	return mapResponse;
    }

    private boolean decideAvailableAction(Long projectId, String projectPolicy) {
	return ServiceUtils.decideAvailablePolicy(projectId, projectPolicy);
    }

    private void findAvailableActionsOnProject(ProjectInfoModel model, Long projectId) {
	final boolean addEditApproved = decideAvailableAction(projectId,
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.toString());
	final boolean addEditPending = decideAvailableAction(projectId,
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM.toString());
	final boolean addEditOnHold = decideAvailableAction(projectId,
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM.toString());
	final boolean addEditBlacklist = decideAvailableAction(projectId,
		ProjectPolicyEnum.POLICY_TM_TERM_ADD_BLACKLIST_TERM.toString());
	final boolean delete = decideAvailableAction(projectId,
		ProjectPolicyEnum.POLICY_TM_TERM_DISABLE_TERM.toString());

	final boolean addEditAttributes = decideAvailableAction(projectId,
		ProjectPolicyEnum.POLICY_TM_TERMENTRY_ASSIGN_ATTRIBUTES.toString());

	final boolean demote = decideAvailableAction(projectId,
		ProjectPolicyEnum.POLICY_TM_TERM_DEMOTE_TERM_STATUS.toString());
	final boolean approve = decideAvailableAction(projectId,
		ProjectPolicyEnum.POLICY_TM_TERM_APPROVE_TERM_STATUS.toString());
	final boolean blacklist = decideAvailableAction(projectId,
		ProjectPolicyEnum.POLICY_TM_TERMENTRY_FORBID_TERMENTRY.toString());
	final boolean onHold = decideAvailableAction(projectId,
		ProjectPolicyEnum.POLICY_TM_TERM_ON_HOLD_TERM_STATUS.toString());

	final Map<String, Boolean> actionsAvailable = model.getActionsAvailable();
	actionsAvailable.put(APPROVE, approve);
	actionsAvailable.put(DEMOTE, demote);
	actionsAvailable.put(BLACKLIST, blacklist);
	actionsAvailable.put(ON_HOLD, onHold);

	model.setDeleteAvailable(delete);
	model.setAddEditApproved(addEditApproved);
	model.setAddEditPending(addEditPending);
	model.setAddEditBlacklist(addEditBlacklist);
	model.setAddEditOnHold(addEditOnHold);
	model.setAddEditAttributes(addEditAttributes);
    }

    private ProjectService getProjectService() {
	return _projectService;
    }
}

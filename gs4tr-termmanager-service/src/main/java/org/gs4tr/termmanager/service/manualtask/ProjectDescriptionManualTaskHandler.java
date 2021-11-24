package org.gs4tr.termmanager.service.manualtask;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.ProjectDescription;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.ProjectDescriptionService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.model.command.ProjectDescriptionCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoProjectDescriptionCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

@SystemTask(priority = TaskPriority.LEVEL_TEN)
public class ProjectDescriptionManualTaskHandler extends AbstractManualTaskHandler {

    private static final String DESCRIPTION_TEXT = "descriptionText";
    private static final String PROJECT_TICKET = "projectTicket";

    @Autowired
    private ProjectDescriptionService _projectDescriptionService;

    @Autowired
    private ProjectService _projectService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoProjectDescriptionCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	ProjectDescriptionCommand descriptionCommand = (ProjectDescriptionCommand) command;
	Long projectId = descriptionCommand.getProjectId();

	TaskModel model = new TaskModel();
	model.addObject(PROJECT_TICKET, convertProjectId(projectId));

	ProjectDescription description = getProjectDescriptionService().findByProjectId(projectId);
	if (Objects.nonNull(description)) {
	    model.addObject(DESCRIPTION_TEXT, description.getText());
	} else {
	    model.addObject(DESCRIPTION_TEXT, StringUtils.EMPTY);
	}

	return new TaskModel[] { model };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	ProjectDescriptionCommand descriptionCommand = (ProjectDescriptionCommand) command;

	String text = descriptionCommand.getProjectDescription();
	Long projectId = descriptionCommand.getProjectId();

	Validate.notNull(projectId, Messages.getString("ProjectTicketError"));

	if (StringUtils.isNotEmpty(text)) {
	    saveOrUpdate(text, projectId);
	} else {
	    delete(projectId);
	}

	return new TaskResponse(new Ticket(projectId));
    }

    private static String convertProjectId(Long projectId) {
	return TicketConverter.fromInternalToDto(projectId);
    }

    private void delete(Long projectId) {
	getProjectDescriptionService().deleteByProjectId(projectId);
    }

    private ProjectDescriptionService getProjectDescriptionService() {
	return _projectDescriptionService;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private void saveOrUpdate(String text, Long projectId) {
	ProjectDescriptionService service = getProjectDescriptionService();
	ProjectDescription projectDescription = service.findByProjectId(projectId);
	if (Objects.isNull(projectDescription)) {
	    TmProject project = getProjectService().load(projectId);
	    projectDescription = new ProjectDescription(text, project);
	} else {
	    projectDescription.setText(text);
	}
	service.saveOrUpdateProjectDescription(projectDescription);
    }

}

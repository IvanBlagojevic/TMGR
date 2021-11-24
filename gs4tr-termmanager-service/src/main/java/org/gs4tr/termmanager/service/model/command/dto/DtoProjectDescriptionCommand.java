package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.ProjectDescriptionCommand;

public class DtoProjectDescriptionCommand implements DtoTaskHandlerCommand<ProjectDescriptionCommand> {

    private String _projectDescription;

    private String _projectTicket;

    @Override
    public ProjectDescriptionCommand convertToInternalTaskHandlerCommand() {
	ProjectDescriptionCommand command = new ProjectDescriptionCommand();
	command.setProjectDescription(getProjectDescription());
	command.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));
	return command;
    }

    public String getProjectDescription() {
	return _projectDescription;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public void setProjectDescription(String projectDescription) {
	_projectDescription = projectDescription;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }
}

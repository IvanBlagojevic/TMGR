package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.service.model.command.ProjectShortCodeCommand;

public class DtoProjectShortCodeCommand implements DtoTaskHandlerCommand<ProjectShortCodeCommand> {

    private String _projectName;

    @Override
    public ProjectShortCodeCommand convertToInternalTaskHandlerCommand() {
	ProjectShortCodeCommand projectShortCodeCommand = new ProjectShortCodeCommand();

	projectShortCodeCommand.setProjectName(getProjectName());

	return projectShortCodeCommand;
    }

    public String getProjectName() {
	return _projectName;
    }

    public void setProjectName(String projectCode) {
	_projectName = projectCode;
    }

}

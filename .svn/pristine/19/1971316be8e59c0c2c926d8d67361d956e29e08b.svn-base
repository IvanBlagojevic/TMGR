package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.service.model.command.PropertyCheckCommand;

public class DtoPropertyCheckCommand
	implements DtoTaskHandlerCommand<org.gs4tr.termmanager.service.model.command.PropertyCheckCommand> {

    private String _jobName;

    private String _projectName;

    private String _shortCode;

    private String _tmName;

    private String _username;

    @Override
    public PropertyCheckCommand convertToInternalTaskHandlerCommand() {
	PropertyCheckCommand command = new PropertyCheckCommand();

	command.setShortCode(getShortCode());
	command.setUsername(getUsername());
	command.setProjectName(getProjectName());
	command.setJobName(getJobName());

	return command;
    }

    public String getJobName() {
	return _jobName;
    }

    public String getProjectName() {
	return _projectName;
    }

    public String getShortCode() {
	return _shortCode;
    }

    public String getTmName() {
	return _tmName;
    }

    public String getUsername() {
	return _username;
    }

    public void setJobName(String jobName) {
	_jobName = jobName;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setShortCode(String shortCode) {
	_shortCode = shortCode;
    }

    public void setTmName(String tmName) {
	_tmName = tmName;
    }

    public void setUsername(String username) {
	_username = username;
    }
}

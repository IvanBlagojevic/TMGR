package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.ProjectInfo;
import org.gs4tr.termmanager.model.dto.ProjectLanguage;
import org.gs4tr.termmanager.model.dto.converter.ItemStatusTypeConverter;
import org.gs4tr.termmanager.model.dto.converter.ProjectInfoConverter;
import org.gs4tr.termmanager.model.dto.converter.ProjectLanguageConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.AssignProjectUserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.ProjectCommand;

public class DtoProjectCommand implements DtoTaskHandlerCommand<ProjectCommand> {

    private String _defaultTermStatus;

    private Boolean _disable;

    private Ticket _organizationTicket;

    private ProjectInfo _projectInfo;

    private ProjectLanguage[] _projectLanguages;

    private Boolean _sharePendingTerms;

    private Ticket _ticket;

    private DtoAssignProjectUserLanguageCommand _userProjectCommand;

    @Override
    public ProjectCommand convertToInternalTaskHandlerCommand() {

	ProjectCommand projectCommand = new ProjectCommand();

	projectCommand.setProjectInfo(ProjectInfoConverter.fromDtoToInternal(getProjectInfo()));

	projectCommand.setId(TicketConverter.fromDtoToInternal(getTicket(), Long.class));

	projectCommand.setDisable(getDisable());

	projectCommand.setProjectLanguages(ProjectLanguageConverter.fromDtoToInternal(getProjectLanguages()));

	projectCommand.setOrganizationId(TicketConverter.fromDtoToInternal(getOrganizationTicket(), Long.class));

	projectCommand.setSharePendingTerms(getSharePendingTerms());

	AssignProjectUserLanguageCommand projectUserLanguageCommand = new AssignProjectUserLanguageCommand();
	if (getUserProjectCommand() != null) {
	    projectUserLanguageCommand = getUserProjectCommand().convertToInternalTaskHandlerCommand();

	}

	projectCommand.setUserProjectCommand(projectUserLanguageCommand);

	String defaultTermStatus = getDefaultTermStatus();
	if (defaultTermStatus != null) {
	    projectCommand.setDefaultTermStatus(ItemStatusTypeConverter.fromDtoToInternal(defaultTermStatus));
	}

	return projectCommand;
    }

    public String getDefaultTermStatus() {
	return _defaultTermStatus;
    }

    public Boolean getDisable() {
	return _disable;
    }

    public Ticket getOrganizationTicket() {
	return _organizationTicket;
    }

    public ProjectInfo getProjectInfo() {
	return _projectInfo;
    }

    public ProjectLanguage[] getProjectLanguages() {
	return _projectLanguages;
    }

    public Boolean getSharePendingTerms() {
	return _sharePendingTerms;
    }

    public Ticket getTicket() {
	return _ticket;
    }

    public DtoAssignProjectUserLanguageCommand getUserProjectCommand() {
	return _userProjectCommand;
    }

    public void setDefaultTermStatus(String defaultTermStatus) {
	_defaultTermStatus = defaultTermStatus;
    }

    public void setDisable(Boolean disable) {
	_disable = disable;
    }

    public void setOrganizationTicket(Ticket organizationTicket) {
	_organizationTicket = organizationTicket;
    }

    public void setProjectInfo(ProjectInfo projectInfo) {
	_projectInfo = projectInfo;
    }

    public void setProjectLanguages(ProjectLanguage[] projectLanguages) {
	_projectLanguages = projectLanguages;
    }

    public void setSharePendingTerms(Boolean sharePendingTerms) {
	_sharePendingTerms = sharePendingTerms;
    }

    public void setTicket(Ticket ticket) {
	_ticket = ticket;
    }

    public void setUserProjectCommand(DtoAssignProjectUserLanguageCommand userProjectCommand) {
	_userProjectCommand = userProjectCommand;
    }
}
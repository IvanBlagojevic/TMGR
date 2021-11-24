package org.gs4tr.termmanager.service.model.command;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.ProjectLanguage;

public class ProjectCommand {

    private ItemStatusType _defaultTermStatus;

    private Boolean _disable;

    private Long _id;

    private Long _organizationId;

    private ProjectInfo _projectInfo;

    private List<ProjectLanguage> _projectLanguages;

    private Boolean _sharePendingTerms;

    private AssignProjectUserLanguageCommand _userProjectCommand;

    public ItemStatusType getDefaultTermStatus() {
	return _defaultTermStatus;
    }

    public Boolean getDisable() {
	return _disable;
    }

    public Long getId() {
	return _id;
    }

    public Long getOrganizationId() {
	return _organizationId;
    }

    public ProjectInfo getProjectInfo() {
	return _projectInfo;
    }

    public List<ProjectLanguage> getProjectLanguages() {
	return _projectLanguages;
    }

    public Boolean getSharePendingTerms() {
	return _sharePendingTerms;
    }

    public AssignProjectUserLanguageCommand getUserProjectCommand() {
	return _userProjectCommand;
    }

    public void setDefaultTermStatus(ItemStatusType defaultTermStatus) {
	_defaultTermStatus = defaultTermStatus;
    }

    public void setDisable(Boolean disable) {
	_disable = disable;
    }

    public void setId(Long id) {
	_id = id;
    }

    public void setOrganizationId(Long organizationId) {
	_organizationId = organizationId;
    }

    public void setProjectInfo(ProjectInfo projectInfo) {
	_projectInfo = projectInfo;
    }

    public void setProjectLanguages(List<ProjectLanguage> projectLanguages) {
	_projectLanguages = projectLanguages;
    }

    public void setSharePendingTerms(Boolean sharePendingTerms) {
	_sharePendingTerms = sharePendingTerms;
    }

    public void setUserProjectCommand(AssignProjectUserLanguageCommand userProjectCommand) {
	_userProjectCommand = userProjectCommand;
    }

}

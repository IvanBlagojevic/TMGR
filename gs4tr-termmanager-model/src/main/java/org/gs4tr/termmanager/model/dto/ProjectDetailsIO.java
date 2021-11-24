package org.gs4tr.termmanager.model.dto;

import java.util.List;

import org.gs4tr.termmanager.model.ProjectLanguageDetailInfoIO;
import org.gs4tr.termmanager.model.ProjectUserDetailIO;

public class ProjectDetailsIO {

    private long _projectId;

    private ProjectDetailCountsIO _projectInfoDetails;

    private List<ProjectLanguageDetailInfoIO> _projectLanguageDetails;

    private List<ProjectUserDetailIO> _projectUserDetails;

    public long getProjectId() {
	return _projectId;
    }

    public ProjectDetailCountsIO getProjectInfoDetails() {
	return _projectInfoDetails;
    }

    public List<ProjectLanguageDetailInfoIO> getProjectLanguageDetails() {
	return _projectLanguageDetails;
    }

    public List<ProjectUserDetailIO> getProjectUserDetails() {
	return _projectUserDetails;
    }

    public void setProjectId(long projectId) {
	_projectId = projectId;
    }

    public void setProjectInfoDetails(ProjectDetailCountsIO projectInfoDetails) {
	_projectInfoDetails = projectInfoDetails;
    }

    public void setProjectLanguageDetails(List<ProjectLanguageDetailInfoIO> projectLanguageDetails) {
	_projectLanguageDetails = projectLanguageDetails;
    }

    public void setProjectUserDetails(List<ProjectUserDetailIO> projectUserDetails) {
	_projectUserDetails = projectUserDetails;
    }
}

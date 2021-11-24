package org.gs4tr.termmanager.model.update;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.TmUserProfile;
import org.springframework.security.core.Authentication;

public class ConnectionInfoHolder implements Serializable {

    private static final long serialVersionUID = -7032502203771070864L;

    private Authentication _authentication;

    private List<String> _exportableStatuses = new ArrayList<>(3);

    private Long _projectId = 0L;

    private String _projectName;

    private String _projectShortCode;

    private String _source;

    private String _target;

    private TmUserProfile _userProfile;

    public Authentication getAuthentication() {
	return _authentication;
    }

    public List<String> getExportableStatuses() {
	return _exportableStatuses;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public String getProjectName() {
	return _projectName;
    }

    public String getProjectShortCode() {
	return _projectShortCode;
    }

    public String getSource() {
	return _source;
    }

    public String getTarget() {
	return _target;
    }

    public TmUserProfile getUserProfile() {
	return _userProfile;
    }

    public void setAuthentication(Authentication authentication) {
	_authentication = authentication;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setProjectShortCode(String projectShortCode) {
	_projectShortCode = projectShortCode;
    }

    public void setSource(String source) {
	_source = source;
    }

    public void setTarget(String target) {
	_target = target;
    }

    public void setUserProfile(TmUserProfile userProfile) {
	_userProfile = userProfile;
    }
}

package org.gs4tr.termmanager.service.solr.restore.model;

public class RecodeOrCloneCommand {

    private String _localeFrom;

    private String _localeTo;

    private Long _projectId;

    private String _projectShortCode;

    public String getLocaleFrom() {
	return _localeFrom;
    }

    public String getLocaleTo() {
	return _localeTo;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public String getProjectShortCode() {
	return _projectShortCode;
    }

    public void setLocaleFrom(String localeFrom) {
	_localeFrom = localeFrom;
    }

    public void setLocaleTo(String localeTo) {
	_localeTo = localeTo;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setProjectShortCode(String projectShortCode) {
	_projectShortCode = projectShortCode;
    }

    @Override
    public String toString() {
	return "RecodeOrCloneCommand{" + "_localeFrom='" + _localeFrom + '\'' + ", _localeTo='" + _localeTo + '\''
		+ ", _projectId=" + _projectId + ", _projectShortCode='" + _projectShortCode + '\'' + '}';
    }
}

package org.gs4tr.termmanager.model.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("project")
public class Project {

    @XStreamAlias("languages")
    private Language[] _languages;

    @XStreamAlias("projectInfo")
    private ProjectInfo _projectInfo;

    @XStreamAlias("ticket")
    private String _ticket;

    @XStreamOmitField
    private String _organizationName;

    @XStreamAlias("readOnly")
    private Boolean _readOnly;

    public Language[] getLanguages() {
	return _languages;
    }

    public String getOrganizationName() {
	return _organizationName;
    }

    public ProjectInfo getProjectInfo() {
	return _projectInfo;
    }

    public Boolean getReadOnly() {
	return _readOnly;
    }

    public String getTicket() {
	return _ticket;
    }

    public void setLanguages(Language[] languages) {
	_languages = languages;
    }

    public void setOrganizationName(String organizationName) {
	_organizationName = organizationName;
    }

    public void setProjectInfo(ProjectInfo projectInfo) {
	_projectInfo = projectInfo;
    }

    public void setReadOnly(Boolean readOnly) {
	_readOnly = readOnly;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }

}

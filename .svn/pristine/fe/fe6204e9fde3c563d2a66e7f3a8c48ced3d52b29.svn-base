package org.gs4tr.termmanager.model;

import java.util.ArrayList;
import java.util.List;

public class ProjectMetadata {

    private List<String> _languages;

    private String _organizationName;

    private String _password;

    private String _projectName;

    private String _projectShortcode;

    private String _username;

    public ProjectMetadata() {
    }

    public ProjectMetadata(String organizationName, String password, String projectName, String projectShortcode,
	    String username) {
	_organizationName = organizationName;
	_password = password;
	_projectName = projectName;
	_projectShortcode = projectShortcode;
	_username = username;
    }

    public void addLanguage(String language) {
	if (_languages == null) {
	    _languages = new ArrayList<>();
	}

	_languages.add(language);
    }

    public List<String> getLanguages() {
	return _languages;
    }

    public String getOrganizationName() {
	return _organizationName;
    }

    public String getPassword() {
	return _password;
    }

    public String getProjectName() {
	return _projectName;
    }

    public String getProjectShortcode() {
	return _projectShortcode;
    }

    public String getUsername() {
	return _username;
    }

    public void setLanguages(List<String> languages) {
	_languages = languages;
    }

    public void setOrganizationName(String organizationName) {
	_organizationName = organizationName;
    }

    public void setPassword(String password) {
	_password = password;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setProjectShortcode(String projectShortcode) {
	_projectShortcode = projectShortcode;
    }

    public void setUsername(String username) {
	_username = username;
    }

}

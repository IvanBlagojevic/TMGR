package org.gs4tr.termmanager.model.view;

public class ProjectReport {

    private long _languageCount = 0;

    private String _languageId;

    private String _maxModifiedDate;

    private String _projectName;

    private long _termCount = 0;

    public long getLanguageCount() {
	return _languageCount;
    }

    public String getLanguageId() {
	return _languageId;
    }

    public String getMaxModifiedDate() {
	return _maxModifiedDate;
    }

    public String getProjectName() {
	return _projectName;
    }

    public long getTermCount() {
	return _termCount;
    }

    public void setLanguageCount(long languageCount) {
	_languageCount = languageCount;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setMaxModifiedDate(String maxModifiedDate) {
	_maxModifiedDate = maxModifiedDate;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setTermCount(long termCount) {
	_termCount = termCount;
    }
}

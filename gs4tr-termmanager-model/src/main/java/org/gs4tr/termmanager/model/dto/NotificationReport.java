package org.gs4tr.termmanager.model.dto;

import java.util.List;

public class NotificationReport {

    private String _projectName;

    private List<Statistics> _statistics;

    public String getProjectName() {
	return _projectName;
    }

    public List<Statistics> getStatistics() {
	return _statistics;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setStatistics(List<Statistics> statistics) {
	_statistics = statistics;
    }
}

package org.gs4tr.termmanager.service.notification.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.service.utils.MailHelper.ReportType;

public class NotificationUserInfo {

    private List<String> _classifiers;
    private String _email;
    private Long _lastWeeklyReport;
    private Map<String, List<Statistics>> _projectStatistics;
    private ReportType _reportType;
    private String _timeZone;
    private Long _userId;
    private String _userName;

    public NotificationUserInfo(Long userId, String userName, String email, String timeZone, Long lastWeeklyReport,
	    ReportType reportType) {
	_userId = userId;
	_userName = userName;
	_email = email;
	_timeZone = timeZone;
	_lastWeeklyReport = lastWeeklyReport;
	_reportType = reportType;
	_classifiers = new ArrayList<>();
    }

    public List<String> getClassifiers() {
	return _classifiers;
    }

    public String getEmail() {
	return _email;
    }

    public Long getLastWeeklyReport() {
	return _lastWeeklyReport;
    }

    public Map<String, List<Statistics>> getProjectStatistics() {
	return _projectStatistics;
    }

    public ReportType getReportType() {
	return _reportType;
    }

    public String getTimeZone() {
	return _timeZone;
    }

    public Long getUserId() {
	return _userId;
    }

    public String getUserName() {
	return _userName;
    }

    public void setProjectStatistics(Map<String, List<Statistics>> projectStatistics) {
	_projectStatistics = projectStatistics;
    }
}

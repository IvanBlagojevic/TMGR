package org.gs4tr.termmanager.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.TmNotificationType;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.NotificationReport;
import org.gs4tr.termmanager.model.dto.converter.StatisticsConverter;
import org.gs4tr.termmanager.service.NotificationReportService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.StatisticsService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.notification.statistics.NotificationUserInfo;
import org.gs4tr.termmanager.service.utils.MailHelper;
import org.gs4tr.termmanager.service.utils.MailHelper.ReportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("notificationReportService")
public class NotificationReportServiceImpl implements NotificationReportService {

    @Autowired
    private MailHelper _mailHelper;

    @Autowired
    private ProjectService _projectService;

    private String _serverAddress;

    @Autowired
    private StatisticsService _statisticsService;

    @Autowired
    private UserProfileService _userProfileService;

    public StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    @Override
    public void sendNotificationReports() {
	List<NotificationUserInfo> infos = getUserNotificationInfos();
	collectStatisticsPerUser(infos);
	sendNotifications(infos);
	decrementStatistics(infos);
    }

    @Value("${serverAddress}")
    public void setServerAddress(String serverAddress) {
	_serverAddress = serverAddress;
    }

    private void collectStatisticsPerUser(List<NotificationUserInfo> infos) {
	ProjectService projectService = getProjectService();

	for (NotificationUserInfo info : infos) {
	    Long userId = info.getUserId();

	    Map<String, List<Statistics>> projectStatistics = projectService.getUserProjectStatistics(userId,
		    info.getReportType().getName());
	    info.setProjectStatistics(projectStatistics);
	}
    }

    private List<org.gs4tr.termmanager.model.dto.Statistics> convertStatisticsToDtos(List<Statistics> statistics) {
	if (CollectionUtils.isEmpty(statistics)) {
	    return Collections.emptyList();
	}
	List<org.gs4tr.termmanager.model.dto.Statistics> dtos = statistics.stream().map(
		stat -> StatisticsConverter.fromInternalToDto(stat, Locale.get(stat.getLanguageId()).getDisplayName()))
		.filter(Objects::nonNull).collect(Collectors.toList());
	return dtos;

    }

    private String createSubjectsDate(NotificationUserInfo info) {
	DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

	String timeZone = info.getTimeZone();
	Calendar ca = Calendar.getInstance();
	ca.setTimeZone(TimeZone.getTimeZone(timeZone));

	ca.setTime(new Date());
	String currentDate = dateFormatter.format(ca.getTime());

	if (info.getReportType() == ReportType.WEEKLY) {
	    Long lastWeeklyReport = info.getLastWeeklyReport();
	    if (lastWeeklyReport != null) {
		String modifiedDate = dateFormatter.format(lastWeeklyReport);
		return String.format("from %s to %s", modifiedDate, currentDate);
	    }
	}

	return currentDate;
    }

    private void decrementStatistics(List<NotificationUserInfo> infos) {
	List<Statistics> sts = new ArrayList<>();

	for (NotificationUserInfo info : infos) {
	    Collection<List<Statistics>> values = info.getProjectStatistics().values();
	    values.forEach(v -> sts.addAll(v));
	}

	getStatisticsService().decrementStatistics(sts);
    }

    private MailHelper getMailHelper() {
	return _mailHelper;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private String getServerAddress() {
	return _serverAddress;
    }

    private List<NotificationUserInfo> getUserNotificationInfos() {
	List<TmUserProfile> users = getUserProfileService().getOrgUsersForReport();
	if (CollectionUtils.isEmpty(users)) {
	    return Collections.emptyList();
	}

	List<NotificationUserInfo> infos = new ArrayList<>();

	for (TmUserProfile user : users) {

	    String timeZone = user.getUserInfo().getTimeZone();
	    Calendar ca = Calendar.getInstance();
	    ca.setTimeZone(Objects.nonNull(timeZone) ? TimeZone.getTimeZone(timeZone) : TimeZone.getDefault());
	    ca.setTime(new Date());

	    int hour = ca.get(Calendar.HOUR_OF_DAY);
	    int dayOfWeek = ca.get(Calendar.DAY_OF_WEEK);

	    Preferences preferences = user.getPreferences();
	    Integer dailyHour = preferences.getDailyHour();
	    Integer weeklyHour = preferences.getWeeklyHour();
	    Integer day = preferences.getDayOfWeek();

	    Long userProfileId = user.getUserProfileId();
	    String userName = user.getUserInfo().getUserName();
	    String emailAddress = user.getUserInfo().getEmailAddress();
	    Long lastWeeklyReport = user.getLastWeeklyReport();

	    List<String> dailyClassifiers = new ArrayList<>();
	    List<String> weeklyClassifiers = new ArrayList<>();

	    List<NotificationProfile> notificationProfiles = user.getNotificationProfiles();
	    if (CollectionUtils.isNotEmpty(notificationProfiles)) {
		for (NotificationProfile p : notificationProfiles) {
		    if (Objects.nonNull(p)) {
			if (p.isSendDailyMailNotification()) {
			    dailyClassifiers.add(p.getNotificationClassifier());
			}
			if (p.isSendWeeklyMailNotification()) {
			    weeklyClassifiers.add(p.getNotificationClassifier());
			}
		    }
		}
	    }

	    boolean dailyReport = dailyHour != null && (dailyHour == hour);
	    if (dailyReport && CollectionUtils.isNotEmpty(dailyClassifiers)) {

		NotificationUserInfo info = new NotificationUserInfo(userProfileId, userName, emailAddress, timeZone,
			lastWeeklyReport, ReportType.DAILY);
		info.getClassifiers().addAll(dailyClassifiers);
		infos.add(info);
	    }

	    boolean weeklyReport = (weeklyHour != null && (weeklyHour == hour) && (day != null && day == dayOfWeek));
	    if (weeklyReport && CollectionUtils.isNotEmpty(weeklyClassifiers)) {
		NotificationUserInfo info = new NotificationUserInfo(userProfileId, userName, emailAddress, timeZone,
			lastWeeklyReport, ReportType.WEEKLY);
		info.getClassifiers().addAll(weeklyClassifiers);
		infos.add(info);
	    }
	}

	return infos;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private void sendNotifications(List<NotificationUserInfo> infos) {
	MailHelper mailHelper = getMailHelper();

	for (NotificationUserInfo info : infos) {
	    Map<String, List<Statistics>> projectStatistics = info.getProjectStatistics();
	    List<NotificationReport> reports = new ArrayList<>();

	    for (Entry<String, List<Statistics>> entry : projectStatistics.entrySet()) {

		String projectName = entry.getKey();
		List<org.gs4tr.termmanager.model.dto.Statistics> dtos = convertStatisticsToDtos(entry.getValue());

		if (CollectionUtils.isNotEmpty(dtos)) {
		    // 5-August-2016, as per [Improvement#TERII-2948]:
		    NotificationReport report = new NotificationReport();
		    report.setProjectName(projectName);
		    report.setStatistics(dtos);
		    reports.add(report);
		}
	    }

	    if (CollectionUtils.isNotEmpty(reports)) {

		String reportDate = createSubjectsDate(info);

		Map<String, Object> config = new HashMap<>();
		config.put("serverAddress", getServerAddress());
		config.put("reports", reports);
		config.put("user", info.getUserName());
		config.put("classifiers", info.getClassifiers());
		config.put("reportType", info.getReportType().getName());
		config.put("interval", info.getReportType().getInterval());
		config.put("reportDate", reportDate);

		mailHelper.sendMailNotification(TmNotificationType.NOTIFICATION_REPORT, config, info.getEmail());
	    }

	    getUserProfileService().updateReportDate(info.getUserId(), info.getTimeZone(), info.getReportType());
	}
    }
}

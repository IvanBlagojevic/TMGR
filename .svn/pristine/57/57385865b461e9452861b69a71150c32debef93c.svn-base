package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.dao.StatisticsDAO;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.TmNotificationType;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.NotificationReport;
import org.gs4tr.termmanager.model.dto.Statistics;
import org.gs4tr.termmanager.service.NotificationReportService;
import org.gs4tr.termmanager.service.StatisticsService;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.utils.MailHelper;
import org.gs4tr.termmanager.service.utils.MailHelper.ReportType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
@TestSuite("service")
public class NotificationReportServiceTest extends AbstractServiceTest {

    private static final String CLASSIFIERS = "classifiers";

    private static final String INTERVAL = "interval";
    private static final String REPORTS = "reports";
    private static final String REPORT_DATE = "reportDate";
    private static final String REPORT_TYPE = "reportType";
    private static final String SERVER_ADDRESS = "serverAddress";

    private static final String USER = "user";

    @Captor
    private ArgumentCaptor<Map<String, Object>> _configurationCaptor;

    @Autowired
    private MailHelper _mailHelper;

    private List<String> _notificationClassifiers;

    @Autowired
    private NotificationReportService _notificationReportService;

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private StatisticsDAO _statisticsDAO;

    @Autowired
    private StatisticsService _statisticsService;

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("notificationReportService")
    public void excludeLanguagesThatHaveNotBeenModifiedTest() {
	List<TmUserProfile> organizationUsers = Collections.singletonList(TmUserProfile.getCurrentUserProfile());

	List<org.gs4tr.termmanager.model.Statistics> statisticsWeekly = getModelObject("statistics3", List.class);

	List<org.gs4tr.termmanager.model.Statistics> statisticsDaily = getModelObject("statistics4", List.class);

	when(getUserProfileService().getOrgUsersForReport()).thenReturn(organizationUsers);

	when(getUserProfileService().getNotificationClassifiers(anyLong(), any(ReportType.class)))
		.thenReturn(getNotificationClassifiers());

	when(getStatisticsDAO().getStatisticsByUserId(TmUserProfile.getCurrentUserProfile().getUserProfileId(),
		ReportType.WEEKLY.getName())).thenReturn(statisticsWeekly);
	when(getStatisticsDAO().getStatisticsByUserId(TmUserProfile.getCurrentUserProfile().getUserProfileId(),
		ReportType.DAILY.getName())).thenReturn(statisticsDaily);

	getNotificationReportService().sendNotificationReports();

	verify(getMailHelper(), times(2)).sendMailNotification(any(TmNotificationType.class),
		getConfigurationCaptor().capture(), any(String.class));

	verify(getUserProfileService(), times(2)).updateReportDate(anyLong(), anyString(), any(ReportType.class));

	Map<String, Object> weekly = getConfigurationByReportType(ReportType.WEEKLY);
	assertEquals(ReportType.WEEKLY.getInterval(), weekly.get(INTERVAL));
	assertEquals(getNotificationClassifiers(), weekly.get(CLASSIFIERS));
	assertEquals(TmUserProfile.getCurrentUserName(), weekly.get(USER));
	assertNotNull(weekly.get(SERVER_ADDRESS));
	assertNotNull(weekly.get(REPORT_DATE));

	List<NotificationReport> weeklyReports = (List<NotificationReport>) weekly.get(REPORTS);
	assertEquals(1, weeklyReports.size());
	assertNotificationReport(weeklyReports.get(0), Locale.GERMANY.getDisplayName(), 3);

	Map<String, Object> daily = getConfigurationByReportType(ReportType.DAILY);
	assertEquals(ReportType.DAILY.getInterval(), daily.get(INTERVAL));
	assertEquals(getNotificationClassifiers(), daily.get(CLASSIFIERS));
	assertEquals(TmUserProfile.getCurrentUserName(), daily.get(USER));
	assertNotNull(daily.get(SERVER_ADDRESS));
	assertNotNull(daily.get(REPORT_DATE));

	List<NotificationReport> dailyReports = (List<NotificationReport>) daily.get(REPORTS);
	assertEquals(1, dailyReports.size());
	assertNotificationReport(dailyReports.get(0), Locale.GERMANY.getDisplayName(), 4);

	int statisticsNumToDecrement = statisticsDaily.size();
	statisticsNumToDecrement += statisticsWeekly.size();

	verify(getStatisticsService(), times(statisticsNumToDecrement))
		.decrementStatistics(any(org.gs4tr.termmanager.model.Statistics.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("notificationReportService")
    public void excludeProjectsThatHaveNotBeenModifiedTest() {
	List<TmUserProfile> organizationUsers = Collections.singletonList(TmUserProfile.getCurrentUserProfile());

	List<org.gs4tr.termmanager.model.Statistics> statisticsWeekly = getModelObject("statistics1", List.class);

	List<org.gs4tr.termmanager.model.Statistics> statisticsDaily = getModelObject("statistics2", List.class);

	when(getUserProfileService().getOrgUsersForReport()).thenReturn(organizationUsers);
	when(getUserProfileService().getNotificationClassifiers(anyLong(), any(ReportType.class)))
		.thenReturn(getNotificationClassifiers());

	when(getStatisticsDAO().getStatisticsByUserId(TmUserProfile.getCurrentUserProfile().getUserProfileId(),
		ReportType.WEEKLY.getName())).thenReturn(statisticsWeekly);
	when(getStatisticsDAO().getStatisticsByUserId(TmUserProfile.getCurrentUserProfile().getUserProfileId(),
		ReportType.DAILY.getName())).thenReturn(statisticsDaily);

	getNotificationReportService().sendNotificationReports();

	verify(getUserProfileService(), times(2)).getNotificationClassifiers(anyLong(), any(ReportType.class));

	verify(getMailHelper(), never()).sendMailNotification(any(TmNotificationType.class), any(Map.class),
		any(TmUserProfile.class));

	int statisticsNumToDecrement = statisticsDaily.size();
	statisticsNumToDecrement += statisticsWeekly.size();

	verify(getStatisticsService(), times(statisticsNumToDecrement))
		.decrementStatistics(any(org.gs4tr.termmanager.model.Statistics.class));
    }

    public StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("notificationReportService")
    public void sendNotificationReportsTest() {
	List<TmUserProfile> organizationUsers = Collections.singletonList(TmUserProfile.getCurrentUserProfile());

	List<org.gs4tr.termmanager.model.Statistics> statisticsDaily = getModelObject("statistics5", List.class);

	when(getUserProfileService().getOrgUsersForReport()).thenReturn(organizationUsers);
	when(getUserProfileService().getNotificationClassifiers(anyLong(), any(ReportType.class)))
		.thenReturn(getNotificationClassifiers());

	when(getStatisticsDAO().getStatisticsByUserId(TmUserProfile.getCurrentUserProfile().getUserProfileId(),
		ReportType.DAILY.getName())).thenReturn(statisticsDaily);
	when(getStatisticsDAO().getStatisticsByUserId(TmUserProfile.getCurrentUserProfile().getUserProfileId(),
		ReportType.WEEKLY.getName())).thenReturn(Collections.emptyList());

	getNotificationReportService().sendNotificationReports();

	verify(getUserProfileService(), times(2)).getNotificationClassifiers(anyLong(), any(ReportType.class));
	verify(getMailHelper(), times(1)).sendMailNotification(any(TmNotificationType.class),
		getConfigurationCaptor().capture(), any(TmUserProfile.class));

	int statisticsNumToDecrement = statisticsDaily.size();

	verify(getStatisticsService(), times(statisticsNumToDecrement))
		.decrementStatistics(any(org.gs4tr.termmanager.model.Statistics.class));

	verify(getUserProfileService(), times(1)).updateUserProfile(TmUserProfile.getCurrentUserProfile());

	Map<String, Object> weekly = getConfigurationByReportType(ReportType.WEEKLY);
	assertNull(weekly);

	Map<String, Object> daily = getConfigurationByReportType(ReportType.DAILY);
	assertEquals(ReportType.DAILY.getInterval(), daily.get(INTERVAL));
	assertEquals(getNotificationClassifiers(), daily.get(CLASSIFIERS));
	assertEquals(TmUserProfile.getCurrentUserName(), daily.get(USER));
	assertNotNull(daily.get(SERVER_ADDRESS));
	assertNotNull(daily.get(REPORT_DATE));

	List<NotificationReport> dailyReports = (List<NotificationReport>) daily.get(REPORTS);
	assertEquals(1, dailyReports.size());

	NotificationReport report = dailyReports.get(0);

	assertNotificationReport(report, Locale.US.getDisplayName(), 2);
	assertNotificationReport(report, Locale.GERMANY.getDisplayName(), 4);
    }

    @Before
    public void setUp() {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	TimeZone tz = TimeZone.getTimeZone(userProfile.getUserInfo().getTimeZone());
	Calendar c = Calendar.getInstance();
	c.setTimeZone(tz);
	c.setTime(new Date());

	Preferences preferences = userProfile.getPreferences();
	int dailyHour = c.get(Calendar.HOUR_OF_DAY);
	preferences.setDailyHour(dailyHour);
	preferences.setWeeklyHour(dailyHour);
	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
	preferences.setDayOfWeek(dayOfWeek);

	_notificationClassifiers = new ArrayList<>(6);
	_notificationClassifiers.add("addedApprovedTerm");
	_notificationClassifiers.add("addedPendingTerm");
	_notificationClassifiers.add("approveTerm");
	_notificationClassifiers.add("deletedTerm");
	_notificationClassifiers.add("demoteTerm");
	_notificationClassifiers.add("editTerm");

	Mockito.reset(getUserProfileService());
	Mockito.reset(getStatisticsDAO());
	Mockito.reset(getMailHelper());
	Mockito.reset(getStatisticsService());
    }

    private void assertNotificationReport(NotificationReport report, String expectedLangName, int expectedCount) {
	assertEquals(getProjectDetail().getProject().getProjectInfo().getName(), report.getProjectName());

	Statistics stats = getStatisticsByLangName(expectedLangName, report);

	assertEquals(expectedCount, stats.getAddedApprovedTerms());
	assertEquals(expectedCount, stats.getAddedPendingTerms());
	assertEquals(expectedCount, stats.getApprovedTerms());
	assertEquals(expectedCount, stats.getDeletedTerms());
	assertEquals(expectedCount, stats.getDemotedTerms());
	assertEquals(expectedCount, stats.getUpdatedTerms());
	assertEquals(expectedCount, stats.getAddedOnHoldTerms());
	assertEquals(expectedCount, stats.getOnHoldTerms());
	assertEquals(expectedCount, stats.getAddedBlacklistedTerms());
	assertEquals(expectedCount, stats.getBlacklistedTerms());

	assertEquals(expectedLangName, stats.getLanguageName());
    }

    private Map<String, Object> getConfigurationByReportType(ReportType reportType) {
	List<Map<String, Object>> configurations = getConfigurationCaptor().getAllValues();
	if (CollectionUtils.isEmpty(configurations)) {
	    return Collections.emptyMap();
	}
	for (final Map<String, Object> m : configurations) {
	    if (reportType.getName().equals(m.get(REPORT_TYPE))) {
		return m;
	    }
	}
	return null;
    }

    private ArgumentCaptor<Map<String, Object>> getConfigurationCaptor() {
	return _configurationCaptor;
    }

    private MailHelper getMailHelper() {
	return _mailHelper;
    }

    private List<String> getNotificationClassifiers() {
	return _notificationClassifiers;
    }

    private NotificationReportService getNotificationReportService() {
	return _notificationReportService;
    }

    private ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    private org.gs4tr.termmanager.model.dto.Statistics getStatisticsByLangName(String expectedLangName,
	    NotificationReport report) {
	List<org.gs4tr.termmanager.model.dto.Statistics> statistics = report.getStatistics();

	org.gs4tr.termmanager.model.dto.Statistics result = null;
	if (CollectionUtils.isEmpty(statistics)) {
	    return result;
	}
	for (final Statistics s : statistics) {
	    if (expectedLangName.equals(s.getLanguageName())) {
		result = s;
		break;
	    }
	}
	return result;
    }

    private StatisticsDAO getStatisticsDAO() {
	return _statisticsDAO;
    }
}

package org.gs4tr.termmanager.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.gs4tr.termmanager.model.NotificationPriority;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.utils.MailHelper.ReportType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import junit.framework.Assert;

public class NotificationReportServiceTest extends AbstractSpringServiceTests {

    @Autowired
    private NotificationReportService _notificationReportService;

    @Test
    public void testSendNotificationReports() {

	long userId = 6L;

	TmUserProfile user = getUserProfileService().load(userId);

	String timeZone = user.getUserInfo().getTimeZone();
	Calendar ca = Calendar.getInstance();
	ca.setTimeZone(TimeZone.getTimeZone(timeZone));
	ca.setTime(new Date());

	int hour = ca.get(Calendar.HOUR_OF_DAY);
	int dayOfWeek = ca.get(Calendar.DAY_OF_WEEK);

	Preferences preferences = user.getPreferences();
	preferences.setDailyHour(hour);
	preferences.setWeeklyHour(hour);
	preferences.setDayOfWeek(dayOfWeek);

	getUserProfileService().update(user);

	List<Statistics> sts = getProjectService().getUserStatistics(userId, ReportType.DAILY.getName());
	for (Statistics st : sts) {
	    assertStatistics(1, st);
	}

	getNotificationReportService().sendNotificationReports();

	sts = getProjectService().getUserStatistics(userId, ReportType.DAILY.getName());
	for (Statistics st : sts) {
	    assertStatistics(0, st);
	}
    }

    @Test
    public void testSendNotificationReportsForMultipleUsers() {

	long userId1 = 6L;
	long userId2 = 3L;

	TmUserProfile user1 = getUserProfileService().load(userId1);

	String timeZone = user1.getUserInfo().getTimeZone();
	Calendar ca = Calendar.getInstance();
	ca.setTimeZone(TimeZone.getTimeZone(timeZone));
	ca.setTime(new Date());

	int hour = ca.get(Calendar.HOUR_OF_DAY);
	int dayOfWeek = ca.get(Calendar.DAY_OF_WEEK);

	Preferences preferences = user1.getPreferences();
	preferences.setDailyHour(hour);
	preferences.setWeeklyHour(hour);
	preferences.setDayOfWeek(dayOfWeek);

	getUserProfileService().update(user1);

	TmUserProfile user2 = getUserProfileService().load(userId2);

	preferences = user2.getPreferences();
	preferences.setDailyHour(hour);
	preferences.setWeeklyHour(hour);
	preferences.setDayOfWeek(dayOfWeek);

	NotificationProfile np = new NotificationProfile();
	np.setDisplayDashboardNotification(Boolean.TRUE);
	np.setNotificationClassifier("addedApprovedTerm");
	np.setNotificationPriority(NotificationPriority.NORMAL);
	np.setSendDailyMailNotification(Boolean.TRUE);
	np.setSendTaskMailNotification(Boolean.FALSE);
	np.setSendWeeklyMailNotification(Boolean.TRUE);
	List<NotificationProfile> npList = new ArrayList<>();
	npList.add(np);
	user2.setNotificationProfiles(npList);

	getUserProfileService().update(user2);

	List<Statistics> sts1 = getProjectService().getUserStatistics(userId1, ReportType.DAILY.getName());
	for (Statistics st : sts1) {
	    assertStatistics(1, st);
	}

	List<Statistics> sts2 = getProjectService().getUserStatistics(userId2, ReportType.DAILY.getName());
	for (Statistics st : sts2) {
	    assertStatistics(1, st);
	}

	getNotificationReportService().sendNotificationReports();

	sts1 = getProjectService().getUserStatistics(userId1, ReportType.DAILY.getName());
	for (Statistics st : sts1) {
	    assertStatistics(0, st);
	}

	sts2 = getProjectService().getUserStatistics(userId2, ReportType.DAILY.getName());
	for (Statistics st : sts2) {
	    assertStatistics(0, st);
	}

    }

    private void assertStatistics(int expectedCount, Statistics statistics) {
	Assert.assertEquals(expectedCount, statistics.getAddedApproved());
	Assert.assertEquals(expectedCount, statistics.getApproved());
	Assert.assertEquals(expectedCount, statistics.getAddedPending());
	Assert.assertEquals(expectedCount, statistics.getDemoted());
	Assert.assertEquals(expectedCount, statistics.getAddedBlacklisted());
	Assert.assertEquals(expectedCount, statistics.getBlacklisted());
	Assert.assertEquals(expectedCount, statistics.getAddedOnHold());
	Assert.assertEquals(expectedCount, statistics.getOnHold());
	Assert.assertEquals(expectedCount, statistics.getDeleted());
	Assert.assertEquals(expectedCount, statistics.getUpdated());
    }

    private NotificationReportService getNotificationReportService() {
	return _notificationReportService;
    }
}

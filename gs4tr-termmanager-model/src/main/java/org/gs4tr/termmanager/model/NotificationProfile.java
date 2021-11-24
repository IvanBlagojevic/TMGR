package org.gs4tr.termmanager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

@Embeddable
public class NotificationProfile implements Serializable {

    private static final long serialVersionUID = 1270332189002380312L;

    private boolean _displayDashboardNotification;

    private String _notificationClassifier;

    private NotificationPriority _notificationPriority = NotificationPriority.DEFAULT;

    private String _profileName;

    private boolean _sendDailyMailNotification;

    private Boolean _sendTaskMailNotification = Boolean.FALSE;

    private boolean _sendWeeklyMailNotification;

    @Column(name = "NOTIFICATION_CLASSIFIER", nullable = false, length = 50)
    public String getNotificationClassifier() {
	return _notificationClassifier;
    }

    @Column(name = "NOTIFICATION_PRIORITY", nullable = false, length = 16)
    @Enumerated(EnumType.ORDINAL)
    public NotificationPriority getNotificationPriority() {
	return _notificationPriority;
    }

    @Transient
    public String getProfileName() {
	return _profileName;
    }

    @Column(name = "DISPLAY_ON_DASHBOARD", nullable = false)
    public boolean isDisplayDashboardNotification() {
	return _displayDashboardNotification;
    }

    @Column(name = "SEND_DAILY_MAIL", nullable = false)
    public boolean isSendDailyMailNotification() {
	return _sendDailyMailNotification;
    }

    @Column(name = "SEND_TASK_MAIL", nullable = true, updatable = true)
    public Boolean isSendTaskMailNotification() {
	return _sendTaskMailNotification;
    }

    @Column(name = "SEND_WEEKLY_MAIL", nullable = false)
    public boolean isSendWeeklyMailNotification() {
	return _sendWeeklyMailNotification;
    }

    public void setDisplayDashboardNotification(boolean displayDashboardNotification) {
	_displayDashboardNotification = displayDashboardNotification;
    }

    public void setNotificationClassifier(String notificationClassifier) {
	_notificationClassifier = notificationClassifier;
    }

    public void setNotificationPriority(NotificationPriority notificationPriority) {
	_notificationPriority = notificationPriority;
    }

    public void setProfileName(String profileName) {
	_profileName = profileName;
    }

    public void setSendDailyMailNotification(boolean sendDailyMailNotification) {
	_sendDailyMailNotification = sendDailyMailNotification;
    }

    public void setSendTaskMailNotification(Boolean sendTaskMailNotification) {
	_sendTaskMailNotification = sendTaskMailNotification;
    }

    public void setSendWeeklyMailNotification(boolean sendWeeklyMailNotificaton) {
	_sendWeeklyMailNotification = sendWeeklyMailNotificaton;
    }

}
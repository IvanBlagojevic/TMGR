package org.gs4tr.termmanager.model.dto;

public class Notification {

    private static final String DEFAULT_ALERT_TEXT = "There was a problem with last project director action.";

    private Date _notificationDate;

    private NotificationPriority _notificationPriority;

    private String _notificationText = DEFAULT_ALERT_TEXT;

    public Date getNotificationDate() {
	return _notificationDate;
    }

    public NotificationPriority getNotificationPriority() {
	return _notificationPriority;
    }

    public String getNotificationText() {
	return _notificationText;
    }

    public void setNotificationDate(Date notificationDate) {
	_notificationDate = notificationDate;
    }

    public void setNotificationPriority(NotificationPriority notificationPriority) {
	_notificationPriority = notificationPriority;
    }

    public void setNotificationText(String notificationText) {
	_notificationText = notificationText;
    }

}
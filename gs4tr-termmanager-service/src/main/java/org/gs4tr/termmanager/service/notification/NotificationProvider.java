package org.gs4tr.termmanager.service.notification;

import java.util.Map;

import org.gs4tr.termmanager.model.TmNotificationType;

public class NotificationProvider {

    private Map<TmNotificationType, TaskNotification> _taskNotifications;

    public TaskNotification getTaskNotification(TmNotificationType type) {
	return getTaskNotifications().get(type);
    }

    public Map<TmNotificationType, TaskNotification> getTaskNotifications() {
	return _taskNotifications;
    }

    public void setTaskNotifications(Map<TmNotificationType, TaskNotification> taskNotifications) {
	_taskNotifications = taskNotifications;
    }
}

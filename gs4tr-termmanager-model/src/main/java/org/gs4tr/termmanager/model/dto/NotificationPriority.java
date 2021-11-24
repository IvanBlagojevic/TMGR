package org.gs4tr.termmanager.model.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class NotificationPriority implements Serializable {

    private static final long serialVersionUID = 6706508733848122381L;

    private static final String HIGH_NAME = "HIGH"; //$NON-NLS-1$

    private static final String LOW_NAME = "LOW"; //$NON-NLS-1$

    private static final Set<Object> NAMES = new HashSet<Object>();

    private static final String NORMAL_NAME = "NORMAL"; //$NON-NLS-1$

    static {
	NAMES.add(HIGH_NAME);
	NAMES.add(LOW_NAME);
	NAMES.add(NORMAL_NAME);
    }

    private String _notificationPriorityName;

    public NotificationPriority() {
    }

    public NotificationPriority(String notificationPriorityName) {
	checkName(notificationPriorityName);

	_notificationPriorityName = notificationPriorityName;
    }

    public String getNotificationPriorityName() {
	return _notificationPriorityName;
    }

    public void setNotificationPriorityName(String notificationPriorityName) {
	checkName(notificationPriorityName);

	_notificationPriorityName = notificationPriorityName;
    }

    private void checkName(String notificationPriorityName) {
	if (!NAMES.contains(notificationPriorityName)) {
	    throw new RuntimeException(String.format(Messages.getString("NotificationPriority.0"), //$NON-NLS-1$
		    new Object[] { notificationPriorityName }));
	}
    }

}
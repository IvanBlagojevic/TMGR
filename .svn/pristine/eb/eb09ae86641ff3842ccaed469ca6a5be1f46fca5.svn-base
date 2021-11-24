package org.gs4tr.termmanager.service.mail.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.usermanager.model.SystemAuthenticationHolder;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.termmanager.service.NotificationReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "scheduledNotificationTaskHandler")
public class DefaultScheduledNotificationTaskHandler {
    private static final Log LOGGER = LogFactory.getLog(DefaultScheduledNotificationTaskHandler.class);

    @Autowired
    private NotificationReportService _notificationReportService;

    @Autowired
    private SessionService _sessionService;

    public void send() {
	if (SystemAuthenticationHolder.getSystemAuthentication() != null) {
	   getSessionService().authenticateSystemUser();
	}

	getNotificationReportService().sendNotificationReports();
	LOGGER.info(Messages.getString("DefaultScheduledNotificationTaskHandler.0")); //$NON-NLS-1$
    }

    private NotificationReportService getNotificationReportService() {
	return _notificationReportService;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }
}

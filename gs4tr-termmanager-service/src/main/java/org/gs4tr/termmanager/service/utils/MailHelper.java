package org.gs4tr.termmanager.service.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.mail.EMailMessageSender;
import org.gs4tr.foundation.modules.mail.model.EMailMessageTemplate;
import org.gs4tr.foundation.modules.mail.service.MailTemplatesService;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.TmNotificationType;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.notification.NotificationProvider;
import org.gs4tr.termmanager.service.notification.TaskNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("mailHelper")
public class MailHelper {

    private static final Pattern EMAIL_PATTERN = Pattern
	    .compile("^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4}$"); //$NON-NLS-1$
    private static final Log _logger = LogFactory.getLog(MailHelper.class);

    @Autowired
    private EMailMessageSender _eMailMessageSender;

    @Autowired
    private MailTemplatesService _mailTemplatesService;

    @Autowired
    private NotificationProvider _notificationProvider;

    public boolean isMailNotificationEnabled(TmUserProfile user, String classifier) {
	boolean enabled = false;

	if (!user.getUserInfo().getEmailNotification()) {
	    return enabled;
	}

	List<NotificationProfile> notificationProfiles = user.getNotificationProfiles();
	if (CollectionUtils.isNotEmpty(notificationProfiles)) {
	    return notificationProfiles.stream().filter(p -> p.getNotificationClassifier().equals(classifier))
		    .findFirst().isPresent();
	}

	return enabled;
    }

    public void sendMail(String[] emails, final String mailTemplate, final Map<String, Object> configuration,
	    final File file) {
	final InternetAddress[] internetAddresses = getInternetAddresses(emails);

	if (internetAddresses != null && internetAddresses.length > 0) {
	    Thread mailThread = new Thread() {
		@Override
		public void run() {
		    EMailMessageTemplate messageTemplate = getMailTemplatesService()
			    .getEMailMessageTemplate(mailTemplate);
		    getEMailMessageSender().sendMessage(messageTemplate, internetAddresses, configuration, file);
		}
	    };

	    mailThread.start();
	} else {
	    _logger.info(Messages.getString("MailHelper.0")); //$NON-NLS-1$
	}
    }

    public void sendMailNotification(final TmNotificationType notificationType, final Map<String, Object> configuration,
	    TmUserProfile... users) {

	final InternetAddress[] notificationRecipients = getInternetAddresses(users);

	sendMail(notificationType, configuration, notificationRecipients);
    }

    public void sendMailNotification(final TmNotificationType notificationType, final Map<String, Object> configuration,
	    String... emails) {

	final InternetAddress[] notificationRecipients = getInternetAddresses(emails);

	sendMail(notificationType, configuration, notificationRecipients);
    }

    private EMailMessageSender getEMailMessageSender() {
	return _eMailMessageSender;
    }

    private static InternetAddress[] getInternetAddresses(TmUserProfile... userProfiles) {
	List<InternetAddress> internetAddresses = new ArrayList<InternetAddress>();

	for (TmUserProfile userProfile : userProfiles) {
	    UserInfo userInfo = userProfile.getUserInfo();
	    InternetAddress internetAddress;
	    try {
		internetAddress = new InternetAddress(userInfo.getEmailAddress());
		internetAddresses.add(internetAddress);
	    } catch (AddressException e) {
		throw new RuntimeException(e.getMessage(), e);
	    }
	}

	return internetAddresses.toArray(new InternetAddress[internetAddresses.size()]);
    }

    private static InternetAddress[] getInternetAddresses(String[] emails) {
	List<InternetAddress> internetAddresses = new ArrayList<InternetAddress>();

	for (String email : emails) {
	    Matcher matcher = EMAIL_PATTERN.matcher(StringUtils.trim(email));

	    if (matcher.find()) {
		try {
		    internetAddresses.add(new InternetAddress(email));
		} catch (AddressException e) {
		    _logger.info(String.format(Messages.getString("MailHelper.1"), //$NON-NLS-1$
			    email));
		}
	    }
	}

	return internetAddresses.toArray(new InternetAddress[internetAddresses.size()]);
    }

    private MailTemplatesService getMailTemplatesService() {
	return _mailTemplatesService;
    }

    private NotificationProvider getNotificationProvider() {
	return _notificationProvider;
    }

    private void sendMail(final TmNotificationType notificationType, final Map<String, Object> configuration,
	    InternetAddress[] notificationRecipients) {
	if (ArrayUtils.isNotEmpty(notificationRecipients)) {
	    Thread thread = new Thread() {
		@Override
		public void run() {
		    TaskNotification taskNotification = getNotificationProvider().getTaskNotification(notificationType);
		    EMailMessageTemplate messageTemplate = taskNotification.getEMailMessageTemplate();
		    getEMailMessageSender().sendMessage(messageTemplate, notificationRecipients, configuration);
		}
	    };

	    thread.start();
	}
    }

    public enum ReportType {
	DAILY("daily", "day"), WEEKLY("weekly", "week");

	private String _interval;

	private String _name;

	ReportType(String name, String interval) {
	    _name = name;
	    _interval = interval;
	}

	public String getInterval() {
	    return _interval;
	}

	public String getName() {
	    return _name;
	}
    }
}

package org.gs4tr.termmanager.service.notification.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.model.TmNotificationType;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.notification.MailConstants;
import org.gs4tr.termmanager.service.notification.NotificationProvider;
import org.gs4tr.termmanager.service.utils.MailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("completedTranslationNotificationListener")
public class CompletedTranslationNotificationListener implements NotifyingMessageListener<BatchMessage> {

    private static final String SERVER_ADDRESS = "serverAddress";

    @Autowired
    private MailHelper _mailHelper;

    @Autowired
    private NotificationProvider _notificationProvider;

    private String _serverAddress;

    @Autowired
    private UserProfileService _userProfileService;

    public NotificationProvider getNotificationProvider() {
	return _notificationProvider;
    }

    @Override
    public Class<BatchMessage> getNotifyingMessageClass() {
	return BatchMessage.class;
    }

    public String getServerAddress() {
	return _serverAddress;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void notify(BatchMessage message) {
	Map<String, Object> propertiesMap = message.getPropertiesMap();

	TmNotificationType notificationType = (TmNotificationType) propertiesMap.get(MailConstants.NOTIFICATION_TYPE);

	String submitter = (String) propertiesMap.get(MailConstants.USER);

	TmUserProfile submitterProfile = getUserProfileService().findUserByUsernameFetchNotifications(submitter);

	String classifier = notificationType.getClassifier();

	MailHelper mailHelper = getMailHelper();

	if (!mailHelper.isMailNotificationEnabled(submitterProfile, classifier)) {
	    return;
	}

	Map<String, MutableInt> languageTermConfiguration = (Map<String, MutableInt>) propertiesMap
		.get(MailConstants.TARGET_LANGUAGE);

	for (Entry<String, MutableInt> entry : languageTermConfiguration.entrySet()) {
	    String targetLanguage = entry.getKey();
	    MutableInt numberOfTerms = entry.getValue();
	    Map<String, Object> configuration = createMailConfiguration(targetLanguage, numberOfTerms, propertiesMap);
	    mailHelper.sendMailNotification(notificationType, configuration, submitterProfile);
	}
    }

    @Value("${serverAddress}")
    public void setServerAddress(String serverAddress) {
	_serverAddress = serverAddress;
    }

    @Override
    public boolean supports(BatchMessage arg0) {
	return true;
    }

    private Map<String, Object> createMailConfiguration(String targetLanguage, MutableInt numberOfTerms,
	    Map<String, Object> propertiesMap) {
	Map<String, Object> config = new HashMap<String, Object>();

	config.put(SERVER_ADDRESS, getServerAddress());
	config.put(MailConstants.SUBMISSION_NAME, propertiesMap.get(MailConstants.SUBMISSION_NAME));
	config.put(MailConstants.ASSIGNEE, propertiesMap.get(MailConstants.ASSIGNEE));
	config.put(MailConstants.USER, propertiesMap.get(MailConstants.USER));
	config.put(MailConstants.SOURCE_LANGUAGE, propertiesMap.get(MailConstants.SOURCE_LANGUAGE));
	config.put(MailConstants.NUMBER_OF_TERMS, numberOfTerms);

	config.put(MailConstants.TARGET_LANGUAGE, targetLanguage);

	return config;
    }

    private MailHelper getMailHelper() {
	return _mailHelper;
    }
}

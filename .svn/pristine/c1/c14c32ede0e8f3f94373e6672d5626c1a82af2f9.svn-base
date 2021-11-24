package org.gs4tr.termmanager.service.notification.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.model.TmNotificationType;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.notification.MailConstants;
import org.gs4tr.termmanager.service.utils.MailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("sendToTranslationNotificationListener")
public class SendToTranslationNotificationListener implements NotifyingMessageListener<BatchMessage> {

    private static final String SERVER_ADDRESS = "serverAddress";

    @Autowired
    private MailHelper _mailHelper;

    private String _serverAddress;

    @Autowired
    private UserProfileService _userProfileService;

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

	Map<String, Set<String>> assigneeLanguages = (Map<String, Set<String>>) propertiesMap
		.get(MailConstants.TARGET_LANGUAGE);

	List<TmUserProfile> users = getUserProfileService().findUsersByUsernames(assigneeLanguages.keySet());

	String classifier = notificationType.getClassifier();

	MailHelper mailHelper = getMailHelper();

	for (TmUserProfile user : users) {
	    if (!mailHelper.isMailNotificationEnabled(user, classifier)) {
		continue;
	    }

	    Map<String, Object> configuration = createMailConfiguration(user, propertiesMap, assigneeLanguages);

	    mailHelper.sendMailNotification(notificationType, configuration, user);
	}
    }

    @Value("${serverAddress}")
    public void setServerAddress(String serverAddress) {
	_serverAddress = serverAddress;
    }

    @Override
    public boolean supports(BatchMessage message) {
	return true;
    }

    private Map<String, Object> createMailConfiguration(TmUserProfile user, Map<String, Object> propertiesMap,
	    Map<String, Set<String>> assigneeLanguages) {
	Map<String, Object> config = new HashMap<String, Object>();

	config.put(SERVER_ADDRESS, getServerAddress());
	config.put(MailConstants.SUBMISSION_NAME, propertiesMap.get(MailConstants.SUBMISSION_NAME));
	config.put(MailConstants.USER, propertiesMap.get(MailConstants.USER));
	config.put(MailConstants.JOB_COMMENT, propertiesMap.get(MailConstants.JOB_COMMENT));
	config.put(MailConstants.SOURCE_LANGUAGE, propertiesMap.get(MailConstants.SOURCE_LANGUAGE));
	config.put(MailConstants.NUMBER_OF_TERMS, propertiesMap.get(MailConstants.NUMBER_OF_TERMS));

	Set<String> targetLanguages = assigneeLanguages.get(user.getUserName());

	String targetLanguagesString = NotificationListenerHelper.buildTargetLanguagesString(targetLanguages);

	config.put(MailConstants.TARGET_LANGUAGE, targetLanguagesString);

	return config;
    }

    private MailHelper getMailHelper() {
	return _mailHelper;
    }
}

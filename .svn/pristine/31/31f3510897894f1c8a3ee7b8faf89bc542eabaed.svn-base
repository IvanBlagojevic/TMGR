package org.gs4tr.termmanager.model;

import java.util.ArrayList;
import java.util.List;

public enum TmNotificationType {

    DELETED(false, true, "deleted"),

    NOTIFICATION_REPORT(true, false, "notificationReport"),

    READY_FOR_TRANSLATION(true, false, "readyForTranslation"),

    TRANSLATION_CANCELED(true, false, "translationCanceled"),

    TRANSLATION_COMPLETED(true, false, "translationCompleted");

    public static List<TmNotificationType> getNotificationTypes() {
	List<TmNotificationType> notificationTypes = new ArrayList<TmNotificationType>();
	notificationTypes.add(READY_FOR_TRANSLATION);
	notificationTypes.add(TRANSLATION_CANCELED);
	notificationTypes.add(TRANSLATION_COMPLETED);

	return notificationTypes;
    }

    private String _classifier;

    private boolean _mailNotification;

    private boolean _popupNotication;

    private TmNotificationType(boolean mailNotification, boolean popupNotification, String classifier) {
	_mailNotification = mailNotification;
	_popupNotication = popupNotification;
	_classifier = classifier;
    }

    public String getClassifier() {
	return _classifier;
    }

    public boolean isMailNotification() {
	return _mailNotification;
    }

    public boolean isPopupNotication() {
	return _popupNotication;
    }
}

package org.gs4tr.termmanager.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.Identifiable;
import org.hibernate.annotations.Type;

//@Entity
//@Table(name = "TERM_NOTIFICATION")
public class Notification implements Identifiable<Long> {

    private static final String DEFAULT_NOTIFICATION_TEXT = "There was a problem with last term manager action.";
    private static final long serialVersionUID = -821426150349134800L;
    private String _classifier;

    private String _compoundNotificationText = DEFAULT_NOTIFICATION_TEXT;

    private Date _notificationDate;

    private Long _notificationId;

    // not persistent property
    private NotificationPriority _notificationPriority;

    private String _notificationText = DEFAULT_NOTIFICATION_TEXT;

    public Notification() {
    }

    public Notification(Date notificationDate, String notificationText) {
	_notificationDate = notificationDate;
	_notificationText = notificationText;
    }

    public Notification(Notification notification) {
	_notificationDate = notification.getNotificationDate();
	_notificationText = notification.getNotificationText();
	_classifier = notification.getClassifier();
	if (notification.getCompoundNotificationText() != null) {
	    _compoundNotificationText = notification.getCompoundNotificationText();
	}
    }

    @Override
    public boolean equals(Object obj) {
	if (Objects.isNull(obj)) {
	    return false;
	}

	if (this == obj) {
	    return true;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}

	Notification notification = (Notification) obj;

	return notification.getNotificationText().equals(this._notificationText)
		&& notification.getCompoundNotificationText().equals(this._compoundNotificationText)
		&& notification.getClassifier().equals(this._classifier);
    }

    @Column(name = "NOTIFICATION_CLASSIFIER", nullable = false, updatable = false)
    public String getClassifier() {
	return _classifier;
    }

    @Column(name = "NOTIFICATION_COMPOUND_TEXT", nullable = false, updatable = false)
    @Type(type = "text")
    public String getCompoundNotificationText() {
	return _compoundNotificationText;
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getNotificationId();
    }

    @Column(name = "NOTIFICATION_DATE", nullable = false, updatable = false)
    @Type(type = "timestamp")
    public Date getNotificationDate() {
	return _notificationDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID", updatable = false)
    public Long getNotificationId() {
	return _notificationId;
    }

    @Transient
    public NotificationPriority getNotificationPriority() {
	return _notificationPriority;
    }

    @Column(name = "NOTIFICATION_TEXT", nullable = false, updatable = false)
    @Type(type = "text")
    public String getNotificationText() {
	return _notificationText;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;

	result = prime * result + ((_compoundNotificationText == null) ? 0 : _compoundNotificationText.hashCode());
	result = prime * result + ((_notificationText == null) ? 0 : _notificationText.hashCode());
	return prime * result + ((_classifier == null) ? 0 : _classifier.hashCode());
    }

    public void setClassifier(String classifier) {
	_classifier = classifier;
    }

    public void setCompoundNotificationText(String compoundNotificationText) {
	_compoundNotificationText = compoundNotificationText;
    }

    @Override
    public void setIdentifier(Long id) {
	setNotificationId(id);
    }

    public void setNotificationDate(Date notificationDate) {
	_notificationDate = notificationDate;
    }

    public void setNotificationId(Long notificationId) {
	_notificationId = notificationId;
    }

    public void setNotificationPriority(NotificationPriority notificationPriority) {
	_notificationPriority = notificationPriority;
    }

    public void setNotificationText(String notificationText) {
	if (StringUtils.isNotBlank(notificationText)) {
	    _notificationText = notificationText;
	}
    }

}

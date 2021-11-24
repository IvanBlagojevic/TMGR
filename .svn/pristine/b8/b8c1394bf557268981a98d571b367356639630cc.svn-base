package org.gs4tr.termmanager.model;

public enum TmNotificationReportType {

    ADDED_APPROVED_TERM(true, false, "addedApprovedTerm"),

    ADDED_PENDING_TERM(true, false, "addedPendingTerm"),

    APPROVE_TERM(true, false, "approveTerm"),

    DELETED_TERM(true, false, "deletedTerm"),

    DEMOTE_TERM(true, false, "demoteTerm"),

    ADDED_BLACKLISTED_TERM(true, false, "addedBlacklistedTerm"),

    BLACKLIST_TERM(true, false, "blacklistTerm"),

    ADDED_ON_HOLD_TERM(true, false, "addedOnHoldTerm"),

    ON_HOLD_TERM(true, false, "onHoldTerm"),

    EDIT_TERM(true, false, "editTerm");

    private String _classifier;

    private boolean _mailNotification;

    private boolean _popupNotication;

    private TmNotificationReportType(boolean mailNotification, boolean popupNotification, String classifier) {
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

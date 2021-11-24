package org.gs4tr.termmanager.model;

public enum ProjectPolicyEnum {

    POLICY_TM_ADD_COMMENT,

    POLICY_TM_CANCEL_TRANSLATION,

    POLICY_TM_PROJECT_REPORT,

    POLICY_TM_READ,

    POLICY_TM_SEND_TO_TRANSLATION_REVIEW,

    POLICY_TM_TERM_ADD_APPROVED_TERM,

    POLICY_TM_TERM_ADD_PENDING_TERM,

    POLICY_TM_TERM_ADD_ON_HOLD_TERM,

    POLICY_TM_TERM_ADD_BLACKLIST_TERM,

    POLICY_TM_TERM_APPROVE_TERM_STATUS,

    POLICY_TM_TERM_AUTO_SAVE_TRANSLATION,

    POLICY_TM_TERM_COMMIT_TRANSLATION_CHANGES,

    POLICY_TM_TERM_DEMOTE_TERM_STATUS,

    POLICY_TM_TERM_ON_HOLD_TERM_STATUS,

    POLICY_TM_TERM_DEMOTE_TO_ON_HOLD_TERM_STATUS,

    POLICY_TM_TERM_DEMOTE_TO_PENDING_APPROVAL_TERM_STATUS,

    POLICY_TM_TERM_DISABLE_TERM,

    POLICY_TM_TERM_UNDO_TRANSLATION_CHANGES,

    POLICY_TM_TERM_VIEW_TERM_HISTORY,

    POLICY_TM_TERM_VIEW_TERM_HISTORY_USERS,

    POLICY_TM_TERMENTRY_ASSIGN_ATTRIBUTES,

    POLICY_TM_TERMENTRY_EXPORT,

    POLICY_TM_TERMENTRY_FORBID_TERMENTRY,

    POLICY_TM_TERMENTRY_IMPORT,

    POLICY_TM_ADD_EDIT_PROJECT_DESCRIPTION,

    POLICY_TM_MERGE_TERM_ENTRIES,

    POLICY_TM_USER_SEND_CONNECTION;

    private String _category;

    ProjectPolicyEnum() {
	_category = Messages.getString(this.name());
    }

    public String getCategory() {
	return _category;
    }
}

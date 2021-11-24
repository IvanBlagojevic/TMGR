package org.gs4tr.termmanager.model;

import org.gs4tr.foundation.modules.security.model.PolicyEnum;

public enum TmPolicyEnum implements PolicyEnum {

    POLICY_TM_MENU_TERMS,

    POLICY_TM_PROJECT_SEND_CONNECTION,

    POLICY_TM_VIEW_TRANSLATOR_INBOX;

    public static PolicyEnum[] policies() {
	return values();
    }

    private String _category;

    TmPolicyEnum() {
	_category = Messages.getString(this.name());
    }

    @Override
    public String getCategory() {
	return _category;
    }
}

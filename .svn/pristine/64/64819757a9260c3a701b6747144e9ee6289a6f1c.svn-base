package org.gs4tr.termmanager.webmvc.model.search;

public enum SearchCriteria {

    ATTRIBUTES_ONLY("attributes", ControlType.RADIO_BOX),

    BOTH("both", ControlType.RADIO_BOX),

    CREATION_USERS("creationUsers", ControlType.COMBO_MULTI),

    TL_CREATION_USERS("creationUsers", ControlType.MULTI),

    DATE_COMPLETED_RANGE("dateCompletedRange", ControlType.DATE_RANGE),

    DATE_CREATED_RANGE("dateCreatedRange", ControlType.DATE_RANGE),

    TL_DATE_CREATED_RANGE("dateCreatedRange", ControlType.DATE),

    DATE_MODIFIED_RANGE("dateModifiedRange", ControlType.DATE_RANGE),

    TL_DATE_MODIFIED_RANGE("dateModifiedRange", ControlType.DATE),

    ENTITY_TYPE("entityType", ControlType.COMBO_MULTI_DOUBLE),

    TL_ENTITY_TYPE("entityType", ControlType.MULTI),

    HIDE_BLANKS("hideBlanks", ControlType.COMBO_MULTI),

    TL_HIDE_BLANKS("hideBlanks", ControlType.MULTI),

    INCLUDE_BLACKLISTED_TERMS("includeBlackListedTerms", ControlType.CHECK_BOX),

    LANGUAGE_DIRECTION("languageDirection", ControlType.COMBO_SINGLE_AND_COMBO_MULTI_LINKED),

    TL_LANGUAGE_DIRECTION("languageDirection", ControlType.MULTI_AND_LIST),

    LANGUAGE_DIRECTION_SUBMISSION("languageDirectionSubmission", ControlType.COMBO_MULTI_DOUBLE),

    LANGUAGE_SET_NAME("languageSetNameInputText", ControlType.INPUT),

    LANGUAGES("languages", ControlType.COMBO_MULTI),

    MODIFICATION_USERS("modificationUsers", ControlType.COMBO_MULTI),

    TL_MODIFICATION_USERS("modificationUsers", ControlType.MULTI),

    ORGANIZATION_NAME("organizationNameInputText", ControlType.INPUT),

    PROJECT_LIST("projectComboBox", ControlType.COMBO_MULTI),

    TL_PROJECT_LIST("projectComboBox", ControlType.MULTI),

    PROJECT_NAME("projectNameInputText", ControlType.INPUT),

    PROJECT_SHORT_CODE("projectShortCodeInputText", ControlType.INPUT),

    ROLE_NAME("roleNameInputText", ControlType.INPUT),

    SEARCH_ONLY_TARGET("searchOnlyTarget", ControlType.CHECK_BOX),

    SEARCH_TYPE("searchType", ControlType.COMBO_CHECK_BOX),

    SEARCH_TYPE_WITHOUT_TARGET_ONLY("searchType", ControlType.COMBO_CHECK_BOX),

    STATUSES_SUBMISSION("submissionStatuses", ControlType.COMBO_MULTI),

    STATUSES_SUBMISSION_SUBMITTER("submissionStatusesSubmitter", ControlType.COMBO_MULTI),

    SUBMISSION_NAME("submissionNameInputText", ControlType.INPUT),

    SUBMISSION_PROJECT_LIST("submissionProjectComboBox", ControlType.COMBO_MULTI),

    SUBMISSION_STATUSES("statuses", ControlType.COMBO_MULTI),

    SUBMISSION_TERM_LANGUAGE("submissionTermLanguage", ControlType.COMBO_SINGLE),

    SUBMISSION_USERS("submissionUsers", ControlType.COMBO_MULTI_DOUBLE),

    TARGET_LANGUAGE_LIST("targetLanguageComboBox", ControlType.COMBO_SINGLE),

    TERM_ENTRY_DATE_CREATED_RANGE("termEntryDateCreatedRange", ControlType.DATE_RANGE),

    TERM_NAME("termNameAndSearchType", ControlType.TEXT_AND_COMBO_SINGLE),

    TL_TERM_NAME("termNameAndSearchType", ControlType.TEXT_AND_LIST),

    TERM_ONLY("terms", ControlType.RADIO_BOX),

    TERM_STATUS_WITHOUT_MISSINGTRANSLATION("termStatus", ControlType.COMBO_SINGLE),

    TERM_STATUSES("statuses", ControlType.COMBO_MULTI),

    TL_TERM_STATUSES("statuses", ControlType.MULTI),

    USE_SIMPLE_FILTER("useSimpleFilter", null),

    USER_FIRST_NAME("firstNameInputText", ControlType.INPUT),

    USER_LAST_NAME("lastNameInputText", ControlType.INPUT),

    USER_NAME("userNameInputText", ControlType.INPUT),

    EMAIL_ADDRESS("emailAddressInputText", ControlType.INPUT);

    private final String _command;

    private ControlType _controlType;

    private SearchCriteria(String command, ControlType controlType) {
	_command = command;
	_controlType = controlType;
    }

    public String getControlName() {
	return _command;
    }

    public ControlType getControlType() {
	return _controlType;
    }
}

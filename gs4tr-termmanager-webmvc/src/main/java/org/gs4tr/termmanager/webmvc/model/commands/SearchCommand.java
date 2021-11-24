package org.gs4tr.termmanager.webmvc.model.commands;

import java.util.List;

import org.gs4tr.termmanager.webmvc.model.search.DoubleMultiComboBoxDefaultValue;
import org.gs4tr.termmanager.webmvc.model.search.InputTextAndComboItem;
import org.gs4tr.termmanager.webmvc.model.search.LinkedComboBoxDefaultValue;

public class SearchCommand extends ItemSearchGridCommand {

    private List<String> _creationUsers;

    private DateRange _dateCompletedRange;

    private DateRange _dateCreatedRange;

    private DateRange _dateModifiedRange;

    private String _emailAddressInputText;

    private DoubleMultiComboBoxDefaultValue _entityType;

    private List<SearchItem> _filterSearch;

    private String _firstNameInputText;

    private List<String> _hideBlanks;

    private LinkedComboBoxDefaultValue _languageDirection;

    private DoubleMultiComboBoxDefaultValue _languageDirectionSubmission;

    private String _languageSetNameInputText;

    private String _lastNameInputText;

    private List<String> _modificationUsers;

    private String _organizationNameInputText;

    private List<String> _projectComboBox;

    private String _projectNameInputText;

    private String _projectShortCodeInputText;

    private String _roleNameInputText;

    private boolean _searchUserLatestChanges = false;

    private String _sourceLanguage;

    private List<String> _statuses;

    private String _submissionNameInputText;

    private List<String> _submissionProjectComboBox;

    private String _submissionTermLanguage;

    private String _submissionTicket;

    private DoubleMultiComboBoxDefaultValue _submissionUsers;

    private InputTextAndComboItem _termNameAndSearchType;

    private String _ticket;

    private String _userNameInputText;

    public List<String> getCreationUsers() {
	return _creationUsers;
    }

    public DateRange getDateCompletedRange() {
	return _dateCompletedRange;
    }

    public DateRange getDateCreatedRange() {
	return _dateCreatedRange;
    }

    public DateRange getDateModifiedRange() {
	return _dateModifiedRange;
    }

    public String getEmailAddressInputText() {
	return _emailAddressInputText;
    }

    public DoubleMultiComboBoxDefaultValue getEntityType() {
	return _entityType;
    }

    public List<SearchItem> getFilterSearch() {
	return _filterSearch;
    }

    public String getFirstNameInputText() {
	return _firstNameInputText;
    }

    public List<String> getHideBlanks() {
	return _hideBlanks;
    }

    public LinkedComboBoxDefaultValue getLanguageDirection() {
	return _languageDirection;
    }

    public DoubleMultiComboBoxDefaultValue getLanguageDirectionSubmission() {
	return _languageDirectionSubmission;
    }

    public String getLanguageSetNameInputText() {
	return _languageSetNameInputText;
    }

    public String getLastNameInputText() {
	return _lastNameInputText;
    }

    public List<String> getModificationUsers() {
	return _modificationUsers;
    }

    public String getOrganizationNameInputText() {
	return _organizationNameInputText;
    }

    public List<String> getProjectComboBox() {
	return _projectComboBox;
    }

    public String getProjectNameInputText() {
	return _projectNameInputText;
    }

    public String getProjectShortCodeInputText() {
	return _projectShortCodeInputText;
    }

    public String getRoleNameInputText() {
	return _roleNameInputText;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public List<String> getStatuses() {
	return _statuses;
    }

    public String getSubmissionNameInputText() {
	return _submissionNameInputText;
    }

    public List<String> getSubmissionProjectComboBox() {
	return _submissionProjectComboBox;
    }

    public String getSubmissionTermLanguage() {
	return _submissionTermLanguage;
    }

    public String getSubmissionTicket() {
	return _submissionTicket;
    }

    public DoubleMultiComboBoxDefaultValue getSubmissionUsers() {
	return _submissionUsers;
    }

    public InputTextAndComboItem getTermNameAndSearchType() {
	return _termNameAndSearchType;
    }

    public String getTicket() {
	return _ticket;
    }

    public String getUserNameInputText() {
	return _userNameInputText;
    }

    public boolean isSearchUserLatestChanges() {
	return _searchUserLatestChanges;
    }

    public void setCreationUsers(List<String> creationUsers) {
	_creationUsers = creationUsers;
    }

    public void setDateCompletedRange(DateRange dateCompletedRange) {
	_dateCompletedRange = dateCompletedRange;
    }

    public void setDateCreatedRange(DateRange dateCreatedRange) {
	_dateCreatedRange = dateCreatedRange;
    }

    public void setDateModifiedRange(DateRange dateModifiedRange) {
	_dateModifiedRange = dateModifiedRange;
    }

    public void setEmailAddressInputText(String emailAddressInputText) {
	_emailAddressInputText = emailAddressInputText;
    }

    public void setEntityType(DoubleMultiComboBoxDefaultValue entityType) {
	_entityType = entityType;
    }

    public void setFilterSearch(List<SearchItem> filterSearch) {
	_filterSearch = filterSearch;
    }

    public void setFirstNameInputText(String firstNameInputText) {
	_firstNameInputText = firstNameInputText;
    }

    public void setHideBlanks(List<String> hideBlanks) {
	_hideBlanks = hideBlanks;
    }

    public void setLanguageDirection(LinkedComboBoxDefaultValue languageDirection) {
	_languageDirection = languageDirection;
    }

    public void setLanguageDirectionSubmission(DoubleMultiComboBoxDefaultValue languageDirectionSubmission) {
	_languageDirectionSubmission = languageDirectionSubmission;
    }

    public void setLanguageSetNameInputText(String languageSetNameInputText) {
	_languageSetNameInputText = languageSetNameInputText;
    }

    public void setLastNameInputText(String lastNameInputText) {
	_lastNameInputText = lastNameInputText;
    }

    public void setModificationUsers(List<String> modificationUsers) {
	_modificationUsers = modificationUsers;
    }

    public void setOrganizationNameInputText(String organizationNameInputText) {
	_organizationNameInputText = organizationNameInputText;
    }

    public void setProjectComboBox(List<String> projectComboBox) {
	_projectComboBox = projectComboBox;
    }

    public void setProjectNameInputText(String projectNameInputText) {
	_projectNameInputText = projectNameInputText;
    }

    public void setProjectShortCodeInputText(String projectShortCodeInputText) {
	_projectShortCodeInputText = projectShortCodeInputText;
    }

    public void setRoleNameInputText(String roleNameInputText) {
	_roleNameInputText = roleNameInputText;
    }

    public void setSearchUserLatestChanges(boolean searchUserLatestChanges) {
	_searchUserLatestChanges = searchUserLatestChanges;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setStatuses(List<String> statuses) {
	_statuses = statuses;
    }

    public void setSubmissionNameInputText(String submissionNameInputText) {
	_submissionNameInputText = submissionNameInputText;
    }

    public void setSubmissionProjectComboBox(List<String> submissionProjectComboBox) {
	_submissionProjectComboBox = submissionProjectComboBox;
    }

    public void setSubmissionTermLanguage(String submissionTermLanguage) {
	_submissionTermLanguage = submissionTermLanguage;
    }

    public void setSubmissionTicket(String submissionTicket) {
	_submissionTicket = submissionTicket;
    }

    public void setSubmissionUsers(DoubleMultiComboBoxDefaultValue submissionUsers) {
	_submissionUsers = submissionUsers;
    }

    public void setTermNameAndSearchType(InputTextAndComboItem termNameAndSearchType) {
	_termNameAndSearchType = termNameAndSearchType;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }

    public void setUserNameInputText(String userNameInputText) {
	_userNameInputText = userNameInputText;
    }
}

package org.gs4tr.termmanager.model.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectInfoDetailsDto {

    private Long _activeSubmissionCount;

    private Long _completedSubmissionCount;

    private java.util.Date _dateModified;

    private Map<String, Long> _languageActiveSubmissionCount;

    private Map<String, Long> _languageApprovedTermCount;

    private Map<String, Long> _languageCompletedSubmissionCount;

    private Map<String, Long> _languageForbiddenTermCount;

    private Map<String, Long> _languageOnHoldTermCount;

    private Map<String, Long> _languagePendingTermCount;

    private Map<String, Long> _languageTermCount;

    private Map<String, Long> _languageTermInSubmissionCount;

    private Long _projectId;

    private Long _termEntryCount;

    private Set<String> _updatedLanguages;

    private Set<Long> _updatedUserIds;

    private Map<Long, Long> _userActiveSubmissionCount;

    private Map<Long, Long> _userCompletedSubmissionCount;

    private Map<Long, Map<String, Long>> _userLanguageActiveSubmissionCount;

    private Map<Long, Map<String, Long>> _userLanguageCompletedSubmissionCount;

    private Map<Long, Long> _userTermEntryCount;

    public ProjectInfoDetailsDto() {
    }

    public ProjectInfoDetailsDto(Long projectId) {

	_projectId = projectId;

	_activeSubmissionCount = 0L;
	_completedSubmissionCount = 0L;

	_languageApprovedTermCount = new HashMap<>();
	_languageActiveSubmissionCount = new HashMap<>();
	_languageCompletedSubmissionCount = new HashMap<>();
	_languageForbiddenTermCount = new HashMap<>();
	_languageTermCount = new HashMap<>();
	_languageTermInSubmissionCount = new HashMap<>();

	_languageOnHoldTermCount = new HashMap<>();
	_languagePendingTermCount = new HashMap<>();

	_termEntryCount = 0L;

	_userTermEntryCount = new HashMap<>();
	_userActiveSubmissionCount = new HashMap<>();
	_userCompletedSubmissionCount = new HashMap<>();
	_userLanguageActiveSubmissionCount = new HashMap<>();
	_userLanguageCompletedSubmissionCount = new HashMap<>();

	_updatedLanguages = new HashSet<>();
	_updatedUserIds = new HashSet<>();
    }

    public Long getActiveSubmissionCount() {
	return _activeSubmissionCount;
    }

    public Long getCompletedSubmissionCount() {
	return _completedSubmissionCount;
    }

    public java.util.Date getDateModified() {
	return _dateModified;
    }

    public Map<String, Long> getLanguageActiveSubmissionCount() {
	return _languageActiveSubmissionCount;
    }

    public Map<String, Long> getLanguageApprovedTermCount() {
	return _languageApprovedTermCount;
    }

    public Map<String, Long> getLanguageCompletedSubmissionCount() {
	return _languageCompletedSubmissionCount;
    }

    public long getLanguageCount(String languageId, Map<String, Long> languageCount) {
	return languageCount.getOrDefault(languageId, 0L);
    }

    public Map<String, Long> getLanguageForbiddenTermCount() {
	return _languageForbiddenTermCount;
    }

    public Map<String, Long> getLanguageOnHoldTermCount() {
	return _languageOnHoldTermCount;
    }

    public Map<String, Long> getLanguagePendingTermCount() {
	return _languagePendingTermCount;
    }

    public Map<String, Long> getLanguageTermCount() {
	return _languageTermCount;
    }

    public Map<String, Long> getLanguageTermInSubmissionCount() {
	return _languageTermInSubmissionCount;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public Long getTermEntryCount() {
	return _termEntryCount;
    }

    public long getTotalCount(Map<String, Long> langCount) {
	return langCount.values().stream().mapToLong(Long::longValue).sum();
    }

    public Set<String> getUpdatedLanguages() {
	return _updatedLanguages;
    }

    public Set<Long> getUpdatedUserIds() {
	return _updatedUserIds;
    }

    public Map<Long, Long> getUserActiveSubmissionCount() {
	return _userActiveSubmissionCount;
    }

    public Map<Long, Long> getUserCompletedSubmissionCount() {
	return _userCompletedSubmissionCount;
    }

    public Map<Long, Map<String, Long>> getUserLanguageActiveSubmissionCount() {
	return _userLanguageActiveSubmissionCount;
    }

    public Map<Long, Map<String, Long>> getUserLanguageCompletedSubmissionCount() {
	return _userLanguageCompletedSubmissionCount;
    }

    public Map<Long, Long> getUserTermEntryCount() {
	return _userTermEntryCount;
    }

    public long getUserTotalCount(Long userId, Map<Long, Long> userCount) {
	return userCount.getOrDefault(userId, 0L);
    }

    public void setActiveSubmissionCount(Long activeSubmissionCount) {
	_activeSubmissionCount = activeSubmissionCount;
    }

    public void setCompletedSubmissionCount(Long completedSubmissionCount) {
	_completedSubmissionCount = completedSubmissionCount;
    }

    public void setDateModified(Date dateModified) {
	_dateModified = dateModified;
    }

    public void setLanguageActiveSubmissionCount(Map<String, Long> languageActiveSubmissionCount) {
	_languageActiveSubmissionCount = languageActiveSubmissionCount;
    }

    public void setLanguageApprovedTermCount(Map<String, Long> languageApprovedTermCount) {
	_languageApprovedTermCount = languageApprovedTermCount;
    }

    public void setLanguageCompletedSubmissionCount(Map<String, Long> languageCompletedSubmissionCount) {
	_languageCompletedSubmissionCount = languageCompletedSubmissionCount;
    }

    public void setLanguageForbiddenTermCount(Map<String, Long> languageForbiddenTermCount) {
	_languageForbiddenTermCount = languageForbiddenTermCount;
    }

    public void setLanguageOnHoldTermCount(Map<String, Long> languageOnHoldTermCount) {
	_languageOnHoldTermCount = languageOnHoldTermCount;
    }

    public void setLanguagePendingTermCount(Map<String, Long> languagePendingTermCount) {
	_languagePendingTermCount = languagePendingTermCount;
    }

    public void setLanguageTermCount(Map<String, Long> languageTermCount) {
	_languageTermCount = languageTermCount;
    }

    public void setLanguageTermInSubmissionCount(Map<String, Long> languageTermInSubmissionCount) {
	_languageTermInSubmissionCount = languageTermInSubmissionCount;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setTermEntryCount(Long termEntryCount) {
	_termEntryCount = termEntryCount;
    }

    public void setUpdatedLanguages(Set<String> updatedLanguages) {
	_updatedLanguages = updatedLanguages;
    }

    public void setUpdatedUserIds(Set<Long> updatedUserIds) {
	_updatedUserIds = updatedUserIds;
    }

    public void setUserActiveSubmissionCount(Map<Long, Long> userActiveSubmissionCount) {
	_userActiveSubmissionCount = userActiveSubmissionCount;
    }

    public void setUserCompletedSubmissionCount(Map<Long, Long> userCompletedSubmissionCount) {
	_userCompletedSubmissionCount = userCompletedSubmissionCount;
    }

    public void setUserLanguageActiveSubmissionCount(Map<Long, Map<String, Long>> userLanguageActiveSubmissionCount) {
	_userLanguageActiveSubmissionCount = userLanguageActiveSubmissionCount;
    }

    public void setUserLanguageCompletedSubmissionCount(
	    Map<Long, Map<String, Long>> userLanguageCompletedSubmissionCount) {
	_userLanguageCompletedSubmissionCount = userLanguageCompletedSubmissionCount;
    }

    public void setUserTermEntryCount(Map<Long, Long> userTermEntryCount) {
	_userTermEntryCount = userTermEntryCount;
    }

}

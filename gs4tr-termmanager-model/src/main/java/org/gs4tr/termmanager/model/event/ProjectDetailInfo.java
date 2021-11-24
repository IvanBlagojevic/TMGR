package org.gs4tr.termmanager.model.event;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectDetailInfo {

    private final AtomicLong _activeSubmissionCount;

    private final AtomicLong _completedSubmissionCount;

    private Date _dateModified;

    private final Map<String, AtomicLong> _languageActiveSubmissionCount;

    private final Map<String, AtomicLong> _languageApprovedTermCount;

    private final Map<String, AtomicLong> _languageCompletedSubmissionCount;

    private final Map<String, AtomicLong> _languageForbiddenTermCount;

    private final Map<String, AtomicLong> _languageOnHoldTermCount;

    private final Map<String, AtomicLong> _languagePendingTermCount;

    private final Map<String, AtomicLong> _languageTermCount;

    private final Map<String, AtomicLong> _languageTermInSubmissionCount;

    private final Long _projectId;

    private final AtomicLong _termEntryCount;

    private final Set<String> _updatedLanguages;

    private final Set<Long> _updatedUserIds;

    private final Map<Long, AtomicLong> _userActiveSubmissionCount;

    private final Map<Long, AtomicLong> _userCompletedSubmissionCount;

    private final Map<Long, Map<String, AtomicLong>> _userLanguageActiveSubmissionCount;

    private final Map<Long, Map<String, AtomicLong>> _userLanguageCompletedSubmissionCount;

    private final Map<Long, AtomicLong> _userTermEntryCount;

    public ProjectDetailInfo(Long projectId) {
	_projectId = projectId;

	_activeSubmissionCount = new AtomicLong();
	_completedSubmissionCount = new AtomicLong();

	_languageApprovedTermCount = new HashMap<>();
	_languageActiveSubmissionCount = new HashMap<>();
	_languageCompletedSubmissionCount = new HashMap<>();
	_languageForbiddenTermCount = new HashMap<>();
	_languageTermCount = new HashMap<>();
	_languageTermInSubmissionCount = new HashMap<>();

	_languageOnHoldTermCount = new HashMap<>();
	_languagePendingTermCount = new HashMap<>();

	_termEntryCount = new AtomicLong();

	_userTermEntryCount = new HashMap<>();
	_userActiveSubmissionCount = new HashMap<>();
	_userCompletedSubmissionCount = new HashMap<>();
	_userLanguageActiveSubmissionCount = new HashMap<>();
	_userLanguageCompletedSubmissionCount = new HashMap<>();

	_updatedLanguages = new HashSet<>();
	_updatedUserIds = new HashSet<>();
    }

    public long addApprovedTermCount(String languageId, long value) {
	return add(languageId, getLanguageApprovedTermCount(), value);
    }

    public long addForbiddenTermCount(String languageId, long value) {
	return add(languageId, getLanguageForbiddenTermCount(), value);
    }

    public long addOnHoldTermCount(String languageId, long value) {
	return add(languageId, getLanguageOnHoldTermCount(), value);
    }

    public long addPendingTermCount(String languageId, long value) {
	return add(languageId, getLanguagePendingTermCount(), value);
    }

    public long addTermCount(String languageId, long value) {
	return add(languageId, getLanguageTermCount(), value);
    }

    public long addTermEntryCount(long value) {
	return getTermEntryCount().addAndGet(value);
    }

    public long addTermInSubmissionCount(String languageId, long value) {
	return add(languageId, getLanguageTermInSubmissionCount(), value);
    }

    public void addUpdatedLanguage(String language) {
	_updatedLanguages.add(language);
    }

    public void addUpdatedLanguage(Long userId) {
	_updatedUserIds.add(userId);
    }

    public long decrementActiveSubmissionCount() {
	return getActiveSubmissionCount().decrementAndGet();
    }

    public long decrementActiveSubmissionCount(Long userId) {
	addUpdatedLanguage(userId);
	return decrement(userId, getUserActiveSubmissionCount());
    }

    public void decrementActiveSubmissionCount(Long userId, String languageId) {
	addUpdatedLanguage(userId);
	Map<String, AtomicLong> languageMap = getUserLanguageActiveSubmissionCount().computeIfAbsent(userId,
		k -> new HashMap<>());
	decrement(languageId, languageMap);
    }

    public long decrementActiveSubmissionCount(String languageId) {
	return decrement(languageId, getLanguageActiveSubmissionCount());
    }

    public long decrementApprovedTermCount(String languageId) {
	return decrement(languageId, getLanguageApprovedTermCount());
    }

    public long decrementForbiddenTermCount(String languageId) {
	return decrement(languageId, getLanguageForbiddenTermCount());
    }

    public long decrementOnHoldTermCount(String languageId) {
	return decrement(languageId, getLanguageOnHoldTermCount());
    }

    public long decrementPendingTermCount(String languageId) {
	return decrement(languageId, getLanguagePendingTermCount());
    }

    public long decrementTermCount(String languageId) {
	return decrement(languageId, getLanguageTermCount());
    }

    public long decrementTermEntryCount() {
	return getTermEntryCount().decrementAndGet();
    }

    public long decrementTermEntryCount(Long userId) {
	addUpdatedLanguage(userId);
	return decrement(userId, getUserTermEntryCount());
    }

    public long decrementTermInSubmissionCount(String languageId) {
	return decrement(languageId, getLanguageTermInSubmissionCount());
    }

    public AtomicLong getActiveSubmissionCount() {
	return _activeSubmissionCount;
    }

    public AtomicLong getCompletedSubmissionCount() {
	return _completedSubmissionCount;
    }

    public Date getDateModified() {
	return _dateModified;
    }

    public Map<String, AtomicLong> getLanguageActiveSubmissionCount() {
	return _languageActiveSubmissionCount;
    }

    public Map<String, AtomicLong> getLanguageApprovedTermCount() {
	return _languageApprovedTermCount;
    }

    public Map<String, AtomicLong> getLanguageCompletedSubmissionCount() {
	return _languageCompletedSubmissionCount;
    }

    public long getLanguageCount(String languageId, Map<String, AtomicLong> languageCount) {
	return languageCount.getOrDefault(languageId, new AtomicLong()).get();
    }

    public Map<String, AtomicLong> getLanguageForbiddenTermCount() {
	return _languageForbiddenTermCount;
    }

    public Map<String, AtomicLong> getLanguageOnHoldTermCount() {
	return _languageOnHoldTermCount;
    }

    public Map<String, AtomicLong> getLanguagePendingTermCount() {
	return _languagePendingTermCount;
    }

    public Map<String, AtomicLong> getLanguageTermCount() {
	return _languageTermCount;
    }

    public Map<String, AtomicLong> getLanguageTermInSubmissionCount() {
	return _languageTermInSubmissionCount;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public AtomicLong getTermEntryCount() {
	return _termEntryCount;
    }

    public long getTotalCount(Map<String, AtomicLong> langCount) {
	return langCount.values().stream().mapToLong(AtomicLong::get).sum();
    }

    public Set<String> getUpdatedLanguages() {
	return _updatedLanguages;
    }

    public Set<Long> getUpdatedUserIds() {
	return _updatedUserIds;
    }

    public Map<Long, AtomicLong> getUserActiveSubmissionCount() {
	return _userActiveSubmissionCount;
    }

    public Map<Long, AtomicLong> getUserCompletedSubmissionCount() {
	return _userCompletedSubmissionCount;
    }

    public Map<Long, Map<String, AtomicLong>> getUserLanguageActiveSubmissionCount() {
	return _userLanguageActiveSubmissionCount;
    }

    public Map<Long, Map<String, AtomicLong>> getUserLanguageCompletedSubmissionCount() {
	return _userLanguageCompletedSubmissionCount;
    }

    public Map<Long, AtomicLong> getUserTermEntryCount() {
	return _userTermEntryCount;
    }

    public long getUserTotalCount(Long userId, Map<Long, AtomicLong> userCount) {
	return userCount.getOrDefault(userId, new AtomicLong()).get();
    }

    public long incrementActiveSubmissionCount() {
	return getActiveSubmissionCount().incrementAndGet();
    }

    public long incrementActiveSubmissionCount(Long userId) {
	addUpdatedLanguage(userId);
	return increment(userId, getUserActiveSubmissionCount());
    }

    public void incrementActiveSubmissionCount(Long userId, String languageId) {
	addUpdatedLanguage(userId);
	Map<String, AtomicLong> languageMap = getUserLanguageActiveSubmissionCount().computeIfAbsent(userId,
		k -> new HashMap<>());
	increment(languageId, languageMap);
    }

    public long incrementActiveSubmissionCount(String languageId) {
	return increment(languageId, getLanguageActiveSubmissionCount());
    }

    public long incrementApprovedTermCount(String languageId) {
	return increment(languageId, getLanguageApprovedTermCount());
    }

    public long incrementCompletedSubmissionCount() {
	return getCompletedSubmissionCount().incrementAndGet();
    }

    public long incrementCompletedSubmissionCount(Long userId) {
	addUpdatedLanguage(userId);
	return increment(userId, getUserCompletedSubmissionCount());
    }

    public void incrementCompletedSubmissionCount(Long userId, String languageId) {
	addUpdatedLanguage(userId);
	Map<String, AtomicLong> languageMap = getUserLanguageCompletedSubmissionCount().computeIfAbsent(userId,
		k -> new HashMap<>());
	increment(languageId, languageMap);
    }

    public long incrementCompletedSubmissionCount(String languageId) {
	return increment(languageId, getLanguageCompletedSubmissionCount());
    }

    public long incrementForbiddenTermCount(String languageId) {
	return increment(languageId, getLanguageForbiddenTermCount());
    }

    public long incrementOnHoldTermCount(String languageId) {
	return increment(languageId, getLanguageOnHoldTermCount());
    }

    public long incrementPendingTermCount(String languageId) {
	return increment(languageId, getLanguagePendingTermCount());
    }

    public long incrementTermCount(String languageId) {
	return increment(languageId, getLanguageTermCount());
    }

    public long incrementTermEntryCount() {
	return getTermEntryCount().incrementAndGet();
    }

    public long incrementTermEntryCount(Long userId) {
	return increment(userId, getUserTermEntryCount());
    }

    public long incrementTermInSubmissionCount(String languageId) {
	return increment(languageId, getLanguageTermInSubmissionCount());
    }

    public void setDateModified(Date newDateModified) {
	_dateModified = newDateModified;
    }

    private long add(String key, Map<String, AtomicLong> map, long value) {
	addUpdatedLanguage(key);
	AtomicLong count = map.computeIfAbsent(key, k -> new AtomicLong());
	return count.addAndGet(value);
    }

    private long decrement(Long key, Map<Long, AtomicLong> map) {
	addUpdatedLanguage(key);
	AtomicLong count = map.computeIfAbsent(key, k -> new AtomicLong());
	return count.decrementAndGet();
    }

    private long decrement(String key, Map<String, AtomicLong> map) {
	addUpdatedLanguage(key);
	AtomicLong count = map.computeIfAbsent(key, k -> new AtomicLong());
	return count.decrementAndGet();
    }

    private long increment(Long key, Map<Long, AtomicLong> map) {
	addUpdatedLanguage(key);
	AtomicLong count = map.computeIfAbsent(key, k -> new AtomicLong());
	return count.incrementAndGet();
    }

    private long increment(String key, Map<String, AtomicLong> map) {
	addUpdatedLanguage(key);
	AtomicLong count = map.computeIfAbsent(key, k -> new AtomicLong());
	return count.incrementAndGet();
    }
}

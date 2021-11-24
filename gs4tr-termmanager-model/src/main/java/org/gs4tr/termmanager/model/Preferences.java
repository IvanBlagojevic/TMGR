package org.gs4tr.termmanager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class Preferences implements Serializable {
    private static final long serialVersionUID = 5931019165915167061L;

    private String _actionSize;

    private Boolean _actionTextVisible;

    private Integer _dailyHour;

    private Integer _dayOfWeek;

    private Long _defaultProjectId;

    private String _defaultSourceLanguage;

    private String _defaultTargetLanguage;

    private Integer _itemsPerPage = 200;

    private String _language;

    private Integer _refreshPeriod;

    private Integer _weeklyHour;

    public Preferences() {
    }

    public Preferences(Preferences preferences) {

    }

    @Column(name = "ACTION_SIZE", nullable = true, updatable = true)
    public String getActionSize() {
	return _actionSize;
    }

    @Column(name = "ACTION_TEXT_VISIBLE", nullable = true, updatable = true)
    public Boolean getActionTextVisible() {
	return _actionTextVisible;
    }

    @Column(name = "DAILY_HOUR", nullable = true, updatable = true)
    public Integer getDailyHour() {
	return _dailyHour;
    }

    @Column(name = "DAY_OF_WEEK", nullable = true, updatable = true)
    public Integer getDayOfWeek() {
	return _dayOfWeek;
    }

    @Column(name = "DEFAULT_PROJECT")
    public Long getDefaultProjectId() {
	return _defaultProjectId;
    }

    @Column(name = "SOURCE_LANGUAGE", length = 10)
    public String getDefaultSourceLanguage() {
	return _defaultSourceLanguage;
    }

    @Column(name = "TARGET_LANGUAGE")
    @Lob
    public String getDefaultTargetLanguage() {
	return _defaultTargetLanguage;
    }

    @Column(name = "ITEMS_PER_PAGE")
    public Integer getItemsPerPage() {
	return _itemsPerPage;
    }

    @Column(name = "LANGUAGE", length = 10)
    public String getLanguage() {
	return _language;
    }

    @Column(name = "REFRESH_PERIOD")
    public Integer getRefreshPeriod() {
	return _refreshPeriod;
    }

    @Column(name = "WEEKLY_HOUR", nullable = true, updatable = true)
    public Integer getWeeklyHour() {
	return _weeklyHour;
    }

    public void setActionSize(String actionSize) {
	_actionSize = actionSize;
    }

    public void setActionTextVisible(Boolean actionTextVisible) {
	_actionTextVisible = actionTextVisible;
    }

    public void setDailyHour(Integer dailyHour) {
	_dailyHour = dailyHour;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
	_dayOfWeek = dayOfWeek;
    }

    public void setDefaultProjectId(Long defaultProjectId) {
	_defaultProjectId = defaultProjectId;
    }

    public void setDefaultSourceLanguage(String defaultSourceLanguage) {
	_defaultSourceLanguage = defaultSourceLanguage;
    }

    public void setDefaultTargetLanguage(String defaultTargetLanguage) {
	_defaultTargetLanguage = defaultTargetLanguage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
	_itemsPerPage = itemsPerPage;
    }

    public void setLanguage(String language) {
	_language = language;
    }

    public void setRefreshPeriod(Integer refreshPeriod) {
	_refreshPeriod = refreshPeriod;
    }

    public void setWeeklyHour(Integer weeklyHour) {
	_weeklyHour = weeklyHour;
    }
}

package org.gs4tr.termmanager.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

public class ProjectTerminologyCounts implements Serializable {

    private static final long serialVersionUID = -4021149637176189911L;

    private List<String> _languages;

    private long _numberOfTermEntries;

    private Map<String, Long> _numberOfTermsByLanguage;

    private Long _projectId;

    public ProjectTerminologyCounts() {
    }

    public ProjectTerminologyCounts(Long projectId, List<String> languages) {
	_projectId = projectId;
	_languages = languages;
	_numberOfTermsByLanguage = new HashMap<>();
    }

    public List<String> getLanguages() {
	return _languages;
    }

    public long getNumberOfTermEntries() {
	return _numberOfTermEntries;
    }

    public Map<String, Long> getNumberOfTermsByLanguage() {
	return _numberOfTermsByLanguage;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public void put(String language, Long termCount) {
	if (MapUtils.isEmpty(_numberOfTermsByLanguage)) {
	    _numberOfTermsByLanguage = new HashMap<>();
	}
	_numberOfTermsByLanguage.put(language, termCount);
    }

    public void setLanguages(List<String> languages) {
	_languages = languages;
    }

    public void setNumberOfTermEntries(long numberOfTermEntries) {
	_numberOfTermEntries = numberOfTermEntries;
    }

    public void setNumberOfTermsByLanguage(Map<String, Long> numberOfTermsByLanguage) {
	_numberOfTermsByLanguage = numberOfTermsByLanguage;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }
}

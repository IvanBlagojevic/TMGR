package org.gs4tr.termmanager.persistence.update;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.locale.Locale;

public class AnalyzeImportInfo {

    private boolean _idColumnExist;

    private Map<Locale, Integer> _languages;

    private Map<Locale, Integer> _languagesAttributes;

    private boolean _notAllowedStatusPresent;

    private MutableInt _noTermEntryForImport = new MutableInt(0);

    private Set<String> _termDescription;

    private Set<String> _termEntryAttributes;

    private Map<String, Integer> _termEntryAttributesCount;

    private Set<String> _termNotes;

    private Set<String> _unknownStatuses;

    public Map<Locale, Integer> getLanguages() {
	return _languages;
    }

    public Map<Locale, Integer> getLanguagesAttributes() {
	return _languagesAttributes;
    }

    public MutableInt getNoTermEntryForImport() {
	return _noTermEntryForImport;
    }

    public Set<String> getTermDescription() {
	return _termDescription;
    }

    public Set<String> getTermEntryAttributes() {
	return _termEntryAttributes;
    }

    public Map<String, Integer> getTermEntryAttributesCount() {
	return _termEntryAttributesCount;
    }

    public Set<String> getTermNotes() {
	return _termNotes;
    }

    public Set<String> getUnknownStatuses() {
	return _unknownStatuses;
    }

    public boolean isIdColumnExist() {
	return _idColumnExist;
    }

    public boolean isNotAllowedStatusPresent() {
	return _notAllowedStatusPresent;
    }

    public void setIdColumnExist(boolean idColumnExist) {
	_idColumnExist = idColumnExist;
    }

    public void setLanguages(Map<Locale, Integer> languages) {
	_languages = languages;
    }

    public void setLanguagesAttributes(Map<Locale, Integer> languagesAttributes) {
	_languagesAttributes = languagesAttributes;
    }

    public void setNotAllowedStatusPresent(boolean notAllowedStatusPresent) {
	_notAllowedStatusPresent = notAllowedStatusPresent;
    }

    public void setTermDescription(Set<String> termDescription) {
	_termDescription = termDescription;
    }

    public void setTermEntryAttributes(Set<String> termEntryAttributes) {
	_termEntryAttributes = termEntryAttributes;
    }

    public void setTermEntryAttributesCount(Map<String, Integer> termEntryAttributesCount) {
	_termEntryAttributesCount = termEntryAttributesCount;
    }

    public void setTermNotes(Set<String> termNotes) {
	_termNotes = termNotes;
    }

    public void setUnknownStatuses(Set<String> unknownStatuses) {
	_unknownStatuses = unknownStatuses;
    }
}

package org.gs4tr.termmanager.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;

/**
 * DTO class which represents changes made between two saves of term entry.
 * 
 * @author TMGR_Backend
 * 
 */
public class TermEntryDifferences {

    private final String _action;

    private Collection<LanguageModifications> _languagesModifications;

    private final Long _modificationDate;

    private final String _modificationUser;

    private boolean _noChange = true;

    private final TermEntryModifications _termEntryModifications;

    public TermEntryDifferences(String action, Long modificationDate, String modificationUser) {
	_termEntryModifications = new TermEntryModifications();
	_action = action;
	_modificationDate = modificationDate;
	_modificationUser = modificationUser;
	_languagesModifications = new ArrayList<>();
    }

    public void addAllAttributeModifications(Collection<DescriptionDifferences> attrDiffs) {
	if (CollectionUtils.isNotEmpty(attrDiffs)) {
	    getTermEntryModifications().getAttributesDifferences().addAll(attrDiffs);
	    boolean noChange = false;
	    setNoChange(noChange);
	}
    }

    public void addAllLanguagesModifications(Collection<LanguageModifications> lms) {
	if (CollectionUtils.isNotEmpty(lms)) {
	    getLanguagesModifications().addAll(lms);
	    boolean noChange = false;
	    setNoChange(noChange);
	}
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	TermEntryDifferences other = (TermEntryDifferences) obj;
	if (_action == null) {
	    if (other._action != null)
		return false;
	} else if (!_action.equals(other._action))
	    return false;
	if (_languagesModifications == null) {
	    if (other._languagesModifications != null)
		return false;
	} else if (!_languagesModifications.equals(other._languagesModifications))
	    return false;
	if (_modificationDate == null) {
	    if (other._modificationDate != null)
		return false;
	} else if (!_modificationDate.equals(other._modificationDate))
	    return false;
	if (_modificationUser == null) {
	    if (other._modificationUser != null)
		return false;
	} else if (!_modificationUser.equals(other._modificationUser))
	    return false;
	if (_noChange != other._noChange)
	    return false;
	if (_termEntryModifications == null) {
	    if (other._termEntryModifications != null)
		return false;
	} else if (!_termEntryModifications.equals(other._termEntryModifications))
	    return false;
	return true;
    }

    public String getAction() {
	return _action;
    }

    public Collection<LanguageModifications> getLanguagesModifications() {
	return _languagesModifications;
    }

    public Long getModificationDate() {
	return _modificationDate;
    }

    public String getModificationUser() {
	return _modificationUser;
    }

    public TermEntryModifications getTermEntryModifications() {
	return _termEntryModifications;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_action == null) ? 0 : _action.hashCode());
	result = prime * result + ((_languagesModifications == null) ? 0 : _languagesModifications.hashCode());
	result = prime * result + ((_modificationDate == null) ? 0 : _modificationDate.hashCode());
	result = prime * result + ((_modificationUser == null) ? 0 : _modificationUser.hashCode());
	result = prime * result + (_noChange ? 1231 : 1237);
	result = prime * result + ((_termEntryModifications == null) ? 0 : _termEntryModifications.hashCode());
	return result;
    }

    public boolean hasNoChange() {
	return _noChange;
    }

    public void sortLanguageModifications(List<String> languageIds) {
	Map<Integer, LanguageModifications> sorted = new TreeMap<>();
	for (LanguageModifications lm : getLanguagesModifications()) {
	    String languageId = lm.getLanguage().getLanguageId();
	    sorted.put(languageIds.indexOf(languageId), lm);
	}
	setLanguagesModifications(sorted.values());
    }

    @Override
    public String toString() {
	return "TermEntryDifferences [_modificationDate=" + _modificationDate + ", _modificationUser="
		+ _modificationUser + ", _action=" + _action + ", _noChange=" + _noChange + ", _termEntryModifications="
		+ _termEntryModifications + ", _languagesModifications=" + _languagesModifications + "]";
    }

    private void setLanguagesModifications(Collection<LanguageModifications> languagesModifications) {
	_languagesModifications = languagesModifications;
    }

    private void setNoChange(boolean noChange) {
	_noChange = noChange;
    }
}

package org.gs4tr.termmanager.model.dto;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.Language;

/**
 * DTO class which represents differences between the corresponding languages in
 * {@code TermEntry}.
 * 
 * @author TMGR_Backend
 * 
 */
public class LanguageModifications {

    private final Language _language;
    private final List<TermDifferences> _termsDifferences;

    public LanguageModifications(String languageId) {
	_language = Language.valueOf(languageId);
	_termsDifferences = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	LanguageModifications other = (LanguageModifications) obj;
	if (_language == null) {
	    if (other._language != null)
		return false;
	} else if (!_language.equals(other._language))
	    return false;
	if (_termsDifferences == null) {
	    if (other._termsDifferences != null)
		return false;
	} else if (!_termsDifferences.equals(other._termsDifferences))
	    return false;
	return true;
    }

    public Language getLanguage() {
	return _language;
    }

    public List<TermDifferences> getTermsDifferences() {
	return _termsDifferences;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_language == null) ? 0 : _language.hashCode());
	result = prime * result + ((_termsDifferences == null) ? 0 : _termsDifferences.hashCode());
	return result;
    }

    @Override
    public String toString() {
	return "LanguageModifications [_language=" + _language + ", _termsDifferences=" + _termsDifferences + "]";
    }
}

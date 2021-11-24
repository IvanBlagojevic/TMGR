package org.gs4tr.termmanager.service.file.analysis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.locale.LocaleException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "locale" })
public class ImportLanguage implements Serializable {

    private static final long serialVersionUID = -6863055921568099685L;

    private final Language _language;
    private final Locale _locale;
    private long _numberOfAttributes;
    private long _numberOfTerms;
    private final List<Language> _similarProjectLanguages;

    private Status _status;

    public ImportLanguage(final String localeCode) throws LocaleException {
	_locale = Locale.makeLocale(localeCode);
	_language = Language.valueOf(localeCode);
	_similarProjectLanguages = new ArrayList<>();
    }

    public void addSimilarProjectLanguage(String localeCode) {
	getSimilarProjectLanguages().add(Language.valueOf(localeCode));
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ImportLanguage other = (ImportLanguage) obj;
	if (_locale == null) {
	    if (other._locale != null)
		return false;
	} else if (!_locale.equals(other._locale))
	    return false;
	if (_numberOfAttributes != other._numberOfAttributes)
	    return false;
	if (_numberOfTerms != other._numberOfTerms)
	    return false;
	if (_similarProjectLanguages == null) {
	    if (other._similarProjectLanguages != null)
		return false;
	} else if (!_similarProjectLanguages.equals(other._similarProjectLanguages))
	    return false;
	return _status == other._status;
    }

    public Language getLanguage() {
	return _language;
    }

    public Locale getLocale() {
	return _locale;
    }

    public long getNumberOfAttributes() {
	return _numberOfAttributes;
    }

    public long getNumberOfTerms() {
	return _numberOfTerms;
    }

    public List<Language> getSimilarProjectLanguages() {
	return _similarProjectLanguages;
    }

    public Status getStatus() {
	return _status;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_locale == null) ? 0 : _locale.hashCode());
	result = prime * result + (int) (_numberOfAttributes ^ (_numberOfAttributes >>> 32));
	result = prime * result + (int) (_numberOfTerms ^ (_numberOfTerms >>> 32));
	result = prime * result + ((_similarProjectLanguages == null) ? 0 : _similarProjectLanguages.hashCode());
	return prime * result + ((_status == null) ? 0 : _status.hashCode());
    }

    public void setNumberOfAttributes(long numberOfAttributes) {
	_numberOfAttributes = numberOfAttributes;
    }

    public void setNumberOfTerms(long numberOfTerms) {
	_numberOfTerms = numberOfTerms;
    }

    /**
     * If <code>SourceLanguagePostProcessorChain</code> set status value to
     * <code>Status.SOURCE</code> do not let other processors (i.e
     * <code>ImportLanguagePostProcessorChain</code>) in the chain to overwrite it.
     */
    public void setStatus(Status status) {
	if (Objects.isNull(_status)) {
	    _status = status;
	}
    }

    @Override
    public String toString() {
	return "ImportLanguage [_locale=" + _locale + ", _numberOfAttributes=" + _numberOfAttributes
		+ ", _numberOfTerms=" + _numberOfTerms + ", _similarProjectLanguages=" + _similarProjectLanguages
		+ ", _status=" + _status + "]";
    }
}
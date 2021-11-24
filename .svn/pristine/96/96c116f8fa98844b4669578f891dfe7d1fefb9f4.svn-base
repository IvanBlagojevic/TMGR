package org.gs4tr.termmanager.service.file.analysis.model;

import java.io.Serializable;
import java.util.Objects;

import org.gs4tr.foundation.locale.Locale;

public class Language implements Serializable {

    private static final long serialVersionUID = 8170099726961440568L;

    public static Language valueOf(String localeCode) {
	String code = Locale.makeLocale(localeCode).getCode();
	return new Language(code, org.gs4tr.termmanager.model.Language.valueOf(code).getDisplayName());
    }

    private final String _locale;
    private final String _value;

    public Language(String locale, String value) {
	_locale = Objects.requireNonNull(locale);
	_value = Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o == null || getClass() != o.getClass())
	    return false;

	Language language = (Language) o;

	if (!_locale.equals(language._locale))
	    return false;
	return _value.equals(language._value);
    }

    public String getLocale() {
	return _locale;
    }

    public String getValue() {
	return _value;
    }

    @Override
    public int hashCode() {
	int result = _locale.hashCode();
	return 31 * result + _value.hashCode();
    }

    @Override
    public String toString() {
	return "Language{" + "_locale='" + _locale + '\'' + ", _value='" + _value + '\'' + '}';
    }
}

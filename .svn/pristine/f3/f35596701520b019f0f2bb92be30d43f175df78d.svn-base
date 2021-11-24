package org.gs4tr.termmanager.service.file.analysis.request;

import static java.util.stream.Collectors.groupingBy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;

/**
 * <p>
 * Context/parameter object which provides access to shared data within single
 * batch analysis.
 * </p>
 * 
 * @since 5.0
 */
@ThreadSafe
public final class Context {

    private static final String LANGUAGE_KEY = "languageKey";
    /**
     * <code>Locale</code> cache holding <code>Locale</code> objects by
     * {@link Locale#getLanguage()} created from list of all user project
     * languages (i.e language codes).
     */
    private final ConcurrentMap<String, Map<String, List<Locale>>> USER_PROJECT_LOCALES_BY_LANGUAGE = new ConcurrentHashMap<>(
	    1);

    private final Map<Level, Set<String>> _attributesByLevel;
    private final Map<String, Set<String>> _comboValuesPerAttribute;
    private final List<String> _projectLanguageCodes;
    private final String _sourceLanguage;
    private final Set<String> _userProjectLanguageCodes;

    private Context(Builder builder) {
	_userProjectLanguageCodes = builder._userProjectLanguageCodes;
	_comboValuesPerAttribute = builder._comboValuesPerAttribute;
	_projectLanguageCodes = builder._projectLanguageCodes;
	_attributesByLevel = builder._attributesByLevel;
	_sourceLanguage = builder._sourceLanguage;
    }

    public Map<Level, Set<String>> getAttributesByLevel() {
	return _attributesByLevel;
    }

    public Map<String, Set<String>> getComboValuesPerAttribute() {
	return _comboValuesPerAttribute;
    }

    public List<String> getProjectLanguageCodes() {
	return _projectLanguageCodes;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public Set<String> getUserProjectLanguageCodes() {
	return _userProjectLanguageCodes;
    }

    public Map<String, List<Locale>> getUserProjectLocalesByLanguage() {
	return USER_PROJECT_LOCALES_BY_LANGUAGE.computeIfAbsent(LANGUAGE_KEY, key -> Collections.unmodifiableMap(
		_userProjectLanguageCodes.stream().map(Locale::get).collect(groupingBy(Locale::getLanguage))));
    }

    public static class Builder {

	private Map<Level, Set<String>> _attributesByLevel;
	private Map<String, Set<String>> _comboValuesPerAttribute;
	private final boolean _ignoreCase;
	private List<String> _projectLanguageCodes;
	private String _sourceLanguage;
	private final Set<String> _userProjectLanguageCodes;

	public Builder(Set<String> userProjectLanguageCodes, final boolean ignoreCase) {
	    _userProjectLanguageCodes = Collections.unmodifiableSet(userProjectLanguageCodes);
	    _ignoreCase = ignoreCase;
	}

	public Builder attributesByLevel(Map<Level, Set<String>> original) {
	    Map<Level, Set<String>> attributesByLevel = _ignoreCase ? convertToTreeSet(original) : original;
	    _attributesByLevel = Collections.unmodifiableMap(attributesByLevel);
	    return this;
	}

	public Context build() {
	    return new Context(this);
	}

	public Builder comboValuesPerAttribute(Map<String, Set<String>> original) {
	    Map<String, Set<String>> comboValuesPerAttribute = _ignoreCase ? convertToTreeMap(original) : original;
	    _comboValuesPerAttribute = Collections.unmodifiableMap(comboValuesPerAttribute);
	    return this;
	}

	public Builder projectLanguageCodes(List<String> projectLanguageCodes) {
	    _projectLanguageCodes = Collections.unmodifiableList(projectLanguageCodes);
	    return this;
	}

	public Builder sourceLanguage(String sourceLanguage) {
	    _sourceLanguage = sourceLanguage;
	    return this;
	}

	private Map<String, Set<String>> convertToTreeMap(Map<String, Set<String>> original) {
	    Map<String, Set<String>> comboValuesPerAttribute = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	    for (final Entry<String, Set<String>> entry : original.entrySet()) {
		TreeSet<String> treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		treeSet.addAll(entry.getValue());
		comboValuesPerAttribute.put(entry.getKey(), treeSet);
	    }
	    return comboValuesPerAttribute;
	}

	private Map<Level, Set<String>> convertToTreeSet(Map<Level, Set<String>> original) {
	    Map<Level, Set<String>> attributesByLevel = new HashMap<>(original.size());
	    for (final Entry<Level, Set<String>> entry : original.entrySet()) {
		TreeSet<String> treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		treeSet.addAll(entry.getValue());
		attributesByLevel.put(entry.getKey(), treeSet);
	    }
	    return attributesByLevel;
	}
    }
}

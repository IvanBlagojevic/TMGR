package org.gs4tr.termmanager.model;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.gs4tr.termmanager.model.LanguageAlignmentEnum.LTR;
import static org.gs4tr.termmanager.model.LanguageAlignmentEnum.RTL;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import org.gs4tr.foundation.locale.Locale;

/**
 * Language class is primarily designed to improve performance and to solve some
 * design problems of {@code Locale} class. As a immutable, Language class is
 * thread safe. You can share {@code Language} objects across threads without
 * additional synchronization. In some places Language class publish an internal
 * state for general use (e.g {@code Language#getAllAvailableLanguages()}), but
 * we are doing that in a thread-safe manner.
 * 
 * @author TMGR_Backend
 *
 */
public class Language {
    /**
     * Language cache holding {@code Language} objects created from array of all
     * locale-codes (jdkLocales, internalLocales and personalLocales).
     */
    private static final ConcurrentMap<String, Map<String, Language>> CACHE = new ConcurrentHashMap<>();

    /* ---------------- Constants ---------------------- */

    /**
     * Key used to associate all languages group by language id (i.e
     * {@linkplain Locale#_code}).
     */
    private static final String LANGUAGES_KEY = "languagesKey";

    /* ---------------- Static utilities --------------- */

    /**
     * Returns an unmodifiable view of all available languages. This method provide
     * users with "read-only" access to internal language collection. Query
     * operations on the returned collection "read through" to the specified
     * collection, and attempts to modify the returned collection, whether direct or
     * via its iterator, result in an <tt>UnsupportedOperationException</tt>.
     * <p>
     * 
     * As a "read-only" view this collection can be accessed concurrently by
     * multiple threads without additional synchronization.
     * <p>
     * 
     * @return unmodifiable collection of all available languages.
     */
    public static Collection<Language> getAllAvailableLanguages() {
	return getLanguagesGroupById().values();
    }

    /**
     * Returns a {@code Language} instance representing the specified {@code code}
     * value. If a new {@code Language} instance is not required, this method should
     * generally be used in preference to the constructor
     * {@link #Language(Locale locale)}, as this method is likely to yield
     * significantly better space and time performance by caching all available
     * language values.
     * 
     * @param code
     * @return immutable {@code Language} instance.
     */
    public static Language valueOf(String code) {
	return getLanguagesGroupById().get(code);
    }

    /**
     * Language name that is appropriate for display to the user.
     */
    private final String _displayName;
    /**
     * Denotes a right-to-left or left-to-right language id.
     */
    private final LanguageAlignmentEnum _languageAlignment;

    /* ---------------- Fields ------------------------- */
    /**
     * Unique language id that is equal to {@link Locale#_code} value.
     */
    private final String _languageId;

    /**
     * Creates a new immutable {@code Language} instance from corresponding
     * {@code Locale} object.
     *
     * @param locale
     */
    public Language(Locale locale) {
	_displayName = locale.getDisplayName();
	_languageId = locale.getCode();
	_languageAlignment = locale.isRTL() ? RTL : LTR;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Language other = (Language) obj;
	if (_displayName == null) {
	    if (other._displayName != null)
		return false;
	} else if (!_displayName.equals(other._displayName))
	    return false;
	if (_languageAlignment != other._languageAlignment)
	    return false;
	if (_languageId == null) {
	    return other._languageId == null;
	} else
	    return _languageId.equals(other._languageId);
    }

    public String getDisplayName() {
	return _displayName;
    }

    /* ---------------- Public operations -------------- */

    public LanguageAlignmentEnum getLanguageAlignment() {
	return _languageAlignment;
    }

    public String getLanguageId() {
	return _languageId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_displayName == null) ? 0 : _displayName.hashCode());
	result = prime * result + ((_languageAlignment == null) ? 0 : _languageAlignment.hashCode());
	return prime * result + ((_languageId == null) ? 0 : _languageId.hashCode());
    }

    /**
     * Returns a string representation of this language. The string representation
     * consists of a displayName, languageAlignment and languageId that are
     * separated by the character {@code ", "}.
     *
     * @return a string representation of this language
     */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder(96);
	builder.append("Language [displayName=");
	builder.append(getDisplayName());
	builder.append(", languageAlignment=");
	builder.append(getLanguageAlignment());
	builder.append(", languageId=");
	builder.append(getLanguageId());

	return builder.append(']').toString();
    }

    /**
     * Get existing or computed unmodifiable language map where languages are group
     * by {@link Language#_languageId}. If the specified {@code LANGUAGES_KEY} is
     * not already associated with a language map, attempts to compute its value
     * using the {@code groupLanguagesById} mapping function and enters it into this
     * map unless null.
     * <p>
     *
     * The {@code ConcurrentMap#computeIfAbsent(Object, Function)} method invocation
     * is performed atomically, so the groupLanguagesById function is applied at
     * most once per key.
     * <p>
     *
     * @return the current (existing or computed) unmodifiable language map
     *         associated with the specified LANGUAGES_KEY, or null if the computed
     *         unmodifiable language map is null.
     */
    private static Map<String, Language> getLanguagesGroupById() {
	return CACHE.computeIfAbsent(LANGUAGES_KEY, groupLanguagesById());
    }

    /**
     * Create mapping function that takes all available {@code Locale}, create
     * {@code Language} for each one, group them by {@link Language#_languageId} and
     * create unmodifiable view of that map.
     *
     * @return mapping function
     */
    private static Function<String, Map<String, Language>> groupLanguagesById() {
	return key -> Collections.unmodifiableMap(Arrays.stream(Locale.getAvailableLocales())
		.map(locale -> new Language(locale)).collect(toMap(Language::getLanguageId, identity())));
    }
}

package org.gs4tr.termmanager.solr.plugin.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation3.solr.model.update.CommandEnum;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

//TODO fljubicic - This is tmgr only helper. 
// Make standard helper for all, validate field values and remove use of '-' in localized fields. 
// See also FieldNameTool, SolrFieldNameHelper and SolrDocumentBuilder.
public class SolrDocHelper {

    private static final String EMPTY = "";

    private static final String UNDERSCORE = "_";

    public static String createDynamicFieldName(String preffix, String sufix) {

	return createDynamicFieldNameInternal(preffix, null, sufix);
    }

    public static String createDynamicFieldName(String preffix, String language, String sufix) {
	return createDynamicFieldNameInternal(preffix, language, sufix);

    }

    public static String extractLocaleCode(String key) {
	String beforeUnderscore = getBeforeUnderscore(key);

	if (beforeUnderscore != null) {
	    key = beforeUnderscore;
	}

	String digit = CharMatcher.DIGIT.retainFrom(key);

	String subString = key.substring(0, key.indexOf(digit));
	if (Locale.checkLocale(subString)) {
	    return subString;
	}

	return key;
    }

    public static Set<String> extractLocaleCodes(Collection<String> fieldNames) {
	Set<String> languageIds = new HashSet<>();

	if (Objects.isNull(fieldNames)) {
	    return languageIds;
	}

	for (String fieldName : fieldNames) {
	    String localeCode = extractLocaleCode(fieldName);
	    if (Locale.checkLocale(localeCode)) {
		languageIds.add(localeCode);
	    }
	}

	return languageIds;
    }

    public static int extractNumber(String fieldName) {
	int num = 0;

	if (fieldName == null) {
	    return num;
	}

	String key = CharMatcher.DIGIT.retainFrom(fieldName);
	try {
	    num = StringUtils.isNotEmpty(key) ? Integer.parseInt(key) : 0;
	} catch (NumberFormatException e) {
	    num = 0;
	}

	return num;
    }

    public static Set<Integer> extractNumbers(Collection<String> fieldNames) {
	Set<Integer> nums = new TreeSet<Integer>(new Comparator<Integer>() {
	    @Override
	    public int compare(Integer o1, Integer o2) {
		return o1.compareTo(o2);
	    }
	});

	if (fieldNames != null) {
	    for (String fieldName : fieldNames) {
		int num = extractNumber(fieldName);
		if (num > 0) {
		    nums.add(num);
		}
	    }
	}

	return nums;
    }

    public static String getBeforeUnderscore(String string) {
	if (string == null) {
	    return EMPTY;
	}

	String beforeUnderscore = string;

	int index = string.indexOf(UNDERSCORE);
	if (index > -1) {
	    beforeUnderscore = string.substring(0, index);
	}
	return beforeUnderscore;
    }

    public static Boolean getBooleanValue(Object item) {
	if (item != null) {
	    if (item instanceof Boolean) {
		return (Boolean) item;
	    } else if (item instanceof String) {
		String value = (String) item;
		return Boolean.valueOf(value);
	    } else if (item instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> setMap = (Map<String, Object>) item;
		Object value = setMap.get(CommandEnum.SET.name().toLowerCase());
		if (value != null) {
		    if (value instanceof Boolean) {
			return (Boolean) value;
		    } else if (value instanceof String) {
			return Boolean.valueOf((String) value);
		    }
		}
	    }
	}
	return Boolean.FALSE;
    }

    @SuppressWarnings("unchecked")
    public static String getStringValue(Object item) {
	if (item != null) {
	    if (item instanceof String) {
		return (String) item;
	    } else if (item instanceof Map) {
		Map<String, String> setMap = (Map<String, String>) item;
		return setMap.get(CommandEnum.SET.name().toLowerCase());
	    }
	}
	return null;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getStringValues(Object item) {
	List<String> result = Collections.emptyList();
	if (Objects.isNull(item)) {
	    return result;
	}
	if (item instanceof List) {
	    List<Object> list = (List<Object>) item;
	    Object value = list.get(0);
	    if (value instanceof Map) {
		// 6-October-2016, as per [TERII-4364]:
		result = exctractLanguagesFromMap(value);
	    } else if (value instanceof String) {
		result = (List<String>) item;
	    }
	} else if (item instanceof Map) {
	    result = exctractLanguagesFromMap(item);
	}
	return result;
    }

    public static Iterator<String> split(String separator, CharSequence sequence) {
	return Splitter.on(separator).split(sequence).iterator();
    }

    private static String createDynamicFieldNameInternal(String preffix, String body, String sufix) {
	StringBuilder builder = new StringBuilder();
	builder.append(preffix);
	builder.append(UNDERSCORE);
	if (body != null) {
	    builder.append(body);
	    builder.append(UNDERSCORE);
	}
	builder.append(sufix);
	return builder.toString();

    }

    @SuppressWarnings("unchecked")
    private static List<String> exctractLanguagesFromMap(Object item) {
	Map<String, Object> map = (Map<String, Object>) item;
	Object value = map.get(CommandEnum.SET.name().toLowerCase());
	List<String> result = Collections.emptyList();
	if (value instanceof List) {
	    result = (List<String>) value;
	} else if (value instanceof String) {
	    result = Collections.singletonList((String) value);
	}
	return result;
    }
}

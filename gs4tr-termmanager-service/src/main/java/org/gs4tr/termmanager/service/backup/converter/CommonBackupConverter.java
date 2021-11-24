package org.gs4tr.termmanager.service.backup.converter;

import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.gs4tr.foundation3.solr.model.update.CommandEnum;

public class CommonBackupConverter {

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
	return null;
    }

    public static Boolean getBooleanValue(String fieldName, Map<String, Object> fieldValues) {
	Object item = fieldValues.get(fieldName);
	Boolean booleanValue = getBooleanValue(item);
	return booleanValue != null ? booleanValue : Boolean.FALSE;
    }

    public static Boolean getBooleanValueDefault(String fieldName, Map<String, Object> fieldValues,
	    Boolean defaultValue) {
	Object item = fieldValues.get(fieldName);
	Boolean booleanValue = getBooleanValue(item);
	return booleanValue != null ? booleanValue : defaultValue;
    }

    public static Integer getIntegerValue(String fieldName, SolrInputDocument doc) {
	Object item = doc.getFieldValue(fieldName);
	if (item != null) {
	    if (item instanceof Integer) {
		return (Integer) item;
	    } else if (item instanceof String) {
		String value = (String) item;
		return Integer.valueOf(value);
	    } else if (item instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> setMap = (Map<String, Object>) item;
		Object value = setMap.get(CommandEnum.SET.name().toLowerCase());
		if (value != null) {
		    if (value instanceof String) {
			return Integer.valueOf((String) value);
		    } else if (value instanceof Long) {
			return ((Long) value).intValue();
		    }
		}
	    }
	}
	return null;
    }

    public static Long getLongValue(Object item) {
	if (item != null) {
	    if (item instanceof Long) {
		return (Long) item;
	    } else if (item instanceof String) {
		String value = (String) item;
		return value.isEmpty() ? null : Long.valueOf(value);
	    } else if (item instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> setMap = (Map<String, Object>) item;
		Object value = setMap.get(CommandEnum.SET.name().toLowerCase());
		if (value != null) {
		    if (value instanceof String) {
			String s = (String) value;
			return s.isEmpty() ? null : Long.valueOf(s);
		    } else if (value instanceof Long) {
			return (Long) value;
		    }
		}
	    }
	}
	return null;
    }

    public static Long getLongValue(String fieldName, Map<String, Object> fieldValues) {
	Object item = fieldValues.get(fieldName);
	return getLongValue(item);
    }

    public static Long getLongValue(String fieldName, SolrInputDocument doc) {
	Object item = doc.getFieldValue(fieldName);
	return getLongValue(item);
    }

    public static String getStringValue(Object item) {
	if (item != null) {
	    if (item instanceof String) {
		return (String) item;
	    } else if (item instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, String> setMap = (Map<String, String>) item;
		return setMap.get(CommandEnum.SET.name().toLowerCase());
	    }
	}
	return null;
    }

    public static String getStringValue(String fieldName, Map<String, Object> fieldValues) {
	Object item = fieldValues.get(fieldName);
	return getStringValue(item);
    }

    public static String getStringValue(String fieldName, SolrInputDocument doc) {
	Object item = doc.getFieldValue(fieldName);
	return getStringValue(item);
    }
}

package org.gs4tr.termmanager.service.backup.converter;

import java.util.HashMap;
import java.util.Map;

public class TermField {

    private Map<String, Object> _fieldNames;

    private String _key;

    private String _languageId;

    public TermField(String key, String languageId) {
	_key = key;
	_languageId = languageId;
	_fieldNames = new HashMap<String, Object>();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	TermField other = (TermField) obj;
	if (_key == null) {
	    if (other._key != null)
		return false;
	} else if (!_key.equals(other._key))
	    return false;
	if (_languageId == null) {
	    if (other._languageId != null)
		return false;
	} else if (!_languageId.equals(other._languageId))
	    return false;
	return true;
    }

    public Map<String, Object> getFieldNames() {
	return _fieldNames;
    }

    public String getKey() {
	return _key;
    }

    public String getLanguageId() {
	return _languageId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_key == null) ? 0 : _key.hashCode());
	result = prime * result + ((_languageId == null) ? 0 : _languageId.hashCode());
	return result;
    }

    public void setFieldNames(Map<String, Object> fieldNames) {
	_fieldNames = fieldNames;
    }

    public void setKey(String key) {
	_key = key;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }
}

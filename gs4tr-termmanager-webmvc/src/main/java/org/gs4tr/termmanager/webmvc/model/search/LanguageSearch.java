package org.gs4tr.termmanager.webmvc.model.search;

import java.util.List;

public class LanguageSearch {

    private List<String> _defaultValues;

    private String _name;

    private String _type;

    private List<LanguageDirectionItem> _values;

    public LanguageSearch() {
    }

    public List<String> getDefaultValues() {
	return _defaultValues;
    }

    public String getName() {
	return _name;
    }

    public String getType() {
	return _type;
    }

    public List<LanguageDirectionItem> getValues() {
	return _values;
    }

    public void setDefaultValues(List<String> defaultValues) {
	_defaultValues = defaultValues;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setType(String type) {
	_type = type;
    }

    public void setValues(List<LanguageDirectionItem> values) {
	_values = values;
    }
}

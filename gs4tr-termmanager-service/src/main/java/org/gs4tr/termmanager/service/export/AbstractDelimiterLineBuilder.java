package org.gs4tr.termmanager.service.export;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractDelimiterLineBuilder {

    public static String buildLine(String[] fields) {
	if (fields == null) {
	    return null;
	}

	CsvLineBuilder lineBuilder = new CsvLineBuilder();

	for (String field : fields) {
	    lineBuilder.addField(field);
	}

	return lineBuilder.toString();
    }

    private StringBuilder _builder;

    private boolean _firstField;

    public AbstractDelimiterLineBuilder() {
	_builder = new StringBuilder();
	_firstField = true;
    }

    public void addField(char ch) {
	if (!_firstField) {
	    _builder.append(getDelimiter());
	}

	_firstField = false;

	if (ch == getDelimiter()) {
	    _builder.append('"').append(getDelimiter()).append('"');
	} else if (ch == '"') {
	    _builder.append('"').append('"').append('"').append('"');
	} else {
	    _builder.append(ch);
	}
    }

    public void addField(String field) {
	if (!_firstField) {
	    _builder.append(getDelimiter());
	}

	_firstField = false;

	if (StringUtils.isEmpty(field)) {
	    return;
	}

	if (field.indexOf(getDelimiter()) != -1 || field.indexOf('"') != -1) {
	    _builder.append('"');

	    for (char ch : field.toCharArray()) {
		if (ch == '"') {
		    _builder.append('"').append('"');
		} else {
		    _builder.append(ch);
		}
	    }

	    _builder.append('"');
	} else {
	    _builder.append(field);
	}
    }

    public String toString() {
	return _builder.toString();
    }

    abstract char getDelimiter();
}

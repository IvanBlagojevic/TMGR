package org.gs4tr.termmanager.service.termentry.reader.factory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.ImportTypeEnum;

public class TermEntryReaderConfig {

    public static class Builder {

	private Map<String, Set<String>> _comboValuesPerAttribute;
	private String _encoding = StandardCharsets.UTF_8.toString();
	private InputStream _in;
	private ImportTypeEnum _type;
	private List<String> _userProjectLanguages;

	public TermEntryReaderConfig build() {
	    return new TermEntryReaderConfig(this);
	}

	public Builder comboValuesPerAttribute(Map<String, Set<String>> comboValuesPerAttribute) {
	    _comboValuesPerAttribute = comboValuesPerAttribute;
	    return this;
	}

	public Builder encoding(String encoding) {
	    _encoding = encoding;
	    return this;
	}

	public Builder importType(ImportTypeEnum type) {
	    _type = type;
	    return this;
	}

	public Builder stream(InputStream stream) {
	    _in = stream;
	    return this;

	}

	public Builder userProjectLanguages(List<String> userProjectLanguages) {
	    _userProjectLanguages = userProjectLanguages;
	    return this;
	}
    }

    private final Map<String, Set<String>> _comboValuesPerAttribute;
    private final String _encoding;
    private final InputStream _in;
    private final ImportTypeEnum _type;
    private final List<String> _userProjectLanguages;

    private TermEntryReaderConfig(Builder builder) {
	_comboValuesPerAttribute = builder._comboValuesPerAttribute;
	_userProjectLanguages = builder._userProjectLanguages;
	_encoding = builder._encoding;
	_type = builder._type;
	_in = builder._in;
    }

    public Map<String, Set<String>> getComboValuesPerAttribute() {
	return _comboValuesPerAttribute;
    }

    public String getEncoding() {
	return _encoding;
    }

    public InputStream getIn() {
	return _in;
    }

    public ImportTypeEnum getType() {
	return _type;
    }

    public List<String> getUserProjectLanguages() {
	return _userProjectLanguages;
    }
}

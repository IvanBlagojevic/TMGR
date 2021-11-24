package org.gs4tr.termmanager.persistence.solr.query;

import org.apache.commons.lang.StringUtils;

public class TextFilter {

    private boolean _allTextSearch = false;

    private boolean _attributeTextSearch = false;

    private boolean _caseSensitive = false;

    private boolean _exactMatch = false;

    private boolean _fuzzyMatch = false;

    private boolean _segmentSearch = false;

    private String _text;

    public TextFilter(String text) {
	if (StringUtils.isEmpty(text)) {
	    throw new IllegalArgumentException(Messages.getString("TextFilter.0")); //$NON-NLS-1$
	}
	_text = text;
    }

    public TextFilter(String text, boolean exactMatch) {
	this(text);
	_exactMatch = exactMatch;
    }

    public TextFilter(String text, boolean exactMatch, boolean caseSensitive) {
	this(text);
	_exactMatch = exactMatch;
	_caseSensitive = caseSensitive;
    }

    public TextFilter(String text, boolean exactMatch, boolean caseSensitive, boolean allTextSearch) {
	this(text);
	_exactMatch = exactMatch;
	_caseSensitive = caseSensitive;
	_allTextSearch = allTextSearch;
    }

    public String getText() {
	return _text;
    }

    public boolean isAllTextSearch() {
	return _allTextSearch;
    }

    public boolean isAttributeTextSearch() {
	return _attributeTextSearch;
    }

    public boolean isCaseSensitive() {
	return _caseSensitive;
    }

    public boolean isExactMatch() {
	return _exactMatch;
    }

    public boolean isFuzzyMatch() {
	return _fuzzyMatch;
    }

    public boolean isSegmentSearch() {
	return _segmentSearch;
    }

    public void setAllTextSearch(boolean allTextSearch) {
	_allTextSearch = allTextSearch;
    }

    public void setAttributeTextSearch(boolean attributeTextSearch) {
	_attributeTextSearch = attributeTextSearch;
    }

    public void setCaseSensitive(boolean caseSensitive) {
	_caseSensitive = caseSensitive;
    }

    public void setExactMatch(boolean exactMatch) {
	_exactMatch = exactMatch;
    }

    public void setFuzzyMatch(boolean fuzzyMatch) {
	_fuzzyMatch = fuzzyMatch;
    }

    public void setSegmentSearch(boolean segmentSearch) {
	_segmentSearch = segmentSearch;
    }

    public void setText(String text) {
	_text = text;
    }

    @Override
    public String toString() {
	return "TextFilter [_allTextSearch=" + _allTextSearch + ", _attributeTextSearch=" + _attributeTextSearch
		+ ", _caseSensitive=" + _caseSensitive + ", _exactMatch=" + _exactMatch + ", _fuzzyMatch=" + _fuzzyMatch
		+ ", _segmentSearch=" + _segmentSearch + ", _text=" + _text + "]";
    }
}

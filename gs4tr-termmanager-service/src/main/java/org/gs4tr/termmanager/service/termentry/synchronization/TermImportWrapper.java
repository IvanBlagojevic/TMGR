package org.gs4tr.termmanager.service.termentry.synchronization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.termmanager.model.glossary.Term;

public class TermImportWrapper {

    private MutableInt _addedCount;

    private Set<String> _addedLanguages;

    private MutableInt _deletedCount;

    private DescriptionImportWrapper _descriptionWrapper;

    private Map<String, List<Term>> _overwrittenTerms;

    private MutableInt _skipedCount;

    private MutableInt _updatedCount;

    public TermImportWrapper() {
	_addedCount = new MutableInt(0);
	_deletedCount = new MutableInt(0);
	_updatedCount = new MutableInt(0);
	_skipedCount = new MutableInt(0);
	_overwrittenTerms = new HashMap<String, List<Term>>();
	_addedLanguages = new HashSet<String>();
	_descriptionWrapper = new DescriptionImportWrapper();
    }

    public void addTerm(Term term) {
	String languageId = term.getLanguageId();
	/*
	 * I am changing this to list because the behavior of a set is not
	 * specified if the value of an object is changed in a manner that
	 * affects equals comparisons while the object is an element in the set.
	 * 
	 * [issue TERII-3392], [issue TERII-4122].
	 */
	List<Term> terms = _overwrittenTerms.get(languageId);
	if (terms == null) {
	    terms = new ArrayList<Term>();
	    _overwrittenTerms.put(languageId, terms);
	}

	if (terms.isEmpty()) {
	    term.setFirst(Boolean.TRUE);
	} else {
	    term.setFirst(Boolean.FALSE);
	}

	terms.add(term);
    }

    public MutableInt getAddedCount() {
	return _addedCount;
    }

    public Set<String> getAddedLanguages() {
	return _addedLanguages;
    }

    public MutableInt getDeletedCount() {
	return _deletedCount;
    }

    public DescriptionImportWrapper getDescriptionWrapper() {
	return _descriptionWrapper;
    }

    public Map<String, List<Term>> getOverwrittenTerms() {
	return _overwrittenTerms;
    }

    public MutableInt getSkipedCount() {
	return _skipedCount;
    }

    public MutableInt getUpdatedCount() {
	return _updatedCount;
    }
}
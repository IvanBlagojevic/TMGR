package org.gs4tr.termmanager.service.termentry.memorizer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;

/**
 * This class provides custom caching logic implemented to reuse results of a
 * previous synchronization, reduce latency and fix merge process on import to
 * work with buffering.
 * 
 * @author TMGR_Backend
 * 
 */
public class ArrayDequeMemorizer implements Memorizable<TermEntry, String> {
    /**
     * The initial _buffer capacity
     */
    private static final int INIT_BUFFER_CAPACITY = 16;

    /**
     * Append term names for sync language.
     */
    private final Set<String> _buffer;

    private final Deque<TermEntry> _cache;

    private boolean _ignoreCase = true;

    /**
     * Constructs an empty cache with an initial capacity sufficient to hold the
     * specified number of elements.
     */
    public ArrayDequeMemorizer(int numElements, boolean ignoreCase) {
	/*
	 * This class is likely to be faster than LinkedList when used as a
	 * queue.
	 */
	_cache = new ArrayDeque<>(numElements);
	_buffer = new HashSet<>(INIT_BUFFER_CAPACITY);
	_ignoreCase = ignoreCase;
    }

    /**
     * Remove all elements from cache once import is completed.
     */
    @Override
    public void clear() {
	_cache.clear();
    }

    /**
     * Retrieves and remove element from cache if the corresponding element
     * exists. Otherwise, it returns {@code null}.
     */
    @Override
    public TermEntry fetch(TermEntry termEntry, String syncLang) {
	TermEntry result = null; // The value that will be returned..
	Set<Term> sourceTerms = extract(termEntry, syncLang);
	if (CollectionUtils.isEmpty(sourceTerms)) {
	    return null;
	}
	Iterator<TermEntry> newEntryIterator = _cache.iterator();

	while (newEntryIterator.hasNext()) {
	    TermEntry lastReturned = newEntryIterator.next();
	    Set<Term> syncTerms = extract(lastReturned, syncLang);
	    append(syncTerms);
	    if (!search(sourceTerms)) {
		clearBuffer();
		continue; // Continue searching for the element...
	    }
	    /* Assign value because we find element in cache. */
	    result = lastReturned;
	    /* Avoid duplicates deleting elements from the cache. */
	    newEntryIterator.remove();
	    clearBuffer();
	    break;
	}
	return result;
    }

    /**
     * Inserts the specified element at the front of this cache if argument is
     * not null.
     */
    @Override
    public void store(TermEntry termEntry) {
	if (isStorable(termEntry)) {
	    _cache.addFirst(termEntry);
	}
    }

    private void append(Set<Term> syncTerms) {
	if (CollectionUtils.isEmpty(syncTerms)) {
	    return; // Empty collections should be skipped.
	}
	// Appends the specified term names to this _buffer.
	for (Term term : syncTerms) {
	    if (term.isDisabled()) {
		continue;
	    }
	    String name = getTermName(term);
	    _buffer.add(name);
	}
    }

    /**
     * Clear _buffer so we can use the same object again
     */
    private void clearBuffer() {
	_buffer.clear();
    }

    private Set<Term> extract(TermEntry termEntry, String syncLang) {
	Map<String, Set<Term>> langTerms = termEntry.getLanguageTerms();
	if (MapUtils.isNotEmpty(langTerms)) {
	    return langTerms.get(syncLang);
	}
	return null;
    }

    private String getTermName(Term term) {
	return _ignoreCase ? term.getName().toLowerCase() : term.getName();
    }

    private boolean isNotNull(TermEntry termEntry) {
	if (termEntry == null) {
	    return false;
	}
	return true;
    }

    private boolean isStorable(TermEntry termEntry) {
	return isNotNull(termEntry) && MapUtils.isNotEmpty(termEntry.getLanguageTerms());
    }

    /**
     * Search for element in cache, return <code>true</code> if element exists.
     */
    private boolean search(Set<Term> sourceTerms) {
	if (_buffer.isEmpty()) {
	    return false;
	}

	for (Term term : sourceTerms) {
	    String name = getTermName(term);
	    if (_buffer.contains(name)) {
		return true;
	    }
	}
	return false;
    }
}
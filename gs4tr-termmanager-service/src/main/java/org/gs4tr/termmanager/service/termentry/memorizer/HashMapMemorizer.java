package org.gs4tr.termmanager.service.termentry.memorizer;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.gs4tr.termmanager.model.glossary.TermEntry;

public class HashMapMemorizer implements Memorizable<TermEntry, String> {

    private final Map<String, TermEntry> _cache;

    /**
     * Constructs an empty cache with an initial capacity sufficient to hold the
     * specified number of elements.
     */
    public HashMapMemorizer(final int numElements) {
	_cache = new HashMap<String, TermEntry>(numElements);
    }

    /**
     * Remove all elements from cache once import is completed.
     */
    @Override
    public void clear() {
	_cache.clear();
    }

    /**
     * Retrieves element with same uuId as input termEntry, if the corresponding
     * element exists. Otherwise, it returns {@code null}.
     */
    @Override
    public TermEntry fetch(TermEntry termEntry, String s) {
	return _cache.get(termEntry.getUuId());
    }

    @Override
    public void store(TermEntry termEntry) {
	// First validate this argument and perform null-safe check
	if (isStorable(termEntry)) {
	    _cache.put(termEntry.getUuId(), termEntry);
	}
    }

    private boolean isNotNull(TermEntry termEntry) {
	if (termEntry == null) {
	    return false;
	}
	return true;
    }

    private boolean isStorable(TermEntry t) {
	return isNotNull(t) && MapUtils.isNotEmpty(t.getLanguageTerms());
    }
}

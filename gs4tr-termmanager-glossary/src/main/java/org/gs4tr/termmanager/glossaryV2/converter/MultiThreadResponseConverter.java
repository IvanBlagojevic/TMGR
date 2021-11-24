package org.gs4tr.termmanager.glossaryV2.converter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.gs4tr.tm3.api.TermsHolder;
import org.gs4tr.tm3.api.glossary.Term;

public class MultiThreadResponseConverter {

    private static final int MAXIMUM_INDEX_REACHED = -1;

    private AtomicInteger _index;
    private final int _reposeSize;
    private volatile TermsHolder<Term>[] _response;

    @SuppressWarnings("unchecked")
    public MultiThreadResponseConverter(int responseSize) {
	_index = new AtomicInteger(0);
	_response = new TermsHolder[responseSize];
	_reposeSize = responseSize;
    }

    public void addResponseTermsHolder(int index, TermsHolder<Term> termsHolder) {
	getResponse()[index] = termsHolder;
    }

    public int getNextIndex() {

	int index = getIndex().get();
	if (index >= getReposeSize()) {
	    return MAXIMUM_INDEX_REACHED;
	}

	/* Note:Index should be tested and returned only atomically */
	int nextIndex = getIndex().getAndIncrement();
	if (nextIndex >= getReposeSize()) {
	    return MAXIMUM_INDEX_REACHED;
	}
	return nextIndex;

    }

    @SuppressWarnings("unchecked")
    public List<TermsHolder<Term>> getResponseResults() {
	return Arrays.asList(getResponse());
    }

    private AtomicInteger getIndex() {
	return _index;
    }

    private int getReposeSize() {
	return _reposeSize;
    }

    private TermsHolder[] getResponse() {
	return _response;
    }
}

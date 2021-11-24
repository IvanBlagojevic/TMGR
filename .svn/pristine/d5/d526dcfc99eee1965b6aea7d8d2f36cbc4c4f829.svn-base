package org.gs4tr.termmanager.persistence.solr.faceting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacetTermCounts {

    private Map<String, LanguageTermCount> _termCountByLanguage;

    public FacetTermCounts() {
    }

    public FacetTermCounts(List<String> languageIds) {
	_termCountByLanguage = new HashMap<>();
	for (String languageId : languageIds) {
	    LanguageTermCount termCount = new LanguageTermCount();
	    _termCountByLanguage.put(languageId, termCount);
	}
    }

    public Map<String, LanguageTermCount> getTermCountByLanguage() {
	return _termCountByLanguage;
    }

    public void setTermCountByLanguage(Map<String, LanguageTermCount> termCountByLanguage) {
	_termCountByLanguage = termCountByLanguage;
    }

    public static class LanguageTermCount {

	private long _approved;
	private long _forbidden;
	private long _inFinalReview;
	private long _inTranslationReview;
	private long _onHold;
	private long _pending;
	private long _termCount;

	public long getApproved() {
	    return _approved;
	}

	public long getForbidden() {
	    return _forbidden;
	}

	public long getInFinalReview() {
	    return _inFinalReview;
	}

	public long getInTranslationReview() {
	    return _inTranslationReview;
	}

	public long getOnHold() {
	    return _onHold;
	}

	public long getPending() {
	    return _pending;
	}

	public long getTermCount() {
	    return _termCount;
	}

	public void setApproved(long approved) {
	    _approved = approved;
	}

	public void setForbidden(long forbidden) {
	    _forbidden = forbidden;
	}

	public void setInFinalReview(long inFinalReview) {
	    _inFinalReview = inFinalReview;
	}

	public void setInTranslationReview(long inTranslationReview) {
	    _inTranslationReview = inTranslationReview;
	}

	public void setOnHold(long onHold) {
	    _onHold = onHold;
	}

	public void setPending(long pending) {
	    _pending = pending;
	}

	public void setTermCount(long termCount) {
	    _termCount = termCount;
	}
    }
}

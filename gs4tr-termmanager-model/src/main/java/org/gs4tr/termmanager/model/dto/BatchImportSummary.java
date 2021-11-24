package org.gs4tr.termmanager.model.dto;

import java.util.Collection;

import org.gs4tr.termmanager.model.dto.converter.ImportSummaryConverter;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;

public class BatchImportSummary {

    public static class Counts {
	/**
	 * Number of <code>Term</code> or <code>TermEntry</code> added to TMGR
	 * by import.
	 */
	private long _added;

	/**
	 * Total number of <code>Term</code> or <code>TermEntry</code> in TMGR
	 * project after import.
	 */
	private long _postTotal;

	/**
	 * Total number of <code>Term</code> or <code>TermEntry</code> in TMGR
	 * project before import.
	 */
	private long _preTotal;

	/**
	 * Number of <code>Term</code> or <code>TermEntry</code> removed from
	 * TMGR by import.
	 */
	private long _removed;

	/**
	 * Number of matched <code>Term</code> or <code>TermEntry</code> that
	 * were not updated (no difference between file and TMGR).
	 */
	private long _unchanged;

	/**
	 * Number of <code>Term</code> or <code>TermEntry</code> updated in TMGR
	 * by import.
	 */
	private long _updated;

	public void addAdded(long added) {
	    _added += added;
	}

	public void addPostTotal(long postTotal) {
	    _postTotal += postTotal;
	}

	public void addPreTotal(long preTotal) {
	    _preTotal += preTotal;
	}

	public void addRemoved(long removed) {
	    _removed += removed;
	}

	public void addUnchanged(long unchanged) {
	    _unchanged += unchanged;
	}

	public void addUpdated(long updated) {
	    _updated += updated;

	}

	public long getAdded() {
	    return _added;
	}

	public long getPostTotal() {
	    return _postTotal;
	}

	public long getPreTotal() {
	    return _preTotal;
	}

	public long getRemoved() {
	    return _removed;
	}

	public long getUnchanged() {
	    return _unchanged;
	}

	public long getUpdated() {
	    return _updated;
	}

	public void setAdded(long added) {
	    _added = added;
	}

	public void setPostTotal(long postTotal) {
	    _postTotal = postTotal;
	}

	public void setPreTotal(long preTotal) {
	    _preTotal = preTotal;
	}

	public void setRemoved(long removed) {
	    _removed = removed;
	}

	public void setUnchanged(long unchanged) {
	    _unchanged = unchanged;
	}

	public void setUpdated(long updated) {
	    _updated = updated;
	}
    }

    public static class TermCounts extends Counts {
	private Language _language;

	public TermCounts() {
	}

	public TermCounts(final String languageCode) {
	    _language = LanguageConverter.fromInternalToDto(languageCode);
	}

	public Language getLanguage() {
	    return _language;
	}

	public void setLanguage(Language language) {
	    _language = language;
	}
    }

    /**
     * @see {@link ImportSummaryConverter#formatTime(long)}
     */
    private String _importTime;
    private Counts _termEntries;
    private Collection<TermCounts> _terms;

    public String getImportTime() {
	return _importTime;
    }

    public Counts getTermEntries() {
	return _termEntries;
    }

    public Collection<TermCounts> getTerms() {
	return _terms;
    }

    public void setImportTime(String importTime) {
	_importTime = importTime;
    }

    public void setTermEntries(Counts termEntries) {
	_termEntries = termEntries;
    }

    public void setTerms(Collection<TermCounts> terms) {
	_terms = terms;
    }
}

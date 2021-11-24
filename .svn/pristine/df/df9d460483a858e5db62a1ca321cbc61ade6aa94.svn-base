package org.gs4tr.termmanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.termmanager.model.xls.report.XlsReport;

public class ImportSummary implements Serializable {

    private static final long serialVersionUID = 6879907540582675191L;

    private long _dateModified;

    private long _endTime;

    private List<ImportErrorMessage> _errorMessages;

    private String _importId;

    private Set<String> _importedTargetLanguages;

    private Set<String> _importedTermDescriptions;

    private Set<String> _importedTermEntryAttributes;

    private Set<String> _importedTermEntryIds;

    private Map<String, Long> _languageDateModified;

    private MutableInt _noDeletedTermEntries = new MutableInt(0);

    private MutableInt _noDeletedTerms = new MutableInt(0);

    private MutableInt _noDuplicatedTerms = new MutableInt(0);

    private MutableInt _noImportedTermAttributes = new MutableInt(0);

    private MutableInt _noImportedTermEntries = new MutableInt(0);

    private MutableInt _noImportedTermEntryAttributes = new MutableInt(0);

    private MutableInt _noImportedTerms = new MutableInt(0);

    private Map<String, CountWrapper> _noImportedTermsPerLanguage;

    private MutableInt _noSkippedTermEntries = new MutableInt(0);

    private MutableInt _noSkippedTerms = new MutableInt(0);

    private MutableInt _noTermEntriesWithoutSynchLanguage = new MutableInt(0);

    private MutableInt _noTermEntryForImport = new MutableInt(0);

    private MutableInt _noUpdatedTerms = new MutableInt(0);

    private ProjectTerminologyCounts _preImportCounts;

    private XlsReport _report;

    private long _startTime;

    private String _totalTimeForImport;

    private Set<String> _unchangedTermEntrieIds;

    private Set<String> _updatedTermEntrieIds;

    public ImportSummary() {
	_errorMessages = new ArrayList<>();
	_importedTargetLanguages = new HashSet<>();
	_importedTermDescriptions = new HashSet<>();
	_importedTermEntryAttributes = new HashSet<>();
	_importedTermEntryIds = new HashSet<>();
	_noImportedTermsPerLanguage = new HashMap<>();
	_report = new XlsReport();
	_languageDateModified = new HashMap<>();
	_updatedTermEntrieIds = new HashSet<>();
	_unchangedTermEntrieIds = new HashSet<>();
    }

    public ImportSummary(int noTermEntryForImport) {
	this();
	_noTermEntryForImport = new MutableInt(noTermEntryForImport);
    }

    public void addErrorMessages(ImportErrorMessage errorMessage) {
	if (_errorMessages == null) {
	    _errorMessages = new ArrayList<>();
	}
	_errorMessages.add(errorMessage);
    }

    public void addImportedTargetLanguage(String targetLanguage) {
	if (_importedTargetLanguages == null) {
	    _importedTargetLanguages = new HashSet<>();
	}
	_importedTargetLanguages.add(targetLanguage);
    }

    public void addImportedTermDescription(String termDescription) {
	if (_importedTermDescriptions == null) {
	    _importedTermDescriptions = new HashSet<>();
	}
	_importedTermDescriptions.add(termDescription);
    }

    public void addImportedTermEntryAttribute(String termEntryAttribute) {
	if (_importedTermEntryAttributes == null) {
	    _importedTermEntryAttributes = new HashSet<>();
	}
	_importedTermEntryAttributes.add(termEntryAttribute);
    }

    public void addImportedTermEntryId(String importedTermEntryId) {
	if (_importedTermEntryIds == null) {
	    _importedTermEntryIds = new HashSet<>();
	}
	_importedTermEntryIds.add(importedTermEntryId);
    }

    public long getDateModified() {
	return _dateModified;
    }

    public long getEndTime() {
	return _endTime;
    }

    public List<ImportErrorMessage> getErrorMessages() {
	return _errorMessages;
    }

    public String getImportId() {
	return _importId;
    }

    public Set<String> getImportedTargetLanguages() {
	return _importedTargetLanguages;
    }

    public Set<String> getImportedTermDescriptions() {
	return _importedTermDescriptions;
    }

    public Set<String> getImportedTermEntryAttributes() {
	return _importedTermEntryAttributes;
    }

    public Set<String> getImportedTermEntryIds() {
	return _importedTermEntryIds;
    }

    public Map<String, Long> getLanguageDateModified() {
	return _languageDateModified;
    }

    public MutableInt getNoDeletedTermEntries() {
	return _noDeletedTermEntries;
    }

    public MutableInt getNoDeletedTerms() {
	return _noDeletedTerms;
    }

    public MutableInt getNoDuplicatedTerms() {
	return _noDuplicatedTerms;
    }

    public MutableInt getNoImportedTermAttributes() {
	return _noImportedTermAttributes;
    }

    public MutableInt getNoImportedTermEntries() {
	return _noImportedTermEntries;
    }

    public MutableInt getNoImportedTermEntryAttributes() {
	return _noImportedTermEntryAttributes;
    }

    public MutableInt getNoImportedTerms() {
	return _noImportedTerms;
    }

    public Map<String, CountWrapper> getNoImportedTermsPerLanguage() {
	return _noImportedTermsPerLanguage;
    }

    public MutableInt getNoSkippedTermEntries() {
	return _noSkippedTermEntries;
    }

    public MutableInt getNoSkippedTerms() {
	return _noSkippedTerms;
    }

    public MutableInt getNoTermEntriesWithoutSynchLanguage() {
	return _noTermEntriesWithoutSynchLanguage;
    }

    public MutableInt getNoTermEntryForImport() {
	return _noTermEntryForImport;
    }

    public MutableInt getNoUpdatedTerms() {
	return _noUpdatedTerms;
    }

    public ProjectTerminologyCounts getPreImportCounts() {
	return _preImportCounts;
    }

    public XlsReport getReport() {
	return _report;
    }

    public long getStartTime() {
	return _startTime;
    }

    public String getTotalTimeForImport() {
	return _totalTimeForImport;
    }

    public Set<String> getUnchangedTermEntrieIds() {
	return _unchangedTermEntrieIds;
    }

    public Set<String> getUpdatedTermEntrieIds() {
	return _updatedTermEntrieIds;
    }

    public Set<String> getUpdatedTermEntryIds() {
	return _updatedTermEntrieIds;
    }

    public void setDateModified(long dateModified) {
	_dateModified = dateModified;
    }

    public void setEndTime(long endTime) {
	_endTime = endTime;
    }

    public void setImportId(String importId) {
	_importId = importId;
    }

    public void setNoImportedTermsPerLanguage(Map<String, CountWrapper> noImportedTermsPerLanguage) {
	_noImportedTermsPerLanguage = noImportedTermsPerLanguage;
    }

    public void setPreImportCounts(ProjectTerminologyCounts preImportCounts) {
	_preImportCounts = preImportCounts;
    }

    public void setStartTime(long startTime) {
	_startTime = startTime;
    }

    public void setTotalTimeForImport(String totalTimeForImport) {
	_totalTimeForImport = totalTimeForImport;
    }

    public static class CountWrapper implements Serializable {

	private static final long serialVersionUID = -6978201114766999781L;

	private final MutableInt _addedApprovedCount;
	private final MutableInt _addedApprovedStatisticsCount;

	private final MutableInt _addedBlacklistedCount;
	private final MutableInt _addedBlacklistedStatisticsCount;

	private final MutableInt _addedOnHoldCount;
	private final MutableInt _addedOnHoldStatisticsCount;

	private final MutableInt _addedPendingCount;
	private final MutableInt _addedPendingStatisticsCount;

	private final MutableInt _addedTermCount;
	private final MutableInt _deletedTerms;
	private final MutableInt _termCount;

	private Set<String> _unchangedTermIds;
	private Set<String> _updatedTermIds;

	public CountWrapper() {
	    _termCount = new MutableInt(0);

	    _addedApprovedCount = new MutableInt(0);
	    _addedBlacklistedCount = new MutableInt(0);
	    _addedPendingCount = new MutableInt(0);
	    _addedOnHoldCount = new MutableInt(0);

	    _deletedTerms = new MutableInt(0);
	    _addedTermCount = new MutableInt(0);

	    _addedOnHoldStatisticsCount = new MutableInt(0);
	    _addedApprovedStatisticsCount = new MutableInt(0);
	    _addedPendingStatisticsCount = new MutableInt(0);
	    _addedBlacklistedStatisticsCount = new MutableInt(0);

	    _updatedTermIds = new HashSet<>();
	    _unchangedTermIds = new HashSet<>();
	}

	public MutableInt getAddedApprovedCount() {
	    return _addedApprovedCount;
	}

	public MutableInt getAddedApprovedStatisticsCount() {
	    return _addedApprovedStatisticsCount;
	}

	public MutableInt getAddedBlacklistedCount() {
	    return _addedBlacklistedCount;
	}

	public MutableInt getAddedBlacklistedStatisticsCount() {
	    return _addedBlacklistedStatisticsCount;
	}

	public MutableInt getAddedOnHoldCount() {
	    return _addedOnHoldCount;
	}

	public MutableInt getAddedOnHoldStatisticsCount() {
	    return _addedOnHoldStatisticsCount;
	}

	public MutableInt getAddedPendingCount() {
	    return _addedPendingCount;
	}

	public MutableInt getAddedPendingStatisticsCount() {
	    return _addedPendingStatisticsCount;
	}

	public MutableInt getAddedTermCount() {
	    return _addedTermCount;
	}

	public MutableInt getDeletedTerms() {
	    return _deletedTerms;
	}

	public MutableInt getTermCount() {
	    return _termCount;
	}

	public Set<String> getUnchangedTermIds() {
	    return _unchangedTermIds;
	}

	public Set<String> getUpdatedTermIds() {
	    return _updatedTermIds;
	}
    }
}

package org.gs4tr.termmanager.service.model.command;

import java.util.List;

public class ForbidTermCommand {

    private boolean _approveTerm = false;

    private List<String> _sourceIds;

    private String _sourceLanguage;

    private List<String> _targetIds;

    private String _termEntryId;

    private List<String> _termEntryIds;

    private List<String> _termIds;

    private boolean _wholeTermEntry = false;

    public List<String> getSourceIds() {
	return _sourceIds;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public List<String> getTargetIds() {
	return _targetIds;
    }

    public String getTermEntryId() {
	return _termEntryId;
    }

    public List<String> getTermEntryIds() {
	return _termEntryIds;
    }

    public List<String> getTermIds() {
	return _termIds;
    }

    public boolean isApproveTerm() {
	return _approveTerm;
    }

    public boolean isWholeTermEntry() {
	return _wholeTermEntry;
    }

    public void setApproveTerm(boolean approveTerm) {
	_approveTerm = approveTerm;
    }

    public void setSourceIds(List<String> sourceIds) {
	_sourceIds = sourceIds;
    }

    public void setSourceLanguage(String sourceLangugae) {
	_sourceLanguage = sourceLangugae;
    }

    public void setTargetIds(List<String> targetIds) {
	_targetIds = targetIds;
    }

    public void setTermEntryId(String termEntryId) {
	_termEntryId = termEntryId;
    }

    public void setTermEntryIds(List<String> termEntryIds) {
	_termEntryIds = termEntryIds;
    }

    public void setTermIds(List<String> termIds) {
	_termIds = termIds;
    }

    public void setWholeTermEntry(boolean wholeTermEntry) {
	_wholeTermEntry = wholeTermEntry;
    }
}

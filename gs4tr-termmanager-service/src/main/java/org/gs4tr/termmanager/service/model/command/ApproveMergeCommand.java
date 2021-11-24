package org.gs4tr.termmanager.service.model.command;

import java.util.List;

public class ApproveMergeCommand {

    private String _matchedTermEntryId;

    private Long _projectId;

    private String _sourceLanguage;

    private List<String> _termIds;

    public String getMatchedTermEntryId() {
	return _matchedTermEntryId;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public List<String> getTermIds() {
	return _termIds;
    }

    public void setMatchedTermEntryId(String matchedTermEntryId) {
	_matchedTermEntryId = matchedTermEntryId;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setTermIds(List<String> termIds) {
	_termIds = termIds;
    }
}

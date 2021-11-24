package org.gs4tr.termmanager.service.model.command;

import java.util.List;
import java.util.Set;

public class LookupTermCommand {

    private String _languageId;

    private Long _projectId;

    private List<String> _termEntryIds;

    private Set<String> _terms;

    public String getLanguageId() {
	return _languageId;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public List<String> getTermEntryIds() {
	return _termEntryIds;
    }

    public Set<String> getTerms() {
	return _terms;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setTermEntryIds(List<String> termEntryIds) {
	_termEntryIds = termEntryIds;
    }

    public void setTerms(Set<String> terms) {
	_terms = terms;
    }
}

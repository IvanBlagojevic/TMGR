package org.gs4tr.termmanager.model.glossary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public class TermEntry implements Serializable {

    private static final long serialVersionUID = 2307214914895136398L;

    private Action _action = Action.NOT_AVAILABLE;

    private Long _dateCreated;

    private Long _dateModified;

    private Set<Description> _descriptions;

    private Map<String, Set<Term>> _languageTerms;

    // link between termEntry and termEntry in submission
    private String _parentUuId;

    private Long _projectId;

    private String _projectName;

    private Long _revisionId;

    private String _shortCode;

    private Long _submissionId;

    private String _submissionName;

    private String _submitter;

    private String _userCreated;

    private String _userModified;

    private String _uuId;

    public TermEntry() {
	_dateCreated = new Date().getTime();
	_dateModified = new Date().getTime();
    }

    public TermEntry(Long projectId, String projectShortCode, String username) {
	this();
	_projectId = projectId;
	_shortCode = projectShortCode;
	_userCreated = username;
	_userModified = username;
    }

    public TermEntry(Long projectId, String shortCode, String username, Set<Term> terms) {
	this(projectId, shortCode, username);

	if (terms != null) {
	    for (Term term : terms) {
		addTerm(term);
	    }
	}
    }

    public TermEntry(Long projectId, String shortCode, String username, Set<Term> terms,
	    Set<Description> termEntrydescriptions) {
	this(projectId, shortCode, username, terms);

	if (termEntrydescriptions != null) {
	    for (Description description : termEntrydescriptions) {
		addDescription(description);
	    }
	}
    }

    public TermEntry(TermEntry termEntry) {
	_dateCreated = termEntry.getDateCreated();
	_dateModified = termEntry.getDateModified();
	_descriptions = cloneDescriptions(termEntry);
	_languageTerms = cloneLanguageTerms(termEntry);
	_projectId = termEntry.getProjectId();
	_projectName = termEntry.getProjectName();
	_shortCode = termEntry.getShortCode();
	_userCreated = termEntry.getUserCreated();
	_userModified = termEntry.getUserModified();
	_uuId = termEntry.getUuId();
    }

    public final void addDescription(Description description) {
	if (_descriptions == null) {
	    _descriptions = new LinkedHashSet<>();
	}

	_descriptions.add(description);
    }

    public final void addTerm(Term term) {
	if (_languageTerms == null) {
	    _languageTerms = new HashMap<>();
	}

	String languageId = term.getLanguageId();

	Set<Term> termSet = _languageTerms.computeIfAbsent(languageId, k -> new HashSet<>());

	if (termSet.isEmpty()) {
	    term.setFirst(Boolean.TRUE);
	} else {
	    term.setFirst(Boolean.FALSE);
	}

	termSet.add(term);
    }

    public final Set<Description> cloneDescriptions(TermEntry termEntry) {
	Set<Description> clone = new HashSet<>();

	Set<Description> descriptions = termEntry.getDescriptions();
	if (Objects.nonNull(descriptions)) {
	    for (Description desc : descriptions) {
		clone.add(new Description(desc));
	    }
	}

	return clone;
    }

    public final Map<String, Set<Term>> cloneLanguageTerms(TermEntry termEntry) {
	Map<String, Set<Term>> clone = new HashMap<>();

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	if (Objects.nonNull(languageTerms)) {
	    for (Entry<String, Set<Term>> e : languageTerms.entrySet()) {
		String languageId = e.getKey();

		Set<Term> termsClone = new HashSet<>();
		e.getValue().forEach(t -> termsClone.add(new Term(t)));

		clone.put(languageId, termsClone);
	    }
	}

	return clone;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	TermEntry other = (TermEntry) obj;
	if (_descriptions == null) {
	    if (other._descriptions != null)
		return false;
	} else if (!_descriptions.equals(other._descriptions))
	    return false;
	if (_languageTerms == null) {
	    if (other._languageTerms != null)
		return false;
	} else if (!_languageTerms.equals(other._languageTerms))
	    return false;
	if (_projectId == null) {
	    if (other._projectId != null)
		return false;
	} else if (!_projectId.equals(other._projectId))
	    return false;
	if (_submissionId == null) {
	    return other._submissionId == null;
	} else
	    return _submissionId.equals(other._submissionId);
    }

    public Action getAction() {
	return _action;
    }

    public Long getDateCreated() {
	return _dateCreated;
    }

    public Long getDateModified() {
	return _dateModified;
    }

    public Set<Description> getDescriptions() {
	return _descriptions;
    }

    public Map<String, Set<Term>> getLanguageTerms() {
	return _languageTerms;
    }

    public String getParentUuId() {
	return _parentUuId;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public String getProjectName() {
	return _projectName;
    }

    public Long getRevisionId() {
	return _revisionId;
    }

    public String getShortCode() {
	return _shortCode;
    }

    public Long getSubmissionId() {
	return _submissionId;
    }

    public String getSubmissionName() {
	return _submissionName;
    }

    public String getSubmitter() {
	return _submitter;
    }

    public String getUserCreated() {
	return _userCreated;
    }

    public String getUserModified() {
	return _userModified;
    }

    public String getUuId() {
	return _uuId;
    }

    public List<Term> ggetAllTerms() {
	if (_languageTerms == null) {
	    return null;
	}

	List<Term> terms = new ArrayList<>();
	for (Entry<String, Set<Term>> entry : _languageTerms.entrySet()) {
	    for (Term term : entry.getValue()) {
		terms.add(term);
	    }
	}

	return terms;
    }

    public Term ggetTermById(String termId) {
	if (_languageTerms == null) {
	    return null;
	}

	for (Entry<String, Set<Term>> entry : _languageTerms.entrySet()) {
	    for (Term term : entry.getValue()) {
		if (termId.equals(term.getUuId())) {
		    return term;
		}
	    }
	}

	return null;
    }

    public List<Term> ggetTerms() {
	if (_languageTerms == null) {
	    return null;
	}

	List<Term> terms = new ArrayList<>();
	for (Entry<String, Set<Term>> entry : _languageTerms.entrySet()) {
	    for (Term term : entry.getValue()) {
		if (!term.isDisabled()) {
		    terms.add(term);
		}
	    }
	}

	return terms;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_descriptions == null) ? 0 : _descriptions.hashCode());
	result = prime * result + ((_languageTerms == null) ? 0 : _languageTerms.hashCode());
	result = prime * result + ((_projectId == null) ? 0 : _projectId.hashCode());
	result = prime * result + ((_submissionId == null) ? 0 : _submissionId.hashCode());
	return result;
    }

    public void setAction(Action action) {
	_action = action;
    }

    public void setDateCreated(Long dateCreated) {
	_dateCreated = dateCreated;
    }

    public void setDateModified(Long dateModified) {
	_dateModified = dateModified;
    }

    public void setDescriptions(Set<Description> descriptions) {
	_descriptions = descriptions;
    }

    public void setLanguageTerms(Map<String, Set<Term>> languageTerms) {
	_languageTerms = languageTerms;
    }

    public void setParentUuId(String parentUuId) {
	_parentUuId = parentUuId;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setRevisionId(Long revisionId) {
	_revisionId = revisionId;
    }

    public void setShortCode(String shortCode) {
	_shortCode = shortCode;
    }

    public void setSubmissionId(Long submissionId) {
	_submissionId = submissionId;
    }

    public void setSubmissionName(String submissionName) {
	_submissionName = submissionName;
    }

    public void setSubmitter(String submitter) {
	_submitter = submitter;
    }

    public void setUserCreated(String userCreated) {
	_userCreated = userCreated;
    }

    public void setUserModified(String userModified) {
	_userModified = userModified;
    }

    public void setUuId(String uuId) {
	_uuId = uuId;
    }

    @Override
    public String toString() {
	return "TermEntry [_descriptions=" + _descriptions + ", _languageTerms=" + _languageTerms + ", _parentUuId="
		+ _parentUuId + ", _projectId=" + _projectId + ", _shortCode=" + _shortCode + ", _submissionId="
		+ _submissionId + ", _userCreated=" + _userCreated + ", _userModified=" + _userModified + ", _uuId="
		+ _uuId + "]";
    }
}

package org.gs4tr.termmanager.model.glossary;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Term implements Serializable, Cloneable {

    private static final long serialVersionUID = 8928366612095051178L;

    private String _assignee;

    private Boolean _canceled;

    private Set<Comment> _comments;

    private Boolean _commited = Boolean.TRUE;

    private Long _dateCompleted;

    private Long _dateCreated;

    private Long _dateModified;

    private Long _dateSubmitted;

    private Set<Description> _descriptions;

    private Boolean _disabled = Boolean.FALSE;

    private Boolean _first = Boolean.FALSE;

    private Boolean _forbidden = Boolean.FALSE;

    private boolean _fuzzy;

    private Boolean _inTranslationAsSource = Boolean.FALSE;

    private String _languageId;

    private String _name;

    // link between term and term in submission
    private String _parentUuId;

    private Priority _priority;

    private Long _projectId;

    private Boolean _reviewRequired;

    private String _status;

    private String _statusOld;

    private Long _submissionId;

    private String _submissionName;

    private String _submitter;

    private String _tempText;

    private TermEntry _termEntry;

    private String _termEntryId;

    private String _userCreated;

    private Long _userLatestChange;

    private String _userModified;

    private String _uuId;

    public Term() {

    }

    public Term(String languageId, String name, boolean forbidden, String status, String user) {
	this();
	_languageId = languageId;
	_name = name;
	_forbidden = forbidden;
	_status = status;
	_userCreated = user;
	_userModified = user;
    }

    public Term(Term term) {
	_dateCreated = term.getDateCreated();
	_dateModified = term.getDateModified();
	_descriptions = cloneDescriptions(term);
	_disabled = term.isDisabled();
	_first = term.isFirst();
	_forbidden = term.isForbidden();
	_languageId = term.getLanguageId();
	_name = term.getName();
	_projectId = term.getProjectId();
	_status = term.getStatus();
	_termEntry = term.ggetTermEntry();
	_termEntryId = term.getTermEntryId();
	_userCreated = term.getUserCreated();
	_userModified = term.getUserModified();
	_uuId = term.getUuId();
    }

    public void addComment(Comment comment) {
	if (_comments == null) {
	    _comments = new HashSet<>();
	}

	_comments.add(comment);
    }

    public void addDescription(Description description) {
	if (_descriptions == null) {
	    _descriptions = new HashSet<>();
	}

	_descriptions.add(description);
    }

    public final Set<Description> cloneDescriptions(Term term) {
	Set<Description> clone = new HashSet<>();

	Set<Description> descriptions = term.getDescriptions();
	if (Objects.nonNull(descriptions)) {
	    for (Description desc : descriptions) {
		clone.add(new Description(desc));
	    }
	}

	return clone;
    }

    public Term cloneTerm() {
	try {
	    return (Term) super.clone();
	} catch (CloneNotSupportedException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Term other = (Term) obj;
	if (_descriptions == null) {
	    if (other._descriptions != null)
		return false;
	} else if (!_descriptions.equals(other._descriptions))
	    return false;
	if (_disabled == null) {
	    if (other._disabled != null)
		return false;
	} else if (!_disabled.equals(other._disabled))
	    return false;
	if (_forbidden == null) {
	    if (other._forbidden != null)
		return false;
	} else if (!_forbidden.equals(other._forbidden))
	    return false;
	if (_languageId == null) {
	    if (other._languageId != null)
		return false;
	} else if (!_languageId.equals(other._languageId))
	    return false;
	if (_name == null) {
	    return other._name == null;
	} else
	    return _name.equals(other._name);
    }

    public String getAssignee() {
	return _assignee;
    }

    public Boolean getCanceled() {
	return _canceled;
    }

    public Set<Comment> getComments() {
	return _comments;
    }

    public Boolean getCommited() {
	return _commited;
    }

    public Long getDateCompleted() {
	return _dateCompleted;
    }

    public Long getDateCreated() {
	return _dateCreated;
    }

    public Long getDateModified() {
	return _dateModified;
    }

    public Long getDateSubmitted() {
	return _dateSubmitted;
    }

    public Set<Description> getDescriptions() {
	return _descriptions;
    }

    public Boolean getFirst() {
	return _first;
    }

    public Boolean getForbidden() {
	return _forbidden;
    }

    public Boolean getInTranslationAsSource() {
	return _inTranslationAsSource;
    }

    public String getLanguageId() {
	return _languageId;
    }

    public String getName() {
	return _name;
    }

    public String getParentUuId() {
	return _parentUuId;
    }

    public Priority getPriority() {
	if (_priority == null) {
	    _priority = new Priority();
	}
	return _priority;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public Boolean getReviewRequired() {
	return _reviewRequired;
    }

    public String getStatus() {
	return _status;
    }

    public String getStatusOld() {
	return _statusOld;
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

    public String getTempText() {
	return _tempText;
    }

    public String getTermEntryId() {
	return _termEntryId;
    }

    public String getUserCreated() {
	return _userCreated;
    }

    public Long getUserLatestChange() {
	return _userLatestChange;
    }

    public String getUserModified() {
	return _userModified;
    }

    public String getUuId() {
	return _uuId;
    }

    public TermEntry ggetTermEntry() {
	return _termEntry;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_descriptions == null) ? 0 : _descriptions.hashCode());
	result = prime * result + ((_disabled == null) ? 0 : _disabled.hashCode());
	result = prime * result + ((_forbidden == null) ? 0 : _forbidden.hashCode());
	result = prime * result + ((_languageId == null) ? 0 : _languageId.hashCode());
	result = prime * result + ((_name == null) ? 0 : _name.hashCode());
	return result;
    }

    public Boolean isDisabled() {
	return _disabled;
    }

    public Boolean isFirst() {
	return _first;
    }

    public Boolean isForbidden() {
	return _forbidden;
    }

    public boolean isFuzzy() {
	return _fuzzy;
    }

    public boolean isSyncEquals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	Term other = (Term) obj;
	if (_disabled == null) {
	    if (other._disabled != null) {
		return false;
	    }
	} else if (!_disabled.equals(other._disabled)) {
	    return false;
	}
	if (_languageId == null) {
	    if (other._languageId != null) {
		return false;
	    }
	} else if (!_languageId.equals(other._languageId)) {
	    return false;
	}
	if (_name == null) {
	    return other._name == null;
	} else
	    return _name.equals(other._name);
    }

    public void setAssignee(String assignee) {
	_assignee = assignee;
    }

    public void setCanceled(Boolean canceled) {
	_canceled = canceled;
    }

    public void setComments(Set<Comment> comments) {
	_comments = comments;
    }

    public void setCommited(Boolean commited) {
	_commited = commited;
    }

    public void setDateCompleted(Long dateCompleted) {
	_dateCompleted = dateCompleted;
    }

    public void setDateCreated(Long dateCreated) {
	_dateCreated = dateCreated;
    }

    public void setDateModified(Long dateModified) {
	_dateModified = dateModified;
    }

    public void setDateSubmitted(Long dateSubmitted) {
	_dateSubmitted = dateSubmitted;
    }

    public void setDescriptions(Set<Description> descriptions) {
	_descriptions = descriptions;
    }

    public void setDisabled(Boolean disabled) {
	_disabled = disabled;
    }

    public void setFirst(Boolean first) {
	_first = first;
    }

    public void setForbidden(Boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setFuzzy(boolean fuzzy) {
	_fuzzy = fuzzy;
    }

    public void setInTranslationAsSource(Boolean inTranslationAsSource) {
	_inTranslationAsSource = inTranslationAsSource;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setParentUuId(String parentUuId) {
	_parentUuId = parentUuId;
    }

    public void setPriority(Priority priority) {
	_priority = priority;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setReviewRequired(Boolean reviewRequired) {
	_reviewRequired = reviewRequired;
    }

    public void setStatus(String status) {
	_status = status;
    }

    public void setStatusOld(String statusOld) {
	_statusOld = statusOld;
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

    public void setTempText(String tempText) {
	_tempText = tempText;
    }

    public void setTermEntry(TermEntry termEntry) {
	_termEntry = termEntry;
    }

    public void setTermEntryId(String termEntryId) {
	_termEntryId = termEntryId;
    }

    public void setUserCreated(String userCreated) {
	_userCreated = userCreated;
    }

    public void setUserLatestChange(Long userLatestChange) {
	_userLatestChange = userLatestChange;
    }

    public void setUserModified(String userModified) {
	_userModified = userModified;
    }

    public void setUuId(String uuId) {
	_uuId = uuId;
    }

    @Override
    public String toString() {
	return "Term [_assignee=" + _assignee + ", _canceled=" + _canceled + ", _descriptions=" + _descriptions
		+ ", _disabled=" + _disabled + ", _first=" + _first + ", _forbidden=" + _forbidden
		+ ", _inTranslationAsSource=" + _inTranslationAsSource + ", _languageId=" + _languageId + ", _name="
		+ _name + ", _projectId=" + _projectId + ", _reviewRequired=" + _reviewRequired + ", _status=" + _status
		+ ", _submissionId=" + _submissionId + ", _submitter=" + _submitter + ", _termEntryId=" + _termEntryId
		+ ", _userCreated=" + _userCreated + ", _userModified=" + _userModified + ", _uuId=" + _uuId + "]";
    }
}

package org.gs4tr.termmanager.model.dto;

public class ExportTermModel {
    private Long _creationDate;
    private String _creationUser;
    private Boolean _forbidden;
    private Long _modificationDate;
    private String _modificationUser;
    private String _operation;
    private String _source;
    private Description[] _sourceAttributes;
    private String[] _suggestions;
    private String _target;
    private Description[] _targetAttributes;
    private String _ticket;

    public Long getCreationDate() {
	return _creationDate;
    }

    public String getCreationUser() {
	return _creationUser;
    }

    public Boolean getForbidden() {
	return _forbidden;
    }

    public Long getModificationDate() {
	return _modificationDate;
    }

    public String getModificationUser() {
	return _modificationUser;
    }

    public String getOperation() {
	return _operation;
    }

    public String getSource() {
	return _source;
    }

    public Description[] getSourceAttributes() {
	return _sourceAttributes;
    }

    public String[] getSuggestions() {
	return _suggestions;
    }

    public String getTarget() {
	return _target;
    }

    public Description[] getTargetAttributes() {
	return _targetAttributes;
    }

    public String getTicket() {
	return _ticket;
    }

    public void setCreationDate(Long creationDate) {
	_creationDate = creationDate;
    }

    public void setCreationUser(String creationUser) {
	_creationUser = creationUser;
    }

    public void setForbidden(Boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setModificationDate(Long modificationDate) {
	_modificationDate = modificationDate;
    }

    public void setModificationUser(String modificationUser) {
	_modificationUser = modificationUser;
    }

    public void setOperation(String operation) {
	_operation = operation;
    }

    public void setSource(String source) {
	_source = source;
    }

    public void setSourceAttributes(Description[] sourceAttributes) {
	_sourceAttributes = sourceAttributes;
    }

    public void setSuggestions(String[] suggestions) {
	_suggestions = suggestions;
    }

    public void setTarget(String target) {
	_target = target;
    }

    public void setTargetAttributes(Description[] targetAttributes) {
	_targetAttributes = targetAttributes;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }
}

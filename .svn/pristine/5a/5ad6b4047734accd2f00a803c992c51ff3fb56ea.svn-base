package org.gs4tr.termmanager.glossaryV2;

import org.gs4tr.tm3.httpconnector.resolver.model.GlossaryUpdateRequest;

public class GlossaryUpdateRequestExt extends GlossaryUpdateRequest {

    private static final long serialVersionUID = 1L;

    private long _projectId;

    private String _shortCode;

    private String _sourceLanguageId;

    private String _targetLanguageId;

    private String _username;

    public GlossaryUpdateRequestExt(GlossaryUpdateRequest request) {
	super(request);
	setOption(request.getOption());
    }

    public long getProjectId() {
	return _projectId;
    }

    public String getShortCode() {
	return _shortCode;
    }

    public String getSourceLanguageId() {
	return _sourceLanguageId;
    }

    public String getTargetLanguageId() {
	return _targetLanguageId;
    }

    public String getUsername() {
	return _username;
    }

    public void setProjectId(long projectId) {
	_projectId = projectId;
    }

    public void setShortCode(String shortCode) {
	_shortCode = shortCode;
    }

    public void setSourceLanguageId(String sourceLanguageId) {
	_sourceLanguageId = sourceLanguageId;
    }

    public void setTargetLanguageId(String targetLanguageId) {
	_targetLanguageId = targetLanguageId;
    }

    public void setUsername(String username) {
	_username = username;
    }

    @Override
    public String toString() {
	return "GlossaryUpdateRequestExt [_projectId=" + _projectId + ", _shortCode=" + _shortCode
		+ ", _sourceLanguageId=" + _sourceLanguageId + ", _targetLanguageId=" + _targetLanguageId
		+ ", _username=" + _username + ", getOption()=" + getOption() + ", size()=" + size() + ", toString()="
		+ super.toString() + "]";
    }
}

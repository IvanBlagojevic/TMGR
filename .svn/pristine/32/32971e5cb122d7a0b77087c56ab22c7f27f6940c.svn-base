package org.gs4tr.termmanager.glossaryV2.blacklist;

import org.gs4tr.tm3.httpconnector.resolver.model.BlacklistUpdateRequest;

public class BlacklistUpdateRequestExt extends BlacklistUpdateRequest {

    private static final long serialVersionUID = 1L;

    private long _projectId;

    private String _shortCode;

    private String _sourceLanguageId;

    private String _targetLanguageId;

    public BlacklistUpdateRequestExt(BlacklistUpdateRequest request) {
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

    @Override
    public String toString() {
	return "BlacklistUpdateRequestExt [_projectId=" + _projectId + ", _shortCode=" + _shortCode
		+ ", _sourceLanguageId=" + _sourceLanguageId + ", _targetLanguageId=" + _targetLanguageId
		+ ", getOption()=" + getOption() + ", size()=" + size() + ", toString()=" + super.toString() + "]";
    }
}

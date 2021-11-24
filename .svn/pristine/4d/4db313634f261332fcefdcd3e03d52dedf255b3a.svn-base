package org.gs4tr.termmanager.model.dto;

import java.util.Collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Term")
public class TermV2Model {

    private String _status;

    private String _termId;

    protected Collection<Description> _descriptions;

    protected boolean _forbidden;

    protected boolean _fuzzy;

    protected String _termText;

    @ApiModelProperty(value = "Collection of attributes and notes.")
    public Collection<Description> getDescriptions() {
	return _descriptions;
    }

    @ApiModelProperty(value = "Term status.For example \"processing\", \"completed\", \"blacklisted\" etc.")
    public String getStatus() {
	return _status;
    }

    @ApiModelProperty(value = "Term id.")
    public String getTermId() {
	return _termId;
    }

    @ApiModelProperty(value = "Term text.", required = true)
    public String getTermText() {
	return _termText;
    }

    @ApiModelProperty(value = "Blacklisted term flag. If true, term is blacklisted.")
    public boolean isForbidden() {
	return _forbidden;
    }

    @ApiModelProperty(value = "Fuzzy match flag. If true, term matches searched pattern approximately.", example = "false")
    public boolean isFuzzy() {
	return _fuzzy;
    }

    public void setDescriptions(Collection<Description> descriptions) {
	_descriptions = descriptions;
    }

    public void setForbidden(boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setFuzzy(boolean fuzzy) {
	_fuzzy = fuzzy;
    }

    public void setStatus(String status) {
	_status = status;
    }

    public void setTermId(String termId) {
	_termId = termId;
    }

    public void setTermText(String termText) {
	_termText = termText;
    }

    @Override
    public String toString() {
	return "TermV2Model [_descriptions=" + _descriptions + ", _forbidden=" + _forbidden + ", _termText=" + _termText
		+ ", _status=" + _status + "]";
    }
}

package org.gs4tr.termmanager.model.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import io.swagger.annotations.ApiModelProperty;

public class ImportErrorMessage {
    @XStreamAlias("errorMessage")
    private String _errorMessage;

    @XStreamAlias("termEntryCounter")
    private int _termEntryCounter;

    @ApiModelProperty(value = "Concrete error message text.")
    public String getErrorMessage() {
	return _errorMessage;
    }

    @ApiModelProperty(value = "Term entry number that failed with concrete error message.")
    public int getTermEntryCounter() {
	return _termEntryCounter;
    }

    public void setErrorMessage(String errorMessage) {
	_errorMessage = errorMessage;
    }

    public void setTermEntryCounter(int termEntryCounter) {
	_termEntryCounter = termEntryCounter;
    }
}
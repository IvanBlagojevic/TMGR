package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.termmanager.service.model.command.ForbidTermCommand;

public class DtoForbidTermCommand implements DtoTaskHandlerCommand<ForbidTermCommand> {

    private boolean _approveTerm = false;

    private String _sourceLanguage;

    private String[] _sourceTickets;

    private String[] _targetTickets;

    private String _termEntryTicket;

    private String[] _termEntryTickets;

    private String[] _termIds;

    private boolean _wholeTermEntry = false;

    @Override
    public ForbidTermCommand convertToInternalTaskHandlerCommand() {
	ForbidTermCommand command = new ForbidTermCommand();

	if (ArrayUtils.isNotEmpty(getTermIds())) {
	    command.setTermIds(Arrays.asList(getTermIds()));
	}

	if (ArrayUtils.isNotEmpty(getSourceTickets())) {
	    command.setSourceIds(Arrays.asList(getSourceTickets()));
	}

	if (ArrayUtils.isNotEmpty(getTargetTickets())) {
	    command.setSourceIds(Arrays.asList(getTargetTickets()));
	}

	command.setTermEntryId(getTermEntryTicket());
	command.setApproveTerm(isApproveTerm());

	// option to delete whole termEntry
	command.setWholeTermEntry(isWholeTermEntry());

	if (ArrayUtils.isNotEmpty(getTermEntryTickets())) {
	    command.setTermEntryIds(Arrays.asList(getTermEntryTickets()));
	}

	command.setSourceLanguage(getSourceLanguage());

	return command;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public String[] getSourceTickets() {
	return _sourceTickets;
    }

    public String[] getTargetTickets() {
	return _targetTickets;
    }

    public String getTermEntryTicket() {
	return _termEntryTicket;
    }

    public String[] getTermEntryTickets() {
	return _termEntryTickets;
    }

    public String[] getTermIds() {
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

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setSourceTickets(String[] sourceTickets) {
	_sourceTickets = sourceTickets;
    }

    public void setTargetTickets(String[] targetTickets) {
	_targetTickets = targetTickets;
    }

    public void setTermEntryTicket(String termEntryTicket) {
	_termEntryTicket = termEntryTicket;
    }

    public void setTermEntryTickets(String[] termEntryTickets) {
	_termEntryTickets = termEntryTickets;
    }

    public void setTermIds(String[] termIds) {
	_termIds = termIds;
    }

    public void setWholeTermEntry(boolean wholeTermEntry) {
	_wholeTermEntry = wholeTermEntry;
    }

}

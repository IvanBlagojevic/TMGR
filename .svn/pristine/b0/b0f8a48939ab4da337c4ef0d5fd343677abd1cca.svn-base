package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.termmanager.model.dto.converter.TermEntryTranslationUnitConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.CommitTranslationCommand;

public class DtoCommitTranslationCommand extends DtoBaseDashboardCommand
	implements DtoTaskHandlerCommand<CommitTranslationCommand> {

    private String _submissionTicket;

    private String _targetLanguage;

    private String[] _targetTermTickets;

    @Override
    public CommitTranslationCommand convertToInternalTaskHandlerCommand() {
	CommitTranslationCommand command = new CommitTranslationCommand();
	command.setSubmissionId(TicketConverter.fromDtoToInternal(getSubmissionTicket(), Long.class));
	command.setTermEntryTranslationUnits(
		TermEntryTranslationUnitConverter.fromDtoToInternal(getTranslationUnits()));
	command.setTargetLanguage(getTargetLanguage());
	String[] targetTermTickets = getTargetTermTickets();
	if (ArrayUtils.isNotEmpty(targetTermTickets)) {
	    command.setTargetTermIds(Arrays.asList(getTargetTermTickets()));
	}

	return command;
    }

    public String getSubmissionTicket() {
	return _submissionTicket;
    }

    public String getTargetLanguage() {
	return _targetLanguage;
    }

    public String[] getTargetTermTickets() {
	return _targetTermTickets;
    }

    public void setSubmissionTicket(String submissionTicket) {
	_submissionTicket = submissionTicket;
    }

    public void setTargetLanguage(String targetLanguage) {
	_targetLanguage = targetLanguage;
    }

    public void setTargetTermTickets(String[] targetTermTickets) {
	_targetTermTickets = targetTermTickets;
    }
}

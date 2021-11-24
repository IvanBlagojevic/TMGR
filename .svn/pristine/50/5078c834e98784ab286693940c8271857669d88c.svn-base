package org.gs4tr.termmanager.service.model.command.dto;

import static java.util.Arrays.asList;

import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.ApproveTermTranslationCommand;

public class DtoApproveTermTranslationCommand implements DtoTaskHandlerCommand<ApproveTermTranslationCommand> {

    private String _submissionTicket;

    private String[] _targetTickets;

    @SuppressWarnings("unchecked")
    @Override
    public ApproveTermTranslationCommand convertToInternalTaskHandlerCommand() {
	ApproveTermTranslationCommand command = new ApproveTermTranslationCommand();
	command.setTargetIds(asList(getTargetTickets()));
	command.setSubmissionId(TicketConverter.fromDtoToInternal(getSubmissionTicket(), Long.class));
	return command;
    }

    public String getSubmissionTicket() {
	return _submissionTicket;
    }

    public String[] getTargetTickets() {
	return _targetTickets;
    }

    public void setSubmissionTicket(String submissionTicket) {
	_submissionTicket = submissionTicket;
    }

    public void setTargetTickets(String[] targetTickets) {
	_targetTickets = targetTickets;
    }
}

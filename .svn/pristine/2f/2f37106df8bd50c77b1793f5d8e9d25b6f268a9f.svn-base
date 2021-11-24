package org.gs4tr.termmanager.service.model.command.dto;

import static java.util.Arrays.asList;

import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.AddCommentCommand;

public class DtoAddCommentCommand implements DtoTaskHandlerCommand<AddCommentCommand> {

    private String _languageId;

    private String _submissionTicket;

    private String[] _termTickets;

    private String _text;

    @SuppressWarnings("unchecked")
    @Override
    public AddCommentCommand convertToInternalTaskHandlerCommand() {
	AddCommentCommand command = new AddCommentCommand();
	command.setSubmissionId(TicketConverter.fromDtoToInternal(getSubmissionTicket(), Long.class));

	String[] termTickets = getTermTickets();
	if (termTickets != null) {
	    command.setTermIds(asList(termTickets));
	}

	command.setText(getText());
	command.setLanguageId(getLanguageId());

	return command;
    }

    public String getLanguageId() {
	return _languageId;
    }

    public String getSubmissionTicket() {
	return _submissionTicket;
    }

    public String[] getTermTickets() {
	return _termTickets;
    }

    public String getText() {
	return _text;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setSubmissionTicket(String submissionTicket) {
	_submissionTicket = submissionTicket;
    }

    public void setTermTickets(String[] termTickets) {
	_termTickets = termTickets;
    }

    public void setText(String text) {
	_text = text;
    }
}

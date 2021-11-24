package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.CancelTranslationCommand;

public class DtoCancelTranslationCommand implements DtoTaskHandlerCommand<CancelTranslationCommand> {

    private String _commentText;

    private String[] _submissionTickets;

    private String[] _termTickets;

    @Override
    public CancelTranslationCommand convertToInternalTaskHandlerCommand() {
	CancelTranslationCommand command = new CancelTranslationCommand();

	String[] submissionTickets = getSubmissionTickets();
	if (ArrayUtils.isNotEmpty(submissionTickets)) {
	    List<Long> submissionIds = new ArrayList<Long>();
	    for (String submissionTicket : submissionTickets) {
		submissionIds.add(TicketConverter.fromDtoToInternal(submissionTicket, Long.class));
	    }
	    command.setSubmissionId(submissionIds);
	}

	String[] termTickets = getTermTickets();
	if (ArrayUtils.isNotEmpty(termTickets)) {
	    List<String> termIds = new ArrayList<String>();
	    for (String termTicket : termTickets) {
		termIds.add(termTicket);
	    }
	    command.setTermIds(termIds);
	}

	command.setCommentText(getCommentText());

	return command;
    }

    public String getCommentText() {
	return _commentText;
    }

    public String[] getSubmissionTickets() {
	return _submissionTickets;
    }

    public String[] getTermTickets() {
	return _termTickets;
    }

    public void setCommentText(String commentText) {
	_commentText = commentText;
    }

    public void setSubmissionTickets(String[] submissionTickets) {
	_submissionTickets = submissionTickets;
    }

    public void setTermTickets(String[] termTickets) {
	_termTickets = termTickets;
    }
}

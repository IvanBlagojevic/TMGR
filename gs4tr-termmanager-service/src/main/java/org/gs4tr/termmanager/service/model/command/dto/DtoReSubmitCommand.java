package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.ReSubmitCommand;

public class DtoReSubmitCommand implements DtoTaskHandlerCommand<ReSubmitCommand> {

    private String _submissionTicket;

    private String[] _termTickets;

    @Override
    public ReSubmitCommand convertToInternalTaskHandlerCommand() {
	ReSubmitCommand command = new ReSubmitCommand();
	command.setSubmissionId(TicketConverter.fromDtoToInternal(getSubmissionTicket(), Long.class));
	String[] termTickets = getTermTickets();
	if (ArrayUtils.isNotEmpty(termTickets)) {
	    List<String> termIds = new ArrayList<String>();
	    for (String termTicket : termTickets) {
		termIds.add(termTicket);
	    }
	    command.setTermIds(termIds);
	}
	return command;
    }

    public String getSubmissionTicket() {
	return _submissionTicket;
    }

    public String[] getTermTickets() {
	return _termTickets;
    }

    public void setSubmissionTicket(String submissionTicket) {
	_submissionTicket = submissionTicket;
    }

    public void setTermTickets(String[] termTickets) {
	_termTickets = termTickets;
    }
}

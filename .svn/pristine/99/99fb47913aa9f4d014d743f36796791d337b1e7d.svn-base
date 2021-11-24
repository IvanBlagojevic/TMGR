package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.MultipleTermEntriesMergeCommand;

public class DtoMultipleTermEntriesMergeCommand implements DtoTaskHandlerCommand<MultipleTermEntriesMergeCommand> {

    private String _projectTicket;
    private String _sourceLanguage;
    private List<String> _termEntryTickets;

    public DtoMultipleTermEntriesMergeCommand() {
	_termEntryTickets = new ArrayList<>();
    }

    @Override
    public MultipleTermEntriesMergeCommand convertToInternalTaskHandlerCommand() {
	MultipleTermEntriesMergeCommand command = new MultipleTermEntriesMergeCommand();
	command.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));
	command.setTermEntryIds(getTermEntryTickets());
	command.setSourceLanguage(getSourceLanguage());
	return command;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public List<String> getTermEntryTickets() {
	return _termEntryTickets;
    }

    public void setProjectTicket(String projectId) {
	_projectTicket = projectId;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setTermEntryTickets(List<String> termEntryIds) {
	_termEntryTickets = termEntryIds;
    }
}

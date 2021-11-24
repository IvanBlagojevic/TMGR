package org.gs4tr.termmanager.service.model.command.dto;

import static java.util.Arrays.asList;

import org.gs4tr.termmanager.service.model.command.UndoCommand;

public class DtoUndoCommand implements DtoTaskHandlerCommand<UndoCommand> {

    private String[] _termTickets;

    @SuppressWarnings("unchecked")
    @Override
    public UndoCommand convertToInternalTaskHandlerCommand() {
	UndoCommand command = new UndoCommand();
	command.setTermIds(asList(getTermTickets()));
	return command;
    }

    public String[] getTermTickets() {
	return _termTickets;
    }

    public void setTermTickets(String[] termTickets) {
	_termTickets = termTickets;
    }

}

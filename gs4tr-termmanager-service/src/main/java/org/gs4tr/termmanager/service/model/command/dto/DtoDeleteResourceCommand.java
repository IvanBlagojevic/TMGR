package org.gs4tr.termmanager.service.model.command.dto;

import java.util.List;

import org.gs4tr.termmanager.service.model.command.DeleteResourceCommand;

public class DtoDeleteResourceCommand implements DtoTaskHandlerCommand<DeleteResourceCommand> {

    private String[] _ticketsForRemoval;

    @Override
    public DeleteResourceCommand convertToInternalTaskHandlerCommand() {
	DeleteResourceCommand command = new DeleteResourceCommand();

	List<String> ticketsForRemoval = command.getTicketsForRemoval();
	for (String ticket : getTicketsForRemoval()) {
	    ticketsForRemoval.add(ticket);
	}

	return command;
    }

    public String[] getTicketsForRemoval() {
	return _ticketsForRemoval;
    }

    public void setTicketsForRemoval(String[] ticketsForRemoval) {
	_ticketsForRemoval = ticketsForRemoval;
    }

}

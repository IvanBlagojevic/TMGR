package org.gs4tr.termmanager.webmvc.model.commands;

import java.util.Arrays;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.JsonCommand;

public class TaskCommand extends JsonCommand {

    private String _jsonTaskData;

    private Ticket[] _parentTickets;

    private String _taskName;

    private Ticket[] _taskTickets;

    public String getJsonTaskData() {
	return _jsonTaskData;
    }

    public Ticket[] getParentTickets() {
	return _parentTickets;
    }

    public String getTaskName() {
	return _taskName;
    }

    public Ticket[] getTaskTickets() {
	return _taskTickets;
    }

    public void setJsonTaskData(String jsonTaskData) {
	_jsonTaskData = jsonTaskData;
    }

    public void setParentTickets(Ticket[] parentTicketIds) {
	_parentTickets = parentTicketIds;
    }

    public void setTaskName(String taskName) {
	_taskName = taskName;
    }

    public void setTaskTickets(Ticket[] taskTickets) {
	_taskTickets = taskTickets;
    }

    @Override
    public String toString() {
	return "TaskCommand{" + ", _taskName='" + _taskName + '\'' + ", _parentIds="
		+ Arrays.toString(TicketConverter.fromDtoToInternal(_parentTickets, Long.class)) + "_jsonTaskData='"
		+ _jsonTaskData + '\'' + '}';
    }
}

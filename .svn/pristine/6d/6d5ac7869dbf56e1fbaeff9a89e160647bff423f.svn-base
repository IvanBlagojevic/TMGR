package org.gs4tr.termmanager.service;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;

public interface TaskService {

    TaskModel[] getTaskInfos(ManualTaskHandler taskHandler, Ticket[] parentTickets, String taskName);

    TaskResponse processTasks(ManualTaskHandler taskHandler, Ticket[] parentTickets, Ticket[] taskTickets,
	    DtoTaskHandlerCommand<?> command);
}

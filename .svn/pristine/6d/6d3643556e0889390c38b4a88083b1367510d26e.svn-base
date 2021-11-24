package org.gs4tr.termmanager.webmvc.controllers;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.webmvc.filters.model.RefreshUserContext;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.manualtask.ImportTbxDocumentTaskHandler;
import org.gs4tr.termmanager.service.model.command.dto.DtoImportCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;

/*TERII-5118
 Target languages combo box doesn't refresh after import.
 Due to Sumo Logic implementation TaskHandlers can be proxy objects.
 */
@SystemTask(priority = TaskPriority.LEVEL_NINE)
public class ImportTbxDocumentTaskHandlerDummy extends ImportTbxDocumentTaskHandler {

    Ticket ticket = new Ticket("some role");
    TaskResponse taskResponse = new TaskResponse(ticket);

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoImportCommand.class;
    }

    @Override
    @RefreshUserContext
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {
	return taskResponse;
    }

}

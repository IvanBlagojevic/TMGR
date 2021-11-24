package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.repository.RepositoryManager;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.model.command.DeleteResourceCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoDeleteResourceCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteMultimediaTaskHandler extends AbstractManualTaskHandler {

    @Autowired(required = false)
    private RepositoryManager _repositoryManager;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoDeleteResourceCommand.class;
    }

    public RepositoryManager getRepositoryManager() {
	return _repositoryManager;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	DeleteResourceCommand deleteResourceCommand = (DeleteResourceCommand) command;

	List<String> ticketsForRemoval = deleteResourceCommand.getTicketsForRemoval();

	for (String ticket : ticketsForRemoval) {
	    getRepositoryManager().delete(new RepositoryTicket(ticket));
	}

	return new TaskResponse(null);
    }
}

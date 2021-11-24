package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;

public interface ManualTaskHandler {

    Class<? extends DtoTaskHandlerCommand<?>> getCommandClass();

    TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command);

    TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command, List<UploadedRepositoryItem> files);
}

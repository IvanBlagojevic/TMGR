package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.model.command.PropertyCheckCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoPropertyCheckCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;

public abstract class AbstractPropertyCheckerTaskHandler extends AbstractManualTaskHandler {

    private static final String EXISTS = "exists"; //$NON-NLS-1$

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoPropertyCheckCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	PropertyCheckCommand propertyCheckCommand = (PropertyCheckCommand) command;

	boolean exists = true;

	exists = checkExistence(propertyCheckCommand);

	List<TaskModel> taskModels = new ArrayList<TaskModel>();

	TaskModel taskModel = new TaskModel();
	taskModel.addObject(EXISTS, exists);

	taskModels.add(taskModel);

	return taskModels.toArray(new TaskModel[taskModels.size()]);
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> repositoryItems) {
	return null;
    }

    abstract protected boolean checkExistence(PropertyCheckCommand propertyCheckCommand);
}

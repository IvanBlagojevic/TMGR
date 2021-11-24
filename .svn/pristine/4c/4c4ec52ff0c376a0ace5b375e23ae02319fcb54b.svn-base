package org.gs4tr.termmanager.service.manualtask;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.model.command.AutoSaveTermCommand;
import org.gs4tr.termmanager.service.model.command.AutoSaveTermCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoAutoSaveTermCommands;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class AutoSaveTermManualTaskHandler extends AbstractManualTaskHandler
	implements AvailableTaskValidator<AbstractItemHolder> {

    @Autowired
    private SubmissionTermService _submissiontermService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoAutoSaveTermCommands.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    @Override
    public boolean isTaskAvailable(AbstractItemHolder entity) {
	return !entity.isCanceled() && entity.isInTranslation();
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	Validate.notEmpty(parentIds, "Parameter parentTickets cannnot be empty.");

	List<Long> projectIds = Arrays.asList(parentIds);
	Long projectId = projectIds.get(0);

	AutoSaveTermCommands internalCommands = (AutoSaveTermCommands) command;
	List<AutoSaveTermCommand> autoSaveTermCommands = internalCommands.getAutoSaveTermCommands();

	if (CollectionUtils.isNotEmpty(autoSaveTermCommands)) {
	    for (AutoSaveTermCommand autoSaveTermCommand : autoSaveTermCommands) {

		String entityId = autoSaveTermCommand.getEntityId();

		String text = autoSaveTermCommand.getText();

		String parentEntityId = autoSaveTermCommand.getParentEntityId();

		boolean isTerm = autoSaveTermCommand.isTerm();

		validateParameters(isTerm, entityId, text);

		if (isTerm) {
		    getSubmissionTermService().updateTempTermText(entityId, text, projectId);
		} else if (autoSaveTermCommand.isNewAttribute()) {
		    // if adding new description
		    String baseType = autoSaveTermCommand.getBaseType().getTypeName();
		    String descriptionType = autoSaveTermCommand.getType();

		    getSubmissionTermService().addNewDescription(descriptionType, text, baseType, parentEntityId,
			    projectId);
		} else {
		    getSubmissionTermService().updateTempDescriptionText(parentEntityId, entityId, text, projectId);
		}
	    }
	}

	return new TaskResponse(null);
    }

    private SubmissionTermService getSubmissionTermService() {
	return _submissiontermService;
    }

    private void validateParameters(boolean isTerm, String entityId, String text) {
	if (isTerm && StringUtils.isBlank(entityId)) {
	    throw new RuntimeException(Messages.getString("AutoSaveTermManualTaskHandler.0")); //$NON-NLS-1$
	}

	if (StringUtils.isEmpty(text)) {
	    throw new RuntimeException(Messages.getString("AutoSaveTermManualTaskHandler.1")); //$NON-NLS-1$
	}
    }
}

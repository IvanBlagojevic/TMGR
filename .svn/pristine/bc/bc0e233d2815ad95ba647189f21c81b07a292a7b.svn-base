package org.gs4tr.termmanager.service.manualtask;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.model.command.UndoCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoUndoCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class UndoTranslationChangesManualTaskHandler extends AbstractManualTaskHandler
	implements AvailableTaskValidator<AbstractItemHolder> {

    @Autowired
    private SubmissionTermService _submissionTermService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoUndoCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    @Override
    public boolean isTaskAvailable(AbstractItemHolder entity) {
	return entity.isInTranslation() && TmUserProfile.getCurrentUserName().equals(entity.getAssignee());
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	Validate.notEmpty(parentIds, "Parameter parentTickets cannnot be empty.");

	List<Long> projectIds = Arrays.asList(parentIds);
	Long projectId = projectIds.get(0);

	UndoCommand undoCommand = (UndoCommand) command;
	List<String> termIds = undoCommand.getTermIds();

	Validate.notEmpty(termIds, Messages.getString("UndoTranslationChangesManualTaskHandler.0")); //$NON-NLS-1$

	Map<String, String> undoResults = getSubmissionTermService().undoTermTranslation(termIds, projectId);

	TaskResponse response = new TaskResponse(null);
	response.addObject("undoResults", undoResults);

	return response;
    }

    private SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }
}

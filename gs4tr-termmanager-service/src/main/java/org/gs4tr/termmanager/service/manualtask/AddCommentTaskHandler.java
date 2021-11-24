package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.model.command.AddCommentCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAddCommentCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class AddCommentTaskHandler extends AbstractManualTaskHandler
	implements AvailableTaskValidatorFolder<AbstractItemHolder> {

    @Autowired
    private SubmissionService _submissionService;

    @Autowired
    private TermService _termService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoAddCommentCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    public TermService getTermService() {
	return _termService;
    }

    @Override
    public boolean isTaskAvailableForFolder(AbstractItemHolder entity, ItemFolderEnum folder) {
	return ItemFolderEnum.SUBMISSIONTERMLIST == folder || ItemFolderEnum.SUBMISSIONDETAILS == folder;

    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	AddCommentCommand addCommentCommand = (AddCommentCommand) command;

	String commentText = addCommentCommand.getText();

	Long submissionId = addCommentCommand.getSubmissionId();

	List<String> termIds = addCommentCommand.getTermIds();

	String languageId = addCommentCommand.getLanguageId();

	validateParameters(submissionId);

	getSubmissionService().addComments(commentText, submissionId, termIds, languageId);

	return new TaskResponse(new Ticket(submissionId));
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }

    private void validateParameters(Long jobId) {
	if (jobId == null) {
	    throw new RuntimeException(Messages.getString("CancelTranslationTaskHandler.0")); //$NON-NLS-1$
	}
    }
}

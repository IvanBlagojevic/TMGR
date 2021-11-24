package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.model.command.ApproveTermTranslationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoApproveTermTranslationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

@SystemTask(priority = TaskPriority.LEVEL_SEVEN)
public class ApproveTermTranslationTaskHandler extends AbstractManualTaskHandler
	implements AvailableTaskValidator<AbstractItemHolder> {

    @Autowired
    private SubmissionTermService _submissionTermService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoApproveTermTranslationCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    @Override
    public boolean isTaskAvailable(AbstractItemHolder entity) {
	String username = TmUserProfile.getCurrentUserName();
	if (username.equals(entity.getSubmitter())) {
	    return entity.isInFinalReview();
	} else {
	    return false;
	}
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	ApproveTermTranslationCommand approveCommand = (ApproveTermTranslationCommand) command;

	Long submissionId = approveCommand.getSubmissionId();
	List<String> submissionTermIds = approveCommand.getTargetIds();

	getSubmissionTermService().approveSubmissionTerms(submissionTermIds, submissionId);

	return new TaskResponse(new Ticket(submissionId));
    }

    private SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }
}

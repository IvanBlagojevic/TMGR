package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.model.command.ReSubmitCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoReSubmitCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class ReSubmitManualTaskHandler extends AbstractToSubmissionTaskHandler
	implements AvailableTaskValidator<AbstractItemHolder> {

    @Autowired
    private SubmissionTermService _submissionTermService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoReSubmitCommand.class;
    }

    @Override
    public boolean isTaskAvailable(AbstractItemHolder entity) {
	return entity.isInFinalReview() && TmUserProfile.getCurrentUserName().equals(entity.getSubmitter());
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	ReSubmitCommand reSubmitCommand = (ReSubmitCommand) command;

	Long submissionId = reSubmitCommand.getSubmissionId();
	List<String> submissionTermIds = reSubmitCommand.getTermIds();

	validateParameters(submissionId, submissionTermIds);

	getSubmissionService().reSubmitTerms(submissionId, submissionTermIds);

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);

	Long projectId = submission.getProject().getProjectId();

	List<Term> submissionTermList = getSubmissionTermService().findByIds(submissionTermIds, projectId);

	MutableInt numberOfTerms = new MutableInt(0);

	StringBuffer jobComment = new StringBuffer();

	List<String> assignees = new ArrayList<String>();

	Map<String, Set<String>> assigneeLanguages = new HashMap<String, Set<String>>();

	collectMessageData(submissionTermList, submission, numberOfTerms, jobComment, assignees, assigneeLanguages);

	jobComment.append(Messages.getString("ReSubmitManualTaskHandler.1")); //$NON-NLS-1$
	sendEmailMessage(submission.getName(), jobComment, numberOfTerms, submission.getSourceLanguageId(),
		assigneeLanguages);

	return new TaskResponse(new Ticket(submissionId));
    }

    private void collectMessageData(List<Term> submissionTermList, Submission submission, MutableInt numberOfTerms,
	    StringBuffer jobComment, List<String> assignees, Map<String, Set<String>> assigneeLanguages) {
	for (Term term : submissionTermList) {
	    numberOfTerms.increment();

	    String asssignee = term.getAssignee();

	    Set<String> targetLanguages = assigneeLanguages.get(asssignee);
	    if (targetLanguages == null) {
		targetLanguages = new HashSet<String>();
		assigneeLanguages.put(asssignee, targetLanguages);
	    }
	    targetLanguages.add(term.getLanguageId());
	    assignees.add(asssignee);
	}
    }

    private SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }

    private void validateParameters(Long submissionId, List<String> submissionTermIds) {
	if (submissionId == null || CollectionUtils.isEmpty(submissionTermIds)) {
	    throw new RuntimeException(Messages.getString("ReSubmitManualTaskHandler.0")); //$NON-NLS-1$
	}
    }
}

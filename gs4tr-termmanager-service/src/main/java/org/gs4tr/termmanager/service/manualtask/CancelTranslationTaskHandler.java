package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmNotificationType;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.batch.BatchJob;
import org.gs4tr.termmanager.service.batch.executor.BatchJobExecutor;
import org.gs4tr.termmanager.service.model.command.CancelTranslationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoCancelTranslationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.notification.MailConstants;
import org.gs4tr.termmanager.service.notification.listeners.CancelTranslationNotificationListener;
import org.springframework.beans.factory.annotation.Autowired;

public class CancelTranslationTaskHandler extends AbstractManualTaskHandler
	implements AvailableTaskValidator<AbstractItemHolder> {

    @Autowired
    private BatchJobExecutor _batchJobExecutor;

    @Autowired
    private CancelTranslationNotificationListener _cancelTranslationNotificationListener;

    @Autowired
    private SubmissionService _submissionService;

    @Autowired
    private SubmissionTermService _submissionTermService;

    public CancelTranslationNotificationListener getCancelTranslationNotificationListener() {
	return _cancelTranslationNotificationListener;
    }

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoCancelTranslationCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    @Override
    public boolean isTaskAvailable(AbstractItemHolder entity) {
	String policy = ProjectPolicyEnum.POLICY_TM_CANCEL_TRANSLATION.toString();
	boolean contains = containsPolicies(entity, policy);

	if (!contains) {
	    return false;
	}

	String userName = TmUserProfile.getCurrentUserName();
	boolean isSubmitter = entity.getSubmitter().equals(userName);
	if (isSubmitter) {
	    return true;
	}

	boolean isAssignee = entity.getAssignee().contains(userName);
	if (isAssignee) {
	    return false;
	}

	return true;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	CancelTranslationCommand cancelTranslationCommand = (CancelTranslationCommand) command;

	List<Long> submissionIds = cancelTranslationCommand.getSubmissionIds();

	List<String> submissionTermIds = cancelTranslationCommand.getTermIds();

	validateParameters(submissionIds, submissionTermIds);

	if (CollectionUtils.isNotEmpty(submissionTermIds)) {
	    Long submissionId = submissionIds.get(0);
	    getSubmissionTermService().cancelTermTranslation(submissionId, submissionTermIds);
	} else {
	    for (Long submissionId : submissionIds) {
		getSubmissionService().cancelSubmission(submissionId);
	    }
	}

	BatchJob batchJob = new BatchJob() {
	    @Override
	    public void start(NotifyingMessageListener<BatchMessage> listener, BatchMessage batchMessage) {
		listener.notify(batchMessage);
	    }
	};

	List<Submission> submissions = getSubmissionService().findSubmissionsByIdsFetchChilds(submissionIds);
	if (CollectionUtils.isNotEmpty(submissions)) {
	    for (Submission submission : submissions) {
		Long projectId = submission.getProject().getProjectId();

		MutableInt numberOfTerms = new MutableInt(0);

		Map<String, Set<String>> assigneeLanguages = new HashMap<String, Set<String>>();

		collectNotificationData(submission, submissionTermIds, numberOfTerms, assigneeLanguages, projectId);

		BatchMessage message = createBatchMessage(TmUserProfile.getCurrentUserName(), submission.getName(),
			numberOfTerms, submission.getSourceLanguageId(), assigneeLanguages);

		getBatchJobExecutor().execute(batchJob, message, getCancelTranslationNotificationListener());
	    }
	}

	return new TaskResponse(new Ticket(submissionIds.get(0)));
    }

    private void collectNotificationData(Submission submission, List<String> submissionTermIds,
	    MutableInt numberOfTerms, Map<String, Set<String>> assigneeLanguages, Long projectId) {
	List<Term> submissionTerms = new ArrayList<Term>();

	if (CollectionUtils.isNotEmpty(submissionTermIds)) {
	    List<Term> result = getSubmissionTermService().findByIds(submissionTermIds, projectId);
	    submissionTerms.addAll(result);
	} else {
	    submissionTerms.addAll(getSubmissionService().findTermsBySubmissionId(submission.getSubmissionId()));
	}

	if (CollectionUtils.isNotEmpty(submissionTerms)) {
	    numberOfTerms.setValue(submissionTerms.size());
	    for (Term submissionTerm : submissionTerms) {
		String assignee = submissionTerm.getAssignee();
		String languageId = submissionTerm.getLanguageId();

		Set<String> targetLanguages = assigneeLanguages.get(assignee);
		if (targetLanguages == null) {
		    targetLanguages = new HashSet<String>();
		    assigneeLanguages.put(assignee, targetLanguages);
		}

		targetLanguages.add(languageId);
	    }
	}
    }

    private BatchMessage createBatchMessage(String username, String submissionName, MutableInt numberOfTerms,
	    String sourceLanguage, Map<String, Set<String>> assigneeLanguages) {
	BatchMessage message = new BatchMessage();

	Map<String, Object> propertiesMap = message.getPropertiesMap();

	propertiesMap.put(MailConstants.SUBMISSION_NAME, submissionName);
	propertiesMap.put(MailConstants.NUMBER_OF_TERMS, numberOfTerms);
	propertiesMap.put(MailConstants.USER, username);
	propertiesMap.put(MailConstants.NOTIFICATION_TYPE, TmNotificationType.TRANSLATION_CANCELED);
	propertiesMap.put(MailConstants.SOURCE_LANGUAGE, Locale.get(sourceLanguage).getDisplayName());
	propertiesMap.put(MailConstants.TARGET_LANGUAGE, assigneeLanguages);
	propertiesMap.put(BatchMessage.BATCH_PROCESS, BatchJobName.CANCEL_TRANSLATION);

	return message;
    }

    private BatchJobExecutor getBatchJobExecutor() {
	return _batchJobExecutor;
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }

    private SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }

    private void validateParameters(List<Long> submissionIds, List<String> submissionTermIds) {
	if (CollectionUtils.isEmpty(submissionIds)) {
	    throw new RuntimeException(Messages.getString("CancelTranslationTaskHandler.0")); //$NON-NLS-1$
	}

	if (CollectionUtils.isNotEmpty(submissionTermIds) && submissionIds.size() > 1) {
	    throw new RuntimeException(Messages.getString("CancelTranslationTaskHandler.1")); //$NON-NLS-1$
	}
    }
}

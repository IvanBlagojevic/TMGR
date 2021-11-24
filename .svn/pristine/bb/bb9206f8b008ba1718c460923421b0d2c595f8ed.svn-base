package org.gs4tr.termmanager.service.manualtask;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmNotificationType;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.batch.BatchJob;
import org.gs4tr.termmanager.service.batch.executor.BatchJobExecutor;
import org.gs4tr.termmanager.service.model.command.CommitTranslationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoCommitTranslationCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.notification.MailConstants;
import org.gs4tr.termmanager.service.notification.NotificationData;
import org.gs4tr.termmanager.service.notification.listeners.CompletedTranslationNotificationListener;
import org.springframework.beans.factory.annotation.Autowired;

public class CommitChangesManualTaskHandler extends AbstractManualTaskHandler
	implements AvailableTaskValidator<AbstractItemHolder> {

    @Autowired
    private BatchJobExecutor _batchJobExecutor;

    @Autowired
    private CompletedTranslationNotificationListener _completedTranslationNotificationListener;

    @Autowired
    private SubmissionService _submissionService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoCommitTranslationCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public boolean isTaskAvailable(AbstractItemHolder entity) {
	String username = TmUserProfile.getCurrentUserName();
	return username.equals(entity.getAssignee());
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	CommitTranslationCommand commitTranslationCommand = (CommitTranslationCommand) command;

	Long submissionId = commitTranslationCommand.getSubmissionId();
	Validate.notNull(submissionId, Messages.getString("CommitChangesManualTaskHandler.0")); //$NON-NLS-1$

	List<String> subTermIds = commitTranslationCommand.getTargetTermIds();

	String targetLanguage = commitTranslationCommand.getTargetLanguage();

	getSubmissionService().commitTranslationChanges(submissionId, targetLanguage, subTermIds);

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);

	NotificationData notificationData = getSubmissionService().collectNotificationData(submissionId);

	if (notificationData.isReady()) {
	    sendNotification(notificationData, submission);
	}

	return new TaskResponse(new Ticket(submissionId));
    }

    private BatchMessage createBatchMessage(String username, String submissionName, String submitter,
	    String sourceLanguage, Map<String, MutableInt> languageTermConfiguration) {
	BatchMessage message = new BatchMessage();

	Map<String, Object> propertiesMap = message.getPropertiesMap();

	propertiesMap.put(MailConstants.SUBMISSION_NAME, submissionName);
	propertiesMap.put(BatchMessage.BATCH_PROCESS, BatchJobName.COMPLETE_TRANSLATION);

	propertiesMap.put(MailConstants.USER, submitter);
	propertiesMap.put(MailConstants.ASSIGNEE, username);
	propertiesMap.put(MailConstants.NOTIFICATION_TYPE, TmNotificationType.TRANSLATION_COMPLETED);
	propertiesMap.put(MailConstants.SOURCE_LANGUAGE, Locale.get(sourceLanguage).getDisplayName());
	propertiesMap.put(MailConstants.TARGET_LANGUAGE, languageTermConfiguration);

	return message;
    }

    private BatchJobExecutor getBatchJobExecutor() {
	return _batchJobExecutor;
    }

    private CompletedTranslationNotificationListener getCompletedTranslationNotificationListener() {
	return _completedTranslationNotificationListener;
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }

    private void sendNotification(NotificationData notificationData, Submission submission) {
	BatchJob batchJob = new BatchJob() {
	    @Override
	    public void start(NotifyingMessageListener<BatchMessage> listener, BatchMessage batchMessage) {
		listener.notify(batchMessage);
	    }
	};

	String username = TmUserProfile.getCurrentUserName();

	Map<String, MutableInt> languageTermConfiguration = notificationData.getLanguageTermNumber();

	BatchMessage message = createBatchMessage(username, submission.getName(), submission.getSubmitter(),
		submission.getSourceLanguageId(), languageTermConfiguration);

	getBatchJobExecutor().execute(batchJob, message, getCompletedTranslationNotificationListener());
    }
}

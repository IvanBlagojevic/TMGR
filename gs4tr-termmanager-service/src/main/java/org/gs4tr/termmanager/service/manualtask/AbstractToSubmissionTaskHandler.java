package org.gs4tr.termmanager.service.manualtask;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.model.TmNotificationType;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.batch.BatchJob;
import org.gs4tr.termmanager.service.batch.executor.BatchJobExecutor;
import org.gs4tr.termmanager.service.notification.MailConstants;
import org.gs4tr.termmanager.service.notification.listeners.SendToTranslationNotificationListener;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractToSubmissionTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private BatchJobExecutor _batchJobExecutor;

    @Autowired
    private SendToTranslationNotificationListener _sendToTranslationNotificationListener;

    @Autowired
    private SubmissionService _submissionService;

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    public SendToTranslationNotificationListener getSendToTranslationNotificationListener() {
	return _sendToTranslationNotificationListener;
    }

    private BatchMessage createBatchMessage(String username, String jobName, String jobComment,
	    MutableInt numberOfTerms, String sourceLanguage, Map<String, Set<String>> assigneeLanguages) {
	BatchMessage message = new BatchMessage();

	Map<String, Object> propertiesMap = message.getPropertiesMap();

	propertiesMap.put(MailConstants.SUBMISSION_NAME, jobName);
	propertiesMap.put(MailConstants.JOB_COMMENT, jobComment);
	propertiesMap.put(MailConstants.NUMBER_OF_TERMS, numberOfTerms);
	propertiesMap.put(MailConstants.USER, username);
	propertiesMap.put(MailConstants.NOTIFICATION_TYPE, TmNotificationType.READY_FOR_TRANSLATION);
	propertiesMap.put(MailConstants.SOURCE_LANGUAGE, Locale.get(sourceLanguage).getDisplayName());
	propertiesMap.put(MailConstants.TARGET_LANGUAGE, assigneeLanguages);
	propertiesMap.put(BatchMessage.BATCH_PROCESS, BatchJobName.SEND_EMAIL_MESSAGE);

	return message;
    }

    private BatchJobExecutor getBatchJobExecutor() {
	return _batchJobExecutor;
    }

    protected SubmissionService getSubmissionService() {
	return _submissionService;
    }

    protected void sendEmailMessage(String submissionName, StringBuffer jobComment, MutableInt numberOfTerms,
	    String sourceLanguage, Map<String, Set<String>> assigneeLanguages) {
	BatchJob batchJob = new BatchJob() {
	    @Override
	    public void start(NotifyingMessageListener<BatchMessage> listener, BatchMessage batchMessage) {
		listener.notify(batchMessage);
	    }
	};

	BatchMessage message = createBatchMessage(TmUserProfile.getCurrentUserName(), submissionName,
		jobComment.toString(), numberOfTerms, sourceLanguage, assigneeLanguages);

	getBatchJobExecutor().execute(batchJob, message, getSendToTranslationNotificationListener());
    }
}

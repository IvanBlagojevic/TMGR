package org.gs4tr.termmanager.service.notification.listeners;

import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.service.batch.register.BatchJobRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("deleteProjectNotesListener")
public class DeleteProjectNotesNotificationListener implements NotifyingMessageListener<BatchMessage> {

    private static final String LONG_SUBTITLE_MSG = "Project notes are deleted or renamed";

    private static final String SUBTITLE_MSG = "Project notes deleted or renamed";

    private static final String SUCCESS_MESSAGE = "Maintenance complete.";
    private static final String TITLE_MSG = "Information";
    @Autowired
    private BatchJobRegister<String> _batchJobRegister;

    @Override
    public Class<BatchMessage> getNotifyingMessageClass() {
	return BatchMessage.class;
    }

    @Override
    public void notify(BatchMessage batchMessage) {
	Map<String, Object> propertiesMap = batchMessage.getPropertiesMap();

	BatchJobInfo batchProcessingItem = new BatchJobInfo(
		(BatchJobName) propertiesMap.get(BatchMessage.BATCH_PROCESS));

	batchProcessingItem.setTitle(TITLE_MSG);
	batchProcessingItem.setMsgSubTitle(SUBTITLE_MSG);
	batchProcessingItem.setMsgLongSubTitle(LONG_SUBTITLE_MSG);

	batchProcessingItem.setDisplayPopup(true);
	batchProcessingItem.setSuccessMessage(SUCCESS_MESSAGE);
	batchProcessingItem.setLongSuccessMessage((String) propertiesMap.get(BatchMessage.ITEMS_TO_PROCESS_KEY));

	String sessionId = (String) propertiesMap.get(BatchMessage.SESSION_ID_KEY);
	batchProcessingItem.setProjectName((String) propertiesMap.get(BatchMessage.PROJECT_NAME_KEY));

	getBatchJobRegister().registeBatchJobCompleted(sessionId, batchProcessingItem);
    }

    @Override
    public boolean supports(BatchMessage batchMessage) {
	return true;
    }

    private BatchJobRegister<String> getBatchJobRegister() {
	return _batchJobRegister;
    }

}

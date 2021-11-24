package org.gs4tr.termmanager.service.notification.listeners;

import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.service.batch.register.BatchJobRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("mergeTermNotificationListener")
public class MergeTermNotificationListener implements NotifyingMessageListener<BatchMessage> {

    @Autowired
    private BatchJobRegister<String> _batchJobRegister;

    @Override
    public Class<BatchMessage> getNotifyingMessageClass() {
	return BatchMessage.class;
    }

    @Override
    public void notify(BatchMessage message) {
	Map<String, Object> propertiesMap = message.getPropertiesMap();

	BatchJobInfo batchJobInfo = new BatchJobInfo((BatchJobName) propertiesMap.get(BatchMessage.BATCH_PROCESS));

	batchJobInfo.setTitle("Warning");
	batchJobInfo.setMsgSubTitle("Term already exists");
	batchJobInfo.setMsgLongSubTitle("Term already exists");

	batchJobInfo.setDisplayPopup(true);
	batchJobInfo.setSuccessMessage("Maintenance complete.");
	batchJobInfo.setLongSuccessMessage((String) propertiesMap.get(BatchMessage.ITEMS_TO_PROCESS_KEY));

	String sessionId = (String) propertiesMap.get(BatchMessage.SESSION_ID_KEY);
	batchJobInfo.setProjectName((String) propertiesMap.get(BatchMessage.PROJECT_NAME_KEY));

	getBatchJobRegister().registeBatchJobCompleted(sessionId, batchJobInfo);
    }

    @Override
    public boolean supports(BatchMessage message) {
	return true;
    }

    private BatchJobRegister<String> getBatchJobRegister() {
	return _batchJobRegister;
    }

}

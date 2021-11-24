package org.gs4tr.termmanager.service.notification.listeners;

import java.io.Serializable;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.service.batch.register.BatchJobRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("exportDocumentNotificationListener")
public class ExportDocumentNotificationListener implements Serializable, NotifyingMessageListener<BatchMessage> {

    private static final String LONG_SUBTITLE_MSG = "Export finished";
    private static final String SUBTITLE_MSG = "Export finished";
    private static final String SUCCESS_MESSAGE = "Export complete.";
    private static final String TITLE_MSG = "Information";
    private static final long serialVersionUID = -8454622834815566570L;
    @Autowired
    private BatchJobRegister<String> _batchJobRegister;

    @Override
    public Class<BatchMessage> getNotifyingMessageClass() {
	return BatchMessage.class;
    }

    @Override
    public void notify(BatchMessage batchMessage) {
	Map<String, Object> propertiesMap = batchMessage.getPropertiesMap();

	BatchJobInfo batchJobInfo = new BatchJobInfo((BatchJobName) propertiesMap.get(BatchMessage.BATCH_PROCESS));

	batchJobInfo.setTitle(TITLE_MSG);
	batchJobInfo.setMsgSubTitle(SUBTITLE_MSG);
	batchJobInfo.setMsgLongSubTitle(LONG_SUBTITLE_MSG);

	batchJobInfo.setDisplayPopup(true);
	batchJobInfo.setSuccessMessage(SUCCESS_MESSAGE);
	batchJobInfo.setLongSuccessMessage((String) propertiesMap.get(BatchMessage.ITEMS_TO_PROCESS_KEY));

	String sessionId = (String) propertiesMap.get(BatchMessage.SESSION_ID_KEY);
	batchJobInfo.setProjectName((String) propertiesMap.get(BatchMessage.PROJECT_NAME_KEY));

	getBatchJobRegister().registeBatchJobCompleted(sessionId, batchJobInfo);
    }

    @Override
    public boolean supports(BatchMessage batchMessage) {
	return true;
    }

    private BatchJobRegister<String> getBatchJobRegister() {
	return _batchJobRegister;
    }

}

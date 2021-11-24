package org.gs4tr.termmanager.service.notification.listeners;

import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.service.batch.register.BatchJobRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("reportExportNotificationListener")
public class ReportExportNotificationListener implements NotifyingMessageListener<BatchMessage> {

    private static final String LONG_SUBTITLE_MSG = "Exporting of projects report is finished.";

    private static final String SUBTITLE_MSG = "Report is generated";

    private static final String SUCCESS_MESSAGE = "Report export complete.";
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

	BatchJobName batchProcess = (BatchJobName) propertiesMap.get(BatchMessage.BATCH_PROCESS);

	BatchJobInfo batchJobInfo = new BatchJobInfo(batchProcess);
	batchJobInfo.setTitle(TITLE_MSG);
	batchJobInfo.setMsgSubTitle(SUCCESS_MESSAGE);
	batchJobInfo.setMsgLongSubTitle(SUBTITLE_MSG);

	batchJobInfo.setDisplayPopup(true);
	batchJobInfo.setSuccessMessage(SUCCESS_MESSAGE);
	batchJobInfo.setLongSuccessMessage(LONG_SUBTITLE_MSG);
	batchJobInfo.setResourceTrack((String) propertiesMap.get(BatchMessage.ITEMS_TO_PROCESS_KEY));

	String sessionId = (String) propertiesMap.get(BatchMessage.SESSION_ID_KEY);

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

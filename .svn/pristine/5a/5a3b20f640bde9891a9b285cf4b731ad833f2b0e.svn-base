package org.gs4tr.termmanager.service.notification.listeners;

import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.service.batch.register.BatchJobRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("importReportNotificationListener")
public class ImportReportNotificationListener implements NotifyingMessageListener<BatchMessage> {

    private static final String LONG_SUBTITLE_MSG = "Exporting of import summary report is finished.";

    private static final String SUBTITLE_MSG = "Import summary report is generated";

    private static final String SUCCESS_MESSAGE = "Import summary report complete.";

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

	BatchJobName batchJobName = (BatchJobName) propertiesMap.get(BatchMessage.BATCH_PROCESS);

	BatchJobInfo batchJobInfo = new BatchJobInfo(batchJobName);
	batchJobInfo.setTitle(TITLE_MSG);

	batchJobInfo.setMsgSubTitle(SUCCESS_MESSAGE);
	batchJobInfo.setMsgLongSubTitle(SUBTITLE_MSG);
	batchJobInfo.setSuccessMessage(SUCCESS_MESSAGE);
	batchJobInfo.setLongSuccessMessage(LONG_SUBTITLE_MSG);

	Boolean failed = (Boolean) propertiesMap.get(BatchMessage.DISPLAY_FAILED_NOTIFICATION);
	if (failed) {
	    String exceptionMessage = (String) propertiesMap.get(BatchMessage.EXCEPTION_MESSAGE);
	    String exceptionDetails = (String) propertiesMap.get(BatchMessage.EXCEPTION_DETAILS);
	    batchJobInfo.setMsgSubTitle(exceptionMessage);
	    batchJobInfo.setMsgLongSubTitle(exceptionDetails);
	    batchJobInfo.setExceptionMessage(exceptionDetails);
	    batchJobInfo.setSuccessMessage(exceptionMessage);
	    batchJobInfo.setLongSuccessMessage(exceptionDetails);
	} else {
	    batchJobInfo.setResourceTrack((String) propertiesMap.get(BatchMessage.ITEMS_TO_PROCESS_KEY));
	}

	batchJobInfo.setDisplayPopup(true);

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

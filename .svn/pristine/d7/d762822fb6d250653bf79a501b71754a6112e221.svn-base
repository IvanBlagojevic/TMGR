package org.gs4tr.termmanager.model;

import java.util.HashMap;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.NotifyingMessage;

public class BatchMessage implements NotifyingMessage {

    public static final String BATCH_PROCESS = "batchProcess"; //$NON-NLS-1$
    public static final String DISPLAY_FAILED_NOTIFICATION = "displayNotification"; //$NON-NLS-1$
    public static final String DISPLAY_NOTIFICATION = "displayNotification"; //$NON-NLS-1$
    public static final String EXCEPTION_DETAILS = "exceptionDetails"; //$NON-NLS-1$
    public static final String EXCEPTION_MESSAGE = "exceptionMessage"; //$NON-NLS-1$
    public static final String FAIL_ITEMS_KEY = "faildItems"; //$NON-NLS-1$
    public static final String FAILED_NOTIFICATION = "failedNotification"; //$NON-NLS-1$
    public static final String ITEMS_TO_PROCESS_KEY = "itemsToProcess"; //$NON-NLS-1$
    public static final String JOB_NAME = "jobName"; //$NON-NLS-1$
    public static final String PROJECT_NAME_KEY = "projectName"; //$NON-NLS-1$
    public static final String SESSION_ID_KEY = "sessionId"; //$NON-NLS-1$
    public static final String SUCCESS_ITEMS_KEY = "succesItems"; //$NON-NLS-1$
    public static final String SUCCESS_NOTIFICATION = "successNotification"; //$NON-NLS-1$
    public static final String TARGET_ITEM_KEY = "targetItem"; //$NON-NLS-1$
    public static final String USER = "user"; //$NON-NLS-1$

    private String _batchProcessPluginid;

    private Map<String, Object> _propertiesMap = new HashMap<String, Object>();

    public String getBatchProcessPluginid() {
	return _batchProcessPluginid;
    }

    @Override
    public String getNotifyingMessageId() {
	return null;
    }

    public Map<String, Object> getPropertiesMap() {
	return _propertiesMap;
    }

    public void setBatchProcessPluginid(String batchProcessPluginid) {
	_batchProcessPluginid = batchProcessPluginid;
    }

    public void setPropertiesMap(Map<String, Object> propertiesMap) {
	_propertiesMap = propertiesMap;
    }

}

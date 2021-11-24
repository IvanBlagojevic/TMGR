package org.gs4tr.termmanager.glossaryV2.logging.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import org.gs4tr.termmanager.model.update.ConnectionInfoHolder;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;

public class ProjectInfoEventDataBuilder {

    private ConnectionInfoHolder _connectionInfoHolder;

    public ProjectInfoEventDataBuilder(ConnectionInfoHolder connectionInfoHolder) {
	_connectionInfoHolder = connectionInfoHolder;
    }

    public Map<String, Object> build() {

	Map<String, Object> connectionInfo = new LinkedHashMap<String, Object>();

	ConnectionInfoHolder connectionInfoHolder = getConnectionInfoHolder();
	if (connectionInfoHolder != null) {

	    connectionInfo.put(EventContextConstants.PROJECT_ID, connectionInfoHolder.getProjectId());
	    connectionInfo.put(EventContextConstants.PROJECT_NAME, connectionInfoHolder.getProjectName());
	    connectionInfo.put(EventContextConstants.PROJECT_SHORT_CODE, connectionInfoHolder.getProjectShortCode());
	}

	return connectionInfo;
    }

    private ConnectionInfoHolder getConnectionInfoHolder() {
	return _connectionInfoHolder;
    }

}
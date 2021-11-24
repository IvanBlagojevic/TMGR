package org.gs4tr.termmanager.io.edd.event;

import org.gs4tr.termmanager.io.edd.api.Event;

public class RevertCountEvent implements Event {

    private String _collection;

    private Long _projectId;

    public RevertCountEvent(long projectId, String collection) {
	_projectId = projectId;
	_collection = collection;
    }

    public String getCollection() {
	return _collection;
    }

    public Long getProjectId() {
	return _projectId;
    }

    @Override
    public Class<? extends Event> getType() {
	return getClass();
    }
}

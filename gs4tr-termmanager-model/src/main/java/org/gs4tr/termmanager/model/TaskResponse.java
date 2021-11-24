package org.gs4tr.termmanager.model;

import java.util.HashMap;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.Ticket;

public class TaskResponse {
    private Map<String, Object> _model;

    private RepositoryItem _repositoryItem;

    private Ticket _responseTicket;

    public TaskResponse(Ticket responseTicket) {
	_model = new HashMap<String, Object>();

	_responseTicket = responseTicket;
    }

    public void addAllObjects(Map<? extends String, ? extends Object> objectMap) {
	_model.putAll(objectMap);
    }

    public void addObject(String key, Object value) {
	_model.put(key, value);
    }

    public Map<String, Object> getModel() {
	return _model;
    }

    public RepositoryItem getRepositoryItem() {
	return _repositoryItem;
    }

    public Ticket getResponseTicket() {
	return _responseTicket;
    }

    public void setRepositoryItem(RepositoryItem repositoryItem) {
	_repositoryItem = repositoryItem;
    }
}

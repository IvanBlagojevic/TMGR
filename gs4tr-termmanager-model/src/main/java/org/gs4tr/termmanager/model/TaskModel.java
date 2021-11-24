package org.gs4tr.termmanager.model;

import java.util.HashMap;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.Ticket;

public class TaskModel {
    private Map<String, Object> _model;

    private Ticket _parentEntityTicket;

    private RepositoryItem _repositoryItem;

    private Ticket _taskTicket;

    public TaskModel() {
	super();
	_model = new HashMap<String, Object>();
    }

    public TaskModel(Ticket parentEntityTicket, Ticket taskTicket) {
	this(parentEntityTicket, taskTicket, null);
    }

    public TaskModel(Ticket parentEntityTicket, Ticket taskTicket, RepositoryItem repositoryItem) {
	this();

	_parentEntityTicket = parentEntityTicket;
	_taskTicket = taskTicket;
	_repositoryItem = repositoryItem;
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

    public Ticket getParentEntityTicket() {
	return _parentEntityTicket;
    }

    public RepositoryItem getRepositoryItem() {
	return _repositoryItem;
    }

    public Ticket getTaskTicket() {
	return _taskTicket;
    }

    public void setRepositoryItem(RepositoryItem repositoryItem) {
	_repositoryItem = repositoryItem;
    }
}

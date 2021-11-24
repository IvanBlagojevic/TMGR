package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractManualTaskHandler implements SystemManualTaskHandler {

    private boolean _global = false;

    private List<String> _policies;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    private String _taskPriority;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return null;
    }

    @Override
    public boolean getGlobal() {
	return _global;
    }

    @Override
    public List<String> getPolicies() {
	return _policies;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	return null;
    }

    public String getTaskPriority() {
	return _taskPriority;
    }

    public void setGlobal(boolean global) {
	_global = global;
    }

    public void setPolicies(List<String> policies) {
	_policies = policies;
    }

    public void setTaskPriority(String taskPriority) {
	_taskPriority = taskPriority;
    }

    protected boolean containsPolicies(AbstractItemHolder entity, String policy) {
	String[] policies = { policy };
	Long projectId = entity.getProjectId();
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	return user.containsContextPolicies(projectId, policies);
    }

    protected int getSynonymNumber() {
	return _synonymNumber;
    }

}
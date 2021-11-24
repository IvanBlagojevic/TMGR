package org.gs4tr.termmanager.service.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.manualtask.AvailableTaskValidator;
import org.gs4tr.termmanager.service.manualtask.AvailableTaskValidatorFolder;
import org.gs4tr.termmanager.service.manualtask.ManualTaskManager;
import org.gs4tr.termmanager.service.manualtask.SaveDashboardManualTaskHandler;
import org.gs4tr.termmanager.service.manualtask.SendConnectionTaskHandler;
import org.gs4tr.termmanager.service.manualtask.SystemManualTaskHandler;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminTasksHolderHelper {

    @Autowired
    @Qualifier("systemManualTaskManager")
    private ManualTaskManager<SystemManualTaskHandler> _systemManualTaskManager;

    @Autowired
    @Qualifier("translationManualTaskManager")
    private ManualTaskManager<SystemManualTaskHandler> _translationManualTaskManager;

    public <T> TaskPagedList<T> assignTasks(PagedList<T> entityPagedList, Long projectId, EntityType... entityTypes) {
	List<Task> tasksList = getSystemEntityTasks(projectId, null, entityTypes);

	TaskPagedList<T> taskPagedList = new TaskPagedList<T>(entityPagedList);

	taskPagedList.setTasks(tasksList.toArray(new Task[tasksList.size()]));

	return taskPagedList;
    }

    public ManualTaskManager<SystemManualTaskHandler> getManualTaskManager(ItemFolderEnum folder) {
	if (folder == null) {
	    return getSystemManualTaskManager();
	} else if (ItemFolderEnum.SUBMISSIONTERMLIST == folder || ItemFolderEnum.SUBMISSIONDETAILS == folder) {
	    return getTranslationManualTaskManager();
	}

	return getSystemManualTaskManager();
    }

    public List<Task> getSystemEntityTasks(Long projectId, ItemFolderEnum folder, EntityType... entityTypes) {
	ManualTaskManager<SystemManualTaskHandler> systemManualTaskManager = getManualTaskManager(folder);

	return getSystemEntityTasksInternal(systemManualTaskManager, entityTypes, projectId, false);
    }

    public <T extends AbstractItemHolder> List<Task> getSystemEntityTasks(Long projectId, T entity,
	    ItemFolderEnum folder, EntityType... entityTypes) {
	ManualTaskManager<SystemManualTaskHandler> systemManualTaskManager = getManualTaskManager(folder);

	return getSystemEntityTasksInternal(entity, folder, systemManualTaskManager, entityTypes, projectId, false);
    }

    public ManualTaskManager<SystemManualTaskHandler> getSystemManualTaskManager() {
	return _systemManualTaskManager;
    }

    public ManualTaskManager<SystemManualTaskHandler> getTranslationManualTaskManager() {
	return _translationManualTaskManager;
    }

    public void setSystemManualTaskManager(ManualTaskManager<SystemManualTaskHandler> systemManualTaskManager) {
	_systemManualTaskManager = systemManualTaskManager;
    }

    public void setTranslationManualTaskManager(
	    ManualTaskManager<SystemManualTaskHandler> translationManualTaskManager) {
	_translationManualTaskManager = translationManualTaskManager;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <T> void addTask(boolean includeGlobalTasks, ItemFolderEnum folder, Set<String> contextPolicies,
	    List<Task> tasksList, Map.Entry<String, SystemManualTaskHandler> taskPoliciesEntry,
	    SystemManualTaskHandler value, T entity) {
	if (value.getPolicies() != null) {
	    if (CollectionUtils.containsAny(contextPolicies, value.getPolicies())) {

		/*
		 * Conversion is added because of sumo logic API (SystemManualTaskHandler object
		 * can be in proxy wrapper)
		 */

		value = getTargetObject(value);

		SelectStyleEnum selectStyle = value.getSelectStyle();
		String key = taskPoliciesEntry.getKey();
		String group = StringUtils.EMPTY;
		TaskPriority priority = TaskPriority.LEVEL_FIVE;

		if (value.getClass().isAnnotationPresent(SystemTask.class)) {
		    SystemTask metadata = value.getClass().getAnnotation(SystemTask.class);
		    priority = metadata.priority();
		    group = metadata.group();
		} else if (value instanceof SaveDashboardManualTaskHandler) {
		    SaveDashboardManualTaskHandler saveDashboardManualTaskHandler = (SaveDashboardManualTaskHandler) value;
		    String taskPriorityValue = saveDashboardManualTaskHandler.getTaskPriority();
		    if (StringUtils.isNotEmpty(taskPriorityValue)) {
			priority = TaskPriority.valueOf(taskPriorityValue);
		    }
		} else if (value instanceof SendConnectionTaskHandler && ItemFolderEnum.PROJECTDETAILS == folder) {
		    priority = TaskPriority.LEVEL_TEN;
		}

		if (value instanceof AvailableTaskValidator) {
		    if (((AvailableTaskValidator) value).isTaskAvailable(entity)) {
			tasksList.add(createTask(key, group, priority, selectStyle));
		    }
		} else if (value instanceof AvailableTaskValidatorFolder) {
		    if (((AvailableTaskValidatorFolder) value).isTaskAvailableForFolder(entity, folder)) {
			tasksList.add(createTask(key, group, priority, selectStyle));
		    }
		} else {
		    tasksList.add(createTask(key, group, priority, selectStyle));
		}
	    }
	}

	if (includeGlobalTasks) {
	    if (value.getGlobal()) {
		SelectStyleEnum selectStyle = value.getSelectStyle();
		String key = taskPoliciesEntry.getKey();
		String group = StringUtils.EMPTY;
		TaskPriority priority = TaskPriority.LEVEL_FIVE;
		Task globalTask = createTask(key, group, priority, selectStyle);
		if (!tasksList.contains(globalTask)) {
		    tasksList.add(globalTask);
		}
	    }
	}
    }

    private void addTask(boolean includeGlobalTasks, Set<String> contextPolicies, List<Task> tasksList,
	    Map.Entry<String, SystemManualTaskHandler> taskPoliciesEntry, SystemManualTaskHandler value) {
	if (value.getPolicies() != null) {

	    /*
	     * Conversion is added because of sumo logic API (SystemManualTaskHandler object
	     * can be in proxy wrapper)
	     */

	    value = getTargetObject(value);

	    if (CollectionUtils.containsAny(contextPolicies, value.getPolicies())) {
		SelectStyleEnum selectStyle = value.getSelectStyle();
		String key = taskPoliciesEntry.getKey();
		String group = StringUtils.EMPTY;
		TaskPriority priority = TaskPriority.LEVEL_FIVE;

		if (value.getClass().isAnnotationPresent(SystemTask.class)) {
		    SystemTask metadata = value.getClass().getAnnotation(SystemTask.class);
		    priority = metadata.priority();
		    group = metadata.group();
		} else if (value instanceof SaveDashboardManualTaskHandler) {
		    SaveDashboardManualTaskHandler saveDashboardManualTaskHandler = (SaveDashboardManualTaskHandler) value;
		    String taskPriorityValue = saveDashboardManualTaskHandler.getTaskPriority();
		    if (StringUtils.isNotEmpty(taskPriorityValue)) {
			priority = TaskPriority.valueOf(taskPriorityValue);
		    }
		} else if (value instanceof SendConnectionTaskHandler) {
		    priority = TaskPriority.LEVEL_TEN;
		}
		tasksList.add(createTask(key, group, priority, selectStyle));
	    }
	}

	if (includeGlobalTasks) {
	    if (value.getGlobal()) {
		SelectStyleEnum selectStyle = value.getSelectStyle();
		String key = taskPoliciesEntry.getKey();
		String group = StringUtils.EMPTY;
		TaskPriority priority = TaskPriority.LEVEL_FIVE;
		Task globalTask = createTask(key, group, priority, selectStyle);
		if (!tasksList.contains(globalTask)) {
		    tasksList.add(globalTask);
		}
	    }
	}
    }

    private List<Task> getSystemEntityTasksInternal(ManualTaskManager<SystemManualTaskHandler> systemManualTaskManager,
	    EntityType[] entityTypes, Long projectId, boolean includeGlobalTasks) {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Set<String> contextPolicies = null;
	if (projectId == null) {
	    contextPolicies = userProfile.getContextPolicies(TmUserProfile.DEFAULT_CONTEXT_ID);
	} else {
	    contextPolicies = userProfile.getContextPolicies(projectId);
	}

	Map<EntityType, Map<String, SystemManualTaskHandler>> taskDefinition = systemManualTaskManager
		.getManualTaskHandlerMap();

	List<Task> tasksList = new ArrayList<Task>();

	for (EntityType entityType : entityTypes) {
	    if (taskDefinition.get(entityType) != null && contextPolicies != null) {
		for (Map.Entry<String, SystemManualTaskHandler> taskPoliciesEntry : taskDefinition.get(entityType)
			.entrySet()) {
		    SystemManualTaskHandler value = taskPoliciesEntry.getValue();
		    addTask(includeGlobalTasks, contextPolicies, tasksList, taskPoliciesEntry, value);
		}
	    }
	}

	return tasksList;
    }

    private <T extends AbstractItemHolder> List<Task> getSystemEntityTasksInternal(T entity, ItemFolderEnum folder,
	    ManualTaskManager<SystemManualTaskHandler> systemManualTaskManager, EntityType[] entityTypes,
	    Long projectId, boolean includeGlobalTasks) {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Set<String> contextPolicies = null;
	if (projectId == null) {
	    contextPolicies = userProfile.getContextPolicies(TmUserProfile.DEFAULT_CONTEXT_ID);
	} else {
	    contextPolicies = userProfile.getContextPolicies(projectId);
	}

	Map<EntityType, Map<String, SystemManualTaskHandler>> taskDefinition = systemManualTaskManager
		.getManualTaskHandlerMap();

	List<Task> tasksList = new ArrayList<Task>();

	for (EntityType entityType : entityTypes) {
	    if (taskDefinition.get(entityType) != null && contextPolicies != null) {
		for (Map.Entry<String, SystemManualTaskHandler> taskPoliciesEntry : taskDefinition.get(entityType)
			.entrySet()) {
		    SystemManualTaskHandler value = taskPoliciesEntry.getValue();
		    addTask(includeGlobalTasks, folder, contextPolicies, tasksList, taskPoliciesEntry, value, entity);
		}
	    }
	}

	return tasksList;
    }

    private SystemManualTaskHandler getTargetObject(Object proxy) {
	if (AopUtils.isJdkDynamicProxy(proxy)) {
	    try {
		return (SystemManualTaskHandler) ((Advised) proxy).getTargetSource().getTarget();
	    } catch (Exception e) {
		throw new RuntimeException("Aop proxy object conversion error", e);
	    }
	} else {
	    return (SystemManualTaskHandler) proxy;
	}
    }

    protected Task createTask(String taskName, String group, TaskPriority priority, SelectStyleEnum selectStyle) {
	Task task = new Task(taskName);
	task.setPriority(priority);
	task.setSelectStyle(selectStyle);
	return task;
    }

}
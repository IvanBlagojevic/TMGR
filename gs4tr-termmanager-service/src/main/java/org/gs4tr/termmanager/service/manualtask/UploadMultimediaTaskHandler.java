package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.RepositoryPath;
import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.repository.RepositoryManager;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.model.command.AssignTermEntryAttributesCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAssignTermEntryAttributesCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class UploadMultimediaTaskHandler extends AbstractManualTaskHandler {

    private static final String ATTRIBUTE_TYPE = "attributeType"; //$NON-NLS-1$
    private static final String FILE_MAP = "fileMap"; //$NON-NLS-1$
    private static final String FILE_NAME = "fileName"; //$NON-NLS-1$
    private static final String TICKET = "ticket"; //$NON-NLS-1$

    @Autowired(required = false)
    private RepositoryManager _repositoryManager;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoAssignTermEntryAttributesCommand.class;
    }

    public RepositoryManager getRepositoryManager() {
	return _repositoryManager;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	TaskResponse taskResponse = new TaskResponse(null);

	ManualTaskHandlerUtils.checkImageFiles(files);

	AssignTermEntryAttributesCommand assignCommand = (AssignTermEntryAttributesCommand) command;

	List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

	List<String> resourceIdsForRemoval = assignCommand.getResourceIdsForRemoval();

	if (CollectionUtils.isNotEmpty(resourceIdsForRemoval)) {
	    deleteBinaryResource(resourceIdsForRemoval);

	}

	Map<String, String> resourceTicketForEditingMap = assignCommand.getResourceTicketForEditingMap();

	if (resourceTicketForEditingMap != null) {
	    editBinaryResource(files, resourceTicketForEditingMap, mapList);

	}

	Map<String, String> attributeTypeFileNameMap = assignCommand.getAttributeTypeFileNameMap();

	if (attributeTypeFileNameMap != null) {
	    addBinaryResource(files, attributeTypeFileNameMap, mapList);
	}

	taskResponse.addObject(FILE_MAP, mapList);

	return taskResponse;
    }

    private void addBinaryResource(List<UploadedRepositoryItem> files, Map<String, String> attributeTypeFileNameMap,
	    List<Map<String, String>> mapList) {
	for (Map.Entry<String, String> entry : attributeTypeFileNameMap.entrySet()) {
	    Map<String, String> fileMap = new HashMap<String, String>();

	    RepositoryItem item = findRepositoryItem(entry.getKey(), files);

	    ResourceInfo resourceInfo = item.getResourceInfo();
	    item.setRepositoryPath(new RepositoryPath(UUID.randomUUID().toString() + StringConstants.SLASH));
	    String attributeType = entry.getValue();
	    validateAttributeType(attributeType);
	    resourceInfo.setDescription(attributeType);

	    RepositoryTicket newTicket = getRepositoryManager().store(item);

	    ServiceUtils.closeInputStream(item.getInputStream());

	    fileMap.put(TICKET, newTicket.getTicket());
	    fileMap.put(FILE_NAME, resourceInfo.getName());
	    fileMap.put(ATTRIBUTE_TYPE, attributeType);
	    mapList.add(fileMap);
	}
    }

    private void deleteBinaryResource(List<String> resourceIdsForRemoval) {

	for (String ticketId : resourceIdsForRemoval) {
	    getRepositoryManager().delete(new RepositoryTicket(ticketId));
	}
    }

    private void editBinaryResource(List<UploadedRepositoryItem> files, Map<String, String> resourceTicketForEditingMap,
	    List<Map<String, String>> mapList) {
	for (Map.Entry<String, String> entry : resourceTicketForEditingMap.entrySet()) {

	    Map<String, String> fileMap = new HashMap<String, String>();

	    RepositoryTicket repositoryTicket = new RepositoryTicket(entry.getValue());
	    RepositoryItem oldRepositoryItem = getRepositoryManager().read(repositoryTicket);

	    getRepositoryManager().delete(repositoryTicket);

	    RepositoryItem newRepositoryItem = findRepositoryItem(entry.getKey(), files);

	    newRepositoryItem
		    .setRepositoryPath(new RepositoryPath(UUID.randomUUID().toString() + StringConstants.SLASH));

	    ResourceInfo resourceInfo = newRepositoryItem.getResourceInfo();
	    String attributeType = oldRepositoryItem.getResourceInfo().getDescription();
	    resourceInfo.setDescription(attributeType);

	    RepositoryTicket newTicket = getRepositoryManager().store(newRepositoryItem);

	    fileMap.put(TICKET, newTicket.getTicket());
	    fileMap.put(FILE_NAME, resourceInfo.getName());
	    fileMap.put(ATTRIBUTE_TYPE, attributeType);
	    mapList.add(fileMap);
	}
    }

    private RepositoryItem findRepositoryItem(String key, List<UploadedRepositoryItem> files) {
	for (UploadedRepositoryItem uploadedRepositoryItem : files) {
	    if (uploadedRepositoryItem.getNameParameter().equals(key)) {
		return uploadedRepositoryItem.getRepositoryItem();
	    }
	}
	return null;
    }

    private void validateAttributeType(String attributeType) {
	if (StringUtils.isBlank(attributeType)) {
	    throw new RuntimeException(Messages.getString("UploadTermEntryReferencesTaskHandler.0")); //$NON-NLS-1$
	}

    }
}

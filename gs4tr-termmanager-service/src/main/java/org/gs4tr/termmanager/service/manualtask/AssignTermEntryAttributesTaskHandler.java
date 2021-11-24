package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TermEntryResourceTrack;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.converter.AttributeConverter;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.model.command.AssignTermEntryAttributesCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAssignTermEntryAttributesCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;

@SystemTask(priority = TaskPriority.LEVEL_THREE)
public class AssignTermEntryAttributesTaskHandler extends AbstractManualTaskHandler {

    private static final String ALL_ATTRIBUTES = "allAttributes"; //$NON-NLS-1$

    private static final String ATTRIBUTE_NAME_KEY = "attributeName"; //$NON-NLS-1$

    private static final String ATTRIBUTE_VALUE_KEY = "attributeValue"; //$NON-NLS-1$

    private static final String MARKER_ID = "markerId";

    private static final String PARENT_MARKER_ID = "parentMarkerId"; //$NON-NLS-1$

    private static final String TERMENTRY_ATTRIBUTES_KEY = "termEntryAttributes"; //$NON-NLS-1$

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoAssignTermEntryAttributesCommand.class;
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	AssignTermEntryAttributesCommand assignCommand = (AssignTermEntryAttributesCommand) command;

	String id = assignCommand.getTermEntryId();

	Validate.notEmpty(parentIds, "Parameter parentTickets cannnot be empty.");

	Long projectId = parentIds[0];

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);

	List<Attribute> projectAttributes = ServiceUtils.getTermEntryLevelAttributes(project.getAttributes());

	if (CollectionUtils.isEmpty(projectAttributes)) {
	    String description = String.format(MessageResolver.getMessage("AssignTermEntryAttributesTaskHandler.6"),
		    project.getProjectInfo().getName());
	    throw new UserException(MessageResolver.getMessage("AssignTermEntryAttributesTaskHandler.1"), //$NON-NLS-1$
		    description);
	}

	TermEntry termEntry = getTermEntryService().findTermEntryById(id, projectId);

	TaskModel newTaskModel = new TaskModel(null, new Ticket(id));

	List<Object> attributesData = new ArrayList<Object>();

	Set<Description> termEntryDescriptions = termEntry.getDescriptions();

	if (CollectionUtils.isNotEmpty(termEntryDescriptions)) {
	    for (Description description : termEntryDescriptions) {

		Map<String, String> data = new LinkedHashMap<String, String>();

		data.put(ATTRIBUTE_NAME_KEY, description.getType());

		data.put(ATTRIBUTE_VALUE_KEY, description.getValue());

		data.put(MARKER_ID, description.getUuid());

		attributesData.add(data);

	    }
	}
	newTaskModel.addObject(TERMENTRY_ATTRIBUTES_KEY, attributesData);
	newTaskModel.addObject(PARENT_MARKER_ID, termEntry.getUuId());
	newTaskModel.addObject(ALL_ATTRIBUTES, AttributeConverter.fromInternalToDto(projectAttributes));
	List<TermEntryResourceTrack> resourceTracks = getTermEntryService().findResourceTracksByTermEntryById(id);
	newTaskModel.addObject("multimedia", ManualTaskHandlerUtils.collectMultimedia(resourceTracks));

	return new TaskModel[] { newTaskModel };
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	Validate.notEmpty(parentIds, "Parameter parentTickets cannnot be empty.");

	Long projectId = parentIds[0];

	ManualTaskHandlerUtils.checkImageFiles(files);

	AssignTermEntryAttributesCommand assignCommand = (AssignTermEntryAttributesCommand) command;

	String termEntryId = assignCommand.getTermEntryId();
	List<UpdateCommand> updateCommands = assignCommand.getUpdateCommands();
	if (CollectionUtils.isNotEmpty(updateCommands)) {
	    TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, projectId);
	    termEntry.setAction(Action.EDITED);
	    getTermEntryService().updateTermEntry(termEntry, updateCommands);
	}

	List<String> resourceIdsForRemoval = assignCommand.getResourceIdsForRemoval();

	if (CollectionUtils.isNotEmpty(resourceIdsForRemoval)) {
	    deleteBinaryResource(termEntryId, resourceIdsForRemoval, projectId);
	}

	Map<String, String> resourceTicketForEditingMap = assignCommand.getResourceTicketForEditingMap();

	if (resourceTicketForEditingMap != null) {
	    editBinaryResource(files, termEntryId, resourceTicketForEditingMap);

	}

	Map<String, String> attributeTypeFileNameMap = assignCommand.getAttributeTypeFileNameMap();

	if (attributeTypeFileNameMap != null) {
	    addBinaryResource(files, termEntryId, attributeTypeFileNameMap);
	}

	return new TaskResponse(new Ticket(termEntryId));
    }

    private void addBinaryResource(List<UploadedRepositoryItem> files, String termEntryId,
	    Map<String, String> attributeTypeFileNameMap) {
	for (Map.Entry<String, String> entry : attributeTypeFileNameMap.entrySet()) {

	    RepositoryItem item = findRepositoryItem(entry.getKey(), files);

	    getTermEntryService().uploadBinaryResource(termEntryId, item.getResourceInfo(), item.getInputStream(),
		    entry.getValue());
	}
    }

    private void deleteBinaryResource(String termEntryId, List<String> resourceIdsForRemoval, Long projectId) {
	getTermEntryService().deleteTermEntryResourceTracks(termEntryId, resourceIdsForRemoval, projectId);
    }

    private void editBinaryResource(List<UploadedRepositoryItem> files, String termEntryId,
	    Map<String, String> resourceTicketForEditingMap) {
	for (Map.Entry<String, String> entry : resourceTicketForEditingMap.entrySet()) {

	    getTermEntryService().updateTermEntryResourceTrack(termEntryId, entry.getValue(),
		    findRepositoryItem(entry.getKey(), files));

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
}

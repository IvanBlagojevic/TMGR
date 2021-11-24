package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchMessage;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.AttributeLevelEnum;
import org.gs4tr.termmanager.model.dto.InputFieldTypeEnum;
import org.gs4tr.termmanager.model.dto.TermEntryAttributeTypeDto;
import org.gs4tr.termmanager.model.dto.converter.AttributeConverter;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.batch.BatchJob;
import org.gs4tr.termmanager.service.batch.executor.BatchJobExecutor;
import org.gs4tr.termmanager.service.batch.register.BatchJobRegister;
import org.gs4tr.termmanager.service.model.command.AssignProjectAttributesCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAssignProjectAttributesCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.notification.listeners.DeleteProjectAttributesNotificationListener;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignProjectAttributesTaskHandler extends AbstractManualTaskHandler {

    private static final String ALL_ATTRIBUTES = "allAttributes";

    private static final String ATTRIBUTE = "attribute";

    private static final String ATTRIBUTE_LEVELS = "attributeLevels";

    private static final String DEFAULT_PROJECT_ATTRIBUTE_TYPE = "defaultProjectAttributeType";

    private static final String FIELD_TYPES = "fieldTypes";

    private static final String PROJECT = "Project";
    private static final String PROJECT_ATTRIBUTES_KEY = "projectAttributes";
    private static final String PROJECT_ATTRIBUTE_TYPES = "projectAttributeTypes";
    private static final String RENAMED_ATTRIBUTES = "Renamed attributes";

    @Autowired
    private BatchJobExecutor _batchJobExecutor;

    @Autowired
    private BatchJobRegister<String> _batchJobRegister;

    @Autowired
    private DeleteProjectAttributesNotificationListener _deleteProjectAttributesNotificationListener;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TermService _termService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoAssignProjectAttributesCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	Long id = parentIds[0];

	TmProject project = getProjectService().findProjectById(id, Attribute.class);

	TaskModel newTaskModel = new TaskModel(null, new Ticket(id));

	org.gs4tr.termmanager.model.dto.Attribute[] dtoAttributes = AttributeConverter
		.fromInternalToDto(project.getDescriptions());
	newTaskModel.addObject(PROJECT_ATTRIBUTES_KEY,
		dtoAttributes == null ? new ArrayList<Attribute>() : dtoAttributes);
	newTaskModel.addObject(PROJECT_ATTRIBUTE_TYPES, TermEntryAttributeTypeDto.getValues());
	newTaskModel.addObject(ATTRIBUTE_LEVELS, AttributeLevelEnum.getValues());
	newTaskModel.addObject(DEFAULT_PROJECT_ATTRIBUTE_TYPE, TermEntryAttributeTypeDto.TEXT);
	newTaskModel.addObject(ALL_ATTRIBUTES, ManualTaskHandlerUtils.getAllAttributesResponse());
	newTaskModel.addObject(FIELD_TYPES, InputFieldTypeEnum.getValues());

	return new TaskModel[] { newTaskModel };
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	TaskResponse taskResponse = new TaskResponse(null);

	final Long projectId = taskIds[0];

	final AssignProjectAttributesCommand assignCommand = (AssignProjectAttributesCommand) command;

	final String username = TmUserProfile.getCurrentUserName();

	final TmProject project = getProjectService().findProjectById(projectId, Attribute.class);

	final List<Attribute> incomingProjectAttributes = assignCommand.getProjectAttributes();

	final Map<org.gs4tr.termmanager.model.AttributeLevelEnum, List<String>> descriptionsByLevel = new HashMap<>();

	final Map<org.gs4tr.termmanager.model.AttributeLevelEnum, List<Attribute>> descriptionsForRenaming = new HashMap<>();

	List<Attribute> existingAttributes = project.getDescriptions();

	maintainDescriptions(incomingProjectAttributes, existingAttributes, descriptionsByLevel,
		descriptionsForRenaming);

	if (CollectionUtils.isNotEmpty(project.getNotes())) {
	    incomingProjectAttributes.addAll(project.getNotes());
	}

	final List<String> projectLanguages = getProjectService().getProjectLanguageCodes(projectId);

	if (!descriptionsByLevel.isEmpty() || !descriptionsForRenaming.isEmpty()) {

	    taskResponse.addObject(ManualTaskHandlerUtils.START_PINGING, Boolean.TRUE);

	    BatchJob batchJob = (listener, batchMessage) -> {

		// deleting term descriptions
		List<String> termDescForDelete = descriptionsByLevel
			.get(org.gs4tr.termmanager.model.AttributeLevelEnum.LANGUAGE);
		if (CollectionUtils.isNotEmpty(termDescForDelete)) {
		    getTermService().deleteTermDescriptionsByType(termDescForDelete, projectId, Description.ATTRIBUTE,
			    projectLanguages);

		}

		// renaming term descriptions
		List<Attribute> termDescForRenaming = descriptionsForRenaming
			.get(org.gs4tr.termmanager.model.AttributeLevelEnum.LANGUAGE);
		if (CollectionUtils.isNotEmpty(termDescForRenaming)) {
		    getTermService().renameTermDescriptions(projectId, Description.ATTRIBUTE, termDescForRenaming,
			    projectLanguages);
		}

		// deleting termEntry descriptions
		List<String> termEntryDescForDelete = descriptionsByLevel
			.get(org.gs4tr.termmanager.model.AttributeLevelEnum.TERMENTRY);
		if (CollectionUtils.isNotEmpty(termEntryDescForDelete)) {
		    getTermEntryService().deleteTermEntryDescriptionsByType(termEntryDescForDelete, projectId,
			    projectLanguages);
		}

		// renaming termEntry descriptions
		List<Attribute> termEntryDescForRenaming = descriptionsForRenaming
			.get(org.gs4tr.termmanager.model.AttributeLevelEnum.TERMENTRY);
		if (CollectionUtils.isNotEmpty(termEntryDescForRenaming)) {
		    getTermEntryService().renameTermDescriptions(projectId, termEntryDescForRenaming);
		}
		updateProjectAttributes(projectId, incomingProjectAttributes);

		listener.notify(batchMessage);
	    };

	    String projectName = project.getProjectInfo().getName();

	    String attributesDeleted = appendAttributesForDeletion(descriptionsByLevel, projectName);

	    String finalAttributesChanged = appendAttributesForRenaming(attributesDeleted, descriptionsForRenaming);

	    BatchMessage message = createBatchMessage(username, projectName, finalAttributesChanged);

	    getBatchJobRegister().registerBatchJob(username, BatchJobName.DELETE_RENAME_PROJECT_ATTRIBUTE);

	    getBatchJobExecutor().execute(batchJob, message, getDeleteProjectAttributesNotificationListener());
	} else {
	    updateProjectAttributes(projectId, incomingProjectAttributes);
	    taskResponse.addObject(ManualTaskHandlerUtils.START_PINGING, Boolean.FALSE);
	}

	return taskResponse;
    }

    private String appendAttributesForDeletion(
	    Map<org.gs4tr.termmanager.model.AttributeLevelEnum, List<String>> descriptionsByLevel, String projectName) {
	StringBuilder builder = new StringBuilder();
	builder.append(PROJECT);
	builder.append(StringConstants.COLON);
	builder.append(StringConstants.SPACE);
	builder.append(projectName);
	for (Map.Entry<org.gs4tr.termmanager.model.AttributeLevelEnum, List<String>> entry : descriptionsByLevel
		.entrySet()) {
	    builder.append("<br/>");
	    org.gs4tr.termmanager.model.AttributeLevelEnum level = entry.getKey();
	    List<String> attributes = entry.getValue();

	    builder.append(level.getLevelName());
	    builder.append(StringConstants.SPACE);
	    builder.append(ATTRIBUTE);
	    builder.append(StringConstants.COLON);
	    builder.append(StringConstants.SPACE);

	    for (String attribute : attributes) {
		builder.append(attribute);
		builder.append(StringConstants.COMMA);
		builder.append(StringConstants.SPACE);
	    }

	    builder.deleteCharAt(builder.length() - 1);
	    builder.deleteCharAt(builder.length() - 1);
	}

	return builder.toString();
    }

    private String appendAttributesForRenaming(String attributesUpdated,
	    Map<org.gs4tr.termmanager.model.AttributeLevelEnum, List<Attribute>> descriptionsByLevel) {
	StringBuilder builder = new StringBuilder(attributesUpdated);

	if (!descriptionsByLevel.isEmpty()) {
	    builder.append("<br/>");
	    builder.append(RENAMED_ATTRIBUTES);
	    builder.append(StringConstants.COLON);
	    builder.append(StringConstants.SPACE);

	    for (Map.Entry<org.gs4tr.termmanager.model.AttributeLevelEnum, List<Attribute>> entry : descriptionsByLevel
		    .entrySet()) {
		builder.append("<br/>");
		org.gs4tr.termmanager.model.AttributeLevelEnum level = entry.getKey();
		List<Attribute> attributes = entry.getValue();

		builder.append(level.getLevelName());
		builder.append(StringConstants.SPACE);
		builder.append(ATTRIBUTE);
		builder.append(StringConstants.COLON);
		builder.append(StringConstants.SPACE);

		for (Attribute attribute : attributes) {
		    builder.append(attribute.getName());
		    builder.append(StringConstants.COMMA);
		    builder.append(StringConstants.SPACE);
		}

		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);
	    }
	}

	return builder.toString();
    }

    private BatchMessage createBatchMessage(String sessionId, String projectName, String itemsToProcess) {
	BatchMessage importMessage = new BatchMessage();

	Map<String, Object> propertiesMap = importMessage.getPropertiesMap();

	propertiesMap.put(BatchMessage.SESSION_ID_KEY, sessionId);
	propertiesMap.put(BatchMessage.PROJECT_NAME_KEY, projectName);
	propertiesMap.put(BatchMessage.BATCH_PROCESS, BatchJobName.DELETE_RENAME_PROJECT_ATTRIBUTE);
	propertiesMap.put(BatchMessage.ITEMS_TO_PROCESS_KEY, itemsToProcess);

	return importMessage;
    }

    private BatchJobExecutor getBatchJobExecutor() {
	return _batchJobExecutor;
    }

    private BatchJobRegister<String> getBatchJobRegister() {
	return _batchJobRegister;
    }

    private DeleteProjectAttributesNotificationListener getDeleteProjectAttributesNotificationListener() {
	return _deleteProjectAttributesNotificationListener;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private TermService getTermService() {
	return _termService;
    }

    private void maintainDescriptions(List<Attribute> incomingAttributes, List<Attribute> existingAttributes,
	    Map<org.gs4tr.termmanager.model.AttributeLevelEnum, List<String>> descriptionsByLevel,
	    Map<org.gs4tr.termmanager.model.AttributeLevelEnum, List<Attribute>> descriptionsForRenaming) {

	if (CollectionUtils.isEmpty(existingAttributes)) {
	    return;
	}

	for (Attribute attribute : existingAttributes) {
	    boolean found = false;
	    for (Attribute incomingAttribute : incomingAttributes) {
		if (attribute.equalsCustom(incomingAttribute)) {
		    found = true;
		    if (StringUtils.isNotEmpty(incomingAttribute.getRenameValue())) {
			org.gs4tr.termmanager.model.AttributeLevelEnum attributeLevel = incomingAttribute
				.getAttributeLevel();

			List<Attribute> descToUpdate = descriptionsForRenaming.computeIfAbsent(attributeLevel,
				k -> new ArrayList<>());

			descToUpdate.add(incomingAttribute);
		    }
		    break;
		}
	    }

	    if (!found) {
		org.gs4tr.termmanager.model.AttributeLevelEnum attributeLevel = attribute.getAttributeLevel();
		List<String> descToDelete = descriptionsByLevel.computeIfAbsent(attributeLevel, k -> new ArrayList<>());
		descToDelete.add(attribute.getName());
	    }
	}
    }

    private void updateProjectAttributes(final Long projectId, final List<Attribute> incomingProjectAttributes) {
	getProjectService().addOrUpdateProjectAttributes(projectId, incomingProjectAttributes);
    }
}

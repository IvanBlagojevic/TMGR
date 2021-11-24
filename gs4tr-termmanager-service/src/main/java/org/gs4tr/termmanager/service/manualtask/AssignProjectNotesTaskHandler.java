package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
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
import org.gs4tr.termmanager.model.dto.InputFieldTypeEnum;
import org.gs4tr.termmanager.model.dto.ProjectNote;
import org.gs4tr.termmanager.model.dto.converter.ProjectNoteConverter;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.batch.BatchJob;
import org.gs4tr.termmanager.service.batch.executor.BatchJobExecutor;
import org.gs4tr.termmanager.service.batch.register.BatchJobRegister;
import org.gs4tr.termmanager.service.model.command.AssignProjectNotesCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoAssignProjectNotesCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.notification.listeners.DeleteProjectNotesNotificationListener;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignProjectNotesTaskHandler extends AbstractManualTaskHandler {

    private static final String ALL_NOTES = "allNotes";

    private static final String FIELD_TYPES = "fieldTypes";

    private static final String PROJECT = "PROJECT";

    private static final String PROJECT_NOTES_KEY = "projectNotes";

    @Autowired
    private BatchJobExecutor _batchJobExecutor;

    @Autowired
    private BatchJobRegister<String> _batchJobRegister;

    @Autowired
    private DeleteProjectNotesNotificationListener _deleteProjectNotesNotificationListener;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TermService _termService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoAssignProjectNotesCommand.class;
    }

    public DeleteProjectNotesNotificationListener getDeleteProjectNotesNotificationListener() {
	return _deleteProjectNotesNotificationListener;
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
	Long id = parentIds[0];

	TmProject project = getProjectService().findProjectById(id, Attribute.class);

	TaskModel newTaskModel = new TaskModel(null, new Ticket(id));

	ProjectNote[] dtoNotes = ProjectNoteConverter.fromInternalToDto(project.getNotes());
	newTaskModel.addObject(PROJECT_NOTES_KEY, dtoNotes == null ? new ArrayList<Attribute>() : dtoNotes);
	newTaskModel.addObject(ALL_NOTES, ManualTaskHandlerUtils.getAllNotesResponse());
	newTaskModel.addObject(FIELD_TYPES, InputFieldTypeEnum.getValues());

	return new TaskModel[] { newTaskModel };
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    public TermService getTermService() {
	return _termService;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	final Long projectId = taskIds[0];

	TaskResponse taskResponse = new TaskResponse(null);

	final String username = TmUserProfile.getCurrentUserName();

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);
	List<Attribute> descriptions = project.getDescriptions();

	AssignProjectNotesCommand assignCommand = (AssignProjectNotesCommand) command;

	final List<Attribute> incommingNotes = assignCommand.getProjectNotes();

	if (CollectionUtils.isNotEmpty(descriptions)) {
	    incommingNotes.addAll(descriptions);
	}

	List<Attribute> existingNotes = project.getNotes();

	final List<String> notesForDeletion = new ArrayList<String>();

	final List<Attribute> notesForRenaming = new ArrayList<Attribute>();

	collectNotesForDelitionAndRenaming(existingNotes, incommingNotes, notesForDeletion, notesForRenaming);

	final List<String> projectLanguages = getProjectService().getProjectLanguageCodes(projectId);

	if (!notesForDeletion.isEmpty() || !notesForRenaming.isEmpty()) {
	    taskResponse.addObject(ManualTaskHandlerUtils.START_PINGING, Boolean.TRUE);
	    BatchJob batchJob = new BatchJob() {

		@Override
		public void start(NotifyingMessageListener<BatchMessage> listener, BatchMessage batchMessage) {
		    if (CollectionUtils.isNotEmpty(notesForDeletion)) {
			getTermService().deleteTermDescriptionsByType(notesForDeletion, projectId, Description.NOTE,
				projectLanguages);
		    }

		    if (CollectionUtils.isNotEmpty(notesForRenaming)) {
			getTermService().renameTermDescriptions(projectId, Description.NOTE, notesForRenaming,
				projectLanguages);
		    }
		    updateProjectNotes(projectId, incommingNotes);

		    listener.notify(batchMessage);
		}
	    };

	    String projectName = project.getProjectInfo().getName();

	    String notesToDelete = appendNotesForDeletion(notesForDeletion, projectName);

	    String finalNotesChanged = appendNotesForRenaming(notesForRenaming, notesToDelete);

	    BatchMessage message = createBatchMessage(username, projectName, BatchJobName.DELETE_PROJECT_NOTE,
		    finalNotesChanged);

	    getBatchJobRegister().registerBatchJob(username, BatchJobName.DELETE_PROJECT_NOTE);

	    getBatchJobExecutor().execute(batchJob, message, getDeleteProjectNotesNotificationListener());

	} else {
	    taskResponse.addObject(ManualTaskHandlerUtils.START_PINGING, Boolean.FALSE);
	    updateProjectNotes(projectId, incommingNotes);
	}

	return taskResponse;
    }

    private String appendNotesForDeletion(List<String> notesToDelete, String projectName) {

	StringBuilder builder = new StringBuilder();
	builder.append(PROJECT + StringConstants.COLON + StringConstants.SPACE).append(projectName);
	builder.append("<br/>");
	builder.append("NOTES: ");
	for (String note : notesToDelete) {
	    builder.append(note);
	    builder.append(StringConstants.COMMA);
	}

	builder.deleteCharAt(builder.length() - 1);
	return builder.toString();
    }

    private String appendNotesForRenaming(List<Attribute> notesToRename, String notesToDelete) {
	StringBuilder builder = new StringBuilder(notesToDelete);
	builder.append("<br/>");
	builder.append("Renamed notes: ");
	for (Attribute note : notesToRename) {
	    builder.append(note.getName());
	    builder.append(StringConstants.COMMA);
	}

	builder.deleteCharAt(builder.length() - 1);
	return builder.toString();
    }

    private void collectNotesForDelitionAndRenaming(List<Attribute> existingNotes, List<Attribute> incommingNotes,
	    List<String> notesForDeletion, List<Attribute> notesForRenaming) {
	if (CollectionUtils.isEmpty(existingNotes)) {
	    return;
	}
	for (Attribute projectNote : existingNotes) {
	    if (isDeleted(incommingNotes, projectNote)) {
		notesForDeletion.add(projectNote.getName());
	    }

	    for (Attribute incommingNote : incommingNotes) {
		if (StringUtils.isEmpty(incommingNote.getRenameValue())) {
		    continue;
		}

		if (projectNote.getName().equals(incommingNote.getName())) {
		    notesForRenaming.add(incommingNote);
		}
	    }
	}
    }

    private BatchMessage createBatchMessage(String sessionId, String projectName, BatchJobName batchProcessName,
	    String itemsToProcess) {
	BatchMessage importMessage = new BatchMessage();

	Map<String, Object> propertiesMap = importMessage.getPropertiesMap();

	propertiesMap.put(BatchMessage.SESSION_ID_KEY, sessionId);
	propertiesMap.put(BatchMessage.PROJECT_NAME_KEY, projectName);
	propertiesMap.put(BatchMessage.BATCH_PROCESS, batchProcessName);
	propertiesMap.put(BatchMessage.ITEMS_TO_PROCESS_KEY, itemsToProcess);

	return importMessage;
    }

    private BatchJobExecutor getBatchJobExecutor() {
	return _batchJobExecutor;
    }

    private BatchJobRegister<String> getBatchJobRegister() {
	return _batchJobRegister;
    }

    private boolean isDeleted(List<Attribute> incommingNotes, Attribute projectNote) {
	for (Attribute incommingNote : incommingNotes) {
	    String incommingNoteName = incommingNote.getName();
	    String projectNoteName = projectNote.getName();
	    if (incommingNoteName.equals(projectNoteName)) {
		return false;
	    }
	}
	return true;
    }

    private void updateProjectNotes(final Long projectId, List<Attribute> incommingNotes) {
	getProjectService().addOrUpdateProjectAttributes(projectId, incommingNotes);
    }
}

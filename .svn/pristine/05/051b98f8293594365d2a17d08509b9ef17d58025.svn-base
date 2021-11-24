package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.ProjectDescription;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.service.ProjectDescriptionService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.manualtask.ProjectDescriptionManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.ProjectDescriptionCommand;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("project_description")
public class ProjectDescriptionTaskHandlerTest extends AbstractSolrGlossaryTest {

    private static final String DESCRIPTION_TEXT = "descriptionText";
    private static final String PROJECT_TICKET = "projectTicket";
    private static final String TASK_NAME = "manage project description";

    @Autowired
    private ProjectDescriptionService _projectDescriptionService;

    @Test
    @TestCase("process_tasks")
    public void addNewProjectDescription() {

	ManualTaskHandler taskHandler = getHandler(TASK_NAME);
	assertEquals(taskHandler.getClass(), ProjectDescriptionManualTaskHandler.class);

	Object command = getTaskHandlerCommand(taskHandler, "addProjectDescription.json");
	assertEquals(command.getClass(), ProjectDescriptionCommand.class);

	TaskResponse taskResponse = taskHandler.processTasks(null, null, command, null);
	assertNotNull(taskResponse);

	ProjectDescriptionCommand descriptionCommand = (ProjectDescriptionCommand) command;
	ProjectDescriptionService service = getProjectDescriptionService();

	ProjectDescription description = service.findByProjectId(descriptionCommand.getProjectId());
	assertEquals("New project description", description.getText());
	Long projectId = descriptionCommand.getProjectId();

	TmProject project = getProjectService().load(projectId);
	assertTrue(project.getAvailableDescription());
    }

    @Test
    @TestCase("process_tasks")
    public void deleteProjectDescription() {

	ManualTaskHandler taskHandler = getHandler(TASK_NAME);
	assertEquals(taskHandler.getClass(), ProjectDescriptionManualTaskHandler.class);

	Object command = getTaskHandlerCommand(taskHandler, "deleteProjectDescription.json");
	assertEquals(command.getClass(), ProjectDescriptionCommand.class);

	TaskResponse taskResponse = taskHandler.processTasks(null, null, command, null);
	assertNotNull(taskResponse);

	ProjectDescriptionCommand descriptionCommand = (ProjectDescriptionCommand) command;
	ProjectDescriptionService service = getProjectDescriptionService();

	ProjectDescription description = service.findByProjectId(descriptionCommand.getProjectId());
	Long projectId = descriptionCommand.getProjectId();
	TmProject project = getProjectService().load(projectId);

	assertNull(description);
	assertFalse(project.getAvailableDescription());
    }

    @Test
    @TestCase("get_task_infos")
    public void retrieveProjectDescription1() {

	ManualTaskHandler taskHandler = getHandler(TASK_NAME);
	assertEquals(taskHandler.getClass(), ProjectDescriptionManualTaskHandler.class);

	Object command = getTaskHandlerCommand(taskHandler, "getProjectDescription1.json");
	assertEquals(command.getClass(), ProjectDescriptionCommand.class);

	TaskModel[] models = taskHandler.getTaskInfos(null, TASK_NAME, command);
	TaskModel model = models[0];
	String descriptionText = "Existing project description";

	assertNotNull(model);
	assertNotNull(model.getModel().get(PROJECT_TICKET));
	assertEquals(descriptionText, model.getModel().get(DESCRIPTION_TEXT));
    }

    @Test
    @TestCase("get_task_infos")
    public void retrieveProjectDescription2() {

	ManualTaskHandler taskHandler = getHandler(TASK_NAME);
	assertEquals(taskHandler.getClass(), ProjectDescriptionManualTaskHandler.class);

	Object command = getTaskHandlerCommand(taskHandler, "getProjectDescription2.json");
	assertEquals(command.getClass(), ProjectDescriptionCommand.class);

	TaskModel[] models = taskHandler.getTaskInfos(null, TASK_NAME, command);
	TaskModel model = models[0];

	assertNotNull(model);
	assertNotNull(model.getModel().get(PROJECT_TICKET));
	assertEquals("", model.getModel().get(DESCRIPTION_TEXT));
    }

    @Test
    @TestCase("process_tasks")
    public void updateExistingProjectDescription() {

	ManualTaskHandler taskHandler = getHandler(TASK_NAME);
	assertEquals(taskHandler.getClass(), ProjectDescriptionManualTaskHandler.class);

	ProjectDescription existingDescription = getProjectDescriptionService().findByProjectId(1L);
	String text = existingDescription.getText();
	assertTrue(StringUtils.isNotEmpty(text));

	Object command = getTaskHandlerCommand(taskHandler, "updateProjectDescription.json");
	assertEquals(command.getClass(), ProjectDescriptionCommand.class);

	TaskResponse taskResponse = taskHandler.processTasks(null, null, command, null);
	assertNotNull(taskResponse);

	ProjectDescriptionCommand descriptionCommand = (ProjectDescriptionCommand) command;
	ProjectDescriptionService service = getProjectDescriptionService();
	Long projectId = descriptionCommand.getProjectId();

	ProjectDescription description = service.findByProjectId(projectId);
	String newText = "Updated project description";
	assertEquals(newText, description.getText());
    }

    private ProjectDescriptionService getProjectDescriptionService() {
	return _projectDescriptionService;
    }

}

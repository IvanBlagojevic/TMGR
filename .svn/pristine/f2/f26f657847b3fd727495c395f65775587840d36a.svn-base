package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.manualtask.ExportDocumentTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.ExportCommand;
import org.gs4tr.termmanager.service.utils.AdminTasksHolderHelper;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class ExportDocumentTaskHandlerTest extends AbstractManualtaskTest {

    private static final String TASK_NAME = "export tbx";

    @Autowired
    private ExportDocumentTaskHandler _handler;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private AdminTasksHolderHelper _tasksHolderHelper;

    @Test
    @TestCase("exportTbx")
    public void exportTbxTest_01() {
	ExportCommand command = getModelObject("exportCommand", ExportCommand.class);

	List<Task> tasks = new ArrayList<Task>();
	tasks.add(new Task(TASK_NAME));

	AdminTasksHolderHelper tasksHolderHelper = getTasksHolderHelper();
	when(tasksHolderHelper.getSystemEntityTasks(1L, null, EntityTypeHolder.TERMENTRY)).thenReturn(tasks);

	TaskModel[] taskInfos = getHandler().getTaskInfos(new Long[] {}, TASK_NAME, command);
	taskInfos[0].getModel().get("terms");
	verifyMock();

	String result = JsonUtils.writeValueAsString(taskInfos);

	try {
	    assertJSONResponse(result, "exportTbxValidation.json");
	} catch (Exception e) {
	    Assert.fail(e.getMessage());
	}
	TaskResponse response = getHandler().processTasks(new Long[] {}, null, command, null);

	Assert.assertNotNull(response);

	Map<String, Object> model = response.getModel();
	Assert.assertNotNull(model);

	String threadName = model.get("threadName").toString();
	Assert.assertNotNull(threadName);
    }

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
	reset(getProjectService());
	reset(getTasksHolderHelper());

	List<TmProject> projects = new ArrayList<TmProject>();
	TmProject tmProject = getModelObject("tmProject", TmProject.class);
	projects.add(tmProject);

	when(getProjectService().findProjectByIds(anyList())).thenReturn(projects);
    }

    private ExportDocumentTaskHandler getHandler() {
	return _handler;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private AdminTasksHolderHelper getTasksHolderHelper() {
	return _tasksHolderHelper;
    }

    @SuppressWarnings("unchecked")
    private void verifyMock() {
	verify(getProjectService(), times(1)).findProjectByIds(anyList());
    }
}

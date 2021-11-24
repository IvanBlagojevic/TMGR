package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.ImportTermService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.concurrency.ServiceThreadPoolHandler;
import org.gs4tr.termmanager.service.file.manager.FileManager;
import org.gs4tr.termmanager.service.file.manager.ImportFileNameMaker;
import org.gs4tr.termmanager.service.manualtask.ImportTbxDocumentTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.ImportCommand;
import org.gs4tr.termmanager.service.utils.AdminTasksHolderHelper;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class ImportDocumentTaskHandlerTest extends AbstractManualtaskTest {

    private static final Log LOG = LogFactory.getLog(ImportDocumentTaskHandlerTest.class);
    private static final String PARENT_DIR = "xls";
    private static final String TASK_NAME = "import tbx";

    @Autowired
    private ImportTermService _importTermService;

    @Autowired
    private FileManager _manager;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private ImportTbxDocumentTaskHandler _taskHandler;

    @Autowired
    private AdminTasksHolderHelper _tasksHolderHelper;

    public FileManager getFileManager() {
	return _manager;
    }

    public ImportTermService getImportTermService() {
	return _importTermService;
    }

    @Test
    @TestCase("importGetTaskInfos")
    public void getTaskInfosTest() {
	TmProject project = getModelObject("tmProject", TmProject.class);

	List<TmProject> projects = Collections.singletonList(project);
	List<Task> tasks = Collections.singletonList(new Task(TASK_NAME));

	when(getProjectService().getUserProjects(Mockito.anyLong())).thenReturn(projects);
	when(getTasksHolderHelper().getSystemEntityTasks(1L, null, EntityTypeHolder.TERMENTRY)).thenReturn(tasks);

	TaskModel[] taskInfos = getTaskHandler().getTaskInfos(new Long[] { 1L }, TASK_NAME, null);

	verify(getTasksHolderHelper(), times(1)).getSystemEntityTasks(1L, null, EntityTypeHolder.TERMENTRY);

	String response = JsonUtils.writeValueAsString(taskInfos);
	try {
	    assertJSONResponse(response, "importGetTaskInfosValidation.json");
	} catch (Exception e) {
	    fail(e.getMessage());
	}
    }

    @Test
    @TestCase("importProcessTasks")
    public void importOptionsSynonymNumberTest() throws Exception {
	final String folder = UUID.randomUUID().toString();

	// Upload xls file
	List<String> pathnames = uploadRepositoryItem(folder, "test5.xls");
	String pathname = pathnames.get(0);

	TmProject project = getModelObject("tmProject", TmProject.class);

	ImportCommand importCommand = getModelObject("importCommand", ImportCommand.class);
	setImportCommand(folder, pathname, importCommand);

	when(getProjectService().findProjectById(Mockito.anyLong(), any())).thenReturn(project);

	// Import xls file
	getTaskHandler().processTasks(null, null, importCommand, null);

	ArgumentCaptor<ImportOptionsModel> argument = ArgumentCaptor.forClass(ImportOptionsModel.class);

	// Waiting for the import to be completed
	waitServiceThreadPoolThreads();

	verify(getImportTermService(), atLeastOnce()).importDocument(Mockito.anyObject(), Mockito.anyObject(),
		argument.capture(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());

	Assert.assertEquals(5, argument.getValue().getSynonymNumber());
    }

    @Before
    public void setUp() throws Exception {
	reset(getProjectService());
	reset(getTasksHolderHelper());
	reset(getImportTermService());
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private URL getResourceFrom(final String pathname) {
	Class<ImportDocumentTaskHandlerTest> clazz = ImportDocumentTaskHandlerTest.class;
	return clazz.getClassLoader().getResource(Paths.get(PARENT_DIR, pathname).toString());
    }

    private ImportTbxDocumentTaskHandler getTaskHandler() {
	return _taskHandler;
    }

    private AdminTasksHolderHelper getTasksHolderHelper() {
	return _tasksHolderHelper;
    }

    private void setImportCommand(String folder, String pathname, ImportCommand importCommand) {

	Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	List<String> languages = Arrays.asList("en-US", "en", "fr-FR", "de");
	importLanguagesPerFile.put(pathname, languages);

	Map<String, Integer> numberOfTermEntriesByFileName = new HashMap<>();
	numberOfTermEntriesByFileName.put(pathname, 2);

	importCommand.setFolder(folder);
	importCommand.setImportLanguagesPerFile(importLanguagesPerFile);
	importCommand.setNumberOfTermEntriesByFileName(numberOfTermEntriesByFileName);
    }

    private List<String> uploadRepositoryItem(String folder, String fileName) throws IOException, URISyntaxException {
	InputStream stream = Files.newInputStream(Paths.get(getResourceFrom(fileName).toURI()));

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setName(fileName);
	resourceInfo.setSize((long) stream.available());
	resourceInfo.setEncoding(StandardCharsets.UTF_8.name());
	resourceInfo.setPath(folder);

	RepositoryItem repositoryItem = new RepositoryItem();
	repositoryItem.setInputStream(stream);
	repositoryItem.setResourceInfo(resourceInfo);

	List<String> pathnames = getFileManager().store(repositoryItem);

	if (LOG.isDebugEnabled()) {

	    LOG.debug(String.format("File(s) %s are uploaded in /%s folder.", //$NON-NLS-1$
		    pathnames.stream().map(ImportFileNameMaker::makeOriginalFrom).collect(Collectors.toList()),
		    folder));
	}
	return pathnames;
    }

    private void waitServiceThreadPoolThreads() {
	long start = System.currentTimeMillis();

	while (true) {
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		LOG.error(e.getMessage(), e);
		throw new RuntimeException(e);
	    }

	    int activeThreads = ServiceThreadPoolHandler.getActiveThreadsCount();

	    LogHelper.debug(LOG, String.format("Active threads count: %d", activeThreads));

	    if (activeThreads == 0) {
		break;
	    }

	    long now = System.currentTimeMillis();
	    if (now - start > 20000) {
		throw new RuntimeException("Time for waiting ServiceThreadPool threads exceeded.");
	    }
	}
    }
}

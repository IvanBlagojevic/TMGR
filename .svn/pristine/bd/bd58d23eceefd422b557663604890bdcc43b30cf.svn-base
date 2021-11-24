package org.gs4tr.termmanager.tests;

import static org.gs4tr.termmanager.cache.model.CacheName.IMPORT_PROGRESS_STATUS;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.dao.backup.BackupException;
import org.gs4tr.termmanager.io.edd.api.Event;
import org.gs4tr.termmanager.io.edd.api.Handler;
import org.gs4tr.termmanager.io.edd.event.ProcessDataEvent;
import org.gs4tr.termmanager.io.edd.handler.AbstractHandler;
import org.gs4tr.termmanager.io.edd.handler.BackupUpdateHandler;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.model.ImportErrorMessage;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguageReport;
import org.gs4tr.termmanager.service.file.manager.FileManager;
import org.gs4tr.termmanager.service.file.manager.ImportFileNameMaker;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.CheckImportProgressCommand;
import org.gs4tr.termmanager.service.model.command.ImportCommand;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

@TestSuite("import_tbx_document")
@Ignore
public class ImportTbxDocumentTaskHandlerIOTest extends AbstractSolrGlossaryTest {

    static final String CHECK_PROGRESS_TASK_NAME = "check import progress";
    static final String IMPORT_TASK_NAME = "import tbx";
    private static final String PARENT_DIR = "tmp";

    @Autowired
    private BackupUpdateHandler _backupUpdateHandler;

    @Autowired
    private CacheGateway<String, ImportProgressInfo> _cacheGateway;

    @Resource(name = "handlersMap")
    private Map<Class<? extends Event>, List<Handler<? extends Event>>> _handlersMap;

    @Autowired
    private FileManager _manager;

    @Test
    @TestCase("process_tasks")
    @SuppressWarnings("unchecked")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void importBackupHandlerExceptionTest()
	    throws IOException, URISyntaxException, TimeoutException, InterruptedException {

	Map<Class<? extends Event>, List<Handler<? extends Event>>> handlersMap = getHandlersMap();

	// Fake BackupHandler which will throw exception
	BackupHandlerTest backupHandlerTest = new BackupHandlerTest();

	// Replace BackupHandler with fake BackupHandlerTest
	handlersMap.get(ProcessDataEvent.class).remove(1);
	handlersMap.get(ProcessDataEvent.class).add(backupHandlerTest);

	final String folder = UUID.randomUUID().toString();
	String fileName1 = "import_medtronic.tbx";
	String language = "en-US";

	uploadRepositoryItem(folder, fileName1);

	ManualTaskHandler importTaskHandler = getHandler(IMPORT_TASK_NAME);
	ManualTaskHandler checkImportProgressTaskHandler = getHandler(CHECK_PROGRESS_TASK_NAME);

	// analysing file before import
	ImportCommand analysisCommand = (ImportCommand) getTaskHandlerCommand(importTaskHandler,
		"filesAnalysisRequest.json", new String[] { "$folder", folder },
		new String[] { "$sourceLanguage", language });

	final Map<String, Object> model = analyzeAsync(analysisCommand);

	// creating import command
	ImportCommand importCommand = (ImportCommand) getTaskHandlerCommand(importTaskHandler,
		"importTbxFilesRequest.json");
	importCommand.setFolder(folder);

	List<ImportLanguageReport> languageReports = (List<ImportLanguageReport>) model.get("importLanguageReports");
	ImportLanguageReport file1 = find(languageReports, report -> report.getFileName().contains(fileName1));

	Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	importLanguagesPerFile.put(file1.getFileName(),
		file1.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));

	importCommand.setImportLanguagesPerFile(importLanguagesPerFile);

	Map<String, Integer> numberOfTermEntriesByFileName = importCommand.getNumberOfTermEntriesByFileName();
	numberOfTermEntriesByFileName.put(file1.getFileName(), file1.getNumberOfTermEntries());

	// starting import
	TaskResponse response = importTaskHandler.processTasks(null, null, importCommand, null);

	List<String> threads = (List<String>) response.getModel().get("importThreadNames");
	Set<String> threadNames = new HashSet<>(threads);

	Thread.sleep(200);

	String name = threadNames.iterator().next();

	Map<String, ImportProgressInfo> importInfos = null;

	int importMessagesSize = 0;

	long timeStart = new Date().getTime();

	while (importMessagesSize == 0) {
	    importInfos = getImportCache().getAll(IMPORT_PROGRESS_STATUS, threadNames);
	    importMessagesSize = importInfos.get(name).getImportSummary().getErrorMessages().size();

	    long currentTime = new Date().getTime();
	    if (currentTime - timeStart > 20000) {
		throw new RuntimeException("Import is not finished");
	    }
	}

	List<ImportErrorMessage> errorMessages = importInfos.get(name).getImportSummary().getErrorMessages();

	assertEquals(1, errorMessages.size());

	assertEquals("org.gs4tr.termmanager.dao.backup.BackupException: Error BackupHandler",
		errorMessages.get(0).getErrorMessage());

	CheckImportProgressCommand checkImportProgressCommand = new CheckImportProgressCommand();
	checkImportProgressCommand.setImportThreadNames(threadNames);

	TaskModel[] taskModels = null;

	boolean isImportFinished = false;

	// checking import progress until import is aborted
	while (!isImportFinished) {
	    taskModels = checkImportProgressTaskHandler.getTaskInfos(null, null, checkImportProgressCommand);
	    Integer percentage = (Integer) taskModels[0].getModel().get("importPercentage");
	    if (percentage == 100) {
		isImportFinished = true;
	    }
	}

    }

    private <T> T find(Collection<T> c, Predicate<T> condition) {
	return c.stream().filter(condition).findFirst().orElseThrow(NoSuchElementException::new);
    }

    private BackupUpdateHandler getBackupUpdateHandler() {
	return _backupUpdateHandler;
    }

    private FileManager getFileManager() {
	return _manager;
    }

    private Map<Class<? extends Event>, List<Handler<? extends Event>>> getHandlersMap() {
	return _handlersMap;
    }

    private CacheGateway<String, ImportProgressInfo> getImportCache() {
	return _cacheGateway;
    }

    private URL getResourceFrom(final String pathname) {
	Class<ImportTbxDocumentTaskHandlerTest> clazz = ImportTbxDocumentTaskHandlerTest.class;
	return clazz.getClassLoader().getResource(Paths.get(PARENT_DIR, pathname).toString());
    }

    private void uploadRepositoryItem(String folder, String fileName) throws IOException, URISyntaxException {
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
    }

    // Fake BackupHandler will throw BackupException when onEvent method is invoked
    private class BackupHandlerTest extends AbstractHandler implements Handler<ProcessDataEvent> {

	@Override
	public void onEvent(ProcessDataEvent event) throws EventException {
	    throw new BackupException("Error BackupHandler");
	}

	@Override
	protected void logMessage(String message) {

	}
    }

}

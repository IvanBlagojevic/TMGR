package org.gs4tr.termmanager.tests;

import static java.util.Collections.singletonList;
import static org.gs4tr.termmanager.cache.model.CacheName.IMPORT_PROGRESS_STATUS;
import static org.gs4tr.termmanager.service.manualtask.CheckAnalysisProgressTaskHandler.COMPLETED;
import static org.gs4tr.termmanager.service.manualtask.CheckAnalysisProgressTaskHandler.EXISTING_PROJECT_ATTRIBUTES;
import static org.gs4tr.termmanager.service.manualtask.CheckAnalysisProgressTaskHandler.FILE_ANALYSIS_ALERTS;
import static org.gs4tr.termmanager.service.manualtask.CheckAnalysisProgressTaskHandler.IMPORT_ATTRIBUTES_REPORTS;
import static org.gs4tr.termmanager.service.manualtask.CheckAnalysisProgressTaskHandler.IMPORT_LANGUAGES_REPORTS;
import static org.gs4tr.termmanager.service.manualtask.CheckAnalysisProgressTaskHandler.IMPORT_TYPE_REPORTS;
import static org.gs4tr.termmanager.service.manualtask.CheckAnalysisProgressTaskHandler.OVERWRITE_BY_TERM_ENTRY_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.dto.TmProjectDto;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.ImportTermService;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisAlerts;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttributeReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguageReport;
import org.gs4tr.termmanager.service.file.manager.FileManager;
import org.gs4tr.termmanager.service.file.manager.ImportFileNameMaker;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.CheckImportProgressCommand;
import org.gs4tr.termmanager.service.model.command.ImportCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StopWatch;

@TestSuite("import_tbx_document")
public class ImportTbxDocumentTaskHandlerTest extends AbstractSolrGlossaryTest {

    static final String CHECK_PROGRESS_TASK_NAME = "check import progress";

    static final String IMPORT_TASK_NAME = "import tbx";

    private static final String PARENT_DIR = "tmp";

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, ImportProgressInfo> _cacheGateway;

    @Autowired
    private ImportTermService _importTermService;

    @Autowired
    private FileManager _manager;

    @Test
    @TestCase("process_tasks")
    @SuppressWarnings("unchecked")
    public void cancelImportTest() throws IOException, URISyntaxException, TimeoutException, InterruptedException {

	final String folder = UUID.randomUUID().toString();
	String fileName1 = "import_medtronic.tbx";
	String fileName2 = "import_skype.tbx";
	String language = "en-US";

	uploadRepositoryItem(folder, fileName1);
	uploadRepositoryItem(folder, fileName2);

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
	ImportLanguageReport file2 = find(languageReports, report -> report.getFileName().contains(fileName2));

	Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	importLanguagesPerFile.put(file1.getFileName(),
		file1.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));
	importLanguagesPerFile.put(file2.getFileName(),
		file2.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));

	importCommand.setImportLanguagesPerFile(importLanguagesPerFile);

	Map<String, Integer> numberOfTermEntriesByFileName = importCommand.getNumberOfTermEntriesByFileName();
	numberOfTermEntriesByFileName.put(file1.getFileName(), file1.getNumberOfTermEntries());
	numberOfTermEntriesByFileName.put(file2.getFileName(), file2.getNumberOfTermEntries());

	// starting import
	TaskResponse response = importTaskHandler.processTasks(null, null, importCommand, null);

	List<String> threads = (List<String>) response.getModel().get("importThreadNames");
	Set<String> threadNames = new HashSet<>(threads);

	TaskModel[] taskModelsBeforeCancel = null;

	CheckImportProgressCommand checkImportProgressCommand = new CheckImportProgressCommand();
	checkImportProgressCommand.setImportThreadNames(threadNames);

	boolean isImportStarted = false;

	Object importPercentage = null;

	// wait to start import
	while (!isImportStarted) {
	    taskModelsBeforeCancel = checkImportProgressTaskHandler.getTaskInfos(null, null,
		    checkImportProgressCommand);

	    assertNotNull(taskModelsBeforeCancel);
	    assertTrue(taskModelsBeforeCancel.length > 0);

	    importPercentage = taskModelsBeforeCancel[0].getModel().get("importPercentage");
	    assertNotNull(importPercentage);

	    if ((int) importPercentage > 0) {
		isImportStarted = true;
	    }
	}

	// aborting import by user
	getImportTermService().cancelImportRequest(threadNames);

	Map<String, ImportProgressInfo> importInfos = getImportCache().getAll(IMPORT_PROGRESS_STATUS, threadNames);

	for (String thread : threadNames) {
	    assertTrue(importInfos.get(thread).isCanceled());
	}

	TaskModel[] taskModels = null;

	boolean isImportFinished = false;

	// checking import progress until import is aborted
	while (!isImportFinished) {
	    taskModels = checkImportProgressTaskHandler.getTaskInfos(null, null, checkImportProgressCommand);

	    assertNotNull(taskModels);
	    assertTrue(taskModels.length > 0);

	    importPercentage = taskModels[0].getModel().get("importPercentage");
	    assertNotNull(importPercentage);

	    if (taskModels[0].getModel().get("importCanceled").equals(Boolean.TRUE)) {
		isImportFinished = true;
	    }
	}

	assertTrue((int) importPercentage < 100);

    }

    @After
    public void manualCleanup() throws IOException {
	getFileManager().cleanup();
    }

    @Test
    @TestCase("process_tasks")
    public void testAsynchronousAnalysistWithLargeXlFile()
	    throws IOException, URISyntaxException, TimeoutException, TmException {
	// TERII-4679 | Allow upload and analysis of large files

	/*
	 **********************************************************
	 * First phase: Upload 'large' Skype.xls (initially original xls file (over 125
	 * MB) was used)
	 **********************************************************
	 */
	final String folder = UUID.randomUUID().toString();
	final String fileName = "Skype.xls";

	StopWatch clock = new StopWatch("Profiling import file upload");
	try {
	    clock.start(String.format("Uploading: %s test file.", fileName));
	    uploadRepositoryItem(folder, fileName);
	} finally {
	    clock.stop();
	    LOG.info(clock.prettyPrint());
	}

	/*
	 **********************************************************
	 * Second phase: Run analysis asynchronously and wait for response
	 **********************************************************
	 */
	final ManualTaskHandler taskHandler = getHandler(IMPORT_TASK_NAME);
	ImportCommand analysisCommand = (ImportCommand) getTaskHandlerCommand(taskHandler, "filesAnalysisRequest.json");
	analysisCommand.setSourceLanguage(Locale.US.getCode());
	analysisCommand.setFolder(folder);

	final Map<String, Object> model = analyzeAsync(analysisCommand);
	assertTrue((boolean) model.get(COMPLETED));

	/*
	 **********************************************************
	 * NOTE: There is no need for a deeper check of analysis results here because it
	 * is already done through FilesAnalysisRequestHandlerTest tests.
	 **********************************************************
	 */
	assertNotNull(model.get(FILE_ANALYSIS_ALERTS));
	assertNotNull(model.get(IMPORT_LANGUAGES_REPORTS));
	assertNotNull(model.get(IMPORT_TYPE_REPORTS));
	assertNotNull(model.get(IMPORT_ATTRIBUTES_REPORTS));
	assertNotNull(model.get(EXISTING_PROJECT_ATTRIBUTES));
	assertNotNull(model.get(OVERWRITE_BY_TERM_ENTRY_ID));
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("process_tasks")
    public void testBatchImportOnHold()
	    throws IOException, InterruptedException, URISyntaxException, TmException, TimeoutException {
	/*
	 **********************************************************
	 * First phase: Upload xls files
	 **********************************************************
	 */
	final String folder = UUID.randomUUID().toString();
	uploadRepositoryItem(folder, "SkypeOnHoldPendingApproval.xls");
	uploadRepositoryItem(folder, "Nikon.xls");

	/*
	 **********************************************************
	 * Second phase: Run analysis (Analysis also should contain On Hold and Pending
	 * Approval statuses)
	 **********************************************************
	 */
	final ManualTaskHandler taskHandler = getHandler(IMPORT_TASK_NAME);
	ImportCommand analysisCommand = (ImportCommand) getTaskHandlerCommand(taskHandler, "filesAnalysisRequest.json");
	analysisCommand.setSourceLanguage(Locale.ENGLISH.getCode());
	analysisCommand.setFolder(folder);

	final Map<String, Object> model = analyzeAsync(analysisCommand);

	assertEquals(2, ((Map<Level, Set<String>>) model.get("existingProjectAttributes")).size());

	List<FileAnalysisAlerts> fileAnalysisAlerts = (List<FileAnalysisAlerts>) model.get("fileAnalysisAlerts");
	assertTrue(fileAnalysisAlerts.stream().map(FileAnalysisAlerts::getAlerts).allMatch(CollectionUtils::isEmpty));

	List<ImportLanguageReport> languageReports = (List<ImportLanguageReport>) model.get("importLanguageReports");
	assertEquals(2, languageReports.size());
	ImportLanguageReport skype = find(languageReports,
		report -> report.getFileName().contains("SkypeOnHoldPendingApproval.xls"));
	assertEquals(2, skype.getImportLanguages().size());
	assertEquals(1, skype.getNumberOfTermEntries());
	ImportLanguageReport nikon = find(languageReports, report -> report.getFileName().contains("Nikon.xls"));
	assertEquals(3, nikon.getImportLanguages().size());
	assertEquals(1, nikon.getNumberOfTermEntries());

	List<ImportAttributeReport> attributeReports = (List<ImportAttributeReport>) model
		.get("importAttributeReports");
	assertEquals(2, attributeReports.size());

	ImportAttributeReport skypeImportAttributeReport = find(attributeReports,
		report -> report.getFileName().contains("SkypeOnHoldPendingApproval.xls"));
	assertEquals(1, skypeImportAttributeReport.getImportAttributes().size());
	ImportAttributeReport nikonImportAttributeReport = find(attributeReports,
		report -> report.getFileName().contains("Nikon.xls"));
	assertEquals(4, nikonImportAttributeReport.getImportAttributes().size());

	/*
	 **********************************************************
	 * Third (last) phase: Import files
	 **********************************************************
	 */
	ImportCommand importCommnad = (ImportCommand) getTaskHandlerCommand(taskHandler, "importXlsFilesRequest.json");
	importCommnad.setFolder(folder);

	Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	importLanguagesPerFile.put(skype.getFileName(),
		skype.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));
	importLanguagesPerFile.put(nikon.getFileName(),
		nikon.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));

	importCommnad.setImportLanguagesPerFile(importLanguagesPerFile);

	Map<String, Integer> numberOfTermEntriesByFileName = importCommnad.getNumberOfTermEntriesByFileName();
	numberOfTermEntriesByFileName.put(skype.getFileName(), skype.getNumberOfTermEntries());
	numberOfTermEntriesByFileName.put(nikon.getFileName(), nikon.getNumberOfTermEntries());

	ProjectDetail projectDetailBefore = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);
	Set<ProjectLanguageDetail> languageDetailsBefore = projectDetailBefore.getLanguageDetails();

	assertOnHoldPendingApprovalCount("de-DE", 0, 0, languageDetailsBefore);

	final TaskResponse importTasksResponse = taskHandler.processTasks(null, null, importCommnad, null);

	/*
	 * Wait asynchronous thread to finish import and update project detail
	 */
	Thread.sleep(3000);

	final Map<String, Object> importModel = importTasksResponse.getModel();

	ProjectDetail projectDetailAfter = getProjectDetailService().findByProjectId(1L, ProjectLanguageDetail.class);
	Set<ProjectLanguageDetail> languageDetailsAfter = projectDetailAfter.getLanguageDetails();

	assertOnHoldPendingApprovalCount("de-DE", 1, 0, languageDetailsAfter);

	List<String> importThreadNames = ((List<String>) importModel.get("importThreadNames"));
	assertEquals(2, importThreadNames.size());
	int totalNumberOfTermEntries = (int) importModel.get("totalNumberOfTermEntries");
	assertEquals(2, totalNumberOfTermEntries);

	waitServiceThreadPoolThreads();

	List<TermEntry> termEntries = getTermEntryService().findTermEntriesByProjectId(PROJECT_ID);

	// At the begining, project was empty.
	assertEquals(totalNumberOfTermEntries, termEntries.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("process_tasks")
    public void testBatchImportRoundTripWithTwoTbxFiles()
	    throws IOException, InterruptedException, URISyntaxException, TmException, TimeoutException {
	/*
	 **********************************************************
	 * First phase: Upload tbx files
	 **********************************************************
	 */
	final String folder = UUID.randomUUID().toString();
	uploadRepositoryItem(folder, "Skype_SKY000001_27-03-2017-11-51-40.tbx");
	uploadRepositoryItem(folder, "Hilton_Terms.tbx");

	/*
	 **********************************************************
	 * Second phase: Run analysis
	 **********************************************************
	 */
	final ManualTaskHandler taskHandler = getHandler(IMPORT_TASK_NAME);
	ImportCommand analysisCommand = (ImportCommand) getTaskHandlerCommand(taskHandler, "filesAnalysisRequest.json");
	analysisCommand.setSourceLanguage(Locale.GERMANY.getCode());
	analysisCommand.setFolder(folder);

	final Map<String, Object> model = analyzeAsync(analysisCommand);

	List<FileAnalysisAlerts> fileAnalysisAlerts = (List<FileAnalysisAlerts>) model.get("fileAnalysisAlerts");
	assertTrue(fileAnalysisAlerts.stream().map(FileAnalysisAlerts::getAlerts).allMatch(CollectionUtils::isEmpty));

	List<ImportLanguageReport> languageReports = (List<ImportLanguageReport>) model.get("importLanguageReports");
	assertEquals(2, languageReports.size());

	ImportLanguageReport skype = find(languageReports,
		report -> report.getFileName().contains("Skype_SKY000001_27-03-2017-11-51-40.tbx"));
	assertEquals(3, skype.getImportLanguages().size());
	assertEquals(3, skype.getNumberOfTermEntries());
	ImportLanguageReport hilton = find(languageReports,
		report -> report.getFileName().contains("Hilton_Terms.tbx"));
	assertEquals(3, hilton.getImportLanguages().size());
	assertEquals(1, hilton.getNumberOfTermEntries());

	List<ImportAttributeReport> importAttributeReports = (List<ImportAttributeReport>) model
		.get("importAttributeReports");
	assertEquals(2, importAttributeReports.size());

	ImportAttributeReport skypeImportAttributeReport = find(importAttributeReports,
		report -> report.getFileName().contains("Skype_SKY000001_27-03-2017-11-51-40.tbx"));
	assertEquals(2, skypeImportAttributeReport.getImportAttributes().size());
	ImportAttributeReport hiltonImportAttributeReport = find(importAttributeReports,
		report -> report.getFileName().contains("Hilton_Terms.tbx"));
	assertEquals(2, hiltonImportAttributeReport.getImportAttributes().size());

	/*
	 **********************************************************
	 * Third (last) phase: Import files
	 **********************************************************
	 */
	ImportCommand importCommnad = (ImportCommand) getTaskHandlerCommand(taskHandler, "importTbxFilesRequest.json");
	importCommnad.setFolder(folder);

	Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	importLanguagesPerFile.put(skype.getFileName(),
		skype.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));
	importLanguagesPerFile.put(hilton.getFileName(),
		hilton.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));

	importCommnad.setImportLanguagesPerFile(importLanguagesPerFile);

	Map<String, Integer> numberOfTermEntriesByFileName = importCommnad.getNumberOfTermEntriesByFileName();
	numberOfTermEntriesByFileName.put(skype.getFileName(), skype.getNumberOfTermEntries());
	numberOfTermEntriesByFileName.put(hilton.getFileName(), hilton.getNumberOfTermEntries());

	TaskResponse importTasksResponse = taskHandler.processTasks(null, null, importCommnad, null);
	Map<String, Object> importModel = importTasksResponse.getModel();

	List<String> importThreadNames = (List<String>) importModel.get("importThreadNames");
	assertEquals(2, importThreadNames.size());
	int totalNumberOfTermEntries = (int) importModel.get("totalNumberOfTermEntries");
	assertEquals(4, totalNumberOfTermEntries);

	waitServiceThreadPoolThreads();

	TmgrSearchFilter filter = getTmgrFilter(PROJECT_ID, Locale.GERMANY.getCode(),
		Arrays.asList(Locale.US.getCode(), Locale.FRANCE.getCode()));

	// Please note that at the begining, glossary was empty.
	assertEquals(totalNumberOfTermEntries,
		getTermEntryService().getNumberOfTermEntries(filter).get(PROJECT_ID).intValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("process_tasks")
    public void testBatchImportRoundTripWithTwoXlsFiles()
	    throws IOException, InterruptedException, URISyntaxException, TmException, TimeoutException {
	/*
	 **********************************************************
	 * First phase: Upload xls files
	 **********************************************************
	 */
	final String folder = UUID.randomUUID().toString();
	uploadRepositoryItem(folder, "Skype.xls");
	uploadRepositoryItem(folder, "Nikon.xls");

	/*
	 **********************************************************
	 * Second phase: Run analysis
	 **********************************************************
	 */
	final ManualTaskHandler taskHandler = getHandler(IMPORT_TASK_NAME);
	ImportCommand analysisCommand = (ImportCommand) getTaskHandlerCommand(taskHandler, "filesAnalysisRequest.json");
	analysisCommand.setSourceLanguage(Locale.ENGLISH.getCode());
	analysisCommand.setFolder(folder);

	final Map<String, Object> model = analyzeAsync(analysisCommand);

	assertEquals(2, ((Map<Level, Set<String>>) model.get("existingProjectAttributes")).size());

	List<FileAnalysisAlerts> fileAnalysisAlerts = (List<FileAnalysisAlerts>) model.get("fileAnalysisAlerts");
	assertTrue(fileAnalysisAlerts.stream().map(FileAnalysisAlerts::getAlerts).allMatch(CollectionUtils::isEmpty));

	List<ImportLanguageReport> languageReports = (List<ImportLanguageReport>) model.get("importLanguageReports");
	assertEquals(2, languageReports.size());
	ImportLanguageReport skype = find(languageReports, report -> report.getFileName().contains("Skype.xls"));
	assertEquals(2, skype.getImportLanguages().size());
	assertEquals(1, skype.getNumberOfTermEntries());
	ImportLanguageReport nikon = find(languageReports, report -> report.getFileName().contains("Nikon.xls"));
	assertEquals(3, nikon.getImportLanguages().size());
	assertEquals(1, nikon.getNumberOfTermEntries());

	List<ImportAttributeReport> attributeReports = (List<ImportAttributeReport>) model
		.get("importAttributeReports");
	assertEquals(2, attributeReports.size());

	ImportAttributeReport skypeImportAttributeReport = find(attributeReports,
		report -> report.getFileName().contains("Skype.xls"));
	assertEquals(1, skypeImportAttributeReport.getImportAttributes().size());
	ImportAttributeReport nikonImportAttributeReport = find(attributeReports,
		report -> report.getFileName().contains("Nikon.xls"));
	assertEquals(4, nikonImportAttributeReport.getImportAttributes().size());

	/*
	 **********************************************************
	 * Third (last) phase: Import files
	 **********************************************************
	 */
	ImportCommand importCommnad = (ImportCommand) getTaskHandlerCommand(taskHandler, "importXlsFilesRequest.json");
	importCommnad.setFolder(folder);

	Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	importLanguagesPerFile.put(skype.getFileName(),
		skype.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));
	importLanguagesPerFile.put(nikon.getFileName(),
		nikon.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));

	importCommnad.setImportLanguagesPerFile(importLanguagesPerFile);

	Map<String, Integer> numberOfTermEntriesByFileName = importCommnad.getNumberOfTermEntriesByFileName();
	numberOfTermEntriesByFileName.put(skype.getFileName(), skype.getNumberOfTermEntries());
	numberOfTermEntriesByFileName.put(nikon.getFileName(), nikon.getNumberOfTermEntries());

	final TaskResponse importTasksResponse = taskHandler.processTasks(null, null, importCommnad, null);
	final Map<String, Object> importModel = importTasksResponse.getModel();

	List<String> importThreadNames = ((List<String>) importModel.get("importThreadNames"));
	assertEquals(2, importThreadNames.size());
	int totalNumberOfTermEntries = (int) importModel.get("totalNumberOfTermEntries");
	assertEquals(2, totalNumberOfTermEntries);

	waitServiceThreadPoolThreads();

	List<TermEntry> termEntries = getTermEntryService().findTermEntriesByProjectId(PROJECT_ID);

	// At the begining, project was empty.
	assertEquals(totalNumberOfTermEntries, termEntries.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("process_tasks")
    public void testBatchImportRoundTripWithZipFile()
	    throws IOException, InterruptedException, URISyntaxException, TmException, TimeoutException {
	/*
	 **********************************************************
	 * First phase: Upload zip file
	 **********************************************************
	 */
	final String folder = UUID.randomUUID().toString();
	uploadRepositoryItem(folder, "Skype.zip");

	/*
	 **********************************************************
	 * Second phase: Run analysis
	 **********************************************************
	 */
	final ManualTaskHandler taskHandler = getHandler(IMPORT_TASK_NAME);
	ImportCommand analysisCommand = (ImportCommand) getTaskHandlerCommand(taskHandler, "filesAnalysisRequest.json");
	analysisCommand.setSourceLanguage(Locale.GERMANY.getCode());
	analysisCommand.setFolder(folder);

	final Map<String, Object> model = analyzeAsync(analysisCommand);

	/*
	 **********************************************************
	 * Third (last) phase: Import files
	 **********************************************************
	 */
	ImportCommand command = (ImportCommand) getTaskHandlerCommand(taskHandler, "importTbxXlsFilesRequest.json");
	command.setFolder(folder);

	List<ImportLanguageReport> languageReports = (List<ImportLanguageReport>) model.get("importLanguageReports");
	ImportLanguageReport skypeTbx = find(languageReports,
		report -> report.getFileName().contains("Skype_SKY000001_27-03-2017-14-36-37.xlsx"));
	ImportLanguageReport skypeXls = find(languageReports,
		report -> report.getFileName().contains("Skype_SKY000001_27-03-2017-11-51-40.tbx"));

	Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	importLanguagesPerFile.put(skypeTbx.getFileName(),
		skypeTbx.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));
	importLanguagesPerFile.put(skypeXls.getFileName(),
		skypeXls.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));

	command.setImportLanguagesPerFile(importLanguagesPerFile);

	Map<String, Integer> numberOfTermEntriesByFileName = command.getNumberOfTermEntriesByFileName();
	numberOfTermEntriesByFileName.put(skypeTbx.getFileName(), skypeTbx.getNumberOfTermEntries());
	numberOfTermEntriesByFileName.put(skypeXls.getFileName(), skypeXls.getNumberOfTermEntries());

	TaskResponse importTasksResponse = taskHandler.processTasks(null, null, command, null);
	Map<String, Object> importModel = importTasksResponse.getModel();

	List<String> importThreadNames = (List<String>) importModel.get("importThreadNames");
	assertEquals(2, importThreadNames.size());
	int totalNumberOfTermEntries = (int) importModel.get("totalNumberOfTermEntries");
	assertEquals(5, totalNumberOfTermEntries);

	waitServiceThreadPoolThreads();

	TmgrSearchFilter filter = getTmgrFilter(PROJECT_ID, Locale.GERMANY.getCode(),
		Arrays.asList(Locale.US.getCode(), Locale.FRANCE.getCode()));

	// Please note that at the begining, glossary was empty.
	assertEquals(totalNumberOfTermEntries,
		getTermEntryService().getNumberOfTermEntries(filter).get(PROJECT_ID).intValue());
    }

    /*
     * TERII-4895 - Batch import of bilingual files, deletes terms of some
     * languages.
     */
    @SuppressWarnings("unchecked")
    @Test
    @TestCase("process_tasks")
    public void testBatchImport_TERII_4895() throws Exception {
	// getRegularConnector().getTmgrUpdater().deleteAll();
	/*
	 **********************************************************
	 * 1st phase: Upload pre-condition xls file
	 **********************************************************
	 */
	final String folder1st = UUID.randomUUID().toString();
	String pre_condition_file = "TERII_4895_pre_condition.xlsx";
	uploadRepositoryItem(folder1st, pre_condition_file);

	/*
	 **********************************************************
	 * 2nd phase: Run analysis
	 **********************************************************
	 */
	final ManualTaskHandler taskHandler = getHandler(IMPORT_TASK_NAME);
	ImportCommand analysisCommand = (ImportCommand) getTaskHandlerCommand(taskHandler, "filesAnalysisRequest.json");
	analysisCommand.setSourceLanguage(Locale.US.getCode());
	analysisCommand.setFolder(folder1st);

	Map<String, Object> model = analyzeAsync(analysisCommand);

	List<ImportLanguageReport> languageReports = (List<ImportLanguageReport>) model.get("importLanguageReports");
	assertEquals(1, languageReports.size());
	ImportLanguageReport pre_condition = find(languageReports,
		report -> report.getFileName().contains(pre_condition_file));

	/*
	 **********************************************************
	 * 3rd phase: import pre-condition
	 **********************************************************
	 */
	ImportCommand importCommnad = (ImportCommand) getTaskHandlerCommand(taskHandler, "importXlsFilesRequest.json");
	importCommnad.setFolder(folder1st);

	List<String> pre_condition_langs = pre_condition.getImportLanguages().stream().map(i -> i.getLocale().getCode())
		.collect(Collectors.toList());

	Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	importLanguagesPerFile.put(pre_condition.getFileName(), pre_condition_langs);

	importCommnad.setImportLanguagesPerFile(importLanguagesPerFile);

	importCommnad.getLanguageReplacementByCode().clear();

	Map<String, Integer> numberOfTermEntries = importCommnad.getNumberOfTermEntriesByFileName();
	numberOfTermEntries.put(pre_condition.getFileName(), pre_condition.getNumberOfTermEntries());

	taskHandler.processTasks(null, null, importCommnad, null);
	waitServiceThreadPoolThreads();

	/*
	 **********************************************************
	 * 4th phase: upload zipped TERII-4895 files
	 **********************************************************
	 */
	final String folder2nd = UUID.randomUUID().toString();
	String TERII_4895_zipped_file = "TERII_4895.zip";
	uploadRepositoryItem(folder2nd, TERII_4895_zipped_file);

	/*
	 **********************************************************
	 * 5th phase: analyze zipped TERII-4895 files
	 **********************************************************
	 */
	analysisCommand.setFolder(folder2nd);

	model = analyzeAsync(analysisCommand);

	languageReports = (List<ImportLanguageReport>) model.get("importLanguageReports");
	assertEquals(3, languageReports.size());

	ImportLanguageReport xls1 = find(languageReports, report -> report.getFileName().contains("en-us_de-de.xlsx"));
	ImportLanguageReport xls2 = find(languageReports, report -> report.getFileName().contains("en-us_fr-fr.xlsx"));
	ImportLanguageReport xls3 = find(languageReports, report -> report.getFileName().contains("en-us_it-it.xlsx"));

	/*
	 **********************************************************
	 * 6th phase: parallel import of TERII-4895 files
	 **********************************************************
	 */
	numberOfTermEntries = importCommnad.getNumberOfTermEntriesByFileName();
	numberOfTermEntries.clear();
	numberOfTermEntries.put(xls1.getFileName(), xls1.getNumberOfTermEntries());
	numberOfTermEntries.put(xls2.getFileName(), xls2.getNumberOfTermEntries());
	numberOfTermEntries.put(xls3.getFileName(), xls3.getNumberOfTermEntries());

	importCommnad.setFinalyOverwriteByTermEntryId(false);
	importCommnad.setFolder(folder2nd);

	List<String> xls1langs = xls1.getImportLanguages().stream().map(i -> i.getLocale().getCode())
		.collect(Collectors.toList());
	List<String> xls2langs = xls2.getImportLanguages().stream().map(i -> i.getLocale().getCode())
		.collect(Collectors.toList());
	List<String> xls3langs = xls3.getImportLanguages().stream().map(i -> i.getLocale().getCode())
		.collect(Collectors.toList());

	importLanguagesPerFile.clear();
	importLanguagesPerFile.put(xls1.getFileName(), xls1langs);
	importLanguagesPerFile.put(xls2.getFileName(), xls2langs);
	importLanguagesPerFile.put(xls3.getFileName(), xls3langs);

	importCommnad.setImportLanguagesPerFile(importLanguagesPerFile);

	taskHandler.processTasks(null, null, importCommnad, null);

	waitServiceThreadPoolThreads();

	/*
	 **********************************************************
	 * 7th phase: assert termEntry
	 **********************************************************
	 */

	TmgrSearchFilter filter = getTmgrFilter(PROJECT_ID, Locale.GERMANY.getCode(),
		Arrays.asList(Locale.US.getCode(), Locale.ITALIAN.getCode(), Locale.FRANCE.getCode()));

	Page<TermEntry> page = getTermEntryService().searchTermEntries(filter);
	assertEquals(1, page.getTotalResults());

	List<TermEntry> results = page.getResults();
	TermEntry termEntry = results.get(0);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	assertTrue(MapUtils.isNotEmpty(languageTerms));

	assertTrue(CollectionUtils.isNotEmpty(languageTerms.get(Locale.GERMANY.getCode())));
	assertTrue(CollectionUtils.isNotEmpty(languageTerms.get(Locale.FRANCE.getCode())));
	assertTrue(CollectionUtils.isEmpty(languageTerms.get(Locale.ITALY.getCode())));
    }

    @Test
    @TestCase("get_task_infos")
    public void testGetTaskInfos() throws Exception {
	ManualTaskHandler taskHandler = getHandler(IMPORT_TASK_NAME);
	TaskModel[] taskInfos = taskHandler.getTaskInfos(null, IMPORT_TASK_NAME, null);
	String jsonResponse = JsonUtils.writeValueAsString(taskInfos);
	assertJSONResponse(jsonResponse, "importGetTaskInfosValidation.json");
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("process_tasks")
    public void testImportCheckedTermNotesOnly()
	    throws IOException, InterruptedException, URISyntaxException, TmException, TimeoutException {
	// 19-May-2017, as per [Improvement#TERII-4707]:

	/*
	 **********************************************************
	 * First phase: Upload xls file
	 **********************************************************
	 */
	final String folder = UUID.randomUUID().toString();
	uploadRepositoryItem(folder, "Big.xls");

	/*
	 **********************************************************
	 * Second phase: Run analysis
	 **********************************************************
	 */
	final ManualTaskHandler taskHandler = getHandler(IMPORT_TASK_NAME);
	ImportCommand analysisCommand = (ImportCommand) getTaskHandlerCommand(taskHandler, "filesAnalysisRequest.json");
	analysisCommand.setSourceLanguage(Locale.US.getCode());
	analysisCommand.setFolder(folder);

	final Map<String, Object> model = analyzeAsync(analysisCommand);

	List<ImportLanguageReport> languageReports = (List<ImportLanguageReport>) model.get("importLanguageReports");
	assertEquals(1, languageReports.size());
	ImportLanguageReport big = find(languageReports, report -> report.getFileName().contains("Big.xls"));

	/*
	 **********************************************************
	 * Third (last) phase: Select only term notes that should be imported and import
	 * files
	 **********************************************************
	 */
	ImportCommand importCommnad = (ImportCommand) getTaskHandlerCommand(taskHandler,
		"importXlsFileCheckTermNotesOnlyRequest.json");
	importCommnad.setFolder(folder);

	Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	importLanguagesPerFile.put(big.getFileName(),
		big.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));

	importCommnad.setImportLanguagesPerFile(importLanguagesPerFile);

	Map<String, Integer> numberOfTermEntries = importCommnad.getNumberOfTermEntriesByFileName();
	numberOfTermEntries.put(big.getFileName(), big.getNumberOfTermEntries());

	taskHandler.processTasks(null, null, importCommnad, null);
	waitServiceThreadPoolThreads();

	List<TermEntry> termEntries = getTermEntryService().findTermEntriesByProjectId(PROJECT_ID);

	assertEquals(1, termEntries.size());

	TermEntry termEntry = termEntries.get(0);

	assertTrue(CollectionUtils.isEmpty(termEntry.getDescriptions()));

	assertTrue(termEntry.ggetTerms().stream().map(Term::getDescriptions).filter(Objects::nonNull)
		.flatMap(Set::stream).map(Description::getBaseType).allMatch(Predicate.isEqual(Description.NOTE)));
    }

    /*
     ***************************************************
     * TERII-5097 User cannot import terms as "On Hold"
     ***************************************************
     */
    @Test
    @TestCase("process_tasks")
    public void testUserImportDefaultStatuses() throws TimeoutException {

	// Expected statuses on Import Dialog
	List<String> expectedStatuses = Arrays.asList("PROCESSED", "WAITING", "ONHOLD");

	ManualTaskHandler taskHandler = getHandler(IMPORT_TASK_NAME);
	TaskModel[] taskInfos = taskHandler.getTaskInfos(null, IMPORT_TASK_NAME, null);

	assertNotNull(taskInfos);

	List<TmProjectDto> projectDtos = (List<TmProjectDto>) taskInfos[0].getModel().get("projects");

	for (TmProjectDto tmProjectDto : projectDtos) {
	    List<TmProjectDto.TermStatusDto> termStatuses = tmProjectDto.getTermStatuses();

	    List<String> termStatusNames = termStatuses.stream().map(ts -> ts.getName()).collect(Collectors.toList());

	    /*
	     * Dialog should contain all expected statuses in combo box that can be selected
	     * as import status
	     */
	    assertTrue(expectedStatuses.containsAll(termStatusNames));

	}

    }

    private void assertOnHoldPendingApprovalCount(String languageId, int onHoldCount, int pendingApprovalCount,
	    Set<ProjectLanguageDetail> languageDetails) {

	Optional<ProjectLanguageDetail> first = languageDetails.stream()
		.filter(ld -> ld.getLanguageId().equals(languageId)).findFirst();

	assertTrue(first.isPresent());

	ProjectLanguageDetail pld = first.get();

	assertEquals(onHoldCount, pld.getOnHoldTermCount());
	assertEquals(pendingApprovalCount, pld.getPendingApprovalCount());
    }

    private <T> T find(Collection<T> c, Predicate<T> condition) {
	return c.stream().filter(condition).findFirst().orElseThrow(NoSuchElementException::new);
    }

    private FileManager getFileManager() {
	return _manager;
    }

    private CacheGateway<String, ImportProgressInfo> getImportCache() {
	return _cacheGateway;
    }

    private ImportTermService getImportTermService() {
	return _importTermService;
    }

    private URL getResourceFrom(final String pathname) {
	Class<ImportTbxDocumentTaskHandlerTest> clazz = ImportTbxDocumentTaskHandlerTest.class;
	return clazz.getClassLoader().getResource(Paths.get(PARENT_DIR, pathname).toString());
    }

    private TmgrSearchFilter getTmgrFilter(Long projectId, String source, List<String> targets) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(singletonList(projectId));
	filter.setSourceLanguage(source);
	filter.setTargetLanguages(targets);
	return filter;
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

    @Override
    protected void populate() throws Exception {
	// Glossary can be empty before import
    }
}

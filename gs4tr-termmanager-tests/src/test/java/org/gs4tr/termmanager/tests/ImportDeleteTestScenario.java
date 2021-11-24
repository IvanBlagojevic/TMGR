package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisAlerts;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguageReport;
import org.gs4tr.termmanager.service.file.manager.FileManager;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.DeleteTermCommands;
import org.gs4tr.termmanager.service.model.command.ImportCommand;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.gs4tr.tm3.api.Page;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("import_tbx_document")
public class ImportDeleteTestScenario extends AbstractSolrGlossaryTest {

    @Autowired
    private FileManager _manager;

    @After
    public void manualCleanup() throws IOException {
	getFileManager().cleanup();
    }

    // TERII-3228 scenario
    @Test
    @TestCase("process_tasks")
    public void testImportDeleteScenario() throws IOException, InterruptedException, TimeoutException {
	importXls();

	Page<TermEntry> page = searchTermEntry();

	validate(page);

	Term term = searchTerm();

	deleteTerm(term.getTermEntryId(), term.getUuId());

	importXls();

	page = searchTermEntry();

	validate(page);
    }

    private void deleteTerm(String termEntryId, String termId) {

	String taskName = "disable term";

	ManualTaskHandler taskHandler = getHandler(taskName);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	DeleteTermCommands command = (DeleteTermCommands) getTaskHandlerCommand(taskHandler, "disableTerm.json",
		new String[] { "$projectTicket", projectTicket }, new String[] { "$termTicket1", termId },
		new String[] { "$termEntryTicket1", termEntryId }, new String[] { "$sourceLanguage", "en-US" },
		new String[] { "$includeSourceSynonyms", "false" }, new String[] { "$includeTargetSynonyms", "false" },
		new String[] { "$deleteTermEntries", "false" }, new String[] { "$targetLanguage1", "" });

	Long[] projectIds = new Long[] { PROJECT_ID };

	taskHandler.processTasks(projectIds, null, command, null);

	try {
	    Term term = getTermService().findTermById(termId, PROJECT_ID);
	    Assert.assertTrue(Objects.isNull(term));
	} catch (Exception e) {
	    Assert.assertTrue(true);
	}
    }

    private <T> T find(Collection<T> c, Predicate<T> condition) {
	return c.stream().filter(condition).findFirst().orElseThrow(NoSuchElementException::new);
    }

    private FileManager getFileManager() {
	return _manager;
    }

    @SuppressWarnings("unchecked")
    private void importXls() throws IOException, TimeoutException {
	ManualTaskHandler taskHandler = getHandler(ImportTbxDocumentTaskHandlerTest.IMPORT_TASK_NAME);

	InputStream xlsFile = new FileInputStream(new File("src/test/resources/tmp/Test2.xls"));

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setSize(new Long(xlsFile.available()));
	resourceInfo.setName("Test2.xls");
	resourceInfo.setEncoding("UTF-8");
	resourceInfo.setPath(UUID.randomUUID().toString());

	RepositoryItem repositoryItem = new RepositoryItem();
	repositoryItem.setInputStream(xlsFile);
	repositoryItem.setResourceInfo(resourceInfo);

	getFileManager().store(repositoryItem);

	ImportCommand analysisCommand = (ImportCommand) getTaskHandlerCommand(taskHandler, "filesAnalysisRequest.json");
	analysisCommand.setSourceLanguage(Locale.US.getCode());
	analysisCommand.setFolder(resourceInfo.getPath());

	Map<String, Object> model = analyzeAsync(analysisCommand);

	List<FileAnalysisAlerts> fileAnalysisAlerts = (List<FileAnalysisAlerts>) model.get("fileAnalysisAlerts");
	assertTrue(fileAnalysisAlerts.stream().map(FileAnalysisAlerts::getAlerts).allMatch(CollectionUtils::isEmpty));

	List<ImportLanguageReport> languageReports = (List<ImportLanguageReport>) model.get("importLanguageReports");
	assertEquals(1, languageReports.size());
	ImportLanguageReport test2 = find(languageReports, report -> report.getFileName().contains("Test2.xls"));
	assertEquals(2, test2.getImportLanguages().size());

	ImportLanguageReport report = languageReports.get(0);

	Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	importLanguagesPerFile.put(report.getFileName(),
		report.getImportLanguages().stream().map(l -> l.getLocale().getCode()).collect(Collectors.toList()));

	ImportCommand importCommnad = (ImportCommand) getTaskHandlerCommand(taskHandler, "importXlsTERII-3228.json");
	importCommnad.setFolder(resourceInfo.getPath());
	importCommnad.setImportLanguagesPerFile(importLanguagesPerFile);

	Map<String, Integer> numberOfTermEntriesByFileName = importCommnad.getNumberOfTermEntriesByFileName();
	numberOfTermEntriesByFileName.put(test2.getFileName(), test2.getNumberOfTermEntries());

	taskHandler.processTasks(null, null, importCommnad, null);
	waitServiceThreadPoolThreads();
    }

    private Term searchTerm() {
	Page<TermEntry> page = searchTermEntry();

	Assert.assertNotNull(page);
	Assert.assertEquals(1, page.getTotalResults());

	List<TermEntry> results = page.getResults();
	Assert.assertNotNull(results);

	Set<Term> deDeTerms = results.get(0).getLanguageTerms().get("de-DE");
	Assert.assertEquals(1, deDeTerms.size());

	return deDeTerms.iterator().next();
    }

    private Page<TermEntry> searchTermEntry() {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(1L);
	filter.setSourceLanguage("en-US");
	List<String> targets = new ArrayList<String>();
	targets.add("de-DE");
	filter.setTargetLanguages(targets);
	TextFilter textFilter = new TextFilter("cat", true);
	filter.setTextFilter(textFilter);
	filter.addLanguageResultField(true, SYNONYM_NUMBER, "en-US", "de-DE");
	filter.setExcludeDisabled(false);

	Page<TermEntry> page = getTermEntryService().searchTermEntries(filter);
	return page;
    }

    private void validate(Page<TermEntry> page) {
	Assert.assertNotNull(page);
	Assert.assertEquals(1, page.getTotalResults());

	List<TermEntry> results = page.getResults();
	Assert.assertNotNull(results);

	Set<Term> deDeTerms = results.get(0).getLanguageTerms().get("de-DE");
	Assert.assertEquals(1, deDeTerms.size());
    }
}

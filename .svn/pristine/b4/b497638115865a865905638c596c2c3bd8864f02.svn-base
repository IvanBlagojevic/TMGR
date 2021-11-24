package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.impl.ExportAdapter;
import org.gs4tr.termmanager.service.manualtask.DefaultSystemManualTaskManager;
import org.gs4tr.termmanager.service.manualtask.ExportDocumentTaskHandler;
import org.gs4tr.termmanager.service.manualtask.SystemManualTaskHandler;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TermEntryServiceTest extends AbstractSolrGlossaryTest {

    private static final String DESCRIPTION_DEFINITION = "definition";

    /* We are using this approach to find specific task handler bean */
    @Resource(name = "systemManualTaskManager")
    private DefaultSystemManualTaskManager _defaultSystemManualTaskManager;

    @Test
    public void exportDocumentCSVAllStatusesIncludeSourceTest() throws Exception {
	addTempTermEntries();
	String exportTypeEnum = ExportFormatEnum.CSVEXPORT.name();

	File testFile = new File("target/Document_Export_testFile.csv");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	TermEntrySearchRequest searchRequest = createSearchRequest("en-US", "de-DE", 2L);
	searchRequest.setIncludeSource(true);
	searchRequest.setTermAttributes(Arrays.asList(DESCRIPTION_DEFINITION));

	TmgrSearchFilter filter = createFilterFromRequest(searchRequest);

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory
		.getInstance(ExportFormatEnum.valueOf(exportTypeEnum));
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocument(searchRequest, filter, exportTypeEnum,
		new ExportAdapter());

	Assert.assertNotNull(exportInfo);
	Assert.assertEquals(Arrays.asList(DESCRIPTION_DEFINITION), exportInfo.getDescriptionsToExport());
	Assert.assertEquals(ExportFormatEnum.CSVEXPORT.name(), exportInfo.getExportFormat());
	Assert.assertEquals(5, exportInfo.getTotalTermEntriesExported());
	Assert.assertEquals(14, exportInfo.getTotalTermsExported());
	Assert.assertEquals(exportTypeEnum, exportInfo.getExportFormat());
    }

    @Test
    public void exportDocumentCSVStatusesExcludeSourceTest() throws Exception {
	addTempTermEntries();
	String exportTypeEnum = ExportFormatEnum.CSVEXPORT.name();

	File testFile = new File("target/Document_Export_testFile.csv");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	TermEntrySearchRequest searchRequest = createSearchRequest("en-US", "de-DE", 2L);
	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());
	searchRequest.setStatuses(statuses);
	searchRequest.setIncludeSource(false);

	TmgrSearchFilter filter = createFilterFromRequest(searchRequest);

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory
		.getInstance(ExportFormatEnum.valueOf(exportTypeEnum));
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocument(searchRequest, filter, exportTypeEnum,
		new ExportAdapter());

	Assert.assertNotNull(exportInfo);
	Assert.assertEquals(ExportFormatEnum.CSVEXPORT.name(), exportInfo.getExportFormat());
	Assert.assertEquals(1, exportInfo.getTotalTermEntriesExported());
	Assert.assertEquals(1, exportInfo.getTotalTermsExported());
	Assert.assertEquals(exportTypeEnum, exportInfo.getExportFormat());
    }

    @Test
    public void exportDocumentCSVStatusesIncludeSourceTest() throws Exception {
	addTempTermEntries();
	String exportTypeEnum = ExportFormatEnum.CSVEXPORT.name();

	File testFile = new File("target/Document_Export_testFile.csv");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	TermEntrySearchRequest searchRequest = createSearchRequest("en-US", "de-DE", 2L);
	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());
	searchRequest.setStatuses(statuses);
	searchRequest.setIncludeSource(true);

	TmgrSearchFilter filter = createFilterFromRequest(searchRequest);

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory
		.getInstance(ExportFormatEnum.valueOf(exportTypeEnum));
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocument(searchRequest, filter, exportTypeEnum,
		new ExportAdapter());

	Assert.assertNotNull(exportInfo);
	Assert.assertEquals(ExportFormatEnum.CSVEXPORT.name(), exportInfo.getExportFormat());
	Assert.assertEquals(1, exportInfo.getTotalTermEntriesExported());
	Assert.assertEquals(2, exportInfo.getTotalTermsExported());
	Assert.assertEquals(exportTypeEnum, exportInfo.getExportFormat());
    }

    @Test
    public void exportDocumentTABStatusesExcludeSourceTest() throws Exception {
	addTempTermEntries();
	String exportTypeEnum = ExportFormatEnum.TAB.name();

	File testFile = new File("target/Document_Export_testFile.tab");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	TermEntrySearchRequest searchRequest = createSearchRequest("en-US", "de-DE", 2L);
	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());
	searchRequest.setStatuses(statuses);
	searchRequest.setIncludeSource(false);

	TmgrSearchFilter filter = createFilterFromRequest(searchRequest);

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory
		.getInstance(ExportFormatEnum.valueOf(exportTypeEnum));
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocument(searchRequest, filter, exportTypeEnum,
		new ExportAdapter());

	Assert.assertNotNull(exportInfo);
	Assert.assertEquals(ExportFormatEnum.TAB.name(), exportInfo.getExportFormat());
	Assert.assertEquals(1, exportInfo.getTotalTermEntriesExported());
	Assert.assertEquals(1, exportInfo.getTotalTermsExported());
	Assert.assertEquals(exportTypeEnum, exportInfo.getExportFormat());
    }

    @Test
    public void exportDocumentTBXTest() throws Exception {
	addTempTermEntries();

	String exportTypeEnum = ExportFormatEnum.TBX.name();

	File testFile = new File("target/Document_Export_testFile.tbx");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	String sourceLangugae = "en-US";
	String targetLanguage = "de-DE";
	TermEntrySearchRequest searchRequest = createSearchRequest(sourceLangugae, targetLanguage, 2L);
	String[] languages = { sourceLangugae, targetLanguage };
	searchRequest.setLanguagesToExport(Arrays.asList(languages));

	TmgrSearchFilter filter = createFilterFromRequest(searchRequest);

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory
		.getInstance(ExportFormatEnum.valueOf(exportTypeEnum));
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocument(searchRequest, filter, exportTypeEnum,
		new ExportAdapter());

	Assert.assertNotNull(exportInfo);
	Assert.assertEquals(ExportFormatEnum.TBX.name(), exportInfo.getExportFormat());
	Assert.assertEquals(5, exportInfo.getTotalTermEntriesExported());
	Assert.assertEquals(8, exportInfo.getTotalTermsExported());
	Assert.assertEquals(ExportFormatEnum.TBX.name(), exportInfo.getExportFormat());
    }

    @Ignore
    @Test
    public void testRenameTermDescriptions() {
	Long projectId = 1L;

	String newAttributeName = "custom";

	String oldAttributeName = DESCRIPTION_DEFINITION;

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);

	assertNotNull(project);

	List<Attribute> attributes = project.getAttributes();

	assertTrue(CollectionUtils.isNotEmpty(attributes));

	List<Attribute> projectAttributes = new ArrayList<Attribute>();

	for (Attribute attribute : attributes) {
	    if (oldAttributeName.equals(attribute.getName())) {
		attribute.setRenameValue(newAttributeName);
		projectAttributes.add(attribute);
	    }
	}

	String termEntryId = AbstractSolrGlossaryTest.TERM_ENTRY_ID_01;

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, projectId);

	Set<Description> descriptions = termEntry.getDescriptions();

	assertTrue(CollectionUtils.isNotEmpty(descriptions));

	getTermEntryService().renameTermDescriptions(projectId, projectAttributes);

	termEntry = getTermEntryService().findTermEntryById(termEntryId, projectId);
	assertEquals(Action.EDITED, termEntry.getAction());

	descriptions = termEntry.getDescriptions();

	assertTrue(CollectionUtils.isNotEmpty(descriptions));

	boolean flag = false;
	for (Description termDescription : descriptions) {
	    String type = termDescription.getType();
	    if (newAttributeName.equals(type)) {
		flag = true;
		break;
	    }
	}

	assertTrue(flag);
    }

    @Test
    public void updateTermEntryLatestChangeAfterLookup() {

	TermEntryService service = getTermEntryService();

	Term term = createTermForLookup();
	assertEquals("house", term.getName());
	assertEquals("en-US", term.getLanguageId());

	TermEntry targetTermEntry = service.findTermEntryById(TERM_ENTRY_ID_01, PROJECT_ID);
	assertNotNull(targetTermEntry);
	assertEquals(2, targetTermEntry.getLanguageTerms().size());
	assertNotNull(targetTermEntry.getLanguageTerms().get("en-US"));
	assertNotNull(targetTermEntry.getLanguageTerms().get("de-DE"));

	TmgrSearchFilter filter = createFilter(term);
	Page<TermEntry> page = service.searchTermEntries(filter);

	List<TermEntry> matchedTermEntries = page.getResults();
	assertEquals(1, matchedTermEntries.size());
	TermEntry matchedTermEntry = matchedTermEntries.get(0);
	assertEquals(matchedTermEntry.getUuId(), targetTermEntry.getUuId());

	Set<Term> terms = matchedTermEntry.getLanguageTerms().get("en-US");

	assertEquals(1, matchedTermEntry.getLanguageTerms().size());

	assertNotNull(terms);
	assertEquals(2, terms.size());
	assertNull(matchedTermEntry.getLanguageTerms().get("de-DE"));

	service.updateTermEntriesLatestChanges(matchedTermEntries);

	Iterator<Term> termIterator = terms.iterator();

	while (termIterator.hasNext()) {
	    Term t = termIterator.next();
	    assertNotNull(t.getUserLatestChange());
	    assertEquals(TmUserProfile.getCurrentUserProfile().getUserProfileId(), t.getUserLatestChange());
	}
    }

    private void addTempTermEntries() throws TmException {
	List<TermEntry> termEntries = new ArrayList<TermEntry>();

	Description description = new Description(DESCRIPTION_DEFINITION, "term entry 1 description value");

	Set<Description> descriptions = new HashSet<Description>();
	descriptions.add(description);

	TermEntry termEntry1 = new TermEntry();
	termEntry1.setUuId(UUID.randomUUID().toString());
	termEntry1.setProjectId(2L);
	termEntry1.addTerm(createTerm("en-US", "1 eng term", ItemStatusTypeHolder.PROCESSED.getName(), descriptions));
	termEntry1.addTerm(
		createTerm("en-US", "1 eng synonym term", ItemStatusTypeHolder.PROCESSED.getName(), descriptions));
	termEntry1.addTerm(
		createTerm("de-DE", "1 de term", ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), descriptions));
	termEntries.add(termEntry1);

	termEntry1.addDescription(description);

	TermEntry termEntry2 = new TermEntry();
	termEntry2.setUuId(UUID.randomUUID().toString());
	termEntry2.setProjectId(2L);
	termEntry2.addTerm(createTerm("en-US", "2 eng term", ItemStatusTypeHolder.BLACKLISTED.getName(), descriptions));
	termEntry2.addTerm(createTerm("de-DE", "2 de term", ItemStatusTypeHolder.PROCESSED.getName(), descriptions));
	termEntries.add(termEntry2);

	description = new Description(DESCRIPTION_DEFINITION, "term entry 2 description value");
	termEntry2.addDescription(description);

	TermEntry termEntry3 = new TermEntry();
	termEntry3.setUuId(UUID.randomUUID().toString());
	termEntry3.setProjectId(2L);
	termEntry3.addTerm(
		createTerm("en-US", "3 eng term", ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), descriptions));
	termEntry3.addTerm(createTerm("de-DE", "3 de term", ItemStatusTypeHolder.PROCESSED.getName(), descriptions));
	termEntries.add(termEntry3);

	description = new Description(DESCRIPTION_DEFINITION, "term entry 3 description value");
	termEntry3.addDescription(description);

	TermEntry termEntry4 = new TermEntry();
	termEntry4.setUuId(UUID.randomUUID().toString());
	termEntry4.setProjectId(2L);
	termEntry4.addTerm(createTerm("en-US", "4 eng term", ItemStatusTypeHolder.WAITING.getName(), descriptions));
	termEntry4.addTerm(createTerm("de-DE", "4 de term", ItemStatusTypeHolder.BLACKLISTED.getName(), descriptions));
	termEntries.add(termEntry4);

	TermEntry termEntry5 = new TermEntry();
	termEntry5.setUuId(UUID.randomUUID().toString());
	termEntry5.setProjectId(2L);
	termEntry5.addTerm(createTerm("en-US", "5 eng term", ItemStatusTypeHolder.BLACKLISTED.getName(), descriptions));
	termEntry5.addTerm(
		createTerm("de-DE", "5 de term", ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), descriptions));
	termEntry5.addTerm(
		createTerm("de-DE", "5 de synonym term", ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), descriptions));
	termEntries.add(termEntry5);

	getRegularConnector().getTmgrUpdater().save(termEntries);
    }

    private TmgrSearchFilter createFilter(Term term) {

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(term.getProjectId());
	filter.setSourceLanguage(term.getLanguageId());

	filter.addLanguageResultField(true, SYNONYM_NUMBER, term.getLanguageId());

	List<String> statuses = Arrays.asList(ItemStatusTypeHolder.PROCESSED.getName(),
		ItemStatusTypeHolder.BLACKLISTED.getName(), ItemStatusTypeHolder.WAITING.getName(),
		ItemStatusTypeHolder.ON_HOLD.getName());
	filter.setStatuses(statuses);

	StringBuilder builder = new StringBuilder();
	builder.append(term.getName());

	filter.setTextFilter(new TextFilter(builder.toString(), true, false));
	filter.setPageable(new TmgrPageRequest(0, 20, null));

	return filter;
    }

    private TmgrSearchFilter createFilterFromRequest(TermEntrySearchRequest searchRequest) {

	ExportDocumentTaskHandler exportDocumentTaskHandler = findSpecificTaskHandler("export tbx");

	return exportDocumentTaskHandler.createFilterFromRequest(searchRequest, new PagedListInfo());
    }

    private TermEntrySearchRequest createSearchRequest(String sourceLanguage, String targetLanguage, long projectId) {
	TermEntrySearchRequest searchRequest = new TermEntrySearchRequest();
	searchRequest.setSourceLocale(sourceLanguage);
	List<String> targetLocales = new ArrayList<String>();
	targetLocales.add(targetLanguage);
	searchRequest.setTargetLocales(targetLocales);
	searchRequest.setProjectId(projectId);
	searchRequest.setStatuses(null);
	return searchRequest;
    }

    private Term createTerm(String languageId, String name, String status, Set<Description> descriptions) {
	Term term = new Term();
	term.setUuId(UUID.randomUUID().toString());
	term.setLanguageId(languageId);
	term.setName(name);
	term.setStatus(status);
	term.setDescriptions(descriptions);

	term.setUserCreated("user");
	term.setUserModified("user");
	term.setDateSubmitted(new Date().getTime());
	term.setDateCreated(new Date().getTime());
	term.setDateModified(new Date().getTime());

	return term;
    }

    @SuppressWarnings("unchecked")
    private <T> T findSpecificTaskHandler(String taskHandlerKey) {
	Map<EntityType, Map<String, SystemManualTaskHandler>> manualTaskHandlerMap = getDefaultSystemManualTaskManager()
		.getManualTaskHandlerMap();

	for (EntityType type : manualTaskHandlerMap.keySet()) {
	    Map<String, SystemManualTaskHandler> taskHandlerMap = manualTaskHandlerMap.get(type);

	    T taskHandler = (T) taskHandlerMap.get(taskHandlerKey);

	    if (Objects.isNull(taskHandler)) {
		continue;
	    }

	    return taskHandler;
	}

	throw new NullPointerException("Specific Task Handler not found");
    }

    private DefaultSystemManualTaskManager getDefaultSystemManualTaskManager() {
	return _defaultSystemManualTaskManager;
    }
}

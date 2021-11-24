package org.gs4tr.termmanager.service.mocking;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation3.io.CsvReader.ReturnCode;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ExportLanguageCriteriaEnum;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.dto.ExportTermModel;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.StatisticsService;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.impl.ExportAdapter;
import org.gs4tr.termmanager.service.impl.ExportNotificationCallback;
import org.gs4tr.termmanager.service.impl.Messages;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.service.xls.XlsReader;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jetbrains.exodus.entitystore.EntityId;

@TestSuite("service")
public class TermEntryServiceTest extends AbstractServiceTest {

    private static final String DE_DE = "de-DE";

    private static final String EN_US = "en-US";

    private static final String FR_FR = "fr-FR";

    private static final String OPERATION = "operation";

    private static final String PROCESSED = "PROCESSED";

    private static final long PROJECT_ID = 1L;

    private static final String TICKET = "ticket";

    @Autowired
    private ExportNotificationCallback _exportNotificationCallback;

    @Autowired
    private OutputStream _outputStream;

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private StatisticsService _statisticsService;

    /* StatisticsInfo should not be created for TermEntry note */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("termEntry")
    public void addTermEntryAttributeStatisticsInfoTest() throws TmException {
	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	List<TranslationUnit> translationUnits = getModelObject("translationUnitsTermEntryNote", List.class);

	when(getGlossaryBrowser().findById(anyString(), any(Long.class))).thenReturn(termEntry);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermEntryService().updateTermEntries(translationUnits, EN_US, PROJECT_ID, Action.EDITED);

	ArgumentCaptor<Long> argumentProjectId = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<EntityId> argumentEntityId = ArgumentCaptor.forClass(EntityId.class);

	ArgumentCaptor<TransactionalUnit> argumentTU = ArgumentCaptor.forClass(TransactionalUnit.class);

	verify(getTransactionLogHandler(), times(1)).appendAndLink(anyLong(), any(EntityId.class),
		argumentTU.capture());

	verify(getTransactionLogHandler(), times(1)).finishAppending(argumentProjectId.capture(),
		argumentEntityId.capture());

	Set<StatisticsInfo> statisticsInfos = argumentTU.getValue().getStatisticsInfo();

	assertEquals(0, statisticsInfos.size());
    }

    /* TERII-3555: Cannot delete term entry attributes. */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("termEntry")
    public void deleteTermEntryDescriptionsByTypeTest() throws TmException {
	List<TermEntry> termEntries = getModelObject("termEntries2", List.class);
	termEntries.forEach(t -> t.setProjectId(PROJECT_ID));

	List<String> languageIds = getModelObject("languageIds", List.class);

	when(getGlossaryBrowser().browse(any(TmgrSearchFilter.class))).thenReturn(termEntries);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermEntryService().deleteTermEntryDescriptionsByType(Arrays.asList("definition"), PROJECT_ID, languageIds);

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);

	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	int editedTermEntryCount = 0;

	TransactionalUnit unit = argument.getValue();

	for (TermEntry termEntry : unit.getTermEntries()) {
	    if (Action.EDITED == termEntry.getAction()) {
		editedTermEntryCount++;
	    }
	    Set<Description> descriptions = termEntry.getDescriptions();
	    assertTrue(CollectionUtils.isEmpty(descriptions));
	}
	// Just one term entry has desctioptions that should be deleted
	assertEquals(1, editedTermEntryCount);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("termEntry")
    public void deleteTermEntryDescriptionsVerifyDateModifiedTest() throws TmException {
	List<TermEntry> termEntries = singletonList(getModelObject("termEntry04", TermEntry.class));
	termEntries.forEach(t -> t.setProjectId(PROJECT_ID));

	List<String> languageIds = getModelObject("languageIds", List.class);

	when(getGlossaryBrowser().browse(any(TmgrSearchFilter.class))).thenReturn(termEntries);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermEntryService().deleteTermEntryDescriptionsByType(singletonList("definition"), PROJECT_ID, languageIds);

	ArgumentCaptor<TransactionalUnit> transactionalUnitCaptor = ArgumentCaptor.forClass(TransactionalUnit.class);
	ArgumentCaptor<Long> projectIdCaptor = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<Date> dateModifiedCaptor = ArgumentCaptor.forClass(Date.class);

	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		transactionalUnitCaptor.capture());

	verify(getProjectDetailDAO(), times(1)).updateDateModifiedByProjectId(projectIdCaptor.capture(),
		dateModifiedCaptor.capture());

	TransactionalUnit unit = transactionalUnitCaptor.getValue();

	assertEquals(PROJECT_ID, projectIdCaptor.getValue().longValue());
	assertEquals(unit.getTermEntries().get(0).getDateModified().longValue(),
		dateModifiedCaptor.getValue().getTime());
    }

    /* TERII-4883:UI Cancel export don't stop actual export on server */
    @Test
    @TestCase("termEntry")
    public void exportCSVdocumentCancelTest() {
	TermEntrySearchRequest searchRequest = getModelObject("searchRequest", TermEntrySearchRequest.class);

	String threadNameUuId = UUID.randomUUID().toString();

	TmgrSearchFilter tmgrSearchFilter = new TmgrSearchFilter();

	List<TermEntry> termentries = getModelObject("termentries", List.class);
	Page<TermEntry> page = new Page<>(0, 0, 1, termentries);

	when(getTermEntryService().searchTermEntries(tmgrSearchFilter)).thenReturn(page);

	when(getExportNotificationCallback().getThreadName()).thenReturn(threadNameUuId);

	getTermEntryExporter().requestStopExport(threadNameUuId);

	File folderForTempFiles = new File(StringConstants.TEMP_DIR);
	int numOfTempFilesBefore = folderForTempFiles.listFiles().length;

	getTermEntryService().exportDocument(searchRequest, tmgrSearchFilter, "CSVEXPORT",
		getExportNotificationCallback());

	int numOfTempFilesAfter = folderForTempFiles.listFiles().length;

	ArgumentCaptor<ExportInfo> eventCaptor = ArgumentCaptor.forClass(ExportInfo.class);

	verify(getExportNotificationCallback(), times(1)).notifyProcessingFinished(eventCaptor.capture());

	List<ExportInfo> eventList = eventCaptor.getAllValues();

	assertTrue(eventList.get(0).isProcessingCanceled());

	/*
	 * All temporary files created in export should be removed after cancel
	 * operation
	 */

	Assert.assertEquals(numOfTempFilesBefore, numOfTempFilesAfter);

    }

    @Test
    @TestCase("termEntry")
    public void exportDocumentTest_01() throws Exception {

	TermEntrySearchRequest searchRequest = getModelObject("searchRequest", TermEntrySearchRequest.class);
	searchRequest.setDateModifiedFrom(new Date(0));
	searchRequest.setStatusRequestList(searchRequest.getStatuses());

	String exportTypeEnum = "TBX";

	File testFile = new File("target/Document_Export_testFile.tbx");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termentries", List.class);
	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termentries);

	when(getGlossarySearcher().concordanceSearch(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory
		.getInstance(ExportFormatEnum.valueOf(exportTypeEnum));
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	TmgrSearchFilter filter = new TmgrSearchFilter();

	ExportInfo exportInfo = getTermEntryService().exportDocument(searchRequest, filter, exportTypeEnum,
		new ExportAdapter());

	verify(getGlossarySearcher(), times(2)).concordanceSearch(any(TmgrSearchFilter.class));

	Assert.assertNotNull(exportInfo);

	Assert.assertEquals(2, exportInfo.getTotalTermEntriesExported());
	Assert.assertEquals(5, exportInfo.getTotalTermsExported());
	Assert.assertEquals("TBX", exportInfo.getExportFormat());
	Assert.assertTrue(exportInfo.getExportTermType().contains(PROCESSED)
		&& exportInfo.getExportTermType().contains("BLACKLISTED"));
	Assert.assertEquals(ExportLanguageCriteriaEnum.BILINGUAL, exportInfo.getLanguageCriteriaEnum());
    }

    /*
     * TERII-3590: "XLS Export | Selecting an attribute in the export filter without
     * a value should export all entries that have this attribute." Try to reproduce
     * and verify fix thought this test case.
     */
    @SuppressWarnings("unchecked")
    @Test
    @TestCase("termEntry")
    public void exportDocumentTest_02() throws Exception {

	List<TermEntry> termEntries = getModelObject("termEntries2", List.class);

	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termEntries);

	TermEntrySearchRequest searchRequest = getModelObject("searchRequest2", TermEntrySearchRequest.class);

	final String exportTypeEnum = "XLS";

	final String path = "target/Document_Export_testFile.xls";

	final List<String> termEntryIds = Arrays.asList("474e93ae-7264-4088-9d54-termentry003",
		"474e93ae-7264-4088-9d54-termentry004");

	when(getGlossarySearcher().concordanceSearch(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	ExportDocumentFactory exporter = ExportDocumentFactory.getInstance(ExportFormatEnum.valueOf(exportTypeEnum));
	exporter.open(new FileOutputStream(createTestFile(path)), searchRequest, null);
	ExportInfo exportInfo = getTermEntryService().exportDocument(searchRequest, new TmgrSearchFilter(),
		exportTypeEnum, new ExportAdapter());

	exporter.close();

	MutableInt termEntriesCount = new MutableInt(0);
	InputStream input = new FileInputStream(exportInfo.getTempFile());
	XlsReader xlsReader = new XlsReader(input, true, true);
	ReturnCode returnedCode = ReturnCode.FIELD;
	List<String> row = new ArrayList<String>();
	while (!ReturnCode.FILE_END.equals(returnedCode)) {
	    returnedCode = xlsReader.readNextRecord(row);
	    assertTrue(CollectionUtils.isNotEmpty(row));
	    assertTrue(termEntryIds.contains(row.get(0)));
	    termEntriesCount.increment();
	}

	assertEquals(2, termEntriesCount.intValue());
	assertEquals(2, exportInfo.getTotalTermEntriesExported());
	assertEquals(4, exportInfo.getTotalTermsExported());

	verify(getGlossarySearcher(), times(2)).concordanceSearch(any(TmgrSearchFilter.class));
    }

    @Test
    @TestCase("termEntryRestExport")
    public void exportDocumentWSRestUserCreatedTermsSharePendingTermsDisabledTest() throws Exception {

	List<TermEntry> termEntries = getModelObject("termEntries", List.class);

	/*
	 * Set user that created terms to be the same as user that exported terms
	 */

	termEntries.stream()
		.forEach(termEntry -> termEntry.ggetAllTerms().stream().forEach(term -> term.setUserCreated("marko")));

	TermEntrySearchRequest searchRequest = getModelObject("searchRequest", TermEntrySearchRequest.class);

	ExportFormatEnum exportEnum = ExportFormatEnum.JSON;

	ExportDocumentFactory exporter = ExportDocumentFactory.getInstance(exportEnum);

	exporter.setOutputStream(getOutputStream());

	exporter.open(getOutputStream(), searchRequest, null);

	getTermEntryService().exportDocumentWS(termEntries, searchRequest, null, exporter);

	ArgumentCaptor<byte[]> arguments = ArgumentCaptor.forClass(byte[].class);
	verify(getOutputStream(), times(8)).write(arguments.capture());

	List<byte[]> responseTerms = arguments.getAllValues();

	/*
	 * Terms are marked as updated and not created because term date created and
	 * date modified are not the same
	 */

	String forbiddenTermCountString = new String(responseTerms.get(0));

	JsonNode resultNode = JsonUtils.readValue(forbiddenTermCountString, JsonNode.class);

	int forbiddenTermsCount = resultNode.get("forbiddenTermsCount").asInt();

	assertEquals(1, forbiddenTermsCount);

	/* Target term is pending, and user is same */
	ExportTermModel termModel = JsonUtils.readValue(new String(responseTerms.get(2)), ExportTermModel.class);
	assertEquals("U", termModel.getOperation());

	/* Source term is pending, and user is same */
	termModel = JsonUtils.readValue(new String(responseTerms.get(3)), ExportTermModel.class);
	assertEquals("U", termModel.getOperation());

	/*
	 * It's updated because time created and time modified are not the same. Source
	 * and target terms are approved.
	 */
	termModel = JsonUtils.readValue(new String(responseTerms.get(4)), ExportTermModel.class);
	assertEquals("U", termModel.getOperation());

	/*
	 * Source and target are On Hold. User that created on hold terms can export
	 * them.
	 */
	termModel = JsonUtils.readValue(new String(responseTerms.get(5)), ExportTermModel.class);
	assertEquals("U", termModel.getOperation());

	/* Source and target are Blacklisted */
	termModel = JsonUtils.readValue(new String(responseTerms.get(6)), ExportTermModel.class);
	assertEquals("D", termModel.getOperation());

	/*
	 * It's created because time created and time modified are the same and source
	 * and target are Approved
	 */
	termModel = JsonUtils.readValue(new String(responseTerms.get(7)), ExportTermModel.class);
	assertEquals("C", termModel.getOperation());
    }

    @Test
    @TestCase("termEntryRestExport")
    public void exportDocumentWSRestUserNotCreatedTermsSharePendingTermsDisabledTest() throws Exception {

	List<TermEntry> termEntries = getModelObject("termEntries", List.class);

	TermEntrySearchRequest searchRequest = getModelObject("searchRequest", TermEntrySearchRequest.class);

	ExportFormatEnum exportEnum = ExportFormatEnum.JSON;

	ExportDocumentFactory exporter = ExportDocumentFactory.getInstance(exportEnum);

	exporter.setOutputStream(getOutputStream());

	exporter.open(getOutputStream(), searchRequest, null);

	getTermEntryService().exportDocumentWS(termEntries, searchRequest, null, exporter);

	ArgumentCaptor<byte[]> arguments = ArgumentCaptor.forClass(byte[].class);
	verify(getOutputStream(), times(8)).write(arguments.capture());

	List<byte[]> responseTerms = arguments.getAllValues();

	/*
	 * Terms are marked as updated and not created because term date created and
	 * date modified are not the same
	 */

	String forbiddenTermCountString = new String(responseTerms.get(0));

	JsonNode resultNode = JsonUtils.readValue(forbiddenTermCountString, JsonNode.class);

	int forbiddenTermsCount = resultNode.get("forbiddenTermsCount").asInt();

	assertEquals(1, forbiddenTermsCount);

	/* Target term is pending */
	ExportTermModel termModel = JsonUtils.readValue(new String(responseTerms.get(2)), ExportTermModel.class);
	assertEquals("D", termModel.getOperation());

	/* Source term is pending */
	termModel = JsonUtils.readValue(new String(responseTerms.get(3)), ExportTermModel.class);
	assertEquals("D", termModel.getOperation());

	/*
	 * It's updated because time created and time modified are not the same. Source
	 * and target terms are approved.
	 */
	termModel = JsonUtils.readValue(new String(responseTerms.get(4)), ExportTermModel.class);
	assertEquals("U", termModel.getOperation());

	/* Source and target are On Hold */
	termModel = JsonUtils.readValue(new String(responseTerms.get(5)), ExportTermModel.class);
	assertEquals("D", termModel.getOperation());

	/* Source and target are Blacklisted */
	termModel = JsonUtils.readValue(new String(responseTerms.get(6)), ExportTermModel.class);
	assertEquals("D", termModel.getOperation());

	/*
	 * It's created because time created and time modified are the same and source
	 * and target are Approved
	 */
	termModel = JsonUtils.readValue(new String(responseTerms.get(7)), ExportTermModel.class);
	assertEquals("C", termModel.getOperation());
    }

    @Test
    @TestCase("termEntryRestExport")
    public void exportDocumentWSRestUserNotCreatedTermsSharePendingTermsEnabledTest() throws Exception {

	List<TermEntry> termEntries = getModelObject("termEntries", List.class);

	TermEntrySearchRequest searchRequest = getModelObject("searchRequest", TermEntrySearchRequest.class);

	/* Set share pending terms */
	searchRequest.setSharePendingTerms(true);

	ExportFormatEnum exportEnum = ExportFormatEnum.JSON;

	ExportDocumentFactory exporter = ExportDocumentFactory.getInstance(exportEnum);

	exporter.setOutputStream(getOutputStream());

	exporter.open(getOutputStream(), searchRequest, null);

	getTermEntryService().exportDocumentWS(termEntries, searchRequest, null, exporter);

	ArgumentCaptor<byte[]> arguments = ArgumentCaptor.forClass(byte[].class);
	verify(getOutputStream(), times(8)).write(arguments.capture());

	List<byte[]> responseTerms = arguments.getAllValues();

	/*
	 * Terms are marked as updated and not created because term date created and
	 * date modified are not the same
	 */

	String forbiddenTermCountString = new String(responseTerms.get(0));

	JsonNode resultNode = JsonUtils.readValue(forbiddenTermCountString, JsonNode.class);

	int forbiddenTermsCount = resultNode.get("forbiddenTermsCount").asInt();

	assertEquals(1, forbiddenTermsCount);

	/* Target term is pending, and sharing pending terms is enabled */
	ExportTermModel termModel = JsonUtils.readValue(new String(responseTerms.get(2)), ExportTermModel.class);
	assertEquals("U", termModel.getOperation());

	/* Source term is pending, and sharing pending terms is enabled */
	termModel = JsonUtils.readValue(new String(responseTerms.get(3)), ExportTermModel.class);
	assertEquals("U", termModel.getOperation());

	/*
	 * It's updated because time created and time modified are not the same. Source
	 * and target terms are approved.
	 */
	termModel = JsonUtils.readValue(new String(responseTerms.get(4)), ExportTermModel.class);
	assertEquals("U", termModel.getOperation());

	/* Source and target are On Hold */
	termModel = JsonUtils.readValue(new String(responseTerms.get(5)), ExportTermModel.class);
	assertEquals("D", termModel.getOperation());

	/* Source and target are Blacklisted */
	termModel = JsonUtils.readValue(new String(responseTerms.get(6)), ExportTermModel.class);
	assertEquals("D", termModel.getOperation());

	/*
	 * It's created because time created and time modified are the same and source
	 * and target are Approved
	 */
	termModel = JsonUtils.readValue(new String(responseTerms.get(7)), ExportTermModel.class);
	assertEquals("C", termModel.getOperation());
    }

    @Test
    @TestCase("termEntry")
    public void exportDocumentWS_CSVTest_01() throws Exception {

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termentries", List.class);
	TermEntrySearchRequest searchRequest = getModelObject("searchRequest", TermEntrySearchRequest.class);
	searchRequest.setDateModifiedFrom(new Date(0));

	ExportFormatEnum exportTypeEnum = ExportFormatEnum.CSVEXPORT;

	File testFile = new File("target/Csv-testFile.csv");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory.getInstance(exportTypeEnum);
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocumentWS(termentries, searchRequest, new ExportAdapter(),
		exportFactory);

	Assert.assertNotNull(exportInfo);

	FileReader fileReader = new FileReader(testFile);
	BufferedReader reader = new BufferedReader(fileReader);

	String line = reader.readLine();
	String[] terms = line.split(",");

	Assert.assertEquals(2, terms.length);
	Assert.assertEquals("term test 1 source", terms[0]);
	Assert.assertEquals("term test 2 target", terms[1]);

	line = reader.readLine();
	terms = line.split(",");

	Assert.assertEquals(2, terms.length);
	Assert.assertEquals("term test 3 source", terms[0]);
	Assert.assertEquals("term test 4 target", terms[1]);

	line = reader.readLine();
	terms = line.split(",");

	Assert.assertEquals(2, terms.length);
	Assert.assertEquals("term test 3 source", terms[0]);
	Assert.assertEquals("term test 5 target", terms[1]);

	line = reader.readLine();
	Assert.assertNull(line);

	reader.close();
    }

    @Test
    @TestCase("termEntry")
    public void exportDocumentWS_JSONTest_01() throws Exception {

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termentries", List.class);
	TermEntrySearchRequest searchRequest = getModelObject("searchRequest", TermEntrySearchRequest.class);
	searchRequest.setDateModifiedFrom(new Date(0));

	ExportFormatEnum exportTypeEnum = ExportFormatEnum.JSON;

	File testFile = new File("target/JsonExport-testFile.txt");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory.getInstance(exportTypeEnum);
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocumentWS(termentries, searchRequest, new ExportAdapter(),
		exportFactory);

	FileReader fileReader = new FileReader(testFile);
	BufferedReader reader = new BufferedReader(fileReader);

	String firstLine = reader.readLine();
	ObjectNode jsonNode = JsonUtils.readValue(firstLine, ObjectNode.class);
	Integer forbiddenTermsCount = jsonNode.get("forbiddenTermsCount").asInt();

	assertEquals(0, forbiddenTermsCount.intValue());

	String line = reader.readLine();
	jsonNode = JsonUtils.readValue(line, ObjectNode.class);

	String ticketStr = jsonNode.get(TICKET).asText();
	assertJsonResponse(jsonNode, ticketStr);

	line = reader.readLine();
	jsonNode = JsonUtils.readValue(line, ObjectNode.class);
	ticketStr = jsonNode.get(TICKET).asText();
	assertJsonResponse(jsonNode, ticketStr);

	line = reader.readLine();
	jsonNode = JsonUtils.readValue(line, ObjectNode.class);
	ticketStr = jsonNode.get(TICKET).asText();
	assertJsonResponse(jsonNode, ticketStr);

	line = reader.readLine();

	assertNull(line);

	reader.close();
	assertNotNull(exportInfo);

	FileUtils.forceDelete(testFile);
    }

    @Test
    @TestCase("termEntry")
    @SuppressWarnings("unchecked")
    public void exportDocumentWS_JSONTest_02() throws Exception {
	List<TermEntry> termEntries = getModelObject("termEntries1", List.class);

	TermEntrySearchRequest searchRequest = getModelObject("searchRequest1", TermEntrySearchRequest.class);

	ExportFormatEnum exportEnum = ExportFormatEnum.JSON_V2;

	File testFile = new File("target/JsonExport-testFile.txt");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	OutputStream outputStream = new FileOutputStream(testFile);

	ExportDocumentFactory exporter = ExportDocumentFactory.getInstance(exportEnum);

	exporter.open(outputStream, searchRequest, null);

	getTermEntryService().exportDocumentWS(termEntries, searchRequest, null, exporter);

	exporter.close();

	try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
	    String firstLine = reader.readLine();
	    Class<ObjectNode> clazz = ObjectNode.class;
	    ObjectNode jsonNode = JsonUtils.readValue(firstLine, clazz);
	    assertNotNull(jsonNode);

	    JsonNode jsonTermEntries = jsonNode.get("termEntries");
	    assertNotNull(jsonTermEntries);
	    assertEquals(1, jsonTermEntries.size());
	    assertNotNull(jsonNode.get("time"));

	    for (JsonNode termEntry : jsonTermEntries) {
		assertNotNull(termEntry);
		assertNotNull(termEntry.get("creationDate"));
		assertNotNull(termEntry.get("creationUser"));
		assertNotNull(termEntry.get("modificationDate"));
		assertNotNull(termEntry.get("modificationUser"));
		assertNotNull(termEntry.get("descriptions"));

		JsonNode returnCode = jsonNode.get("returnCode");
		assertEquals(0, Integer.parseInt(returnCode.toString()));

		JsonNode langSet = termEntry.get("langList");
		assertNotNull(langSet);
		assertEquals(2, langSet.size());

		Iterator<JsonNode> iterator = langSet.iterator();
		while (iterator.hasNext()) {
		    JsonNode language = iterator.next();
		    assertNotNull(language);
		    JsonNode locale = language.get("locale");
		    assertNotNull(locale);
		    JsonNode terms = language.get("terms");
		    validateTerms(terms, locale);
		}
	    }
	    reader.close();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    @Test
    @TestCase("termEntry")
    public void exportDocumentWS_TABTest_01() throws Exception {

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termentries", List.class);
	TermEntrySearchRequest searchRequest = getModelObject("searchRequest", TermEntrySearchRequest.class);
	searchRequest.setDateModifiedFrom(new Date(0));

	ExportFormatEnum exportTypeEnum = ExportFormatEnum.TABSYNC;

	File testFile = new File("target/Tab-testFile.txt");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory.getInstance(exportTypeEnum);
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocumentWS(termentries, searchRequest, new ExportAdapter(),
		exportFactory);

	Assert.assertNotNull(exportInfo);

	FileReader fileReader = new FileReader(testFile);
	BufferedReader reader = new BufferedReader(fileReader);

	String line = reader.readLine();
	String[] terms = line.split("\\t");

	Assert.assertEquals(2, terms.length);
	Assert.assertEquals("term test 1 source", terms[0]);
	Assert.assertEquals("term test 2 target", terms[1]);

	line = reader.readLine();
	terms = line.split("\\t");

	Assert.assertEquals(2, terms.length);
	Assert.assertEquals("term test 3 source", terms[0]);
	Assert.assertEquals("term test 4 target", terms[1]);

	line = reader.readLine();
	terms = line.split("\\t");

	Assert.assertEquals(2, terms.length);
	Assert.assertEquals("term test 3 source", terms[0]);
	Assert.assertEquals("term test 5 target", terms[1]);

	line = reader.readLine();
	Assert.assertNull(line);

	reader.close();
    }

    @Test
    @TestCase("termEntry")
    public void exportDocumentWS_TBXTest_01() throws Exception {

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termentries", List.class);
	TermEntrySearchRequest searchRequest = getModelObject("searchRequest", TermEntrySearchRequest.class);
	searchRequest.setDateModifiedFrom(new Date(0));

	ExportFormatEnum exportTypeEnum = ExportFormatEnum.TBX;

	File testFile = new File("target/Tbx-testFile.tbx");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory.getInstance(exportTypeEnum);
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocumentWS(termentries, searchRequest, new ExportAdapter(),
		exportFactory);

	Assert.assertNotNull(exportInfo);
    }

    @Test
    @TestCase("termEntry")
    public void exportDocumentWithPendingApprovalTermWSCsvSyncTest() throws Exception {

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termentries5", List.class);
	TermEntrySearchRequest searchRequest = getModelObject("searchRequest4", TermEntrySearchRequest.class);

	ExportFormatEnum exportTypeEnum = ExportFormatEnum.CSVSYNC;

	File testFile = new File("target/Csv-pendingApproval1.csv");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory.getInstance(exportTypeEnum);
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocumentWS(termentries, searchRequest, new ExportAdapter(),
		exportFactory);

	Assert.assertNotNull(exportInfo);

	FileReader fileReader = new FileReader(testFile);
	BufferedReader reader = new BufferedReader(fileReader);

	String line = reader.readLine();
	String[] terms = line.split(",");

	Assert.assertEquals("approvedTermEnglish", terms[1]);
	Assert.assertEquals("approvedTermGermany", terms[2]);

	line = reader.readLine();
	terms = line.split(",");

	Assert.assertEquals("blacklistedTermEnglish", terms[1]);
	Assert.assertEquals("blacklistedTermGermany", terms[2]);

	line = reader.readLine();
	terms = line.split(",");

	Assert.assertEquals("pendingApprovalTermEnglish", terms[1]);
	Assert.assertEquals("pendingApprovalTermGermany", terms[2]);

	line = reader.readLine();
	Assert.assertNull(line);

	reader.close();

    }

    @Test
    @TestCase("termEntry")
    public void exportDocumentWithPendingApprovalTermWSTest() throws Exception {

	@SuppressWarnings("unchecked")
	List<TermEntry> termentries = getModelObject("termentries5", List.class);
	TermEntrySearchRequest searchRequest = getModelObject("searchRequest4", TermEntrySearchRequest.class);

	ExportFormatEnum exportTypeEnum = ExportFormatEnum.CSVEXPORT;

	File testFile = new File("target/Csv-pendingApproval.csv");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory.getInstance(exportTypeEnum);
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	ExportInfo exportInfo = getTermEntryService().exportDocumentWS(termentries, searchRequest, new ExportAdapter(),
		exportFactory);

	Assert.assertNotNull(exportInfo);

	FileReader fileReader = new FileReader(testFile);
	BufferedReader reader = new BufferedReader(fileReader);

	String line = reader.readLine();
	String[] terms = line.split(",");

	Assert.assertEquals(2, terms.length);
	Assert.assertEquals("approvedTermEnglish", terms[0]);
	Assert.assertEquals("approvedTermGermany", terms[1]);

	line = reader.readLine();
	terms = line.split(",");

	Assert.assertEquals(2, terms.length);
	Assert.assertEquals("blacklistedTermEnglish", terms[0]);
	Assert.assertEquals("blacklistedTermGermany", terms[1]);

	line = reader.readLine();
	terms = line.split(",");

	Assert.assertEquals(2, terms.length);
	Assert.assertEquals("pendingApprovalTermEnglish", terms[0]);
	Assert.assertEquals("pendingApprovalTermGermany", terms[1]);

	line = reader.readLine();
	Assert.assertNull(line);

	reader.close();

    }

    @Test
    @TestCase("termEntry")
    public void exportForbiddenTermsTest_01() throws Exception {
	ExportFormatEnum exportTypeEnum = ExportFormatEnum.JSON;

	File testFile = new File("target/exportForbidden.json");
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();

	OutputStream tempFileOutputStream = new FileOutputStream(testFile);
	ExportDocumentFactory exportFactory = ExportDocumentFactory.getInstance(exportTypeEnum);
	TermEntrySearchRequest searchRequest = new TermEntrySearchRequest();
	searchRequest.setDateModifiedFrom(new Date(0));
	exportFactory.open(tempFileOutputStream, searchRequest, null);

	@SuppressWarnings("unchecked")
	List<Term> forbiddenTerms = getModelObject("forbiddenTerms", List.class);

	getTermEntryService().exportForbiddenTerms(forbiddenTerms, exportFactory);

	FileReader fileReader = new FileReader(testFile);
	BufferedReader reader = new BufferedReader(fileReader);

	String firstLine = reader.readLine();
	ObjectNode jsonNode = JsonUtils.readValue(firstLine, ObjectNode.class);
	Integer forbiddenTermsCount = jsonNode.get("forbiddenTermsCount").asInt();
	Assert.assertNotNull(forbiddenTermsCount);

	String line = reader.readLine();
	jsonNode = JsonUtils.readValue(line, ObjectNode.class);

	String ticketStr = jsonNode.get(TICKET).asText();
	Assert.assertEquals("export-term00000005", ticketStr);
	Assert.assertEquals(jsonNode.get(OPERATION).asText(), "C");
	Assert.assertEquals(jsonNode.get("source").asText(), "term test 5 target");
	reader.close();
    }

    /* TERII-4883:UI Cancel export don't stop actual export on server */
    @Test
    @TestCase("termEntry")
    public void exportTBXdocumentCancelTest() {
	TermEntrySearchRequest searchRequest = getModelObject("searchRequest", TermEntrySearchRequest.class);

	String threadNameUuId = UUID.randomUUID().toString();

	TmgrSearchFilter tmgrSearchFilter = new TmgrSearchFilter();

	List<TermEntry> termentries = getModelObject("termentries", List.class);
	Page<TermEntry> page = new Page<>(0, 0, 1, termentries);

	when(getTermEntryService().searchTermEntries(tmgrSearchFilter)).thenReturn(page);

	when(getExportNotificationCallback().getThreadName()).thenReturn(threadNameUuId);

	getTermEntryExporter().requestStopExport(threadNameUuId);

	File folderForTempFiles = new File(StringConstants.TEMP_DIR);
	int numOfTempFilesBefore = folderForTempFiles.listFiles().length;

	getTermEntryService().exportDocument(searchRequest, tmgrSearchFilter, "TBX", getExportNotificationCallback());

	int numOfTempFilesAfter = folderForTempFiles.listFiles().length;

	ArgumentCaptor<ExportInfo> eventCaptor = ArgumentCaptor.forClass(ExportInfo.class);

	verify(getExportNotificationCallback(), times(1)).notifyProcessingFinished(eventCaptor.capture());

	List<ExportInfo> eventList = eventCaptor.getAllValues();

	assertTrue(eventList.get(0).isProcessingCanceled());

	/*
	 * All temporary files created in export should be removed after cancel
	 * operation
	 */
	Assert.assertEquals(numOfTempFilesBefore, numOfTempFilesAfter);

    }

    @Test
    @TestCase("termEntry")
    public void exportTbxDocumentWithDescriptionFilter() throws Exception {

	// Description which will be used for filtering term entries
	Description requiredDescription = getModelObject("description3", Description.class);

	// Description with same type as requested description but with different value
	Description description = getModelObject("description4", Description.class);
	assertEquals(requiredDescription.getType(), description.getType());
	assertFalse(requiredDescription.getValue().equals(description.getValue()));

	TermEntrySearchRequest searchRequest = getModelObject("searchRequest3", TermEntrySearchRequest.class);
	assertEquals(searchRequest.getSourceLocale(), "en-US");
	assertEquals(searchRequest.getTargetLocales().get(0), "fr-FR");
	assertTrue(searchRequest.getDescriptions().contains(requiredDescription));

	List<TermEntry> termEntries = getModelObject("termEntries4", List.class);
	assertEquals(4, termEntries.size());

	/*
	 * Term entry which contains term with requested description type and
	 * description value but with different language id
	 */
	TermEntry invalidTermEntry = termEntries.get(1);
	Set<Term> englishUSTerms = invalidTermEntry.getLanguageTerms().get("en-US");
	Set<Term> frenchTerms = invalidTermEntry.getLanguageTerms().get("fr-FR");
	Set<Term> englishGBTerms = invalidTermEntry.getLanguageTerms().get("en-GB");

	for (Term term : englishUSTerms) {
	    assertNull(term.getDescriptions());
	}

	for (Term term : frenchTerms) {
	    assertNull(term.getDescriptions());
	}

	for (Term term : englishGBTerms) {
	    assertTrue(term.getDescriptions().contains(requiredDescription));
	}

	/*
	 * Term entry which contains terms with requested language ids and requested
	 * description type but with different description value
	 */
	TermEntry invalidTermEntry1 = termEntries.get(3);
	Set<Term> englishUSTerms1 = invalidTermEntry1.getLanguageTerms().get("en-US");
	Set<Term> frenchTerms1 = invalidTermEntry1.getLanguageTerms().get("fr-FR");

	for (Term term : englishUSTerms1) {
	    assertNull(term.getDescriptions());
	}
	for (Term term : frenchTerms1) {
	    assertTrue(term.getDescriptions().contains(description));
	}

	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termEntries);

	final String exportTypeEnum = "TBX";

	final String path = "target/Document_Export_testFile1.tbx";

	when(getGlossarySearcher().concordanceSearch(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	ExportDocumentFactory exporter = ExportDocumentFactory.getInstance(ExportFormatEnum.valueOf(exportTypeEnum));
	exporter.open(new FileOutputStream(createTestFile(path)), searchRequest, null);
	ExportInfo exportInfo = getTermEntryService().exportDocument(searchRequest, new TmgrSearchFilter(),
		exportTypeEnum, new ExportAdapter());

	exporter.close();

	assertNotNull(exportInfo);
	assertEquals(exportInfo.getTotalTermEntriesExported(), 2);

    }

    @Test
    @TestCase("termEntry")
    public void exportXlsDocumentWithDescriptionFilter() throws Exception {

	// Description which will be used for filtering term entries
	Description requiredDescription = getModelObject("description3", Description.class);

	// Description with same type as requested description but with different value
	Description description = getModelObject("description4", Description.class);
	assertEquals(requiredDescription.getType(), description.getType());
	assertFalse(requiredDescription.getValue().equals(description.getValue()));

	TermEntrySearchRequest searchRequest = getModelObject("searchRequest3", TermEntrySearchRequest.class);
	assertEquals(searchRequest.getSourceLocale(), "en-US");
	assertEquals(searchRequest.getTargetLocales().get(0), "fr-FR");
	assertTrue(searchRequest.getDescriptions().contains(requiredDescription));

	List<TermEntry> termEntries = getModelObject("termEntries4", List.class);
	assertEquals(4, termEntries.size());

	/*
	 * Term entry which contains term with requested description type and
	 * description value but with different language id
	 */
	TermEntry invalidTermEntry = termEntries.get(1);
	Set<Term> englishUSTerms = invalidTermEntry.getLanguageTerms().get("en-US");
	Set<Term> frenchTerms = invalidTermEntry.getLanguageTerms().get("fr-FR");
	Set<Term> englishGBTerms = invalidTermEntry.getLanguageTerms().get("en-GB");

	for (Term term : englishUSTerms) {
	    assertNull(term.getDescriptions());
	}

	for (Term term : frenchTerms) {
	    assertNull(term.getDescriptions());
	}

	for (Term term : englishGBTerms) {
	    assertTrue(term.getDescriptions().contains(requiredDescription));
	}

	/*
	 * Term entry which contains terms with requested language ids and requested
	 * description type but with different description value
	 */
	TermEntry invalidTermEntry1 = termEntries.get(3);
	Set<Term> englishUSTerms1 = invalidTermEntry1.getLanguageTerms().get("en-US");
	Set<Term> frenchTerms1 = invalidTermEntry1.getLanguageTerms().get("fr-FR");

	for (Term term : englishUSTerms1) {
	    assertNull(term.getDescriptions());
	}
	for (Term term : frenchTerms1) {
	    assertTrue(term.getDescriptions().contains(description));
	}

	Page<TermEntry> page = new Page<TermEntry>(1, 0, 1, termEntries);

	final String exportTypeEnum = "XLS";

	final String path = "target/Document_Export_testFile1.xls";

	when(getGlossarySearcher().concordanceSearch(any(TmgrSearchFilter.class))).thenReturn(page)
		.thenReturn(new Page<TermEntry>(0, 0, 0, null));

	ExportDocumentFactory exporter = ExportDocumentFactory.getInstance(ExportFormatEnum.valueOf(exportTypeEnum));
	exporter.open(new FileOutputStream(createTestFile(path)), searchRequest, null);
	ExportInfo exportInfo = getTermEntryService().exportDocument(searchRequest, new TmgrSearchFilter(),
		exportTypeEnum, new ExportAdapter());

	exporter.close();

	assertNotNull(exportInfo);
	assertEquals(exportInfo.getTotalTermEntriesExported(), 2);

    }

    public ExportNotificationCallback getExportNotificationCallback() {
	return _exportNotificationCallback;
    }

    public StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    @TestCase("termEntry")
    public void mergeTermEtriesTest_01() throws Exception {
	List<TermEntry> termentries = getModelObject("termentries", List.class);
	termentries.forEach(t -> t.setProjectId(1L));

	Assert.assertEquals(2, termentries.size());

	String userName = TmUserProfile.getCurrentUserName();

	Iterator<TermEntry> iterator = termentries.iterator();

	TermEntry existing = iterator.next();
	existing.setProjectId(1L);

	Assert.assertEquals(2, existing.ggetTerms().size());

	List<TermEntry> incoming = new ArrayList<TermEntry>();
	TermEntry second = iterator.next();
	incoming.add(second);

	when(getGlossaryBrowser().findById(any(String.class), any(Long.class))).thenReturn(second);

	Map<Long, Long> numberOfTermEntries = new HashMap<>();
	numberOfTermEntries.put(1L, 1L);
	when(getGlossarySearcher().getNumberOfTermEntries(any(TmgrSearchFilter.class))).thenReturn(numberOfTermEntries);

	FacetTermCounts facetTermCounts = new FacetTermCounts(new ArrayList<>(existing.getLanguageTerms().keySet()));
	when(getGlossarySearcher().searchFacetTermCounts(any(TmgrSearchFilter.class))).thenReturn(facetTermCounts);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermEntryService().mergeTermEntries(existing, incoming);

	Assert.assertEquals(5, existing.ggetTerms().size());
	Assert.assertEquals(Action.MERGED, existing.getAction());
	Assert.assertEquals(userName, existing.getUserModified());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("termEntry")
    public void renameTermDescriptionsTest() throws TmException {
	List<TermEntry> termEntries = getModelObject("termEntries", List.class);
	termEntries.forEach(t -> t.setProjectId(PROJECT_ID));

	List<Attribute> attributes = Collections.singletonList(getModelObject("definitionAttribute", Attribute.class));

	when(getGlossaryBrowser().findByProjectId(PROJECT_ID)).thenReturn(termEntries);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermEntryService().renameTermDescriptions(1L, attributes);

	verify(getGlossaryBrowser(), times(1)).findByProjectId(PROJECT_ID);

	ArgumentCaptor<TransactionalUnit> transactionalUnitCaptor = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		transactionalUnitCaptor.capture());

	TransactionalUnit unit = transactionalUnitCaptor.getValue();

	assertTrue(unit.getTermEntries().get(0).getDescriptions().stream().map(Description::getType)
		.allMatch(Predicate.isEqual(attributes.get(0).getRenameValue())));

	ArgumentCaptor<Long> projectIdCaptor = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<Date> dateModifiedCaptor = ArgumentCaptor.forClass(Date.class);

	verify(getProjectDetailDAO()).updateDateModifiedByProjectId(projectIdCaptor.capture(),
		dateModifiedCaptor.capture());

	assertEquals(PROJECT_ID, projectIdCaptor.getValue().longValue());
	assertEquals(unit.getTermEntries().get(0).getDateModified().longValue(),
		dateModifiedCaptor.getValue().getTime());
    }

    @Before
    public void setUp() throws Exception {
	// Reset mock
	reset(getProjectDAO());
	reset(getGlossaryBrowser());

	reset(getTermEntryService());
	reset(getExportNotificationCallback());

	reset(getOutputStream());

	reset(getStatisticsService());

	TmProject tmProject = new TmProject();

	tmProject = getModelObject("tmProject", TmProject.class);
	tmProject.getProjectDetail().setProject(tmProject);

	Set<ProjectUserDetail> set = new HashSet<ProjectUserDetail>();
	set.add(new ProjectUserDetail((TmUserProfile) UserProfileContext.getCurrentUserProfile(),
		tmProject.getProjectDetail()));
	tmProject.getProjectDetail().setUserDetails(set);

	when(getProjectDAO().load(any(Long.class))).thenReturn(tmProject);
	when(getProjectDAO().findById(any(Long.class))).thenReturn(tmProject);

	ProjectDetail projectDetail = getModelObject("projectDetail", ProjectDetail.class);
	when(getQuery().uniqueResult()).thenReturn(projectDetail);
    }

    /*
     * TERII-3640: Target synonym is not saved after blacklist/unblacklist term
     * entry
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("termEntry")
    public void updateTermEntriesTest_01() throws TmException {
	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);
	termEntry.setProjectId(PROJECT_ID);

	List<TranslationUnit> translationUnits = getModelObject("translationUnits", List.class);

	when(getGlossaryBrowser().findById(anyString(), any(Long.class))).thenReturn(termEntry);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermEntryService().updateTermEntries(translationUnits, EN_US, PROJECT_ID, Action.EDITED);

	ArgumentCaptor<Long> argumentProjectId = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<EntityId> argumentEntityId = ArgumentCaptor.forClass(EntityId.class);

	ArgumentCaptor<TransactionalUnit> argumentTU = ArgumentCaptor.forClass(TransactionalUnit.class);

	verify(getTransactionLogHandler(), times(1)).appendAndLink(anyLong(), any(EntityId.class),
		argumentTU.capture());
	verify(getTransactionLogHandler(), times(1)).finishAppending(argumentProjectId.capture(),
		argumentEntityId.capture());

	TransactionalUnit transactionalUnit = argumentTU.getValue();
	Set<StatisticsInfo> statisticsInfos = transactionalUnit.getStatisticsInfo();
	assertEquals(1, statisticsInfos.size());

	StatisticsInfo statisticsInfo = statisticsInfos.iterator().next();

	assertEquals(1, statisticsInfo.getAddedApprovedCount().longValue());
	assertEquals(1, statisticsInfo.getApprovedCount().longValue());
	assertEquals(1, statisticsInfo.getUpdatedCount().longValue());
	assertEquals(0, statisticsInfo.getAddedPendingApprovalCount().longValue());
	assertEquals(0, statisticsInfo.getPendingApprovalCount().longValue());
	assertEquals(0, statisticsInfo.getAddedOnHoldCount().longValue());
	assertEquals(0, statisticsInfo.getOnHoldCount().longValue());
	assertEquals(0, statisticsInfo.getAddedBlacklistedCount().longValue());
	assertEquals(0, statisticsInfo.getBlackListedCount().longValue());
	assertEquals(0, statisticsInfo.getDeletedCount().longValue());
    }

    /*
     * TERII-5866: user tries to remove target term that is already removed by
     * another user on same project
     */
    @SuppressWarnings("unchecked")
    @Test
    @TestCase("termEntry")
    public void updateTermEntriesTest_02() throws TmException {
	TermEntry termEntry = getModelObject("termEntry12", TermEntry.class);

	List<TranslationUnit> translationUnits = getModelObject("targetTermUpdateTranslationUnit", List.class);

	when(getGlossaryBrowser().findById(anyString(), any(Long.class))).thenReturn(termEntry);

	String message = String.format(Messages.getString("SaveDashboard.1"), "de-DE");
	boolean isUserException = false;

	try {

	    getTermEntryService().updateTermEntries(translationUnits, EN_US, PROJECT_ID, Action.EDITED);
	} catch (UserException exception) {
	    isUserException = true;
	    assertEquals(exception.getMessage(), message);
	}

	assertTrue(isUserException);
    }

    /*
     * TERII-5866: user tries to remove source term that is already removed by
     * another user on same project
     */
    @SuppressWarnings("unchecked")
    @Test
    @TestCase("termEntry")
    public void updateTermEntriesTest_03() throws TmException {
	TermEntry termEntry = getModelObject("termEntry12", TermEntry.class);

	List<TranslationUnit> translationUnits = getModelObject("sourceTermUpdateTranslationUnit", List.class);

	when(getGlossaryBrowser().findById(anyString(), any(Long.class))).thenReturn(termEntry);

	String message = String.format(Messages.getString("SaveDashboard.1"), "en-EN");
	boolean isUserException = false;

	try {
	    getTermEntryService().updateTermEntries(translationUnits, EN_US, PROJECT_ID, Action.EDITED);
	} catch (UserException exception) {
	    isUserException = true;
	    assertEquals(exception.getMessage(), message);
	}
	assertTrue(isUserException);
    }

    /*
     * TERII-5866: user tries to remove target term but term entry is already
     * deleted by another user
     */
    @SuppressWarnings("unchecked")
    @Test
    @TestCase("termEntry")
    public void updateTermEntriesTest_04() throws TmException {
	TermEntry termEntry = getModelObject("termEntry13", TermEntry.class);

	List<TranslationUnit> translationUnits = getModelObject("targetTermUpdateTranslationUnit", List.class);

	when(getGlossaryBrowser().findById(anyString(), any(Long.class))).thenReturn(termEntry);

	String message = Messages.getString("SaveDashboard.2");
	boolean isUserException = false;

	try {
	    getTermEntryService().updateTermEntries(translationUnits, EN_US, PROJECT_ID, Action.EDITED);
	} catch (UserException exception) {
	    isUserException = true;
	    assertEquals(exception.getMessage(), message);
	}

	assertTrue(isUserException);
    }

    private void assertJsonResponse(ObjectNode jsonNode, String ticket) {
	boolean flag = false;
	if (ticket.equals("export-term00000001export-term00000002")) {
	    assertEquals(jsonNode.get(OPERATION).asText(), "U");
	    assertEquals(jsonNode.get("source").asText(), "term test 1 source");
	    assertEquals(jsonNode.get("target").asText(), "term test 2 target");
	    flag = true;
	} else if (ticket.equals("export-term00000003export-term00000004")) {
	    assertEquals(jsonNode.get(OPERATION).asText(), "U");
	    assertEquals(jsonNode.get("source").asText(), "term test 3 source");
	    assertEquals(jsonNode.get("target").asText(), "term test 4 target");
	    flag = true;
	} else if (ticket.equals("export-term00000003export-term00000005")) {
	    assertEquals(jsonNode.get(OPERATION).asText(), "D");
	    assertEquals(jsonNode.get("source").asText(), "term test 3 source");
	    assertEquals(jsonNode.get("target").asText(), "term test 5 target");
	    flag = true;
	}
	assertTrue(flag);

    }

    private static void assertTermEntry(TermEntry termEntry) {
	assertEquals(Action.EDITED, termEntry.getAction());

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Entry<String, Set<Term>>> languages = languageTerms.entrySet();

	for (Entry<String, Set<Term>> langSet : languages) {
	    switch (langSet.getKey()) {
	    case EN_US:
		Set<Term> sourceTerms = langSet.getValue();

		Term source = findTermWithValue("term test 1 source", sourceTerms);

		assertNotNull(source);

		// There is only one term for source language
		assertEquals(1, sourceTerms.size());
		assertEquals(PROCESSED, source.getStatus());
		break;
	    case DE_DE:
		Set<Term> targetTerms = langSet.getValue();

		// Find main term for this language and assert value
		Term target = findTermWithValue("term test 1 source", targetTerms);

		assertNotNull(target);
		assertEquals(PROCESSED, target.getStatus());

		// Find synonym term for this language and assert value
		target = findTermWithValue("term test 2 target", targetTerms);

		assertNotNull(target);
		assertEquals(PROCESSED, target.getStatus());

		assertEquals(2, targetTerms.size());
		break;
	    default:
		throw new IllegalArgumentException("Wrong language locale for this term entry");
	    }
	}
    }

    private File createTestFile(String path) throws IOException {
	File testFile = new File(path);
	if (testFile.exists()) {
	    testFile.delete();
	}
	testFile.createNewFile();
	testFile.deleteOnExit();
	return testFile;
    }

    private static Term findTermWithValue(String termName, Set<Term> terms) {
	return terms.stream().filter(term -> term.getName().equals(termName)).findFirst().orElse(null);
    }

    private OutputStream getOutputStream() {
	return _outputStream;
    }

    private ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    private void validateTerms(JsonNode terms, JsonNode locale) {
	Iterator<JsonNode> iterator = terms.iterator();

	switch (locale.asText()) {
	case EN_US:
	    assertEquals(1, terms.size());
	    // There is only one term for this language
	    JsonNode term = iterator.next();
	    assertEquals("en term text", term.get("termText").asText());
	    JsonNode forbidden = term.get("forbidden");
	    assertEquals(false, Boolean.valueOf(forbidden.toString()));
	    assertNotNull(term.get("descriptions"));
	    break;
	case DE_DE:
	    assertEquals(1, terms.size());
	    // There is only one term for this language
	    term = iterator.next();
	    assertEquals("de term text", term.get("termText").asText());
	    forbidden = term.get("forbidden");
	    assertEquals(false, Boolean.valueOf(forbidden.toString()));
	    assertNotNull(term.get("descriptions"));
	    break;
	case FR_FR:
	    assertEquals(1, terms.size());
	    // There is only one term for this language
	    term = iterator.next();
	    assertEquals("fr term text", term.get("termText").asText());
	    forbidden = term.get("forbidden");
	    assertEquals(false, Boolean.valueOf(forbidden.toString()));
	    assertNotNull(term.get("descriptions"));
	    break;
	default:
	    throw new IllegalArgumentException("Wrong language locale for this test");
	}
    }
}

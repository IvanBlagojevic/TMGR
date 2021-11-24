package org.gs4tr.termmanager.service.file.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisAlerts;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttributeReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguage;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguageReport;
import org.gs4tr.termmanager.service.file.analysis.model.Language;
import org.gs4tr.termmanager.service.file.analysis.model.Status;
import org.gs4tr.termmanager.service.file.analysis.request.Context;
import org.gs4tr.termmanager.service.file.analysis.request.Context.Builder;
import org.gs4tr.termmanager.service.file.analysis.request.FilesAnalysisRequest;
import org.gs4tr.termmanager.service.file.analysis.response.FilesAnalysisResponse;
import org.gs4tr.termmanager.service.file.manager.FileManager;
import org.gs4tr.termmanager.service.impl.ImportValidationCallback;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig;
import org.gs4tr.termmanager.service.xls.DefaultXlsTermEntryReader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("unchecked")
@TestSuite("fileAnalysis")
public class FilesAnalysisRequestHandlerTest extends AbstractSpringFilesAnalysisTest {

    private static final String PARENT_DIRECTORY = "batch";

    private String _directory;

    @Autowired
    private FileManager _manager;

    @Autowired
    private FilesAnalysisRequestHandler _requestHandler;

    @Test
    @TestCase("checkAnalysisReportSort")
    public void handleFileCheckAnalysisReportSortTest()
	    throws IOException, URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
	uploadRepositoryItem("starwood");

	Set<String> userProjectLanguages = getModelObject("userProjectLanguages", Set.class);
	Builder builder = new Builder(userProjectLanguages, false);

	Map<Level, Set<String>> attributesByLevel = getModelObject("attributesByLevel", Map.class);
	builder.attributesByLevel(attributesByLevel);

	FilesAnalysisRequest request = new FilesAnalysisRequest(getDirectory(),
		builder.sourceLanguage(Locale.US.getCode()).build());

	FilesAnalysisResponse response = handleAsync(request);

	List<ImportLanguageReport> importLanguageReports = response.getImportLanguageReports();
	assertEquals(1, importLanguageReports.size());

	ImportLanguageReport starwood = importLanguageReports.get(0);

	List<ImportLanguage> starwoodImportLanguages = starwood.getImportLanguages();
	assertEquals(3, starwoodImportLanguages.size());

	Iterator<ImportLanguage> eachLanguage = starwoodImportLanguages.iterator();

	// Locale.US should be first in the list..
	ImportLanguage englishSource = eachLanguage.next();
	assertEquals(Locale.US, englishSource.getLocale());
	assertEquals(Status.SOURCE, englishSource.getStatus());

	// then Locale.FRENCH with similar project language
	ImportLanguage newFrench = eachLanguage.next();
	assertEquals(Locale.FRENCH, newFrench.getLocale());
	assertTrue(CollectionUtils.isNotEmpty(newFrench.getSimilarProjectLanguages()));
	assertEquals(Status.NEW, newFrench.getStatus());

	/*
	 * and finally Locale.GERMANY because, for this language, there are no warnings
	 * (i.e similar project languages)
	 */
	ImportLanguage newGermany = eachLanguage.next();
	assertEquals(Locale.GERMANY, newGermany.getLocale());
	assertTrue(CollectionUtils.isEmpty(newGermany.getSimilarProjectLanguages()));
	assertEquals(Status.NEW, newGermany.getStatus());

	List<ImportAttributeReport> importAttributeReports = response.getImportAttributeReports();
	assertEquals(1, importAttributeReports.size());

	ImportAttributeReport starwoodImportAttributeReport = importAttributeReports.get(0);
	List<ImportAttribute> starwoodImportAttributes = starwoodImportAttributeReport.getImportAttributes();
	assertEquals(2, starwoodImportAttributes.size());

	Iterator<ImportAttribute> eachAttribute = starwoodImportAttributes.iterator();

	// "usage" should be first because project attributes list is not empty
	ImportAttribute usage = eachAttribute.next();
	assertEquals("usage", usage.getName());
	assertEquals(Status.NEW, usage.getStatus());
	assertTrue(CollectionUtils.isNotEmpty(usage.getProjectAttributes()));

	// and then context which is existing attribute
	ImportAttribute context = eachAttribute.next();
	assertEquals("context", context.getName());
	assertEquals(Status.EXISTING, context.getStatus());
    }

    @Test
    @TestCase("fileHasInvalidLanguageCode")
    public void handleFileCheckAnalysisWithInvalidLanguageCode()
	    throws IOException, URISyntaxException, InterruptedException, ExecutionException, TimeoutException {

	String invalidLanguageCode = "en-MK";

	uploadRepositoryItem("invalid");

	Set<String> userProjectLanguages = getModelObject("userProjectLanguages", Set.class);
	Builder builder = new Builder(userProjectLanguages, false);
	builder.sourceLanguage(Locale.ENGLISH.getCode());

	Context context = builder.attributesByLevel(Collections.emptyMap()).build();
	FilesAnalysisRequest request = new FilesAnalysisRequest(getDirectory(), context);

	FilesAnalysisResponse response = handleAsync(request);

	List<FileAnalysisAlerts> fileAnalysisAlerts = response.getFileAnalysisAlerts();
	assertTrue(CollectionUtils.isNotEmpty(fileAnalysisAlerts));
	assertEquals(1, fileAnalysisAlerts.size());

	List<Alert> alerts = fileAnalysisAlerts.get(0).getAlerts();
	assertEquals(1, alerts.size());

	Alert fileAlert = alerts.get(0);

	String alertMessage = fileAlert.getMessage();
	AlertType alertType = fileAlert.getType();
	assertEquals(alertMessage,
		String.format(MessageResolver.getMessage("ImportValidationCallback.1"), invalidLanguageCode));
	assertEquals(AlertType.ERROR, alertType);

    }

    @Test
    @TestCase("fistFileHaveErrorSecondFileHaveWarrnings")
    public void handleTwoFilesWhenFistFileHaveErrorSecondFileHaveWarrningsTest()
	    throws IOException, URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
	uploadRepositoryItem("medra");
	uploadRepositoryItem("skype");

	Set<String> userProjectLanguages = getModelObject("userProjectLanguages", Set.class);
	Builder builder = new Context.Builder(userProjectLanguages, true);
	builder.sourceLanguage(Locale.GERMANY.getCode());

	Context context = builder.attributesByLevel(Collections.emptyMap()).build();
	FilesAnalysisRequest request = new FilesAnalysisRequest(getDirectory(), context);

	FilesAnalysisResponse response = handleAsync(request);
	assertFalse(response.isOverwriteByTermEntryId());

	List<FileAnalysisAlerts> alerts = response.getFileAnalysisAlerts();

	FileAnalysisAlerts skype = find(alerts, alert -> alert.getFileName().contains("Skype.xls"));
	List<Alert> skypeAlerts = skype.getAlerts();
	assertEquals(1, skypeAlerts.size());
	Alert skypeAlert = skypeAlerts.get(0);
	assertEquals(AlertSubject.HIDDEN_WORKSHEET, skypeAlert.getSubject());
	assertEquals(AlertType.ERROR, skypeAlert.getType());

	FileAnalysisAlerts medra = find(alerts, alert -> alert.getFileName().contains("Medra.xls"));
	List<Alert> medraAlerts = medra.getAlerts();
	assertEquals(1, medraAlerts.size());
	Alert statusCheckNikonAlert = medraAlerts.get(0);
	assertEquals(AlertSubject.STATUS_CHECK, statusCheckNikonAlert.getSubject());
	assertEquals(AlertType.WARNING, statusCheckNikonAlert.getType());

	List<ImportLanguageReport> importLanguageReports = response.getImportLanguageReports();
	assertEquals(1, importLanguageReports.size());

	ImportLanguageReport medraImportLanguageReport = find(importLanguageReports,
		report -> report.getFileName().contains("Medra.xls"));
	assertEquals(2, medraImportLanguageReport.getNumberOfTermEntries());

	List<ImportLanguage> importLanguages = medraImportLanguageReport.getImportLanguages();
	assertEquals(3, importLanguages.size());

	ImportLanguage english = find(importLanguages, language -> language.getLocale().equals(Locale.ENGLISH));
	assertEquals(Status.EXISTING, english.getStatus());
	List<Language> englishSimilarLanguages = english.getSimilarProjectLanguages();
	assertEquals(0, englishSimilarLanguages.size());

	ImportLanguage germany = find(importLanguages, language -> language.getLocale().equals(Locale.GERMANY));
	assertEquals(Status.SOURCE, germany.getStatus());
	assertEquals(0, germany.getSimilarProjectLanguages().size());

	ImportLanguage french = find(importLanguages, language -> language.getLocale().equals(Locale.FRANCE));
	assertEquals(Status.NEW, french.getStatus());
	List<Language> simiralFrenchLanguages = french.getSimilarProjectLanguages();
	assertEquals(1, simiralFrenchLanguages.size());
	Language frenchSimilarLanguage = simiralFrenchLanguages.get(0);
	assertEquals(Locale.FRENCH.getCode(), frenchSimilarLanguage.getLocale());
	assertEquals(Locale.FRENCH.getDisplayName(), frenchSimilarLanguage.getValue());

	List<ImportAttributeReport> importAttributeReports = response.getImportAttributeReports();
	assertEquals(1, importAttributeReports.size());

	assertTrue(importAttributeReports.stream().map(ImportAttributeReport::getImportAttributes).flatMap(List::stream)
		.map(ImportAttribute::getStatus).allMatch(Predicate.isEqual(Status.NEW)));
    }

    @Test
    @TestCase("twoFilesWithFatalErrors")
    public void handleTwoFilesWithFatalErrorsTest()
	    throws IOException, URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
	uploadRepositoryItem("skype");
	uploadRepositoryItem("nikon");

	Set<String> userProjectLanguages = getModelObject("userProjectLanguages", Set.class);
	Context context = new Context.Builder(userProjectLanguages, true).build();

	FilesAnalysisRequest request = new FilesAnalysisRequest(getDirectory(), context);

	FilesAnalysisResponse response = handleAsync(request);

	assertTrue(CollectionUtils.isEmpty(response.getImportLanguageReports()));
	assertTrue(CollectionUtils.isEmpty(response.getImportAttributeReports()));
	assertFalse(response.isOverwriteByTermEntryId());

	List<FileAnalysisAlerts> alerts = response.getFileAnalysisAlerts();

	FileAnalysisAlerts skype = find(alerts, alert -> alert.getFileName().contains("Skype.xls"));
	List<Alert> skypeAlerts = skype.getAlerts();
	assertEquals(1, skypeAlerts.size());
	Alert skypeAlert = skypeAlerts.get(0);
	assertEquals(AlertSubject.HIDDEN_WORKSHEET, skypeAlert.getSubject());
	assertEquals(AlertType.ERROR, skypeAlert.getType());

	FileAnalysisAlerts nikon = find(alerts, alert -> alert.getFileName().contains("Nikon.xls"));
	List<Alert> nikonAlerts = nikon.getAlerts();
	assertEquals(1, nikonAlerts.size());
	Alert nikonAlert = nikonAlerts.get(0);
	assertEquals(AlertSubject.HEADER_CHECK, nikonAlert.getSubject());
	assertEquals(AlertType.ERROR, nikonAlert.getType());

	assertEquals(2, alerts.size());
    }

    @Test
    @TestCase("twoFilesWithWarrnings")
    public void handleTwoFilesWithWarrningsTest()
	    throws IOException, URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
	uploadRepositoryItem("hilton");
	uploadRepositoryItem("medronic");

	Set<String> userProjectLanguages = getModelObject("userProjectLanguages", Set.class);

	/*
	 * This should affect check for all defined project attributes(including combo
	 * value attributes)
	 */
	final boolean ignoreCase = true;

	Builder builder = new Context.Builder(userProjectLanguages, ignoreCase);

	Map<Level, Set<String>> attributesByLevel = getModelObject("attributesByLevel", Map.class);
	builder.attributesByLevel(attributesByLevel);

	Map<String, Set<String>> comboValuesPerAttribute = getModelObject("comboValuesPerAttribute", Map.class);
	builder.comboValuesPerAttribute(comboValuesPerAttribute);
	builder.sourceLanguage(Locale.GERMANY.getCode());

	FilesAnalysisRequest request = new FilesAnalysisRequest(getDirectory(), builder.build());

	FilesAnalysisResponse response = handleAsync(request);
	assertFalse(response.isOverwriteByTermEntryId());

	List<FileAnalysisAlerts> alerts = response.getFileAnalysisAlerts();

	FileAnalysisAlerts hilton = find(alerts, alert -> alert.getFileName().contains("Hilton.xls"));
	assertTrue(CollectionUtils.isEmpty(hilton.getAlerts()));

	FileAnalysisAlerts medronic = find(alerts, alert -> alert.getFileName().contains("Medronic.xls"));
	List<Alert> medronicAlerts = medronic.getAlerts();
	assertEquals(1, medronicAlerts.size());
	assertEquals(AlertSubject.ATTRIBUTE_CHECK, medronicAlerts.get(0).getSubject());

	List<ImportLanguageReport> importLanguageReports = response.getImportLanguageReports();
	assertEquals(2, importLanguageReports.size());

	ImportLanguageReport hiltonImportLanguageReport = find(importLanguageReports,
		report -> report.getFileName().contains("Hilton.xls"));
	assertEquals(3, hiltonImportLanguageReport.getImportLanguages().size());

	ImportLanguageReport medronicImportLanguageReport = find(importLanguageReports,
		report -> report.getFileName().contains("Medronic.xls"));
	assertEquals(2, medronicImportLanguageReport.getImportLanguages().size());

	List<ImportAttributeReport> importAttributeReports = response.getImportAttributeReports();
	assertEquals(2, importAttributeReports.size());

	ImportAttributeReport hiltonImportAttributeReport = find(importAttributeReports,
		report -> report.getFileName().contains("Hilton.xls"));
	List<ImportAttribute> importAttributes = hiltonImportAttributeReport.getImportAttributes();
	assertEquals(2, importAttributes.size());

	ImportAttribute hiltonUsageImportAttribute = find(importAttributes,
		attribute -> attribute.getName().equals("Usage"));
	assertEquals(Status.NEW, hiltonUsageImportAttribute.getStatus());
	assertEquals(3, hiltonUsageImportAttribute.getProjectAttributes().size());

	// Because ignoreCase flag is on
	ImportAttribute hiltonContextImportAttribute = find(importAttributes,
		attribute -> attribute.getName().equals("CONtext"));
	assertEquals(Status.EXISTING, hiltonContextImportAttribute.getStatus());
	assertEquals(0, hiltonContextImportAttribute.getProjectAttributes().size());

	ImportAttributeReport medronicImportAttributeReport = find(importAttributeReports,
		report -> report.getFileName().contains("Medronic.xls"));
	List<ImportAttribute> medronicImportAttributes = medronicImportAttributeReport.getImportAttributes();
	assertEquals(2, medronicImportAttributes.size());

	/*
	 * Medronic file contains: cOMBO: COMBO value2, Combo: combo value. Existing
	 * project attributes: [ "CONTEXT", "definition", "combo"].
	 *
	 * All Medronic import attributes are existing because ignoreCase flag is on.
	 */
	assertTrue(medronicImportAttributes.stream().map(ImportAttribute::getStatus)
		.allMatch(Predicate.isEqual(Status.EXISTING)));
    }

    @Test
    @TestCase("zipTbxXlsFiles")
    public void handleZipFileTest()
	    throws IOException, URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
	uploadRepositoryItem("zip");

	Set<String> userProjectLanguages = getModelObject("userProjectLanguages", Set.class);
	Map<Level, Set<String>> attributesByLevel = getModelObject("attributesByLevel", Map.class);

	FilesAnalysisRequest request = new FilesAnalysisRequest(getDirectory(), new Builder(userProjectLanguages, false)
		.sourceLanguage(Locale.ITALIAN.getCode()).attributesByLevel(attributesByLevel).build());

	FilesAnalysisResponse response = handleAsync(request);

	List<FileAnalysisAlerts> alerts = response.getFileAnalysisAlerts();

	FileAnalysisAlerts medronic = find(alerts, alert -> alert.getFileName().contains("Medronic.xls"));
	List<Alert> medronicAlerts = medronic.getAlerts();
	assertEquals(1, medronicAlerts.size());
	assertEquals(AlertSubject.HEADER_CHECK, medronicAlerts.get(0).getSubject());

	FileAnalysisAlerts nikon = find(alerts, alert -> alert.getFileName().contains("Nikon.xls"));
	List<Alert> nikonAlerts = nikon.getAlerts();
	assertEquals(1, nikonAlerts.size());
	assertEquals(AlertSubject.HEADER_CHECK, nikonAlerts.get(0).getSubject());

	FileAnalysisAlerts hilton = find(alerts, alert -> alert.getFileName().contains("Hilton.xls"));
	List<Alert> hiltonAlerts = hilton.getAlerts();
	assertEquals(1, hiltonAlerts.size());
	assertEquals(AlertSubject.NO_COMMON_SOURCE_LANGUAGE, hiltonAlerts.get(0).getSubject());

	FileAnalysisAlerts big = find(alerts, alert -> alert.getFileName().contains("big (en, fr_FR) - OK.tbx"));
	List<Alert> bigAlerts = big.getAlerts();
	assertEquals(1, bigAlerts.size());
	assertEquals(AlertSubject.NO_COMMON_SOURCE_LANGUAGE, bigAlerts.get(0).getSubject());

	FileAnalysisAlerts skype = find(alerts,
		alert -> alert.getFileName().contains("terms_skype_utf8_TMgr4-fixed-by-Marko.tbx"));
	List<Alert> skypeAlerts = skype.getAlerts();
	assertEquals(1, skypeAlerts.size());
	assertEquals(AlertSubject.NO_COMMON_SOURCE_LANGUAGE, skypeAlerts.get(0).getSubject());

	// Size is 0 because all files have a fatal error(s).
	List<ImportLanguageReport> importLanguageReports = response.getImportLanguageReports();
	assertEquals(0, importLanguageReports.size());

	List<ImportAttributeReport> importAttributeReports = response.getImportAttributeReports();
	assertEquals(0, importAttributeReports.size());
    }

    @After
    public void manualCleanup() throws IOException {
	getFileManager().cleanup();
    }

    /*
     * Import - Attribute check - <> operator isn't recognized as multi combo value
     * match | TERII-5341
     */
    @Test
    public void testCase_TERII_5341() throws Exception {

	String xls = "src/test/resources/xls/TERII_5341.xls";

	TermEntryReader importer = createImporterWithComboValues(xls, "en-US");

	ImportValidationCallback callback = new ImportValidationCallback(1);

	importer.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	Assert.assertTrue(alerts.isEmpty());

    }

    private TermEntryReader createImporterWithComboValues(String xls, String syncLanguage)
	    throws FileNotFoundException {
	InputStream input = new FileInputStream(xls);
	String encoding = "UTF-8";

	List<String> importingLanguageIds = new ArrayList<String>();
	importingLanguageIds.add("en-US");

	Map<String, Set<String>> comboValuesPerAttribute = new HashMap<>();

	Set<String> comboValues = new HashSet<>();
	comboValues.add("comboValue1");
	comboValues.add("comboValue2");

	comboValuesPerAttribute.put("context1", comboValues);

	TermEntryReader reader = new DefaultXlsTermEntryReader(new TermEntryReaderConfig.Builder().stream(input)
		.encoding(encoding).userProjectLanguages(importingLanguageIds)
		.comboValuesPerAttribute(comboValuesPerAttribute).build());
	return reader;
    }

    private <T> T find(Collection<T> c, Predicate<T> condition) {
	return c.stream().filter(condition).findFirst().orElseThrow(NoSuchElementException::new);
    }

    private String getDirectory() {
	return _directory;
    }

    private FileManager getFileManager() {
	return _manager;
    }

    private RepositoryItem getRepositoryItem(String name) throws IOException, URISyntaxException {
	RepositoryItem repositoryItem = getModelObject(name, RepositoryItem.class);
	URL resource = getResourceFrom(repositoryItem.getResourceInfo().getName());
	repositoryItem.setInputStream(Files.newInputStream(Paths.get(resource.toURI())));

	return repositoryItem;
    }

    private FilesAnalysisRequestHandler getRequestHandler() {
	return _requestHandler;
    }

    private FilesAnalysisResponse handleAsync(FilesAnalysisRequest request)
	    throws InterruptedException, ExecutionException, TimeoutException {
	Future<FilesAnalysisResponse> responseFuture = getRequestHandler().handleAsync(request);
	return responseFuture.get(1, TimeUnit.MINUTES);
    }

    private void setDirectory(String directory) {
	_directory = directory;
    }

    private void uploadRepositoryItem(final String name) throws IOException, URISyntaxException {
	RepositoryItem repositoryItem = getRepositoryItem(name);
	List<String> pathnames = getFileManager().store(repositoryItem);
	setDirectory(repositoryItem.getResourceInfo().getPath());

	LOG.info(String.format("Files %s are uploaded in /%s directory.", pathnames, getDirectory()));
    }

    @Override
    protected URL getResourceFrom(final String pathname) {
	Class<FilesAnalysisRequestHandlerTest> clazz = FilesAnalysisRequestHandlerTest.class;
	URL resource = clazz.getClassLoader().getResource(Paths.get(PARENT_DIRECTORY, pathname).toString());
	return resource;
    }
}

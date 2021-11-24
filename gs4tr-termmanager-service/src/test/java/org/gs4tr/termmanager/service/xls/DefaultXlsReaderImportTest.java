package org.gs4tr.termmanager.service.xls;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.locale.LocaleException;
import org.gs4tr.foundation3.callback.ImportCallback;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.impl.ImportValidationCallback;
import org.gs4tr.termmanager.service.mocking.AbstractServiceTest;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.junit.Assert;
import org.junit.Test;

public class DefaultXlsReaderImportTest extends AbstractServiceTest {

    private static final String ALLOWED_TERM_ATTRIBUTE_TYPE = "context";

    private static final Log LOG = LogFactory.getLog(DefaultXlsReaderImportTest.class);

    private static final String PARENT_DIRECTORY = "xls";

    private static final String PROCESSED = ItemStatusTypeHolder.PROCESSED.getName();

    private static final Long PROJECT_ID = 1L;

    private static final String PROJECT_NAME = "Skype";

    private static final String SHORT_CODE = "SKY000001";

    @Test
    public void testCase1() throws Exception {
	String xls = "src/test/resources/xls/test1.xls";

	Set<String> allowedTermEntryAttributes = new HashSet<>();
	allowedTermEntryAttributes.add("termEntry1");
	allowedTermEntryAttributes.add("TermEntry2");

	ImportOptionsModel importOptions = createImportOptionsModel(PROJECT_ID, SHORT_CODE, PROJECT_NAME, PROCESSED,
		allowedTermEntryAttributes, ALLOWED_TERM_ATTRIBUTE_TYPE);

	TermEntryReader importer = createImporter(xls, Locale.US.getCode());

	ImportCallback callback = new ImportCallback() {
	    @Override
	    public int getTotalTerms() {
		return 2;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		String message = "Import completed: " + percentage + "%";
		LOG.debug(message);
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		TermEntry termEntry = TermEntryUtils.createTermEntry(item, importOptions, SyncOption.MERGE);
		Assert.assertTrue(CollectionUtils.isNotEmpty(termEntry.getDescriptions()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(termEntry.ggetTerms()));
	    }
	};

	importer.readTermEntries(callback);
    }

    @Test
    public void testCase2() throws Exception {
	String xls = "src/test/resources/xls/test2.xls";

	TermEntryReader importer = createImporter(xls, Locale.ENGLISH.getCode());

	Set<String> allowedTermEntryAttributes = new HashSet<>();
	allowedTermEntryAttributes.add("termEntry1");
	allowedTermEntryAttributes.add("TermEntry2");

	ImportOptionsModel importOptions = createImportOptionsModel(1L, "TES00001", "TES00001",
		ItemStatusTypeHolder.PROCESSED.getName(), allowedTermEntryAttributes, ALLOWED_TERM_ATTRIBUTE_TYPE);

	ImportCallback callback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 2;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		String message = "Import completed: " + percentage + "%";
		LOG.debug(message);
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		TermEntry termEntry = TermEntryUtils.createTermEntry(item, importOptions, SyncOption.MERGE);
		Assert.assertTrue(CollectionUtils.isNotEmpty(termEntry.getDescriptions()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(termEntry.ggetTerms()));
	    }
	};

	importer.readTermEntries(callback);
    }

    @Test
    public void testCase3() throws Exception {
	String xls = "src/test/resources/xls/test3.xls";

	Set<String> allowedTermEntryAttributes = new HashSet<>();
	allowedTermEntryAttributes.add("termEntry1");
	allowedTermEntryAttributes.add("TermEntry2");

	ImportOptionsModel importOptions = createImportOptionsModel(PROJECT_ID, SHORT_CODE, PROJECT_NAME, PROCESSED,
		allowedTermEntryAttributes, ALLOWED_TERM_ATTRIBUTE_TYPE);

	TermEntryReader importer = createImporter(xls, Locale.US.getCode());

	ImportCallback callback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 2;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		String message = "Import completed: " + percentage + "%";
		LOG.debug(message);
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		TermEntry termEntry = TermEntryUtils.createTermEntry(item, importOptions, SyncOption.MERGE);
		Assert.assertTrue(CollectionUtils.isNotEmpty(termEntry.getDescriptions()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(termEntry.ggetTerms()));
	    }
	};

	importer.readTermEntries(callback);
    }

    @Test
    public void testCase4() throws Exception {
	String xls = "src/test/resources/xls/test4.xls";

	Set<String> allowedTermEntryAttributes = new HashSet<>();
	allowedTermEntryAttributes.add("termEntry1");
	allowedTermEntryAttributes.add("TermEntry2");

	ImportOptionsModel importOptions = createImportOptionsModel(PROJECT_ID, SHORT_CODE, PROJECT_NAME, PROCESSED,
		allowedTermEntryAttributes, ALLOWED_TERM_ATTRIBUTE_TYPE);

	TermEntryReader importer = createImporter(xls, Locale.US.getCode());

	ImportCallback callback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 2;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		String message = "Import completed: " + percentage + "%";
		LOG.debug(message);
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		TermEntry termEntry = TermEntryUtils.createTermEntry(item, importOptions, SyncOption.MERGE);
		Assert.assertTrue(CollectionUtils.isNotEmpty(termEntry.getDescriptions()));
		List<Term> terms = termEntry.ggetTerms();
		Assert.assertTrue(CollectionUtils.isNotEmpty(terms));
		for (Term term : terms) {
		    if (term.getName().equals("dog")) {
			Set<Description> termDescriptions = term.getDescriptions();
			Assert.assertTrue(CollectionUtils.isNotEmpty(termDescriptions));
			Assert.assertEquals(1, termDescriptions.size());
			for (Description termDescription : termDescriptions) {
			    Assert.assertEquals("context", termDescription.getType());
			    Assert.assertEquals("dog context", termDescription.getValue());
			}
		    }
		}
	    }
	};

	importer.readTermEntries(callback);
    }

    @Test
    public void testCase5() throws Exception {
	String xls = "src/test/resources/xls/test5.xls";

	Set<String> allowedTermEntryAttributes = new HashSet<>();
	allowedTermEntryAttributes.add("termEntry1");
	allowedTermEntryAttributes.add("TermEntry2");

	ImportOptionsModel importOptions = createImportOptionsModel(PROJECT_ID, SHORT_CODE, PROJECT_NAME, PROCESSED,
		allowedTermEntryAttributes, ALLOWED_TERM_ATTRIBUTE_TYPE);

	TermEntryReader importer = createImporter(xls, Locale.US.getCode());

	ImportCallback callback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 2;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		String message = "Import completed: " + percentage + "%";
		LOG.debug(message);
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		TermEntry termEntry = TermEntryUtils.createTermEntry(item, importOptions, SyncOption.MERGE);
		Assert.assertTrue(CollectionUtils.isNotEmpty(termEntry.getDescriptions()));
		List<Term> terms = termEntry.ggetTerms();
		Assert.assertTrue(CollectionUtils.isNotEmpty(terms));
		for (Term term : terms) {
		    if (term.getName().equals("dog")) {
			Set<Description> termDescriptions = term.getDescriptions();
			Assert.assertTrue(CollectionUtils.isNotEmpty(termDescriptions));
			Assert.assertEquals(1, termDescriptions.size());
			for (Description termDescription : termDescriptions) {
			    Assert.assertEquals("context", termDescription.getType());
			    Assert.assertEquals("dog context", termDescription.getValue());
			}
		    }
		}
	    }
	};

	importer.readTermEntries(callback);
    }

    @Test
    public void testCase6() throws Exception {
	String xls = "src/test/resources/xls/test6.xls";

	Set<String> allowedTermEntryAttributes = new HashSet<>();
	allowedTermEntryAttributes.add("termEntry1");
	allowedTermEntryAttributes.add("TermEntry2");

	ImportOptionsModel importOptions = createImportOptionsModel(PROJECT_ID, SHORT_CODE, PROJECT_NAME, PROCESSED,
		allowedTermEntryAttributes, ALLOWED_TERM_ATTRIBUTE_TYPE);

	TermEntryReader importer = createImporter(xls, Locale.US.getCode());

	ImportCallback callback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 2;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		String message = "Import completed: " + percentage + "%";
		LOG.debug(message);
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		TermEntry termEntry = TermEntryUtils.createTermEntry(item, importOptions, SyncOption.MERGE);
		Assert.assertTrue(CollectionUtils.isNotEmpty(termEntry.getDescriptions()));
		List<Term> terms = termEntry.ggetTerms();
		Assert.assertTrue(CollectionUtils.isNotEmpty(terms));
		for (Term term : terms) {
		    if (term.getName().equals("dog1")) {
			Set<Description> termDescriptions = term.getDescriptions();
			Assert.assertTrue(CollectionUtils.isNotEmpty(termDescriptions));
			Assert.assertEquals(1, termDescriptions.size());
			for (Description termDescription : termDescriptions) {
			    Assert.assertEquals("context", termDescription.getType());
			    Assert.assertEquals("dog1 context", termDescription.getValue());
			}
		    }
		}
	    }
	};

	importer.readTermEntries(callback);
    }

    @Test
    public void testCase_TERII_1174() throws Exception {
	String xls = "src/test/resources/xls/TERII_1174.xls";

	ImportOptionsModel importOptions = createImportOptionsModel(PROJECT_ID, SHORT_CODE, PROJECT_NAME, PROCESSED,
		Collections.emptySet(), ALLOWED_TERM_ATTRIBUTE_TYPE);

	TermEntryReader importer = createImporter(xls, Locale.US.getCode());

	ImportCallback callback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 2;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		String message = "Import completed: " + percentage + "%";
		LOG.debug(message);
	    }

	    @Override
	    public void handleTermEntry(Object item) {

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, importOptions, SyncOption.MERGE);
		Assert.assertTrue(MapUtils.isNotEmpty(termEntry.getLanguageTerms()));
		Assert.assertTrue(CollectionUtils.isEmpty(termEntry.getDescriptions()));

		Assert.assertTrue(CollectionUtils
			.isEmpty(termEntry.getLanguageTerms().get("en-US").iterator().next().getDescriptions()));
	    }
	};

	importer.readTermEntries(callback);
    }

    @Test(expected = LocaleException.class)
    public void testCase_TERII_3741() throws Exception {

	String xls = "src/test/resources/xls/TERII_3741.xlsx";

	TermEntryReader importer = createImporter(xls, "en-US");

	ImportValidationCallback callback = new ImportValidationCallback(1);

	importer.validate(callback);
    }

    @Test
    public void testReplaceImportLanguage() throws Exception {
	ImportCallback importCallback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 1;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		LOG.info(String.format("Import percentage: %d", percentage));
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		ImportOptionsModel options = createImportOptionsModel(PROJECT_ID, SHORT_CODE, PROJECT_NAME, PROCESSED,
			Collections.emptySet(), ALLOWED_TERM_ATTRIBUTE_TYPE);
		Map<String, String> languageReplacementByCode = new HashMap<>(1);
		languageReplacementByCode.put(Locale.ENGLISH.getCode(), Locale.US.getCode());
		options.setLanguageReplacementByCode(languageReplacementByCode);

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, options, SyncOption.OVERWRITE);
		Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
		assertTrue(languageTerms.containsKey(Locale.US.getCode()));
	    }
	};

	final URL resource = getResourceFrom("Skype.xls");

	TermEntryReaderConfig.Builder readerConfig = new TermEntryReaderConfig.Builder();
	readerConfig.stream(Files.newInputStream(Paths.get(resource.toURI())));
	readerConfig.userProjectLanguages(Arrays.asList(Locale.ENGLISH.getCode(), Locale.GERMANY.getCode()));
	readerConfig.encoding(Charsets.UTF_8.name());

	TermEntryReader reader = new DefaultXlsTermEntryReader(readerConfig.build());
	reader.readTermEntries(importCallback);
    }

    @Test
    public void testReplaceTermAttribute() throws Exception {
	ImportCallback importCallback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 1;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		LOG.info(String.format("Import percentage: %d", percentage));
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		ImportOptionsModel options = createImportOptionsModel(PROJECT_ID, SHORT_CODE, PROJECT_NAME, PROCESSED,
			Collections.emptySet(), ALLOWED_TERM_ATTRIBUTE_TYPE);
		Map<String, Map<String, String>> attributeNoteReplacements = new HashMap<>(1);
		Map<String, String> attributeReplacements = new HashMap<>(1);
		String oldTermAttribute = "context", newTermAttribute = "Supply Chain";
		attributeReplacements.put(oldTermAttribute, newTermAttribute);
		attributeNoteReplacements.put(Description.ATTRIBUTE, attributeReplacements);
		options.setAttributeNoteReplacements(attributeNoteReplacements);

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, options, SyncOption.OVERWRITE);

		assertTrue(termEntry.ggetAllTerms().stream().map(Term::getDescriptions)
			.filter(CollectionUtils::isNotEmpty).flatMap(Set::stream)
			.filter(description -> description.getBaseType().equals(Description.ATTRIBUTE))
			.map(Description::getType).allMatch(Predicate.isEqual(newTermAttribute)));
	    }
	};

	List<String> userProjectLanguages = new ArrayList<>(3);
	userProjectLanguages.add(Locale.US.getCode());
	userProjectLanguages.add(Locale.GERMANY.getCode());
	userProjectLanguages.add(Locale.FRANCE.getCode());

	final URL resource = getResourceFrom("Nikon.xls");

	TermEntryReaderConfig.Builder readerConfig = new TermEntryReaderConfig.Builder();
	readerConfig.stream(Files.newInputStream(Paths.get(resource.toURI())));
	readerConfig.userProjectLanguages(userProjectLanguages);
	readerConfig.encoding(Charsets.UTF_8.name());

	TermEntryReader reader = new DefaultXlsTermEntryReader(readerConfig.build());
	reader.readTermEntries(importCallback);
    }

    @Test
    public void testReplaceTermEntryAttribute() throws Exception {
	ImportCallback importCallback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 1;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		LOG.info(String.format("Import percentage: %d", percentage));
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		ImportOptionsModel options = createImportOptionsModel(PROJECT_ID, SHORT_CODE, PROJECT_NAME, PROCESSED,
			Collections.emptySet(), ALLOWED_TERM_ATTRIBUTE_TYPE);
		Map<String, String> termEntryAttributeReplacements = new HashMap<>(1);
		String oldTermEntryAttribute = "definition", newTermEntryAttribute = "Context";
		termEntryAttributeReplacements.put(oldTermEntryAttribute, newTermEntryAttribute);
		options.setTermEntryAttributeReplacements(termEntryAttributeReplacements);

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, options, SyncOption.OVERWRITE);
		Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
		Set<Description> descriptions = termEntry.getDescriptions();

		assertTrue(languageTerms.containsKey(Locale.ENGLISH.getCode()));

		assertTrue(descriptions.stream().map(Description::getType)
			.allMatch(Predicate.isEqual(newTermEntryAttribute)));
	    }
	};

	final URL resource = getResourceFrom("Skype.xls");

	TermEntryReaderConfig.Builder readerConfig = new TermEntryReaderConfig.Builder();
	readerConfig.stream(Files.newInputStream(Paths.get(resource.toURI())));
	readerConfig.userProjectLanguages(Arrays.asList(Locale.ENGLISH.getCode(), Locale.GERMANY.getCode()));
	readerConfig.encoding(Charsets.UTF_8.name());

	TermEntryReader reader = new DefaultXlsTermEntryReader(readerConfig.build());
	reader.readTermEntries(importCallback);
    }

    @Test
    public void testReplaceTermNote() throws Exception {

	ImportCallback importCallback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 1;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
		LOG.info(String.format("Import percentage: %d", percentage));
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		ImportOptionsModel options = createImportOptionsModel(PROJECT_ID, SHORT_CODE, PROJECT_NAME, PROCESSED,
			Collections.emptySet(), ALLOWED_TERM_ATTRIBUTE_TYPE);
		Map<String, Map<String, String>> attributeNoteReplacements = new HashMap<>(1);
		Map<String, String> noteReplacements = new HashMap<>(1);
		String oldTermNote = "Part of speech", newTermNote = "Corporate";
		noteReplacements.put(oldTermNote, newTermNote);
		attributeNoteReplacements.put(Description.NOTE, noteReplacements);

		options.setAttributeNoteReplacements(attributeNoteReplacements);

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, options, SyncOption.OVERWRITE);

		assertTrue(termEntry.ggetAllTerms().stream().map(Term::getDescriptions)
			.filter(CollectionUtils::isNotEmpty).flatMap(Set::stream)
			.filter(description -> description.getBaseType().equals(Description.NOTE))
			.map(Description::getType).allMatch(Predicate.isEqual(newTermNote)));
	    }
	};

	List<String> userProjectLanguages = new ArrayList<>(3);
	userProjectLanguages.add(Locale.US.getCode());
	userProjectLanguages.add(Locale.GERMANY.getCode());
	userProjectLanguages.add(Locale.FRANCE.getCode());

	final URL resource = getResourceFrom("Nikon.xls");

	TermEntryReaderConfig.Builder readerConfig = new TermEntryReaderConfig.Builder();
	readerConfig.stream(Files.newInputStream(Paths.get(resource.toURI())));
	readerConfig.userProjectLanguages(userProjectLanguages);
	readerConfig.encoding(Charsets.UTF_8.name());

	TermEntryReader reader = new DefaultXlsTermEntryReader(readerConfig.build());
	reader.readTermEntries(importCallback);
    }

    private ImportOptionsModel createImportOptionsModel(Long projectId, String projectShortCode, String projectName,
	    String status, Set<String> allowedTermEntryAttributeType, String allowedTermAttributeType) {
	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setProjectId(projectId);
	importOptions.setProjectShortCode(projectShortCode);
	importOptions.setProjectName(projectName);
	importOptions.setStatus(status);

	Set<String> termAttributeNames = new HashSet<>();
	termAttributeNames.add(allowedTermAttributeType);

	Map<String, Set<String>> allowedTermDescriptions = new HashMap<>(2);
	allowedTermDescriptions.put(Description.ATTRIBUTE, termAttributeNames);
	allowedTermDescriptions.put(Description.NOTE, Collections.emptySet());

	importOptions.setAllowedTermDescriptions(allowedTermDescriptions);
	importOptions.setAllowedTermEntryAttributes(allowedTermEntryAttributeType);

	return importOptions;
    }

    private TermEntryReader createImporter(String xls, String syncLanguage) throws FileNotFoundException {
	InputStream input = new FileInputStream(xls);
	String encoding = "UTF-8";

	List<String> importingLanguageIds = new ArrayList<String>();
	importingLanguageIds.add("en-US");
	importingLanguageIds.add("en");
	importingLanguageIds.add("de");
	importingLanguageIds.add("de-DE");
	importingLanguageIds.add("fr-FR");
	importingLanguageIds.add("fr");
	importingLanguageIds.add("es-ES");
	importingLanguageIds.add("es");

	TermEntryReader reader = new DefaultXlsTermEntryReader(new TermEntryReaderConfig.Builder().stream(input)
		.encoding(encoding).userProjectLanguages(importingLanguageIds).build());
	return reader;
    }

    protected URL getResourceFrom(final String pathname) {
	Class<DefaultXlsReaderImportTest> clazz = DefaultXlsReaderImportTest.class;
	URL resource = clazz.getClassLoader().getResource(Paths.get(PARENT_DIRECTORY, pathname).toString());
	return resource;
    }
}

package org.gs4tr.termmanager.service.xls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation3.callback.ImportCallback;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.mocking.AbstractServiceTest;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Arrays;

public class XlsImportTermNoteColumnReaderTest extends AbstractServiceTest {

    private static final String ATTRIBUTE = Description.ATTRIBUTE;

    private static final String DE_LANGUAGE_ID = "de-DE";

    private static final String ENCODING = "ISO-8859-1";

    private static final String EN_LANGUAGE_ID = "en-US";

    private static final String EN_NOTE_VALUE = "English note value";

    private static final String ERROR_MESSAGE = "Invalid language ID.";

    private static final String FR_LANGUAGE_ID = "fr-FR";

    private static final String FR_NOTE_VALUE = "French note value";

    private static final String GERMANY_CONTEXT_VALUE = "Context value";

    private static final String GERMANY_NOTE_VALUE = "Germany note value";

    private static final String NOTE = Description.NOTE;

    private static final String PROCESSED = "PROCESSED";

    private static final Long PROJECT_ID = 1L;

    private static final String PROJECT_NAME = "Skype";

    private static final String SHORT_CODE = "SKY000001";

    private static final String TEST_FILES_PATH = "src/test/resources/xls/filesWithTermNoteColumn/";

    @Test
    public void termNoteColumnReaderTest_01() throws FileNotFoundException {

	TermEntryReader importer = createImporter(TEST_FILES_PATH.concat("testCase1.xls"),
		new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID });

	ImportCallback callback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 2;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		ImportOptionsModel importOptions = createImportOptionsModel();

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, importOptions, SyncOption.OVERWRITE);

		List<Term> languageTerms = termEntry.ggetTerms();

		assertTrue(CollectionUtils.isNotEmpty(languageTerms));

		Description description = null;

		for (Term languageTerm : languageTerms) {
		    Set<Description> descriptions = languageTerm.getDescriptions();
		    String termName = languageTerm.getName();

		    switch (languageTerm.getLanguageId()) {
		    case EN_LANGUAGE_ID:
			assertEquals("Unlimited Country", termName);
			assertTrue(CollectionUtils.isNotEmpty(descriptions));
			assertEquals(1, descriptions.size());

			description = findDescriptionWithValue(EN_NOTE_VALUE, NOTE, descriptions);

			assertNotNull(description);
			break;
		    case DE_LANGUAGE_ID:
			assertEquals("Länder-Package", termName);
			assertTrue(CollectionUtils.isNotEmpty(descriptions));
			assertEquals(1, descriptions.size());

			description = findDescriptionWithValue(GERMANY_NOTE_VALUE, NOTE, descriptions);

			assertNotNull(description);
			break;
		    default:
			throw new IllegalArgumentException(ERROR_MESSAGE);
		    }
		}
	    }
	};
	importer.readTermEntries(callback);
    }

    @Test
    public void termNoteColumnReaderTest_02() throws FileNotFoundException {
	TermEntryReader importer = createImporter(TEST_FILES_PATH.concat("testCase2.xls"),
		new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID });

	ImportCallback callback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 3;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		ImportOptionsModel importOptions = createImportOptionsModel();

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, importOptions, SyncOption.OVERWRITE);

		List<Term> languageTerms = termEntry.ggetTerms();
		Set<Description> attributes = termEntry.getDescriptions();

		assertTrue(CollectionUtils.isNotEmpty(languageTerms));
		assertTrue(CollectionUtils.isNotEmpty(attributes));
		assertEquals(3, attributes.size());

		Description description = null;

		for (Term languageTerm : languageTerms) {
		    Set<Description> descriptions = languageTerm.getDescriptions();
		    String termName = languageTerm.getName();

		    switch (languageTerm.getLanguageId()) {
		    case EN_LANGUAGE_ID:
			assertEquals("Unlimited Country", termName);
			assertTrue(CollectionUtils.isNotEmpty(descriptions));
			assertEquals(1, descriptions.size());

			description = findDescriptionWithValue(EN_NOTE_VALUE, NOTE, descriptions);

			assertNotNull(description);
			break;
		    case DE_LANGUAGE_ID:
			assertEquals("Länder-Package", termName);
			assertTrue(CollectionUtils.isNotEmpty(descriptions));
			assertEquals(1, descriptions.size());

			description = findDescriptionWithValue(GERMANY_CONTEXT_VALUE, ATTRIBUTE, descriptions);

			assertNotNull(description);
			break;
		    case FR_LANGUAGE_ID:
			assertEquals("Illimité Pays", termName);
			assertTrue(CollectionUtils.isNotEmpty(descriptions));
			assertEquals(1, descriptions.size());

			description = findDescriptionWithValue(FR_NOTE_VALUE, NOTE, descriptions);

			assertNotNull(description);

			break;
		    default:
			throw new IllegalArgumentException(ERROR_MESSAGE);
		    }
		}
	    }
	};
	importer.readTermEntries(callback);
    }

    @Test
    public void termNoteColumnReaderTest_03() throws FileNotFoundException {
	TermEntryReader importer = createImporter(TEST_FILES_PATH.concat("testCase3.xls"),
		new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID });

	ImportCallback callback = new ImportCallback() {

	    @Override
	    public int getTotalTerms() {
		return 3;
	    }

	    @Override
	    public void handlePercentage(int percentage) {
	    }

	    @Override
	    public void handleTermEntry(Object item) {
		ImportOptionsModel importOptions = createImportOptionsModel();

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, importOptions, SyncOption.OVERWRITE);

		List<Term> languageTerms = termEntry.ggetTerms();
		assertTrue(CollectionUtils.isNotEmpty(languageTerms));

		Description description = null;

		for (Term languageTerm : languageTerms) {
		    String termName = languageTerm.getName();
		    Set<Description> descriptions = languageTerm.getDescriptions();

		    switch (languageTerm.getLanguageId()) {
		    case EN_LANGUAGE_ID:
			assertEquals("Unlimited Country", termName);
			assertTrue(CollectionUtils.isNotEmpty(descriptions));
			assertEquals(1, descriptions.size());
			description = findDescriptionWithValue(EN_NOTE_VALUE, NOTE, descriptions);
			assertNotNull(description);
			break;
		    case DE_LANGUAGE_ID:
			assertEquals("Länder-Package", termName);
			assertTrue(CollectionUtils.isNotEmpty(descriptions));
			assertEquals(2, descriptions.size());

			description = findDescriptionWithValue("Context value1", ATTRIBUTE, descriptions);
			assertNotNull(description);

			description = findDescriptionWithValue("Context value2", ATTRIBUTE, descriptions);
			assertNotNull(description);
			break;
		    case FR_LANGUAGE_ID:
			assertEquals("Illimité Pays", termName);
			assertTrue(CollectionUtils.isNotEmpty(descriptions));
			assertEquals(3, descriptions.size());

			description = findDescriptionWithValue("PartOfSpeach1", NOTE, descriptions);

			assertNotNull(description);

			description = findDescriptionWithValue("PartOfSpeach2", NOTE, descriptions);

			assertNotNull(description);

			description = findDescriptionWithValue("PartOfSpeach3", NOTE, descriptions);

			assertNotNull(description);
			break;
		    default:
			throw new IllegalArgumentException(ERROR_MESSAGE);
		    }
		}
	    }
	};
	importer.readTermEntries(callback);

    }

    private ImportOptionsModel createImportOptionsModel() {
	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setProjectShortCode(SHORT_CODE);
	importOptions.setProjectName(PROJECT_NAME);
	importOptions.setProjectId(PROJECT_ID);
	importOptions.setStatus(PROCESSED);

	Set<String> termEntryAttributeNames = new HashSet<>();
	termEntryAttributeNames.add("definition");

	Set<String> termAttributeNames = new HashSet<>();
	termAttributeNames.add("context");

	Set<String> termNoteNames = new HashSet<>();
	termNoteNames.add("Usage");
	termNoteNames.add("PartOfSpeach");

	Map<String, Set<String>> allowedTermDescriptions = new HashMap<>(2);
	allowedTermDescriptions.put(Description.ATTRIBUTE, termAttributeNames);
	allowedTermDescriptions.put(Description.NOTE, termNoteNames);

	importOptions.setAllowedTermDescriptions(allowedTermDescriptions);
	importOptions.setAllowedTermEntryAttributes(termEntryAttributeNames);

	return importOptions;
    }

    @SuppressWarnings("unchecked")
    private TermEntryReader createImporter(String xls, String[] importingLanguageIds) throws FileNotFoundException {
	TermEntryReaderConfig config = new TermEntryReaderConfig.Builder().stream(new FileInputStream(xls))
		.encoding(ENCODING).userProjectLanguages(Arrays.asList(importingLanguageIds)).build();

	TermEntryReader importer = new DefaultXlsTermEntryReader(config);
	return importer;
    }

    private Description findDescriptionWithValue(String value, String baseType, Set<Description> descriptions) {
	if (CollectionUtils.isNotEmpty(descriptions)) {
	    for (Description description : descriptions) {
		if (value.equals(description.getValue()) && baseType.equals(description.getBaseType())) {
		    return description;
		}
	    }
	}
	return null;
    }
}

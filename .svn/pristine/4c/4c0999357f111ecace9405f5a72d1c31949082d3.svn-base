package org.gs4tr.termmanager.service.xls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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

public class XlsImportWithStatusColumnReaderTest extends AbstractServiceTest {

    private static final String ATTRIBUTE_TYPE = "context";

    private static final String BLACKLISTED = "BLACKLISTED";

    private static final String DE_LANGUAGE_ID = "de-DE";

    private static final String ENCODING = "ISO-8859-1";

    private static final String EN_LANGUAGE_ID = "en-US";

    private static final String FR_LANGUAGE_ID = "fr-FR";

    private static final String INVALID_LANGUAGE_ID = "Invalid language ID.";

    private static final String IN_FINAL_REVIEW = "INFINALREVIEW";

    private static final String IN_TRANSLATION_REVIEW = "INTRANSLATIONREVIEW";

    private static final String PROCESSED = "PROCESSED";

    private static final Long PROJECT_ID = 1L;

    private static final String PROJECT_NAME = "Skype";

    private static final String SHORT_CODE = "SKY000001";

    private static final String TEST_FILES_PATH = "src/test/resources/xls/FilesWithStatusColumn/";

    private static final String WAITING = "WAITING";

    /*
     * TEST CASE: en-US status column: blank, de-DE status column: "Blacklisted"
     */
    @Test
    public void xlsImportWithStatusColumnTest_01() throws FileNotFoundException {

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
		ImportOptionsModel options = createImportOptionsModel(PROJECT_ID, PROJECT_NAME, SHORT_CODE, PROCESSED,
			ATTRIBUTE_TYPE);

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, options, SyncOption.OVERWRITE);

		List<Term> languageTerms = termEntry.ggetTerms();

		assertTrue(CollectionUtils.isNotEmpty(languageTerms));

		for (Term languageTerm : languageTerms) {
		    String termName = languageTerm.getName();
		    switch (languageTerm.getLanguageId()) {
		    case EN_LANGUAGE_ID:
			assertEquals("Unlimited Country", termName);
			assertEquals(PROCESSED, languageTerm.getStatus());
			break;
		    case DE_LANGUAGE_ID:
			assertEquals("Länder-Package", termName);
			assertEquals(BLACKLISTED, languageTerm.getStatus());
			break;
		    default:
			throw new IllegalArgumentException(INVALID_LANGUAGE_ID);
		    }
		}
	    }
	};
	importer.readTermEntries(callback);
    }

    /*
     * TEST CASE: en-US status column: does not exist, de-DE status column:
     * "Pending Approval", fr-FR status column: "Blacklisted"
     */
    @Test
    public void xlsImportWithStatusColumnTest_02() throws FileNotFoundException {

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
		ImportOptionsModel options = createImportOptionsModel(PROJECT_ID, PROJECT_NAME, SHORT_CODE, PROCESSED,
			ATTRIBUTE_TYPE);

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, options, SyncOption.OVERWRITE);

		List<Term> languageTerms = termEntry.ggetTerms();

		assertTrue(CollectionUtils.isNotEmpty(languageTerms));

		for (Term languageTerm : languageTerms) {
		    String termName = languageTerm.getName();
		    Set<Description> descriptions = languageTerm.getDescriptions();
		    switch (languageTerm.getLanguageId()) {
		    case EN_LANGUAGE_ID:
			assertEquals("Unlimited Country", termName);
			assertEquals(PROCESSED, languageTerm.getStatus());
			assertNull(descriptions);
			break;
		    case DE_LANGUAGE_ID:
			assertEquals("Länder-Package", termName);
			assertEquals(WAITING, languageTerm.getStatus());
			assertNull(descriptions);

			break;
		    case FR_LANGUAGE_ID:
			assertEquals("Illimité Pays", termName);
			assertEquals(BLACKLISTED, languageTerm.getStatus());
			assertNull(descriptions);
			break;

		    default:
			throw new IllegalArgumentException(INVALID_LANGUAGE_ID);
		    }
		}
	    }
	};
	importer.readTermEntries(callback);
    }

    /*
     * TEST CASE: en-US status column: "APPROVED", de-DE status column:
     * "PeNDing ApprOval", de-DE1 status column: "blacklisted".
     */
    @Test
    public void xlsImportWithStatusColumnTest_03() throws FileNotFoundException {

	TermEntryReader importer = createImporter(TEST_FILES_PATH.concat("testCase3.xls"),
		new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID });

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
		ImportOptionsModel options = createImportOptionsModel(PROJECT_ID, PROJECT_NAME, SHORT_CODE, WAITING,
			ATTRIBUTE_TYPE);

		/* Status chosen in import dialog is "Pending Approval" */
		TermEntry termEntry = TermEntryUtils.createTermEntry(item, options, SyncOption.OVERWRITE);
		/*
		 * NOTE: TMGR import should permit any lower and upper case combination of the
		 * status text (e.g. "APPROVED", "PeNDing ApprOval", blacklisted). This warning
		 * should come before submitted terms are identified.
		 */

		List<Term> languageTerms = termEntry.ggetTerms();

		assertTrue(CollectionUtils.isNotEmpty(languageTerms));

		for (Term languageTerm : languageTerms) {
		    String termName = languageTerm.getName();
		    switch (languageTerm.getLanguageId()) {
		    case EN_LANGUAGE_ID:
			assertEquals("Unlimited Country", termName);
			assertEquals(PROCESSED, languageTerm.getStatus());
			break;
		    case DE_LANGUAGE_ID:
			/* Valide status for Germman term and synonym */
			if (termName.equals("Länder-Package")) {
			    assertEquals(WAITING, languageTerm.getStatus());
			    break;
			} else {
			    assertEquals("Länder-Package synonym", termName);
			    assertEquals(BLACKLISTED, languageTerm.getStatus());
			    /* Validate that synonym have attribute also */
			    assertTrue(CollectionUtils.isNotEmpty(languageTerm.getDescriptions()));
			    break;
			}
		    default:
			throw new IllegalArgumentException(INVALID_LANGUAGE_ID);
		    }
		}
	    }
	};
	importer.readTermEntries(callback);
    }

    /*
     * TEST CASE: en-US status column: "Approved", de-DE status column:
     * "In Final Review", fr-FR status column: "In Translation/Review"
     */
    @Test
    public void xlsImportWithStatusColumnTest_04() throws FileNotFoundException {

	TermEntryReader importer = createImporter(TEST_FILES_PATH.concat("testCase4.xls"),
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
		ImportOptionsModel options = createImportOptionsModel(PROJECT_ID, PROJECT_NAME, SHORT_CODE, WAITING,
			ATTRIBUTE_TYPE);

		/* Status chosen in import dialog is "Pending Approval" */
		TermEntry termEntry = TermEntryUtils.createTermEntry(item, options, SyncOption.OVERWRITE);

		List<Term> languageTerms = termEntry.ggetTerms();

		assertTrue(CollectionUtils.isNotEmpty(languageTerms));

		for (Term languageTerm : languageTerms) {
		    String termName = languageTerm.getName();
		    switch (languageTerm.getLanguageId()) {
		    case EN_LANGUAGE_ID:
			assertEquals("Unlimited Country", termName);
			assertEquals(PROCESSED, languageTerm.getStatus());
			break;
		    case DE_LANGUAGE_ID:
			assertEquals("Länder-Package", termName);
			assertEquals(IN_FINAL_REVIEW, languageTerm.getStatus());
			break;
		    case FR_LANGUAGE_ID:
			assertEquals("Illimité Pays", termName);
			assertEquals(IN_TRANSLATION_REVIEW, languageTerm.getStatus());
			break;

		    default:
			throw new IllegalArgumentException(INVALID_LANGUAGE_ID);
		    }
		}
	    }
	};
	importer.readTermEntries(callback);
    }

    /*
     * TEST CASE: en-US status column: "Approved", de-DE status column:
     * "In Final Review", fr-FR status column: "In Translation/Review"
     */
    @Test
    public void xlsImportWithStatusColumnTest_05() throws FileNotFoundException {

	TermEntryReader importer = createImporter(TEST_FILES_PATH.concat("testCase5.xls"),
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
		ImportOptionsModel options = createImportOptionsModel(PROJECT_ID, PROJECT_NAME, SHORT_CODE, PROCESSED,
			ATTRIBUTE_TYPE);

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, options, SyncOption.MERGE);

		assertTrue(MapUtils.isNotEmpty(termEntry.getLanguageTerms()));
		/*
		 * NOTE: If MERGE option is selected, TermEntryID and status columns will be
		 * ignored if they exist in the file.
		 */
		for (Term languageTerm : termEntry.ggetTerms()) {
		    String termName = languageTerm.getName();
		    switch (languageTerm.getLanguageId()) {
		    case EN_LANGUAGE_ID:
			assertEquals("Unlimited Country", termName);
			assertEquals(PROCESSED, languageTerm.getStatus());
			break;
		    case DE_LANGUAGE_ID:
			assertEquals("Länder-Package", termName);
			assertEquals(PROCESSED, languageTerm.getStatus());
			break;
		    case FR_LANGUAGE_ID:
			assertEquals("Illimité Pays", termName);
			assertEquals(PROCESSED, languageTerm.getStatus());
			break;

		    default:
			throw new IllegalArgumentException(INVALID_LANGUAGE_ID);
		    }
		}
	    }
	};
	importer.readTermEntries(callback);
    }

    /*
     * TEST CASE: en-US status column: "Missing Translation", de-DE status column:
     * blank, fr-FR status column: "In Translation/Review"
     */
    @Test
    public void xlsImportWithStatusColumnTest_06() throws FileNotFoundException {

	TermEntryReader importer = createImporter(TEST_FILES_PATH.concat("testCase6.xls"),
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
		ImportOptionsModel options = createImportOptionsModel(PROJECT_ID, PROJECT_NAME, SHORT_CODE, PROCESSED,
			ATTRIBUTE_TYPE);

		TermEntry termEntry = TermEntryUtils.createTermEntry(item, options, SyncOption.OVERWRITE);

		assertTrue(MapUtils.isNotEmpty(termEntry.getLanguageTerms()));
		/*
		 * NOTE: In case contents of status column is blank or "Missing Translation",
		 * term will be imported with default status selected for import
		 */
		for (Term languageTerm : termEntry.ggetTerms()) {
		    String termName = languageTerm.getName();
		    switch (languageTerm.getLanguageId()) {
		    case EN_LANGUAGE_ID:
			assertEquals("Unlimited Country", termName);
			assertEquals(PROCESSED, languageTerm.getStatus());
			break;
		    case DE_LANGUAGE_ID:
			assertEquals("Länder-Package", termName);
			assertEquals(PROCESSED, languageTerm.getStatus());
			break;
		    case FR_LANGUAGE_ID:
			assertEquals("Illimité Pays", termName);
			assertEquals(IN_TRANSLATION_REVIEW, languageTerm.getStatus());
			break;

		    default:
			throw new IllegalArgumentException(INVALID_LANGUAGE_ID);
		    }
		}
	    }
	};
	importer.readTermEntries(callback);
    }

    private ImportOptionsModel createImportOptionsModel(Long projectId, String projectName, String shortCode,
	    String status, String attributeType) {
	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setProjectId(projectId);
	importOptions.setProjectShortCode(shortCode);
	importOptions.setProjectName(projectName);
	importOptions.setStatus(status);

	Set<String> attributeNames = new HashSet<>();
	attributeNames.add(attributeType);

	Map<String, Set<String>> allowedTermDescriptions = new HashMap<>(2);
	allowedTermDescriptions.put(Description.ATTRIBUTE, attributeNames);
	allowedTermDescriptions.put(Description.NOTE, Collections.emptySet());
	importOptions.setAllowedTermDescriptions(allowedTermDescriptions);

	return importOptions;
    }

    private TermEntryReader createImporter(String xls, String[] importingLanguageIds) throws FileNotFoundException {
	TermEntryReaderConfig readerConfig = new TermEntryReaderConfig.Builder().stream(new FileInputStream(xls))
		.encoding(ENCODING).userProjectLanguages(java.util.Arrays.asList(importingLanguageIds)).build();
	TermEntryReader reader = new DefaultXlsTermEntryReader(readerConfig);
	return reader;
    }

}

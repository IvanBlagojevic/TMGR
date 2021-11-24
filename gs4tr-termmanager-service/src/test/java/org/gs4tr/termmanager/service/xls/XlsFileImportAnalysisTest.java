package org.gs4tr.termmanager.service.xls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.callback.AbstractValidationCallback;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.service.AbstractSpringTermEntryReaderTest;
import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.gs4tr.termmanager.service.impl.ImportValidationCallback;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig;
import org.junit.Test;

import com.google.common.collect.Sets;

public class XlsFileImportAnalysisTest extends AbstractSpringTermEntryReaderTest {

    private static final String DEFINITION = "definition";
    private static final String DE_LANGUAGE_ID = "de-DE";
    private static final String EN_LANGUAGE_ID = "en-US";
    private static final String FR_LANGUAGE_ID = "fr-FR";

    private static final int NOTIFICATION_PER_TERMS = 1;

    private static final String TEST_FILES_PATH = "src/test/resources/xls/analysisTestFiles/";

    @Test
    public void comboValueAttributesDoesNotMatchTest() throws FileNotFoundException {
	Map<String, Set<String>> comboValuesPerAttribute = new HashMap<>(1);
	comboValuesPerAttribute.put("combo", Sets.newHashSet("combo value1", "combo value2"));

	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(TEST_FILES_PATH.concat("testCase15.xls")))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID))
		.comboValuesPerAttribute(comboValuesPerAttribute).build();

	TermEntryReader importer = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	importer.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert comboValueDoesNotMatchAlert = alerts.get(0);
	assertEquals(AlertSubject.ATTRIBUTE_CHECK, comboValueDoesNotMatchAlert.getSubject());
	assertEquals(AlertType.WARNING, comboValueDoesNotMatchAlert.getType());

	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.13"), "combo value",
		comboValuesPerAttribute.get("combo")), comboValueDoesNotMatchAlert.getMessage());
    }

    @Test
    public void hiddenColumnTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase19.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert hiddenColumnAlert = alerts.get(0);
	assertEquals(AlertSubject.HIDDEN_COLUMN, hiddenColumnAlert.getSubject());
	assertEquals(AlertType.ERROR, hiddenColumnAlert.getType());

	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.14"), 6),
		hiddenColumnAlert.getMessage());
    }

    @Test
    public void hiddenRowTest() throws FileNotFoundException {

	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase20.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert hiddenRowAlert = alerts.get(0);
	assertEquals(AlertSubject.HIDDEN_ROW, hiddenRowAlert.getSubject());
	assertEquals(AlertType.ERROR, hiddenRowAlert.getType());

	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.15"), 4),
		hiddenRowAlert.getMessage());

    }

    @Test
    public void hiddenWorksheetTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase17.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert hiddenWorksheetAlert = alerts.get(0);
	assertEquals(AlertSubject.HIDDEN_WORKSHEET, hiddenWorksheetAlert.getSubject());
	assertEquals(AlertType.ERROR, hiddenWorksheetAlert.getType());

	String expectedMessage = String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.0"), "Sheet2");
	assertEquals(expectedMessage, hiddenWorksheetAlert.getMessage());
    }

    @Test
    public void illegalStatusTest() throws FileNotFoundException {
	/*
	 * When content is "In Translation Review", "In Final Review" or
	 * "Missing Translation" the user will be informed that terms with these
	 * statuses will be skipped during import.
	 */
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase13.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	Set<String> termDescriptions = callback.getTermDescriptions();
	Set<String> termNotes = callback.getTermNotes();
	Map<String, Integer> languageMap = callback.getLanguageMap();

	assertTrue(MapUtils.isNotEmpty(languageMap));
	assertTrue(CollectionUtils.isNotEmpty(termDescriptions));
	assertTrue(callback.isIdColumnExist());

	assertEquals(2, languageMap.get(EN_LANGUAGE_ID).intValue());
	assertEquals(4, languageMap.get(DE_LANGUAGE_ID).intValue());
	assertEquals(2, languageMap.get(FR_LANGUAGE_ID).intValue());
	assertEquals(2, callback.getTotalTerms());
	assertEquals(1, termNotes.size());
	assertEquals(2, termDescriptions.size());
	assertEquals(1, callback.getTermEntryDescriptions().size());
	assertEquals(2, callback.getTermEntryAttributesCount().get(DEFINITION).intValue());
	assertEquals(0, callback.getTotalTermEntrySkipped());

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert statusInSubmissionAlert = alerts.get(0);
	assertEquals(AlertSubject.STATUS_CHECK, statusInSubmissionAlert.getSubject());
	assertEquals(AlertType.WARNING, statusInSubmissionAlert.getType());
	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.11"), "de-DE1:Status"),
		statusInSubmissionAlert.getMessage());
    }

    // Rainy Day Scenarios

    @Test
    public void invalidAttributeColumnFormatTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase3.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert invalidDelimiterAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, invalidDelimiterAlert.getSubject());
	assertEquals(AlertType.ERROR, invalidDelimiterAlert.getType());
	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.6"), "de-DE~context", 6),
		invalidDelimiterAlert.getMessage());
    }

    /*
     * [Improvement#TERII-4477]: Import analysis should inform users when their file
     * format is not XLS/XLSX/TBX
     */
    @Test
    public void invalidFileFormatTest() throws FileNotFoundException {
	TermEntryReaderConfig readerConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "invalidFileFormat.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(readerConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert invalidFileFormatAlert = alerts.get(0);

	assertEquals(AlertSubject.INVALID_FILE_FORMAT, invalidFileFormatAlert.getSubject());
	assertEquals(AlertType.ERROR, invalidFileFormatAlert.getType());
	assertEquals(MessageResolver.getMessage("AsposeFactory.0"), invalidFileFormatAlert.getMessage());
    }

    @Test
    public void invalidLocaleCodeInLanguageColumnTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase5.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert invalidLocaleCodeFormatAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, invalidLocaleCodeFormatAlert.getSubject());
	assertEquals(AlertType.ERROR, invalidLocaleCodeFormatAlert.getType());
	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.6"), "de-DEZ", 5),
		invalidLocaleCodeFormatAlert.getMessage());
    }

    @Test
    public void invalidLocaleCodeInStatusColumnTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase4.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert invalidColumnFormatAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, invalidColumnFormatAlert.getSubject());
	assertEquals(AlertType.ERROR, invalidColumnFormatAlert.getType());
	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.6"), "de-DZE:Status", 6),
		invalidColumnFormatAlert.getMessage());
    }

    @Test
    public void invalidNoteColumnFormatTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase7.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert invalidTermNoteFormatAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, invalidTermNoteFormatAlert.getSubject());
	assertEquals(AlertType.ERROR, invalidTermNoteFormatAlert.getType());

	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.8"), 7),
		invalidTermNoteFormatAlert.getMessage());
    }

    @Test
    public void invalidTermNoteColumnFormatTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase6.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert invalidTermNoteFormatAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, invalidTermNoteFormatAlert.getSubject());
	assertEquals(AlertType.ERROR, invalidTermNoteFormatAlert.getType());

	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.8"), 7),
		invalidTermNoteFormatAlert.getMessage());
    }

    @Test
    public void multipleStatusColumnWithTheSameLocaleCode() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase8.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert multipleStatusColumnsAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, multipleStatusColumnsAlert.getSubject());
	assertEquals(AlertType.ERROR, multipleStatusColumnsAlert.getType());

	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.10"), "de-DE1"),
		multipleStatusColumnsAlert.getMessage());
    }

    /*
     * Test case: There are multiple columns with identical TermEntryID header in
     * import file.
     */
    @Test
    public void multipleTermEntryIDColumnsTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase12.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert multipleTermEntryIDColumnsAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, multipleTermEntryIDColumnsAlert.getSubject());
	assertEquals(AlertType.ERROR, multipleTermEntryIDColumnsAlert.getType());
	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.3"),
		XlsConfiguration.TERM_ENTRY_ID), multipleTermEntryIDColumnsAlert.getMessage());
    }

    @Test
    public void multipleWorksheetsTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase18.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert multipleWorksheetsAlert = alerts.get(0);
	assertEquals(AlertSubject.MULTIPLE_WORKSHEETS, multipleWorksheetsAlert.getSubject());
	assertEquals(AlertType.WARNING, multipleWorksheetsAlert.getType());

	String expectedMessage = String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.1"),
		"[Sheet1, Sheet2]");
	assertEquals(expectedMessage, multipleWorksheetsAlert.getMessage());
    }

    /*
     * Test case10: If there is no matching term, the user should be notified of
     * this. [Applies to synonym].
     */
    @Test
    public void noMatchingMainTermForSynonymColumnTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase9.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert noMainColumnAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, noMainColumnAlert.getSubject());
	assertEquals(AlertType.ERROR, noMainColumnAlert.getType());

	String expectedMainLanguageCode = Locale.FRANCE.getCode();
	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.5"), expectedMainLanguageCode),
		noMainColumnAlert.getMessage());
    }

    @Test
    public void noMatchingTermForStatusColumnTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase10.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert noMatchingTermAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, noMatchingTermAlert.getSubject());
	assertEquals(AlertType.ERROR, noMatchingTermAlert.getType());
	assertEquals(
		String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.7"), "de-DE1", "de-DE1:Status"),
		noMatchingTermAlert.getMessage());
    }

    @Test
    public void noMatchingTermForTermNoteColumnTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase11.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert noMatchingTermAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, noMatchingTermAlert.getSubject());
	assertEquals(AlertType.ERROR, noMatchingTermAlert.getType());

	assertEquals(
		String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.9"), "fr-FR1", "fr-FR1:Note:Usage"),
		noMatchingTermAlert.getMessage());
    }

    /*
     * Test case: Analysis should return error alert if file have status, attribute,
     * note or synonym columns without the main language column.
     */
    @Test
    public void synonymStatusAttributeWithoutMainLanguageColumn() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase9.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert noMainColumnAlert = alerts.get(0);

	assertEquals(AlertSubject.HEADER_CHECK, noMainColumnAlert.getSubject());
	assertEquals(AlertType.ERROR, noMainColumnAlert.getType());

	String expectedMainLanguageCode = Locale.FRANCE.getCode();
	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.5"), expectedMainLanguageCode),
		noMainColumnAlert.getMessage());
    }

    /*
     * Test case: In case the content of status column is unknow, the user will be
     * informed that this field will be ignored and default status will be applied
     * instead "Your import has unknown status "In Trans PeNDing APPROVED
     * ". If you proceed with the import, default import status will be applied." .
     */
    @Test
    public void unknowStatusTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase16.xls").toString()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader importer = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	ImportValidationCallback callback = createValidationCallback(NOTIFICATION_PER_TERMS);
	importer.validate(callback);

	List<Alert> alerts = callback.getAlerts();
	assertEquals(1, alerts.size());

	Alert unknowStatusAlert = alerts.get(0);
	assertEquals(AlertSubject.STATUS_CHECK, unknowStatusAlert.getSubject());
	assertEquals(AlertType.WARNING, unknowStatusAlert.getType());

	assertEquals(String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.12"),
		"In Trans PeNDing APPROVED", "de-DE:Status"), unknowStatusAlert.getMessage());
    }

    // Sunny Day Scenarios
    @Test
    public void validateImportFileTest() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase1.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	AbstractValidationCallback analysisInfo = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(analysisInfo);

	Set<String> termDescriptions = analysisInfo.getTermDescriptions();

	Set<String> termNotes = analysisInfo.getTermNotes();

	Map<String, Integer> languageMap = analysisInfo.getLanguageMap();

	assertTrue(analysisInfo.isIdColumnExist());

	assertTrue(MapUtils.isNotEmpty(languageMap));

	assertTrue(CollectionUtils.isNotEmpty(termDescriptions));

	assertEquals(1, languageMap.get(EN_LANGUAGE_ID).intValue());

	assertEquals(1, languageMap.get(DE_LANGUAGE_ID).intValue());

	assertEquals(0, languageMap.get(FR_LANGUAGE_ID).intValue());

	assertEquals(1, termNotes.size());

	assertEquals(1, analysisInfo.getTotalTerms());

	assertEquals(1, termDescriptions.size());

	assertEquals(1, analysisInfo.getTermEntryDescriptions().size());

	assertEquals(0, analysisInfo.getTotalTermEntrySkipped());
    }

    @Test
    public void xlsFileImportAnalysis_02() throws FileNotFoundException {
	TermEntryReaderConfig termEntryReaderConfig = new TermEntryReaderConfig.Builder()
		.stream(new FileInputStream(Paths.get(TEST_FILES_PATH, "testCase2.xls").toFile()))
		.userProjectLanguages(Arrays.asList(EN_LANGUAGE_ID, DE_LANGUAGE_ID)).build();

	TermEntryReader reader = new DefaultXlsTermEntryReader(termEntryReaderConfig);

	AbstractValidationCallback analysisInfo = createValidationCallback(NOTIFICATION_PER_TERMS);
	reader.validate(analysisInfo);

	Set<String> termDescriptions = analysisInfo.getTermDescriptions();

	Set<String> termNotes = analysisInfo.getTermNotes();

	Map<String, Integer> attrCountMap = analysisInfo.getTermEntryAttributesCount();

	Map<String, Integer> languageMap = analysisInfo.getLanguageMap();

	assertTrue(MapUtils.isNotEmpty(languageMap));

	assertTrue(CollectionUtils.isNotEmpty(termDescriptions));

	assertTrue(analysisInfo.isIdColumnExist());

	assertEquals(1, languageMap.get(EN_LANGUAGE_ID).intValue());

	assertEquals(2, languageMap.get(DE_LANGUAGE_ID).intValue());

	assertEquals(2, analysisInfo.getTotalTerms());

	assertEquals(1, termNotes.size());

	assertEquals(1, termDescriptions.size());

	assertEquals(1, analysisInfo.getTermEntryDescriptions().size());

	assertEquals(1, attrCountMap.size());

	assertEquals(2, attrCountMap.get(DEFINITION).intValue());

	assertEquals(0, analysisInfo.getTotalTermEntrySkipped());
    }

    private ImportValidationCallback createValidationCallback(int notificationPerTerms) {
	ImportValidationCallback info = new ImportValidationCallback(notificationPerTerms) {
	    @Override
	    public void notifyError(Exception exception) {

	    }

	    @Override
	    public void notifyFatalError(Exception fatalError) {
		throw new RuntimeException(fatalError.getMessage(), fatalError);
	    }

	    @Override
	    public void notifyTermsValidated(int termCount) {
	    }

	    @Override
	    public void notifyWarning(Exception exception) {
	    }
	};
	return info;
    }
}

package org.gs4tr.termmanager.service.xls;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation3.callback.AbstractValidationCallback;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.service.impl.ImportValidationCallback;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig;
import org.junit.Test;

public class DefaultXlsReaderValidationTest {

    private static final String UTF_8 = "UTF-8";

    @Test
    public void testCase1() throws Exception {
	String xls = "src/test/resources/xls/test1.xls";

	InputStream input = new FileInputStream(xls);

	TermEntryReader importer = new DefaultXlsTermEntryReader(
		new TermEntryReaderConfig.Builder().stream(input).encoding(UTF_8).build());

	AbstractValidationCallback info = createValidationCallback();

	importer.validate(info);

	int totalTermEntries = info.getTotalTerms();
	Assert.assertEquals(2, totalTermEntries);

	Set<String> termDescriptions = info.getTermDescriptions();
	Assert.assertTrue(CollectionUtils.isEmpty(termDescriptions));

	Map<String, Integer> languageMap = info.getLanguageMap();
	Assert.assertEquals(10, languageMap.get("en").intValue());
	Assert.assertEquals(2, languageMap.get("de").intValue());
	Assert.assertEquals(2, languageMap.get("fr").intValue());

	int skipped = info.getTotalTermEntrySkipped();
	Assert.assertEquals(0, skipped);

	Assert.assertEquals(2, info.getTermEntryDescriptions().size());
    }

    @Test
    public void testCase2() throws Exception {
	String xls = "src/test/resources/xls/test2.xls";

	TermEntryReader importer = createImporter(xls);

	AbstractValidationCallback info = createValidationCallback();

	importer.validate(info);

	int totalTermEntries = info.getTotalTerms();
	Assert.assertEquals(2, totalTermEntries);

	Set<String> termDescriptions = info.getTermDescriptions();
	Assert.assertTrue(CollectionUtils.isEmpty(termDescriptions));

	Map<String, Integer> languageMap = info.getLanguageMap();
	Assert.assertEquals(4, languageMap.get("en-US").intValue());
	Assert.assertEquals(6, languageMap.get("en").intValue());
	Assert.assertEquals(2, languageMap.get("de").intValue());
	Assert.assertEquals(2, languageMap.get("fr-FR").intValue());

	int skipped = info.getTotalTermEntrySkipped();
	Assert.assertEquals(0, skipped);

	Assert.assertEquals(2, info.getTermEntryDescriptions().size());
    }

    @Test
    public void testCase3() throws Exception {
	String xls = "src/test/resources/xls/test3.xls";

	TermEntryReader importer = createImporter(xls);

	AbstractValidationCallback info = createValidationCallback();

	importer.validate(info);

	int totalTermEntries = info.getTotalTerms();
	Assert.assertEquals(2, totalTermEntries);

	Set<String> termDescriptions = info.getTermDescriptions();
	Assert.assertTrue(CollectionUtils.isEmpty(termDescriptions));

	Map<String, Integer> languageMap = info.getLanguageMap();
	Assert.assertEquals(8, languageMap.get("en-US").intValue());
	Assert.assertEquals(2, languageMap.get("de").intValue());
	Assert.assertEquals(2, languageMap.get("fr-FR").intValue());
	Assert.assertEquals(2, languageMap.get("en").intValue());

	int skipped = info.getTotalTermEntrySkipped();
	Assert.assertEquals(0, skipped);

	Assert.assertEquals(2, info.getTermEntryDescriptions().size());
    }

    @Test
    public void testCase4() throws Exception {
	String xls = "src/test/resources/xls/test4.xls";

	TermEntryReader importer = createImporter(xls);

	AbstractValidationCallback info = createValidationCallback();

	importer.validate(info);

	int totalTermEntries = info.getTotalTerms();
	Assert.assertEquals(2, totalTermEntries);

	Set<String> termDescriptions = info.getTermDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(termDescriptions));
	Assert.assertEquals(1, termDescriptions.size());

	Map<String, Integer> languageMap = info.getLanguageMap();
	Assert.assertEquals(8, languageMap.get("en-US").intValue());
	Assert.assertEquals(2, languageMap.get("de").intValue());
	Assert.assertEquals(2, languageMap.get("fr-FR").intValue());
	Assert.assertEquals(2, languageMap.get("en").intValue());

	Map<String, Integer> languageAttributeMap = info.getLanguageAttributeMap();
	Assert.assertEquals(4, languageAttributeMap.get(Locale.US.getCode()).intValue());

	int skipped = info.getTotalTermEntrySkipped();
	Assert.assertEquals(0, skipped);
    }

    @Test
    public void testCase5() throws Exception {
	String xls = "src/test/resources/xls/test5.xls";

	TermEntryReader importer = createImporter(xls);

	AbstractValidationCallback info = createValidationCallback();

	importer.validate(info);

	int totalTermEntries = info.getTotalTerms();
	Assert.assertEquals(2, totalTermEntries);

	Set<String> termDescriptions = info.getTermDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(termDescriptions));
	Assert.assertEquals(1, termDescriptions.size());

	Map<String, Integer> languageMap = info.getLanguageMap();
	Assert.assertEquals(8, languageMap.get(Locale.US.getCode()).intValue());
	Assert.assertEquals(2, languageMap.get("de").intValue());
	Assert.assertEquals(2, languageMap.get("fr-FR").intValue());
	Assert.assertEquals(2, languageMap.get("en").intValue());

	Map<String, Integer> languageAttributeMap = info.getLanguageAttributeMap();
	Assert.assertEquals(6, languageAttributeMap.get(Locale.US.getCode()).intValue());

	int skipped = info.getTotalTermEntrySkipped();
	Assert.assertEquals(0, skipped);
    }

    @Test
    public void testCase6() throws Exception {
	String xls = "src/test/resources/xls/test5.xls";

	TermEntryReader importer = createImporter(xls);

	AbstractValidationCallback info = createValidationCallback();

	importer.validate(info);

	int totalTermEntries = info.getTotalTerms();
	Assert.assertEquals(2, totalTermEntries);

	Set<String> termDescriptions = info.getTermDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(termDescriptions));
	Assert.assertEquals(1, termDescriptions.size());

	Map<String, Integer> languageMap = info.getLanguageMap();
	Assert.assertEquals(8, languageMap.get(Locale.US.getCode()).intValue());
	Assert.assertEquals(2, languageMap.get("de").intValue());
	Assert.assertEquals(2, languageMap.get("fr-FR").intValue());
	Assert.assertEquals(2, languageMap.get("en").intValue());

	Map<String, Integer> languageAttributeMap = info.getLanguageAttributeMap();
	Assert.assertEquals(6, languageAttributeMap.get(Locale.US.getCode()).intValue());

	int skipped = info.getTotalTermEntrySkipped();
	Assert.assertEquals(0, skipped);
    }

    @Test
    public void testCase7() throws IOException {
	/*
	 * TERII-4276: Not able to import terms with 3 word locales like
	 * [sr-RS-CYRL1, ja-JP-JP1].
	 */
	try (InputStream stream = new FileInputStream("src/test/resources/xls/3word-locales-test.xls")) {
	    TermEntryReaderConfig config = new TermEntryReaderConfig.Builder().stream(stream).encoding(UTF_8).build();
	    TermEntryReader termEntryReader = new DefaultXlsTermEntryReader(config);
	    AbstractValidationCallback callback = createValidationCallback();
	    termEntryReader.validate(callback);

	    Map<String, Integer> countPerLanguage = callback.getLanguageMap();
	    assertEquals(2, countPerLanguage.size());

	    Integer jaCount = countPerLanguage.get(Locale.get("ja-JP-JP").getCode());
	    assertEquals(2, jaCount.intValue());

	    Integer srCount = countPerLanguage.get(Locale.get("sr-RS-CYRL").getCode());
	    assertEquals(2, srCount.intValue());
	}
    }

    private TermEntryReader createImporter(String xls) throws FileNotFoundException {
	InputStream input = new FileInputStream(xls);
	String encoding = UTF_8;

	List<String> importingLanguageIds = new ArrayList<String>();
	importingLanguageIds.add("en-US");
	importingLanguageIds.add("en");
	importingLanguageIds.add("de");
	importingLanguageIds.add("de-DE");
	importingLanguageIds.add("fr-FR");
	importingLanguageIds.add("fr");
	importingLanguageIds.add("es-ES");
	importingLanguageIds.add("es");

	TermEntryReaderConfig config = new TermEntryReaderConfig.Builder().stream(input).encoding(encoding)
		.userProjectLanguages(importingLanguageIds).build();
	TermEntryReader importer = new DefaultXlsTermEntryReader(config);
	return importer;
    }

    private AbstractValidationCallback createValidationCallback() {
	ImportValidationCallback info = new ImportValidationCallback(1) {
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

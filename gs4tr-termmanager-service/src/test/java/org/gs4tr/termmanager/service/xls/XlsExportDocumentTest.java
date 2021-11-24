package org.gs4tr.termmanager.service.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandlerUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class XlsExportDocumentTest {

    private static final String ID_1 = UUID.randomUUID().toString();

    private static final String ID_2 = UUID.randomUUID().toString();

    private List<String> _synonymCodes;

    private List<TermEntry> _termEntries;

    private List<String> expectedRow1;

    private List<String> expectedRow2;

    private File file;

    private final String status = "Approved";

    private String termEntryAttribute;

    private final String termEntryAttribute2 = "this second entry attribute";

    @After
    public void cleanUp() {
	if (Objects.nonNull(file)) {
	    ManualTaskHandlerUtils.forceDeleteFile(file);
	}
    }

    @Before
    public void setUp() {
	_termEntries = new ArrayList<>();
	_synonymCodes = new ArrayList<>();
	termEntryAttribute = new StringBuilder().append("this is a attribute").append(XlsHelper.DELIMITER)
		.append("this is a just one more").toString();

	// languages
	String en = "en";
	String de = "de";
	String fr = "fr";

	// description types
	String att1 = "definition";
	String termAtt = "context";
	String termNote = "partOfSpeach";

	// first termEntry
	Term enTerm = createTerm(en, "dog", new Description(termAtt, "en ker"), new Description(termNote, "en noun"));
	Term deTerm = createTerm(de, "Hund", new Description(termAtt, "de ker"), new Description(termNote, "de noun"));
	Term frTerm = createTerm(fr, "chien", new Description(termAtt, "fr ker"), new Description(termNote, "fr noun"));

	TermEntry termEntry1 = createTermEntry(ID_1, enTerm, deTerm, frTerm);
	termEntry1.addDescription(new Description(att1, "this is a attribute"));
	termEntry1.addDescription(new Description(att1, "this is a just one more"));

	// second termEntry
	Term enSynonym = createTerm(en, "puppy", new Description(termAtt, "en puppy"),
		new Description(termNote, "puppy noun"));

	TermEntry termEntry2 = createTermEntry(ID_2, enTerm, deTerm, frTerm, enSynonym);
	termEntry2.addDescription(new Description(att1, "this second entry attribute"));

	_termEntries.add(termEntry1);
	_termEntries.add(termEntry2);
    }

    @Test
    public void testSortXlsColumns() throws Exception {
	/* Both term-entries have one synonym */

	// languages
	String en = "en";
	String de = "de";

	// description types
	String att1 = "definition";
	String termAtt = "context";
	String termNote = "partOfSpeach";

	Term enSynonym = createTerm(en, "puppy synonym", new Description(termAtt, "en puppy desc"),
		new Description(termNote, "puppy noun"));

	Term deSynonym = createTerm(de, "de hund synonym", new Description(termAtt, "de ker desc"),
		new Description(termNote, "de noun"));

	TermEntry termEntry1 = getTermEntries().get(0);
	termEntry1.addTerm(enSynonym);
	termEntry1.addTerm(deSynonym);

	TermEntry termEntry2 = getTermEntries().get(1);
	termEntry2.addTerm(deSynonym);

	_synonymCodes.add("en1");
	_synonymCodes.add("de1");

	String fileName = "testSortXlsColumns.xlsx";
	String filePath = "target/".concat(fileName);
	file = new File(filePath);
	OutputStream out = new FileOutputStream(file);

	List<String> languagesToExport = new LinkedList<>();
	languagesToExport.add(en);
	languagesToExport.add(de);

	List<String> termEntryAttributes = new LinkedList<>();
	termEntryAttributes.add(att1);

	List<String> termAttributes = new LinkedList<>();
	termAttributes.add(termAtt);

	List<String> termNotes = new LinkedList<>();
	termNotes.add(termNote);

	// search request
	TermEntrySearchRequest request = new TermEntrySearchRequest();
	request.setLanguagesToExport(languagesToExport);
	request.setTermEntriesAttributes(termEntryAttributes);
	request.setTermAttributes(termAttributes);
	request.setTermNotes(termNotes);

	// export
	export(fileName, out, request);

	// validate (en,en1,de,de1)
	expectedRow1 = Arrays.asList(ID_1, termEntryAttribute, "dog", status, "en ker", "en noun", "puppy synonym",
		status, "en puppy desc", "puppy noun", "Hund", status, "de ker", "de noun", "de hund synonym", status,
		"de ker desc", "de noun");

	expectedRow2 = Arrays.asList(ID_2, termEntryAttribute2, "dog", status, "en ker", "en noun", "puppy", status,
		"en puppy", "puppy noun", "Hund", status, "de ker", "de noun", "de hund synonym", status, "de ker desc",
		"de noun");

	exportValidation(file, languagesToExport, termEntryAttributes, termAttributes, termNotes);
    }

    @Test
    public void testSortXlsWithSameCountryCode() throws Exception {
	// languages
	String en = "en";
	String enUS = "en-US";

	// description types
	String att1 = "definition";
	String termAtt = "context";
	String termNote = "partOfSpeach";

	Term enSynonym = createTerm(en, "puppy synonym", new Description(termAtt, "en puppy desc"),
		new Description(termNote, "puppy noun"));

	Term enUSterm1 = createTerm(enUS, "en-US puppy synonym", new Description(termAtt, "en-US puppy desc"),
		new Description(termNote, "en-US puppy noun synonym"));

	TermEntry termEntry1 = getTermEntries().get(0);
	termEntry1.addTerm(enSynonym);
	termEntry1.addTerm(enUSterm1);

	Term enUSterm2 = createTerm(enUS, "en-US puppy", new Description(termAtt, "en-US puppy desc"),
		new Description(termNote, "en-US puppy noun"));

	Term enUSSynonym = createTerm(enUS, "en-US hund synonym", new Description(termAtt, "en-US ker desc"),
		new Description(termNote, "en-US noun"));

	TermEntry termEntry2 = getTermEntries().get(1);
	termEntry2.addTerm(enUSterm2);
	termEntry2.addTerm(enUSSynonym);

	_synonymCodes.add("en1");
	_synonymCodes.add("en-US1");

	String fileName = "testSortXlsSameCountryCode.xlsx";
	String filePath = "target/".concat(fileName);
	file = new File(filePath);
	OutputStream out = new FileOutputStream(file);

	List<String> languagesToExport = new LinkedList<>();
	languagesToExport.add(en);
	languagesToExport.add(enUS);

	List<String> termEntryAttributes = new LinkedList<>();
	termEntryAttributes.add(att1);

	List<String> termAttributes = new LinkedList<>();
	termAttributes.add(termAtt);

	List<String> termNotes = new LinkedList<>();
	termNotes.add(termNote);

	// search request
	TermEntrySearchRequest request = new TermEntrySearchRequest();
	request.setLanguagesToExport(languagesToExport);
	request.setTermEntriesAttributes(termEntryAttributes);
	request.setTermAttributes(termAttributes);
	request.setTermNotes(termNotes);

	// export
	export(fileName, out, request);

	// validate (en,en1,en-US,en-US1)
	expectedRow1 = Arrays.asList(ID_1, termEntryAttribute, "dog", status, "en ker", "en noun", "puppy synonym",
		status, "en puppy desc", "puppy noun", "en-US puppy synonym", status, "en-US puppy desc",
		"en-US puppy noun synonym", "", "", "", "");

	expectedRow2 = Arrays.asList(ID_2, termEntryAttribute2, "dog", status, "en ker", "en noun", "puppy", status,
		"en puppy", "puppy noun", "en-US puppy", status, "en-US puppy desc", "en-US puppy noun",
		"en-US hund synonym", status, "en-US ker desc", "en-US noun");

	exportValidation(file, languagesToExport, termEntryAttributes, termAttributes, termNotes);
    }

    @Test
    public void testSortXlsWithTwoSynonyms() throws Exception {
	// languages
	String en = "en";
	String de = "de";

	// description types
	String att1 = "definition";
	String termAtt = "context";
	String termNote = "partOfSpeach";

	Term enSynonym1 = createTerm(en, "puppy synonym1", new Description(termAtt, "en puppy desc1"),
		new Description(termNote, "puppy noun1"));
	Term enSynonym2 = createTerm(en, "puppy synonym2", new Description(termAtt, "en puppy desc2"),
		new Description(termNote, "puppy noun2"));

	TermEntry termEntry1 = getTermEntries().get(0);
	termEntry1.addTerm(enSynonym1);
	termEntry1.addTerm(enSynonym2);

	_synonymCodes.add("en1");
	_synonymCodes.add("en2");

	String fileName = "testSortXlsTwoSynonyms.xlsx";
	String filePath = "target/".concat(fileName);
	file = new File(filePath);
	OutputStream out = new FileOutputStream(file);

	List<String> languagesToExport = new LinkedList<>();
	languagesToExport.add(en);
	languagesToExport.add(de);

	List<String> termEntryAttributes = new LinkedList<>();
	termEntryAttributes.add(att1);

	List<String> termAttributes = new LinkedList<>();
	termAttributes.add(termAtt);

	List<String> termNotes = new LinkedList<>();
	termNotes.add(termNote);

	// search request
	TermEntrySearchRequest request = new TermEntrySearchRequest();
	request.setLanguagesToExport(languagesToExport);
	request.setTermEntriesAttributes(termEntryAttributes);
	request.setTermAttributes(termAttributes);
	request.setTermNotes(termNotes);

	// export
	export(fileName, out, request);

	// validate (en,en1,en2,de)
	expectedRow1 = Arrays.asList(ID_1, termEntryAttribute, "dog", status, "en ker", "en noun", "puppy synonym2",
		status, "en puppy desc2", "puppy noun2", "puppy synonym1", status, "en puppy desc1", "puppy noun1",
		"Hund", status, "de ker", "de noun");

	expectedRow2 = Arrays.asList(ID_2, termEntryAttribute2, "dog", status, "en ker", "en noun", "puppy", status,
		"en puppy", "puppy noun", "", "", "", "", "Hund", status, "de ker", "de noun");

	exportValidation(file, languagesToExport, termEntryAttributes, termAttributes, termNotes);
    }

    @Test
    public void testXlsExport() throws Exception {
	/* First term-entry has one "en" synonym */

	String fileName = "testXlsxExport.xlsx";
	String filePath = "target/".concat(fileName);
	file = new File(filePath);
	OutputStream out = new FileOutputStream(file);

	// languages
	String en = "en";
	String de = "de";
	String fr = "fr";

	List<String> languagesToExport = new LinkedList<>();
	languagesToExport.add(en);
	languagesToExport.add(de);
	languagesToExport.add(fr);

	_synonymCodes.add("en1");

	// description types
	String att1 = "definition";
	String termAtt = "context";
	String termNote = "partOfSpeach";

	List<String> termEntryAttributes = new LinkedList<>();
	termEntryAttributes.add(att1);

	List<String> termAttributes = new LinkedList<>();
	termAttributes.add(termAtt);

	List<String> termNotes = new LinkedList<>();
	termNotes.add(termNote);

	// search request
	TermEntrySearchRequest request = new TermEntrySearchRequest();
	request.setLanguagesToExport(languagesToExport);
	request.setTermEntriesAttributes(termEntryAttributes);
	request.setTermAttributes(termAttributes);
	request.setTermNotes(termNotes);

	// export
	export(fileName, out, request);

	// validate (en,en1,de,fr)
	expectedRow1 = Arrays.asList(ID_1, termEntryAttribute, "dog", status, "en ker", "en noun", "", "", "", "",
		"Hund", status, "de ker", "de noun", "chien", status, "fr ker", "fr noun");

	expectedRow2 = Arrays.asList(ID_2, termEntryAttribute2, "dog", status, "en ker", "en noun", "puppy", status,
		"en puppy", "puppy noun", "Hund", status, "de ker", "de noun", "chien", status, "fr ker", "fr noun");

	exportValidation(file, languagesToExport, termEntryAttributes, termAttributes, termNotes);
    }

    private Term createTerm(String languageId, String name, Description att, Description note) {
	att.setBaseType(Description.ATTRIBUTE);
	note.setBaseType(Description.NOTE);

	Term term = new Term(languageId, name, false, "PROCESSED", "user");
	term.setUuId(UUID.randomUUID().toString());
	term.addDescription(att);
	term.addDescription(note);
	return term;
    }

    private TermEntry createTermEntry(String termEntryId, Term... terms) {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(termEntryId);

	for (Term term : terms) {
	    termEntry.addTerm(term);
	}

	return termEntry;
    }

    private List<String> expectedHeader(List<String> languagesToExport, List<String> termEntryAttributes,
	    List<String> termAttributes, List<String> termNotes) {
	List<String> expectedHeader = new LinkedList<String>();
	expectedHeader.add(XlsConfiguration.TERM_ENTRY_ID);
	expectedHeader.addAll(termEntryAttributes);

	if (getSynonymCodes().contains("en1")) {
	    languagesToExport.add(1, "en1");
	}
	if (getSynonymCodes().contains("en2")) {
	    languagesToExport.add(2, "en2");
	}
	if (getSynonymCodes().contains("de1")) {
	    languagesToExport.add("de1");
	}
	if (getSynonymCodes().contains("en-US1")) {
	    languagesToExport.add("en-US1");
	}

	for (String languageId : languagesToExport) {
	    expectedHeader.add(languageId);
	    expectedHeader.add(XlsHelper.joinLanguageAndStatus(languageId));

	    XlsHelper.addToHeader(expectedHeader, languageId, XlsHelper.COLON, termAttributes);
	    XlsHelper.addToHeader(expectedHeader, languageId, XlsHelper.NOTE, termNotes);
	}

	return expectedHeader;
    }

    private void export(String fileName, OutputStream out, TermEntrySearchRequest request) throws Exception {
	ExportDocumentFactory factory = ExportDocumentFactory.getInstance(ExportFormatEnum.XLSX);
	factory.open(out, request, fileName);
	for (TermEntry termEntry : getTermEntries()) {
	    factory.write(termEntry, new ExportInfo(), false, null);
	}
	factory.close();
    }

    private void exportValidation(File file, List<String> languagesToExport, List<String> termEntryAttributes,
	    List<String> termAttributes, List<String> termNotes) throws FileNotFoundException {
	List<String> expectedHeader = expectedHeader(languagesToExport, termEntryAttributes, termAttributes, termNotes);

	InputStream stream = new FileInputStream(file);
	XlsReader reader = new XlsReader(stream, true, true);

	List<String> actualHeader = reader.getHeaderColumns();
	Assert.assertEquals(expectedHeader, actualHeader);

	List<String> actualRow1 = new ArrayList<String>();
	reader.readNextRecord(actualRow1);
	Assert.assertEquals(getExpectedRow1(), actualRow1);

	List<String> actualRow2 = new ArrayList<String>();
	reader.readNextRecord(actualRow2);
	Assert.assertEquals(getExpectedRow2(), actualRow2);
    }

    private List<String> getExpectedRow1() {
	return expectedRow1;
    }

    private List<String> getExpectedRow2() {
	return expectedRow2;
    }

    private List<String> getSynonymCodes() {
	return _synonymCodes;
    }

    private List<TermEntry> getTermEntries() {
	return _termEntries;
    }
}

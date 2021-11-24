package org.gs4tr.termmanager.service.export;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation3.tbx.constraint.ConstraintFactory;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.service.xls.XlsHeaderRange;
import org.gs4tr.termmanager.service.xls.XlsHelper;
import org.gs4tr.termmanager.service.xls.XlsType;
import org.gs4tr.termmanager.service.xls.XlsWriter;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.google.common.base.CharMatcher;

public class XlsExportDocument extends ExportDocumentFactory {

    private List<XlsHeaderRange> _languageRanges;

    private XlsHeaderRange _termEntryAttributesRange;

    private XlsWriter _writer;

    private XlsType _xlsType = XlsType.XLSX;

    @Override
    public void close() throws Exception {
	sortXlxsColumns();
	OutputStream output = getOutputStream();
	getWriter().save(output, getXlsType());
	output.flush();
	output.close();
    }

    @Override
    public ConstraintFactory open(OutputStream outputStream, TermEntrySearchRequest searchRequest, String xcsFileName) {
	resolveXlsType();
	setWriter(new XlsWriter());
	setSearchRequest(searchRequest);
	setOutputStream(outputStream);

	writeHeader(searchRequest);

	return null;
    }

    private void addEmptyTerm(XlsHeaderRange languageRange, List<String> row) {
	XlsWriter writer = getWriter();

	List<String> header = writer.getHeaderColumns();

	List<String> headerLanguage = header.subList(languageRange.getStart(), languageRange.getEnd());

	for (int i = 0; i < headerLanguage.size(); i++) {
	    row.add(StringUtils.EMPTY);
	}
    }

    private void addSynonymsHeaderRange(Map<Integer, Term> termMap, List<XlsHeaderRange> languageRanges,
	    List<XlsHeaderRange> synonymRanges) {

	XlsWriter writer = getWriter();

	TermEntrySearchRequest searchRequest = getSearchRequest();
	List<String> termAttributes = searchRequest.getTermAttributes();
	List<String> termNotes = searchRequest.getTermNotes();

	for (Entry<Integer, Term> entry : termMap.entrySet()) {
	    Term term = entry.getValue();
	    String languageId = term.getLanguageId();

	    int key = entry.getKey();
	    if (key == 0) {
		continue;
	    }

	    String synonymKey = languageId.concat(String.valueOf(key));

	    XlsHeaderRange range = new XlsHeaderRange();
	    range.setKey(synonymKey);
	    range.setValue(languageId);

	    if (!languageRanges.contains(range) && !synonymRanges.contains(range)) {

		int start = writer.getHeaderColumns().size();

		writer.addHeaderCell(synonymKey);
		writer.addHeaderCell(XlsHelper.joinLanguageAndStatus(synonymKey));

		if (CollectionUtils.isNotEmpty(termAttributes)) {
		    for (String attribute : termAttributes) {
			writer.addHeaderCell(XlsHelper.joinStrings(synonymKey, XlsHelper.COLON, attribute));
		    }
		}

		if (CollectionUtils.isNotEmpty(termNotes)) {
		    for (String note : termNotes) {
			writer.addHeaderCell(XlsHelper.joinStrings(synonymKey, XlsHelper.NOTE, note));
		    }
		}

		int end = writer.getHeaderColumns().size();

		range.setStart(start);
		range.setEnd(end);

		synonymRanges.add(range);
	    }
	}
    }

    private void addTerm(XlsHeaderRange languageRange, Term term, List<String> row, ExportInfo exportInfo) {
	XlsWriter writer = getWriter();

	List<String> header = writer.getHeaderColumns();

	List<String> headerLanguage = header.subList(languageRange.getStart(), languageRange.getEnd());

	for (String headerCell : headerLanguage) {
	    if (headerCell.endsWith(XlsHelper.STATUS)) {
		row.add(ItemStatusTypeHolder.getStatusDisplayName(term.getStatus()));
	    } else if (headerCell.contains(XlsHelper.NOTE)) {
		String type = headerCell.split(XlsHelper.NOTE)[1];
		Set<Description> notes = ServiceUtils.getDescriptionsByType(term, Description.NOTE);
		row.add(handleDescriptions(type, notes));
	    } else if (headerCell.contains(XlsHelper.COLON)) {
		String type = headerCell.split(XlsHelper.COLON)[1];
		Set<Description> attributes = ServiceUtils.getDescriptionsByType(term, Description.ATTRIBUTE);
		row.add(handleDescriptions(type, attributes));
	    } else {
		row.add(term.getName());
		exportInfo.incrementTotalTermsExported();
	    }
	}
    }

    private void addTermEntryAttributes(TermEntry termEntry, List<String> row) {
	XlsWriter writer = getWriter();

	List<String> header = writer.getHeaderColumns();

	XlsHeaderRange termEntryAttributesRange = getTermEntryAttributesRange();

	if (termEntryAttributesRange == null) {
	    return;
	}

	Set<Description> termEntryAttributes = termEntry.getDescriptions();

	List<String> headerTermEntryAttributes = header.subList(termEntryAttributesRange.getStart(),
		termEntryAttributesRange.getEnd());
	for (String attribute : headerTermEntryAttributes) {
	    if (CollectionUtils.isEmpty(termEntryAttributes)) {
		row.add(StringUtils.EMPTY);
		continue;
	    }

	    row.add(handleDescriptions(attribute, termEntryAttributes));
	}
    }

    private void copyIdAndAttrColumns(Worksheet notSortedWorksheet, Worksheet sortedWorksheet) {
	int sourceTermStartIndex = getLanguageRanges().get(0).getStart();

	for (int i = 0; i < sourceTermStartIndex; i++) {
	    sortedWorksheet.getCells().copyColumn(notSortedWorksheet.getCells(), i, i);
	}
    }

    private void copySourceTermColumns(Worksheet notSortedWorksheet, Worksheet sortedWorksheet) {
	int sourceTermStartIndex = getLanguageRanges().get(0).getStart();
	int sourceTermEndIndex = getLanguageRanges().get(0).getEnd();

	for (int i = sourceTermStartIndex; i < sourceTermEndIndex; i++) {
	    sortedWorksheet.getCells().copyColumn(notSortedWorksheet.getCells(), i, i);
	}
    }

    private Map<Integer, Term> createTermMap(Set<Term> terms) {
	Map<Integer, Term> termMap = new HashMap<Integer, Term>();

	int i = 1;
	for (Term term : terms) {
	    if (term.isDisabled()) {
		continue;
	    }

	    if (termMap.get(0) == null && term.isFirst()) {
		termMap.put(0, term);
	    } else {
		termMap.put(i, term);
		i++;
	    }
	}
	return termMap;
    }

    private int getLanguageRange(List<XlsHeaderRange> languageRanges) {
	XlsHeaderRange xlsHeaderRange = languageRanges.get(0);
	return xlsHeaderRange.getEnd() - xlsHeaderRange.getStart();
    }

    private List<XlsHeaderRange> getLanguageRanges() {
	return _languageRanges;
    }

    private int getNumberOfMainTerms(List<XlsHeaderRange> languageRanges) {
	int numberOfMainTerms = 0;

	/* if xlsHeaderRange key doesn't contain number it means that it's main term */
	for (XlsHeaderRange xlsHeaderRange : languageRanges) {
	    if (!xlsHeaderRange.getKey().matches(".*\\d+.*")) {
		numberOfMainTerms++;
	    }
	}
	return numberOfMainTerms;
    }

    private Term getTerm(String key, Map<Integer, Term> termMap) {
	if (Locale.checkLocale(key)) {
	    return termMap.get(0);
	} else {
	    String digit = CharMatcher.DIGIT.retainFrom(key);
	    if (StringUtils.isNotEmpty(digit)) {
		return termMap.get(Integer.valueOf(digit));
	    }
	}

	return null;
    }

    private XlsHeaderRange getTermEntryAttributesRange() {
	return _termEntryAttributesRange;
    }

    private Worksheet getWorksheet(XlsWriter xlsWriter) {
	Workbook workbook = xlsWriter.getWorkbook();
	return workbook.getWorksheets().get(0);
    }

    private XlsWriter getWriter() {
	return _writer;
    }

    private XlsType getXlsType() {
	return _xlsType;
    }

    private String handleDescriptions(String type, Set<Description> descriptions) {
	boolean multiplicity = false;

	StringBuilder builder = new StringBuilder();

	for (Description description : descriptions) {
	    if (type.equals(description.getType())) {
		if (multiplicity) {
		    builder.append(XlsHelper.DELIMITER);
		}
		builder.append(description.getValue());
		multiplicity = true;
	    }
	}

	return builder.toString();
    }

    private void handleLanguageRanges(TermEntry termEntry, List<String> row, List<XlsHeaderRange> languageRanges,
	    List<XlsHeaderRange> synonymRanges, ExportInfo exportInfo) {

	for (XlsHeaderRange languageRange : languageRanges) {

	    String languageKey = languageRange.getKey();
	    String languageId = languageRange.getValue();

	    Set<Term> languageTerms = termEntry.getLanguageTerms().get(languageId);
	    if (CollectionUtils.isEmpty(languageTerms)) {
		addEmptyTerm(languageRange, row);
		continue;
	    }

	    Map<Integer, Term> termMap = createTermMap(languageTerms);

	    if (synonymRanges != null) {
		addSynonymsHeaderRange(termMap, languageRanges, synonymRanges);
	    }

	    Term term = getTerm(languageKey, termMap);
	    if (term == null) {
		addEmptyTerm(languageRange, row);
		continue;
	    }

	    addTerm(languageRange, term, row, exportInfo);

	}
    }

    private boolean isSortingNeeded(List<XlsHeaderRange> languageRanges, int numberOfMainTerms) {
	return languageRanges.size() > numberOfMainTerms;
    }

    private void resolveXlsType() {
	XlsType xlsType = XlsType.XLSX.getDisplayName().equals(getFileFormat()) ? XlsType.XLSX : XlsType.XLS;
	setXlsType(xlsType);
    }

    private void setLanguageRanges(List<XlsHeaderRange> languageRanges) {
	_languageRanges = languageRanges;
    }

    private void setTermEntryAttributesRange(XlsHeaderRange termEntryAttributesRange) {
	_termEntryAttributesRange = termEntryAttributesRange;
    }

    private void setWriter(XlsWriter writer) {
	_writer = writer;
    }

    private void setXlsType(XlsType xlsType) {
	_xlsType = xlsType;
    }

    private void sortXlxsColumns() {
	List<XlsHeaderRange> languageRanges = getLanguageRanges();
	int numberOfMainTerms = getNumberOfMainTerms(languageRanges);

	/* If xls file doesn't have synonyms sorting is not needed */
	if (!isSortingNeeded(languageRanges, numberOfMainTerms)) {
	    return;
	}

	XlsWriter writer = getWriter();
	Worksheet notSortedWorksheet = getWorksheet(writer);

	int numberOfColumns = writer.getHeaderColumns().size();

	int languageRange = getLanguageRange(languageRanges);

	XlsWriter xlsWriter = new XlsWriter();
	setWriter(xlsWriter);

	Worksheet sortedWorksheet = getWorksheet(xlsWriter);

	copyIdAndAttrColumns(notSortedWorksheet, sortedWorksheet);

	copySourceTermColumns(notSortedWorksheet, sortedWorksheet);

	int columnIndex = languageRanges.get(0).getEnd();

	for (int i = 0; i < numberOfMainTerms; i++) {
	    XlsHeaderRange mainTermRange = languageRanges.get(i);

	    for (int j = numberOfMainTerms; j < languageRanges.size(); j++) {
		XlsHeaderRange synonymRange = languageRanges.get(j);

		if (synonymRange.getValue().equals(mainTermRange.getValue())) {
		    sortedWorksheet.getCells().copyColumns(notSortedWorksheet.getCells(), synonymRange.getStart(),
			    columnIndex, languageRange);
		    columnIndex += languageRange;
		}
	    }

	    if (columnIndex == numberOfColumns) {
		break;
	    }
	    sortedWorksheet.getCells().copyColumns(notSortedWorksheet.getCells(), mainTermRange.getEnd(), columnIndex,
		    languageRange);

	    columnIndex += languageRange;
	}
    }

    private void writeHeader(TermEntrySearchRequest searchRequest) {
	List<String> header = new LinkedList<String>();
	header.add(XlsHelper.TERM_ENTRY_ID);

	List<String> termEntriesAttributes = searchRequest.getTermEntriesAttributes();
	if (CollectionUtils.isNotEmpty(termEntriesAttributes)) {
	    header.addAll(termEntriesAttributes);

	    setTermEntryAttributesRange(new XlsHeaderRange(1, termEntriesAttributes.size() + 1));
	}

	List<XlsHeaderRange> languageRanges = new ArrayList<XlsHeaderRange>();

	List<String> languageIds = searchRequest.getLanguagesToExport();
	for (String languageId : languageIds) {
	    int start = header.size();

	    header.add(languageId);
	    header.add(XlsHelper.joinLanguageAndStatus(languageId));

	    List<String> termAttributes = searchRequest.getTermAttributes();
	    XlsHelper.addToHeader(header, languageId, XlsHelper.COLON, termAttributes);

	    List<String> termNotes = searchRequest.getTermNotes();
	    XlsHelper.addToHeader(header, languageId, XlsHelper.NOTE, termNotes);

	    int end = header.size();

	    XlsHeaderRange range = new XlsHeaderRange(start, end, languageId, languageId);
	    languageRanges.add(range);
	}

	setLanguageRanges(languageRanges);

	getWriter().write(header, true);
    }

    @Override
    protected String buildTermEntryXml(TermEntry termEntry, ExportInfo exportInfo, boolean isWS,
	    ExportTermsCallback exportTermsCallback) {
	XlsWriter writer = getWriter();

	List<String> row = new LinkedList<String>();
	row.add(termEntry.getUuId());

	// Handle termEntry attributes
	addTermEntryAttributes(termEntry, row);

	List<XlsHeaderRange> languageRanges = getLanguageRanges();

	List<XlsHeaderRange> synonymRanges = new ArrayList<XlsHeaderRange>();

	handleLanguageRanges(termEntry, row, languageRanges, synonymRanges, exportInfo);

	if (!synonymRanges.isEmpty()) {
	    handleLanguageRanges(termEntry, row, synonymRanges, null, exportInfo);

	    languageRanges.addAll(synonymRanges);
	    setLanguageRanges(languageRanges);
	}

	writer.write(row, false);
	exportInfo.setTotalTermEntriesExported(exportInfo.getTotalTermEntriesExported() + 1);

	return null;
    }

    @Override
    protected boolean isExportableByForbidden(Term term, Boolean exportForbidden) {
	return true;
    }
}

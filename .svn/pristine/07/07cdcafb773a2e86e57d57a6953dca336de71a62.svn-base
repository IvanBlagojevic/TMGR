package org.gs4tr.termmanager.service.xls.report.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.xls.report.CellTextStyle;
import org.gs4tr.termmanager.model.xls.report.ReportColor;
import org.gs4tr.termmanager.model.xls.report.XlsReport;
import org.gs4tr.termmanager.model.xls.report.XlsReportCell;
import org.gs4tr.termmanager.model.xls.report.XlsReportRow;
import org.gs4tr.termmanager.model.xls.report.XlsReportSheet;
import org.gs4tr.termmanager.service.xls.XlsHelper;
import org.gs4tr.termmanager.service.xls.report.builder.terminology.ReportDescription;
import org.gs4tr.termmanager.service.xls.report.builder.terminology.ReportTerm;

/**
 * 
 * This builder is responsible for collecting changes and appending it in the
 * import summary report. It creates sheet per language and a row per term
 * entry.
 * 
 * @author emisia
 * @since 5.0.0.
 */
public class ImportSummaryReportBuilder {

    private ReportEqualsHelper _equalsHelper;

    /** Languages that were in the header */
    private List<String> _importedLocales;

    private final Map<String, List<ReportTerm>> _languageReportTerms;

    private TermEntry _newEntry;

    private TermEntry _oldEntry;

    private XlsReport _report;

    private final List<ReportDescription> _reportTermEntryAttributes;

    private String _sourceLanguageId;

    private List<String> _termEntryAttributes;

    private String _termEntryId;

    public ImportSummaryReportBuilder(TermEntry oldEntry, TermEntry newEntry, Collection<String> termEntryAttributes,
	    String sourceLanguageId, List<String> importedLocales, XlsReport report, boolean ignoreCase) {
	this();
	_sourceLanguageId = sourceLanguageId;
	_report = report;
	_oldEntry = oldEntry;
	_newEntry = newEntry;
	_termEntryId = newEntry.getUuId();
	_equalsHelper = new ReportEqualsHelper(ignoreCase);
	_importedLocales = getNullSafeList(importedLocales);
	_termEntryAttributes = getNullSafeList(termEntryAttributes);
    }

    private ImportSummaryReportBuilder() {
	_languageReportTerms = new HashMap<>();
	_reportTermEntryAttributes = new ArrayList<>();
	_equalsHelper = new ReportEqualsHelper();
    }

    public void appendReport() {
	collectChanges();

	Set<String> targetLanguageIds = getTargetLanguageIds();
	/* Issue: TERII-4696 Date: 1-June-2017 */
	if (targetLanguageIds.isEmpty()) {
	    appendLanguage(_sourceLanguageId);
	    return;
	}

	for (String languageId : targetLanguageIds) {
	    // if there are no changes, skip.
	    appendLanguage(languageId);
	}
    }

    private void addReportTerm(Term term, ReportColor color) {
	String languageId = term.getLanguageId();
	List<ReportTerm> list = _languageReportTerms.get(languageId);
	if (Objects.isNull(list)) {
	    list = new ArrayList<>();
	    _languageReportTerms.put(languageId, list);
	}
	list.add(new ReportTerm(term, color));
    }

    private void addReportTerm(Term oldTerm, Term newTerm) {
	String languageId = oldTerm.getLanguageId();
	List<ReportTerm> list = _languageReportTerms.get(languageId);
	if (Objects.isNull(list)) {
	    list = new ArrayList<>();
	    _languageReportTerms.put(languageId, list);
	}
	list.add(new ReportTerm(oldTerm, newTerm, ReportColor.RED_GREEN));
    }

    private void addReportTermEntryAttribute(Description oldDescription, Description newDescription) {
	_reportTermEntryAttributes.add(new ReportDescription(oldDescription, newDescription));
    }

    private void addReportTermEntryAttribute(Description description, ReportColor color) {
	_reportTermEntryAttributes.add(new ReportDescription(description, color));
    }

    private void addTermEntryFieldsToHeader(XlsReportSheet sheet) {
	appendHeader(sheet, XlsHelper.TERM_ENTRY_ID);

	for (String type : getTermEntryAttributes()) {
	    appendHeader(sheet, type);
	}
    }

    private void appendCell(XlsReportRow row, String oldValue, String newValue, ReportColor color, int rowIndex,
	    int columnIndex) {
	if (_equalsHelper.equalsString(oldValue, newValue)) {
	    if (color == ReportColor.RED_GREEN) {
		color = ReportColor.BLACK;
	    }
	    row.addCell(new XlsReportCell(rowIndex, columnIndex, color, oldValue));
	} else {
	    String value = oldValue.concat(StringConstants.SPACE).concat(newValue);

	    CellTextStyle redStyle = new CellTextStyle();
	    redStyle.setColor(ReportColor.RED);
	    redStyle.setLength(oldValue.length());
	    redStyle.setStartIndex(0);

	    CellTextStyle greenStyle = new CellTextStyle();
	    greenStyle.setColor(ReportColor.GREEN);
	    greenStyle.setLength(value.length());
	    greenStyle.setStartIndex(value.length() - newValue.length());

	    row.addCell(new XlsReportCell(rowIndex, columnIndex, Arrays.asList(redStyle, greenStyle), value));
	}
    }

    private int appendHeader(XlsReportSheet sheet, String value) {
	List<String> header = sheet.getHeader();
	if (!header.contains(value)) {
	    header.add(value);
	}

	return header.indexOf(value);
    }

    private void appendLanguage(String languageId) {
	if (!hasChanges(languageId)) {
	    return;
	}

	XlsReportSheet sheet = getOrAddSheet(languageId);

	/* Issue TERII-4685 Date: 1-June-2017 */
	addTermEntryFieldsToHeader(sheet);

	/* Issue TERII-4758 */
	appendHeader(sheet, _sourceLanguageId);
	appendHeader(sheet, _sourceLanguageId.concat(XlsHelper.STATUS));

	AtomicInteger rowIndex = sheet.getRowIndex();
	XlsReportRow row = new XlsReportRow();

	// termEntry
	appendTermEntry(sheet, rowIndex.get(), row);

	// source terms
	if (!languageId.equals(_sourceLanguageId)) {
	    appendTerms(sheet, row, _sourceLanguageId, rowIndex.get());
	}

	// target terms
	appendTerms(sheet, row, languageId, rowIndex.get());

	sheet.addRow(row);
	sheet.incrementRowIndex();
    }

    private void appendTerm(ReportTerm reportTerm, XlsReportSheet sheet, XlsReportRow row, int rowIndex,
	    int synonymIndex) {
	if (Objects.isNull(reportTerm)) {
	    return;
	}

	ReportColor color = reportTerm.getColor();

	String languageId = synonymIndex == 0 ? reportTerm.getLanguageId() : reportTerm.getLanguageId() + synonymIndex;

	int columnIndex = appendHeader(sheet, languageId);
	appendCell(row, reportTerm.getOldName(), reportTerm.getNewName(), color, rowIndex, columnIndex);

	columnIndex = appendHeader(sheet, languageId.concat(XlsHelper.STATUS));
	appendCell(row, reportTerm.getOldStatus(), reportTerm.getNewStatus(), color, rowIndex, columnIndex);

	appendTermDescriptions(reportTerm, sheet, row, rowIndex, languageId);
    }

    private void appendTermDescriptions(ReportTerm reportTerm, XlsReportSheet sheet, XlsReportRow row, int rowIndex,
	    String languageId) {
	List<ReportDescription> oldDescs = reportTerm.getOldDescriptions();
	List<ReportDescription> newDescs = reportTerm.getNewDescriptions();

	if (CollectionUtils.isEmpty(oldDescs) && CollectionUtils.isEmpty(newDescs)) {
	    return;
	}

	if (CollectionUtils.isEmpty(oldDescs)) {
	    for (ReportDescription newDesc : newDescs) {
		String colon = newDesc.isNote() ? XlsHelper.NOTE : XlsHelper.COLON;
		int columnIndex = appendHeader(sheet, languageId.concat(colon).concat(newDesc.getType()));
		appendCell(row, newDesc.getOldValue(), newDesc.getNewValue(), ReportColor.GREEN, rowIndex, columnIndex);
	    }
	    return;
	}

	if (CollectionUtils.isEmpty(newDescs)) {
	    for (ReportDescription oldDesc : oldDescs) {
		String colon = oldDesc.isNote() ? XlsHelper.NOTE : XlsHelper.COLON;
		int columnIndex = appendHeader(sheet, languageId.concat(colon).concat(oldDesc.getType()));
		appendCell(row, oldDesc.getOldValue(), oldDesc.getNewValue(), ReportColor.RED, rowIndex, columnIndex);
	    }
	    return;
	}

	if (_equalsHelper.containsAllReportDescriptions(newDescs, oldDescs)) {
	    for (ReportDescription oldDesc : oldDescs) {
		String colon = oldDesc.isNote() ? XlsHelper.NOTE : XlsHelper.COLON;
		int columnIndex = appendHeader(sheet, languageId.concat(colon).concat(oldDesc.getType()));
		appendCell(row, oldDesc.getOldValue(), oldDesc.getNewValue(), reportTerm.getColor(), rowIndex,
			columnIndex);
	    }
	    return;
	}

	List<ReportDescription> copyNewDescs = new ArrayList<>(newDescs);

	Iterator<ReportDescription> newDescIterator = copyNewDescs.iterator();
	while (newDescIterator.hasNext()) {
	    ReportDescription newDesc = newDescIterator.next();

	    String colon = newDesc.isNote() ? XlsHelper.NOTE : XlsHelper.COLON;
	    int columnIndex = appendHeader(sheet, languageId.concat(colon).concat(newDesc.getType()));

	    ReportDescription match = _equalsHelper.containsReportDescription(newDesc, oldDescs);
	    if (Objects.nonNull(match)) {
		// non changed
		appendCell(row, newDesc.getOldValue(), newDesc.getNewValue(), ReportColor.BLACK, rowIndex, columnIndex);
		newDescIterator.remove();
		oldDescs.remove(match);
	    }
	}

	for (ReportDescription newDesc : copyNewDescs) {
	    String colon = newDesc.isNote() ? XlsHelper.NOTE : XlsHelper.COLON;
	    int columnIndex = appendHeader(sheet, languageId.concat(colon).concat(newDesc.getType()));

	    /*
	     * ReportDescription match =
	     * _equalsHelper.containsReportDescription(newDesc, oldDescs); if
	     * (Objects.nonNull(match)) { // non changed appendCell(row,
	     * newDesc.getOldValue(), newDesc.getNewValue(), ReportColor.BLACK,
	     * rowIndex, columnIndex); continue; }
	     */

	    ReportDescription match = _equalsHelper.containsReportDescriptionByType(newDesc, oldDescs);
	    if (Objects.nonNull(match)) {
		// updated
		appendCell(row, match.getOldValue(), newDesc.getNewValue(), ReportColor.RED_GREEN, rowIndex,
			columnIndex);
	    } else {
		// added
		appendCell(row, newDesc.getOldValue(), newDesc.getNewValue(), ReportColor.GREEN, rowIndex, columnIndex);
	    }
	}

	// deleted
	for (ReportDescription oldDesc : oldDescs) {
	    ReportDescription matchByType = _equalsHelper.containsReportDescriptionByType(oldDesc, newDescs);
	    ReportDescription match = _equalsHelper.containsReportDescription(oldDesc, newDescs);
	    if (Objects.isNull(matchByType) && Objects.isNull(match)) {
		String colon = oldDesc.isNote() ? XlsHelper.NOTE : XlsHelper.COLON;
		int columnIndex = appendHeader(sheet, languageId.concat(colon).concat(oldDesc.getType()));
		appendCell(row, oldDesc.getOldValue(), oldDesc.getNewValue(), ReportColor.RED, rowIndex, columnIndex);
	    }
	}
    }

    private void appendTermEntry(XlsReportSheet sheet, int rowIndex, XlsReportRow row) {
	int columnIndex = appendHeader(sheet, XlsHelper.TERM_ENTRY_ID);
	appendCell(row, _termEntryId, _termEntryId, ReportColor.BLACK, rowIndex, columnIndex);

	for (ReportDescription d : _reportTermEntryAttributes) {
	    columnIndex = appendHeader(sheet, d.getType());
	    appendCell(row, d.getOldValue(), d.getNewValue(), d.getColor(), rowIndex, columnIndex);
	}
    }

    private void appendTerms(XlsReportSheet sheet, XlsReportRow row, String languageId, int rowIndex) {
	List<ReportTerm> reportTerms = _languageReportTerms.get(languageId);
	if (Objects.isNull(reportTerms)) {
	    return;
	}

	int synonymCount = 0;

	ReportTerm firstTerm = getFirstTerm(reportTerms);
	appendTerm(firstTerm, sheet, row, rowIndex, synonymCount);

	if (Objects.nonNull(firstTerm)) {
	    synonymCount++;
	}

	for (ReportTerm reportTerm : reportTerms) {
	    if (reportTerm.equals(firstTerm)) {
		continue;
	    }
	    appendTerm(reportTerm, sheet, row, rowIndex, synonymCount);
	    synonymCount++;
	}
    }

    private void collectChanges() {
	collectTermEntryAttributes(_newEntry.getDescriptions(), _oldEntry.getDescriptions());

	Map<String, Set<Term>> oldLanguageTerms = _oldEntry.getLanguageTerms();
	Map<String, Set<Term>> newLanguageTerms = _newEntry.getLanguageTerms();
	if (Objects.nonNull(newLanguageTerms)) {
	    for (String languageId : getImportedLocales()) {
		Set<Term> newTerms = newLanguageTerms.get(languageId);
		Set<Term> oldTerms = oldLanguageTerms.get(languageId);
		collectTerms(newTerms, oldTerms);
	    }
	}
    }

    private void collectTermEntryAttributes(Set<Description> newDescriptions, Set<Description> oldDescriptions) {
	if (CollectionUtils.isEmpty(oldDescriptions) && CollectionUtils.isEmpty(newDescriptions)) {
	    return;
	}

	if (CollectionUtils.isEmpty(oldDescriptions)) {
	    newDescriptions.forEach(i -> addReportTermEntryAttribute(i, ReportColor.GREEN));
	    return;
	}

	if (CollectionUtils.isEmpty(newDescriptions)) {
	    oldDescriptions.forEach(o -> addReportTermEntryAttribute(o, ReportColor.RED));
	    return;
	}

	if (_equalsHelper.containsAllDescriptions(newDescriptions, oldDescriptions)) {
	    oldDescriptions.forEach(o -> addReportTermEntryAttribute(o, ReportColor.BLACK));
	    return;
	}

	for (Description newDesc : newDescriptions) {
	    Description match = _equalsHelper.containsDescription(newDesc, oldDescriptions);
	    if (Objects.nonNull(match)) {
		// non changed
		addReportTermEntryAttribute(newDesc, ReportColor.BLACK);
		continue;
	    }

	    match = _equalsHelper.containsDescriptionByType(newDesc, oldDescriptions);
	    if (Objects.nonNull(match)) {
		// updated
		addReportTermEntryAttribute(match, newDesc);
	    } else {
		// added
		addReportTermEntryAttribute(newDesc, ReportColor.GREEN);
	    }
	}

	for (Description oldDesc : oldDescriptions) {
	    Description matchByType = _equalsHelper.containsDescriptionByType(oldDesc, newDescriptions);
	    Description match = _equalsHelper.containsDescription(oldDesc, newDescriptions);
	    if (Objects.isNull(matchByType) && Objects.isNull(match)) {
		// deleted
		addReportTermEntryAttribute(oldDesc, ReportColor.RED);
	    }
	}
    }

    private void collectTerms(Set<Term> newTerms, Set<Term> oldTerms) {
	if (CollectionUtils.isEmpty(oldTerms) && CollectionUtils.isEmpty(newTerms)) {
	    return;
	}

	// added
	if (CollectionUtils.isEmpty(oldTerms)) {
	    newTerms.forEach(t -> addReportTerm(t, ReportColor.GREEN));
	    return;
	}

	/*
	 * Here we are guessing that term might be updated but other conditions
	 * like containsTerm of containsTermByName must be satisfied before we
	 * say it is updated.
	 */
	boolean updated = isUpdatedTerm(newTerms, oldTerms);

	for (Term newTerm : newTerms) {
	    if (newTerm.isDisabled()) {
		if (!updated) {
		    addReportTerm(newTerm, ReportColor.RED);
		}
		continue;
	    }

	    Term match = _equalsHelper.containsTerm(newTerm, oldTerms, true);
	    if (Objects.nonNull(match)) {
		// non changed
		addReportTerm(newTerm, ReportColor.BLACK);
		continue;
	    }

	    match = _equalsHelper.containsTerm(newTerm, oldTerms, false);
	    if (Objects.nonNull(match)) {
		// updated
		addReportTerm(match, newTerm);
		continue;
	    }

	    match = _equalsHelper.containsTermByName(newTerm, oldTerms);
	    if (Objects.nonNull(match)) {
		// updated
		addReportTerm(match, newTerm);
		continue;
	    }

	    if (updated) {
		Term disabled = findUpdateMatch(newTerm, newTerms);
		if (Objects.nonNull(disabled)) {
		    // updated
		    addReportTerm(disabled, newTerm);
		    continue;
		}
	    }

	    // added
	    addReportTerm(newTerm, ReportColor.GREEN);
	}
    }

    private Term findUpdateMatch(Term term, Set<Term> terms) {
	List<Term> disabledList = terms.stream().filter(Term::isDisabled).collect(Collectors.toList());
	if (disabledList.isEmpty()) {
	    return null;
	}

	Term match = disabledList.stream().filter(Term::isFirst).findFirst().orElse(disabledList.get(0));

	return term.isFirst() ? match : disabledList.get(0);
    }

    private ReportTerm getFirstTerm(List<ReportTerm> reportTerms) {
	return reportTerms.stream().filter(ReportTerm::isFirst).findFirst().orElse(null);
    }

    private List<String> getImportedLocales() {
	return _importedLocales;
    }

    private List<String> getNullSafeList(Collection<String> coll) {
	return Objects.nonNull(coll) ? new ArrayList<>(coll) : new ArrayList<>();
    }

    private XlsReportSheet getOrAddSheet(String targetLanguageId) {
	Optional<XlsReportSheet> optional = _report.getSheets().stream()
		.filter(s -> s.getTargetLanguageId().equals(targetLanguageId)).findFirst();
	if (optional.isPresent()) {
	    return optional.get();
	} else {
	    XlsReportSheet sheet = new XlsReportSheet(_sourceLanguageId, targetLanguageId);
	    _report.addSheet(sheet);
	    return sheet;
	}
    }

    private Set<String> getTargetLanguageIds() {
	Set<String> languageIds = new HashSet<>();

	languageIds.addAll(_languageReportTerms.keySet());
	languageIds.remove(_sourceLanguageId);

	return languageIds;
    }

    private List<String> getTermEntryAttributes() {
	return _termEntryAttributes;
    }

    private boolean hasChanges(String targetLanguageId) {
	boolean hasAttributeChanges = _reportTermEntryAttributes.stream()
		.anyMatch(a -> ReportColor.BLACK != a.getColor());
	if (hasAttributeChanges) {
	    return true;
	}

	List<ReportTerm> sourceTerms = _languageReportTerms.get(_sourceLanguageId);
	boolean hasSourceChanges = Objects.nonNull(sourceTerms)
		&& sourceTerms.stream().anyMatch(s -> ReportColor.BLACK != s.getColor());
	if (hasSourceChanges) {
	    return true;
	}

	List<ReportTerm> targetTerms = _languageReportTerms.get(targetLanguageId);
	return Objects.nonNull(targetTerms) && targetTerms.stream().anyMatch(t -> ReportColor.BLACK != t.getColor());
    }

    private boolean isUpdatedTerm(Set<Term> newTerms, Set<Term> oldTerms) {
	return newTerms.size() - (int) newTerms.stream().filter(Term::isDisabled).count() == oldTerms.size();
    }
}

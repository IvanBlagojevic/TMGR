package org.gs4tr.termmanager.service.xls;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.callback.AbstractValidationCallback;
import org.gs4tr.foundation3.callback.ImportCallback;
import org.gs4tr.foundation3.importer.AbstractBackgroundReader;
import org.gs4tr.foundation3.io.CsvReader.ReturnCode;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.xls.XlsDescription;
import org.gs4tr.termmanager.model.xls.XlsTerm;
import org.gs4tr.termmanager.model.xls.XlsTermEntry;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.gs4tr.termmanager.service.impl.ImportValidationCallback;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig;

import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.google.common.base.CharMatcher;

public class DefaultXlsTermEntryReader extends AbstractBackgroundReader<XlsTermEntry> implements TermEntryReader {

    private static final Set<String> ALLOWED_STATUSES;

    private static final Set<String> ILLEGAL_STATUSES;

    private static final Log LOG = LogFactory.getLog(DefaultXlsTermEntryReader.class);

    private static final String NOTE = "Note"; //$NON-NLS-1$

    private static final String STATUS = "Status"; //$NON-NLS-1$

    static {
	ILLEGAL_STATUSES = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

	ILLEGAL_STATUSES.addAll(ItemStatusTypeHolder.getStatusDisplayName(
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), ItemStatusTypeHolder.IN_FINAL_REVIEW.getName()));

	ALLOWED_STATUSES = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

	ALLOWED_STATUSES.addAll(ItemStatusTypeHolder.getStatusDisplayName(ItemStatusTypeHolder.PROCESSED.getName(),
		ItemStatusTypeHolder.WAITING.getName(), ItemStatusTypeHolder.BLACKLISTED.getName(),
		ItemStatusTypeHolder.ON_HOLD.getName(), ItemStatusTypeHolder.MISSING_TRANSLATION.getName()));
    }

    private final TermEntryReaderConfig _termEntryReaderConfig;

    public DefaultXlsTermEntryReader(TermEntryReaderConfig termEntryReaderConfig) {
	super(termEntryReaderConfig.getIn());
	_termEntryReaderConfig = termEntryReaderConfig;
    }

    @Override
    public void readTermEntries(ImportCallback callback) {
	int currentTermCount = 1;

	if (callback == null) {
	    return;
	}

	int totalTerms = callback.getTotalTerms();

	int percentage = 0;

	try {
	    XlsTermEntry termEntry = readItem();
	    while (termEntry != null) {

		if (callback.isImportCanceled()) {
		    break;
		}

		callback.handleTermEntry(termEntry);

		percentage = (int) (((float) currentTermCount / (float) totalTerms) * 50);

		callback.handlePercentage(percentage);

		termEntry = readItem();

		currentTermCount++;
	    }
	} finally {
	    try {
		callback.handlePercentage(50);
	    } finally {
		stop();
	    }
	}
    }

    /**
     * @throws <code>LocaleException</code>
     *             because {@link #extractLocaleCode(String)} throws
     *             <code>LocaleException</code> in some rare use cases where locale
     *             object for specified locale-code doesn't exist (e.g locale-code:
     *             cz). Please provide appropriate error handling for
     *             <code>LocaleException</code>, otherwise this will result in
     *             "application error" on UI side.
     * 
     * @see <a href=
     *      "https://techqa1.translations.com/jira/browse/TERII-3741">TERII-3741</a>
     * 
     * @since 5.0
     */
    @Override
    public void validate(AbstractValidationCallback info) {
	/*
	 * TODO: @nbekcic, @fmiskovic: Due to 5.0 release time pressure, this class is
	 * just extended to support all required features.
	 * 
	 * But please, in the future, consider re-factoring or even writing analysis
	 * from the scratch in order to improve the design and readability. We should
	 * try to make existing code simpler and easier to maintain. If we do that,
	 * future self will thank us for.
	 * 
	 * 2-March-2017.
	 */

	ImportValidationCallback report = (ImportValidationCallback) info;

	XlsReader reader = null;
	try {
	    reader = new XlsReader(getInputStream(), true, true);
	} catch (HiddenColumnException hiddenColumnException) {
	    String message = hiddenColumnException.getMessage();
	    report.addAlert(AlertSubject.HIDDEN_COLUMN, AlertType.ERROR, message);
	    return;
	} catch (UserException userException) {
	    LogHelper.error(LOG, userException.getMessage(), userException);
	    // 6-April-2017, as per [Improvement#TERII-4477]:
	    String message = userException.getMessage();
	    report.addAlert(AlertSubject.INVALID_FILE_FORMAT, AlertType.ERROR, message);
	    return;
	}

	List<String> worksheetNames = new ArrayList<>();

	WorksheetCollection worksheetCollection = reader.getWorkbook().getWorksheets();
	for (Object object : worksheetCollection) {
	    Worksheet worksheet = (Worksheet) object;
	    String name = worksheet.getName();
	    if (!worksheet.isVisible()) {
		String message = String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.0"), name); // $NON-NLS-1$
		report.addAlert(AlertSubject.HIDDEN_WORKSHEET, AlertType.ERROR, message);
		return;
	    }
	    worksheetNames.add(name);
	}

	if (worksheetNames.size() > 1) {
	    String message = String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.1"), worksheetNames); // $NON-NLS-1$
	    report.addAlert(AlertSubject.MULTIPLE_WORKSHEETS, AlertType.WARNING, message);
	}

	List<String> headerColumns = reader.getHeaderColumns();

	ReturnCode returnedCode = basicHeaderValidation(headerColumns, report);
	if (ReturnCode.FILE_END == returnedCode) {
	    return;
	}

	Set<String> identifiers = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	MutableInt columnIndex = new MutableInt(0);

	Set<String> termEntryAttributes = new HashSet<>();
	returnedCode = collectTermEntryDescriptionTypes(headerColumns, termEntryAttributes, columnIndex, report);
	if (ReturnCode.FILE_END == returnedCode) {
	    return;
	}

	report.getTermEntryDescriptions().addAll(termEntryAttributes);

	returnedCode = validateHeaderLocales(headerColumns, identifiers, report);
	if (ReturnCode.FILE_END == returnedCode) {
	    return;
	}
	/*
	 * Case when there are status, attribute, note or synonym columns without the
	 * main language column.
	 */
	for (String identifier : identifiers) {
	    String localeCode = extractLocaleCode(identifier);
	    if (!identifiers.contains(localeCode)) {
		String message = String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.5"), localeCode); // $NON-NLS-1$
		report.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR, message);
		return;
	    }
	}

	returnedCode = validateColonCells(headerColumns, identifiers, report, columnIndex.intValue());
	if (ReturnCode.FILE_END == returnedCode) {
	    return;
	}

	List<String> row = new ArrayList<String>();

	Map<String, Integer> languageMap = report.getLanguageMap();

	while (ReturnCode.FILE_END != returnedCode) {

	    try {
		returnedCode = reader.readNextRecord(row);
	    } catch (HiddenColumnException hiddenColumnException) {
		String message = hiddenColumnException.getMessage();
		report.addAlert(AlertSubject.HIDDEN_ROW, AlertType.ERROR, message);
		return;
	    }

	    if (row.size() == 0) {
		report.incrementSkippedTermEntries();
		continue;
	    }
	    // Term entries without id or terms will be consider as not
	    // valid.
	    int termsOrIDCounter = 0;

	    Map<String, Integer> statusColumnCount = new HashMap<String, Integer>();

	    boolean isTermEntryAttribute = true;
	    for (int i = 0; i < headerColumns.size(); i++) {
		String headerCell = headerColumns.get(i);
		String rowCell = row.get(i);

		String localeCode = extractLocaleCode(headerCell);

		if (StringUtils.isBlank(headerCell)) {
		    continue;
		}

		if (StringUtils.isBlank(rowCell)) {
		    if (localeCode != null) {
			languageMap.computeIfAbsent(localeCode, k -> 0);
		    }
		    continue;
		}

		boolean idColumn = headerCell.equals(XlsHelper.TERM_ENTRY_ID);
		if (idColumn && StringUtils.isNotBlank((rowCell))) {
		    termsOrIDCounter++;
		}

		if (isTermEntryAttribute && localeCode == null && !idColumn) {
		    Map<String, Integer> attrCountMap = report.getTermEntryAttributesCount();
		    countTermEntryDescriptions(report, attrCountMap, headerCell, rowCell);
		} else if (localeCode != null) {
		    isTermEntryAttribute = false;
		    if (StringUtils.isNotBlank(rowCell)) {
			termsOrIDCounter++;
		    }
		    report.incrementCountPerLanguage(languageMap, localeCode);
		} else {
		    populateAnalyzeInfo(report, identifiers, headerCell, statusColumnCount, rowCell);
		}
	    }
	    // Case when we have multiple status columns with same
	    // identifier.
	    for (Entry<String, Integer> entry : statusColumnCount.entrySet()) {
		if (entry.getValue() > 1) {
		    report.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR,
			    String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.10"), entry.getKey()));
		    return;
		}
	    }
	    // Do not count term entries without id or terms
	    if (termsOrIDCounter > 0) {
		report.incrementTotalTerms();
	    }
	}

    }

    private void addDescriptions(List<XlsDescription> descriptions, String definition, String baseType,
	    String rowCell) {
	definition = definition.trim();
	String[] cellValues = rowCell.split(XlsHelper.DELIMITER);

	Set<String> comboValues = getComboValuesPerAttribute(definition);
	if (CollectionUtils.isNotEmpty(comboValues)) {
	    for (int i = 0; i < cellValues.length; i++) {
		if (!comboValues.contains(cellValues[i])) {
		    continue;
		}
		descriptions.add(new XlsDescription(definition, cellValues[i], baseType));
	    }
	} else {
	    for (int i = 0; i < cellValues.length; i++) {
		descriptions.add(new XlsDescription(definition, cellValues[i], baseType));
	    }
	}
    }

    private void addTermEntryDescriptions(List<XlsDescription> descriptions, String headerCell, String baseType,
	    String rowCell) {
	if (isLocaleNull(headerCell)) {
	    addDescriptions(descriptions, headerCell, baseType, rowCell);
	}
    }

    private void addWarnings(ImportValidationCallback report, String attrName, String rowCell) {
	Set<String> comboValues = getComboValuesPerAttribute(attrName);
	if (CollectionUtils.isNotEmpty(comboValues)) {
	    if (!rowCell.contains(StringConstants.MULTIPLICITY_OPERATOR) && !comboValues.contains(rowCell)) {
		report.addAlert(AlertSubject.ATTRIBUTE_CHECK, AlertType.WARNING, String
			.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.13"), rowCell, comboValues));
	    } else {
		multipleComboAddWarnings(report, rowCell, comboValues);
	    }
	}
    }

    private ReturnCode basicHeaderValidation(List<String> headerColumns, ImportValidationCallback info) {
	if (headerColumns == null || headerColumns.size() == 0) {
	    info.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR,
		    MessageResolver.getMessage("DefaultXlsTermEntryReader.2")); // $NON-NLS-1$
	    return ReturnCode.FILE_END;
	}
	return ReturnCode.FIELD;
    }

    private ReturnCode collectTermEntryDescriptionTypes(List<String> headerColumns, Set<String> termEntryAttributes,
	    MutableInt columnIndex, ImportValidationCallback info) {

	int idColumnCount = 0;
	for (String headerCell : headerColumns) {
	    if (StringUtils.isBlank(headerCell)) {
		columnIndex.increment();
		continue;
	    }

	    /* loop until the first language column */
	    if (!isLocaleNull(headerCell)) {
		break;
	    }
	    // ..with the exception of TermEntryID
	    columnIndex.increment();
	    String idKey = XlsHelper.TERM_ENTRY_ID;
	    if (headerCell.equals(idKey)) {
		info.setIdColumnExist(true);
		idColumnCount++;
		continue;
	    }
	    termEntryAttributes.add(headerCell.trim());
	}
	/* Shown an error if there are multiple id columns */
	if (idColumnCount > 1) {
	    info.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR,
		    String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.3"), XlsHelper.TERM_ENTRY_ID));
	    return ReturnCode.FILE_END;
	}

	return ReturnCode.FIELD;
    }

    private void countDescriptions(AbstractValidationCallback info, Map<String, Integer> descriptionsCount,
	    String localeCode, String rowCell) {
	String[] cellValues = rowCell.split(XlsHelper.DELIMITER);
	for (int i = 0; i < cellValues.length; i++) {
	    info.incrementCountPerLanguage(descriptionsCount, localeCode);
	}
    }

    private void countTermEntryDescriptions(AbstractValidationCallback info, Map<String, Integer> descriptionsCount,
	    String headerCell, String rowCell) {
	if (isLocaleNull(headerCell)) {
	    countDescriptions(info, descriptionsCount, headerCell, rowCell);
	}
    }

    private String extractLocaleCode(String column) {
	if (Locale.checkLocale(column)) {
	    String columnText = removeDigitIfExist(column);
	    return Locale.makeLocale(columnText).getCode();
	} else if (!column.contains(StringConstants.COLON)) {
	    String columnText = removeDigitIfExist(column);
	    if (Locale.checkLocale(columnText)) {
		return Locale.makeLocale(columnText).getCode();
	    }
	}

	return null;
    }

    private Set<String> getComboValuesPerAttribute(String type) {
	Set<String> comboValues = new HashSet<>();

	Map<String, Set<String>> comboValuesPerAttribute = getTermEntryReaderConfig().getComboValuesPerAttribute();
	if (MapUtils.isEmpty(comboValuesPerAttribute)) {
	    return comboValues;
	}

	if (comboValuesPerAttribute.containsKey(type)) {
	    comboValues.addAll(comboValuesPerAttribute.get(type));
	}

	return comboValues;
    }

    private TermEntryReaderConfig getTermEntryReaderConfig() {
	return _termEntryReaderConfig;
    }

    private boolean isLangAllowed(String language) {
	List<String> allowedLanguageIds = getTermEntryReaderConfig().getUserProjectLanguages();
	return allowedLanguageIds != null && allowedLanguageIds.contains(language);
    }

    private boolean isLocaleNull(String headerCell) {
	String[] headerParts = headerCell.split(StringConstants.COLON);
	return extractLocaleCode(headerParts[0]) == null;
    }

    private boolean isUnknowStatus(String termStatus) {
	return !ALLOWED_STATUSES.contains(termStatus) && !ILLEGAL_STATUSES.contains(termStatus);
    }

    private void multipleComboAddWarnings(ImportValidationCallback report, String rowCell, Set<String> comboValues) {

	String[] multipleComboValues = rowCell.split(StringConstants.MULTIPLICITY_OPERATOR);

	for (String comboValue : multipleComboValues) {

	    if (!comboValues.contains(comboValue)) {
		report.addAlert(AlertSubject.ATTRIBUTE_CHECK, AlertType.WARNING, String
			.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.13"), comboValue, comboValues));
	    }
	}
    }

    private void populateAnalyzeInfo(ImportValidationCallback report, Set<String> identifiers, String headerCell,
	    Map<String, Integer> statusPerLanguage, String rowCell) {

	String[] split = headerCell.split(StringConstants.COLON);

	if (split.length == 2) {
	    if (split[1].equals(STATUS)) {
		report.incrementCountPerLanguage(statusPerLanguage, split[0]);
		if (ILLEGAL_STATUSES.contains(rowCell)) {
		    report.addAlert(AlertSubject.STATUS_CHECK, AlertType.WARNING,
			    String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.11"), headerCell));
		} else if (isUnknowStatus(rowCell)) {
		    report.addAlert(AlertSubject.STATUS_CHECK, AlertType.WARNING, String
			    .format(MessageResolver.getMessage("DefaultXlsTermEntryReader.12"), rowCell, headerCell));
		}
	    } else {
		String attrName = split[1];
		addWarnings(report, attrName, rowCell);
		String localeCode = extractLocaleCode(split[0]);
		countDescriptions(report, report.getLanguageAttributeMap(), localeCode, rowCell);
	    }
	} else if (split.length == 3 && split[1].equals(NOTE)) {
	    String attrName = split[2];
	    addWarnings(report, attrName, rowCell);
	}
    }

    private void readColonCell(Map<String, List<XlsTerm>> identifierTermMap, String headerCell, String rowCell) {
	String xlsTermStatus = null;
	List<XlsDescription> descriptions = new ArrayList<XlsDescription>();

	String[] headerCellParts = headerCell.split(StringConstants.COLON);
	if (headerCellParts.length == 2) {
	    if (headerCellParts[1].equals(STATUS)) {
		xlsTermStatus = rowCell;
	    } else {
		addDescriptions(descriptions, headerCellParts[1], Description.ATTRIBUTE, rowCell);
	    }
	}
	if (headerCellParts.length == 3) {
	    addDescriptions(descriptions, headerCellParts[2], Description.NOTE, rowCell);
	}

	String identifier = headerCellParts[0].trim();
	List<XlsTerm> terms = identifierTermMap.get(identifier);
	if (CollectionUtils.isNotEmpty(terms)) {
	    for (XlsTerm term : terms) {
		if (CollectionUtils.isNotEmpty(descriptions)) {
		    term.getDescriptions().addAll(descriptions);
		} else {
		    term.setXlsStatus(xlsTermStatus);
		}
	    }
	}
    }

    private String removeDigitIfExist(String column) {
	String digit = CharMatcher.DIGIT.retainFrom(column);
	if (StringUtils.isEmpty(digit)) {
	    return column;
	}
	return column.substring(0, column.indexOf(digit));
    }

    /*
     * Validate status, term attribute and term note header columns
     */
    private ReturnCode validateColonCells(List<String> headerColumns, Set<String> identifiers,
	    ImportValidationCallback info, int columnIndex) {
	final int headerSize = headerColumns.size();

	if (headerSize > columnIndex) {
	    for (int i = columnIndex; i < headerSize; i++) {
		int headerColumnIndex = i + 1;
		String headerCell = headerColumns.get(i);
		if (StringUtils.isBlank(headerCell)) {
		    continue;
		}

		// Invalid type in file header
		if (extractLocaleCode(headerCell) == null) {
		    if (!(headerCell.contains(StringConstants.COLON))) {
			info.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR,
				String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.6"), // $NON-NLS-1$
					headerCell, headerColumnIndex));
			return ReturnCode.FILE_END;
		    }

		    String[] split = headerCell.split(StringConstants.COLON);
		    String identifier = split[0].trim();

		    String partTwo = split[1].trim();

		    // Invalid language code in file header
		    if (extractLocaleCode(identifier) == null || StringUtils.isBlank(partTwo)) {
			info.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR,
				String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.6"), headerCell,
					headerColumnIndex)); // $NON-NLS-1$
			return ReturnCode.FILE_END;
		    }

		    if (split.length == 2) {
			// No matching term for status & attribute column
			if (!identifiers.contains(identifier)) {
			    info.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR, String.format(
				    MessageResolver.getMessage("DefaultXlsTermEntryReader.7"), identifier, headerCell)); //$NON-NLS-1$
			    return ReturnCode.FILE_END;
			} else if (!partTwo.equals(STATUS)) {
			    // Collect term attrubutes for report
			    info.getTermDescriptions().add(partTwo);
			}
		    }

		    if (split.length == 3) {
			String noteType = split[2].trim();
			// Validate term note header column
			if (!(partTwo.equals(NOTE))) {
			    info.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR, String.format(
				    MessageResolver.getMessage("DefaultXlsTermEntryReader.8"), headerColumnIndex));
			    return ReturnCode.FILE_END;
			}

			// Case when no matching term for note column
			if (!headerColumns.contains(identifier)) {
			    info.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR,
				    String.format(MessageResolver.getMessage("DefaultXlsTermEntryReader.9"), //$NON-NLS-1$
					    identifier, headerCell));
			    return ReturnCode.FILE_END;
			}

			info.getTermNotes().add(noteType);
		    }
		}
	    }
	}
	return ReturnCode.FIELD;
    }

    private ReturnCode validateHeaderLocales(List<String> headerColumns, Set<String> identifiers,
	    ImportValidationCallback info) {
	List<String> locales = new ArrayList<String>();
	for (String headerCell : headerColumns) {
	    String localeCode = extractLocaleCode(headerCell);
	    if (localeCode != null) {
		locales.add(localeCode);
		identifiers.add(headerCell);
	    }
	}

	if (locales.size() == 0) {
	    info.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR,
		    MessageResolver.getMessage("DefaultXlsTermEntryReader.4")); // $NON-NLS-1$
	    return ReturnCode.FILE_END;
	}
	return ReturnCode.FIELD;
    }

    @Override
    protected void executeParse(InputStream input) {
	XlsReader xlsReader = new XlsReader(input, true, true);
	List<String> header = xlsReader.getHeaderColumns();
	ReturnCode returnedCode = ReturnCode.FIELD;
	List<String> row = new ArrayList<>();

	while (ReturnCode.FILE_END != returnedCode) {
	    returnedCode = xlsReader.readNextRecord(row);
	    if (row.size() == 0) {
		continue;
	    }

	    String xlsUuId = null;
	    List<XlsDescription> descriptions = new ArrayList<XlsDescription>();

	    Map<String, List<XlsTerm>> identifierTermMap = new TreeMap<String, List<XlsTerm>>(
		    String.CASE_INSENSITIVE_ORDER);

	    boolean isTermEntryDescription = true;
	    for (int i = 0; i < header.size(); i++) {
		String headerCell = header.get(i);
		String rowCell = row.get(i);

		if (StringUtils.isBlank(headerCell)) {
		    continue;
		}

		if (StringUtils.isBlank(rowCell)) {
		    continue;
		}

		if (headerCell.equals(XlsHelper.TERM_ENTRY_ID)) {
		    xlsUuId = rowCell;
		    continue;
		}

		String localeCode = extractLocaleCode(headerCell);
		if (isTermEntryDescription && localeCode == null) {
		    addTermEntryDescriptions(descriptions, headerCell, Description.ATTRIBUTE, rowCell);
		} else if (localeCode != null && isLangAllowed(localeCode)) {
		    isTermEntryDescription = false;
		    String identifier = headerCell.trim();
		    List<XlsTerm> terms = identifierTermMap.get(identifier);
		    if (terms == null) {
			terms = new ArrayList<XlsTerm>();
			identifierTermMap.put(identifier, terms);
		    }
		    terms.add(new XlsTerm(rowCell, new ArrayList<XlsDescription>(), localeCode));
		} else {
		    readColonCell(identifierTermMap, headerCell, rowCell);
		}
	    }

	    Set<Entry<String, List<XlsTerm>>> entries = identifierTermMap.entrySet();
	    List<XlsTerm> xlsTerms = new ArrayList<XlsTerm>();
	    for (Entry<String, List<XlsTerm>> entry : entries) {
		xlsTerms.addAll(entry.getValue());
	    }

	    if (xlsUuId == null && CollectionUtils.isEmpty(xlsTerms)) {
		continue; // Do not offer term entries without terms or id.
	    }

	    offerItem(new XlsTermEntry(xlsTerms, descriptions, xlsUuId));
	}
    }
}

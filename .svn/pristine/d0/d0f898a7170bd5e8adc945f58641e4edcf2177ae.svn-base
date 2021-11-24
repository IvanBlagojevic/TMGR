package org.gs4tr.termmanager.service.file.analysis;

import static java.lang.Integer.valueOf;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisAlerts;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttributeReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguage;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguageReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportTypeReport;
import org.gs4tr.termmanager.service.impl.ImportValidationCallback;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Package private supporting class used to transform
 * <code>ImportValidationCallback</code> to more user friendly
 * <code>FileAnalysisReport</code> model.
 * </p>
 * 
 * @since 5.0
 */
@Component
class ImportValidationCallbackTransformer {

    private static final Log LOG = LogFactory.getLog(ImportValidationCallbackTransformer.class);

    private List<ImportAttribute> collectImportAttributes(ImportValidationCallback callback) {
	final List<ImportAttribute> attributes = new ArrayList<>();
	attributes.addAll(transformToImportAttributes(callback.getTermEntryDescriptions(), Level.TERM_ENTRY));
	attributes.addAll(transformToImportAttributes(callback.getTermDescriptions(), Level.TERM_ATTRIBUTE));
	attributes.addAll(transformToImportAttributes(callback.getTermNotes(), Level.TERM_NOTE));
	return attributes;
    }

    private List<ImportAttribute> transformToImportAttributes(Set<String> descriptions, Level level) {
	return CollectionUtils.isEmpty(descriptions) ? Collections.emptyList()
		: descriptions.stream().map(name -> new ImportAttribute(name, level)).collect(toList());
    }

    FileAnalysisReport transformToFileAnalysisReport(ImportValidationCallback validationCallback) {
	List<Alert> alerts = validationCallback.getAlerts();
	String fileName = validationCallback.getFileName();

	Map<AlertType, List<Alert>> alertsByType = alerts.stream().collect(groupingBy(Alert::getType));

	FileAnalysisReport result = new FileAnalysisReport();
	FileAnalysisAlerts fileAnalysisAlerts = new FileAnalysisAlerts(fileName);
	result.setFileAnalysisAlerts(fileAnalysisAlerts);

	List<Alert> errorAlerts = alertsByType.get(AlertType.ERROR);
	if (CollectionUtils.isNotEmpty(errorAlerts)) {
	    errorAlerts.stream().map(Alert::toString).forEach(LOG::error);
	    fileAnalysisAlerts.getAlerts().addAll(errorAlerts);
	    return result;
	}

	fileAnalysisAlerts.getAlerts().addAll(alerts);
	result.setTermEntryIdExist(validationCallback.isIdColumnExist());

	ImportLanguageReport importLanguageReport = new ImportLanguageReport(fileName);
	importLanguageReport.setNumberOfTermEntries(validationCallback.getTotalTerms());

	Map<String, Integer> numberOfAttributesByLanguage = validationCallback.getLanguageAttributeMap();
	Map<String, Integer> numberOfTermsByLanguage = validationCallback.getLanguageMap();

	for (Entry<String, Integer> entry : numberOfTermsByLanguage.entrySet()) {
	    final String localeCode = entry.getKey();

	    ImportLanguage importLanguage;
	    try {
		importLanguage = new ImportLanguage(localeCode);
	    } catch (Exception e) {
		String message = String.format(MessageResolver.getMessage("ImportValidationCallback.1"), localeCode); //$NON-NLS-1$
		result.getFileAnalysisAlerts().getAlerts()
			.add(new Alert(AlertSubject.HEADER_CHECK, AlertType.ERROR, message));
		continue;
	    }

	    importLanguage.setNumberOfTerms(entry.getValue().longValue());
	    importLanguage.setNumberOfAttributes(numberOfAttributesByLanguage.getOrDefault(localeCode, valueOf(0)));

	    importLanguageReport.getImportLanguages().add(importLanguage);
	}

	result.setImportLanguageReport(importLanguageReport);

	ImportAttributeReport importAttributeReport = new ImportAttributeReport(fileName);
	importAttributeReport.getImportAttributes().addAll(collectImportAttributes(validationCallback));
	result.setImportAttributeReport(importAttributeReport);

	result.setImportTypeReport(new ImportTypeReport(fileName, validationCallback.isIdColumnExist()));

	return result;
    }
}

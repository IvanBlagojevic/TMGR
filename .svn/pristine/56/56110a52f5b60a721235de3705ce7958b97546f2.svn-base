package org.gs4tr.termmanager.service.file.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttributeReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguage;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguageReport;
import org.gs4tr.termmanager.service.impl.ImportValidationCallback;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("fileAnalysis")
public class ImportValidationCallbackTransformerTest extends AbstractSpringFilesAnalysisTest {

    @Autowired
    private ImportValidationCallbackTransformer _importValidationCallbackTransformer;

    @Test
    @TestCase("importValidationCallbackHasInvalidLanguageCode")
    public void importValidationCallbackHasInvalidLanguageCode() {

	ImportValidationCallback callback = getModelObject("importValidationCallback", ImportValidationCallback.class);
	String invalidLanguageCode = "en-MK";
	FileAnalysisReport result = getImportValidationCallbackTransformer().transformToFileAnalysisReport(callback);
	List<Alert> alerts = result.getFileAnalysisAlerts().getAlerts();
	assertNotNull(alerts);
	assertTrue(CollectionUtils.isNotEmpty(alerts));

	for (Alert alert : alerts) {
	    assertEquals(alert.getMessage(),
		    String.format(MessageResolver.getMessage("ImportValidationCallback.1"), invalidLanguageCode));
	    assertEquals(alert.getSubject(), AlertSubject.HEADER_CHECK);
	    assertEquals(alert.getType(), AlertType.ERROR);
	}

    }

    @Test
    @TestCase("importValidationCallbackHasErrorAlerts")
    public void transformToFileAnalysisReportWhenImportValidationCallbackHasErrorAlertsTest() {
	ImportValidationCallback callback = getModelObject("importValidationCallback", ImportValidationCallback.class);

	FileAnalysisReport report = getImportValidationCallbackTransformer().transformToFileAnalysisReport(callback);

	assertNull(report.getImportLanguageReport());
	assertNull(report.getImportAttributeReport());

	List<Alert> alerts = report.getFileAnalysisAlerts().getAlerts();
	assertEquals(1, alerts.size());

	assertEquals(AlertType.ERROR, alerts.get(0).getType());
    }

    @Test
    @TestCase("importValidationCallbackHasNoErrorAlerts")
    public void transformToFileAnalysisReportWhenImportValidationCallbackHasNoErrorAlertsTest() {
	ImportValidationCallback callback = getModelObject("importValidationCallback", ImportValidationCallback.class);

	FileAnalysisReport report = getImportValidationCallbackTransformer().transformToFileAnalysisReport(callback);

	List<Alert> alerts = report.getFileAnalysisAlerts().getAlerts();
	assertEquals(1, alerts.size());

	Alert multipleWorksheetsAlert = alerts.get(0);
	assertEquals(AlertSubject.MULTIPLE_WORKSHEETS, multipleWorksheetsAlert.getSubject());

	ImportLanguageReport importLanguageReport = report.getImportLanguageReport();
	assertEquals(callback.getFileName(), importLanguageReport.getFileName());
	assertEquals(2, importLanguageReport.getNumberOfTermEntries());

	List<ImportLanguage> importLanguages = importLanguageReport.getImportLanguages();
	assertEquals(3, importLanguages.size());

	ImportLanguage english = find(importLanguages, language -> Locale.US.equals(language.getLocale()));
	assertEquals(2, english.getNumberOfTerms());
	assertEquals(0, english.getNumberOfAttributes());

	ImportLanguage germany = find(importLanguages, language -> Locale.GERMANY.equals(language.getLocale()));
	assertEquals(4, germany.getNumberOfTerms());
	assertEquals(2, germany.getNumberOfAttributes());

	ImportLanguage france = find(importLanguages, language -> Locale.FRANCE.equals(language.getLocale()));
	assertEquals(2, france.getNumberOfTerms());
	assertEquals(2, france.getNumberOfAttributes());

	ImportAttributeReport importAttributeReport = report.getImportAttributeReport();
	assertEquals(callback.getFileName(), importAttributeReport.getFileName());

	List<ImportAttribute> importAttributes = importAttributeReport.getImportAttributes();
	assertEquals(3, importAttributes.size());

	ImportAttribute definition = find(importAttributes, attribute -> attribute.getName().equals("definition"));
	assertEquals(Level.TERM_ENTRY, definition.getLevel());

	ImportAttribute context = find(importAttributes, attribute -> attribute.getName().equals("context"));
	assertEquals(Level.TERM_ATTRIBUTE, context.getLevel());

	ImportAttribute usage = find(importAttributes, attribute -> attribute.getName().equals("Usage"));
	assertEquals(Level.TERM_NOTE, usage.getLevel());

	assertTrue(report.isTermEntryIdExist());
    }

    private <T> T find(Collection<T> c, Predicate<T> condition) {
	return c.stream().filter(condition).findFirst().orElseThrow(NoSuchElementException::new);
    }

    private ImportValidationCallbackTransformer getImportValidationCallbackTransformer() {
	return _importValidationCallbackTransformer;
    }
}

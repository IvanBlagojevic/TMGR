package org.gs4tr.termmanager.service.file.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguage;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguageReport;
import org.gs4tr.termmanager.service.file.analysis.model.Status;
import org.gs4tr.termmanager.service.file.analysis.request.Context;
import org.gs4tr.termmanager.service.file.analysis.request.Context.Builder;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@SuppressWarnings("unchecked")
@TestSuite("fileAnalysis")
public class SourceLanguagePostProcessorChainTest extends AbstractSpringFilesAnalysisTest {

    @Autowired
    @Qualifier("sourceLanguagePostProcessorChain")
    private PostProcessorChain _postProcessorChain;

    @Test
    @TestCase("commonSourceLanguageDoesNotExist")
    public void postProcessWhenCommonSourceLanguageDoesNotExistTest() {
	FileAnalysisReport report = getModelObject("report", FileAnalysisReport.class);

	Set<String> userProjectLanguageCodes = getModelObject("userProjectLanguageCodes", Set.class);

	getPostProcessorChain().postProcess(report,
		new Builder(userProjectLanguageCodes, false).sourceLanguage(Locale.US.getCode()).build());

	Alert noCommonSourceLanguageAlert = report.getFileAnalysisAlerts().getAlerts().get(0);

	assertEquals(AlertSubject.NO_COMMON_SOURCE_LANGUAGE, noCommonSourceLanguageAlert.getSubject());
	assertEquals(AlertType.ERROR, noCommonSourceLanguageAlert.getType());
	assertEquals(String.format(MessageResolver.getMessage("SourceLanguagePostProcessor.0"), // $NON-NLS-1$
		Locale.US.getCode()), noCommonSourceLanguageAlert.getMessage());
	
	assertNull(report.getImportLanguageReport());
	assertNull(report.getImportAttributeReport());
    }

    
    @Test
    @TestCase("commonSourceLanguageExist")
    public void postProcessWhenCommonSourceLanguageExistTest() {
	FileAnalysisReport report = getModelObject("report", FileAnalysisReport.class);

	Set<String> userProjectLanguageCodes = getModelObject("userProjectLanguageCodes", Set.class);

	Context.Builder cb = new Context.Builder(userProjectLanguageCodes, true);
	cb.sourceLanguage(Locale.GERMANY.getCode());

	getPostProcessorChain().postProcess(report, cb.build());

	ImportLanguageReport importLanguageReport = report.getImportLanguageReport();
	List<ImportLanguage> importLanguages = importLanguageReport.getImportLanguages();

	ImportLanguage germany = find(importLanguages,
		language -> Locale.GERMANY.getCode().equals(language.getLocale().getCode()));

	assertEquals(Status.SOURCE, germany.getStatus());
    }

    private <T> T find(Collection<T> c, Predicate<T> condition) {
	return c.stream().filter(condition).findFirst().orElseThrow(NoSuchElementException::new);
    }

    private PostProcessorChain getPostProcessorChain() {
	return _postProcessorChain;
    }

}

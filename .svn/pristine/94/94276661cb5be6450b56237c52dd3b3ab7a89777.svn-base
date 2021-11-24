package org.gs4tr.termmanager.service.file.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguage;
import org.gs4tr.termmanager.service.file.analysis.model.Language;
import org.gs4tr.termmanager.service.file.analysis.model.Status;
import org.gs4tr.termmanager.service.file.analysis.request.Context.Builder;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@SuppressWarnings("unchecked")
@TestSuite("fileAnalysis")
public class ImportLanguagePostProcessorChainTest extends AbstractSpringFilesAnalysisTest {

    @Autowired
    @Qualifier("importLanguagePostProcessorChain")
    private PostProcessorChain _postProcessorChain;

    @Test
    @TestCase("importLanguagesDoesNoExistOnProject")
    public void postProcessWhenAllImportLanguagesAreNewAndNoSimilarLanguagesTest() {
	FileAnalysisReport report = getModelObject("report", FileAnalysisReport.class);

	Set<String> userProjectLanguageCodes = getModelObject("userProjectLanguageCodes", Set.class);

	getPostProcessorChain().postProcess(report,
		new Builder(userProjectLanguageCodes, false).sourceLanguage(Locale.US.getCode()).build());

	List<ImportLanguage> importLanguages = report.getImportLanguageReport().getImportLanguages();

	assertTrue(importLanguages.stream().map(ImportLanguage::getStatus).allMatch(Predicate.isEqual(Status.NEW)));

	assertTrue(importLanguages.stream().map(ImportLanguage::getSimilarProjectLanguages)
		.allMatch(CollectionUtils::isEmpty));
    }

    @Test
    @TestCase("similarLanguagesExistOnProject")
    public void postProcessWhenSimilarLanguagesExistOnProjectTest() {
	FileAnalysisReport report = getModelObject("report", FileAnalysisReport.class);

	Set<String> userProjectLanguageCodes = getModelObject("userProjectLanguageCodes", Set.class);

	getPostProcessorChain().postProcess(report,
		new Builder(userProjectLanguageCodes, false).sourceLanguage(Locale.US.getCode()).build());

	List<ImportLanguage> importLanguages = report.getImportLanguageReport().getImportLanguages();

	ImportLanguage importLanguage = importLanguages.stream()
		.filter(language -> Locale.ENGLISH.equals(language.getLocale())).findFirst().get();

	List<Language> similarProjectLanguages = importLanguage.getSimilarProjectLanguages();
	assertEquals(1, similarProjectLanguages.size());

	Language similarProjectLanguage = similarProjectLanguages.get(0);
	assertEquals(Locale.US.getDisplayName(), similarProjectLanguage.getValue());
	assertEquals(Locale.US.getCode(), similarProjectLanguage.getLocale());
    }

    @Test
    @TestCase("importLanguagesExistOnProject")
    public void postProcessWhentAllImportLanguagesExistOnProjectTest() {
	FileAnalysisReport report = getModelObject("report", FileAnalysisReport.class);

	Set<String> userProjectLanguageCodes = getModelObject("userProjectLanguageCodes", Set.class);

	getPostProcessorChain().postProcess(report,
		new Builder(userProjectLanguageCodes, false).sourceLanguage(Locale.US.getCode()).build());

	List<ImportLanguage> importLanguages = report.getImportLanguageReport().getImportLanguages();

	assertTrue(
		importLanguages.stream().map(ImportLanguage::getStatus).allMatch(Predicate.isEqual(Status.EXISTING)));

	assertTrue(importLanguages.stream().map(ImportLanguage::getSimilarProjectLanguages)
		.allMatch(CollectionUtils::isEmpty));
    }

    private PostProcessorChain getPostProcessorChain() {
	return _postProcessorChain;
    }
}

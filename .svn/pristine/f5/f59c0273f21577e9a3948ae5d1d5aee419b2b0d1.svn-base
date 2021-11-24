package org.gs4tr.termmanager.service.file.analysis;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute;
import org.gs4tr.termmanager.service.file.analysis.model.ProjectAttribute;
import org.gs4tr.termmanager.service.file.analysis.model.Status;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;
import org.gs4tr.termmanager.service.file.analysis.request.Context.Builder;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@SuppressWarnings("unchecked")
@TestSuite("fileAnalysis")
public class ImportAttributePostProcessorChainTest extends AbstractSpringFilesAnalysisTest {

    @Autowired
    @Qualifier("importAttributePostProcessorChain")
    private PostProcessorChain _postProcessorChain;

    @Test
    @TestCase("importAttributesExistOnProject")
    public void postProcessWhentAllImportAttributesExistOnProjectTest() {
	FileAnalysisReport report = getModelObject("report", FileAnalysisReport.class);

	Map<Level, Set<String>> attributesByLevel = new HashMap<>(getModelObject("attributesByLevel", Map.class));
	getPostProcessorChain().postProcess(report,
		new Builder(newHashSet(Locale.US.getCode()), false).attributesByLevel(attributesByLevel).build());

	List<ImportAttribute> importAttributes = report.getImportAttributeReport().getImportAttributes();
	assertTrue(
		importAttributes.stream().map(ImportAttribute::getStatus).allMatch(Predicate.isEqual(Status.EXISTING)));
    }

    @Test
    @TestCase("importAttributesDoesNotExistOnProjectAndNotAllAttributesAreAccountedForInTheTable")
    public void postProcessWhentImportAttributeDoesNotExistOnProjectAndNotAllAttributesAreAccountedForInTheTableTest() {
	FileAnalysisReport report = getModelObject("report", FileAnalysisReport.class);

	Map<Level, Set<String>> attributesByLevel = new HashMap<>(getModelObject("attributesByLevel", Map.class));
	getPostProcessorChain().postProcess(report,
		new Builder(newHashSet(Locale.US.getCode()), true).attributesByLevel(attributesByLevel).build());

	List<ImportAttribute> importAttributes = report.getImportAttributeReport().getImportAttributes();

	// Test case: File:[custom1, custom2], Project:[custom2, custom3]

	ImportAttribute newImportAttribute = importAttributes.stream()
		.filter(attribute -> attribute.getName().equals("custom1")).findFirst().get();
	assertEquals(Status.NEW, newImportAttribute.getStatus());

	List<ProjectAttribute> projectAttributes = newImportAttribute.getProjectAttributes();
	assertEquals(2, projectAttributes.size());

	for (final ProjectAttribute projectAttribute : projectAttributes) {
	    Set<String> existingProjectAttributes = attributesByLevel.get(projectAttribute.getLevel());
	    assertTrue(existingProjectAttributes.contains(projectAttribute.getName()));
	    LOG.debug(projectAttribute.toString());
	}

	assertTrue(importAttributes.stream().filter(Predicate.isEqual(newImportAttribute).negate())
		.map(ImportAttribute::getStatus).allMatch(Predicate.isEqual(Status.EXISTING)));
    }

    @Test
    @TestCase("importAttributesDoesNotExistOnProjectAndAllAttributesAreAccountedForInTheTable")
    public void postProcessWhentImportAttributesDoesNotExistOnProjectAndAllAttributesAreAccountedForInTheTableTest() {
	FileAnalysisReport report = getModelObject("report", FileAnalysisReport.class);

	Map<Level, Set<String>> attributesByLevel = new HashMap<>(getModelObject("attributesByLevel", Map.class));
	getPostProcessorChain().postProcess(report,
		new Builder(newHashSet(Locale.US.getCode()), false).attributesByLevel(attributesByLevel).build());

	List<ImportAttribute> importAttributes = report.getImportAttributeReport().getImportAttributes();

	// File: [custom1, custom2, custom3], Project: [custom2, custom3]

	ImportAttribute newImportAttribute = importAttributes.stream()
		.filter(attribute -> attribute.getName().equals("custom1")).findFirst().get();

	assertEquals(Status.NEW, newImportAttribute.getStatus());

	assertTrue(importAttributes.stream().filter(Predicate.isEqual(newImportAttribute).negate())
		.map(ImportAttribute::getStatus).allMatch(Predicate.isEqual(Status.EXISTING)));
    }

    @Test
    @TestCase("importAttributesDoesNotExistOnProject")
    public void postProcessWhentImportAttributesDoesNotExistOnProjectTest() {
	FileAnalysisReport report = getModelObject("report", FileAnalysisReport.class);

	getPostProcessorChain().postProcess(report, new Builder(newHashSet(Locale.US.getCode()), false).build());

	List<ImportAttribute> importAttributes = report.getImportAttributeReport().getImportAttributes();
	
	assertTrue(importAttributes.stream().map(ImportAttribute::getStatus).allMatch(Predicate.isEqual(Status.NEW)));
    }

    private PostProcessorChain getPostProcessorChain() {
	return _postProcessorChain;
    }
}

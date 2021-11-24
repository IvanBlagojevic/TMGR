package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

public class TermServiceTest extends AbstractSolrGlossaryTest {

    private static final Long PROJECT_ID = 1L;

    @Test
    public void testDeleteTerm() {

	TermEntry termEntry = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_01, PROJECT_ID);

	Term term = termEntry.ggetTermById(TERM_ID_02);
	Assert.assertNotNull(term);

	List<UpdateCommand> targetCommands = new ArrayList<>();
	UpdateCommand targetUpdateCommand = new UpdateCommand();
	targetUpdateCommand.setCommand("remove");
	targetUpdateCommand.setItemType("term");
	targetUpdateCommand.setMarkerId(TERM_ID_02);
	targetUpdateCommand.setLanguageId("de-DE");
	targetCommands.add(targetUpdateCommand);

	List<TranslationUnit> translationUnits = new ArrayList<>();
	TranslationUnit unit = new TranslationUnit();
	unit.setTargetTermUpdateCommands(targetCommands);
	unit.setTermEntryId(TERM_ENTRY_ID_01);

	translationUnits.add(unit);
	getTermEntryService().updateTermEntries(translationUnits, "en-US", PROJECT_ID, Action.EDITED);

	termEntry = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_01, PROJECT_ID);
	Assert.assertEquals(Action.EDITED, termEntry.getAction());
	term = termEntry.ggetTermById(TERM_ID_02);
	Assert.assertNull(term);
    }

    @Test
    public void testFindHistoryTermEntryIds() {

	Map<String, List<TermEntry>> history = getTermEntryService()
		.findHistoriesByTermEntryIds(Arrays.asList(TERM_ENTRY_ID_01));

	Assert.assertNotNull(history);
	List<TermEntry> entries = history.get(TERM_ENTRY_ID_01);

	Assert.assertFalse(entries.isEmpty());
	Assert.assertEquals(1, history.size());
    }

    @Test
    public void testGetAllTermsInTermEntry() {
	List<Term> terms = getTermService().getAllTermsInTermEntry(TERM_ENTRY_ID_01, PROJECT_ID);

	assertTrue(CollectionUtils.isNotEmpty(terms));
    }

    @Test
    public void testGetTermsByMarkerIds() {
	List<String> markerIds = new ArrayList<>();
	markerIds.add(TERM_ID_01);

	List<Term> terms = getTermService().findTermsByIds(markerIds, Arrays.asList(PROJECT_ID));
	assertNotNull(terms);
	assertTrue(CollectionUtils.isNotEmpty(terms));
	assertEquals(1, terms.size());
	assertEquals(terms.get(0).getUuId(), TERM_ID_01);
    }

    @Test
    public void testGetTermsByTermEntryIds() {
	List<String> termIds = new ArrayList<>();
	termIds.add(TERM_ID_01);
	termIds.add(TERM_ID_02);

	List<Term> terms = getTermService().findTermsByIds(termIds, Arrays.asList(PROJECT_ID));
	assertTrue(CollectionUtils.isNotEmpty(terms));
    }

    @Test
    public void testRenameTermDescriptions() {
	Long projectId = PROJECT_ID;

	String newAttributeName = "custom";

	String oldAttributeName = "definition";

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);

	assertNotNull(project);

	List<Attribute> attributes = project.getAttributes();

	assertTrue(CollectionUtils.isNotEmpty(attributes));

	List<Attribute> projectAttributes = new ArrayList<>();

	for (Attribute attribute : attributes) {
	    if (oldAttributeName.equals(attribute.getName())) {
		attribute.setRenameValue(newAttributeName);
		projectAttributes.add(attribute);
	    }
	}

	Term beforeTerm = getTermService().findTermById(TERM_ID_01, projectId);

	Set<Description> beforeTermAttributes = beforeTerm.getDescriptions().stream()
		.filter(d -> Description.ATTRIBUTE.equals(d.getBaseType())).collect(Collectors.toSet());
	assertTrue(CollectionUtils.isNotEmpty(beforeTermAttributes));

	// RENAME
	List<String> projectLanguages = getProjectService().getProjectLanguageCodes(projectId);
	getTermService().renameTermDescriptions(projectId, Description.ATTRIBUTE, projectAttributes, projectLanguages);

	Term afterTerm = getTermService().findTermById(TERM_ID_01, projectId);

	Set<Description> afterTermAttributes = afterTerm.getDescriptions().stream()
		.filter(d -> Description.ATTRIBUTE.equals(d.getBaseType())).collect(Collectors.toSet());
	assertTrue(CollectionUtils.isNotEmpty(afterTermAttributes));

	List<Description> reNamed = afterTermAttributes.stream().filter(a -> a.getType().equals(newAttributeName))
		.collect(Collectors.toList());

	assertTrue(CollectionUtils.isNotEmpty(reNamed));
    }

    @Test
    public void updateDisabledStatusTest() throws TmException {
	String termEntryId = TERM_ENTRY_ID_01;
	String termId = TERM_ID_01;

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	Term term = termEntry.ggetTermById(termId);

	Assert.assertEquals(false, term.isDisabled());

	term.setDisabled(Boolean.TRUE);

	getRegularConnector().getTmgrUpdater().update(termEntry);

	termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	term = termEntry.ggetTermById(termId);

	Assert.assertNull(term);
    }
}

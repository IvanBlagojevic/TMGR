package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

public class SaveDashboardTest extends AbstractSolrGlossaryTest {

    @Test
    // adding new terms
    public void addNewTermsTest() {
	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	UpdateCommand sourceUpdateCommand = new UpdateCommand();
	sourceUpdateCommand.setCommand("add");
	sourceUpdateCommand.setItemType("term");
	String sourceTermMarkerId = UUID.randomUUID().toString();
	sourceUpdateCommand.setMarkerId(sourceTermMarkerId);
	sourceUpdateCommand.setValue("sourceTerm");
	sourceUpdateCommand.setLanguageId(Locale.US.getCode());
	sourceUpdateCommand.setStatus(ItemStatusTypeHolder.WAITING.getName());
	updateCommands.add(sourceUpdateCommand);

	List<UpdateCommand> targetCommands = new ArrayList<UpdateCommand>();
	UpdateCommand targetUpdateCommand = new UpdateCommand();
	targetUpdateCommand.setCommand("add");
	targetUpdateCommand.setItemType("term");

	String targetMarkerId = UUID.randomUUID().toString();
	targetUpdateCommand.setMarkerId(targetMarkerId);
	targetUpdateCommand.setValue("targetTerm");
	targetUpdateCommand.setLanguageId(Locale.GERMANY.getCode());
	targetUpdateCommand.setStatus(ItemStatusTypeHolder.BLACKLISTED.getName());
	targetCommands.add(targetUpdateCommand);

	List<TranslationUnit> translationUnits = new ArrayList<TranslationUnit>();
	TranslationUnit unit = new TranslationUnit();
	unit.setSourceTermUpdateCommands(updateCommands);
	unit.setTargetTermUpdateCommands(targetCommands);
	unit.setTermEntryId(null);

	translationUnits.add(unit);

	Long projectId = 1L;

	getTermEntryService().updateTermEntries(translationUnits, Locale.US.getCode(), projectId, Action.ADDED);

	List<String> markerIds = new ArrayList<String>();
	markerIds.add(sourceTermMarkerId);
	markerIds.add(targetMarkerId);

	List<Term> terms = getTermService().findTermsByIds(markerIds, Arrays.asList(projectId));

	String termStatus = terms.get(0).getStatus();
	assertEquals(ItemStatusTypeHolder.WAITING.getName(), termStatus);
	assertEquals(2, terms.size());
    }

    @Test
    public void addTermEntryDescriptionTest() throws TmException {
	String termEntryId = TERM_ENTRY_ID_01;
	String descriptionText = "some term entry description";

	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	UpdateCommand sourceUpdateCommand = new UpdateCommand();
	sourceUpdateCommand.setCommand("add");
	sourceUpdateCommand.setItemType("description");
	sourceUpdateCommand.setParentMarkerId(termEntryId);
	sourceUpdateCommand.setValue(descriptionText);
	sourceUpdateCommand.setLanguageId(Locale.US.getCode());
	sourceUpdateCommand.setSubType("definition");

	updateCommands.add(sourceUpdateCommand);

	List<TranslationUnit> translationUnits = new ArrayList<TranslationUnit>();
	TranslationUnit unit = new TranslationUnit();
	unit.setSourceTermUpdateCommands(updateCommands);
	unit.setTermEntryId(termEntryId);

	translationUnits.add(unit);

	Long projectId = 1L;

	TermEntry termEntry = getRegularConnector().getTmgrBrowser().findById(termEntryId, projectId);
	Set<Description> termEntryDescriptions = termEntry.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(termEntryDescriptions));
	Assert.assertEquals(2, termEntryDescriptions.size());

	getTermEntryService().updateTermEntries(translationUnits, Locale.US.getCode(), projectId, Action.EDITED);

	termEntry = getRegularConnector().getTmgrBrowser().findById(termEntryId, projectId);
	Assert.assertEquals(Action.EDITED, termEntry.getAction());
	termEntryDescriptions = termEntry.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(termEntryDescriptions));
	Assert.assertEquals(3, termEntryDescriptions.size());

	boolean flag = false;
	for (Description desc : termEntryDescriptions) {
	    if (desc.getValue().equals(descriptionText)) {
		flag = true;
	    }
	}
	Assert.assertTrue(flag);
    }

    @Test
    // updating existing terms
    public void updateExistingTest_01() throws TmException {
	String termEntryId = "test-term-entry-01";

	Term sourceTerm = new Term();
	String sourceTermId = UUID.randomUUID().toString();
	sourceTerm.setUuId(sourceTermId);
	sourceTerm.setName("source term");
	sourceTerm.setLanguageId("en-US");
	sourceTerm.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	sourceTerm.setUserCreated("userCreated");
	sourceTerm.setDateCreated(System.currentTimeMillis());
	sourceTerm.setDateModified(System.currentTimeMillis());

	Term targetTerm = new Term();
	String targetTermId = UUID.randomUUID().toString();
	targetTerm.setUuId(targetTermId);
	targetTerm.setName("target term");
	targetTerm.setLanguageId("de-DE");
	targetTerm.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	targetTerm.setUserCreated("userCreated");
	targetTerm.setDateCreated(System.currentTimeMillis());
	targetTerm.setDateModified(System.currentTimeMillis());

	Long projectId = 1L;

	TermEntry termEntry = new TermEntry();
	termEntry.setProjectId(projectId);
	termEntry.setUuId(termEntryId);
	termEntry.addTerm(sourceTerm);
	termEntry.addTerm(targetTerm);

	getRegularConnector().getTmgrUpdater().save(termEntry);

	termEntry = getRegularConnector().getTmgrBrowser().findById(termEntryId, projectId);
	Assert.assertEquals(2, termEntry.ggetTerms().size());

	List<UpdateCommand> targetCommands = new ArrayList<UpdateCommand>();
	UpdateCommand targetUpdateCommand = new UpdateCommand();
	targetUpdateCommand.setCommand("update");
	targetUpdateCommand.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	targetUpdateCommand.setItemType("term");
	targetUpdateCommand.setMarkerId(targetTermId);
	targetUpdateCommand.setValue("targetTerm");
	targetUpdateCommand.setLanguageId(Locale.GERMANY.getCode());
	targetCommands.add(targetUpdateCommand);

	List<TranslationUnit> translationUnits = new ArrayList<TranslationUnit>();
	TranslationUnit unit = new TranslationUnit();
	unit.setTargetTermUpdateCommands(targetCommands);
	unit.setTermEntryId(termEntryId);

	assertEquals(2, termEntry.ggetTerms().size());

	translationUnits.add(unit);

	getTermEntryService().updateTermEntries(translationUnits, Locale.US.getCode(), projectId, Action.EDITED);

	termEntry = getTermEntryService().findTermEntryById(termEntryId, projectId);
	Assert.assertEquals(Action.EDITED, termEntry.getAction());
	assertEquals(2, termEntry.ggetTerms().size());

	targetTerm = termEntry.ggetTermById(targetTermId);
	Assert.assertEquals("targetTerm", targetTerm.getName());
    }

    @Test
    // updating existing terms
    public void updateExistingTest_02() {
	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	UpdateCommand sourceUpdateCommand = new UpdateCommand();
	sourceUpdateCommand.setCommand("add");
	sourceUpdateCommand.setItemType("term");
	sourceUpdateCommand.setMarkerId(UUID.randomUUID().toString());
	sourceUpdateCommand.setValue("sourceTerm");
	sourceUpdateCommand.setLanguageId(Locale.US.getCode());
	sourceUpdateCommand.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	updateCommands.add(sourceUpdateCommand);

	List<UpdateCommand> targetCommands = new ArrayList<UpdateCommand>();
	UpdateCommand targetUpdateCommand = new UpdateCommand();
	targetUpdateCommand.setCommand("add");
	targetUpdateCommand.setItemType("term");
	targetUpdateCommand.setMarkerId(UUID.randomUUID().toString());
	targetUpdateCommand.setValue("targetTerm");
	targetUpdateCommand.setLanguageId(Locale.GERMANY.getCode());
	targetUpdateCommand.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	targetCommands.add(targetUpdateCommand);

	List<TranslationUnit> translationUnits = new ArrayList<TranslationUnit>();
	TranslationUnit unit = new TranslationUnit();
	unit.setSourceTermUpdateCommands(updateCommands);
	unit.setTargetTermUpdateCommands(targetCommands);
	String termEntryId = TERM_ENTRY_ID_01;
	unit.setTermEntryId(termEntryId);

	translationUnits.add(unit);

	Long projectId = 1L;

	getTermEntryService().updateTermEntries(translationUnits, Locale.US.getCode(), projectId, Action.EDITED);

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, projectId);
	Assert.assertEquals(Action.EDITED, termEntry.getAction());
	List<Term> terms = termEntry.ggetTerms();
	assertEquals(5, terms.size());
    }

    @Test
    // updating existing with descriptions terms
    public void updateExistingWithDescriptionsTest() {
	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	UpdateCommand sourceUpdateCommand = new UpdateCommand();
	sourceUpdateCommand.setCommand("add");
	sourceUpdateCommand.setItemType("term");
	String sourceMarkerId = UUID.randomUUID().toString();
	sourceUpdateCommand.setMarkerId(sourceMarkerId);
	sourceUpdateCommand.setValue("sourceTerm");
	sourceUpdateCommand.setLanguageId(Locale.US.getCode());
	sourceUpdateCommand.setStatus(ItemStatusTypeHolder.PROCESSED.getName());

	UpdateCommand descUpdateCommand = new UpdateCommand();
	descUpdateCommand.setCommand("add");
	descUpdateCommand.setItemType("description");
	descUpdateCommand.setSubType("context");
	descUpdateCommand.setMarkerId(UUID.randomUUID().toString());
	descUpdateCommand.setParentMarkerId(sourceMarkerId);
	descUpdateCommand.setLanguageId(Locale.US.getCode());
	descUpdateCommand.setValue("desc");

	updateCommands.add(sourceUpdateCommand);
	updateCommands.add(descUpdateCommand);

	List<UpdateCommand> targetCommands = new ArrayList<UpdateCommand>();
	UpdateCommand targetUpdateCommand = new UpdateCommand();
	targetUpdateCommand.setCommand("add");
	targetUpdateCommand.setItemType("term");
	targetUpdateCommand.setMarkerId(UUID.randomUUID().toString());
	targetUpdateCommand.setValue("targetTerm");
	targetUpdateCommand.setLanguageId(Locale.GERMANY.getCode());
	targetUpdateCommand.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	targetCommands.add(targetUpdateCommand);

	List<TranslationUnit> translationUnits = new ArrayList<TranslationUnit>();
	TranslationUnit unit = new TranslationUnit();
	unit.setSourceTermUpdateCommands(updateCommands);
	unit.setTargetTermUpdateCommands(targetCommands);
	String termEntryId = TERM_ENTRY_ID_01;
	unit.setTermEntryId(termEntryId);

	translationUnits.add(unit);

	Long projectId = 1L;

	getTermEntryService().updateTermEntries(translationUnits, Locale.US.getCode(), projectId, Action.EDITED);

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, projectId);
	Assert.assertEquals(Action.EDITED, termEntry.getAction());
	List<Term> terms = termEntry.ggetTerms();
	assertEquals(5, terms.size());

	List<String> markerIds = new ArrayList<String>();
	markerIds.add(sourceMarkerId);
	List<Term> foundTerms = getTermService().findTermsByIds(markerIds, Arrays.asList(projectId));

	Term term = foundTerms.get(0);

	term = getTermService().findTermById(term.getUuId(), projectId);

	Set<Description> termDescriptions = term.getDescriptions();
	assertNotNull(termDescriptions);

	Set<Description> descriptions = term.getDescriptions();
	assertNotNull(descriptions);

	for (Description termDescription : descriptions) {
	    assertEquals("context", termDescription.getType());
	    assertEquals("desc", termDescription.getValue());
	}

    }
}

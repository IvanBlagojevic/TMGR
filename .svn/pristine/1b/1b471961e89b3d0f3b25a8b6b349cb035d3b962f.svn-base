package org.gs4tr.termmanager.service.termentry.synchronization.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.termentry.synchronization.TermEntrySynchronizerFactory;
import org.gs4tr.tm3.api.TmException;
import org.junit.Test;

public class TermEntryMergerTest extends AbstractTermEntrySyncTest {

    // TERII-4703
    @Test
    public void syncNewTermAttributeIgnoreCase() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();

	TermEntry existing = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	existing.setProjectName(PROJECT_NAME);
	existing.setUuId(UUID.randomUUID().toString());

	TermEntry incoming = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	incoming.setProjectName(PROJECT_NAME);
	incoming.setUuId(UUID.randomUUID().toString());

	String existingType = "type";
	Term existingEnTerm = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	existingEnTerm.addDescription(new Description(existingType, "value"));

	String newValue = "new value";
	Term incomingEnTerm = createTerm(EN_LANGUAGE_ID, "New english main term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	incomingEnTerm.addDescription(new Description("TYPE", newValue));

	prepareTermEntry(existing, new Term[] { existingEnTerm }, true);
	prepareTermEntry(incoming, new Term[] { incomingEnTerm }, false);

	Set<String> allowedDesc = new HashSet<>();
	allowedDesc.add(existingType);

	Map<String, Set<String>> allowedDescMap = new HashMap<>();
	allowedDescMap.put(Description.ATTRIBUTE, allowedDesc);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID }));
	_importModel.setAllowedTermDescriptions(allowedDescMap);

	setUpConfiguration(incoming);

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	TermEntry termEntry = getBrowser().findById(incoming.getUuId(), PROJECT_ID);

	assertNotNull(termEntry);

	List<Term> terms = termEntry.ggetTerms();
	assertTrue(CollectionUtils.isNotEmpty(terms));
	assertEquals(1, terms.size());

	for (Term term : terms) {
	    Set<Description> descriptions = term.getDescriptions();
	    assertTrue(CollectionUtils.isNotEmpty(descriptions));
	    assertEquals(1, descriptions.size());

	    for (Description desc : descriptions) {
		assertEquals(existingType, desc.getType());
		assertEquals(newValue, desc.getValue());
	    }
	}
    }

    /*
     **********************************************************
     * TERII-5106 Investigate - Batch import - UI/import stalls
     **********************************************************
     */
    @Test
    public void syncNewTermMultipleEqualExistingTerms() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();

	TermEntry existing = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	existing.setProjectName(PROJECT_NAME);
	existing.setUuId(UUID.randomUUID().toString());

	TermEntry incoming = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	incoming.setProjectName(PROJECT_NAME);
	incoming.setUuId(UUID.randomUUID().toString());

	String existingType = "type";
	Term existingEnTerm1 = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	existingEnTerm1.addDescription(new Description(existingType, "value"));

	// Add equal existing term
	Term existingEnTerm2 = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);

	String descriptionValue = "This desc should be merged to existing term";

	Term incomingEnTerm = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	existingEnTerm1.addDescription(new Description(existingType, descriptionValue));

	prepareTermEntry(existing, new Term[] { existingEnTerm1, existingEnTerm2 }, true);
	prepareTermEntry(incoming, new Term[] { incomingEnTerm }, false);

	Set<String> allowedDesc = new HashSet<>();
	allowedDesc.add(existingType);

	Map<String, Set<String>> allowedDescMap = new HashMap<>();
	allowedDescMap.put(Description.ATTRIBUTE, allowedDesc);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID }));
	_importModel.setAllowedTermDescriptions(allowedDescMap);

	setUpConfiguration(incoming);

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	TermEntry termEntry = getBrowser().findById(incoming.getUuId(), PROJECT_ID);

	assertNull(termEntry);

	Term existingTerm = getBrowser().findTermById(existingEnTerm1.getUuId(), PROJECT_ID);

	// Merged term should not exist
	assertNotNull(existingTerm);

	TermEntry existingTermEntry = getBrowser().findByTermId(existingTerm.getUuId(), PROJECT_ID);

	List<Term> existingTerms = existingTermEntry.ggetAllTerms();

	assertEquals(2, existingTerms.size());

	Description descriptionFromIncomingTerm = null;

	for (Term term : existingTerms) {
	    if (CollectionUtils.isNotEmpty(term.getDescriptions())) {
		descriptionFromIncomingTerm = term.getDescriptions().stream()
			.filter(desc -> desc.getValue().equals(descriptionValue)).findFirst().orElse(null);
	    }
	}

	assertNotNull(descriptionFromIncomingTerm);
    }

    @Override
    protected void assertOverwriteTermAttributeIgnoreCase(String termEntryId, String newValue, String existingType)
	    throws TmException {
	TermEntry termEntry = getBrowser().findById(termEntryId, PROJECT_ID);
	assertNotNull(termEntry);

	List<Term> terms = termEntry.ggetTerms();
	assertTrue(CollectionUtils.isNotEmpty(terms));
	assertEquals(1, terms.size());

	for (Term term : terms) {
	    Set<Description> descriptions = term.getDescriptions();
	    assertTrue(CollectionUtils.isNotEmpty(descriptions));
	    assertEquals(2, descriptions.size());

	    for (Description desc : descriptions) {
		assertEquals(existingType, desc.getType());
	    }
	}
    }

    @Override
    protected void assertOverwriteTermEntryAttributeIgnoreCase(String termEntryId, String newValue, String existingType)
	    throws TmException {
	TermEntry termEntry = getBrowser().findById(termEntryId, PROJECT_ID);
	assertNotNull(termEntry);

	Set<Description> descriptions = termEntry.getDescriptions();
	assertTrue(CollectionUtils.isNotEmpty(descriptions));
	assertEquals(2, descriptions.size());

	for (Description desc : descriptions) {
	    assertEquals(existingType, desc.getType());
	}
    }

    @Override
    protected ITermEntrySynchronizer initTermEntrySynchronizer() {
	return TermEntrySynchronizerFactory.INSTANCE.createTermEntrySynchronizer((SyncOption.APPEND));
    }
}

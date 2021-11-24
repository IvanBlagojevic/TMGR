package org.gs4tr.termmanager.persistence.solr;

import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserProfile;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

public class TmgrGlossaryUpdaterTest extends AbstractSolrGlossaryTest {

    @Test
    public void testAddSynonym() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();
	List<TermEntry> termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());

	TermEntry termEntry = termEntries.get(0);
	Long projectId = termEntry.getProjectId();
	String termEntryId = termEntry.getUuId();

	String languageId = "en";

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	Assert.assertEquals(1, languageTerms.size());

	Set<Term> terms = languageTerms.get(languageId);
	Assert.assertEquals(1, terms.size());

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();

	String updatedName = "synonym term name";

	Term synonym = createTerm(languageId, updatedName, true, "WAITING", "user");

	termEntry.addTerm(synonym);

	// update termEntry
	updater.update(termEntry);

	termEntry = browser.findById(termEntryId, projectId);

	languageTerms = termEntry.getLanguageTerms();

	terms = languageTerms.get(languageId);
	Assert.assertEquals(2, terms.size());

	for (Term term : terms) {
	    if (!term.isFirst()) {
		Assert.assertTrue(term.equals(synonym));
	    }
	}
    }

    @Test
    public void testAddTermEntryAttribute() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();
	List<TermEntry> termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());

	TermEntry termEntry = termEntries.get(0);
	Set<Description> descriptions = termEntry.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(descriptions));
	Assert.assertEquals(1, descriptions.size());

	Long projectId = termEntry.getProjectId();
	String termEntryId = termEntry.getUuId();

	String type = "definition";
	String value = "this is testUpdateTermEntryAttribute";
	Description description = new Description(type, value);

	termEntry.addDescription(description);

	// update term entry attribute
	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.update(termEntry);

	termEntry = browser.findById(termEntryId, projectId);
	descriptions = termEntry.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(descriptions));
	Assert.assertEquals(2, descriptions.size());
    }

    @Test
    public void testBatchUpdate() throws TmException {
	addNewTermEntry();

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();
	List<TermEntry> termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(2, termEntries.size());

	for (TermEntry termEntry : termEntries) {
	    List<Term> terms = termEntry.ggetTerms();
	    Assert.assertNotNull(termEntries);
	    for (Term term : terms) {
		term.setForbidden(true);
	    }
	}

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.update(termEntries);

	termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(2, termEntries.size());

	for (TermEntry termEntry : termEntries) {
	    List<Term> terms = termEntry.ggetTerms();
	    Assert.assertNotNull(termEntries);
	    for (Term term : terms) {
		Assert.assertTrue(term.isForbidden());
	    }
	}
    }

    @Test
    public void testDelete() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();
	List<TermEntry> termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());

	TermEntry entity = termEntries.get(0);
	Long projectId = entity.getProjectId();
	String id = entity.getUuId();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.delete(entity);

	entity = connector.getTmgrBrowser().findById(id, projectId);
	Assert.assertNull(entity);
    }

    @Test
    public void testDeleteByProjects() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();
	List<TermEntry> termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());

	TermEntry entity = termEntries.get(0);
	Long projectId = entity.getProjectId();
	String id = entity.getUuId();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.deleteByProjects(Arrays.asList(projectId));

	entity = connector.getTmgrBrowser().findById(id, projectId);
	Assert.assertNull(entity);
    }

    @Test
    public void testRemoveTermDescription() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();
	List<TermEntry> termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());

	TermEntry termEntry = termEntries.get(0);
	Long projectId = termEntry.getProjectId();
	String termEntryId = termEntry.getUuId();

	String languageId = "en";

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	Assert.assertEquals(1, languageTerms.size());

	Set<Term> terms = languageTerms.get(languageId);
	Assert.assertEquals(1, terms.size());

	Term term = terms.iterator().next();

	Set<Description> descriptions = term.getDescriptions();
	Assert.assertNotNull(descriptions);
	Assert.assertEquals(1, descriptions.size());

	// remove all term descriptions
	// term.setDescriptions(null);
	descriptions.clear();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.update(termEntry);

	termEntry = browser.findById(termEntryId, projectId);

	languageTerms = termEntry.getLanguageTerms();
	Assert.assertEquals(1, languageTerms.size());

	terms = languageTerms.get(languageId);
	Assert.assertEquals(1, terms.size());

	term = terms.iterator().next();

	descriptions = term.getDescriptions();
	Assert.assertNull(descriptions);
    }

    @Test
    public void testRemoveTermEntryDescription() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();
	List<TermEntry> termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());

	TermEntry termEntry = termEntries.get(0);
	Long projectId = termEntry.getProjectId();
	String termEntryId = termEntry.getUuId();

	Set<Description> termEntryDescriptions = termEntry.getDescriptions();
	Assert.assertNotNull(termEntryDescriptions);
	Assert.assertEquals(1, termEntryDescriptions.size());

	// remove all term descriptions
	// term.setDescriptions(null);
	termEntryDescriptions.clear();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.update(termEntry);

	termEntry = browser.findById(termEntryId, projectId);

	termEntryDescriptions = termEntry.getDescriptions();
	Assert.assertNull(termEntryDescriptions);
    }

    @Test
    public void testSave() throws TmException {
	String type1 = "context";
	String value1 = "This is some dummy term attribute context";
	Description description1 = createDescription(type1, value1);

	String type2 = "definition";
	String value2 = "This is some dummy term attribute definition";
	Description description2 = createDescription(type2, value2);

	String languageId = "sr";
	String name = "Dummy term text";
	boolean forbidden = false;
	String status = "PROCESSED";
	String user = "user";

	Term term = createTerm(languageId, name, forbidden, status, user);
	term.addDescription(description1);
	term.addDescription(description2);

	String name1 = "Synonym dummy term text";
	Term term1 = createTerm(languageId, name1, forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = "user";

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.setUuId(UUID.randomUUID().toString());
	termEntry.addTerm(term);
	termEntry.addTerm(term1);

	String type3 = "definition";
	String value3 = "This is some dummy term entry attribute";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();
	TermEntry foundTermEntry = browser.findById(termEntry.getUuId(), projectId);

	Assert.assertNotNull(foundTermEntry);

	Set<Description> foundTermEntryDescriptions = foundTermEntry.getDescriptions();
	Assert.assertNotNull(foundTermEntryDescriptions);

	Map<String, Set<Term>> languageTerms = foundTermEntry.getLanguageTerms();
	Assert.assertNotNull(languageTerms);

	Set<Term> terms = languageTerms.get(languageId);

	Assert.assertEquals(termEntry, foundTermEntry);
	Assert.assertEquals(1, foundTermEntryDescriptions.size());
	Assert.assertTrue(foundTermEntryDescriptions.contains(description3));
	Assert.assertEquals(2, terms.size());
	Assert.assertTrue(terms.contains(term));

	Iterator<Term> termIterator = terms.iterator();
	Term foundTerm = termIterator.next();
	if (!foundTerm.equals(term)) {
	    foundTerm = termIterator.next();
	}
	Set<Description> termDescriptions = foundTerm.getDescriptions();

	Assert.assertEquals(2, termDescriptions.size());
	Assert.assertTrue(termDescriptions.contains(description1));
	Assert.assertTrue(termDescriptions.contains(description2));
    }

    @Test
    public void testUpdate() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();
	List<TermEntry> termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());

	TermEntry entity = termEntries.get(0);
	Assert.assertNotNull(entity.getUserModified());
	Assert.assertEquals("super", entity.getUserModified());

	Long projectId = entity.getProjectId();
	String uuId = entity.getUuId();

	UserProfile userProfile = UserProfileContext.getCurrentUserProfile();
	UserInfo userInfo = userProfile.getUserInfo();
	userInfo.setUserName(SDULIN);

	TermEntry updatedEntity = new TermEntry();
	updatedEntity.setUuId(uuId);
	updatedEntity.setProjectId(projectId);
        updatedEntity.setUserModified(SDULIN);

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.update(updatedEntity);

	TermEntry foundTermEntry = browser.findById(uuId, projectId);
	Assert.assertEquals(SDULIN, foundTermEntry.getUserModified());

	termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());
    }

    @Test
    public void testUpdateExistingTermName() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryBrowser browser = connector.getTmgrBrowser();
	List<TermEntry> termEntries = browser.findAll();
	Assert.assertNotNull(termEntries);
	Assert.assertEquals(1, termEntries.size());

	TermEntry termEntry = termEntries.get(0);
	Long projectId = termEntry.getProjectId();
	String termEntryId = termEntry.getUuId();

	String languageId = "en";

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	Assert.assertEquals(1, languageTerms.size());

	Set<Term> terms = languageTerms.get(languageId);
	Assert.assertEquals(1, terms.size());

	Term term = terms.iterator().next();
	// String termText = term.getName();

	String updatedName = "updated term name";

	term.setName(updatedName);

	// update term text
	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.update(termEntry);

	termEntry = browser.findById(termEntryId, projectId);

	languageTerms = termEntry.getLanguageTerms();
	Assert.assertEquals(1, languageTerms.size());

	terms = languageTerms.get(languageId);
	Assert.assertEquals(1, terms.size());

	term = terms.iterator().next();
	Assert.assertEquals(updatedName, term.getName());
    }

    /*
     * [Bug#TERII-4444]: Terms for the migrated submissions are not editable in term
     * list when we complete this subs.
     */
    @Test
    public void testUpdateInTranslationAsSourceNoParentUuIdTest() throws TmException {
	final String termEntryUuid = UUID.randomUUID().toString();
	TermEntry termEntry = new TermEntry(PROJECT_ID, "Nikon", SDULIN);
	termEntry.setUuId(termEntryUuid);

	Term source = new Term(LANGUAGE, "In submission as source", Boolean.FALSE, STATUS, SDULIN);
	final String termUuid = UUID.randomUUID().toString();
	source.setUuId(termUuid);
	source.setInTranslationAsSource(Boolean.TRUE);
	/*
	 * After rebuild index from V2 backup, for migrated submissions, regular terms
	 * that are in translation as source does not have parentUuId.
	 */
	source.setParentUuId(null);

	termEntry.addTerm(source);

	getUpdater().save(termEntry);

	// I'm trying to simulate complete translation
	source.setInTranslationAsSource(Boolean.FALSE);

	getUpdater().update(termEntry);

	Term updatedSource = getBrowser().findById(termEntryUuid, PROJECT_ID).ggetTermById(termUuid);

	assertFalse(updatedSource.getInTranslationAsSource());
    }
}

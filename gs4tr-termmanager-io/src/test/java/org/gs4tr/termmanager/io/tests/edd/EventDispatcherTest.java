package org.gs4tr.termmanager.io.tests.edd;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.io.edd.api.EventDispatcher;
import org.gs4tr.termmanager.io.edd.event.ProcessDataEvent;
import org.gs4tr.termmanager.io.tests.AbstractIOTest;
import org.gs4tr.termmanager.io.tests.TestHelper;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EventDispatcherTest extends AbstractIOTest {

    @Autowired
    private EventDispatcher _dispatcher;

    /*
     * TERII-5997 History | Source and target attributes are shown in history after
     * deleting only target attribute
     */
    @Test
    public void descriptionsHasSameIdsInDbAndInSolrTest() throws Exception {

	List<TermEntry> entries = new ArrayList<>();

	TermEntry termEntry = TestHelper.createEmptyTermEntry();
	Term term = TestHelper.createTerm("en-US", "englishTerm", "Approved");
	termEntry.addTerm(term);
	entries.add(termEntry);

	getDispatcher().dispatch(new ProcessDataEvent(getRegularCollection(), entries));

	TermEntry solrEntry = getBrowser().findById(termEntry.getUuId(), null);
	String solrTermEntryDescriptionId = solrEntry.getDescriptions().iterator().next().getUuid();
	String solrTermDescriptionId = solrEntry.ggetAllTerms().get(0).getDescriptions().iterator().next().getUuid();

	DbTermEntry dbEntry = getDbTermEntryDAO().findByUuid(termEntry.getUuId(), true);
	String dbTermEntryDescriptionId = dbEntry.getDescriptions().iterator().next().getUuid();
	String dbTermDescriptionId = dbEntry.getTerms().iterator().next().getDescriptions().iterator().next().getUuid();

	assertEquals(solrTermDescriptionId, dbTermDescriptionId);
	assertEquals(solrTermEntryDescriptionId, dbTermEntryDescriptionId);

    }

    @Test
    public void testDispatch() throws Exception {
	LOGGER.info("Running dispatch test...");
	List<TermEntry> entries = new ArrayList<>();
	entries.add(TestHelper.createTermEntry());

	getDispatcher().dispatch(new ProcessDataEvent(getRegularCollection(), entries));
	LOGGER.info("Finished test.");

	List<TermEntry> solrEntries = getBrowser().findAll();
	Assert.assertTrue(CollectionUtils.isNotEmpty(solrEntries));
	assertEquals(1, solrEntries.size());

	List<DbTermEntry> dbEntries = getDbTermEntryDAO().findAll();
	Assert.assertTrue(CollectionUtils.isNotEmpty(dbEntries));
	assertEquals(1, dbEntries.size());
    }

    /*
     * TERII-5634 Unable to complete translation locked project error appears
     */
    @Test
    public void testDispatchFinishTranslationTermWithReviewRequiredNull() throws Exception {
	LOGGER.info("Running dispatch multiple events test...");

	EventDispatcher dispatcher = getDispatcher();

	ProcessDataEvent event1 = new ProcessDataEvent(getSubmissionCollection(),
		Arrays.asList(TestHelper.createSubmissionTermEntry()));

	boolean isReviewRequiredNull = false;

	try {
	    dispatcher.dispatch(event1);
	} catch (Exception e) {
	    isReviewRequiredNull = true;
	}

	Assert.assertFalse(isReviewRequiredNull);

    }

    @Test
    public void testDispatchMultipleEvents() throws Exception {
	LOGGER.info("Running dispatch multiple events test...");

	EventDispatcher dispatcher = getDispatcher();

	ProcessDataEvent event1 = new ProcessDataEvent(getRegularCollection(),
		Arrays.asList(TestHelper.createTermEntry()));
	dispatcher.dispatch(event1);

	ProcessDataEvent event2 = new ProcessDataEvent(getRegularCollection(),
		Arrays.asList(TestHelper.createTermEntry()));
	dispatcher.dispatch(event2);

	LOGGER.info("Finished test.");

	List<TermEntry> solrEntries = getBrowser().findAll();
	Assert.assertTrue(CollectionUtils.isNotEmpty(solrEntries));
	assertEquals(2, solrEntries.size());

	List<DbTermEntry> dbEntries = getDbTermEntryDAO().findAll();
	Assert.assertTrue(CollectionUtils.isNotEmpty(dbEntries));
	assertEquals(2, dbEntries.size());
    }

    private EventDispatcher getDispatcher() {
	return _dispatcher;
    }
}

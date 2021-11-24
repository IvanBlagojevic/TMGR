package org.gs4tr.termmanager.persistence.solr;

import java.util.UUID;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

public class TmgrDeleteTest extends AbstractSolrGlossaryTest {

    // TERII-3661
    @Test
    public void testDelete_case1() throws TmException {

	String status = ItemStatusTypeHolder.PROCESSED.getName();
	String user = "super";

	// source
	Term term1 = createTerm("en", "dog", false, status, user);
	Term term2 = createTerm("en", "puppy", false, status, user);
	Term term3 = createTerm("en", "hot dog", false, status, user);

	// target
	Term term4 = createTerm("sr", "pas", false, status, user);

	String termEntryId = UUID.randomUUID().toString();

	Long projectId = 1L;

	TermEntry termEntry = new TermEntry(projectId, "TES000001", user);
	termEntry.setUuId(termEntryId);
	termEntry.addTerm(term1);
	termEntry.addTerm(term2);
	termEntry.addTerm(term3);
	termEntry.addTerm(term4);

	ITmgrGlossaryUpdater updater = getUpdater();
	updater.update(termEntry);

	ITmgrGlossaryBrowser browser = getBrowser();
	termEntry = browser.findById(termEntryId, projectId);

	Assert.assertNotNull(termEntry);
	Assert.assertEquals(4, termEntry.ggetTerms().size());

	// Delete first source and target
	term1 = termEntry.ggetTermById(term1.getUuId());
	term1.setDisabled(Boolean.TRUE);
	term1.setFirst(false);

	term4 = termEntry.ggetTermById(term4.getUuId());
	term4.setDisabled(Boolean.TRUE);

	// Set second source synonym as first
	term3 = termEntry.ggetTermById(term3.getUuId());
	term3.setFirst(true);

	updater.update(termEntry);

	termEntry = browser.findById(termEntryId, projectId);
	Assert.assertNotNull(termEntry);
	Assert.assertEquals(2, termEntry.ggetTerms().size());

	// Delete source terms
	term2 = termEntry.ggetTermById(term2.getUuId());
	term2.setDisabled(Boolean.TRUE);
	term2.setFirst(true);

	term3 = termEntry.ggetTermById(term3.getUuId());
	term3.setDisabled(Boolean.TRUE);
	term3.setFirst(false);

	updater.update(termEntry);

	termEntry = browser.findById(termEntryId, projectId);
	Assert.assertNotNull(termEntry);
    }
}

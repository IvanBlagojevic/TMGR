package org.gs4tr.termmanager.persistence.solr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.DateFilter;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;

public class TmgrGlossarySearcherDatesTest extends AbstractSolrGlossaryTest {

    private static final Long PROJECT_ID = 1L;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Test
    public void testCreationDateFilter() throws TmException {
	saveNewTermEntry();

	ITmgrGlossarySearcher searcher = getSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(PROJECT_ID);

	filter.setSourceLanguage("en");
	filter.setTargetLanguages(Arrays.asList("sr"));

	Date startDate = new Date(getDateTime("09/01/2015 00:00"));
	Date endDate = new Date(getDateTime("09/01/2015 23:59"));
	DateFilter dateCreatedFilter = new DateFilter(startDate, true, endDate, true);
	filter.setDateCreatedFilter(dateCreatedFilter);

	Page<TermEntry> page = searcher.concordanceSearch(filter);
	Assert.assertNotNull(page);
	Assert.assertEquals(1, page.getTotalResults());
	Assert.assertTrue(CollectionUtils.isNotEmpty(page.getResults()));
    }

    private long getDateTime(String ddMMyyyy) {
	Date date;
	try {
	    date = SDF.parse(ddMMyyyy);
	} catch (ParseException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
	return date.getTime();
    }

    private TermEntry saveNewTermEntry() throws TmException {
	String languageId = "en";
	String name = "Crazy brown fox jumps over the lazy dog or something.";
	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	long termDateTime = getDateTime("26/01/2015 00:00");

	Term term = createTerm(languageId, name, forbidden, status, user);
	term.setDateCreated(termDateTime);

	String name1 = "Luda braun lisica skace preko lenjog kuceta ili tako nesto.";
	String languageId1 = "sr";
	Term term1 = createTerm(languageId1, name1, forbidden, status, user);
	term1.setDateCreated(termDateTime);

	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	long termEntryDateTime = getDateTime("09/01/2015 10:00");

	TermEntry termEntry = new TermEntry(PROJECT_ID, projectShortCode, username);
	termEntry.setDateCreated(termEntryDateTime);

	termEntry.setUuId(TE_ID);
	termEntry.addTerm(term);
	termEntry.addTerm(term1);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);

	return termEntry;
    }
}

package org.gs4tr.termmanager.glossaryV2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.QueryMatchLocation;
import org.gs4tr.tm3.api.glossary.GlossaryConcordanceQuery;
import org.gs4tr.tm3.api.glossary.Term;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsExceptionValue;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperations;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

@TestSuite("glossary")
public class ConcordanceSearchTest extends AbstractV2GlossaryServiceTest {

    private static final String SHORT_CODE = "TES000001";

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("segmentAndConcordanceSearch")
    public void testConcordanceSearch() throws Exception {
	TmgrKey key = createKey();

	ITmgrGlossaryOperations operations = getGlossaryFactory().getOperations(key);

	GlossaryConcordanceQuery query = new GlossaryConcordanceQuery();
	query.setLocation(QueryMatchLocation.SOURCE_AND_TARGET);
	query.setMaxResults(100);
	query.setQuery("text to search");

	List<TermEntry> termEntries = getModelObject("termEntries", List.class);

	Page<TermEntry> page = new Page<TermEntry>(1, 0, 100, termEntries);

	Mockito.when(getTermEntryService().searchTermEntries(Mockito.any())).thenReturn(page);

	Page<Term> result = operations.concordanceSearch(query);

	Assert.assertNotNull(result);
	Assert.assertTrue(CollectionUtils.isNotEmpty(result.getResults()));
	Assert.assertTrue(result.getTotalResults() > 0);
    }

    @Test(expected = OperationsException.class)
    public void testConcordanceSearchEmptyText() throws Exception {
	TmgrKey key = createKey();

	ITmgrGlossaryOperations operations = getGlossaryFactory().getOperations(key);

	GlossaryConcordanceQuery query = new GlossaryConcordanceQuery();
	query.setLocation(QueryMatchLocation.SOURCE);
	query.setMaxResults(100);
	query.setExactMatch(true);
	query.setMatchWholeWords(true);
	query.setQuery("");

	Page<TermEntry> page = new Page<TermEntry>(0, 0, 100, new ArrayList<TermEntry>());

	Mockito.when(getTermEntryService().searchTermEntries(Mockito.any())).thenReturn(page);

	Page<Term> result = operations.concordanceSearch(query);

	Assert.assertNotNull(result);
	Assert.assertTrue(CollectionUtils.isEmpty(result.getResults()));
	Assert.assertTrue(result.getTotalResults() == 0);
    }

    @Test(expected = OperationsException.class)
    public void testConcordanceSearchNullText() throws Exception {
	TmgrKey key = createKey();

	ITmgrGlossaryOperations operations = getGlossaryFactory().getOperations(key);

	GlossaryConcordanceQuery query = new GlossaryConcordanceQuery();
	query.setLocation(QueryMatchLocation.SOURCE);
	query.setMaxResults(100);
	query.setExactMatch(true);
	query.setMatchWholeWords(true);
	query.setQuery(null);

	Page<TermEntry> page = new Page<TermEntry>(0, 0, 100, new ArrayList<TermEntry>());

	Mockito.when(getTermEntryService().searchTermEntries(Mockito.any())).thenReturn(page);

	Page<Term> result = operations.concordanceSearch(query);

	Assert.assertNotNull(result);
	Assert.assertTrue(CollectionUtils.isEmpty(result.getResults()));
	Assert.assertTrue(result.getTotalResults() == 0);
    }

    /*
     * TERII-4162: Web Services - TMGR sends terms of non-existent language to
     * Wordfast.
     */
    @Test
    public void testConcordanceSearchWhenLanguageDoesNotExist() throws Exception {
	TmgrKey key = createKey();

	ITmgrGlossaryOperations glossaryOperations = getGlossaryFactory().getOperations(key);
	Assert.assertNotNull(glossaryOperations);

	// Admin removes a language (de-DE) from a TMGR project
	removeLanguageId(Locale.GERMANY.getCode(), 1L);

	// Try to add the glossary with same key(same sessionKey, source and
	// (non-existing) target)
	try {
	    getGlossaryFactory().getOperations(key);
	    Assert.fail("OperationsException not thrown. Test should not reach here.");
	} catch (OperationsException oe) {
	    /*
	     * Expected from ticket description: A clear error message that the language
	     * does not exist must be given and the the terms of the removed language should
	     * not be sent to Wordfast.
	     */
	    Assert.assertEquals(getExpectedErrorMessage(key), oe.getMessage());
	    Assert.assertEquals(OperationsExceptionValue.AUTHORIZATION, oe.getValue());
	}
    }

    private String getExpectedErrorMessage(final TmgrKey key) {
	String target = key.getTarget();
	TmProject project = getProjectService().findProjectByShortCode(SHORT_CODE);
	return String.format(Messages.getString("BaseOperationsFactory.5"), target, project.getProjectInfo().getName());
    }

    private void removeLanguageId(final String languageId, final Long projectId) {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	Set<String> userLanguageIds = userProfile.getProjectUserLanguages().get(projectId);
	userLanguageIds.remove(languageId);
    }
}

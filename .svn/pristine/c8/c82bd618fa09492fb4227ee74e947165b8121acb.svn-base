package org.gs4tr.termmanager.service.project.terminology.counts;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.Matchers.any;

import org.gs4tr.termmanager.model.ProjectTerminologyCounts;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("projectTerminologyCounts")
public class ProjectTerminologyCountsProviderTest extends AbstractSpringProjectTerminologyCountsProviderTest {

    @Rule
    public final ExpectedException TROWN = ExpectedException.none();

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private ProjectTerminologyCountsProvider _terminologyCountsProvider;

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("projectTerminologyCounts")
    public void getProjectTerminologyCountsTest() {
	List<String> languages = getModelObject("languages", List.class);

	FacetTermCounts termCounts = getModelObject("termCounts", FacetTermCounts.class);

	when(getTermEntryService().getNumberOfTermEntries(any(TmgrSearchFilter.class)))
		.thenReturn(Collections.singletonMap(1L, 50L));
	when(getTermEntryService().searchFacetTermCounts(any(TmgrSearchFilter.class))).thenReturn(termCounts);

	ProjectTerminologyCounts result = getTerminologyCountsProvider().getProjectTerminologyCounts(1L, languages);

	verify(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));
	verify(getTermEntryService()).searchFacetTermCounts(any(TmgrSearchFilter.class));

	assertEquals(50l, result.getNumberOfTermEntries());

	Map<String, Long> numberOfTermsByLanguage = result.getNumberOfTermsByLanguage();

	assertTrue(numberOfTermsByLanguage.values().stream().allMatch(Predicate.isEqual(50l)));
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("emptyGlossary")
    public void getProjectTerminologyCountsWhenGlossaryEmptyTest() {
	List<String> languages = getModelObject("languages", List.class);

	FacetTermCounts termCounts = getModelObject("termCounts", FacetTermCounts.class);

	when(getTermEntryService().getNumberOfTermEntries(any(TmgrSearchFilter.class)))
		.thenReturn(Collections.singletonMap(1L, 0L));
	when(getTermEntryService().searchFacetTermCounts(any(TmgrSearchFilter.class))).thenReturn(termCounts);

	ProjectTerminologyCounts result = getTerminologyCountsProvider().getProjectTerminologyCounts(1L, languages);

	verify(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));
	verify(getTermEntryService()).searchFacetTermCounts(any(TmgrSearchFilter.class));

	assertEquals(0, result.getNumberOfTermEntries());

	assertEquals(languages, result.getLanguages());
    }

    @Before
    public void resetTermEntryService() {
	reset(getTermEntryService());
    }

    @Test
    public void whenLanguagesEmptyThrowIllegalArgumentExceptionTest() {
	TROWN.expect(IllegalArgumentException.class);
	TROWN.expectMessage("Parameter languages cannot be empty.");

	getTerminologyCountsProvider().getProjectTerminologyCounts(1L, null);
    }

    @Test
    public void whenProjectIdNullThrowIllegalArgumentExceptionTest() {
	TROWN.expect(IllegalArgumentException.class);
	TROWN.expectMessage("projectId must not be null");

	getTerminologyCountsProvider().getProjectTerminologyCounts(null, Collections.emptyList());
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private ProjectTerminologyCountsProvider getTerminologyCountsProvider() {
	return _terminologyCountsProvider;
    }
}

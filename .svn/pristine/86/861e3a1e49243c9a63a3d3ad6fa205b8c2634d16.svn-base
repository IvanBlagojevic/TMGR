package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.dao.StatisticsDAO;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.StatisticsService;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import jetbrains.exodus.entitystore.EntityId;

@TestSuite("service")
public class TermServiceTest extends AbstractServiceTest {

    private static final String BASE_TYPE = "ATTRIBUTE";

    private static final Long PROJECT_ID = 1L;

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private StatisticsDAO _statisticsDAO;

    @Autowired
    private StatisticsService _statisticsService;

    private TmProject _tmProject;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("term")
    public void approveTermsTest_01() throws TmException {
	TermEntry termEntry1 = getModelObject("termEntry1", TermEntry.class);
	termEntry1.setProjectId(PROJECT_ID);

	List<String> termIds = getModelObject("termIds", List.class);

	when(getGlossaryBrowser().findByTermIds(termIds, PROJECT_ID)).thenReturn(Arrays.asList(termEntry1));

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermService().approveTerms(termIds, PROJECT_ID);

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	TransactionalUnit transactionalUnit = argument.getValue();
	Set<StatisticsInfo> statisticsInfos = transactionalUnit.getStatisticsInfo();

	Assert.assertEquals(2, statisticsInfos.size());

	Iterator<StatisticsInfo> stIterator = statisticsInfos.iterator();

	StatisticsInfo statisticsInfo = stIterator.next();

	Assert.assertEquals(1, statisticsInfo.getApprovedCount().intValue());
	Assert.assertEquals("en-US", statisticsInfo.getLanguageId());

	statisticsInfo = stIterator.next();

	Assert.assertEquals(1, statisticsInfo.getApprovedCount().intValue());
	Assert.assertEquals("de-DE", statisticsInfo.getLanguageId());

	ProjectDetailInfo projectDetailInfo = transactionalUnit.getProjectDetailInfo();

	projectDetailInfo.getUpdatedLanguages()
		.forEach(l -> Assert.assertEquals(1, projectDetailInfo.getLanguageApprovedTermCount().get(l).get()));

	List<TermEntry> termEntiesAfter = argument.getValue().getTermEntries();

	boolean flag = false;
	for (TermEntry termEntry : termEntiesAfter) {
	    Assert.assertEquals(Action.EDITED, termEntry.getAction());
	    for (Term term : termEntry.ggetTerms()) {
		if (termIds.contains(term.getUuId())) {
		    Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), term.getStatus());
		    Assert.assertEquals(Boolean.FALSE, term.isForbidden());
		    flag = true;
		}
	    }
	}

	Assert.assertTrue(flag);
    }

    /*
     * TERII-3536: Wrong modification user if actions are performed on same term but
     * in different windows with different users.. In this test case i want to
     * reproduce bug for approve term action.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("term")
    public void approveTermsTest_02() throws TmException {
	TermEntry termEntry = getModelObject("termEntry_02", TermEntry.class);
	termEntry.setProjectId(PROJECT_ID);

	List<String> termIds = getModelObject("termIds_02", List.class);

	when(getGlossaryBrowser().findByTermIds(anyListOf(String.class), any(Long.class)))
		.thenReturn(Arrays.asList(termEntry));

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermService().approveTerms(termIds, PROJECT_ID);

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);

	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	List<TermEntry> termEntries = argument.getValue().getTermEntries();

	ItemStatusType processed = ItemStatusTypeHolder.PROCESSED;

	// Validate that dateModified, term status and userModified are changed.
	for (TermEntry tempEntry : termEntries) {
	    Assert.assertEquals(Action.EDITED, termEntry.getAction());
	    List<Term> terms = tempEntry.ggetTerms();
	    for (Term term : terms) {
		if (termIds.contains(term.getUuId())) {
		    // New status for this term should be "PROCESSED".
		    assertEquals(processed.getName(), term.getStatus());
		    assertNotNull(term.getDateModified());
		    // New userModified for this term should be "marko".
		    assertEquals("marko", term.getUserModified());
		}
	    }
	}
    }

    @Test
    @TestCase("term")
    public void approveTermsWhenTermIdsIsEmptyList() throws TmException {
	@SuppressWarnings("unchecked")
	List<String> termIds = getModelObject("emptyTermIds", List.class);

	getTermService().approveTerms(termIds, 1L);

	Mockito.verifyZeroInteractions(getGlossaryBrowser());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("term")
    public void changeForbiddStatusTest_01() throws TmException {
	TermEntry termEntry1 = getModelObject("termEntry1", TermEntry.class);
	termEntry1.setProjectId(PROJECT_ID);
	List<String> termIds = getModelObject("termIds", List.class);

	when(getGlossaryBrowser().findByTermIds(termIds, PROJECT_ID)).thenReturn(Arrays.asList(termEntry1));

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermService().changeForbiddStatus(termIds, 1L);

	verify(getProjectDAO(), times(1)).load(1L);

	@SuppressWarnings("rawtypes")
	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	ProjectDetailInfo projectDetailInfo = argument.getValue().getProjectDetailInfo();

	projectDetailInfo.getUpdatedLanguages()
		.forEach(l -> Assert.assertEquals(1, projectDetailInfo.getLanguageForbiddenTermCount().get(l).get()));

	List<TermEntry> termEntiesAfter = argument.getValue().getTermEntries();

	boolean flag1 = false;
	for (TermEntry termEntry : termEntiesAfter) {
	    Assert.assertEquals(Action.EDITED, termEntry.getAction());
	    for (Term term : termEntry.ggetTerms()) {
		Assert.assertEquals(ItemStatusTypeHolder.BLACKLISTED.getName(), term.getStatus());
		Assert.assertEquals(Boolean.TRUE, term.isForbidden());
		flag1 = true;
	    }
	}

	Assert.assertTrue(flag1);
    }

    @Test
    @TestCase("term")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void deleteTermDescriptionsByTypeTest() throws TmException {
	List<String> types = getModelObject("type", List.class);
	List<String> languageIds = getModelObject("languageIds", List.class);

	List<TermEntry> termEntries = getModelObject("termEntries", List.class);
	termEntries.forEach(t -> t.setProjectId(PROJECT_ID));

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	ArgumentCaptor<TmgrSearchFilter> filterCaptor = ArgumentCaptor.forClass(TmgrSearchFilter.class);
	when(getGlossaryBrowser().browse(any(TmgrSearchFilter.class))).thenReturn(termEntries);

	getTermService().deleteTermDescriptionsByType(types, 1L, BASE_TYPE, languageIds);

	verify(getGlossaryBrowser(), new Times(1)).browse(filterCaptor.capture());

	Assert.assertTrue(filterCaptor.getValue().getProjectIds().size() == 1);
	Assert.assertTrue(filterCaptor.getValue().getProjectIds().contains(1L));
	Assert.assertTrue(filterCaptor.getValue().getSourceLanguage().equals(Locale.US.getCode()));

	ArgumentCaptor<TransactionalUnit> termEntriesCaptor = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		termEntriesCaptor.capture());

	List<TermEntry> termEntriesCaptured = termEntriesCaptor.getValue().getTermEntries();

	assertTrue(termEntriesCaptured.stream().map(TermEntry::ggetTerms).flatMap(List::stream)
		.map(Term::getDescriptions).flatMap(Set::stream).filter(Objects::nonNull).map(Description::getBaseType)
		.allMatch(Predicate.isEqual(Description.NOTE)));

	ArgumentCaptor<Long> projectIdCaptor = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<Date> dbDateModifiedCaptor = ArgumentCaptor.forClass(Date.class);
	ArgumentCaptor<Set> languagesCaptor = ArgumentCaptor.forClass(Set.class);

	verify(getProjectDetailDAO(), times(1)).updateProjectAndLanguagesDateModifiedByProjectId(
		projectIdCaptor.capture(), languagesCaptor.capture(), dbDateModifiedCaptor.capture());

	assertEquals(PROJECT_ID, projectIdCaptor.getValue());
	assertEquals(Sets.newHashSet(Locale.US.getCode()), languagesCaptor.getValue());
	/*
	 * Both term entries will be updated, therefore get dateModified from the first
	 * one
	 */
	assertEquals(termEntriesCaptured.get(0).getDateModified().longValue(),
		dbDateModifiedCaptor.getValue().getTime());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("term")
    public void demoteTermTest_01() throws TmException {
	TermEntry termEntry1 = getModelObject("termEntry1", TermEntry.class);
	termEntry1.setProjectId(PROJECT_ID);
	List<String> termIds = getModelObject("termIds", List.class);

	when(getGlossaryBrowser().findByTermIds(termIds, PROJECT_ID)).thenReturn(Arrays.asList(termEntry1));

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermService().demoteTerms(termIds, 1L);

	@SuppressWarnings("rawtypes")
	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	List<TermEntry> termEntiesAfter = argument.getValue().getTermEntries();

	boolean flag = false;
	for (TermEntry termEntry : termEntiesAfter) {
	    for (Term term : termEntry.ggetTerms()) {
		if (termIds.contains(term.getUuId())) {
		    Assert.assertEquals(Action.EDITED, termEntry.getAction());
		    Assert.assertEquals(ItemStatusTypeHolder.WAITING.getName(), term.getStatus());
		    Assert.assertEquals(Boolean.FALSE, term.isForbidden());
		    flag = true;
		}
	    }
	    Assert.assertEquals(2, termEntry.ggetTerms().size());
	}

	Assert.assertTrue(flag);
    }

    /*
     * TERII-3536: Wrong modification user if actions are performed on same term but
     * in different windows with different users.. In this test case i want to
     * reproduce bug for demote term action.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("term")
    public void demoteTermTest_02() throws TmException {
	TermEntry termEntry = getModelObject("termEntry_01", TermEntry.class);
	termEntry.setProjectId(PROJECT_ID);

	List<String> termIds = getModelObject("termIds_01", List.class);

	when(getGlossaryBrowser().findByTermIds(anyListOf(String.class), any(Long.class)))
		.thenReturn(Arrays.asList(termEntry));

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermService().demoteTerms(termIds, PROJECT_ID);

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);

	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	List<TermEntry> termEntries = argument.getValue().getTermEntries();

	ItemStatusType waiting = ItemStatusTypeHolder.WAITING;

	// Validate that dateModified, term status and userModified are changed.
	for (TermEntry tempEntry : termEntries) {
	    Assert.assertEquals(Action.EDITED, termEntry.getAction());
	    List<Term> terms = tempEntry.ggetTerms();
	    for (Term term : terms) {
		if (termIds.contains(term.getUuId())) {
		    assertEquals(waiting.getName(), term.getStatus());
		    assertNotNull(term.getDateModified());
		    // New userModified for this term should be "marko".
		    assertEquals("marko", term.getUserModified());
		}
	    }
	}
    }

    @Test
    @TestCase("term")
    public void findTermByIdEWhenTermIdIsEmptyTest() {
	Term result = getTermService().findTermById("", 1L);

	Assert.assertEquals(null, result);
    }

    @Test
    @TestCase("term")
    public void findTermByIdTest() throws TmException {
	Term term = getModelObject("term1", Term.class);

	when(getGlossaryBrowser().findTermById(Mockito.anyString(), Mockito.anyLong())).thenReturn(term);

	Term result = getTermService().findTermById("474e93ae-7264-4088-9d54-term00000001", term.getProjectId());

	verify(getGlossaryBrowser(), new Times(1)).findTermById(Mockito.anyString(), Mockito.anyLong());

	Assert.assertNotNull(result);
	Assert.assertEquals(term, result);
    }

    @Test(expected = RuntimeException.class)
    @TestCase("term")
    public void findTermByIdWhengetBrowserThrowTmExceptionTest() throws TmException {
	when(getGlossaryBrowser().findTermById(Mockito.anyString(), Mockito.anyLong()))
		.thenThrow(new TmException("getITmgrGlossaryBrowser fail"));

	getTermService().findTermById("474e93ae-7264-4088-9d54-term00000001", 1L);
    }

    @Test
    @TestCase("term")
    @SuppressWarnings("unchecked")
    public void findTermsByIdsTest() throws TmException {
	List<String> termIds = getModelObject("termIds", List.class);
	List<Term> terms = getModelObject("terms", List.class);

	Long projectId = terms.get(0).getProjectId();

	when(getGlossaryBrowser().findTermsByIds(termIds, Arrays.asList(projectId))).thenReturn(terms);

	List<Term> result = getTermService().findTermsByIds(termIds, Arrays.asList(projectId));

	verify(getGlossaryBrowser(), new Times(1)).findTermsByIds(termIds, Arrays.asList(projectId));

	Assert.assertNotNull(result);
	Assert.assertEquals(terms, result);
	Assert.assertTrue(result.size() == 2);
    }

    @Test
    @TestCase("term")
    @SuppressWarnings("unchecked")
    public void findTermsByIdsWhenTermIdIsEmptyTest() {
	List<String> termIds = getModelObject("emptyTermIds", List.class);

	List<Term> result = getTermService().findTermsByIds(termIds, Arrays.asList(1L));

	Assert.assertEquals(null, result);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = RuntimeException.class)
    @TestCase("term")
    public void findTermsByIdsWhengetBrowserThrowTmExceptionTest() throws TmException {
	List<String> termIds = getModelObject("termIds", List.class);

	when(getGlossaryBrowser().findTermsByIds(Mockito.anyList(), Mockito.anyList()))
		.thenThrow(new TmException("getITmgrGlossaryBrowser fail"));

	getTermService().findTermsByIds(termIds, Arrays.asList(1L));
    }

    @Test
    @TestCase("term")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getAllTermsForMergeTest() throws TmException {
	Set<Term> termSetOne = getModelObject("termSetOne", Set.class);
	TermEntry termEntryOne = getModelObject("termEntryOne", TermEntry.class);
	Map<String, Set<Term>> languageTermsOne = new HashMap<String, Set<Term>>();
	languageTermsOne.put("en-US", termSetOne);
	termEntryOne.setLanguageTerms(languageTermsOne);

	Set<Term> termSetTwo = getModelObject("termSetTwo", Set.class);
	TermEntry termEntryTwo = getModelObject("termEntryTwo", TermEntry.class);
	Map<String, Set<Term>> languageTermsTwo = new HashMap<String, Set<Term>>();
	languageTermsTwo.put("en-US", termSetTwo);
	termEntryTwo.setLanguageTerms(languageTermsTwo);

	List<TermEntry> termEntries = new ArrayList<TermEntry>();
	termEntries.add(termEntryOne);
	termEntries.add(termEntryTwo);

	Page page = new Page<TermEntry>(0, 0, 0, termEntries);

	List<String> termNames = new ArrayList<String>();
	termNames.add("dog");
	termNames.add("cat");
	termNames.add("LION");

	when(getGlossarySearcher().concordanceSearch(any(TmgrSearchFilter.class))).thenReturn(page);

	List<Term> result = getTermService().getAllTermsForMerge(1L, "en-US", termNames);

	verify(getGlossarySearcher(), new Times(1)).concordanceSearch(any(TmgrSearchFilter.class));

	Assert.assertTrue(3 == result.size());
	Assert.assertTrue(result.get(0).getName().equals("dog"));
	Assert.assertTrue(result.get(2).getName().equals("lion"));
	Assert.assertTrue(result.get(0).getStatus().equals("PROCESSED"));
    }

    @Test
    @TestCase("term")
    public void getAllTermsInTermEntryTest() throws TmException {
	String termEntryId = getModelObject("termEntryId_01", String.class);
	TermEntry termEntry = getModelObject("termEntry1", TermEntry.class);

	Long projectId = termEntry.getProjectId();

	when(getGlossaryBrowser().findById(termEntryId, projectId)).thenReturn(termEntry);

	List<Term> result = getTermService().getAllTermsInTermEntry(termEntryId, projectId);

	verify(getGlossaryBrowser(), new Times(1)).findById(termEntryId, projectId);

	Assert.assertNotNull(result);
	Assert.assertTrue(result.size() == 2);
	Assert.assertEquals("en-US", result.get(1).getLanguageId());
	Assert.assertTrue("474e93ae-7264-4088-9d54-termentry001" == result.get(0).getTermEntryId());
    }

    @Test
    @TestCase("term")
    public void getAllTermsInTermEntryWhenTermEntryIsNull() throws TmException {
	String termEntryId = getModelObject("termEntryId_01", String.class);

	Long projectId = 1L;

	when(getGlossaryBrowser().findById(termEntryId, projectId)).thenReturn(null);

	List<Term> result = getTermService().getAllTermsInTermEntry(termEntryId, projectId);

	Assert.assertNotNull(result);
	Assert.assertTrue(result.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    @TestCase("term")
    public void getAllTermsInTermEntryWhengetBrowserFailTest() throws TmException {
	String termEntryId = getModelObject("termEntryId_01", String.class);

	TermEntry termEntry = getModelObject("termEntry1", TermEntry.class);

	Long projectId = termEntry.getProjectId();

	when(getGlossaryBrowser().findById(termEntryId, any(Long.class)))
		.thenThrow(new TmException("getITmgrGlossaryBrowser fail"));

	getTermService().getAllTermsInTermEntry(termEntryId, projectId);

	verify(getGlossaryBrowser(), new Times(1)).findById(termEntryId, projectId);
    }

    public StatisticsDAO getStatisticsDAO() {
	return _statisticsDAO;
    }

    public StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    @Test
    @TestCase("term")
    @SuppressWarnings("unchecked")
    public void getTermsByTermEntryIdsTest() throws TmException {
	List<TermEntry> termEntryList = getModelObject("termEntryList", List.class);

	Long projectId = 1L;

	List<String> ids = termEntryList.stream().map(t -> t.getUuId()).collect(Collectors.toList());

	when(getGlossaryBrowser().findByIds(ids, Arrays.asList(projectId))).thenReturn(termEntryList);

	List<Term> result = getTermService().getTermsByTermEntryIds(ids, Arrays.asList(projectId));

	Assert.assertNotNull(result);
	Assert.assertEquals(2, result.size());
	Assert.assertEquals(result.get(1).getUuId(), "474e93ae-7264-4088-9d54-term00000001");
    }

    @Test(expected = RuntimeException.class)
    @TestCase("term")
    @SuppressWarnings("unchecked")
    public void getTermsByTermEntryIdsWhengetBrowserFailTest() throws TmException {
	Long projectId = 1L;

	when(getGlossaryBrowser().findByIds(Mockito.anyCollection(), Arrays.asList(projectId)))
		.thenThrow(new TmException("getITmgrGlossaryBrowser fail"));

	getTermService().getTermsByTermEntryIds(Mockito.anyCollection(), Arrays.asList(projectId));
    }

    /* Test case: Rename type: "custom" to "Part of speech" */
    @Test
    @TestCase("term")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void renameTermDescriptionsTest() throws TmException {
	List<String> languageIds = getModelObject("languageIds", List.class);

	List<Attribute> attributes = getModelObject("attributes", List.class);

	List<TermEntry> termEntries = getModelObject("termEntries", List.class);
	termEntries.forEach(t -> t.setProjectId(PROJECT_ID));

	when(getGlossaryBrowser().browse(any(TmgrSearchFilter.class))).thenReturn(termEntries);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermService().renameTermDescriptions(1L, "NOTE", attributes, languageIds);

	verify(getGlossaryBrowser(), new Times(1)).browse(any(TmgrSearchFilter.class));

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);

	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	List<TermEntry> termEntriesAfter = argument.getValue().getTermEntries();

	MutableInt renamedNoteCount = new MutableInt(0);
	MutableInt editedTermEntryCount = new MutableInt(0);

	final String newType = "Part of speech"; //$NON-NLS-1$

	Iterator<TermEntry> it = termEntriesAfter.iterator();
	while (it.hasNext()) {
	    TermEntry termEntry = it.next();
	    if (Action.EDITED == termEntry.getAction()) {
		editedTermEntryCount.increment();
	    }
	    List<Term> langTerms = termEntry.ggetTerms();
	    for (Term term : langTerms) {
		countRenamedValues(renamedNoteCount, newType, term);
	    }
	}

	assertEquals(2, renamedNoteCount.intValue());
	assertEquals(1, editedTermEntryCount.intValue());
    }

    @Before
    public void resetSomeMocks() {
	reset(getStatisticsService());
    }

    @Before
    public void setUp() throws Exception {
	// Reset mock
	reset(getProjectDAO());
	reset(getGlossaryUpdater());
	reset(getGlossaryBrowser());

	reset(getStatisticsDAO());

	_tmProject = getModelObject("tmProject", TmProject.class);
	_tmProject.getProjectDetail().setProject(_tmProject);

	Set<ProjectUserDetail> set = new HashSet<ProjectUserDetail>();
	set.add(new ProjectUserDetail((TmUserProfile) UserProfileContext.getCurrentUserProfile(),
		_tmProject.getProjectDetail()));
	_tmProject.getProjectDetail().setUserDetails(set);

	when(getProjectDAO().load(any(Long.class))).thenReturn(_tmProject);
	when(getProjectDAO().findById(any(Long.class))).thenReturn(_tmProject);

	ProjectDetail projectDetail = getTmProject().getProjectDetail();
	projectDetail.setApprovedTermCount(0L);
	projectDetail.setForbiddenTermCount(0L);

	when(getQuery().uniqueResult()).thenReturn(projectDetail);
    }

    private void countRenamedValues(MutableInt renamedNoteCount, final String newType, Term term) {
	Set<Description> descriptions = term.getDescriptions();
	for (Description description : descriptions) {
	    if (isNoteWithNewType(newType, description)) {
		renamedNoteCount.increment();
	    }
	}
    }

    private ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    private TmProject getTmProject() {
	return _tmProject;
    }

    private boolean isNoteWithNewType(final String newType, Description description) {
	return description.getBaseType().equals(Description.NOTE) && description.getType().equals(newType);
    }

}

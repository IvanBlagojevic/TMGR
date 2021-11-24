package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.types.BaseItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.StatisticsService;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;

import jetbrains.exodus.entitystore.EntityId;

@TestSuite("service")
public class SubmissionTermServiceTest extends AbstractServiceTest {

    private static final String EN_US = "en-US";

    private static final String SUBMISSION_TERM_ENTRY_ID_01 = "474e93ae-7264-4088-9d54-termentryS01";

    private static final String SUBMISSION_TERM_ID_01 = "474e93ae-7264-4088-9d54-sub-term0001";

    private static final String SUBMISSION_TERM_ID_02 = "474e93ae-7264-4088-9d54-sub-term0002";

    private static final String TERM_ENTRY_ID_01 = "474e93ae-7264-4088-9d54-termentry001";

    private static final String TERM_ID_01 = "474e93ae-7264-4088-9d54-term00000001";

    private static final String TERM_ID_02 = "474e93ae-7264-4088-9d54-term00000002";

    @Autowired
    private StatisticsService _statisticsService;

    private Submission _submission;

    @Autowired
    private SubmissionTermService _submissionTermService;

    private TmProject _tmProject;

    @Test
    @TestCase("submissionTerm")
    public void addNewDescriptionTest_01() throws TmException {
	TermEntry termEntry = getModelObject("submissionTermEntry1", TermEntry.class);
	termEntry.setProjectId(1L);
	Term subTerm = getModelObject("submissionTerm3", Term.class);

	String termId = subTerm.getUuId();
	when(getGlossaryBrowser().findByTermId(termId, termEntry.getProjectId())).thenReturn(termEntry);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getSubmissionTermService().addNewDescription("description", "description text", Description.NOTE, termId,
		termEntry.getProjectId());

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);

	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());
	verify(getGlossaryBrowser(), times(1)).findByTermId(anyString(), anyLong());

	TermEntry after = argument.getValue().getTermEntries().get(0);
	Term termAfter = after.ggetTermById(termId);

	Set<Description> descriptions = termAfter.getDescriptions();

	assertNotNull(descriptions);
	assertEquals(1, descriptions.size());

	Description description = descriptions.iterator().next();

	assertEquals("description text", description.getTempValue());
    }

    /*
     * This test is modified after source code refactoring TODO: Check this logic
     */
    @Test
    @TestCase("submissionTerm")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void approveSubmissionTermTest_01() throws TmException {
	Long projectId = 1L;
	TermEntry termEntry = getModelObject("termEntry1", TermEntry.class);
	termEntry.setProjectId(projectId);

	TermEntry submissionTermEntry = getModelObject("submissionTermEntry1", TermEntry.class);
	Term submissionTerm1 = getModelObject("submissionTerm2", Term.class);

	String subTermId = submissionTerm1.getUuId();

	when(getSubmissionDAO().load(1L)).thenReturn(_submission);
	when(getGlossaryBrowser().findByTermIds(Arrays.asList(subTermId), projectId))
		.thenReturn(Arrays.asList(submissionTermEntry));
	when(getGlossaryBrowser().findByTermIds(Arrays.asList(submissionTerm1.getParentUuId()), projectId))
		.thenReturn(Arrays.asList(termEntry));

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	List<String> approvedTerms = getSubmissionTermService().approveSubmissionTerms(Arrays.asList(subTermId),
		projectId);

	verify(getSubmissionDAO(), times(1)).load(1L);
	verify(getGlossaryBrowser(), times(2)).findByTermIds(anyCollection(), anyLong());

	assertNotNull(approvedTerms);

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), new Times(2)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	List<TransactionalUnit> units = argument.getAllValues();

	boolean subFlag = false;

	for (TransactionalUnit unit : units) {

	    List<TermEntry> entries = unit.getTermEntries();

	    for (TermEntry afterTermEntry : entries) {
		for (Term afterTerm : afterTermEntry.ggetTerms()) {
		    if (afterTerm.getUuId().equals(subTermId)) {
			assertEquals(BaseItemStatusTypeHolder.PROCESSED.getName(), afterTerm.getStatus());
			subFlag = true;
		    }
		}
	    }
	}

	assertTrue(subFlag);
    }

    /*
     * This test should "cover" changes in source code after approve term process
     * optimization
     */
    @Test
    @TestCase("submissionTerm")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void approveSubmissionTermsTest() throws TmException {
	TermEntry termEntry = getModelObject("regularTermEntry", TermEntry.class);
	termEntry.setProjectId(1L);
	TermEntry submissionEntry = getModelObject("submissionTermEntry", TermEntry.class);
	submissionEntry.setProjectId(1L);
	List<String> termIdsFromUI = getModelObject("submissionTargetTermIds", List.class);

	when(getSubmissionDAO().load(1L)).thenReturn(_submission);
	when(getGlossaryBrowser().findByTermIds(anyCollection(), anyLong())).thenReturn(Arrays.asList(submissionEntry),
		Arrays.asList(termEntry));

	ProjectDetail projectDetail = Mockito.mock(ProjectDetail.class);
	when(getQuery().uniqueResult()).thenReturn(_submission).thenReturn(projectDetail);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	List<String> approvedTerms = getSubmissionTermService().approveSubmissionTerms(termIdsFromUI, 1L);

	assertEquals(Action.APPROVED_TRANSLATIONS, termEntry.getAction());

	assertNotNull(approvedTerms);
	assertTrue(CollectionUtils.isNotEmpty(approvedTerms));

	verify(getSubmissionDAO(), times(1)).load(1L);
	verify(getGlossaryBrowser(), times(2)).findByTermIds(anyCollection(), anyLong());

	ArgumentCaptor<TransactionalUnit> updateArguments = ArgumentCaptor.forClass(TransactionalUnit.class);

	verify(getTransactionLogHandler(), new Times(2)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		updateArguments.capture());

	List<TermEntry> entries = updateArguments.getValue().getTermEntries();

	for (TermEntry tEntry : entries) {
	    if (tEntry.getUuId().equals(termEntry.getUuId())) {
		verifyRegularTerms(tEntry);
	    } else if (tEntry.getUuId().equals(submissionEntry.getUuId())) {
		verifySubmissionTerms(tEntry);
	    }
	}

	Set<StatisticsInfo> statisticInfos = updateArguments.getValue().getStatisticsInfo();

	assertEquals(1, statisticInfos.size());

	StatisticsInfo statisticsInfo = statisticInfos.iterator().next();

	assertEquals(1, statisticsInfo.getApprovedCount().longValue());
	assertEquals(0, statisticsInfo.getPendingApprovalCount().longValue());
	assertEquals(0, statisticsInfo.getOnHoldCount().longValue());
	assertEquals(0, statisticsInfo.getBlackListedCount().longValue());
	assertEquals(0, statisticsInfo.getAddedApprovedCount().longValue());
	assertEquals(0, statisticsInfo.getAddedPendingApprovalCount().longValue());
	assertEquals(0, statisticsInfo.getAddedOnHoldCount().longValue());
	assertEquals(0, statisticsInfo.getAddedBlacklistedCount().longValue());
	assertEquals(0, statisticsInfo.getDeletedCount().longValue());
	assertEquals(0, statisticsInfo.getUpdatedCount().longValue());

    }

    @Test
    @TestCase("submissionTerm")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void cancelTermTranslationTest_01() throws TmException {
	Long projectId = 1L;

	List<String> submissionTermIds = getModelObject("submissionTermIds", List.class);
	Set<String> regularTermEntryIds = getModelObject("regularTermEntryIds", Set.class);

	List<TermEntry> regularTermEntries = getModelObject("regularTermEntries", List.class);
	regularTermEntries.forEach(e -> e.setProjectId(projectId));
	List<TermEntry> subTermEntries = getModelObject("subTermEntries", List.class);
	subTermEntries.forEach(e -> e.setProjectId(projectId));

	ProjectDetail projectDetail = Mockito.mock(ProjectDetail.class);
	when(getQuery().uniqueResult()).thenReturn(_submission).thenReturn(projectDetail);

	when(getSubmissionDAO().load(1L)).thenReturn(_submission);
	when(getGlossaryBrowser().findByTermIds(submissionTermIds, projectId)).thenReturn(subTermEntries);
	when(getGlossaryBrowser().findByIds(regularTermEntryIds, Arrays.asList(projectId)))
		.thenReturn(regularTermEntries);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	Set<String> result = getSubmissionTermService().cancelTermTranslation(1L, submissionTermIds);

	for (TermEntry termEntry : regularTermEntries) {
	    assertEquals(Action.CANCELED_TRANSLATIONS, termEntry.getAction());
	}

	String regularTermEntryId = result.iterator().next();

	assertEquals(1, result.size());

	TermEntry submissionTermEntry = subTermEntries.get(0);
	assertEquals(regularTermEntryId, submissionTermEntry.getParentUuId());

	verify(getSubmissionDAO(), times(1)).load(eq(1L));
	verify(getGlossaryBrowser(), times(2)).findByTermIds(eq(submissionTermIds), anyLong());
	verify(getGlossaryBrowser(), times(1)).findByIds(eq(regularTermEntryIds), any(List.class));

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), new Times(2)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	List<TransactionalUnit> units = argument.getAllValues();

	boolean regularFlag = false;
	boolean submissionFlag = false;

	for (TransactionalUnit unit : units) {
	    List<TermEntry> entries = unit.getTermEntries();
	    assertTrue(CollectionUtils.isNotEmpty(entries));

	    for (TermEntry afterTermEntry : entries) {
		assertNotNull(afterTermEntry);
		if (afterTermEntry.getUuId().equals(TERM_ENTRY_ID_01)) {
		    Term sourceTerm = afterTermEntry.ggetTermById(TERM_ID_01);
		    String sourceName = sourceTerm.getName();
		    String sourceStatus = sourceTerm.getStatus();
		    String sourceStatusOld = sourceTerm.getStatusOld();

		    assertEquals(sourceStatus, sourceStatusOld);
		    assertEquals(sourceStatus, ItemStatusTypeHolder.PROCESSED.getName());
		    assertEquals("voicemail (voicemail message)", sourceName);
		    assertFalse(sourceTerm.getInTranslationAsSource());

		    Term targetTerm = afterTermEntry.ggetTermById(TERM_ID_02);
		    String targetName = targetTerm.getName();
		    String targetStatus = targetTerm.getStatus();
		    String targetStatusOld = targetTerm.getStatusOld();

		    assertNotNull(targetTerm.getDateModified());

		    assertEquals(targetStatus, targetStatusOld);
		    assertEquals("message vocal", targetName);
		    assertEquals(targetStatus, ItemStatusTypeHolder.PROCESSED.getName());

		    regularFlag = true;
		}
		if (afterTermEntry.getUuId().equals(SUBMISSION_TERM_ENTRY_ID_01)) {
		    Term submissionSourceTerm = afterTermEntry.ggetTermById(SUBMISSION_TERM_ID_01);

		    String sourceStatus = submissionSourceTerm.getStatus();
		    String sourceStatusOld = submissionSourceTerm.getStatusOld();

		    assertTrue(submissionSourceTerm.getCommited());
		    assertFalse(submissionSourceTerm.getInTranslationAsSource());

		    assertEquals(sourceStatus, sourceStatusOld);

		    Term submissionTargetTerm = afterTermEntry.ggetTermById(SUBMISSION_TERM_ID_02);
		    String targetStatus = submissionTargetTerm.getStatus();
		    String targetStatusOld = submissionTargetTerm.getStatusOld();

		    assertNotNull(submissionTargetTerm.getDateCompleted());

		    assertEquals(targetStatus, targetStatusOld);
		    assertTrue(submissionTargetTerm.getCanceled());
		    assertTrue(submissionTargetTerm.getCommited());

		    submissionFlag = true;
		}
	    }
	}

	assertTrue(regularFlag);
	assertTrue(submissionFlag);
    }

    @Before
    public void setUp() throws Exception {
	_tmProject = getModelObject("tmProject", TmProject.class);
	_tmProject.getProjectDetail().setProject(_tmProject);

	_submission = new Submission();
	_submission.setIdentifier(1L);
	_submission.setName("submission name");
	_submission.setSubmissionLanguages(new HashSet<SubmissionLanguage>());
	_submission.setProject(_tmProject);
	_submission.setSourceLanguageId(EN_US);

	reset(getSubmissionDAO());
	reset(getGlossaryBrowser());
	reset(getStatisticsService());

	when(getSubmissionDAO().findSubmissionByIdFetchChilds(1L)).thenReturn(_submission);
    }

    @Test
    @TestCase("submissionTerm")
    public void updateTempTermTextTest_01() throws TmException {
	Long projectId = 1L;

	Term term = getModelObject("term2", Term.class);
	TermEntry termEntry = getModelObject("termEntry1", TermEntry.class);
	termEntry.setProjectId(projectId);

	String termId = term.getUuId();

	when(getGlossaryBrowser().findByTermId(termId, projectId)).thenReturn(termEntry);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getSubmissionTermService().updateTempTermText(termId, "some changed text2", projectId);

	verify(getGlossaryBrowser(), times(1)).findByTermId(termId, projectId);

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	TermEntry after = argument.getValue().getTermEntries().get(0);
	Term termAfter = after.ggetTermById(termId);

	assertNotNull(termAfter);
	assertEquals("some changed text2", termAfter.getTempText());
	assertEquals(Boolean.FALSE, termAfter.getCommited());
    }

    private StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    private SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }

    private void verifyRegularTerms(TermEntry tEntry) {
	for (Term term : tEntry.ggetTerms()) {
	    assertTrue(term.getStatus().equals(ItemStatusTypeHolder.PROCESSED.getName()));

	    if (term.getUuId().equals("474e93ae-7264-4088-9d54-targetTermId_0100000002")) {
		assertNotNull(term.getDateModified());

	    }
	    if (term.getUuId().equals("474e93ae-7264-4088-9d54-sourceTermId00000001")) {
		assertFalse(term.getInTranslationAsSource());
	    }

	}
    }

    private void verifySubmissionTerms(TermEntry tEntry) {
	for (Term term : tEntry.ggetTerms()) {
	    assertTrue(term.getStatus().equals(ItemStatusTypeHolder.PROCESSED.getName()));
	    if (term.getUuId().equals("474e93ae-7264-4088-9d54-sub-submissionTargetTermId0002")) {
		assertEquals(term.getName(), term.getTempText());
		assertTrue(term.getStatus().equals(ItemStatusTypeHolder.PROCESSED.getName()));

		assertNotNull(term.getDateCompleted());
		assertNotNull(term.getDateModified());

		assertNull(term.getReviewRequired());
	    }
	}
    }

}

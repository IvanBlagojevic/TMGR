package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.SubmissionDAO;
import org.gs4tr.termmanager.dao.SubmissionLanguageCommentDAO;
import org.gs4tr.termmanager.dao.SubmissionLanguageDAO;
import org.gs4tr.termmanager.dao.SubmissionUserDAO;
import org.gs4tr.termmanager.model.EntityStatusPriority;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionLanguageComment;
import org.gs4tr.termmanager.model.SubmissionUser;
import org.gs4tr.termmanager.model.TermEntryTranslationUnit;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.PriorityEnum;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import jetbrains.exodus.entitystore.EntityId;

@TestSuite("service")
public class SubmissionServiceTest extends AbstractServiceTest {

    private static final String COMMENT_MARKER_ID = "922547ea-0b0d-4fcf-aa94-40672e46d277";

    private static final String COMMENT_VALUE = "I try to reproduce bug";

    private static final boolean COMMITED = false;

    private static final Long PROJECT_ID = 1L;

    private static final String SUBMISSION_COMMIT_TERM_ENTRY_ID_01 = "474e93ae-submission-entry-term-sc01";

    private static final String SUBMISSION_COMMIT_TERM_ID_01 = "474e93ae-7264-4088-9d54-sub-term-c01";

    private static final String SUBMISSION_COMMIT_TERM_ID_02 = "474e93ae-7264-4088-9d54-sub-term-c02";

    private static final String SUBMISSION_MARKER_ID = "15bfd173-fa91-4ed2-9111-be7b915eec01";

    private static final String TERM_ENTRY_ID_01 = "474e93ae-7264-4088-9d54-termentry001";

    private static final String TERM_ID_01 = "474e93ae-7264-4088-9d54-term00000001";

    private static final String TERM_ID_02 = "474e93ae-7264-4088-9d54-term00000002";

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private SessionFactory _sessionFactory;

    @Autowired
    private SubmissionDAO _submissionDAO;

    @Autowired
    private SubmissionLanguageCommentDAO _submissionLanguageCommentDAO;

    @Autowired
    private SubmissionLanguageDAO _submissionLanguageDAO;

    @Autowired
    private SubmissionService _submissionService;

    @Autowired
    private SubmissionUserDAO _submissionUserDAO;

    @SuppressWarnings({ "unchecked" })
    @Test
    @TestCase("submission")
    public void addCommentsTest_01() throws TmException {
	Submission submission = mockSubmission();

	List<String> submissionTermIds = getModelObject("submissionTermIds", List.class);
	TermEntry submissionTermEntry = getModelObject("submissionTermEntry1", TermEntry.class);

	when(getSubmissionDAO().load(1L)).thenReturn(submission);
	when(getGlossaryBrowser().findByTermId(anyString(), anyLong())).thenReturn(submissionTermEntry);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getSubmissionService().addComments("comment text", 1L, submissionTermIds, "en-US");

	verify(getSubmissionDAO(), times(1)).load(1L);
	verify(getGlossaryBrowser(), times(2)).findByTermId(anyString(), anyLong());

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), atLeastOnce()).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	TermEntry termEntry = argument.getValue().getTermEntries().get(0);

	List<Term> updatedTerms = termEntry.ggetTerms();
	Assert.assertEquals(2, updatedTerms.size());

	for (Term term : updatedTerms) {
	    Assert.assertNotNull(term.getCommited());
	    Assert.assertEquals("comment text", term.getComments().iterator().next().getText());
	}
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("submission")
    public void cancelSubmissionTest_01() throws TmException {
	Submission submission = mockSubmission();

	TermEntry submissionTermEntry = getModelObject("submissionTermEntry1", TermEntry.class);
	submissionTermEntry.setProjectId(PROJECT_ID);
	List<String> submissionTermIds = getModelObject("submissionTermIds", List.class);

	List<TermEntry> termEntyList = new ArrayList<>();
	termEntyList.add(submissionTermEntry);

	Long projectId = PROJECT_ID;

	when(getSubmissionDAO().load(1L)).thenReturn(submission);
	when(getGlossaryBrowser().browse(any(TmgrSearchFilter.class))).thenReturn(termEntyList);
	when(getGlossaryBrowser().findByTermIds(submissionTermIds, projectId)).thenReturn(termEntyList);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	Set<String> termEntryIds = getSubmissionService().cancelSubmission(1L);

	Assert.assertNotNull(termEntryIds);
	Assert.assertEquals(1, termEntryIds.size());

	verify(getGlossaryBrowser(), times(1)).browse(any(TmgrSearchFilter.class));
	verify(getGlossaryBrowser(), times(2)).findByTermIds(submissionTermIds, projectId);

	Assert.assertEquals(2, submission.getProject().getProjectDetail().getApprovedTermCount());
	Assert.assertEquals(0, submission.getProject().getProjectDetail().getTermInSubmissionCount());

    }

    /*
     * TERII-3121: Able to cancel completed/canceled submissions and that effects
     * completed submission count in project view
     */
    @Test
    @TestCase("submission")
    @SuppressWarnings("unchecked")
    public void canceledSubmissionCountTest() throws TmException {
	Submission canceledSubmission = mockSubmission();

	canceledSubmission.setDateModified(new Date());

	List<TermEntry> termEntries = getModelObject("termEntries", List.class);
	termEntries.forEach(e -> e.setProjectId(PROJECT_ID));

	when(getSubmissionDAO().load(1L)).thenReturn(canceledSubmission);
	when(getGlossaryBrowser().browse(any(TmgrSearchFilter.class))).thenReturn(termEntries);
	when(getGlossaryBrowser().findByTermIds(anyList(), anyLong())).thenReturn(termEntries);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	Set<String> termEntryIds = getSubmissionService().cancelSubmission(1L);

	assertNotNull(termEntryIds);
	assertTrue(CollectionUtils.isEmpty(termEntryIds));

	verify(getGlossaryBrowser(), times(1)).browse(any(TmgrSearchFilter.class));
	verify(getGlossaryBrowser(), times(2)).findByTermIds(anyList(), anyLong());

	ProjectDetail projectDetail = canceledSubmission.getProject().getProjectDetail();

	assertNotNull(projectDetail);
	assertNotNull(projectDetail.getDateModified());

	/* Validate that the project details have not changed */

	assertEquals(3l, projectDetail.getLanguageCount());
	assertEquals(2l, projectDetail.getTermCount());
	assertEquals(2l, projectDetail.getApprovedTermCount());
	assertEquals(0l, projectDetail.getForbiddenTermCount());
	assertEquals(1l, projectDetail.getTermEntryCount());
	assertEquals(0l, projectDetail.getTermInSubmissionCount());

	assertEquals(0l, projectDetail.getActiveSubmissionCount());
	assertEquals(1l, projectDetail.getCompletedSubmissionCount());
    }

    /* Test without send to review */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("submission")
    public void commitTranslationChangesTest_01() throws TmException {

	TermEntry submissionTermEntry = mockSubmissionCommitTermEntry(false);
	submissionTermEntry.setProjectId(PROJECT_ID);
	TermEntry regularTermEntry = mockRegularTermEntry();
	regularTermEntry.setProjectId(PROJECT_ID);

	/* Submission term ids */
	List<String> termIds = getModelObject("submissionTermCommitIds", List.class);

	Long projectId = PROJECT_ID;

	when(getGlossaryBrowser().findTermsByIds(termIds, Arrays.asList(projectId)))
		.thenReturn(submissionTermEntry.ggetTerms());
	when(getGlossaryBrowser().findById(TERM_ENTRY_ID_01, projectId)).thenReturn(regularTermEntry);
	when(getGlossaryBrowser().findById(SUBMISSION_COMMIT_TERM_ENTRY_ID_01, projectId))
		.thenReturn(submissionTermEntry);

	List<Term> submissionTerms = submissionTermEntry.ggetTerms();
	when(getGlossaryBrowser().findTermById(submissionTerms.get(0).getUuId(), projectId))
		.thenReturn(submissionTerms.get(0));
	when(getGlossaryBrowser().findTermById(submissionTerms.get(1).getUuId(), projectId))
		.thenReturn(submissionTerms.get(1));

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	Set<String> result = getSubmissionService().commitTranslationChanges(1L, "en-US", termIds);

	Assert.assertEquals(Action.UPDATED_TRANSLATIONS, regularTermEntry.getAction());

	Assert.assertNotNull(result);
	Assert.assertEquals(1, result.size());
	Assert.assertEquals(SUBMISSION_COMMIT_TERM_ENTRY_ID_01, result.iterator().next());

	verify(getGlossaryBrowser(), times(1)).findTermsByIds(anyList(), anyList());
	verify(getGlossaryBrowser(), times(4)).findById(anyString(), anyLong());

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), atLeastOnce()).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	List<TransactionalUnit> units = argument.getAllValues();

	boolean regularFlag = false;
	boolean submissionFlag = false;

	for (TransactionalUnit unit : units) {
	    List<TermEntry> entries = unit.getTermEntries();
	    for (TermEntry termEntry : entries) {
		if (termEntry.getUuId().equals(TERM_ENTRY_ID_01)) {
		    Term sourceTerm = termEntry.ggetTermById(TERM_ID_01);
		    Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), sourceTerm.getStatus());
		    Assert.assertEquals("regular term text 1", sourceTerm.getName());
		    Assert.assertEquals(Boolean.FALSE, sourceTerm.getInTranslationAsSource());

		    Term targetTerm = termEntry.ggetTermById(TERM_ID_02);
		    Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), targetTerm.getStatus());
		    Assert.assertEquals("some changed target text", targetTerm.getName());
		    Assert.assertEquals(Boolean.FALSE, targetTerm.getInTranslationAsSource());
		    regularFlag = true;
		}
		if (termEntry.getUuId().equals(SUBMISSION_COMMIT_TERM_ENTRY_ID_01)) {
		    Term submissionSourceTerm = termEntry.ggetTermById(SUBMISSION_COMMIT_TERM_ID_01);
		    Assert.assertEquals(Boolean.TRUE, submissionSourceTerm.getCommited());
		    Assert.assertEquals(Boolean.FALSE, submissionSourceTerm.getInTranslationAsSource());

		    Term submissionTargetTerm = termEntry.ggetTermById(SUBMISSION_COMMIT_TERM_ID_02);
		    Assert.assertEquals(Boolean.TRUE, submissionTargetTerm.getCommited());
		    Assert.assertEquals(Boolean.FALSE, submissionTargetTerm.getInTranslationAsSource());
		    submissionFlag = true;
		}
	    }
	}

	Assert.assertTrue(regularFlag);
	Assert.assertTrue(submissionFlag);
    }

    /* Test with send to review */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("submission")
    public void commitTranslationChangesTest_02() throws TmException {
	/* Mock submission */
	Submission submission = mockSubmission();
	Set<SubmissionLanguage> submissionLanguages = getModelObject("submissionLanguagesCommit", Set.class);
	submission.setSubmissionLanguages(submissionLanguages);

	TermEntry submissionTermEntry = mockSubmissionCommitTermEntry(true);
	submissionTermEntry.setProjectId(PROJECT_ID);
	TermEntry regularTermEntry = mockRegularTermEntry();
	regularTermEntry.setProjectId(PROJECT_ID);

	/* Submission term ids */
	List<String> termIds = getModelObject("submissionTermCommitIds", List.class);

	Long projectId = PROJECT_ID;

	when(getSubmissionDAO().findSubmissionByIdFetchChilds(1L)).thenReturn(submission);
	when(getGlossaryBrowser().findTermsByIds(termIds, Arrays.asList(projectId)))
		.thenReturn(submissionTermEntry.ggetTerms());
	when(getGlossaryBrowser().findById(TERM_ENTRY_ID_01, projectId)).thenReturn(regularTermEntry);
	when(getGlossaryBrowser().findById(SUBMISSION_COMMIT_TERM_ENTRY_ID_01, projectId))
		.thenReturn(submissionTermEntry);
	List<Term> submissionTerms = submissionTermEntry.ggetTerms();

	when(getGlossaryBrowser().findTermById(submissionTerms.get(0).getUuId(), projectId))
		.thenReturn(submissionTerms.get(0));
	when(getGlossaryBrowser().findTermById(submissionTerms.get(1).getUuId(), projectId))
		.thenReturn(submissionTerms.get(1));

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	Set<String> result = getSubmissionService().commitTranslationChanges(1L, "en-US", termIds);

	Assert.assertEquals(Action.UPDATED_TRANSLATIONS, regularTermEntry.getAction());
	Assert.assertNotNull(result);
	Assert.assertEquals(1, result.size());
	Assert.assertEquals(SUBMISSION_COMMIT_TERM_ENTRY_ID_01, result.iterator().next());

	// verify(getSubmissionDAO(),
	// times(1)).findSubmissionByIdFetchChilds(1L);
	verify(getGlossaryBrowser(), times(1)).findTermsByIds(any(List.class), anyList());
	verify(getGlossaryBrowser(), times(4)).findById(anyString(), anyLong());

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), new Times(2)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	List<TransactionalUnit> units = argument.getAllValues();

	boolean regularFlag = false;
	boolean submissionFlag = false;

	for (TransactionalUnit unit : units) {
	    List<TermEntry> entries = unit.getTermEntries();
	    for (TermEntry termEntry : entries) {
		if (termEntry.getUuId().equals(TERM_ENTRY_ID_01)) {
		    Term sourceTerm = termEntry.ggetTermById(TERM_ID_01);
		    Assert.assertEquals("regular term text 1", sourceTerm.getName());
		    Assert.assertEquals(Boolean.TRUE, sourceTerm.getInTranslationAsSource());
		    Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), sourceTerm.getStatus());

		    Term targetTerm = termEntry.ggetTermById(TERM_ID_02);
		    Assert.assertEquals("some changed target text", targetTerm.getName());
		    Assert.assertEquals(Boolean.FALSE, targetTerm.getInTranslationAsSource());
		    Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), targetTerm.getStatus());
		    regularFlag = true;
		}
		if (termEntry.getUuId().equals(SUBMISSION_COMMIT_TERM_ENTRY_ID_01)) {
		    Term submissionSourceTerm = termEntry.ggetTermById(SUBMISSION_COMMIT_TERM_ID_01);
		    Assert.assertEquals(Boolean.TRUE, submissionSourceTerm.getCommited());
		    Assert.assertEquals(Boolean.TRUE, submissionSourceTerm.getInTranslationAsSource());
		    Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(),
			    submissionSourceTerm.getStatus());

		    Term submissionTargetTerm = termEntry.ggetTermById(SUBMISSION_COMMIT_TERM_ID_02);
		    Assert.assertEquals(Boolean.TRUE, submissionTargetTerm.getCommited());
		    Assert.assertEquals(Boolean.FALSE, submissionTargetTerm.getInTranslationAsSource());
		    Assert.assertEquals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(),
			    submissionTargetTerm.getStatus());
		    submissionFlag = true;
		}
	    }
	}

	assertTrue(regularFlag);
	assertTrue(submissionFlag);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("submission")
    public void createSubmissionTest_01() throws TmException {
	List<Term> sourceTerms = getModelObject("sourceTerms", List.class);

	Long projectId = 1L;
	TermEntry regularTermEntry = getModelObject("termEntryEntity", TermEntry.class);
	regularTermEntry.setProjectId(projectId);
	for (Term term : sourceTerms) {
	    regularTermEntry.addTerm(term);
	}

	mockSubmissionDaos();

	String submissionMarkerId = UUID.randomUUID().toString();

	List<TermEntryTranslationUnit> translationUnits = createTranslationUnits(projectId, submissionMarkerId);

	TermEntry submissionTermEntry = getModelObject("termEntryEntity", TermEntry.class);

	when(getGlossaryBrowser().findById(any(String.class), any(Long.class))).thenReturn(submissionTermEntry);
	when(getGlossaryBrowser().browse(any(TmgrSearchFilter.class))).thenReturn(null);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	Submission submission = getSubmissionService().createSubmission(1L, translationUnits, "submission name",
		"submission-marker", "en-US", false, null, sourceTerms);

	verify(getSubmissionDAO(), atLeastOnce()).save(any(Submission.class));
	verify(getGlossaryBrowser(), atLeastOnce()).findById(any(String.class), any(Long.class));

	Assert.assertNotNull(submission);
	Assert.assertNotNull(submission.getDateSubmitted());
	Assert.assertNotNull(submission.getEntityStatusPriority());
	Assert.assertNotNull(submission.getMarkerId());
	Assert.assertNotNull(submission.getName());
	Assert.assertNotNull(submission.getProject());
	Assert.assertNotNull(submission.getSourceLanguageId());
	Assert.assertNotNull(submission.getSubmissionId());
	Assert.assertNotNull(submission.getSubmissionLanguages());
	Assert.assertNotNull(submission.getSubmissionUsers());
	Assert.assertNotNull(submission.getSubmitter());
	Assert.assertNotNull(submission.getTermEntryCount());

	Assert.assertEquals(Action.SENT_TO_TRANSLATION, regularTermEntry.getAction());

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);

	verify(getTransactionLogHandler(), atLeastOnce()).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	List<TermEntry> entries = argument.getValue().getTermEntries();

	boolean flag = false;

	for (TermEntry termEntry : entries) {
	    if (termEntry.getParentUuId() != null && termEntry.getParentUuId().equals(TERM_ENTRY_ID_01)) {
		Set<Term> deTerms = termEntry.getLanguageTerms().get("de-DE");
		Assert.assertNotNull(deTerms);
		Assert.assertEquals(1, deTerms.size());
		Set<Term> frTerms = termEntry.getLanguageTerms().get("fr-FR");
		Assert.assertNotNull(frTerms);
		Assert.assertEquals(1, frTerms.size());
		flag = true;
	    }

	    for (Term term : termEntry.ggetTerms()) {
		if (!term.getInTranslationAsSource()) {
		    Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), term.getStatus());
		}
	    }
	}

	Assert.assertTrue(flag);
    }

    /* Create Submission with submission comment test */
    @Test
    @TestCase("submission")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void createSubmissionWithSubmissionCommentTest() throws TmException {
	List<Term> sourceTerms = getModelObject("sourceTerms", List.class);

	TermEntry termEntryEntity = mockRegularTermEntry();

	List<TermEntryTranslationUnit> translationUnits = mockTranslationUnits(PROJECT_ID, SUBMISSION_MARKER_ID);

	when(getGlossaryBrowser().findById(eq(TERM_ENTRY_ID_01), any(Long.class))).thenReturn(termEntryEntity);
	when(getGlossaryBrowser().browse(any(TmgrSearchFilter.class))).thenReturn(null);

	mockSubmissionDaos();

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	Submission submission = getSubmissionService().createSubmission(PROJECT_ID, translationUnits, "Submission1",
		SUBMISSION_MARKER_ID, "en-US", false, null, sourceTerms);

	when(getSubmissionDAO().findSubmissionByIdFetchChilds(1L)).thenReturn(submission);
	when(getSubmissionDAO().load(1L)).thenReturn(submission);

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);

	verify(getTransactionLogHandler(), new Times(2)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());
	verify(getSubmissionDAO(), atLeastOnce()).save(any(Submission.class));
	verify(getGlossaryBrowser(), atLeastOnce()).findById(anyString(), any(Long.class));

	verifySubmission(submission);

	List<TermEntry> entries = argument.getValue().getTermEntries();
	for (TermEntry termEntry : entries) {
	    if (termEntry.getParentUuId() != null && termEntry.getParentUuId().equals(TERM_ENTRY_ID_01)) {
		Set<Term> enTerms = termEntry.getLanguageTerms().get("en-US");
		Set<Term> deTerms = termEntry.getLanguageTerms().get("de-DE");

		for (Term term : termEntry.ggetAllTerms()) {
		    if (!term.getInTranslationAsSource()) {
			assertTrue(term.getStatus().equals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName()));

			assertTrue(term.getCommited().equals(COMMITED));
		    }
		}
		assertNotNull(enTerms);
		assertNotNull(deTerms);

		assertTrue(deTerms.size() == 1);
		assertTrue(enTerms.size() == 1);
	    } else {
		for (Term term : termEntry.ggetAllTerms()) {

		    if (!term.getInTranslationAsSource()) {
			assertTrue(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName().equals(term.getStatus()));
		    }
		}
	    }
	}

    }

    @Override
    public ProjectDetailDAO getProjectDetailDAO() {
	return _projectDetailDAO;
    }

    public SessionFactory getSessionFactory() {
	return _sessionFactory;
    }

    @Override
    public SubmissionDAO getSubmissionDAO() {
	return _submissionDAO;
    }

    /*
     * "Term count is off in submission for resubmit and cancel combination" . I try
     * to reproduce that case in this test.
     */
    @Test
    @TestCase("submission")
    @SuppressWarnings("unchecked")
    public void reSubmitTermCountTest() throws TmException {
	List<String> resubmitTermIds = getModelObject("resubmitTermIds", List.class);

	/* Mock submission and set submissionLanguageState */
	Submission submission = mockSubmission();

	TermEntry termEntry = getModelObject("resubmitTermEntry", TermEntry.class);
	termEntry.setProjectId(1L);

	when(getSubmissionDAO().load(anyLong())).thenReturn(submission);

	try {
	    when(getGlossaryBrowser().findByTermIds(anyCollection(), anyLong())).thenReturn(Arrays.asList(termEntry));
	} catch (TmException e) {
	    e.printStackTrace();
	}

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	Submission result = getSubmissionService().reSubmitTerms(1L, resubmitTermIds);

	verify(getSubmissionDAO(), times(1)).load(anyLong());
	verify(getGlossaryBrowser(), times(2)).findByTermIds(anyCollection(), anyLong());
	verify(getTransactionLogHandler(), times(2)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		any(TransactionalUnit.class));

	assertNotNull(result);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @TestCase("submission")
    public void reSubmitTest_01() throws TmException {
	/* Mock submission */
	Submission submission = mockSubmission();

	/* Submission term ids */
	List<String> termIds = getModelObject("submissionTermCommitIds", List.class);

	TermEntry termEntry = getModelObject("submissionCommitTermEntry1", TermEntry.class);
	termEntry.setProjectId(PROJECT_ID);
	Term term1 = getModelObject("submissionTermCommit1", Term.class);
	term1.setStatus(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName());
	Term term2 = getModelObject("submissionTermCommit2", Term.class);
	term2.setStatus(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName());
	termEntry.addTerm(term1);
	termEntry.addTerm(term2);

	Long projectId = PROJECT_ID;

	when(getSubmissionDAO().load(1L)).thenReturn(submission);
	when(getGlossaryBrowser().findByTermIds(termIds, projectId)).thenReturn(Arrays.asList(termEntry));

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	Submission result = getSubmissionService().reSubmitTerms(1L, termIds);
	Assert.assertNotNull(result);

	verify(getSubmissionDAO(), times(1)).load(1L);
	verify(getGlossaryBrowser(), times(1)).findByTermIds(termIds, projectId);

	ArgumentCaptor<TransactionalUnit> argument = ArgumentCaptor.forClass(TransactionalUnit.class);
	verify(getTransactionLogHandler(), new Times(1)).appendAndLink(anyLong(), Mockito.any(EntityId.class),
		argument.capture());

	List<TermEntry> entries = argument.getValue().getTermEntries();

	for (TermEntry te : entries) {

	    Assert.assertNotNull(te);
	    Assert.assertEquals(2, termEntry.ggetTerms().size());

	    for (Term term : te.ggetTerms()) {
		Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), term.getStatus());
		Assert.assertEquals(PriorityEnum.HIGH.getValue(), term.getPriority().getAssigneePriority());
		Assert.assertEquals(PriorityEnum.NORMAL.getValue(), term.getPriority().getSubmitterPriority());
	    }
	}

    }

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
	// Reset mock
	reset(getGlossaryBrowser());
	reset(getGlossaryUpdater());
	reset(getProjectDAO());
	reset(getProjectDetailDAO());
	reset(getSubmissionDAO());
	reset(getSubmissionLanguageDAO());
	reset(getSubmissionUserDAO());

	TmProject tmProject = new TmProject();

	tmProject = getModelObject("tmProject", TmProject.class);
	tmProject.getProjectDetail().setProject(tmProject);

	Set<ProjectUserDetail> set = new HashSet<ProjectUserDetail>();
	set.add(new ProjectUserDetail((TmUserProfile) UserProfileContext.getCurrentUserProfile(),
		tmProject.getProjectDetail()));
	tmProject.getProjectDetail().setUserDetails(set);

	when(getProjectDAO().load(any(Long.class))).thenReturn(tmProject);
	when(getProjectDAO().findById(any(Long.class))).thenReturn(tmProject);

	Submission submission = mockSubmission();
	Set<SubmissionLanguage> submissionLanguages = getModelObject("submissionLanguagesCommit", Set.class);
	submission.setSubmissionLanguages(submissionLanguages);

	when(getSubmissionDAO().findSubmissionByIdFetchChilds(1L)).thenReturn(submission);
	when(getSubmissionDAO().load(1L)).thenReturn(submission);

	ProjectDetail projectDetail = Mockito.mock(ProjectDetail.class);
	when(getQuery().uniqueResult()).thenReturn(submission).thenReturn(projectDetail);
    }

    private List<TermEntryTranslationUnit> createTranslationUnits(Long projectId, String jobUuid) {
	UpdateCommand termCommand1 = createUpdateCommand(TypeEnum.TERM, CommandEnum.TRANSLATE, "de-DE", "translator de",
		TERM_ID_01, TERM_ENTRY_ID_01, null);
	UpdateCommand termCommand2 = createUpdateCommand(TypeEnum.TERM, CommandEnum.TRANSLATE, "fr-FR", "translator fr",
		TERM_ID_02, TERM_ENTRY_ID_01, null);

	List<TermEntryTranslationUnit> translationUnits = new ArrayList<TermEntryTranslationUnit>();
	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();

	updateCommands.add(termCommand1);
	updateCommands.add(termCommand2);

	TermEntryTranslationUnit unit = new TermEntryTranslationUnit();

	unit.setProjectId(projectId);
	unit.setTermEntryId(TERM_ENTRY_ID_01);
	unit.setUpdateCommands(updateCommands);

	translationUnits.add(unit);

	return translationUnits;
    }

    private UpdateCommand createUpdateCommand(TypeEnum type, CommandEnum command, String languageId, String asssignee,
	    String markerId, String parentMarkerId, String value) {
	UpdateCommand updateCommand = new UpdateCommand();
	updateCommand.setItemType(type.name().toLowerCase());
	updateCommand.setCommand(command.name().toLowerCase());
	updateCommand.setLanguageId(languageId);
	updateCommand.setMarkerId(markerId);
	updateCommand.setAsssignee(asssignee);
	updateCommand.setParentMarkerId(parentMarkerId);
	updateCommand.setValue(value);

	return updateCommand;
    }

    private ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    private SubmissionLanguageCommentDAO getSubmissionLanguageCommentDAO() {
	return _submissionLanguageCommentDAO;
    }

    private SubmissionLanguageDAO getSubmissionLanguageDAO() {
	return _submissionLanguageDAO;
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }

    private SubmissionUserDAO getSubmissionUserDAO() {
	return _submissionUserDAO;
    }

    private void maintainTranslationUnit(UpdateCommand command, List<TermEntryTranslationUnit> translationUnits,
	    Long projectId, String termEntryId) {
	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	updateCommands.add(command);

	TermEntryTranslationUnit unit = new TermEntryTranslationUnit();

	unit.setProjectId(projectId);
	unit.setTermEntryId(termEntryId);
	unit.setUpdateCommands(updateCommands);

	translationUnits.add(unit);
    }

    private TermEntry mockRegularTermEntry() {
	/* Populate regular term entry */
	TermEntry regularTermEntry = getModelObject("termEntryEntity", TermEntry.class);
	regularTermEntry.setProjectId(1L);
	Term regularTerm1 = getModelObject("term1", Term.class);
	Term regularTerm2 = getModelObject("term2", Term.class);

	regularTermEntry.addTerm(regularTerm1);
	regularTermEntry.addTerm(regularTerm2);

	return regularTermEntry;
    }

    private Submission mockSubmission() {
	TmProject tmProject = getModelObject("tmProject", TmProject.class);
	tmProject.getProjectDetail().setProject(tmProject);

	@SuppressWarnings("unchecked")
	Set<SubmissionLanguage> submissionLanguages = getModelObject("submissionLanguages", Set.class);

	Submission submission = new Submission();
	submission.setDateSubmitted(new Date());
	submission.setIdentifier(1L);
	submission.setSubmissionLanguages(submissionLanguages);
	submission.setProject(tmProject);
	submission.setSubmitter("marko");
	submission.setSourceLanguageId("en-US");
	submission.setTargetLanguageIds("de-DE");
	submission.setMarkerId("markerId");
	submission.setName("subName");

	Set<SubmissionUser> submissionUsers = new HashSet<SubmissionUser>();
	SubmissionUser user1 = new SubmissionUser();
	user1.setUser((TmUserProfile) UserProfileContext.getCurrentUserProfile());
	user1.setEntityStatusPriority(new EntityStatusPriority());
	submissionUsers.add(user1);
	submission.setSubmissionUsers(submissionUsers);

	return submission;
    }

    private TermEntry mockSubmissionCommitTermEntry(boolean reviewRequired) {
	/* Populate submission term entry */
	TermEntry submissionTermEntry = getModelObject("submissionCommitTermEntry1", TermEntry.class);
	Term submissionTerm1 = getModelObject("submissionTermCommit1", Term.class);
	submissionTerm1.setReviewRequired(reviewRequired);
	Assert.assertEquals(Boolean.TRUE, submissionTerm1.getInTranslationAsSource());
	Assert.assertEquals(Boolean.FALSE, submissionTerm1.getCommited());

	Term submissionTerm2 = getModelObject("submissionTermCommit2", Term.class);
	submissionTerm2.setReviewRequired(reviewRequired);
	Assert.assertEquals(Boolean.FALSE, submissionTerm2.getInTranslationAsSource());
	Assert.assertEquals(Boolean.FALSE, submissionTerm2.getCommited());

	submissionTermEntry.addTerm(submissionTerm1);
	submissionTermEntry.addTerm(submissionTerm2);
	return submissionTermEntry;
    }

    private void mockSubmissionDaos() {
	when(getSubmissionDAO().save(any(Submission.class))).thenAnswer(new Answer<Submission>() {
	    @Override
	    public Submission answer(InvocationOnMock invocation) throws Throwable {
		Submission submission = (Submission) invocation.getArguments()[0];
		submission.setIdentifier(1L);
		return submission;
	    }
	});

	when(getSubmissionLanguageDAO().save(any(SubmissionLanguage.class)))
		.thenAnswer(new Answer<SubmissionLanguage>() {

		    @Override
		    public SubmissionLanguage answer(InvocationOnMock invocation) throws Throwable {
			return (SubmissionLanguage) invocation.getArguments()[0];
		    }
		});

	when(getSubmissionUserDAO().save(any(SubmissionUser.class))).thenAnswer(new Answer<SubmissionUser>() {

	    @Override
	    public SubmissionUser answer(InvocationOnMock invocation) throws Throwable {
		return (SubmissionUser) invocation.getArguments()[0];
	    }
	});

	when(getSubmissionLanguageCommentDAO().save(any(SubmissionLanguageComment.class)))
		.thenAnswer(new Answer<SubmissionLanguageComment>() {

		    @Override
		    public SubmissionLanguageComment answer(InvocationOnMock invocation) throws Throwable {

			return (SubmissionLanguageComment) invocation.getArguments()[0];
		    }
		});
    }

    private List<TermEntryTranslationUnit> mockTranslationUnits(Long projectId, String jobUuid) {
	UpdateCommand command1 = createUpdateCommand(TypeEnum.TERM, CommandEnum.REVIEW, "de-DE", "translator de",
		TERM_ID_02, TERM_ENTRY_ID_01, null);
	UpdateCommand command2 = createUpdateCommand(TypeEnum.COMMENT, CommandEnum.TRANSLATE, null, null,
		COMMENT_MARKER_ID, jobUuid, COMMENT_VALUE);

	List<TermEntryTranslationUnit> translationUnits = new ArrayList<TermEntryTranslationUnit>();
	maintainTranslationUnit(command1, translationUnits, projectId, TERM_ENTRY_ID_01);
	maintainTranslationUnit(command2, translationUnits, projectId, null);

	return translationUnits;
    }

    private void verifySubmission(Submission submission) {
	assertNotNull(submission);
	assertNotNull(submission.getMarkerId());
	assertNotNull(submission.getProject());
	assertNotNull(submission.getDateSubmitted());
	assertNotNull(submission.getSourceLanguageId());
	assertNotNull(submission.getSubmissionLanguages());
	assertNotNull(submission.getSubmissionUsers());
	assertNotNull(submission.getSubmitter());
	assertNotNull(submission.getTermEntryCount());

	for (SubmissionLanguage subLang : submission.getSubmissionLanguages()) {
	    Set<SubmissionLanguageComment> submissionLanguageComments = subLang.getSubmissionLanguageComments();
	    assertNotNull(submissionLanguageComments);
	}

	assertTrue(StringUtils.isNotEmpty(submission.getName()));
	assertTrue(submission.getName().equals("Submission1"));
    }

}

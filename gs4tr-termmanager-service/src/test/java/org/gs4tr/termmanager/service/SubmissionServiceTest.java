package org.gs4tr.termmanager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionLanguageComment;
import org.gs4tr.termmanager.model.TermEntryTranslationUnit;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Comment;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.TmException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmissionServiceTest extends AbstractSolrGlossaryTest {

    private static final String SUB_COMENT_TEXT = "This is new submission coment text";

    private static final String TERM_COMENT_TEXT = "This is new term coment text";

    @Autowired
    private SubmissionService _submissionService;

    @Autowired
    private SubmissionTermService _submissionTermService;

    @Test
    public void addSubmissionCommentTest() {
	String commentText = "This is new comment";
	String oldCommentText = "This is sub language comment";
	Long submissionId = 1L;

	getSubmissionService().addComments(commentText, submissionId, null, null);

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    Set<SubmissionLanguageComment> submissionLanguageComments = submissionLanguage
		    .getSubmissionLanguageComments();
	    boolean flag = false;
	    for (SubmissionLanguageComment comment : submissionLanguageComments) {
		String text = comment.getText();
		if (commentText.equals(text) || oldCommentText.equals(text)) {
		    flag = true;
		}
		Assert.assertTrue(flag);
	    }
	}
    }

    @Test
    public void addTermCommentTest() throws TmException {
	String commentText = "This is new comment";
	Long submissionId = 1L;

	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);

	Long projectId = submission.getProject().getProjectId();

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));

	List<String> subTermIds = new ArrayList<String>();
	subTermIds.add(SUB_TERM_ID_01);

	getSubmissionService().addComments(commentText, submissionId, subTermIds, null);

	Term subTerm = getSubmissionConnector().getTmgrBrowser().findTermById(SUB_TERM_ID_01, projectId);

	Set<Comment> subTermComments = subTerm.getComments();
	Assert.assertTrue(CollectionUtils.isNotEmpty(subTermComments));

	boolean flag = false;
	for (Comment comment : subTermComments) {
	    String text = comment.getText();
	    if (commentText.equals(text)) {
		flag = true;
	    }
	}

	Assert.assertTrue(flag);
    }

    @Test
    public void cancelSubmissionTest() {
	Long submissionId = 1L;
	Submission submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	getSubmissionService().cancelSubmission(submissionId);

	submission = getSubmissionService().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);
	Assert.assertEquals(ItemStatusTypeHolder.CANCELLED, submission.getEntityStatusPriority().getStatus());
	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));

	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    Assert.assertEquals(ItemStatusTypeHolder.CANCELLED,
		    submissionLanguage.getEntityStatusPriority().getStatus());
	    Assert.assertEquals(ItemStatusTypeHolder.CANCELLED, submissionLanguage.getStatusAssignee());
	    Assert.assertTrue(submissionLanguage.getTermCanceledCount() > 0);
	    Assert.assertTrue(submissionLanguage.getTermCanceledCount() == submissionLanguage.getTermCount());
	}
    }

    @Test
    public void findBySubmissionIdTest_01() throws TmException {
	long submissionId = 10L;

	Long projectId = 1L;

	TermEntry termEntry = new TermEntry();
	String termEntryId = UUID.randomUUID().toString();
	termEntry.setUuId(termEntryId);
	termEntry.setSubmissionId(submissionId);
	termEntry.setProjectId(projectId);

	Term term = new Term();
	term.setUuId(UUID.randomUUID().toString());
	term.setLanguageId("en-US");
	term.setSubmissionId(submissionId);
	termEntry.addTerm(term);

	getRegularConnector().getTmgrUpdater().save(termEntry);

	TermEntry submissionTermEntry = getRegularConnector().getTmgrBrowser().findById(termEntryId, projectId);
	Assert.assertNotNull(submissionTermEntry);
	Assert.assertEquals(Long.valueOf(10L), submissionTermEntry.getSubmissionId());

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSubmissionId(10L);

	List<TermEntry> allTermEntryes = getRegularConnector().getTmgrBrowser().findAll();
	List<TermEntry> aaa = new ArrayList<TermEntry>();
	for (TermEntry te : allTermEntryes) {
	    if (te.getSubmissionId() != null && te.getSubmissionId().equals(submissionId)) {
		aaa.add(te);
	    }
	}

	List<TermEntry> submissionTermEntryes = getRegularConnector().getTmgrBrowser().browse(filter);
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionTermEntryes));
	Assert.assertEquals(1, submissionTermEntryes.size());

	TermEntry submissionTermEntry2 = submissionTermEntryes.get(0);
	Assert.assertEquals(Long.valueOf(submissionId), submissionTermEntry2.getSubmissionId());
    }

    @Test
    public void findBySubmissionIdTest_02() throws TmException {
	long submissionId = 1L;

	List<TermEntry> allTermEntryes = getRegularConnector().getTmgrBrowser().findAll();
	List<TermEntry> allTermEntriesList = new ArrayList<TermEntry>();
	for (TermEntry termEntry : allTermEntryes) {
	    if (termEntry.getSubmissionId() != null && termEntry.getSubmissionId().equals(submissionId)) {
		allTermEntriesList.add(termEntry);
	    }
	}

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSubmissionId(submissionId);
	List<TermEntry> submissionTermEntryes = getRegularConnector().getTmgrBrowser().browse(filter);

	Assert.assertNotNull(submissionTermEntryes);
	Assert.assertEquals(allTermEntriesList.size(), submissionTermEntryes.size());
    }

    @Test
    public void findJobsByProjectIdTest() {
	Long projectId = 1L;

	List<Submission> submissions = getSubmissionService().findSubmissionsByProjectId(projectId);

	Assert.assertNotNull(submissions);
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissions));
	Submission submission = submissions.get(0);
	Assert.assertNotNull(submission);
	Assert.assertNotNull(submission.getDateSubmitted());
	Assert.assertNotNull(submission.getDateModified());
    }

    @Test
    public void testSendToTranslationCase() throws TmException {
	Long projectId = 1L;

	TmProject project = getProjectService().findProjectById(projectId, ProjectDetail.class,
		ProjectLanguageDetail.class);
	Assert.assertNotNull(project);

	ProjectDetail projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);

	Assert.assertEquals(1, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(2, projectDetail.getTermInSubmissionCount());

	Set<ProjectLanguageDetail> languageDetails = projectDetail.getLanguageDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(languageDetails));
	for (ProjectLanguageDetail languageDetail : languageDetails) {
	    Assert.assertEquals(1, languageDetail.getActiveSubmissionCount());
	    Assert.assertEquals(1, languageDetail.getTermInSubmissionCount());
	}

	String submissionName = "SUB000001";
	String submissionMarkerId = UUID.randomUUID().toString();
	String termEntryId = TERM_ENTRY_ID_01;
	String termUuid = UUID.randomUUID().toString();
	String commentUuid = UUID.randomUUID().toString();

	List<TermEntryTranslationUnit> translationUnits = createTranslationUnits(projectId, termEntryId, termUuid,
		commentUuid, submissionMarkerId);

	String deTermUUID = UUID.randomUUID().toString();
	addNewUnit(projectId, TERM_ENTRY_ID_02, "de-DE", translationUnits, deTermUUID);

	boolean reviewIsRequired = true;
	String sourceLanguage = "en-US";
	String sourceTermId = TERM_ID_01;

	List<String> sourceTermIds = new ArrayList<String>();
	sourceTermIds.add(sourceTermId);
	sourceTermIds.add(TERM_ID_04);

	Term submissionTerm = getRegularConnector().getTmgrBrowser().findTermById(termUuid, projectId);
	Assert.assertNull(submissionTerm);

	List<Term> sourceTerms = getTermService().findTermsByIds(sourceTermIds, Arrays.asList(projectId));

	Submission submission = getSubmissionService().createSubmission(projectId, translationUnits, submissionName,
		submissionMarkerId, sourceLanguage, reviewIsRequired, null, sourceTerms);
	Assert.assertNotNull(submission);

	TermEntry termEntry = getRegularConnector().getTmgrBrowser().findById(termEntryId, projectId);
	Assert.assertEquals(Action.SENT_TO_TRANSLATION, termEntry.getAction());

	Long submissionId = submission.getSubmissionId();

	List<Term> submissionTerms = getSubmissionTermService().findSubmissionTermsBySubmissionId(submissionId);
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionTerms));

	submissionTerm = getRegularConnector().getTmgrBrowser().findTermById(sourceTermId, projectId);
	Assert.assertNotNull(submissionTerm);
	Assert.assertTrue(submissionTerm.getInTranslationAsSource());

	// re-load submission
	submission = getSubmissionService().findByIdFetchChilds(submissionId);

	// verify that term entry count is updated
	Assert.assertEquals(2, submission.getTermEntryCount());

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));

	project = getProjectService().findProjectById(projectId, ProjectDetail.class, ProjectLanguageDetail.class);
	Assert.assertNotNull(project);

	projectDetail = project.getProjectDetail();
	Assert.assertNotNull(projectDetail);

	Assert.assertEquals(2, projectDetail.getActiveSubmissionCount());
	Assert.assertEquals(4, projectDetail.getTermInSubmissionCount());

	languageDetails = projectDetail.getLanguageDetails();
	Assert.assertTrue(CollectionUtils.isNotEmpty(languageDetails));
	for (ProjectLanguageDetail languageDetail : languageDetails) {
	    String languageId = languageDetail.getLanguageId();
	    if (languageId.equals("de-DE") || languageId.equals("fr-FR")) {
		Assert.assertEquals(2, languageDetail.getActiveSubmissionCount());
		Assert.assertEquals(2, languageDetail.getTermInSubmissionCount());
	    }
	}
    }

    private void addNewUnit(Long projectId, String termEntryId, String languageId,
	    List<TermEntryTranslationUnit> translationUnits, String deTermUUID) {
	UpdateCommand termCommand = createUpdateCommand(TypeEnum.TERM, CommandEnum.TRANSLATE, languageId, "translator",
		deTermUUID, null, null);
	List<UpdateCommand> commands = new ArrayList<UpdateCommand>();
	commands.add(termCommand);

	TermEntryTranslationUnit unit = new TermEntryTranslationUnit();
	unit.setProjectId(projectId);
	unit.setTermEntryId(termEntryId);
	unit.setUpdateCommands(commands);

	translationUnits.add(unit);
    }

    @SuppressWarnings("unused")
    private void assertDeTemResults(String deTermUuid, List<Term> subTerms) {
	Term deTerm = null;
	for (Term term : subTerms) {
	    if (term.getUuId().equals(deTermUuid)) {
		deTerm = term;
		break;
	    }
	}

	Assert.assertNotNull(deTerm);
	Assert.assertNotNull(deTerm.getTempText());
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), deTerm.getStatus());
    }

    private List<TermEntryTranslationUnit> createTranslationUnits(Long projectId, String termEntryId, String termUuid,
	    String comentUuid, String jobUuid) {
	UpdateCommand termCommand = createUpdateCommand(TypeEnum.TERM, CommandEnum.TRANSLATE, "fr-FR", "translator",
		termUuid, null, null);

	String termCommentText = TERM_COMENT_TEXT;
	UpdateCommand termComentCommand = createUpdateCommand(TypeEnum.COMMENT, CommandEnum.TRANSLATE, null, null,
		comentUuid, termUuid, termCommentText);

	String jobCommentText = SUB_COMENT_TEXT;
	UpdateCommand subComentCommand = createUpdateCommand(TypeEnum.COMMENT, CommandEnum.TRANSLATE, null, null,
		comentUuid, jobUuid, jobCommentText);

	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	updateCommands.add(termCommand);
	updateCommands.add(termComentCommand);
	updateCommands.add(subComentCommand);

	TermEntryTranslationUnit unit = new TermEntryTranslationUnit();
	unit.setProjectId(projectId);
	unit.setTermEntryId(termEntryId);
	unit.setUpdateCommands(updateCommands);

	List<TermEntryTranslationUnit> translationUnits = new ArrayList<TermEntryTranslationUnit>();
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

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }

    private SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }

}

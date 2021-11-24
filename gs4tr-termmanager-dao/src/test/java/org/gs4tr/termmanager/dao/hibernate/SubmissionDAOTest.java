package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.termmanager.dao.SubmissionDAO;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.TmProject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmissionDAOTest extends AbstractSpringDAOIntegrationTest {

    private static final String PM = "pm";

    private static final Long PROJECT_ID_01 = 2L;

    private static final Long PROJECT_ID_02 = 1L;

    private static final String PROJECT_NAME_01 = "translationProject";

    private static final String SHORT_CODE_01 = "TEST002";

    private static final String SOURCE_LANGUAGE_ID = "en-US";

    private static final Long SUBMISSION_ID_01 = 1L;

    private static final Long SUBMISSION_ID_02 = 2L;

    private static final Long SUBMISSION_ID_03 = 3L;

    @Autowired
    private SubmissionDAO _submissionDAO;

    @Test
    public void findAllSubmissionIds() {
	Set<Long> allSubmissionIds = getSubmissionDAO().findAllSubmissionIds();

	assertNotNull(allSubmissionIds);

	assertTrue(CollectionUtils.isNotEmpty(allSubmissionIds));
	assertTrue(allSubmissionIds.contains(SUBMISSION_ID_01));
	assertTrue(allSubmissionIds.contains(SUBMISSION_ID_02));
	assertTrue(allSubmissionIds.contains(SUBMISSION_ID_03));

	assertTrue(allSubmissionIds.size() == 3);
    }

    @Test
    public void findAllSubmissionsTest() {
	List<Submission> subs = getSubmissionDAO().findAllSubmissions();

	assertNotNull(subs);

	assertTrue(CollectionUtils.isNotEmpty(subs));
    }

    @Test
    public void findProjectsBySubmissionIds() {
	List<Long> submissionIds = new ArrayList<Long>();
	submissionIds.add(SUBMISSION_ID_01);

	List<TmProject> tmProjects = getSubmissionDAO().findProjectsBySubmissionIds(submissionIds);

	TmProject tmProject = tmProjects.get(0);

	ProjectInfo projectInfo = tmProject.getProjectInfo();

	assertNotNull(tmProjects);

	assertTrue(CollectionUtils.isNotEmpty(tmProjects));
	assertTrue(tmProject.getProjectId().equals(PROJECT_ID_01));
	assertTrue(tmProjects.size() == 1);

	assertTrue(projectInfo.getShortCode().equals(SHORT_CODE_01));
	assertTrue(projectInfo.getName().equals(PROJECT_NAME_01));

    }

    @Test
    public void findProjectsBySubmissionIdsTest() {
	final List<Long> submissionIds = new ArrayList<Long>();

	submissionIds.add(SUBMISSION_ID_01);
	submissionIds.add(SUBMISSION_ID_02);
	submissionIds.add(SUBMISSION_ID_03);

	List<TmProject> tmProjects = getSubmissionDAO().findProjectsBySubmissionIds(submissionIds);

	assertNotNull(tmProjects);

	assertTrue(CollectionUtils.isNotEmpty(tmProjects));
	assertTrue(tmProjects.size() == 2);

	for (Long submissionId : submissionIds) {
	    Submission submissionById = getSubmissionDAO().findById(submissionId);
	    TmProject tmProject = submissionById.getProject();

	    assertNotNull(tmProject);
	    assertTrue(tmProjects.contains(tmProject));
	}
    }

    @Test
    public void findSubmissionsByProjectIdTest() {
	List<Submission> submissions = getSubmissionDAO().findSubmissionsByProjectId(PROJECT_ID_01);

	assertNotNull(submissions);

	assertTrue(CollectionUtils.isNotEmpty(submissions));
	assertTrue(submissions.size() == 2);

	for (Submission submission : submissions) {
	    TmProject project = submission.getProject();

	    assertTrue(project.getProjectId().equals(PROJECT_ID_01));
	    assertTrue(project.getProjectInfo().getName().equals(PROJECT_NAME_01));
	    assertTrue(submission.getSubmitter().equals(PM));
	    assertTrue(submission.getSourceLanguageId().equals(SOURCE_LANGUAGE_ID));
	}
    }

    @Test
    public void findSubmissionsByProjectIdsTest() {
	final Collection<Long> projectIds = new ArrayList<Long>();

	projectIds.add(PROJECT_ID_01);
	projectIds.add(PROJECT_ID_02);

	List<Submission> submissions = getSubmissionDAO().findSubmissionsByProjectIds(projectIds);

	assertNotNull(submissions);

	List<Submission> submissionsByProjectId_01 = getSubmissionDAO().findSubmissionsByProjectId(PROJECT_ID_01);
	List<Submission> submissionsByProjectId_02 = getSubmissionDAO().findSubmissionsByProjectId(PROJECT_ID_02);

	assertTrue(CollectionUtils.isNotEmpty(submissions));
	assertTrue(submissions.containsAll(submissionsByProjectId_01));
	assertTrue(submissions.containsAll(submissionsByProjectId_02));
	assertTrue(submissions.size() == 3);

    }

    public SubmissionDAO getSubmissionDAO() {
	return _submissionDAO;
    }

    @Test
    public void testFindSubmissionByIdFetchChilds() {
	Long submissionId = 1L;

	Submission submission = getSubmissionDAO().findSubmissionByIdFetchChilds(submissionId);
	Assert.assertNotNull(submission);

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	Assert.assertTrue(CollectionUtils.isNotEmpty(submission.getSubmissionUsers()));

	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    if (submissionLanguage.getSubmissionLanguageId().equals(1L)) {
		Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguage.getSubmissionLanguageComments()));
	    }
	}
    }

    @Test
    public void testFindSubmissionsByIdsFetchChilds() {
	Long submissionId = 1L;

	List<Long> submissionIds = new ArrayList<Long>();
	submissionIds.add(submissionId);

	List<Submission> submissions = getSubmissionDAO().findSubmissionsByIdsFetchChilds(submissionIds);
	Assert.assertNotNull(submissions);
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissions));

	Submission submission = submissions.get(0);
	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	Assert.assertTrue(CollectionUtils.isNotEmpty(submission.getSubmissionUsers()));

	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    if (submissionLanguage.getSubmissionLanguageId().equals(1L)) {
		Assert.assertTrue(CollectionUtils.isNotEmpty(submissionLanguage.getSubmissionLanguageComments()));
	    }
	}
    }

    @Test
    public void updateLanguageByProjectIdTest() {
	final Collection<Long> projectIds = new ArrayList<>();

	projectIds.add(PROJECT_ID_01);
	projectIds.add(PROJECT_ID_02);

	// Assert configuration before
	List<Submission> submissionsBefore = getSubmissionDAO().findSubmissionsByProjectIds(projectIds);

	Submission submissionBefore1 = submissionsBefore.get(0);
	Assert.assertEquals(submissionBefore1.getSourceLanguageId(), "en-US");

	Submission submissionBefore2 = submissionsBefore.get(1);
	Assert.assertEquals(submissionBefore2.getSourceLanguageId(), "en-US");

	Submission submissionBefore3 = submissionsBefore.get(2);
	Assert.assertEquals(submissionBefore3.getSourceLanguageId(), "en-US");

	int updatedNum = getSubmissionDAO().updateLanguageByProjectId("en-US", "en", PROJECT_ID_01);

	Assert.assertEquals(2, updatedNum);

	getSubmissionDAO().flush();
	getSubmissionDAO().clear();

	List<Submission> submissionsAfter = getSubmissionDAO().findSubmissionsByProjectIds(projectIds);

	// Submission source language for project 1 should not be changed
	Submission submissionAfter1 = submissionsAfter.get(0);
	Assert.assertEquals(submissionAfter1.getSourceLanguageId(), "en-US");

	Submission submissionAfter2 = submissionsAfter.get(1);
	Assert.assertEquals(submissionAfter2.getSourceLanguageId(), "en");

	Submission submissionAfter3 = submissionsAfter.get(2);
	Assert.assertEquals(submissionAfter3.getSourceLanguageId(), "en");
    }
}

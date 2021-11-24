package org.gs4tr.termmanager.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.SubmissionDetailViewDAO;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.search.command.SubmissionSearchRequest;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmissionDetailViewDAOTest extends AbstractSpringDAOIntegrationTest {

    @Autowired
    private SubmissionDetailViewDAO _submissionDetailViewDAO;

    @Test
    public void testGetEntityPagedListAsPowerUser() {
	Long submissionId = 1L;
	Set<Long> submissionIds = new HashSet<Long>();
	submissionIds.add(submissionId);

	String assignee1 = "bob_jones";
	String assignee2 = "super";
	List<String> assingees = new ArrayList<String>();
	assingees.add(assignee1);
	assingees.add(assignee2);

	List<String> submitters = new ArrayList<String>();
	submitters.add("pm");

	String sourceLanguageId = "en-US";
	List<String> sourceLanguageIds = new ArrayList<String>();
	sourceLanguageIds.add(sourceLanguageId);

	String frTargetLanguageId = "fr-FR";
	String deTargetLanguageId = "de-DE";
	List<String> targetLanguageIds = new ArrayList<String>();
	targetLanguageIds.add(frTargetLanguageId);
	targetLanguageIds.add(deTargetLanguageId);

	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName());

	SubmissionSearchRequest command = new SubmissionSearchRequest();
	command.setName("First");
	command.setDateSubmittedTo(new Date());
	command.setSubmissionIds(submissionIds);
	command.setAssingees(assingees);
	command.setSubmitter(submitters);
	command.setSourceLanguageIds(sourceLanguageIds);
	command.setTargetLanguageIds(targetLanguageIds);
	command.setStatuses(statuses);
	command.setSubmitterView(true);
	command.setPowerUser(true);
	command.setUserId(1L);
	command.setUserName(assignee1);

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<SubmissionDetailView> pagedList = getSubmissionDetailViewDAO().getEntityPagedList(command,
		pagedListInfo);
	Assert.assertNotNull(pagedList);

	SubmissionDetailView[] elements = pagedList.getElements();
	Assert.assertTrue(ArrayUtils.isNotEmpty(elements));
	Assert.assertEquals(1, elements.length);

	SubmissionDetailView view = elements[0];
	Assert.assertEquals(2, view.getProjectId().longValue());
	String assignees = assignee2 + ";" + assignee1;
	Assert.assertEquals(assignees, view.getAssignee());
	// Power see all languages
	Assert.assertEquals("de-DE;fr-FR", view.getTargetLanguageIds());
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW, view.getStatus());

    }

    @Test
    public void testGetEntityPagedListAssignee() {
	Long submissionId = 1L;
	Set<Long> submissionIds = new HashSet<Long>();
	submissionIds.add(submissionId);

	String assignee = "bob_jones";
	List<String> assingees = new ArrayList<String>();
	assingees.add(assignee);

	List<String> submitters = new ArrayList<String>();
	submitters.add("pm");

	String sourceLanguageId = "en-US";
	List<String> sourceLanguageIds = new ArrayList<String>();
	sourceLanguageIds.add(sourceLanguageId);

	String frTargetLanguageId = "fr-FR";
	String deTargetLanguageId = "de-DE";
	List<String> targetLanguageIds = new ArrayList<String>();
	targetLanguageIds.add(frTargetLanguageId);
	targetLanguageIds.add(deTargetLanguageId);

	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName());

	SubmissionSearchRequest command = new SubmissionSearchRequest();
	command.setName("First");
	command.setDateSubmittedTo(new Date());
	command.setSubmissionIds(submissionIds);
	command.setAssingees(assingees);
	command.setSubmitter(submitters);
	command.setSourceLanguageIds(sourceLanguageIds);
	command.setTargetLanguageIds(targetLanguageIds);
	command.setStatuses(statuses);
	command.setSubmitterView(true);
	command.setUserId(1L);
	command.setUserName(assignee);

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<SubmissionDetailView> pagedList = getSubmissionDetailViewDAO().getEntityPagedList(command,
		pagedListInfo);
	Assert.assertNotNull(pagedList);

	SubmissionDetailView[] elements = pagedList.getElements();
	Assert.assertTrue(ArrayUtils.isNotEmpty(elements));
	Assert.assertEquals(1, elements.length);

	SubmissionDetailView view = elements[0];
	Assert.assertEquals(2, view.getProjectId().longValue());
	String assignees = "super;bob_jones";
	Assert.assertEquals(assignees, view.getAssignee());
	// Assert.assertEquals("fr-FR", view.getTargetLanguageIds());
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW, view.getStatus());
    }

    @Test
    public void testGetEntityPagedListSubmitter() {
	Long submissionId = 1L;
	Set<Long> submissionIds = new HashSet<Long>();
	submissionIds.add(submissionId);

	String assignee = "bob_jones";
	List<String> assingees = new ArrayList<String>();
	assingees.add(assignee);

	List<String> submitters = new ArrayList<String>();
	submitters.add("pm");

	String sourceLanguageId = "en-US";
	List<String> sourceLanguageIds = new ArrayList<String>();
	sourceLanguageIds.add(sourceLanguageId);

	String frTargetLanguageId = "fr-FR";
	String deTargetLanguageId = "de-DE";
	List<String> targetLanguageIds = new ArrayList<String>();
	targetLanguageIds.add(frTargetLanguageId);
	targetLanguageIds.add(deTargetLanguageId);

	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName());

	SubmissionSearchRequest command = new SubmissionSearchRequest();
	command.setName("First");
	command.setDateSubmittedTo(new Date());
	command.setSubmissionIds(submissionIds);
	command.setAssingees(assingees);
	command.setSubmitter(submitters);
	command.setSourceLanguageIds(sourceLanguageIds);
	command.setTargetLanguageIds(targetLanguageIds);
	command.setStatuses(statuses);
	command.setSubmitterView(true);
	command.setPowerUser(false);
	command.setUserId(1L);
	command.setUserName(assignee);

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<SubmissionDetailView> pagedList = getSubmissionDetailViewDAO().getEntityPagedList(command,
		pagedListInfo);
	Assert.assertNotNull(pagedList);

	SubmissionDetailView[] elements = pagedList.getElements();
	Assert.assertTrue(ArrayUtils.isNotEmpty(elements));
	Assert.assertEquals(1, elements.length);

	SubmissionDetailView view = elements[0];
	Assert.assertEquals(2, view.getProjectId().longValue());
	String assignees = "super;bob_jones";
	Assert.assertEquals(assignees, view.getAssignee());
	// Assert.assertEquals("fr-FR", view.getTargetLanguageIds());
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW, view.getStatus());
    }

    @Test
    public void testGetEntityPagedListSuperUserAsAssignee() {
	Long submissionId = 1L;
	Set<Long> submissionIds = new HashSet<Long>();
	submissionIds.add(submissionId);

	String assignee1 = "bob_jones";
	String assignee2 = "super";
	List<String> assingees = new ArrayList<String>();
	assingees.add(assignee1);
	assingees.add(assignee2);

	List<String> submitters = new ArrayList<String>();
	submitters.add("pm");

	String sourceLanguageId = "en-US";
	List<String> sourceLanguageIds = new ArrayList<String>();
	sourceLanguageIds.add(sourceLanguageId);

	String frTargetLanguageId = "fr-FR";
	String deTargetLanguageId = "de-DE";
	List<String> targetLanguageIds = new ArrayList<String>();
	targetLanguageIds.add(frTargetLanguageId);
	targetLanguageIds.add(deTargetLanguageId);

	List<String> statuses = new ArrayList<String>();
	statuses.add(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName());

	SubmissionSearchRequest command = new SubmissionSearchRequest();
	command.setName("First");
	command.setDateSubmittedTo(new Date());
	command.setSubmissionIds(submissionIds);
	command.setAssingees(assingees);
	command.setSubmitter(submitters);
	command.setSourceLanguageIds(sourceLanguageIds);
	command.setTargetLanguageIds(targetLanguageIds);
	command.setStatuses(statuses);
	command.setSubmitterView(true);
	command.setPowerUser(false);
	command.setUserId(1L);
	command.setUserName(assignee2);

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<SubmissionDetailView> pagedList = getSubmissionDetailViewDAO().getEntityPagedList(command,
		pagedListInfo);
	Assert.assertNotNull(pagedList);

	SubmissionDetailView[] elements = pagedList.getElements();
	Assert.assertTrue(ArrayUtils.isNotEmpty(elements));
	Assert.assertEquals(1, elements.length);

	SubmissionDetailView view = elements[0];
	Assert.assertEquals(2, view.getProjectId().longValue());
	String assignees = assignee2 + ";" + assignee1;
	Assert.assertEquals(assignees, view.getAssignee());
	// Only see one language that is assignee to it
	// Assert.assertEquals("de-DE", view.getTargetLanguageIds());
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW, view.getStatus());
    }

    private SubmissionDetailViewDAO getSubmissionDetailViewDAO() {
	return _submissionDetailViewDAO;
    }
}

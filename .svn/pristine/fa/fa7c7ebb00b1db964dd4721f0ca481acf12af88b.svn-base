package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.dao.SubmissionLanguageDAO;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import junit.framework.Assert;

public class SubmissionLanguageDAOTest extends AbstractSpringDAOIntegrationTest {

    private static final Long ID_01 = 1L;

    private static final Long ID_02 = 2L;

    @Autowired
    private SubmissionLanguageDAO _submissionLanguageDAO;

    @Test
    public void findByIdTest() {
	SubmissionLanguage submissionLanguage = getSubmissionLanguageDAO().findById(ID_01);

	verifyNotNull(submissionLanguage);

	/* I want to check is this a valid response */
	verifyFields(submissionLanguage);
    }

    @Test
    public void findSubmissionLanguagesBySubmissionIdSortByLanguageTest() {
	List<SubmissionLanguage> submissionLanguages = getSubmissionLanguageDAO()
		.findSubmissionLanguagesBySubmissionId(ID_01);

	assertNotNull(submissionLanguages);

	List<String> actual = submissionLanguages.stream().map(sl -> sl.getLanguageId())
		.collect(Collectors.toCollection(LinkedList::new));

	List<String> expected = submissionLanguages.stream().map(sl -> sl.getLanguageId()).sorted()
		.collect(Collectors.toCollection(LinkedList::new));

	Assert.assertEquals(expected, actual);
    }

    @Test
    public void findSubmissionLanguagesBySubmissionIdTest() {
	List<SubmissionLanguage> submissionLanguages = getSubmissionLanguageDAO()
		.findSubmissionLanguagesBySubmissionId(ID_01);

	SubmissionLanguage subLang1 = getSubmissionLanguageDAO().findById(ID_01);
	SubmissionLanguage subLang2 = getSubmissionLanguageDAO().findById(ID_02);

	assertNotNull(submissionLanguages);
	assertNotNull(subLang1);
	assertNotNull(subLang2);

	assertTrue(CollectionUtils.isNotEmpty(submissionLanguages));
	assertTrue(submissionLanguages.contains(subLang1));
	assertTrue(submissionLanguages.contains(subLang2));
	assertTrue(submissionLanguages.size() == 2);
    }

    public SubmissionLanguageDAO getSubmissionLanguageDAO() {
	return _submissionLanguageDAO;
    }

    @Test
    public void updateLanguageByProjectIdTest() {

	// Assert setup before update action
	SubmissionLanguage subLangBefore1 = getSubmissionLanguageDAO().findById(ID_01);
	SubmissionLanguage subLangBefore2 = getSubmissionLanguageDAO().findById(ID_02);

	assertNotNull(subLangBefore1);
	assertNotNull(subLangBefore2);

	assertEquals("fr-FR", subLangBefore1.getLanguageId());
	assertEquals("de-DE", subLangBefore2.getLanguageId());

	// Perform update language action
	getSubmissionLanguageDAO().updateLanguageByProjectId("de-DE", "de", 2L);

	getSubmissionLanguageDAO().flush();
	getSubmissionLanguageDAO().clear();

	SubmissionLanguage subLangAfter1 = getSubmissionLanguageDAO().findById(ID_01);
	SubmissionLanguage subLangAfter2 = getSubmissionLanguageDAO().findById(ID_02);

	assertNotNull(subLangAfter1);
	assertNotNull(subLangAfter2);

	// Should not be changed
	assertEquals("fr-FR", subLangAfter1.getLanguageId());

	// Language de-DE should be updated to de
	assertEquals("de", subLangAfter2.getLanguageId());

    }

    private void verifyFields(SubmissionLanguage subLang) {
	assertEquals(1, subLang.getTermCount());
	assertEquals(0, subLang.getTermCanceledCount());
	assertEquals(0, subLang.getTermCompletedCount());
	assertEquals(0, subLang.getTermInFinalReviewCount());
	assertEquals(1, subLang.getTermInTranslationCount());
	assertEquals(2, subLang.getEntityStatusPriority().getPriority());
	assertEquals(2, subLang.getEntityStatusPriority().getPriorityAssignee());

	assertEquals("fr-FR", subLang.getLanguageId());
	assertEquals("bob_jones", subLang.getAssignee());
	assertEquals("INTRANSLATIONREVIEW", subLang.getEntityStatusPriority().getStatus().getName());
    }

    private void verifyNotNull(SubmissionLanguage subLang) {
	assertNotNull(subLang);
	assertNotNull(subLang.getSubmission());
	assertNotNull(subLang.getDateModified());
	assertNotNull(subLang.getDateSubmitted());
    }
}

package org.gs4tr.termmanager.dao.hibernate;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.SubmissionLanguageDetailViewDAO;
import org.gs4tr.termmanager.model.dto.DtoSubmissionLanguageComment;
import org.gs4tr.termmanager.model.dto.DtoSubmissionLanguageDetailView;
import org.gs4tr.termmanager.model.dto.converter.SubmissionLanguageDetailViewConverter;
import org.gs4tr.termmanager.model.search.command.SubmissionLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmissionLanguageDetailViewDAOTest extends AbstractSpringDAOIntegrationTest {

    @Autowired
    private SubmissionLanguageDetailViewDAO _submissionLanguageDetailViewDAO;

    @Test
    public void testGetEntityPagedList() {
	Long submissionId = 1L;

	String deLang = "de-DE";

	Set<String> languageIds = new HashSet<>();
	languageIds.add(deLang);

	SubmissionLanguageDetailRequest command = new SubmissionLanguageDetailRequest();
	command.setSubmissionId(submissionId);
	command.setLanguageIds(languageIds);

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<SubmissionLanguageDetailView> pagedList = getSubmissionLanguageDetailViewDAO()
		.getEntityPagedList(command, pagedListInfo);
	Assert.assertNotNull(pagedList);

	SubmissionLanguageDetailView[] elements = pagedList.getElements();
	Assert.assertTrue(ArrayUtils.isNotEmpty(elements));
	Assert.assertEquals(1, elements.length);

	SubmissionLanguageDetailView view = elements[0];
	Assert.assertEquals(deLang, view.getLanguageId());
    }

    @Test
    public void testGetEntityPagedListFr() {
	Long submissionId = 1L;

	String frLang = "fr-FR";

	Set<String> languageIds = new HashSet<>();
	languageIds.add(frLang);

	SubmissionLanguageDetailRequest command = new SubmissionLanguageDetailRequest();
	command.setSubmissionId(submissionId);
	command.setLanguageIds(languageIds);

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<SubmissionLanguageDetailView> pagedList = getSubmissionLanguageDetailViewDAO()
		.getEntityPagedList(command, pagedListInfo);
	Assert.assertNotNull(pagedList);

	SubmissionLanguageDetailView[] elements = pagedList.getElements();
	Assert.assertTrue(ArrayUtils.isNotEmpty(elements));
	Assert.assertEquals(1, elements.length);

	SubmissionLanguageDetailView view = elements[0];
	Assert.assertEquals(frLang, view.getLanguageId());

	DtoSubmissionLanguageDetailView dtoView = SubmissionLanguageDetailViewConverter.fromInternalToDto(view);
	DtoSubmissionLanguageComment[] comments = dtoView.getComments();
	Assert.assertTrue(ArrayUtils.isNotEmpty(comments));
    }

    private SubmissionLanguageDetailViewDAO getSubmissionLanguageDetailViewDAO() {
	return _submissionLanguageDetailViewDAO;
    }
}

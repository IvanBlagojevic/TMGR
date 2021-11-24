package org.gs4tr.termmanager.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.dto.DtoSubmissionLanguageComment;
import org.gs4tr.termmanager.model.dto.DtoSubmissionLanguageDetailView;
import org.gs4tr.termmanager.model.dto.converter.SubmissionLanguageDetailViewConverter;
import org.gs4tr.termmanager.model.search.command.SubmissionLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SubmissionLanguageDetailViewServiceTest extends AbstractSpringServiceTests {

    @Autowired
    private SubmissionLanguageDetailViewService _submissionLanguageDetailViewService;

    @Test
    public void testSearchSubmissionLanguageDetails() {
	Long submissionId = 1L;

	String frLang = "fr-FR";

	Set<String> languageIds = new HashSet<String>();
	languageIds.add(frLang);

	SubmissionLanguageDetailRequest command = new SubmissionLanguageDetailRequest();
	command.setSubmissionId(submissionId);
	command.setLanguageIds(languageIds);

	PagedListInfo pagedListInfo = new PagedListInfo();

	TaskPagedList<SubmissionLanguageDetailView> pagedList = (TaskPagedList<SubmissionLanguageDetailView>) getSubmissionLanguageDetailViewService()
		.search(command, pagedListInfo);
	Assert.assertNotNull(pagedList);

	SubmissionLanguageDetailView[] elements = pagedList.getElements();
	Assert.assertTrue(ArrayUtils.isNotEmpty(elements));
	Assert.assertEquals(2, elements.length);

	SubmissionLanguageDetailView view = elements[0];
	Assert.assertEquals(frLang, view.getLanguageId());

	DtoSubmissionLanguageDetailView dtoView = SubmissionLanguageDetailViewConverter.fromInternalToDto(view);
	DtoSubmissionLanguageComment[] comments = dtoView.getComments();
	Assert.assertTrue(ArrayUtils.isNotEmpty(comments));
    }

    private SubmissionLanguageDetailViewService getSubmissionLanguageDetailViewService() {
	return _submissionLanguageDetailViewService;
    }
}

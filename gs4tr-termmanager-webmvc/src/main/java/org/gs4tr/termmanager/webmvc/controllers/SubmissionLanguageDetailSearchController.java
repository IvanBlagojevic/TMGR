package org.gs4tr.termmanager.webmvc.controllers;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.dto.DtoSubmissionLanguageDetailView;
import org.gs4tr.termmanager.model.dto.converter.SubmissionLanguageDetailViewConverter;
import org.gs4tr.termmanager.model.search.ItemPaneEnum;
import org.gs4tr.termmanager.model.search.command.SubmissionLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView;
import org.gs4tr.termmanager.service.SubmissionLanguageDetailViewService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = UrlConstants.SUBMISSION_LANGUAGE_DETAIL_SEARCH, method = RequestMethod.POST)
public class SubmissionLanguageDetailSearchController extends
	AbstractSearchGridController<SubmissionLanguageDetailView, DtoSubmissionLanguageDetailView, SubmissionLanguageDetailRequest, SearchCommand> {

    @Autowired
    private SubmissionLanguageDetailViewService _submissionLanguageDetailViewService;

    protected SubmissionLanguageDetailSearchController() {
	super(EntityTypeHolder.SUBMISSIONLANGUAGEDETAIL, SubmissionLanguageDetailView.class,
		DtoSubmissionLanguageDetailView.class);
    }

    private SubmissionLanguageDetailViewService getSubmissionLanguageDetailViewService() {
	return _submissionLanguageDetailViewService;
    }

    @Override
    protected DtoSubmissionLanguageDetailView createDtoEntityFromEntity(SubmissionLanguageDetailView entity) {
	return SubmissionLanguageDetailViewConverter.fromInternalToDto(entity);
    }

    @Override
    protected String createGridConfigKey(SearchCommand searchGridCommand) {
	return ItemPaneEnum.SUBMISSIONLANGUAGEDETAILS.name().toLowerCase();
    }

    @Override
    protected SubmissionLanguageDetailRequest createSearchRequestFromSearchCommand(SearchCommand command) {
	String ticket = command.getTicket();

	Long submissionId = StringUtils.isNotEmpty(ticket) ? IdEncrypter.decryptGenericId(ticket) : null;

	SubmissionLanguageDetailRequest request = new SubmissionLanguageDetailRequest();
	request.setSubmissionId(submissionId);

	return request;
    }

    @Override
    protected TaskPagedList<SubmissionLanguageDetailView> search(SubmissionLanguageDetailRequest searchRequest,
	    SearchCommand command, PagedListInfo pagedListInfo) {
	TaskPagedList<SubmissionLanguageDetailView> result = (TaskPagedList<SubmissionLanguageDetailView>) getSubmissionLanguageDetailViewService()
		.search(searchRequest, pagedListInfo);
	return result;
    }

    @Override
    protected void setCommandFields(SearchCommand command) {

    }
}

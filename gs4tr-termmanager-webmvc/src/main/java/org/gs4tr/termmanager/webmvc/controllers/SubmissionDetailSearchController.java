package org.gs4tr.termmanager.webmvc.controllers;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.dto.DtoSubmissionDetailView;
import org.gs4tr.termmanager.model.dto.converter.SubmissionDetailViewConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.search.command.SubmissionSearchRequest;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;
import org.gs4tr.termmanager.service.SubmissionDetailViewService;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.commands.DateRange;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.gs4tr.termmanager.webmvc.model.search.DoubleMultiComboBoxDefaultValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = UrlConstants.SUBMISSION_DETAIL_SEARCH, method = RequestMethod.POST)
public class SubmissionDetailSearchController extends
	AbstractSearchGridController<SubmissionDetailView, DtoSubmissionDetailView, SubmissionSearchRequest, SearchCommand> {

    @Autowired
    private SubmissionDetailViewService _submissionDetailViewService;

    protected SubmissionDetailSearchController() {
	super(EntityTypeHolder.SUBMISSIONDETAIL, SubmissionDetailView.class, DtoSubmissionDetailView.class);
    }

    private SubmissionDetailViewService getSubmissionDetailViewService() {
	return _submissionDetailViewService;
    }

    @Override
    protected DtoSubmissionDetailView createDtoEntityFromEntity(SubmissionDetailView entity) {
	return SubmissionDetailViewConverter.fromInternalToDto(entity);
    }

    @Override
    protected String createGridConfigKey(SearchCommand searchGridCommand) {
	return ItemFolderEnum.SUBMISSIONDETAILS.name().toLowerCase();
    }

    @Override
    protected SubmissionSearchRequest createSearchRequestFromSearchCommand(SearchCommand command) {
	SubmissionSearchRequest request = new SubmissionSearchRequest();
	request.setFolder(ItemFolderEnum.SUBMISSIONDETAILS);
	request.setName(command.getSubmissionNameInputText());
	request.setStatuses(command.getStatuses());

	List<String> projectTickets = command.getSubmissionProjectComboBox();
	if (CollectionUtils.isNotEmpty(projectTickets)) {
	    List<Long> projectIds = TicketConverter.fromDtoToInternal(projectTickets);
	    request.setProjectIds(projectIds);
	}

	DoubleMultiComboBoxDefaultValue languageDirection = command.getLanguageDirectionSubmission();
	if (languageDirection != null) {
	    List<String> sourceLanguages = languageDirection.getValue1();
	    if (CollectionUtils.isNotEmpty(sourceLanguages)) {
		request.setSourceLanguageIds(sourceLanguages);
	    }

	    List<String> targetLanguages = languageDirection.getValue2();
	    if (CollectionUtils.isNotEmpty(targetLanguages)) {
		request.setTargetLanguageIds(targetLanguages);
	    }
	}

	DoubleMultiComboBoxDefaultValue submissionUsers = command.getSubmissionUsers();
	if (submissionUsers != null) {
	    List<String> submitters = submissionUsers.getValue1();
	    if (submitters != null) {
		request.setSubmitter(submitters);
	    }
	    List<String> assignees = submissionUsers.getValue2();
	    if (assignees != null) {
		request.setAssingees(assignees);
	    }
	}

	DateRange dateCreatedRange = command.getDateCreatedRange();
	if (dateCreatedRange != null) {
	    if (dateCreatedRange.getFromDate() != null) {
		request.setDateSubmittedFrom(new Date(dateCreatedRange.getFromDate()));
	    }

	    if (dateCreatedRange.getToDate() != null) {
		request.setDateSubmittedTo(new Date(dateCreatedRange.getToDate()));
	    }
	}

	DateRange dateModifiedRange = command.getDateModifiedRange();
	if (dateModifiedRange != null) {
	    if (dateModifiedRange.getFromDate() != null) {
		request.setDateModifiedFrom(new Date(dateModifiedRange.getFromDate()));
	    }

	    if (dateModifiedRange.getToDate() != null) {
		request.setDateModifiedTo(new Date(dateModifiedRange.getToDate()));
	    }
	}

	DateRange dateCompletedRange = command.getDateCompletedRange();
	if (dateCompletedRange != null) {
	    if (dateCompletedRange.getFromDate() != null) {
		request.setDateCompletedFrom(new Date(dateCompletedRange.getFromDate()));
	    }

	    if (dateCompletedRange.getToDate() != null) {
		request.setDateCompletedTo(new Date(dateCompletedRange.getToDate()));
	    }
	}

	return request;
    }

    @Override
    protected TaskPagedList<SubmissionDetailView> search(SubmissionSearchRequest searchRequest, SearchCommand command,
	    PagedListInfo pagedListInfo) {
	TaskPagedList<SubmissionDetailView> result = (TaskPagedList<SubmissionDetailView>) getSubmissionDetailViewService()
		.search(searchRequest, pagedListInfo);
	return result;
    }

    @Override
    protected void setCommandFields(SearchCommand command) {

    }
}

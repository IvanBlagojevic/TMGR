package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.termmanager.model.dto.converter.TermEntryTranslationUnitConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.SendToTranslationCommand;

public class DtoSendToTranslationCommand extends DtoBaseDashboardCommand
	implements DtoTaskHandlerCommand<SendToTranslationCommand> {

    private String _projectTicket;

    private boolean _reviewRequired = false;

    private String _sourceLanguage;

    private String[] _sourceTermTickets;

    private String _submissionMarkerId;

    private String _submissionName;

    private String _submissionTicket;

    private String[] _termEntryTickets;

    @Override
    public SendToTranslationCommand convertToInternalTaskHandlerCommand() {
	SendToTranslationCommand command = new SendToTranslationCommand();
	command.setTermEntryTranslationUnits(
		TermEntryTranslationUnitConverter.fromDtoToInternal(getTranslationUnits()));
	command.setSubmissionName(getSubmissionName());
	command.setSubmissionMarkerId(getSubmissionMarkerId());
	command.setReviewRequired(isReviewRequired());
	command.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));
	command.setSubmissionId(TicketConverter.fromDtoToInternal(getSubmissionTicket(), Long.class));

	// getTaskInfos parameters
	command.setSourceLanguage(getSourceLanguage());
	String[] termEntryTickets = getTermEntryTickets();
	if (ArrayUtils.isNotEmpty(termEntryTickets)) {
	    Set<String> termEntryIds = new HashSet<String>();
	    for (String termEntryTicket : termEntryTickets) {
		termEntryIds.add(termEntryTicket);
	    }
	    command.setTermEntryIds(termEntryIds);
	}
	String[] sourceTermTickets = getSourceTermTickets();
	if (ArrayUtils.isNotEmpty(sourceTermTickets)) {
	    List<String> sourceTermIds = new ArrayList<String>();
	    for (String sourceTermTicket : sourceTermTickets) {
		sourceTermIds.add(sourceTermTicket);
	    }
	    command.setSourceTermIds(sourceTermIds);
	}

	return command;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public String[] getSourceTermTickets() {
	return _sourceTermTickets;
    }

    public String getSubmissionMarkerId() {
	return _submissionMarkerId;
    }

    public String getSubmissionName() {
	return _submissionName;
    }

    public String getSubmissionTicket() {
	return _submissionTicket;
    }

    public String[] getTermEntryTickets() {
	return _termEntryTickets;
    }

    public boolean isReviewRequired() {
	return _reviewRequired;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setReviewRequired(boolean reviewRequired) {
	_reviewRequired = reviewRequired;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setSourceTermTickets(String[] sourceTermTickets) {
	_sourceTermTickets = sourceTermTickets;
    }

    public void setSubmissionMarkerId(String submissionMarkerId) {
	_submissionMarkerId = submissionMarkerId;
    }

    public void setSubmissionName(String submissionName) {
	_submissionName = submissionName;
    }

    public void setSubmissionTicket(String submissionTicket) {
	_submissionTicket = submissionTicket;
    }

    public void setTermEntryTickets(String[] termEntryTickets) {
	_termEntryTickets = termEntryTickets;
    }
}

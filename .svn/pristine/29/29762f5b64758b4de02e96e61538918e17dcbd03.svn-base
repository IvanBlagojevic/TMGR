package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.ApproveMergeCommand;

public class DtoApproveMergeCommand implements DtoTaskHandlerCommand<ApproveMergeCommand> {

    private String _matchedTermEntryTicket;

    private String _projectTicket;

    private String _sourceLanguage;

    private String[] _termIds;

    @Override
    public ApproveMergeCommand convertToInternalTaskHandlerCommand() {
	ApproveMergeCommand command = new ApproveMergeCommand();
	command.setMatchedTermEntryId(getMatchedTermEntryTicket());
	command.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));
	command.setSourceLanguage(getSourceLanguage());

	List<String> termIds = new ArrayList<String>();
	if (ArrayUtils.isNotEmpty(getTermIds())) {
	    termIds.addAll(Arrays.asList(getTermIds()));
	}
	command.setTermIds(termIds);

	return command;
    }

    public String getMatchedTermEntryTicket() {
	return _matchedTermEntryTicket;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public String[] getTermIds() {
	return _termIds;
    }

    public void setMatchedTermEntryTicket(String matchedTermEntryTicket) {
	_matchedTermEntryTicket = matchedTermEntryTicket;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setTermIds(String[] termIds) {
	_termIds = termIds;
    }

}

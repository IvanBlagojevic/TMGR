package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.LookupTermCommand;

public class DtoLookupTermCommand implements DtoTaskHandlerCommand<LookupTermCommand> {

    private String _languageId;

    private String _projectTicket;

    private String[] _termEntryTickets;

    private String[] _terms;

    @Override
    public LookupTermCommand convertToInternalTaskHandlerCommand() {
	LookupTermCommand cmd = new LookupTermCommand();
	cmd.setLanguageId(getLanguageId());
	cmd.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));

	String[] terms = getTerms();
	if (ArrayUtils.isNotEmpty(terms)) {
	    Set<String> termSet = new HashSet<>();
	    for (String term : terms) {
		if (StringUtils.isNotBlank(term)) {
		    termSet.add(term);
		}
	    }

	    cmd.setTerms(termSet);
	}

	String[] termEntryTickets = getTermEntryTickets();
	if (termEntryTickets != null && termEntryTickets.length > 0) {
	    cmd.setTermEntryIds(Arrays.asList(termEntryTickets));
	}

	return cmd;
    }

    public String getLanguageId() {
	return _languageId;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String[] getTermEntryTickets() {
	return _termEntryTickets;
    }

    public String[] getTerms() {
	return _terms;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setTermEntryTickets(String[] termEntryTickets) {
	_termEntryTickets = termEntryTickets;
    }

    public void setTerms(String[] terms) {
	_terms = terms;
    }

}

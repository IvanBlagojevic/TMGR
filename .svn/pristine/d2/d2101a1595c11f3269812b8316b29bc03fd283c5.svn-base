package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.TermCommandPerProject;

public class DtoTermCommandPerProject implements DtoTaskHandlerCommand<TermCommandPerProject> {

    private String _projectTicket;

    private String[] _termEntryTickets;

    private String[] _termTickets;

    @Override
    public TermCommandPerProject convertToInternalTaskHandlerCommand() {
	TermCommandPerProject termCommandPerProject = new TermCommandPerProject();

	Long projectId = TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class);

	termCommandPerProject.setProjectId(projectId);

	termCommandPerProject.setTermEntryIds(new ArrayList<>());
	for (int i = 0; i < getTermEntryTickets().length; i++) {
	    if (StringUtils.isNotEmpty(getTermEntryTickets()[i])) {
		termCommandPerProject.getTermEntryIds().add(getTermEntryTickets()[i]);
	    }
	}

	termCommandPerProject.setTermIds(new ArrayList<>());
	for (int i = 0; i < getTermTickets().length; i++) {
	    if (StringUtils.isNotEmpty(getTermTickets()[i])) {
		termCommandPerProject.getTermIds().add(getTermTickets()[i]);
	    }
	}

	return termCommandPerProject;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String[] getTermEntryTickets() {
	return _termEntryTickets;
    }

    public String[] getTermTickets() {
	return _termTickets;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setTermEntryTickets(String[] termEntryTickets) {
	_termEntryTickets = termEntryTickets;
    }

    public void setTermTickets(String[] termTickets) {
	_termTickets = termTickets;
    }
}

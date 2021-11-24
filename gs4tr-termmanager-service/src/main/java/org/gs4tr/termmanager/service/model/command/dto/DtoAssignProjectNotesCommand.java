package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.ProjectNote;
import org.gs4tr.termmanager.model.dto.converter.ProjectNoteConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.AssignProjectNotesCommand;

public class DtoAssignProjectNotesCommand implements DtoTaskHandlerCommand<AssignProjectNotesCommand> {

    private ProjectNote[] _projectNotes;

    private Ticket _projectTicket;

    @Override
    public AssignProjectNotesCommand convertToInternalTaskHandlerCommand() {
	AssignProjectNotesCommand AssignProjectNotesCommand = new AssignProjectNotesCommand();

	AssignProjectNotesCommand.setProjectNotes(ProjectNoteConverter.fromDtoToInternal(getProjectNotes()));
	AssignProjectNotesCommand.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));

	return AssignProjectNotesCommand;
    }

    public ProjectNote[] getProjectNotes() {
	return _projectNotes;
    }

    public Ticket getProjectTicket() {
	return _projectTicket;
    }

    public void setProjectNotes(ProjectNote[] projectNotes) {
	_projectNotes = projectNotes;
    }

    public void setProjectTicket(Ticket projectTicket) {
	_projectTicket = projectTicket;
    }

}

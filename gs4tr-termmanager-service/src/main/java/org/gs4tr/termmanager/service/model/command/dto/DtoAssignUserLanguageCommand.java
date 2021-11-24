package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.Language;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.AssignUserLanguageCommand;

public class DtoAssignUserLanguageCommand implements DtoTaskHandlerCommand<AssignUserLanguageCommand> {

    private Language[] _userLanguages;

    private Ticket _userTicket;

    @Override
    public AssignUserLanguageCommand convertToInternalTaskHandlerCommand() {
	AssignUserLanguageCommand assignUserLanguageCommand = new AssignUserLanguageCommand();

	assignUserLanguageCommand.setUserLanguages(LanguageConverter.fromDtoToLanguageCode(getUserLanguages()));

	assignUserLanguageCommand.setUserId(TicketConverter.fromDtoToInternal(getUserTicket(), Long.class));

	return assignUserLanguageCommand;
    }

    public Language[] getUserLanguages() {
	return _userLanguages;
    }

    public Ticket getUserTicket() {
	return _userTicket;
    }

    public void setUserLanguages(Language[] userLanguages) {
	_userLanguages = userLanguages;
    }

    public void setUserTicket(Ticket userTicket) {
	_userTicket = userTicket;
    }

}

package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;
import java.util.HashSet;

import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.LanguageSetCommand;

public class DtoLanguageSetCommand implements DtoTaskHandlerCommand<LanguageSetCommand> {

    private String _languageSetName;

    private String[] _languageCodes;

    private String _ticket;

    public LanguageSetCommand convertToInternalTaskHandlerCommand() {

	LanguageSetCommand userCommand = new LanguageSetCommand();

	userCommand.setLanguageCodes(new HashSet<String>(Arrays.asList(getLanguageCodes())));

	userCommand.setLanguageSetName(getLanguageSetName());

	userCommand.setLanguageSetId(TicketConverter.fromDtoToInternal(getTicket(), Long.class));

	return userCommand;
    }

    public String[] getLanguageCodes() {
	return _languageCodes;
    }

    public String getLanguageSetName() {
	return _languageSetName;
    }

    public String getTicket() {
	return _ticket;
    }

    public void setLanguageCodes(String[] languageCodes) {
	_languageCodes = languageCodes;
    }

    public void setLanguageSetName(String languageSetName) {
	_languageSetName = languageSetName;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }

}

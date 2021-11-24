package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.GenerateImportTemplateCommand;

public class DtoGenerateImportTemplateCommand implements DtoTaskHandlerCommand<GenerateImportTemplateCommand> {

    private String[] _languages;

    private Ticket _projectTicket;

    @Override
    public GenerateImportTemplateCommand convertToInternalTaskHandlerCommand() {
	GenerateImportTemplateCommand command = new GenerateImportTemplateCommand();
	command.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));
	if (ArrayUtils.isNotEmpty(getLanguages())) {
	    command.setLanguages(Arrays.asList(getLanguages()));
	}
	return command;
    }

    public String[] getLanguages() {
	return _languages;
    }

    public Ticket getProjectTicket() {
	return _projectTicket;
    }

    public void setLanguages(String[] languages) {
	_languages = languages;
    }

    public void setProjectTicket(Ticket projectTicket) {
	_projectTicket = projectTicket;
    }
}

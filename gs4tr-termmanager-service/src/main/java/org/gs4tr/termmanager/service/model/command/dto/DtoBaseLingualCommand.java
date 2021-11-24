package org.gs4tr.termmanager.service.model.command.dto;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.service.model.command.BaseLingualCommand;
import org.gs4tr.termmanager.service.model.command.TranslationViewCommand;

public class DtoBaseLingualCommand implements DtoTaskHandlerCommand<BaseLingualCommand> {

    private String _sourceLocale;

    private String _sourceTicket;

    private DtoTranslationViewCommand[] _targetTerms;

    private String _termEntryTicket;

    @Override
    public BaseLingualCommand convertToInternalTaskHandlerCommand() {
	BaseLingualCommand command = new BaseLingualCommand();
	command.setSourceId(getSourceTicket());
	command.setTermEntryId(getTermEntryTicket());
	command.setSourceLocale(getSourceLocale());

	DtoTranslationViewCommand[] targetTerms = getTargetTerms();
	if (targetTerms != null) {
	    List<TranslationViewCommand> targetTermsList = new ArrayList<>();
	    for (DtoTranslationViewCommand dto : targetTerms) {
		TranslationViewCommand translationViewCommand = new TranslationViewCommand();
		translationViewCommand.setTargetId(dto.getTargetTicket());
		translationViewCommand.setTargetLocale(dto.getTargetLocale());

		targetTermsList.add(translationViewCommand);
	    }
	    command.setTargetTerms(targetTermsList);
	}
	return command;
    }

    public String getSourceLocale() {
	return _sourceLocale;
    }

    public String getSourceTicket() {
	return _sourceTicket;
    }

    public DtoTranslationViewCommand[] getTargetTerms() {
	return _targetTerms;
    }

    public String getTermEntryTicket() {
	return _termEntryTicket;
    }

    public void setSourceLocale(String sourceLocale) {
	_sourceLocale = sourceLocale;
    }

    public void setSourceTicket(String sourceTicket) {
	_sourceTicket = sourceTicket;
    }

    public void setTargetTerms(DtoTranslationViewCommand[] targetTerms) {
	_targetTerms = targetTerms;
    }

    public void setTermEntryTicket(String termEntryTicket) {
	_termEntryTicket = termEntryTicket;
    }
}

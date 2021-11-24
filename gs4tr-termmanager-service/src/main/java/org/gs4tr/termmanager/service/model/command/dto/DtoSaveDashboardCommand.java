package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;

import org.gs4tr.termmanager.model.dto.TranslationUnit;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.dto.converter.TranslationUnitConverter;
import org.gs4tr.termmanager.service.model.command.SaveDashboardCommand;

public class DtoSaveDashboardCommand implements DtoTaskHandlerCommand<SaveDashboardCommand> {

    private String _projectTicket;

    private boolean _saveTerm = false;

    private String _sourceLanguage;

    private String[] _targetLanguage;

    private TranslationUnit[] _translationUnits;

    @Override
    public SaveDashboardCommand convertToInternalTaskHandlerCommand() {
	SaveDashboardCommand command = new SaveDashboardCommand();
	command.setTranslationUnits(TranslationUnitConverter.fromDtoToInternal(getTranslationUnits()));
	command.setProjectId(TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class));
	command.setSourceLanguage(getSourceLanguage());
	String[] targetLanguage = getTargetLanguage();
	if (targetLanguage != null) {
	    command.setTargetLanguage(Arrays.asList(targetLanguage));
	}
	command.setSaveTerm(isSaveTerm());

	return command;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public String[] getTargetLanguage() {
	return _targetLanguage;
    }

    public TranslationUnit[] getTranslationUnits() {
	return _translationUnits;
    }

    public boolean isSaveTerm() {
	return _saveTerm;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setSAveTerm(boolean saveTerm) {
	_saveTerm = saveTerm;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setTargetLanguage(String[] targetLanguage) {
	_targetLanguage = targetLanguage;
    }

    public void setTranslationUnits(TranslationUnit[] translationUnits) {
	_translationUnits = translationUnits;
    }

}

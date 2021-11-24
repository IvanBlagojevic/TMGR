package org.gs4tr.termmanager.service.model.command.dto;

public class DtoTranslationViewCommand {
    private String _targetLocale;

    private String _targetTicket;

    public String getTargetLocale() {
	return _targetLocale;
    }

    public String getTargetTicket() {
	return _targetTicket;
    }

    public void setTargetLocale(String targetLocale) {
	_targetLocale = targetLocale;
    }

    public void setTargetTicket(String targetTicket) {
	_targetTicket = targetTicket;
    }
}

package org.gs4tr.termmanager.model.dto;

public class TermEntryTranslationUnit {

    private String _projectTicket;

    private String _termEntryTicket;

    private UpdateCommand[] _updateCommands;

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String getTermEntryTicket() {
	return _termEntryTicket;
    }

    public UpdateCommand[] getUpdateCommands() {
	return _updateCommands;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setTermEntryTicket(String termEntryTicket) {
	_termEntryTicket = termEntryTicket;
    }

    public void setUpdateCommands(UpdateCommand[] updateCommands) {
	_updateCommands = updateCommands;
    }
}

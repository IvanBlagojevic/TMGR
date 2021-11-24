package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;

import org.gs4tr.termmanager.service.model.command.TermEntryHistoryCommand;

public class DtoTermEntryHistoryCommand implements DtoTaskHandlerCommand<TermEntryHistoryCommand> {

    private String _termEntryTicket;
    private String[] _gridLanguages;
    private boolean _showAllLanguages;

    @Override
    public TermEntryHistoryCommand convertToInternalTaskHandlerCommand() {
	TermEntryHistoryCommand cmd = new TermEntryHistoryCommand();
	cmd.setTermEntryId(getTermEntryTicket());
	cmd.setGridLanguages(Arrays.asList(getGridLanguages()));
	cmd.setShowAllLanguages(showAllLanguages());
	return cmd;
    }

    public String[] getGridLanguages() {
	return _gridLanguages;
    }

    public String getTermEntryTicket() {
	return _termEntryTicket;
    }

    public void setGridLanguages(String[] gridLanguages) {
	_gridLanguages = gridLanguages;
    }

    public void setShowAllLanguages(boolean showAllLanguages) {
	_showAllLanguages = showAllLanguages;
    }

    public void setTermEntryTicket(String termEntryTicket) {
	_termEntryTicket = termEntryTicket;
    }

    public boolean showAllLanguages() {
	return _showAllLanguages;
    }
}

package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;

import org.gs4tr.termmanager.service.model.command.TermEntryPreviewCommand;

public class DtoTermEntryPreviewCommand implements DtoTaskHandlerCommand<TermEntryPreviewCommand> {

    private String _termEntryTicket;
    private String[] _gridLanguages;
    private boolean _showAllLanguages;

    @Override
    public TermEntryPreviewCommand convertToInternalTaskHandlerCommand() {
	TermEntryPreviewCommand previewCommand = new TermEntryPreviewCommand();
	previewCommand.setTermEntryId(getTermEntryTicket());
	previewCommand.setGridLanguages(Arrays.asList(getGridLanguages()));
	previewCommand.setShowAllLanguages(showAllLanguages());
	return previewCommand;
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

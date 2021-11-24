package org.gs4tr.termmanager.webservice.model.request;

public class RecodeOrCloneTermsCommand extends BaseCommand {

    private String _cloneCommands;

    private String _recodeCommands;

    public String getCloneCommands() {
	return _cloneCommands;
    }

    public String getRecodeCommands() {
	return _recodeCommands;
    }

    public void setCloneCommands(String cloneCommands) {
	_cloneCommands = cloneCommands;
    }

    public void setRecodeCommands(String recodeCommands) {
	_recodeCommands = recodeCommands;
    }
}

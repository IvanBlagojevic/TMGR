package org.gs4tr.termmanager.model.dto;

public class TranslationUnit {

    private String _matchedTermEntryTicket;

    private UpdateCommand[] _sourceTermUpdateCommands;

    private UpdateCommand[] _targetTermUpdateCommands;

    private String _termEntryTicket;

    public String getMatchedTermEntryTicket() {
	return _matchedTermEntryTicket;
    }

    public UpdateCommand[] getSourceTermUpdateCommands() {
	return _sourceTermUpdateCommands;
    }

    public UpdateCommand[] getTargetTermUpdateCommands() {
	return _targetTermUpdateCommands;
    }

    public String getTermEntryTicket() {
	return _termEntryTicket;
    }

    public void setMatchedTermEntryTicket(String matchedTermEntryTicket) {
	_matchedTermEntryTicket = matchedTermEntryTicket;
    }

    public void setSourceTermUpdateCommands(UpdateCommand[] sourceTermUpdateCommands) {
	_sourceTermUpdateCommands = sourceTermUpdateCommands;
    }

    public void setTargetTermUpdateCommands(UpdateCommand[] targetTermUpdateCommands) {
	_targetTermUpdateCommands = targetTermUpdateCommands;
    }

    public void setTermEntryTicket(String termEntryTicket) {
	_termEntryTicket = termEntryTicket;
    }

}

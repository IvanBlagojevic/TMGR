package org.gs4tr.termmanager.model;

import java.util.ArrayList;
import java.util.List;

public class TranslationUnit {

    private String _matchedTermEntryId;

    private List<UpdateCommand> _sourceTermUpdateCommands;

    private List<UpdateCommand> _targetTermUpdateCommands;

    private String _termEntryId;

    private String _username;

    public void addSourceTermUpdateCommand(UpdateCommand command) {
	if (_sourceTermUpdateCommands == null) {
	    _sourceTermUpdateCommands = new ArrayList<>();
	}

	_sourceTermUpdateCommands.add(command);
    }

    public void addTargetTermUpdateCommand(UpdateCommand command) {
	if (_targetTermUpdateCommands == null) {
	    _targetTermUpdateCommands = new ArrayList<>();
	}

	_targetTermUpdateCommands.add(command);
    }

    public String getMatchedTermEntryId() {
	return _matchedTermEntryId;
    }

    public List<UpdateCommand> getSourceTermUpdateCommands() {
	return _sourceTermUpdateCommands;
    }

    public List<UpdateCommand> getTargetTermUpdateCommands() {
	return _targetTermUpdateCommands;
    }

    public String getTermEntryId() {
	return _termEntryId;
    }

    public String getUsername() {
	return _username;
    }

    public void setMatchedTermEntryId(String matchedTermEntryId) {
	_matchedTermEntryId = matchedTermEntryId;
    }

    public void setSourceTermUpdateCommands(List<UpdateCommand> sourceTermUpdateCommands) {
	_sourceTermUpdateCommands = sourceTermUpdateCommands;
    }

    public void setTargetTermUpdateCommands(List<UpdateCommand> targetTermUpdateCommands) {
	_targetTermUpdateCommands = targetTermUpdateCommands;
    }

    public void setTermEntryId(String termEntryId) {
	_termEntryId = termEntryId;
    }

    public void setUsername(String username) {
	_username = username;
    }
}

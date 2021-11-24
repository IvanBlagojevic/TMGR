package org.gs4tr.termmanager.model;

import java.util.List;

public class TermEntryTranslationUnit {

    private Long _projectId;

    private String _termEntryId;

    private List<UpdateCommand> _updateCommands;

    public Long getProjectId() {
	return _projectId;
    }

    public String getTermEntryId() {
	return _termEntryId;
    }

    public List<UpdateCommand> getUpdateCommands() {
	return _updateCommands;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setTermEntryId(String termEntryId) {
	_termEntryId = termEntryId;
    }

    public void setUpdateCommands(List<UpdateCommand> updateCommands) {
	_updateCommands = updateCommands;
    }
}

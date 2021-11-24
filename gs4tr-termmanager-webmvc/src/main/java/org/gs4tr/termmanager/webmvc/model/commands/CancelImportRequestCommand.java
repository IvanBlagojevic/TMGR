package org.gs4tr.termmanager.webmvc.model.commands;

import java.util.Set;

import org.gs4tr.termmanager.service.model.command.JsonCommand;

public class CancelImportRequestCommand extends JsonCommand {

    private Set<String> _threadNames;

    public Set<String> getThreadNames() {
	return _threadNames;
    }

    public void setThreadNames(Set<String> threadNames) {
	_threadNames = threadNames;
    }
}

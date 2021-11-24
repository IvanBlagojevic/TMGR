package org.gs4tr.termmanager.webmvc.model.commands;

import org.gs4tr.termmanager.service.model.command.JsonCommand;

public class DashboardCommand extends JsonCommand {

    private String[] _projectTickets;

    public String[] getProjectTickets() {
	return _projectTickets;
    }

    public void setProjectTickets(String[] projectTickets) {
	_projectTickets = projectTickets;
    }
}

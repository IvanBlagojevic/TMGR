package org.gs4tr.termmanager.webmvc.model.commands;

public class DeleteResourceCommand {

    private String _projectTicket;

    private String[] _resourceIds;

    private String _termEntryTicket;

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String[] getResourceIds() {
	return _resourceIds;
    }

    public String getTermEntryTicket() {
	return _termEntryTicket;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setResourceIds(String[] resourceIds) {
	_resourceIds = resourceIds;
    }

    public void setTermEntryTicket(String termEntryTicket) {
	_termEntryTicket = termEntryTicket;
    }
}
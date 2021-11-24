package org.gs4tr.termmanager.service.model.command;

import java.util.List;
import java.util.Map;

public class MatchedTermGroup {

    private List<Map<String, Object>> _matchedList;

    private String _projectName;

    private String _projectTicket;

    public List<Map<String, Object>> getMatchedList() {
	return _matchedList;
    }

    public String getProjectName() {
	return _projectName;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public void setMatchedList(List<Map<String, Object>> matchedList) {
	_matchedList = matchedList;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }
}

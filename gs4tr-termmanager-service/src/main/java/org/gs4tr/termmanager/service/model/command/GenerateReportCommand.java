package org.gs4tr.termmanager.service.model.command;

public class GenerateReportCommand {

    private boolean _groupByLanguage = false;

    public boolean isGroupByLanguage() {
	return _groupByLanguage;
    }

    public void setGroupByLanguage(boolean groupByLanguage) {
	_groupByLanguage = groupByLanguage;
    }
}

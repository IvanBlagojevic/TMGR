package org.gs4tr.termmanager.service.model.command;

import org.gs4tr.termmanager.service.utils.JsonUtils;

public class JsonCommand {

    private String _jsonData;

    public String getJsonData() {
	return _jsonData;
    }

    public void setJsonData(String jsonData) {
	_jsonData = jsonData;

	JsonUtils.updateExistingCommand(this, jsonData);
    }
}
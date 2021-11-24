package org.gs4tr.termmanager.service.model.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gs4tr.termmanager.model.UpdateCommand;

public class AssignTermEntryAttributesCommand {

    private List<UpdateCommand> _updateCommands;

    private Map<String, String> _attributeTypeFileNameMap = new HashMap<String, String>();

    private Map<String, String> _resourceTicketForEditingMap = new HashMap<String, String>();

    private List<String> _resourceIdsForRemoval;

    private String _termEntryId;

    public Map<String, String> getAttributeTypeFileNameMap() {
	return _attributeTypeFileNameMap;
    }

    public List<String> getResourceIdsForRemoval() {
	return _resourceIdsForRemoval;
    }

    public Map<String, String> getResourceTicketForEditingMap() {
	return _resourceTicketForEditingMap;
    }

    public String getTermEntryId() {
	return _termEntryId;
    }

    public List<UpdateCommand> getUpdateCommands() {
	return _updateCommands;
    }

    public void setAttributeTypeFileNameMap(Map<String, String> attributeTypeFileNameMap) {
	_attributeTypeFileNameMap = attributeTypeFileNameMap;
    }

    public void setResourceIdsForRemoval(List<String> resourceIdsForRemoval) {
	_resourceIdsForRemoval = resourceIdsForRemoval;
    }

    public void setResourceTicketForEditingMap(Map<String, String> resourceTicketForEditingMap) {
	_resourceTicketForEditingMap = resourceTicketForEditingMap;
    }

    public void setTermEntryId(String termEntryId) {
	_termEntryId = termEntryId;
    }

    public void setUpdateCommands(List<UpdateCommand> updateCommands) {
	_updateCommands = updateCommands;
    }

}

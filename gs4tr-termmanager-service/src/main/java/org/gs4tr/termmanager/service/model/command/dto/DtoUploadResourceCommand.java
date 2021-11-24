package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Map;

import org.gs4tr.termmanager.service.model.command.UploadResourceCommand;

public class DtoUploadResourceCommand implements DtoTaskHandlerCommand<UploadResourceCommand> {

    private DtoAttributeTypeFileCommand[] _attributeTypeFileCommands;

    private String _termEntryId;

    @Override
    public UploadResourceCommand convertToInternalTaskHandlerCommand() {
	UploadResourceCommand command = new UploadResourceCommand();

	command.setTermEntryId(getTermEntryId());
	Map<String, String> attributeTypeFileNameMap = command.getAttributeTypeFileNameMap();
	for (DtoAttributeTypeFileCommand attributeTypeFile : getAttributeTypeFileCommands()) {
	    attributeTypeFileNameMap.put(attributeTypeFile.getName(), attributeTypeFile.getAttributeType());

	}

	return command;
    }

    public DtoAttributeTypeFileCommand[] getAttributeTypeFileCommands() {
	return _attributeTypeFileCommands;
    }

    public String getTermEntryId() {
	return _termEntryId;
    }

    public void setAttributeTypeFileCommands(DtoAttributeTypeFileCommand[] attributeTypeFileCommands) {
	_attributeTypeFileCommands = attributeTypeFileCommands;
    }

    public void setTermEntryId(String termEntryId) {
	_termEntryId = termEntryId;
    }

}

package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.service.model.command.CheckAnalysisProgressCommand;

public class DtoCheckAnalysisProgressCommand implements DtoTaskHandlerCommand<CheckAnalysisProgressCommand> {

    private String _processingId;

    @Override
    public CheckAnalysisProgressCommand convertToInternalTaskHandlerCommand() {
	CheckAnalysisProgressCommand command = new CheckAnalysisProgressCommand();
	command.setProcessingId(getProcessingId());
	return command;
    }

    public String getProcessingId() {
	return _processingId;
    }

    public void setProcessingId(String processingId) {
	_processingId = processingId;
    }
}

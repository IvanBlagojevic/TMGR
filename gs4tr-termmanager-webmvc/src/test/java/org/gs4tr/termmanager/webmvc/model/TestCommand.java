package org.gs4tr.termmanager.webmvc.model;

import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;

public class TestCommand implements DtoTaskHandlerCommand<Object> {

    @Override
    public Object convertToInternalTaskHandlerCommand() {
	return new Object();
    }
}

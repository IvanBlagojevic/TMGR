package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.gs4tr.termmanager.service.model.command.CheckImportProgressCommand;

public class DtoCheckImportProgressCommand implements DtoTaskHandlerCommand<CheckImportProgressCommand> {

    private String[] _importThreadNames;

    @Override
    public CheckImportProgressCommand convertToInternalTaskHandlerCommand() {
	CheckImportProgressCommand cmd = new CheckImportProgressCommand();
	cmd.setImportThreadNames(Arrays.stream(getImportThreadNames()).collect(Collectors.toSet()));
	return cmd;
    }

    public String[] getImportThreadNames() {
	return _importThreadNames;
    }

    public void setImportThreadNames(String[] importThreadNames) {
	_importThreadNames = importThreadNames;
    }
}

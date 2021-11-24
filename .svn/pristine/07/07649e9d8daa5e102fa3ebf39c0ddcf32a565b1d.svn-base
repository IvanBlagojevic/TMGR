package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.termmanager.service.model.command.FileManagerCommand;

public class DtoFileManagerCommand implements DtoTaskHandlerCommand<FileManagerCommand> {

    private String _action;

    private String[] _fileNames;

    private String _folder;

    @Override
    public FileManagerCommand convertToInternalTaskHandlerCommand() {
	FileManagerCommand command = new FileManagerCommand();
	command.setAction(getAction());
	
	String[] fileNames = getFileNames();
	if (ArrayUtils.isNotEmpty(fileNames)) {
	    command.setFileNames(Arrays.asList(fileNames));
	}
	
	command.setFolder(getFolder());

	return command;
    }

    public String getAction() {
	return _action;
    }

    public String[] getFileNames() {
	return _fileNames;
    }

    public String getFolder() {
	return _folder;
    }

    public void setAction(String action) {
	_action = action;
    }

    public void setFileNames(String[] fileNames) {
	_fileNames = fileNames;
    }

    public void setFolder(String folder) {
	_folder = folder;
    }
}

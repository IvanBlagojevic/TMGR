package org.gs4tr.termmanager.service.model.command;

import java.util.List;

public class FileManagerCommand {

    public enum Action {

	ADD("add"), REMOVE("remove");

	private String _actionName;

	private Action(String actionName) {
	    _actionName = actionName;
	}

	public String getActionName() {
	    return _actionName;
	}
    }

    private String _action;

    private List<String> _fileNames;

    private String _folder;

    public String getAction() {
	return _action;
    }

    public List<String> getFileNames() {
	return _fileNames;
    }

    public String getFolder() {
	return _folder;
    }

    public void setAction(String action) {
	_action = action;
    }

    public void setFileNames(List<String> fileNames) {
	_fileNames = fileNames;
    }

    public void setFolder(String folder) {
	_folder = folder;
    }
}

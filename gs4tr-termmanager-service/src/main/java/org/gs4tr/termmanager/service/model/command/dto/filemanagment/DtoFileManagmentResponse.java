package org.gs4tr.termmanager.service.model.command.dto.filemanagment;

import java.util.List;

public class DtoFileManagmentResponse {

    private List<DtoFile> _files;

    private String _folder;

    public List<DtoFile> getFiles() {
	return _files;
    }

    public String getFolder() {
	return _folder;
    }

    public void setFiles(List<DtoFile> files) {
	_files = files;
    }

    public void setFolder(String folder) {
	_folder = folder;
    }
}

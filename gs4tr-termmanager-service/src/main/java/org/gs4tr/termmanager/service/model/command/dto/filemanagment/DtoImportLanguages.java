package org.gs4tr.termmanager.service.model.command.dto.filemanagment;

public class DtoImportLanguages {

    private String _fileName;

    private String[] _importLanguages;

    public String getFileName() {
	return _fileName;
    }

    public String[] getImportLanguages() {
	return _importLanguages;
    }

    public void setFileName(String fileName) {
	_fileName = fileName;
    }

    public void setImportLanguages(String[] importLanguages) {
	_importLanguages = importLanguages;
    }
}

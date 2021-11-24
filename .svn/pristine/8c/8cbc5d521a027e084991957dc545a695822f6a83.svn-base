package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.service.model.command.DownloadExportedDocumentCommand;

public class DtoDownloadExportedDocumentCommand implements DtoTaskHandlerCommand<DownloadExportedDocumentCommand> {

    private String _threadName;

    private String _xslName;

    @Override
    public DownloadExportedDocumentCommand convertToInternalTaskHandlerCommand() {
	DownloadExportedDocumentCommand downloadExportedDocumentCommand = new DownloadExportedDocumentCommand();
	downloadExportedDocumentCommand.setThreadName(getThreadName());
	downloadExportedDocumentCommand.setXslName(getXslName());

	return downloadExportedDocumentCommand;
    }

    public String getThreadName() {
	return _threadName;
    }

    public String getXslName() {
	return _xslName;
    }

    public void setThreadName(String threadName) {
	_threadName = threadName;
    }

    public void setXslName(String xslName) {
	_xslName = xslName;
    }
}

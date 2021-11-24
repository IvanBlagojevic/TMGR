package org.gs4tr.termmanager.service.model.command;

public class DownloadExportedDocumentCommand {
    private String _threadName;
    private String _xslName;

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

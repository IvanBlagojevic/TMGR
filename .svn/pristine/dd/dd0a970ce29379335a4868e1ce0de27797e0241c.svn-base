package org.gs4tr.termmanager.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.callback.AbstractValidationCallback;
import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.xml.sax.SAXParseException;

public class ImportValidationCallback extends AbstractValidationCallback {

    private static final Log LOG = LogFactory.getLog(ImportValidationCallback.class);

    private final List<Alert> _alerts;

    private String _fileName;

    public ImportValidationCallback(int notificationPerTerms) {
	super(notificationPerTerms);
	_alerts = new ArrayList<>();
    }

    public void addAlert(AlertSubject subject, AlertType type, String message) {
	Alert alert = new Alert(subject, type, message);
	if (!getAlerts().contains(alert)) {
	    getAlerts().add(alert);
	}
    }

    public List<Alert> getAlerts() {
	return _alerts;
    }

    public String getFileName() {
	return _fileName;
    }

    @Override
    public void notifyError(Exception e) {
	// Since TMGR 5.0, do nothing
    }

    @Override
    public void notifyFatalError(Exception ex) {
	LogHelper.error(LOG, ex.getMessage(), ex);

	if (ex instanceof SAXParseException) {
	    SAXParseException e = (SAXParseException) ex;
	    String format = MessageResolver.getMessage("ImportValidationCallback.0"); //$NON-NLS-1$
	    String message = String.format(format, e.getLineNumber(), e.getColumnNumber(), e.getMessage());
	    addAlert(AlertSubject.FILE_PARSE_ERROR, AlertType.ERROR, message);
	} else if (ex instanceof RuntimeException) {
	    RuntimeException e = (RuntimeException) ex;
	    addAlert(AlertSubject.FILE_PARSE_ERROR, AlertType.ERROR, e.getMessage());
	}
    }

    @Override
    public void notifyTermsValidated(int ex) {
	// Do nothing
    }

    @Override
    public void notifyWarning(Exception ex) {
	// Since TMGR 5.0, do nothing
    }

    public void setFileName(String fileName) {
	_fileName = fileName;
    }
}

package org.gs4tr.termmanager.service;

import java.io.IOException;
import java.util.List;

import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.impl.ExportNotificationCallback;
import org.springframework.security.access.annotation.Secured;

public interface TermEntryExporter {

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Boolean exportInternal(List<TermEntry> termEntries, ExportNotificationCallback exportTbxNotificationCallback,
	    ExportDocumentFactory exportDocumentFactory, ExportInfo exportInfo) throws IOException;

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void requestStopExport(String threadName);
}

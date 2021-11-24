package org.gs4tr.termmanager.service;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.persistence.update.AnalyzeImportInfo;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.springframework.security.access.annotation.Secured;

public interface ImportTermService {

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void cancelImportRequest(Set<String> threadNames);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    ImportSummary importDocument(InputStream stream, ImportProgressInfo importProgressInfo,
	    ImportOptionsModel importOptions, ImportTypeEnum importType, String fileEncoding, SyncOption syncOption)
	    throws Exception;

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    ImportSummary importDocumentWS(ImportOptionsModel importOptions, InputStream stream, Long startTime,
	    ImportTypeEnum importType, SyncOption syncOption) throws Exception;

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    boolean isLocked(long projectId);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    void updateProjectDetailOnImport(ImportOptionsModel importOptions, ImportSummary importSummary);

    @Secured("POLICY_FOUNDATION_SECURITY_GETLOGINDATA")
    AnalyzeImportInfo validateImportingDocument(InputStream inputStream, List<String> projectUserLanguages,
	    ImportTypeEnum importType, String fileEncoding) throws Exception;
}

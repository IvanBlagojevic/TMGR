package org.gs4tr.termmanager.webservice.model.response;

import org.gs4tr.termmanager.model.dto.ImportSummary;

public class ImportGlossaryResponse extends BaseResponse {

    ImportSummary _importSummary;

    public ImportGlossaryResponse(int returnCode, boolean success, ImportSummary importSummary) {
	setReturnCode(returnCode);
	setSuccess(success);
	_importSummary = importSummary;
    }

    public ImportGlossaryResponse(int returnCode, boolean success) {
	setReturnCode(returnCode);
	setSuccess(success);
    }

    public ImportGlossaryResponse() {
    }

    public ImportSummary getImportSummary() {
	return _importSummary;
    }

    public void setImportSummary(ImportSummary importSummary) {
	_importSummary = importSummary;
    }
}

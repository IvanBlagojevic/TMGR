package org.gs4tr.termmanager.webservice.model.response;

import io.swagger.annotations.ApiModelProperty;

public class BatchSegmentSearchResponse extends BaseResponse {

    private BatchSegmentTermHit _termHit;

    public BatchSegmentSearchResponse(int returnCode, boolean success) {
	setReturnCode(returnCode);
	setSuccess(success);
    }

    public BatchSegmentSearchResponse() {
    }

    @ApiModelProperty(value = "Object that holds segment term hits, for each segment.")
    public BatchSegmentTermHit getTermHit() {
	return _termHit;
    }

    public void setTermHit(BatchSegmentTermHit termHit) {
	_termHit = termHit;
    }
}

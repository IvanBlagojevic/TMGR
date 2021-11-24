package org.gs4tr.termmanager.webservice.model.response;

import java.util.Collection;

import io.swagger.annotations.ApiModelProperty;

public class SegmentSearchResponse extends BaseResponse {

    private Collection<SegmentTermHit> _termHits;

    public SegmentSearchResponse(int returnCode, boolean success, Collection<SegmentTermHit> termHits) {
	setReturnCode(returnCode);
	setSuccess(success);
	_termHits = termHits;
    }

    public SegmentSearchResponse(int returnCode, boolean success) {
	setReturnCode(returnCode);
	setSuccess(success);
	setReturnCode(returnCode);
    }

    public SegmentSearchResponse() {
    }

    @ApiModelProperty(value = "Collection of term matches with their translations.")
    public Collection<SegmentTermHit> getTermHits() {
	return _termHits;
    }

    public void setTermHits(Collection<SegmentTermHit> termHits) {
	_termHits = termHits;
    }

}

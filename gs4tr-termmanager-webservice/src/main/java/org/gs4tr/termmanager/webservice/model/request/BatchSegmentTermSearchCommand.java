package org.gs4tr.termmanager.webservice.model.request;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class BatchSegmentTermSearchCommand extends TermSearchCommand {

    private List<String> _batchSegment;

    @ApiModelProperty(value = "Collection of the segments that will be searched.", required = true)
    public List<String> getBatchSegment() {
	return _batchSegment;
    }

    public void setBatchSegment(List<String> batchSegment) {
	_batchSegment = batchSegment;
    }

}

package org.gs4tr.termmanager.webservice.model.response;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by emisia on 5/17/17.
 */
public class BatchSegmentTermHit {

    private String _segment;

    private List<SegmentTermHit> _segmentTermHits;

    public void addSegmentTermHit(SegmentTermHit segmentTermHit) {
	_segmentTermHits.add(segmentTermHit);
    }

    @ApiModelProperty(value = "A single segment from the batch.")
    public String getSegment() {
	return _segment;
    }

    @ApiModelProperty(value = "Collection of terms matches with their translations for specified segment.")
    public List<SegmentTermHit> getSegmentTermHits() {
	return _segmentTermHits;
    }

    public void setSegment(String segment) {
	_segment = segment;
    }

    public void setSegmentTermHits(List<SegmentTermHit> segmentTermHits) {
	_segmentTermHits = segmentTermHits;
    }
}

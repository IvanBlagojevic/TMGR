package org.gs4tr.termmanager.webservice.model.response;

import io.swagger.annotations.ApiModelProperty;

public class SegmentHitRange {

    private boolean _fuzzy;

    private int _length;

    private int _start;

    public SegmentHitRange(int start, int length) {
	_start = start;
	_length = length;
    }

    @ApiModelProperty(value = "Length of highlighted match.")
    public int getLength() {
	return _length;
    }

    @ApiModelProperty(value = "Beginning of highlighted match, starting from first segment index.")
    public int getStart() {
	return _start;
    }

    @ApiModelProperty(value = "Determines if match is fuzzy.")
    public boolean isFuzzy() {
	return _fuzzy;
    }

    public void setFuzzy(boolean fuzzy) {
	_fuzzy = fuzzy;
    }

    public void setLength(int length) {
	_length = length;
    }

    public void setStart(int start) {
	_start = start;
    }

}

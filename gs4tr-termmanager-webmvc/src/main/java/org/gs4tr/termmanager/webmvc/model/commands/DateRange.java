package org.gs4tr.termmanager.webmvc.model.commands;

public class DateRange {

    private Long _fromDate;

    private Long _toDate;

    public DateRange() {
    }

    public Long getFromDate() {
	return _fromDate;
    }

    public Long getToDate() {
	return _toDate;
    }

    public void setFromDate(Long fromDate) {
	_fromDate = fromDate;
    }

    public void setToDate(Long toDate) {
	_toDate = toDate;
    }

}

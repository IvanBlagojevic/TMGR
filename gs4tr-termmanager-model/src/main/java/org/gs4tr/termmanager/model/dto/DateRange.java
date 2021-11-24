package org.gs4tr.termmanager.model.dto;

import java.io.Serializable;

public class DateRange implements Serializable {

    private static final long serialVersionUID = -2123607116077553810L;

    private Long _fromDate;
    private Long _toDate;

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

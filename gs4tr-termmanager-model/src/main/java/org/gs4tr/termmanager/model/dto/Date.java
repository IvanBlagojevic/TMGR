package org.gs4tr.termmanager.model.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("date")
public class Date {

    @XStreamAlias("date")
    private long _date;

    public Date() {
    }

    public Date(long date) {
	_date = date;
    }

    public long getDate() {
	return _date;
    }

    public void setDate(long date) {
	_date = date;
    }

}

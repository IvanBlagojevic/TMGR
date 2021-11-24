package org.gs4tr.termmanager.model.dto;

public class ExportConfiguration {

    private long _afterTimeStamp;

    private int _format;

    public long getAfterTimeStamp() {
	return _afterTimeStamp;
    }

    public int getFormat() {
	return _format;
    }

    public void setAfterTimeStamp(long afterTimeStamp) {
	_afterTimeStamp = afterTimeStamp;
    }

    public void setFormat(int format) {
	_format = format;
    }
}

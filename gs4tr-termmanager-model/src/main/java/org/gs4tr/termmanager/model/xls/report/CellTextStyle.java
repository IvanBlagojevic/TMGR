package org.gs4tr.termmanager.model.xls.report;

import java.io.Serializable;

public class CellTextStyle implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1873320045645775369L;

    private ReportColor _color;

    private int _length;

    private int _startIndex;

    private boolean _strikeout = false;

    public CellTextStyle() {

    }

    public CellTextStyle(ReportColor color, int startIndex, int length, boolean strikeout) {
	_color = color;
	_startIndex = startIndex;
	_length = length;
	_strikeout = strikeout;
    }

    public ReportColor getColor() {
	return _color;
    }

    public int getLength() {
	return _length;
    }

    public int getStartIndex() {
	return _startIndex;
    }

    public boolean isStrikeout() {
	return _strikeout;
    }

    public void setColor(ReportColor color) {
	_color = color;
    }

    public void setLength(int length) {
	_length = length;
    }

    public void setStartIndex(int startIndex) {
	_startIndex = startIndex;
    }
}

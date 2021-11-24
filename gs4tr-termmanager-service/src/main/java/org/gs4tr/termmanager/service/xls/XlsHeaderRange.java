package org.gs4tr.termmanager.service.xls;

public class XlsHeaderRange {

    private int _end;

    private String _key;

    private int _start;

    private String _value;

    public XlsHeaderRange() {

    }

    public XlsHeaderRange(int start, int end) {
	_start = start;
	_end = end;
    }

    public XlsHeaderRange(int start, int end, String key, String value) {
	_start = start;
	_end = end;
	_key = key;
	_value = value;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	XlsHeaderRange other = (XlsHeaderRange) obj;
	if (_key == null) {
	    if (other._key != null)
		return false;
	} else if (!_key.equals(other._key))
	    return false;
	if (_value == null) {
	    if (other._value != null)
		return false;
	} else if (!_value.equals(other._value))
	    return false;
	return true;
    }

    public int getEnd() {
	return _end;
    }

    public String getKey() {
	return _key;
    }

    public int getStart() {
	return _start;
    }

    public String getValue() {
	return _value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_key == null) ? 0 : _key.hashCode());
	result = prime * result + ((_value == null) ? 0 : _value.hashCode());
	return result;
    }

    public void setEnd(int end) {
	_end = end;
    }

    public void setKey(String key) {
	_key = key;
    }

    public void setStart(int start) {
	_start = start;
    }

    public void setValue(String value) {
	_value = value;
    }
}

package org.gs4tr.termmanager.model.dto;

/**
 * DTO class which represents one difference.
 * 
 * @author TMGR_Backend
 * 
 */
public class Difference {

    private final String _operation;
    private final int _length;
    private final int _startIndex;

    public Difference(int startIndex, int length, String operation) {
	_startIndex = startIndex;
	_length = length;
	_operation = operation;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Difference other = (Difference) obj;
	if (_length != other._length)
	    return false;
	if (_operation == null) {
	    if (other._operation != null)
		return false;
	} else if (!_operation.equals(other._operation))
	    return false;
	if (_startIndex != other._startIndex)
	    return false;
	return true;
    }

    public int getLength() {
        return _length;
    }

    public String getOperation() {
        return _operation;
    }

    public int getStartIndex() {
        return _startIndex;
    }
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + _length;
	result = prime * result + ((_operation == null) ? 0 : _operation.hashCode());
	result = prime * result + _startIndex;
	return result;
    }

    @Override
    public String toString() {
	return "Difference [_operation=" + _operation + ", _length=" + _length + ", _startIndex=" + _startIndex + "]";
    }
}

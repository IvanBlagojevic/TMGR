package org.gs4tr.termmanager.model.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO class which represents differences between the two {@code Description}.
 * 
 * @author TMGR_Backend
 * 
 */
public class DescriptionDifferences {

    private final List<Difference> _differences;
    private final String _name;
    private final String _value;

    public DescriptionDifferences(String name, String value) {
	_name = name;
	_value = value;
	_differences = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DescriptionDifferences other = (DescriptionDifferences) obj;
	if (_differences == null) {
	    if (other._differences != null)
		return false;
	} else if (!_differences.equals(other._differences))
	    return false;
	if (_name == null) {
	    if (other._name != null)
		return false;
	} else if (!_name.equals(other._name))
	    return false;
	if (_value == null) {
	    if (other._value != null)
		return false;
	} else if (!_value.equals(other._value))
	    return false;
	return true;
    }

    public List<Difference> getDifferences() {
	return _differences;
    }

    public String getName() {
	return _name;
    }

    public String getValue() {
	return _value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_differences == null) ? 0 : _differences.hashCode());
	result = prime * result + ((_name == null) ? 0 : _name.hashCode());
	result = prime * result + ((_value == null) ? 0 : _value.hashCode());
	return result;
    }

    @Override
    public String toString() {
	return "DescriptionDifferences [_differences=" + _differences + ", _name=" + _name + ", _value=" + _value + "]";
    }
}

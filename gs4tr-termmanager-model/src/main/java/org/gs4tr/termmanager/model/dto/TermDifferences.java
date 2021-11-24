package org.gs4tr.termmanager.model.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO class which represents differences between the two {@code Term}.
 * 
 * @author TMGR_Backend
 * 
 */
public class TermDifferences {

    private final List<DescriptionDifferences> _attributesDifferences;
    private final List<Difference> _differences;
    private final String _newStatus;
    private final List<DescriptionDifferences> _notesDifferences;
    private final String _oldStatus;
    private final String _value;

    public TermDifferences(String oldStatus, String newStatus, String value) {
	_differences = new ArrayList<Difference>();
	_oldStatus = oldStatus;
	_newStatus = newStatus;
	_notesDifferences = new ArrayList<>();
	_attributesDifferences = new ArrayList<>();
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
	TermDifferences other = (TermDifferences) obj;
	if (_attributesDifferences == null) {
	    if (other._attributesDifferences != null)
		return false;
	} else if (!_attributesDifferences.equals(other._attributesDifferences))
	    return false;
	if (_differences == null) {
	    if (other._differences != null)
		return false;
	} else if (!_differences.equals(other._differences))
	    return false;
	if (_newStatus == null) {
	    if (other._newStatus != null)
		return false;
	} else if (!_newStatus.equals(other._newStatus))
	    return false;
	if (_notesDifferences == null) {
	    if (other._notesDifferences != null)
		return false;
	} else if (!_notesDifferences.equals(other._notesDifferences))
	    return false;
	if (_oldStatus == null) {
	    if (other._oldStatus != null)
		return false;
	} else if (!_oldStatus.equals(other._oldStatus))
	    return false;
	if (_value == null) {
	    if (other._value != null)
		return false;
	} else if (!_value.equals(other._value))
	    return false;
	return true;
    }

    public List<DescriptionDifferences> getAttributesDifferences() {
	return _attributesDifferences;
    }

    public List<Difference> getDifferences() {
	return _differences;
    }

    public String getNewStatus() {
	return _newStatus;
    }

    public List<DescriptionDifferences> getNotesDifferences() {
	return _notesDifferences;
    }

    public String getOldStatus() {
	return _oldStatus;
    }

    public String getValue() {
	return _value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_attributesDifferences == null) ? 0 : _attributesDifferences.hashCode());
	result = prime * result + ((_differences == null) ? 0 : _differences.hashCode());
	result = prime * result + ((_newStatus == null) ? 0 : _newStatus.hashCode());
	result = prime * result + ((_notesDifferences == null) ? 0 : _notesDifferences.hashCode());
	result = prime * result + ((_oldStatus == null) ? 0 : _oldStatus.hashCode());
	result = prime * result + ((_value == null) ? 0 : _value.hashCode());
	return result;
    }

    @Override
    public String toString() {
	return "TermDifferences [_differences=" + _differences + ", _oldStatus=" + _oldStatus + ", _newStatus="
		+ _newStatus + ", _value=" + _value + ", _attributesDifferences=" + _attributesDifferences
		+ ", _notesDifferences=" + _notesDifferences + "]";
    }
}

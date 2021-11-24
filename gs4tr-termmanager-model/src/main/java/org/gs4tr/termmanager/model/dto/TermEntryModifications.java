package org.gs4tr.termmanager.model.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO class which represents differences between the two collections of
 * {@code Description} objects that are term entry level.
 * 
 * @author TMGR_Backend
 * 
 */
public class TermEntryModifications {

    private final List<DescriptionDifferences> _attributesDifferences;

    public TermEntryModifications() {
	_attributesDifferences = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	TermEntryModifications other = (TermEntryModifications) obj;
	if (_attributesDifferences == null) {
	    if (other._attributesDifferences != null)
		return false;
	} else if (!_attributesDifferences.equals(other._attributesDifferences))
	    return false;
	return true;
    }

    public List<DescriptionDifferences> getAttributesDifferences() {
	return _attributesDifferences;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_attributesDifferences == null) ? 0 : _attributesDifferences.hashCode());
	return result;
    }

    @Override
    public String toString() {
	return "TermEntryModifications [_attributesDifferences=" + _attributesDifferences + "]";
    }
}

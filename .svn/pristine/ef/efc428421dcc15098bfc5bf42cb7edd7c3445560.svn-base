package org.gs4tr.termmanager.service.termentry.synchronization;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.termmanager.model.glossary.Description;

public class DescriptionImportWrapper {

    private MutableInt _count;

    private Set<Description> _overwrittenDescriptions;

    private Set<String> _types;

    public DescriptionImportWrapper() {
	_overwrittenDescriptions = new HashSet<Description>();
	_count = new MutableInt(0);
	_types = new HashSet<String>();
    }

    public MutableInt getCount() {
	return _count;
    }

    public Set<Description> getOverwrittenDescriptions() {
	return _overwrittenDescriptions;
    }

    public Set<String> getTypes() {
	return _types;
    }
}

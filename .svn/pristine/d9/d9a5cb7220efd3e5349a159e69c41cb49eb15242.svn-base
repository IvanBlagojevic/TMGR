package org.gs4tr.termmanager.service.termentry.synchronization;

public enum SyncOption {

    APPEND(TermEntryMerger.class, "Append"), MERGE(TermEntryMerger.class, "Merge"), OVERWRITE(TermEntryOverwriter.class,
	    "Overwrite");

    private final Class<?> _className;
    private final String _value;

    private SyncOption(Class<?> className, String value) {
	_className = className;
	_value = value;
    }

    public Class<?> getClassName() {
	return _className;
    }

    public String getValue() {
	return _value;
    }
}

package org.gs4tr.termmanager.model;

public enum Priority {

    HIGH(3), LOW(1), LOWEST(0), NORMAL(2);

    private int _value;

    private Priority(int value) {
	_value = value;
    }

    public final int getValue() {
	return _value;
    }
}

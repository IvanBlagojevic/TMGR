package org.gs4tr.termmanager.model;

public enum NotificationPriority {

    HIGH(2), LOW(0), NORMAL(1);

    public static final NotificationPriority DEFAULT = NORMAL;

    public static NotificationPriority getEnum(int value) {
	switch (value) {
	case 0: {
	    return NotificationPriority.LOW;
	}

	case 1: {
	    return NotificationPriority.NORMAL;
	}

	case 2: {
	    return NotificationPriority.HIGH;
	}

	default:
	    throw new RuntimeException(String.format("Cannot find AlertPriority enumerator for: %s.", value));
	}
    }

    private int _value;

    NotificationPriority(int value) {
	_value = value;
    }

    public int getValue() {
	return _value;
    }

}

package org.gs4tr.termmanager.service.notification;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;

public class NotificationData {

    private Map<String, MutableInt> _languageTermNumber;

    private boolean _ready = false;

    public NotificationData() {
	_languageTermNumber = new HashMap<String, MutableInt>();
    }

    public Map<String, MutableInt> getLanguageTermNumber() {
	return _languageTermNumber;
    }

    public boolean isReady() {
	return _ready;
    }

    public void setLanguageTermNumber(Map<String, MutableInt> languageTermNumber) {
	_languageTermNumber = languageTermNumber;
    }

    public void setReady(boolean ready) {
	_ready = ready;
    }
}

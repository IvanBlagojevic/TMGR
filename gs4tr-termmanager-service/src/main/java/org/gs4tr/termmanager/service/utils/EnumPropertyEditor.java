package org.gs4tr.termmanager.service.utils;

import java.beans.PropertyEditorSupport;

public class EnumPropertyEditor<T> extends PropertyEditorSupport {
    private Class<T> enumType;

    public EnumPropertyEditor() {
    }

    public EnumPropertyEditor(Class<T> enumType) {
	setEnumType(enumType);
    }

    public Class<T> getEnumType() {
	return enumType;
    }

    public void setAsText(String text) throws IllegalArgumentException {
	for (Object e : getEnumType().getEnumConstants()) {
	    if (e.toString().equals(text)) {
		setValue(e);
		return;
	    }
	}
	throw new IllegalArgumentException(
		"Invalid text for enum of type '" + enumType.getName() + ": '" + text + "'.");
    }

    public void setEnumType(Class<T> enumType) {
	if (!enumType.isEnum()) {
	    throw new IllegalArgumentException("Class must be an enum");
	}
	this.enumType = enumType;
    }
}
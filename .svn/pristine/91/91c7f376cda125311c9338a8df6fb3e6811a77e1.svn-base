package org.gs4tr.termmanager.model.dto;

public class AttributeLevelEnum {

    public static final AttributeLevelEnum LANGUAGE = new AttributeLevelEnum(1);
    public static final AttributeLevelEnum TERMENTRY = new AttributeLevelEnum(0);

    public static AttributeLevelEnum[] getValues() {
	return _values;
    }

    private int _value;
    private static AttributeLevelEnum[] _values = { AttributeLevelEnum.TERMENTRY, AttributeLevelEnum.LANGUAGE };
    private static String[] names = { "Term-entry", "Language" };

    public AttributeLevelEnum() {
    }

    public AttributeLevelEnum(int value) {
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
	AttributeLevelEnum other = (AttributeLevelEnum) obj;
	return _value == other._value;
    }

    public String getName() {
	return names[getValue()];
    }

    public int getValue() {
	return _value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	return prime * result + _value;
    }

    public void setValue(int value) {
	_value = value;
    }
}

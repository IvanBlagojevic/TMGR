package org.gs4tr.termmanager.model.dto;

public class InputFieldTypeEnum {

    public static final InputFieldTypeEnum COMBO = new InputFieldTypeEnum(1);
    public static final InputFieldTypeEnum TEXT = new InputFieldTypeEnum(0);

    private static final InputFieldTypeEnum[] _values = { InputFieldTypeEnum.TEXT, InputFieldTypeEnum.COMBO };

    private static final String[] names = { "Text", "Combo" };

    public static InputFieldTypeEnum[] getValues() {
	return _values;
    }

    private int _value;

    public InputFieldTypeEnum() {
    }

    public InputFieldTypeEnum(int value) {
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
	InputFieldTypeEnum other = (InputFieldTypeEnum) obj;
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

package org.gs4tr.termmanager.model.dto;

public class RoleTypeEnum {

    public static final RoleTypeEnum CONTEXT = new RoleTypeEnum(0);

    public static final RoleTypeEnum SYSTEM = new RoleTypeEnum(1);

    private int _value;

    public RoleTypeEnum() {
    }

    public RoleTypeEnum(int value) {
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
	RoleTypeEnum other = (RoleTypeEnum) obj;
	return _value == other._value;
    }

    public int getValue() {
	return _value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + _value;
	return result;
    }

    public void setValue(int value) {
	_value = value;
    }
}

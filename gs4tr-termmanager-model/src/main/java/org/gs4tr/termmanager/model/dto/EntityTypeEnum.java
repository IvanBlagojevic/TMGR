package org.gs4tr.termmanager.model.dto;

public class EntityTypeEnum {

    public static final EntityTypeEnum ORGANIZATION = new EntityTypeEnum(0);

    public static final EntityTypeEnum PROJECT = new EntityTypeEnum(1);

    public static final EntityTypeEnum ROLE = new EntityTypeEnum(2);

    public static final EntityTypeEnum TERM = new EntityTypeEnum(3);

    public static final EntityTypeEnum TERMENTRY = new EntityTypeEnum(4);

    public static final EntityTypeEnum USER = new EntityTypeEnum(5);

    private int _value;

    private static String[] names = { "ORGANIZATION", "PROJECT", "ROLE", "TERM", "TERMENTRY", "USER" };

    public EntityTypeEnum() {
    }

    public EntityTypeEnum(int value) {
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
	EntityTypeEnum other = (EntityTypeEnum) obj;
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

package org.gs4tr.termmanager.model.dto;

public class TermEntryAttributeTypeDto {

    public static final TermEntryAttributeTypeDto MULTIMEDIA = new TermEntryAttributeTypeDto(1);
    public static final TermEntryAttributeTypeDto TEXT = new TermEntryAttributeTypeDto(0);

    public static TermEntryAttributeTypeDto[] getValues() {
	return _values;
    }
    private int _value;
    private static TermEntryAttributeTypeDto[] _values = { TermEntryAttributeTypeDto.TEXT,
	    TermEntryAttributeTypeDto.MULTIMEDIA };
    private static String[] names = { "Text", "Multimedia" };

    public TermEntryAttributeTypeDto() {
    }

    public TermEntryAttributeTypeDto(int value) {
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
	TermEntryAttributeTypeDto other = (TermEntryAttributeTypeDto) obj;
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
	result = prime * result + _value;
	return result;
    }

    public void setValue(int value) {
	_value = value;
    }
}

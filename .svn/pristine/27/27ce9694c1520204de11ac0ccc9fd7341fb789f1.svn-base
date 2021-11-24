package org.gs4tr.termmanager.webservice.utils;

import org.gs4tr.foundation.modules.webmvc.test.model.Part;

/**
 * Use this class instead Foundation-Modules ParameterPart, because this one has
 * proper Content-Type header.
 * 
 * @author fmiskovic
 */
public class ParameterPart implements Part {

    private static final String PARAMETER_PART_PATTERN = "\r\nContent-Disposition: form-data; name=\"%s\"\r\nContent-Type: application/json\r\n\r\n%s\r\n--";

    private String name;

    private String parameterValue;

    public ParameterPart(String name, String value) {
	this.name = name;
	this.parameterValue = value;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public String getValue() {
	String rawPart = String.format(PARAMETER_PART_PATTERN, getName(), getParameterValue());
	return rawPart;
    }

    private String getParameterValue() {
	return parameterValue;
    }

}

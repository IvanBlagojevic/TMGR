package org.gs4tr.termmanager.service.export;

/**
 * Builds CSV line from fields using {@link #addField(String field)}. Threats
 * <code>null</code> fields like empty fields.
 * 
 * @author Nguzina
 */
public class TabLineBuilder extends AbstractDelimiterLineBuilder {

    @Override
    char getDelimiter() {
	return '\t';
    }

}

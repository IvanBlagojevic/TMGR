package org.gs4tr.termmanager.service.export;

import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.glossary.TermEntry;

public class JSONLineBuilder extends AbstractDelimiterLineBuilder {

    protected String buildTermEntryXml(TermEntry termEntry, Boolean withDetails, ExportInfo exportInfo) {
	return null;
    }

    @Override
    char getDelimiter() {
	return StringConstants.SPACE.charAt(0);
    }
}

package org.gs4tr.termmanager.service.export;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.TermEntry;

public class CsvExportDocument extends AbstractTypeExportDocument<CsvLineBuilder> {

    @Override
    protected void appendTermEntryDescriptions(CsvLineBuilder builder, TermEntry termEntry, List<String> descriptions) {
	Set<Description> termEntryDescriptions = termEntry.getDescriptions();
	StringBuilder descBuilder = new StringBuilder();
	boolean isFirst = true;
	if (CollectionUtils.isNotEmpty(termEntryDescriptions)) {
	    for (Description termEntryDescription : termEntryDescriptions) {
		if (descriptions.contains(termEntryDescription.getType())) {
		    if (!isFirst) {
			descBuilder.append(StringConstants.PIPE);
		    }
		    descBuilder.append(buildTermEntryDescriptionString(termEntryDescription));
		    isFirst = false;
		}
	    }
	}

	String field = descBuilder.toString();
	if (StringUtils.isNotBlank(field)) {
	    builder.addField(field);
	}
    }

    @Override
    CsvLineBuilder getBuilderInstance() {
	return new CsvLineBuilder();
    }
}

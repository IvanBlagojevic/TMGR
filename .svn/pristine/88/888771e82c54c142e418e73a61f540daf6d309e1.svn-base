package org.gs4tr.termmanager.service.xls.report.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.service.xls.report.builder.terminology.ReportDescription;

public class ReportDescriptionConverter {

    public static List<ReportDescription> convertTo(Collection<Description> descriptions) {
	List<ReportDescription> reportDescriptions = new ArrayList<>();
	if (CollectionUtils.isNotEmpty(descriptions)) {
	    descriptions.forEach(d -> reportDescriptions.add(convertTo(d)));
	}

	return reportDescriptions;
    }

    public static ReportDescription convertTo(Description description) {
	return new ReportDescription(description);
    }

    public static ReportDescription convertTo(Description oldDescription, Description newDescription) {
	return new ReportDescription(oldDescription, newDescription);
    }

    private ReportDescriptionConverter() {

    }
}

package org.gs4tr.termmanager.service.export;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.LastOperationEnum;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;

public class CsvSyncDocument extends AbstractDelimiterExportDocument<CsvLineBuilder> {

    private StringBuffer buildDescriptions(boolean descriptionTypeRequested, List<Description> termDescriptions) {
	StringBuffer buffer = new StringBuffer();

	boolean isFirst = true;
	for (Description termDescription : termDescriptions) {
	    String description = null;
	    if (!isFirst) {
		buffer.append(StringConstants.PIPE);
	    }
	    if (descriptionTypeRequested) {
		description = termDescription.getValue();
	    } else {
		description = buildDescriptionString(termDescription);
	    }
	    buffer.append(description);
	    isFirst = false;
	}

	return buffer;
    }

    private void filterDescriptionsInternal(Set<Description> sourceDescriptions, String descriptionType,
	    List<Description> descriptionsToExport) {
	if (CollectionUtils.isNotEmpty(sourceDescriptions)) {
	    for (Description termDescription : sourceDescriptions) {
		if (StringUtils.isBlank(descriptionType) || descriptionType.equals(termDescription.getType())) {
		    descriptionsToExport.add(termDescription);
		}
	    }
	}
    }

    private List<Description> filterDescriptionsToExport(Set<Description> sourceDescriptions,
	    Set<Description> targetDescriptions, String descriptionType) {
	List<Description> descriptionsToExport = new ArrayList<Description>();
	filterDescriptionsInternal(sourceDescriptions, descriptionType, descriptionsToExport);
	filterDescriptionsInternal(targetDescriptions, descriptionType, descriptionsToExport);
	return descriptionsToExport;
    }

    private void filterNonForbiddenTerms(List<Term> terms) {
	CollectionUtils.filter(terms, new Predicate() {
	    @Override
	    public boolean evaluate(Object item) {
		Term term = (Term) item;
		return !(term.isDisabled() || term.isForbidden());
	    }
	});
    }

    private Term findFirstExportable(List<Term> targetTerms) {
	if (CollectionUtils.isEmpty(targetTerms)) {
	    return null;
	}

	for (Term term : targetTerms) {
	    if (isTermExportable(term)) {
		return term;
	    }
	}
	return null;
    }

    @Override
    protected String buildTermEntryXml(TermEntry termEntry, ExportInfo exportInfo, boolean isWS,
	    ExportTermsCallback exportTermsCallback) {
	TermEntrySearchRequest searchRequest = getSearchRequest();
	String source = searchRequest.getSourceLocale();
	String target = searchRequest.getTargetLocales().get(0);
	List<Term> terms = exportTermsCallback.getAllTermEntryTerms(termEntry);

	if (isWS) {
	    setWsStatuses();
	    filterTermsByStatus(terms);
	}

	String descriptionType = searchRequest.getDescriptionType();
	boolean descriptionTypeRequested = StringUtils.isNotBlank(descriptionType);
	List<Term> sourceTerms = findTermsByLanguage(terms, source);
	List<Term> targetTerms = findTermsByLanguage(terms, target);

	Term sourceTerm = findFirstExportable(sourceTerms);
	Term targetTerm = findFirstExportable(targetTerms);
	if (sourceTerm == null) {
	    sourceTerm = findTermByLanguage(sourceTerms, source);
	} else if (targetTerm == null) {
	    targetTerm = findTermByLanguage(targetTerms, target);
	}

	if (sourceTerm == null || targetTerm == null) {
	    return StringUtils.EMPTY;
	}
	StringBuilder stringBuilder = new StringBuilder();
	int totalTermEntriesExported = exportInfo.getTotalTermEntriesExported();
	exportInfo.setTotalTermEntriesExported(totalTermEntriesExported + 1);

	CsvLineBuilder builder = getBuilderInstance();
	builder.addField(TicketConverter.fromInternalToDto(termEntry.getUuId()));
	builder.addField(sourceTerm.getName());
	builder.addField(targetTerm.getName());
	StringBuffer descriptions = new StringBuffer();
	Set<Description> sourceDescriptions = sourceTerm.getDescriptions();
	Set<Description> targetDescriptions = targetTerm.getDescriptions();

	List<Description> descriptionsToExport = filterDescriptionsToExport(sourceDescriptions, targetDescriptions,
		descriptionType);
	if (CollectionUtils.isNotEmpty(descriptionsToExport)) {
	    descriptions = buildDescriptions(descriptionTypeRequested, descriptionsToExport);

	}

	builder.addField(descriptions.toString());
	builder.addField(sourceTerm.getUserCreated());
	builder.addField(String.valueOf(sourceTerm.getDateCreated()));
	builder.addField(LastOperationEnum.UPDATED.getShortLabel());
	builder.addField(sourceTerm.getUserModified());
	Date sourceDateModified = new Date(sourceTerm.getDateModified());
	Date targetDateModified = new Date(targetTerm.getDateModified());
	builder.addField(
		String.valueOf(sourceDateModified.compareTo(targetDateModified) > 0 ? sourceDateModified.getTime()
			: targetDateModified.getTime()));
	String line = builder.toString();
	stringBuilder.append(line);
	stringBuilder.append(StringConstants.NEW_LINE);

	return stringBuilder.toString();
    }

    @Override
    CsvLineBuilder getBuilderInstance() {
	return new CsvLineBuilder();
    }

}

package org.gs4tr.termmanager.service.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation3.tbx.constraint.ConstraintFactory;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.LastOperationEnum;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.dto.ExportTermModel;
import org.gs4tr.termmanager.model.dto.converter.DescriptionConverter;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.service.utils.ServiceUtils;

public class JSONExportDocument extends AbstractDelimiterExportDocument<JSONLineBuilder> {

    @Override
    public String buildTermEntryXml(TermEntry termEntry, ExportInfo exportInfo, boolean isWS,
	    ExportTermsCallback exportTermsCallback) throws IOException {

	TermEntrySearchRequest searchRequest = getSearchRequest();

	String source = searchRequest.getSourceLocale();
	String target = searchRequest.getTargetLocales().get(0);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	if (languageTerms == null) {
	    return null;
	}

	Set<Term> sourceTerms = languageTerms.get(source);
	Set<Term> targetTerms = languageTerms.get(target);

	StringBuilder builder = new StringBuilder();

	// just regular terms
	if (CollectionUtils.isNotEmpty(sourceTerms) && CollectionUtils.isNotEmpty(targetTerms)) {
	    appendRegularTerms(builder, termEntry, sourceTerms, targetTerms);
	}

	return builder.toString();
    }

    @Override
    public void close() {
	try {
	    getOutputStream().write(StringConstants.END_OF_FILE.getBytes(StandardCharsets.UTF_8));
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException(e);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	} finally {
	    super.close();
	}
    }

    @Override
    public ConstraintFactory open(OutputStream outputStream, TermEntrySearchRequest searchRequest, String xcsFileName) {
	ConstraintFactory factory = super.open(outputStream, searchRequest, xcsFileName);
	Map<String, Long> forbbiddenTermCountMap = new HashMap<>();
	forbbiddenTermCountMap.put("forbiddenTermsCount", searchRequest.getForbiddenTermCount());
	try {
	    String jsonString = JsonUtils.writeValueAsString(forbbiddenTermCountMap);
	    getOutputStream().write(jsonString.getBytes(StandardCharsets.UTF_8));
	    getOutputStream().write(StringConstants.NEW_LINE.getBytes(StandardCharsets.UTF_8));
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException(e);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
	return factory;
    }

    private void appendRegularTerms(StringBuilder builder, TermEntry termEntry, Set<Term> sourceTerms,
	    Set<Term> targetTerms) throws IOException {
	ExportTermModel model = new ExportTermModel();
	for (Term sourceTerm : sourceTerms) {
	    Boolean sourceExportable = isTermExportable(sourceTerm);
	    for (Term targetTerm : targetTerms) {
		if (isTermExportable(targetTerm) || sourceExportable) {

		    Set<Description> entryDescriptions = termEntry.getDescriptions();
		    Set<Description> sourceDescriptions = ServiceUtils.getDescriptionsByType(sourceTerm,
			    Description.ATTRIBUTE);
		    if (CollectionUtils.isNotEmpty(entryDescriptions)) {
			sourceDescriptions.addAll(entryDescriptions);
		    }
		    Set<Description> targetDescriptions = ServiceUtils.getDescriptionsByType(targetTerm,
			    Description.ATTRIBUTE);

		    Set<Description> filteredSourceDescriptions = new HashSet<>();
		    Set<Description> filteredTargetDescriptions = new HashSet<>();

		    if (getSearchRequest().getExportAllDescriptions()) {
			filteredSourceDescriptions.addAll(sourceDescriptions);
			filteredTargetDescriptions.addAll(targetDescriptions);
		    } else {
			filterDescriptions(sourceDescriptions, filteredSourceDescriptions);
			filterDescriptions(targetDescriptions, filteredTargetDescriptions);
		    }

		    org.gs4tr.termmanager.model.dto.Description[] dtoSourceDescriptions = DescriptionConverter
			    .fromInternalToDto(filteredSourceDescriptions);
		    org.gs4tr.termmanager.model.dto.Description[] dtoTargetDescriptions = DescriptionConverter
			    .fromInternalToDto(filteredTargetDescriptions);
		    model.setSourceAttributes(dtoSourceDescriptions);
		    model.setTargetAttributes(dtoTargetDescriptions);
		    // in case it is deleted term, date is not needed so set
		    // default value 0
		    Long dateCreated = sourceTerm.getDateCreated() != null ? sourceTerm.getDateCreated() : 0;
		    model.setCreationDate(dateCreated);
		    model.setCreationUser(sourceTerm.getUserCreated());
		    model.setModificationUser(sourceTerm.getUserModified());
		    model.setForbidden(Boolean.FALSE);
		    model.setSource(sourceTerm.getName());
		    // in case it is deleted term, date is not needed so set
		    // default value 0
		    Long srcDateModified = sourceTerm.getDateModified() != null ? sourceTerm.getDateModified() : 0;
		    Long trgDateModified = targetTerm.getDateModified() != null ? targetTerm.getDateModified() : 0;
		    model.setModificationDate(Math.max(srcDateModified, trgDateModified));
		    LastOperationEnum lastOperation = chooseLastOperation(sourceTerm, targetTerm);
		    model.setOperation(lastOperation.getShortLabel());
		    model.setTicket(sourceTerm.getUuId().concat(targetTerm.getUuId()));
		    model.setTarget(targetTerm.getName());
		    model.setSuggestions(ArrayUtils.EMPTY_STRING_ARRAY);

		    String jsonSingleLine = JsonUtils.writeValueAsString(model);

		    builder.append(jsonSingleLine);
		    builder.append(StringConstants.NEW_LINE);
		}
	    }
	}
    }

    @Override
    JSONLineBuilder getBuilderInstance() {
	return new JSONLineBuilder();
    }

    @Override
    protected boolean isExportableByInTranslation(Term term, Boolean exportable) {
	return true;
    }
}

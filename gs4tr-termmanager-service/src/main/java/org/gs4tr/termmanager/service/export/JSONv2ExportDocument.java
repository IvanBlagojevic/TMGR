package org.gs4tr.termmanager.service.export;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.gs4tr.foundation3.tbx.constraint.ConstraintFactory;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.LanguageModel;
import org.gs4tr.termmanager.model.dto.TermEntryV2Model;
import org.gs4tr.termmanager.model.dto.TermV2Model;
import org.gs4tr.termmanager.model.dto.converter.DescriptionConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.service.utils.ServiceUtils;

public class JSONv2ExportDocument extends ExportDocumentFactory {

    @Override
    public void close() throws Exception {
	try {
	    getOutputStream().write(createEnd());
	    getOutputStream().flush();
	} catch (IOException e) {
	    throw new RuntimeException(e.getMessage(), e);
	} finally {
	    getOutputStream().close();
	}
    }

    @Override
    public ConstraintFactory open(OutputStream outputStream, TermEntrySearchRequest searchRequest, String xcsFileName)
	    throws IOException {
	setSearchRequest(searchRequest);
	setOutputStream(outputStream);
	try {
	    getOutputStream().write(createHeader().getBytes(StandardCharsets.UTF_8));
	} catch (IOException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
	return null;
    }

    private long calculateExportTime() {
	return System.currentTimeMillis() - getSearchRequest().getStartTime();
    }

    private List<TermV2Model> convertTerms(Set<Term> terms) {

	List<TermV2Model> termModels = new ArrayList<>();

	for (Term term : terms) {
	    if (isTermExportable(term)) {
		termModels.add(createTermV2Model(term));
	    }
	}

	return termModels;
    }

    private byte[] createEnd() {
	return end().concat(String.valueOf(calculateExportTime())).concat("}").getBytes(StandardCharsets.UTF_8);
    }

    private String createHeader() {
	return "{" + "\"termEntries\"" + ":" + " ["; //$NON-NLS-1$
    }

    private String createJsonTermEntry(TermEntry termEntry) {
	TermEntrySearchRequest searchRequest = getSearchRequest();

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	TermEntryV2Model model = new TermEntryV2Model();

	model.setCreationDate(termEntry.getDateCreated());
	model.setCreationUser(termEntry.getUserCreated());
	model.setModificationDate(termEntry.getDateModified());
	model.setModificationUser(termEntry.getUserModified());
	model.setDescriptions(DescriptionConverter.convertDescriptionsToDto(termEntry.getDescriptions()));

	List<LanguageModel> langsSet = new ArrayList<LanguageModel>();

	String sourceLocale = searchRequest.getSourceLocale();
	Set<Term> sourceTerms = languageTerms.get(sourceLocale);
	if (CollectionUtils.isNotEmpty(sourceTerms)) {
	    langsSet.add(createLanguageModel(sourceLocale, sourceTerms));
	}

	List<String> targetLocales = searchRequest.getTargetLocales();

	if (CollectionUtils.isNotEmpty(targetLocales)) {
	    for (String target : targetLocales) {
		Set<Term> targetTerms = languageTerms.get(target);
		if (CollectionUtils.isNotEmpty(targetTerms)) {
		    langsSet.add(createLanguageModel(target, targetTerms));
		}
	    }
	}

	/* Don't send TermEntry without terms */
	LanguageModel languageModel = langsSet.stream().filter(langModel -> !langModel.getTerms().isEmpty()).findFirst()
		.orElse(null);
	if (Objects.isNull(languageModel)) {
	    return StringConstants.EMPTY;
	}

	model.setLangList(langsSet);

	return JsonUtils.writeValueAsString(model);
    }

    private LanguageModel createLanguageModel(String locale, Set<Term> terms) {
	LanguageModel langSet = new LanguageModel();
	langSet.setLocale(locale);
	langSet.setTerms(convertTerms(terms));
	return langSet;
    }

    private TermV2Model createTermV2Model(Term term) {
	TermV2Model termModel = new TermV2Model();
	termModel.setTermText(term.getName());
	termModel.setForbidden(term.getForbidden());
	termModel.setStatus(ItemStatusTypeHolder.getStatusDisplayName(term.getStatus()));
	termModel.setDescriptions(DescriptionConverter.convertDescriptionsToDto(term.getDescriptions()));

	return termModel;
    }

    private String end() {
	return "],\"returnCode\": 0,\"time\":"; //$NON-NLS-1$
    }

    @Override
    protected String buildTermEntryXml(TermEntry termEntry, ExportInfo exportInfo, boolean isWS,
	    ExportTermsCallback exportTermsCallback) {

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	StringBuilder builder = new StringBuilder();
	if (MapUtils.isNotEmpty(languageTerms)) {
	    if (!getSearchRequest().isFirstTermEntry()) {
		builder.append(StringConstants.COMMA);
	    }

	    String termEntryString = createJsonTermEntry(termEntry);
	    if (termEntryString.isEmpty()) {
		return termEntryString;
	    }
	    builder.append(termEntryString);
	    getSearchRequest().setFirstTermEntry(false);
	}

	return builder.toString();
    }

    @Override
    protected boolean isExportableByForbidden(Term term, Boolean exportForbidden) {
	return false;
    }

    @Override
    protected Boolean isTermExportable(Term term) {
	String currentUser = TmUserProfile.getCurrentUserName();
	return ServiceUtils.isTermExportable(term, currentUser, getSearchRequest().getStatuses());
    }
}

package org.gs4tr.termmanager.service.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation3.tbx.constraint.ConstraintFactory;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.LastOperationEnum;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;

public abstract class AbstractDelimiterExportDocument<T extends AbstractDelimiterLineBuilder>
	extends ExportDocumentFactory {

    @Override
    public void close() {
	try {
	    getOutputStream().flush();
	    getOutputStream().close();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}

    }

    @Override
    public ConstraintFactory open(OutputStream outputStream, TermEntrySearchRequest searchRequest, String xcsFileName) {
	setSearchRequest(searchRequest);
	setOutputStream(outputStream);
	setForbiddenClause(Boolean.TRUE);
	return null;
    }

    public void setWsStatuses() {
	Boolean isBlacklistedSearch = getSearchRequest().getForbidden();
	boolean blacklistIncluded = isBlacklistedSearch != null && isBlacklistedSearch;

	List<String> wsStatuses = new ArrayList<>();
	wsStatuses.add(ItemStatusTypeHolder.PROCESSED.getName());

	if (blacklistIncluded) {
	    wsStatuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());
	}

	if (getSearchRequest().isSharePendingTerms()) {
	    wsStatuses.add(ItemStatusTypeHolder.WAITING.getName());
	}

	getSearchRequest().setStatuses(wsStatuses);
    }

    private LastOperationEnum chooseLastOperationByDate(Term sourceTerm, Term targetTerm) {
	if (isForUpdate(sourceTerm, targetTerm)) {
	    return LastOperationEnum.UPDATED;
	} else {
	    return LastOperationEnum.CREATED;
	}
    }

    private LastOperationEnum chooseLastOperationIfPending(Term sourceTerm, Term targetTerm) {
	String currentUserName = TmUserProfile.getCurrentUserName();

	String sourceUserCreated = sourceTerm.getUserCreated();

	String targetUserCreated = targetTerm.getUserCreated();

	/*
	 ***********************************************************************
	 * Share pending terms is added due to: TERII-4916 Server - Edit Project dialog
	 * | User should be able to configure sync statuses
	 ***********************************************************************
	 */
	if (sourceUserCreated.equals(currentUserName) || targetUserCreated.equals(currentUserName)
		|| getSearchRequest().isSharePendingTerms()) {
	    /*
	     * if term is pending or onHold and creation user is equal to current user,
	     * command should be updated. TERII-1470
	     */
	    return chooseLastOperationByDate(sourceTerm, targetTerm);
	} else {
	    return LastOperationEnum.DELETED;
	}
    }

    private LastOperationEnum choseLastOperationIfOnHold(Term sourceTerm, Term targetTerm) {
	String currentUserName = TmUserProfile.getCurrentUserName();
	String sourceUserCreated = sourceTerm.getUserCreated();
	String targetUserCreated = targetTerm.getUserCreated();
	if (sourceUserCreated.equals(currentUserName) || targetUserCreated.equals(currentUserName)) {
	    return chooseLastOperationByDate(sourceTerm, targetTerm);
	} else {
	    return LastOperationEnum.DELETED;
	}
    }

    private boolean isExportableDescription(List<String> projectAttributes, Description termDescription) {
	return projectAttributes.contains(termDescription.getType());
    }

    private boolean isForUpdate(Term sourceTerm, Term targetTerm) {
	return (!sourceTerm.getDateCreated().equals(sourceTerm.getDateModified()))
		|| (!targetTerm.getDateCreated().equals(targetTerm.getDateModified()));
    }

    private boolean isInTranslationSearch(TermEntrySearchRequest request) {
	List<String> statuses = request.getStatuses();
	if (statuses == null) {
	    return false;
	}

	return statuses.contains(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName())
		|| statuses.contains(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName());
    }

    protected String appendDescriptions(Set<Description> termEntryDescriptions, Set<Description> sourceDescriptions,
	    Set<Description> targetDescriptions, List<String> descriptions, List<String> notes) {

	boolean notEmptyTermEntrytDesc = CollectionUtils.isNotEmpty(termEntryDescriptions);
	boolean notEmptySourceDesc = CollectionUtils.isNotEmpty(sourceDescriptions);
	boolean notEmptyTargetDesc = CollectionUtils.isNotEmpty(targetDescriptions);
	StringBuilder descBuilder = new StringBuilder();
	if (notEmptyTermEntrytDesc || notEmptySourceDesc || notEmptyTargetDesc) {
	    boolean isFirst = true;

	    if (notEmptyTermEntrytDesc) {
		for (Description termEntryDescription : termEntryDescriptions) {
		    if (descriptions != null && descriptions.contains(termEntryDescription.getType())) {
			if (!isFirst) {
			    descBuilder.append(StringConstants.PIPE);
			}
			descBuilder.append(buildTermEntryDescriptionString(termEntryDescription));
			isFirst = false;
		    }
		}
	    }

	    if (notEmptySourceDesc) {
		for (Description termDescription : sourceDescriptions) {
		    if (isDescriptionExportable(termDescription, descriptions, notes)) {
			if (!isFirst) {
			    descBuilder.append(StringConstants.PIPE);
			}
			descBuilder.append(buildDescriptionString(termDescription));
			isFirst = false;
		    }
		}
	    }
	    if (notEmptyTargetDesc) {
		for (Description termDescription : targetDescriptions) {
		    if (isDescriptionExportable(termDescription, descriptions, notes)) {

			if (!isFirst) {
			    descBuilder.append(StringConstants.PIPE);
			}
			descBuilder.append(buildDescriptionString(termDescription));
			isFirst = false;
		    }
		}
	    }
	}
	return descBuilder.toString();
    }

    protected void appendTermEntryDescriptions(T builder, TermEntry termEntry, List<String> descriptions) {
    }

    protected String buildDescriptionString(Description termDescription) {
	return termDescription.getType().concat(StringConstants.COLON).concat(termDescription.getValue());
    }

    protected String buildTermEntryDescriptionString(Description termEntryDescription) {
	return termEntryDescription.getType().concat(StringConstants.COLON).concat(termEntryDescription.getValue());
    }

    @Override
    protected String buildTermEntryXml(TermEntry termEntry, ExportInfo exportInfo, boolean isWS,
	    ExportTermsCallback exportTermsCallback) throws IOException {
	TermEntrySearchRequest searchRequest = getSearchRequest();
	String source = searchRequest.getSourceLocale();
	String target = searchRequest.getTargetLocales().get(0);
	List<Term> terms = exportTermsCallback.getAllTermEntryTerms(termEntry);
	List<Term> sourceTerms = findTermsByLanguage(terms, source);
	List<Term> targetTerms = findTermsByLanguage(terms, target);

	if (CollectionUtils.isEmpty(sourceTerms) && CollectionUtils.isEmpty(targetTerms)) {
	    return StringUtils.EMPTY;
	}
	StringBuilder buffer = new StringBuilder();
	int totalTermEntriesExported = exportInfo.getTotalTermEntriesExported();
	exportInfo.setTotalTermEntriesExported(totalTermEntriesExported + 1);
	List<String> attributeTypes = searchRequest.getTermAttributes();
	List<String> noteTypes = searchRequest.getTermNotes();

	for (Term sourceTerm : sourceTerms) {
	    Boolean sourceTermExportable = isTermExportable(sourceTerm);
	    for (Term targetTerm : targetTerms) {
		if (sourceTermExportable && isTermExportable(targetTerm)) {
		    T builder = getBuilderInstance();
		    exportInfo.incrementTotalTermsExported();
		    exportInfo.incrementTotalTermsExported();

		    builder.addField(sourceTerm.getName());
		    builder.addField(targetTerm.getName());

		    Set<Description> termEntryDescriptions = termEntry.getDescriptions();
		    Set<Description> sourceDescriptions = sourceTerm.getDescriptions();
		    Set<Description> targetDescriptions = targetTerm.getDescriptions();
		    String descriptions = appendDescriptions(termEntryDescriptions, sourceDescriptions,
			    targetDescriptions, attributeTypes, noteTypes);
		    builder.addField(descriptions);
		    String line = builder.toString();
		    buffer.append(line);
		    buffer.append(StringConstants.NEW_LINE);
		}
	    }
	}

	return buffer.toString();
    }

    protected LastOperationEnum chooseLastOperation(Term sourceTerm, Term targetTerm) {
	LastOperationEnum lastOperation = LastOperationEnum.CREATED;

	if (sourceTerm.isDisabled() || targetTerm.isDisabled() || sourceTerm.isForbidden()
		|| targetTerm.isForbidden()) {
	    lastOperation = LastOperationEnum.DELETED;
	} else if (ItemStatusTypeHolder.isTermInTranslation(sourceTerm)
		|| ItemStatusTypeHolder.isTermInTranslation(targetTerm)) {
	    lastOperation = LastOperationEnum.DELETED;
	} else if (StringUtils.isBlank(sourceTerm.getName()) || StringUtils.isBlank(targetTerm.getName())) {
	    lastOperation = LastOperationEnum.DELETED;
	} else if (sourceTerm.getStatus().equals(ItemStatusTypeHolder.ON_HOLD.getName())
		|| targetTerm.getStatus().equals(ItemStatusTypeHolder.ON_HOLD.getName())) {
	    lastOperation = choseLastOperationIfOnHold(sourceTerm, targetTerm);
	} else if (sourceTerm.getStatus().equals(ItemStatusTypeHolder.WAITING.getName())
		|| targetTerm.getStatus().equals(ItemStatusTypeHolder.WAITING.getName())) {
	    lastOperation = chooseLastOperationIfPending(sourceTerm, targetTerm);
	} else if (isForUpdate(sourceTerm, targetTerm)) {
	    lastOperation = LastOperationEnum.UPDATED;
	}

	return lastOperation;

    }

    protected Set<Description> filterDescriptions(Set<Description> sourceDescriptions,
	    Set<Description> filteredDescriptions) {
	if (CollectionUtils.isEmpty(sourceDescriptions)) {
	    return null;
	}

	List<String> projectAttributes = getSearchRequest().getTermAttributes();
	for (Description termDescription : sourceDescriptions) {
	    if (isExportableDescription(projectAttributes, termDescription)) {
		filteredDescriptions.add(termDescription);
	    }
	}
	return filteredDescriptions;
    }

    protected Term findTermByLanguage(Collection<Term> terms, String source) {
	for (Term term : terms) {
	    if (term.getLanguageId().equals(source)) {
		return term;
	    }
	}
	return null;
    }

    protected List<Term> findTermsByLanguage(Collection<Term> allTerms, String source) {
	List<Term> terms = new ArrayList<>();
	if (CollectionUtils.isNotEmpty(allTerms)) {
	    for (Term term : allTerms) {
		if (term.getLanguageId().equals(source)) {
		    terms.add(term);
		}
	    }
	}
	return terms;
    }

    abstract T getBuilderInstance();

    protected boolean isDescriptionExportable(Description termDescription, List<String> descriptions,
	    List<String> notes) {
	switch (termDescription.getBaseType()) {
	case Description.ATTRIBUTE:
	    if (descriptions != null && descriptions.contains(termDescription.getType())) {
		return true;
	    }
	    break;

	case Description.NOTE:
	    if (notes != null && notes.contains(termDescription.getType())) {
		return true;
	    }
	    break;

	default:
	    break;
	}
	return false;
    }

    @Override
    protected boolean isExportableByForbidden(Term term, Boolean exportForbidden) {
	return true;
    }

    protected boolean isExportableByInTranslation(Term term, Boolean exportable) {
	return !ItemStatusTypeHolder.isTermInTranslation(term);
    }

    protected boolean isExportableByStatus(Term term) {
	return true;
    }

    protected boolean isExportableByUser(Term term) {
	return true;
    }

    @Override
    protected Boolean isTermExportable(Term term) {

	TermEntrySearchRequest searchRequest = getSearchRequest();
	Date dateModifiedFrom = searchRequest.getDateModifiedFrom();

	String termLocale = term.getLanguageId();
	String sourceLocale = searchRequest.getSourceLocale();
	String targetLocale = searchRequest.getTargetLocales().get(0);

	boolean exportableByName = StringUtils.isNotEmpty(term.getName());
	if (!exportableByName) {
	    return Boolean.FALSE;
	}

	boolean exportableByLanguage = (termLocale.equals(sourceLocale) || termLocale.equals(targetLocale));

	boolean exportableByDate = term.isDisabled() || term.getDateModified() > dateModifiedFrom.getTime();

	boolean exportableByStatus = isExportableByStatus(term);

	boolean exportableByUser = isExportableByUser(term);

	boolean exportableForbidden = isExportableByForbidden(term, searchRequest.getForbidden());

	boolean exportableByTranslation = isExportableByInTranslation(term, isInTranslationSearch(searchRequest));

	return exportableByName && exportableByLanguage && exportableByDate && exportableForbidden
		&& exportableByTranslation && (exportableByStatus || exportableByUser);
    }

}

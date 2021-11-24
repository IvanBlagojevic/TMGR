package org.gs4tr.termmanager.io.edd.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.gs4tr.termmanager.model.ProjectLanguageDetailInfoIO;
import org.gs4tr.termmanager.model.dto.ProjectDetailCountsIO;
import org.gs4tr.termmanager.model.dto.ProjectDetailsIO;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.query.AbstractPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;

public class HandlerUtils {

    public static ProjectDetailsIO createProjectDetailInfoFromFacetSearch(Set<String> languageIds, long projectId,
	    ITmgrGlossarySearcher searcher, int synonymNumber) {

	TmgrSearchFilter filter = createSearchFilter(projectId, languageIds, synonymNumber);
	FacetTermCounts facetTermCounts = searchFacetTermCounts(filter, searcher);
	long numberOfTermEntries = getNumberOfTermEntries(filter, projectId, searcher);

	return createProjectDetailsIO(projectId, numberOfTermEntries, facetTermCounts,
		new Date(System.currentTimeMillis()));

    }

    private static ProjectDetailsIO createProjectDetailsIO(Long projectId, long numberOfTermEntries,
	    FacetTermCounts facetTermCounts, Date dateModified) {

	ProjectDetailsIO projectDetails = new ProjectDetailsIO();
	projectDetails.setProjectId(projectId);

	ProjectDetailCountsIO projectDetailCounts = new ProjectDetailCountsIO(projectId);
	projectDetailCounts.setDateModified(dateModified);
	projectDetailCounts.setTermEntryCount(numberOfTermEntries);

	if (Objects.isNull(facetTermCounts)) {
	    return projectDetails;
	}

	List<ProjectLanguageDetailInfoIO> languageDetailInfoIOList = new ArrayList<>();

	Map<String, FacetTermCounts.LanguageTermCount> termCountByLanguage = facetTermCounts.getTermCountByLanguage();
	for (Map.Entry<String, FacetTermCounts.LanguageTermCount> entry : termCountByLanguage.entrySet()) {

	    String languageId = entry.getKey();
	    FacetTermCounts.LanguageTermCount languageTermCount = entry.getValue();

	    long approved = languageTermCount.getApproved();
	    long forbidden = languageTermCount.getForbidden();
	    long inFinalReview = languageTermCount.getInFinalReview();
	    long inTranslationReview = languageTermCount.getInTranslationReview();
	    long onHold = languageTermCount.getOnHold();
	    long pending = languageTermCount.getPending();
	    long termCount = languageTermCount.getTermCount();

	    ProjectLanguageDetailInfoIO projectLanguageDetailInfoIO = new ProjectLanguageDetailInfoIO(languageId);
	    projectLanguageDetailInfoIO.setTermCount(termCount);
	    projectLanguageDetailInfoIO.setApprovedTermCount(approved);
	    projectLanguageDetailInfoIO.setOnHoldTermCount(onHold);
	    projectLanguageDetailInfoIO.setPendingTermCount(pending);
	    projectLanguageDetailInfoIO.setForbiddenTermCount(forbidden);
	    projectLanguageDetailInfoIO.setTermInSubmissionCount(inFinalReview + inTranslationReview);
	    projectLanguageDetailInfoIO.setTermEntryCount(numberOfTermEntries);
	    projectLanguageDetailInfoIO.setDateModified(dateModified);
	    projectLanguageDetailInfoIO.setProjectId(projectId);

	    projectDetailCounts.incrementTotalTermCount(termCount);
	    projectDetailCounts.incrementApprovedCount(approved);
	    projectDetailCounts.incrementOnHoldCount(onHold);
	    projectDetailCounts.incrementPendingTermCount(pending);
	    projectDetailCounts.incrementForbiddenCount(forbidden);
	    projectDetailCounts.incrementTermInSubmissionCount(inFinalReview + inTranslationReview);

	    languageDetailInfoIOList.add(projectLanguageDetailInfoIO);

	}

	projectDetails.setProjectInfoDetails(projectDetailCounts);
	projectDetails.setProjectLanguageDetails(languageDetailInfoIOList);

	return projectDetails;

    }

    private static TmgrSearchFilter createSearchFilter(Long projectId, Set<String> languageIds, int synonymNumber) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setTargetLanguages(new ArrayList<>(languageIds));
	filter.setProjectIds(Collections.singletonList(projectId));
	filter.addLanguageResultField(true, synonymNumber, languageIds.toArray(new String[languageIds.size()]));
	filter.setPageable(new TmgrPageRequest(AbstractPageRequest.DEFAULT_PAGE, 1, null));
	return filter;
    }

    private static long getNumberOfTermEntries(TmgrSearchFilter filter, long projectId,
	    ITmgrGlossarySearcher searcher) {
	return searcher.getNumberOfTermEntries(filter).get(projectId);
    }

    private static FacetTermCounts searchFacetTermCounts(TmgrSearchFilter filter, ITmgrGlossarySearcher searcher) {
	return searcher.searchFacetTermCounts(filter);

    }

}

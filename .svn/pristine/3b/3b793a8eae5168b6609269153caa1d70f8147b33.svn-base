package org.gs4tr.termmanager.persistence.solr.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation3.solr.ICloudHttpSolrClient;
import org.gs4tr.foundation3.solr.model.client.SearchRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.persistence.solr.util.RouteHelper;
import org.gs4tr.termmanager.persistence.solr.util.SolrQueryUtils;

class TmgrGlossarySearcherHelper {

    protected static void addQueries(SearchRequest request, TmgrSearchFilter filter) {
	if (filter.isImportSearch()) {
	    SolrQueryUtils.createExactMatchQuery(request, filter);
	} else {
	    SolrQueryUtils.createSearchQuery(request, filter);
	}
	if (!filter.isImportSearch() && !filter.isSyncSearch()) {
	    SolrQueryUtils.createChildFilterQuery(request, filter);
	}

	SolrQueryUtils.createParentFilterQuery(request, filter);

	if (filter.isHideBlanks()) {
	    SolrQueryUtils.createHideBlanksFilterQuery(request, filter);
	} else if (filter.isOnlyMissingTranslation()) {
	    SolrQueryUtils.createMissingTranslationFilterQuery(request, filter);
	}

	SolrQueryUtils.createResultFields(request, filter);

	SolrQueryUtils.addPage(request, filter);
	SolrQueryUtils.addSort(request, filter);
    }

    protected static void addRoutes(SearchRequest request, TmgrSearchFilter filter, ICloudHttpSolrClient client,
	    String collection) {
	List<Long> projectIds = filter.getProjectIds();
	if (CollectionUtils.isNotEmpty(projectIds)) {
	    Set<String> routes = RouteHelper.getRoutes(client, collection, projectIds);
	    request.setRoutes(routes);
	} else if (CollectionUtils.isNotEmpty(filter.getRoutes())) {
	    request.setRoutes(filter.getRoutes());
	}
    }

    protected static Set<String> getLanguagesId(TmgrSearchFilter filter) {
	Set<String> languageIds = new HashSet<String>();

	String sourceLanguage = filter.getSourceLanguage();
	if (StringUtils.isNotEmpty(sourceLanguage)) {
	    languageIds.add(sourceLanguage);
	}

	List<String> targetLanguages = filter.getTargetLanguages();
	if (CollectionUtils.isNotEmpty(targetLanguages)) {
	    languageIds.addAll(targetLanguages);
	}
	return languageIds;
    }
}

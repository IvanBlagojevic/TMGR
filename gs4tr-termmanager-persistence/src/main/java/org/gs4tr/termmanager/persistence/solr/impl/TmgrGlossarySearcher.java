package org.gs4tr.termmanager.persistence.solr.impl;

import static java.util.stream.Collectors.joining;
import static org.gs4tr.termmanager.persistence.solr.impl.TmgrGlossarySearcherHelper.addQueries;
import static org.gs4tr.termmanager.persistence.solr.impl.TmgrGlossarySearcherHelper.addRoutes;
import static org.gs4tr.termmanager.persistence.solr.impl.TmgrGlossarySearcherHelper.getLanguagesId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.gs4tr.foundation3.solr.ICloudHttpSolrClient;
import org.gs4tr.foundation3.solr.model.client.SearchRequest;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.solr.TmgrSolrConnector;
import org.gs4tr.termmanager.persistence.solr.converter.PivotFieldConverter;
import org.gs4tr.termmanager.persistence.solr.converter.TermEntryConverter;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.query.IPageable;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.persistence.solr.util.RouteHelper;
import org.gs4tr.termmanager.persistence.solr.util.SolrQueryUtils;
import org.gs4tr.termmanager.solr.plugin.utils.SolrChildDocFileds;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;

public class TmgrGlossarySearcher implements ITmgrGlossarySearcher {

    private static final Log LOGGER = LogFactory.getLog(TmgrGlossarySearcher.class);

    private ICloudHttpSolrClient _client;

    private String _collection;

    public TmgrGlossarySearcher(TmgrSolrConnector connector) {
	_client = connector.getClient();
	_collection = connector.getConnectionInfo().getName();
    }

    @Override
    public Page<TermEntry> concordanceSearch(TmgrSearchFilter filter) throws TmException {

	ICloudHttpSolrClient client = getClient();

	String collection = getCollection();
	SearchRequest request = new SearchRequest(collection);
	addRoutes(request, filter, client, collection);
	addQueries(request, filter);

	Set<String> languageIds = getLanguagesId(filter);

	QueryResponse response = client.search(request);

	long start = System.currentTimeMillis();
	List<TermEntry> termEntries = TermEntryConverter.convertToTermEntries(response.getResults(), languageIds,
		filter.isFetchDeleted());

	long end = System.currentTimeMillis();

	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace(String.format(Messages.getString("TmgrGlossarySearcher.0"), //$NON-NLS-1$
		    end - start));
	}

	int totalCount = (int) response.getResults().getNumFound();

	return new Page<>(totalCount, filter.getPageable().getOffset(), filter.getPageable().getPageSize(),
		termEntries);
    }

    @Override
    public Map<Long, Long> getNumberOfTermEntries(TmgrSearchFilter filter) {
	List<Long> projectIds = filter.getProjectIds();
	Validate.notEmpty(projectIds, "Parameter projectIds cannot be empty.");

	List<String> languageIds = filter.getTargetLanguages();
	Validate.notEmpty(languageIds, "Parameter languageIds cannot be empty.");

	ICloudHttpSolrClient client = getClient();
	String collection = getCollection();

	SearchRequest query = SolrQueryUtils.createProjectFacetQuery(projectIds, languageIds);
	query.setCollection(collection);

	Set<String> routes = RouteHelper.getRoutes(client, collection, projectIds);
	query.setRoutes(routes);

	query.addFacetField(SolrParentDocFields.PROJECT_ID_INDEX_STORE);

	IPageable pageable = filter.getPageable();

	// Maximum number of facets for a field that should be returned
	query.setFacetLimit(pageable.getPageSize());

	QueryResponse response = client.search(query);

	return PivotFieldConverter.convertToTermEntriesCount(projectIds, response);
    }

    @Override
    public long getNumberOfTerms(TmgrSearchFilter filter) {
	ICloudHttpSolrClient client = getClient();
	String collection = getCollection();

	SearchRequest query = new SearchRequest();
	query.setCollection(collection);

	List<Long> projectIds = filter.getProjectIds();
	if (CollectionUtils.isNotEmpty(projectIds)) {
	    Set<String> routes = RouteHelper.getRoutes(client, collection, projectIds);
	    query.setRoutes(routes);
	}

	query.setStart(0);
	query.setRows(0);

	SolrQueryUtils.createChildMainQuery(query, filter);

	SolrQueryUtils.createParentChildFilterQuery(query, filter);

	QueryResponse response = client.search(query);

	return response.getResults().getNumFound();
    }

    @Override
    public FacetTermCounts searchFacetTermCounts(TmgrSearchFilter filter) {
	List<Long> projectIds = filter.getProjectIds();
	Validate.notEmpty(projectIds, "Parameter projectIds cannot be empty.");

	List<String> languageIds = filter.getTargetLanguages();
	Validate.notEmpty(languageIds, "Parameter languageIds cannot be empty.");

	Long projectId = projectIds.get(0);
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("TmgrGlossarySearcher.3"), //$NON-NLS-1$
		    projectId, languageIds));
	}

	ICloudHttpSolrClient client = getClient();
	String collection = getCollection();

	SearchRequest query = SolrQueryUtils.createFacetTermCountQuery(projectId, languageIds);
	query.setCollection(collection);

	String route = RouteHelper.getRoute(client, collection, projectId);
	query.setRoute(route);

	List<String> pivotFields = new ArrayList<String>(2);
	pivotFields.add(SolrChildDocFileds.LANGUAGE_ID_INDEX);
	pivotFields.add(SolrChildDocFileds.STATUS_INDEX);
	query.addFacetPivotField(pivotFields.stream().collect(joining(",")));

	SolrQueryUtils.createResultFields(query, filter);

	query.setFacetLimit(-1);

	QueryResponse response = client.search(query);

	NamedList<List<PivotField>> namedList = response.getFacetPivot();

	return PivotFieldConverter.convertToFacetTermCounts(namedList, languageIds);
    }

    @Override
    public Page<TermEntry> segmentSearch(TmgrSearchFilter filter) throws TmException {
	ICloudHttpSolrClient client = getClient();
	String collection = getCollection();

	SearchRequest request = new SearchRequest(collection);
	addRoutes(request, filter, client, collection);
	addQueries(request, filter);

	QueryResponse response = client.search(request);

	Set<String> languageIds = getLanguagesId(filter);

	long start = System.currentTimeMillis();
	List<TermEntry> termEntries = TermEntryConverter.convertToTermEntries(response.getResults(), languageIds,
		filter.isFetchDeleted());

	long end = System.currentTimeMillis();

	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace(String.format(Messages.getString("TmgrGlossarySearcher.0"), //$NON-NLS-1$
		    end - start));
	}

	int totalCount = (int) response.getResults().getNumFound();

	return new Page<>(totalCount, filter.getPageable().getOffset(), filter.getPageable().getPageSize(),
		termEntries);
    }

    private ICloudHttpSolrClient getClient() {
	return _client;
    }

    private String getCollection() {
	return _collection;
    }
}

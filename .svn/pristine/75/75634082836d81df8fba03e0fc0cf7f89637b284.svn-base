package org.gs4tr.termmanager.persistence.solr.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper;
import org.gs4tr.foundation3.solr.ICloudHttpSolrClient;
import org.gs4tr.foundation3.solr.ISolrResultIterator;
import org.gs4tr.foundation3.solr.model.client.SearchRequest;
import org.gs4tr.foundation3.solr.model.client.SearchResponse;
import org.gs4tr.foundation3.solr.util.SolrQueryBuilder;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.solr.TmgrSolrConnector;
import org.gs4tr.termmanager.persistence.solr.converter.TermConverter;
import org.gs4tr.termmanager.persistence.solr.converter.TermEntryConverter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.persistence.solr.util.RouteHelper;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.termmanager.persistence.solr.util.SolrQueryUtils;
import org.gs4tr.tm3.api.TmException;

public class TmgrGlossaryBrowser implements ITmgrGlossaryBrowser {

    private static final Log LOGGER = LogFactory.getLog(TmgrGlossaryBrowser.class);

    private final SolrGlossaryAdapter _adapter;

    private final ICloudHttpSolrClient _client;

    private final String _collection;

    public TmgrGlossaryBrowser(TmgrSolrConnector connector) {
	_client = connector.getClient();
	_adapter = connector.getAdapter();
	_collection = connector.getConnectionInfo().getName();
    }

    @Override
    public List<TermEntry> browse(TmgrSearchFilter filter) {
	SearchRequest query = new SearchRequest();

	SolrQueryUtils.createSearchQuery(query, filter);

	SolrQueryUtils.addSort(query, filter);

	SolrQueryUtils.createChildFilterQuery(query, filter);
	SolrQueryUtils.createParentFilterQuery(query, filter);
	SolrQueryUtils.createResultFields(query, filter);

	return browseInternal(query, filter);
    }

    @Override
    public List<TermEntry> findAll() {
	SearchRequest query = new SearchRequest();

	SolrQueryUtils.createFindAllQuery(query);
	SolrQueryUtils.createParentFilterQuery(query);

	return browseInternal(query, new TmgrSearchFilter());
    }

    @Override
    public TermEntry findById(String id, Long projectId, String... flParameters) throws TmException {
	if (StringUtils.isBlank(id)) {
	    return null;
	}

	SolrQueryBuilder builder = SolrQueryBuilder.newInstance();

	ICloudHttpSolrClient client = getClient();

	String collection = getCollection();

	SearchRequest query = new SearchRequest();
	query.setCollection(collection);

	if (projectId != null) {
	    String route = RouteHelper.getRoute(client, collection, projectId);
	    query.setRoute(route);
	}

	SolrQueryUtils.createIdQuery(query, builder, id);

	SolrQueryUtils.createResultFields(query, flParameters);

	SearchResponse response = client.search(query);

	List<TermEntry> termEntries = TermEntryConverter.convertToTermEntries(response.getResults());

	if (CollectionUtils.isEmpty(termEntries)) {
	    return null;
	} else if (termEntries.size() > 1) {
	    throw new TmException(Messages.getString("TmgrGlossaryBrowser.2")); //$NON-NLS-1$
	}

	return termEntries.get(0);
    }

    @Override
    public List<TermEntry> findByIds(Collection<String> ids, List<Long> projectIds) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(projectIds);

	List<TermEntry> result = new ArrayList<>();

	ChunkedExecutionHelper.executeChuncked(ids, chunk -> {
	    SolrQueryBuilder builder = SolrQueryBuilder.newInstance();

	    SearchRequest query = new SearchRequest();
	    SolrQueryUtils.createIdsQuery(query, builder, chunk);

	    List<TermEntry> chunkedResultList = browseInternal(query, filter);
	    result.addAll(chunkedResultList);
	}, getAdapter().getExportBatchSize());

	return result;
    }

    @Override
    public List<TermEntry> findByProjectId(Long projectId) {
	SearchRequest query = new SearchRequest();

	SolrQueryUtils.createFindAllQuery(query);
	SolrQueryUtils.createProjectIdFilterQuery(query, projectId);

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);

	return browseInternal(query, filter);
    }

    @Override
    public TermEntry findByTermId(String termId, Long projectId) throws TmException {
	ICloudHttpSolrClient client = getClient();

	String collection = getCollection();

	SearchRequest query = new SearchRequest();
	query.setCollection(collection);

	if (projectId != null) {
	    String route = RouteHelper.getRoute(client, collection, projectId);
	    query.setRoute(route);
	}

	SolrQueryBuilder builder = SolrQueryUtils.makeBlockJoinParentSolrQueryBuilder();

	SolrQueryUtils.createIdQuery(query, builder, termId);

	SearchResponse response = client.search(query);

	List<TermEntry> termEntries = TermEntryConverter.convertToTermEntries(response.getResults());
	if (CollectionUtils.isEmpty(termEntries)) {
	    return null;
	} else if (termEntries.size() > 1) {
	    throw new TmException(Messages.getString("TmgrGlossaryBrowser.0")); //$NON-NLS-1$
	}

	return termEntries.get(0);
    }

    @Override
    public List<TermEntry> findByTermIds(Collection<String> termIds, Long projectId) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);

	List<TermEntry> resultList = new ArrayList<>();

	ChunkedExecutionHelper.executeChuncked(termIds, chunk -> {
	    SolrQueryBuilder builder = SolrQueryUtils.makeBlockJoinParentSolrQueryBuilder();

	    SearchRequest query = new SearchRequest();

	    SolrQueryUtils.createIdsQuery(query, builder, chunk);

	    List<TermEntry> result = browseInternal(query, filter);
	    resultList.addAll(result);
	}, getAdapter().getExportBatchSize());

	return resultList;
    }

    @Override
    public Term findTermById(String termId, Long projectId) throws TmException {
	ICloudHttpSolrClient client = getClient();

	String collection = getCollection();

	SearchRequest query = new SearchRequest();
	query.setCollection(collection);

	if (projectId != null) {
	    String route = RouteHelper.getRoute(client, collection, projectId);
	    query.setRoute(route);
	}

	SolrQueryBuilder builder = SolrQueryUtils.makeBlockJoinParentSolrQueryBuilder();

	SolrQueryUtils.createIdQuery(query, builder, termId);

	SearchResponse response = getClient().search(query);

	List<TermEntry> entries = TermEntryConverter.convertToTermEntries(response.getResults());

	if (CollectionUtils.isEmpty(entries)) {
	    return null;
	} else if (entries.size() > 1) {
	    throw new TmException(Messages.getString("TmgrGlossaryBrowser.1")); //$NON-NLS-1$
	}

	return TermConverter.buildTermFromSolrDocument(termId, entries.get(0));
    }

    @Override
    public List<Term> findTermsByIds(Collection<String> termIds, List<Long> projectIds) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(projectIds);

	List<Term> resultList = new ArrayList<>();

	ChunkedExecutionHelper.executeChuncked(termIds, chunk -> {
	    SolrQueryBuilder builder = SolrQueryUtils.makeBlockJoinParentSolrQueryBuilder();

	    SearchRequest query = new SearchRequest();

	    SolrQueryUtils.createIdsQuery(query, builder, chunk);

	    resultList.addAll(browseTermsInternal(chunk, query, filter));
	}, getAdapter().getExportBatchSize());

	return resultList;
    }

    @Override
    public List<Term> findTermsByLanguageIds(Long projectId, List<String> langIds, int synonymNumber) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);
	filter.addLanguageResultField(false, synonymNumber, langIds.toArray(new String[langIds.size()]));

	Set<String> fields = filter.getResultFields();

	SearchRequest query = new SearchRequest();

	query.setFields(fields.toArray(new String[fields.size()]));

	SolrQueryUtils.createProjectIdFilterQuery(query, projectId);

	return browseTermsInternal(null, query, filter);
    }

    @Override
    public List<Term> findTermsByProjectId(Long projectId) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);

	SearchRequest query = new SearchRequest();

	SolrQueryUtils.createFindAllQuery(query);
	SolrQueryUtils.createProjectIdFilterQuery(query, projectId);

	return browseTermsInternal(null, query, filter);
    }

    @Override
    public Long getNumberOfTermEntriesOnProject(TmgrSearchFilter filter) {
	ICloudHttpSolrClient client = getClient();
	String collection = getCollection();

	SearchRequest query = new SearchRequest();
	query.setCollection(collection);

	Long projectId = filter.getProjectIds().get(0);
	if (projectId != null) {
	    String route = RouteHelper.getRoute(client, collection, projectId);
	    query.setRoutes(route);
	}

	query.setCollection(collection);
	query.setStart(0);
	query.setRows(0);

	SolrQueryUtils.createFindAllQuery(query);
	SolrQueryUtils.createProjectIdFilterQuery(query, projectId);

	String languageId = filter.getSourceLanguage();
	if (StringUtils.isNotBlank(languageId)) {
	    SolrQueryUtils.createLanguageIdFilterQuery(query, languageId);
	}

	QueryResponse response = client.search(query);

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("TmgrGlossaryBrowser.4"), //$NON-NLS-1$
		    projectId, response.getQTime()));
	}
	return response.getResults().getNumFound();
    }

    private List<TermEntry> browseInternal(SearchRequest query, TmgrSearchFilter filter) {
	ICloudHttpSolrClient client = getClient();

	String collection = getCollection();

	List<Long> projectIds = filter.getProjectIds();
	if (CollectionUtils.isNotEmpty(projectIds)) {
	    Set<String> routes = RouteHelper.getRoutes(client, collection, projectIds);
	    query.setRoutes(routes);
	}

	int exportBatchSize = getAdapter().getExportBatchSize();
	query.setRows(exportBatchSize);
	query.setCollection(collection);

	List<TermEntry> termEntries = new ArrayList<>();
	ISolrResultIterator iterator = client.iterator(query);
	while (iterator.hasNext()) {
	    QueryResponse response = iterator.next();
	    List<TermEntry> entries = TermEntryConverter.convertToTermEntries(response.getResults());
	    termEntries.addAll(entries);
	}

	return termEntries;
    }

    private List<Term> browseTermsInternal(Collection<String> termIds, SearchRequest query, TmgrSearchFilter filter) {
	ICloudHttpSolrClient client = getClient();

	String collection = getCollection();

	List<Long> projectIds = filter.getProjectIds();
	if (CollectionUtils.isNotEmpty(projectIds)) {
	    Set<String> routes = RouteHelper.getRoutes(client, collection, projectIds);
	    query.setRoutes(routes);
	}

	int exportBatchSize = getAdapter().getExportBatchSize();
	query.setRows(exportBatchSize);
	query.setCollection(collection);

	List<Term> terms = new ArrayList<>();
	ISolrResultIterator iterator = client.iterator(query);
	while (iterator.hasNext()) {
	    QueryResponse response = iterator.next();

	    List<TermEntry> entries = TermEntryConverter.convertToTermEntries(response.getResults());
	    terms.addAll(TermConverter.buildTermsFromSolrDocuments(termIds, entries));
	}

	return terms;
    }

    private SolrGlossaryAdapter getAdapter() {
	return _adapter;
    }

    private ICloudHttpSolrClient getClient() {
	return _client;
    }

    private String getCollection() {
	return _collection;
    }
}

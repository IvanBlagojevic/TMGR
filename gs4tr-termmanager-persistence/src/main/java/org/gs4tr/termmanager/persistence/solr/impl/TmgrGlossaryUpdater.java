package org.gs4tr.termmanager.persistence.solr.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.UpdateParams;
import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper;
import org.gs4tr.foundation3.solr.ICloudHttpSolrClient;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.foundation3.solr.model.client.AddRequest;
import org.gs4tr.foundation3.solr.model.client.AddResponse;
import org.gs4tr.foundation3.solr.model.client.DeleteRequest;
import org.gs4tr.foundation3.solr.model.update.CommitOption;
import org.gs4tr.foundation3.solr.model.update.RevisionCommand;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.TmgrSolrConnector;
import org.gs4tr.termmanager.persistence.solr.util.RouteHelper;
import org.gs4tr.termmanager.persistence.solr.util.SolrErrorHandler;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.termmanager.persistence.solr.util.TmgrUpdateConstants;
import org.gs4tr.tm3.api.TmException;

public class TmgrGlossaryUpdater implements ITmgrGlossaryUpdater {

    private static final Log LOGGER = LogFactory.getLog(TmgrGlossaryUpdater.class);

    private static final String TMGR_REBUILD_CHAIN = "tmgrRebuildChain"; //$NON-NLS-1$

    private static final String TMGR_UPDATE_CHAIN = "tmgrUpdateChain"; //$NON-NLS-1$

    private final SolrGlossaryAdapter _adapter;

    private final ICloudHttpSolrClient _client;

    private final String _collection;

    public TmgrGlossaryUpdater(TmgrSolrConnector connector) {
	_client = connector.getClient();
	_adapter = connector.getAdapter();
	_collection = connector.getConnectionInfo().getName();
    }

    @Override
    public void delete(TermEntry entity) throws TmException {
	if (entity == null) {
	    return;
	}

	deleteByIdInternal(entity.getUuId(), entity.getProjectId());
    }

    @Override
    public void deleteAll() throws TmException {
	String collection = getCollection();

	ICloudHttpSolrClient client = getClient();

	List<String> routes = client.routes(collection);
	if (CollectionUtils.isNotEmpty(routes)) {
	    for (String route : routes) {
		DeleteRequest req = new DeleteRequest();
		req.setCollection(collection);
		req.setRoute(route);
		req.addQuery(SolrConstants.FIND_ALL_QUERY);

		client.delete(req);
	    }
	}
    }

    @Override
    public void deleteByProjects(List<Long> projectIds) throws TmException {
	if (CollectionUtils.isEmpty(projectIds)) {
	    return;
	}

	String collection = getCollection();

	ICloudHttpSolrClient client = getClient();

	for (Long projectId : projectIds) {
	    String route = RouteHelper.getRoute(client, collection, projectId);

	    DeleteRequest req = new DeleteRequest();
	    req.setCollection(collection);
	    req.setRoute(route);
	    req.addQuery(String.format("projectId_LONG:%d", projectId));

	    client.delete(req);
	}
    }

    @Override
    public boolean revertLastHistory(Long projectId, TermEntry entity) throws TmException {
	SolrInputDocument doc = getAdapter().buildInputDocumentFromTermEntry(entity);

	Validate.notNull(projectId, Messages.getString("TmgrGlossaryUpdater.0")); //$NON-NLS-1$

	ICloudHttpSolrClient client = getClient();

	String collection = getCollection();

	String route = RouteHelper.getRoute(client, collection, projectId);

	AddRequest req = new AddRequest();
	req.setCollection(collection);
	req.set(UpdateParams.UPDATE_CHAIN, TMGR_UPDATE_CHAIN);
	req.addDoc(doc);
	req.setRoute(route);
	req.setCommand(RevisionCommand.ADD);
	req.setRevision(0);
	req.setReturnFields(TmgrUpdateConstants.FL_FIELDS);

	AddResponse res = client.add(req);
	SolrErrorHandler.handleAddResponse(res, req.getDocSize());

	return true;
    }

    @Override
    public void save(List<TermEntry> entities) throws TmException {
	saveOrUpdate(entities);
    }

    @Override
    public void save(TermEntry entity) throws TmException {
	saveOrUpdate(Collections.singletonList(entity));
    }

    /**
     * When command is not set.Context initializer processor dose not open real time
     * searchers.
     **/
    // re-build index
    @Override
    public void saveSolrDocs(String route, CommitOption commitOption, List<SolrInputDocument> docs) throws TmException {
	if (docs == null) {
	    return;
	}

	String collection = getCollection();

	AddRequest req = new AddRequest();
	req.setCollection(collection);
	req.setRoute(route);
	req.set(UpdateParams.UPDATE_CHAIN, TMGR_REBUILD_CHAIN);
	req.setOption(commitOption);
	req.addDocs(docs);

	AddResponse res = getClient().add(req);
	SolrErrorHandler.handleAddResponse(res, req.getDocSize());
    }

    @Override
    public void update(List<TermEntry> entities) throws TmException {
	saveOrUpdate(entities);
    }

    @Override
    public void update(TermEntry entity) throws TmException {
	if (entity.getUuId() == null) {
	    throw new TmException(Messages.getString("TmgrGlossaryUpdater.1")); //$NON-NLS-1$
	}
	saveOrUpdate(Collections.singletonList(entity));
    }

    private void deleteByIdInternal(String id, Long projectId) throws TmException {
	if (id == null) {
	    throw new TmException(Messages.getString("TmgrGlossaryUpdater.2")); //$NON-NLS-1$
	}

	Validate.notNull(projectId, Messages.getString("TmgrGlossaryUpdater.7")); //$NON-NLS-1$

	String collection = getCollection();

	ICloudHttpSolrClient client = getClient();

	String route = RouteHelper.getRoute(client, collection, projectId);

	DeleteRequest req = new DeleteRequest();
	req.setCollection(collection);
	req.addId(id);
	req.setRoute(route);

	client.delete(req);
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

    private Map<String, AddRequest> groupRequestByRoute(List<TermEntry> entries) {
	SolrGlossaryAdapter adapter = getAdapter();

	ICloudHttpSolrClient client = getClient();
	String collection = getCollection();

	Map<String, AddRequest> grouped = new HashMap<>();

	for (TermEntry entity : entries) {
	    Long projectId = entity.getProjectId();

	    Validate.notNull(projectId, Messages.getString("TmgrGlossaryUpdater.6")); //$NON-NLS-1$

	    String route = RouteHelper.getRoute(client, collection, projectId);

	    AddRequest req = grouped.get(route);
	    if (req == null) {
		req = new AddRequest();
		req.setCollection(collection);
		req.set(UpdateParams.UPDATE_CHAIN, TMGR_UPDATE_CHAIN);
		req.setRoute(route);
		req.setRevision(0);
		req.setCommand(RevisionCommand.ADD);
		grouped.put(route, req);
	    }

	    SolrInputDocument doc = adapter.buildInputDocumentFromTermEntry(entity);

	    req.addDoc(doc);
	}
	return grouped;
    }

    private void saveOrUpdate(List<TermEntry> entities) throws TmException {
	if (CollectionUtils.isEmpty(entities)) {
	    return;
	}

	try {
	    ChunkedExecutionHelper.executeChuncked(entities, chunk -> {
		/*
		 * Because of solr routing, we must send one request for each unique projectId.
		 */
		Map<String, AddRequest> grouped = groupRequestByRoute(chunk);

		for (Entry<String, AddRequest> entrySet : grouped.entrySet()) {
		    AddRequest req = entrySet.getValue();

		    Iterator<SolrInputDocument> docIterator = req.getDocIterator();

		    if (docIterator.hasNext()) {
			AddResponse res = getClient().add(req);
			try {
			    SolrErrorHandler.handleAddResponse(res, req.getDocSize());
			} catch (TmException e) {
			    throw new RuntimeException(e.getMessage(), e);
			}
		    }
		}
	    }, getAdapter().getExportBatchSize());
	} catch (Exception e) {
	    throw new TmException(e.getMessage(), e);
	}
    }
}

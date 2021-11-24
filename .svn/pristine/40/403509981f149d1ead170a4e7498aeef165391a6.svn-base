package org.gs4tr.termmanager.persistence.solr;

import org.gs4tr.foundation3.solr.impl.CloudHttpSolrClient;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.impl.TmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.solr.impl.TmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.solr.impl.TmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.tm3.api.ConnectionInfo;
import org.gs4tr.tm3.api.TmException;

public class TmgrSolrConnector implements ITmgrGlossaryConnector {

    private SolrGlossaryAdapter _adapter;

    private CloudHttpSolrClient _client;

    private ConnectionInfo _connectionInfo;

    public TmgrSolrConnector(TmgrConnectionInfo info, CloudHttpSolrClient client) {
	_client = client;
	_connectionInfo = info;
	_adapter = getAdapterInstance(info);
    }

    @Override
    public void connect() throws TmException {
	// nothing to do
    }

    @Override
    public void disconnect() {
	setAdapter(null);
    }

    public SolrGlossaryAdapter getAdapter() {
	return _adapter;
    }

    @Override
    public CloudHttpSolrClient getClient() {
	return _client;
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
	return _connectionInfo;
    }

    @Override
    public ITmgrGlossaryBrowser getTmgrBrowser() throws TmException {
	return new TmgrGlossaryBrowser(this);
    }

    @Override
    public ITmgrGlossarySearcher getTmgrSearcher() throws TmException {
	return new TmgrGlossarySearcher(this);
    }

    @Override
    public ITmgrGlossaryUpdater getTmgrUpdater() throws TmException {
	return new TmgrGlossaryUpdater(this);
    }

    public void setAdapter(SolrGlossaryAdapter adapter) {
	_adapter = adapter;
    }

    private SolrGlossaryAdapter getAdapterInstance(TmgrConnectionInfo info) {
	int batchSize = info.getBatchSize();
	SolrGlossaryAdapter adapter = new SolrGlossaryAdapter();
	adapter.setExportBatchSize(batchSize);
	adapter.setImportBatchSize(batchSize);
	return adapter;
    }
}

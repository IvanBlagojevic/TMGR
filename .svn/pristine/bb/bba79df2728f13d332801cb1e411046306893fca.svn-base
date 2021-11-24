package org.gs4tr.termmanager.service.mocking;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation3.httpclient.impl.HttpClientFactory;
import org.gs4tr.foundation3.httpclient.model.HttpClientConfiguration;
import org.gs4tr.foundation3.solr.impl.CloudHttpSolrClient;
import org.gs4tr.foundation3.solr.impl.SolrClientFactory;
import org.gs4tr.foundation3.solr.model.CloudHttpConfiguration;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.service.DbSubmissionTermEntryService;
import org.gs4tr.termmanager.service.DbTermEntryService;
import org.gs4tr.termmanager.service.solr.restore.IRestoreProcessorV2;
import org.gs4tr.termmanager.service.solr.restore.RecodeOrCloneTermsProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RestoreProcessorV2Test extends AbstractServiceTest {

    @Autowired
    private DbSubmissionTermEntryService _dbSubmissionTermEntryService;

    @Autowired
    private DbTermEntryService _dbTermEntryService;

    @Autowired
    private IRestoreProcessorV2 _iRestoreProcessorV2;

    @Autowired
    private ITmgrGlossaryConnector _iTmgrGlossaryConnector;

    @Autowired
    private ITmgrGlossaryUpdater _iTmgrGlossaryUpdater;

    @Autowired
    private RecodeOrCloneTermsProcessor _recodeOrCloneTermsProcessor;

    private static CloudHttpSolrClient _solrClient;

    private static String _submissionCollection;

    private static ITmgrGlossaryConnector _submissionConnector;

    private static String _zkHosts;

    /*
     * TERII-6044 Cloning and recoding | Rebuild index is done only for the projects
     * that are used for cloning and recoding languages
     */
    @Test
    @SuppressWarnings("unchecked")
    public void rebuildIndexForEntireDatabaseTest() throws Exception {

	when(getRecodeOrCloneTermsProcessor().recodeOrCloneTerms())
		.thenReturn(new HashSet(Collections.singletonList("TES000001")));

	PagedList<DbTermEntry> page = new PagedList<>();

	PagedList<DbSubmissionTermEntry> pageSubmission = new PagedList<>();

	when(getDbTermEntryService().getDbTermEntries(any(), any())).thenReturn(page);

	when(getDbSubmissionTermEntryService().getDbSubmissionTermEntries(any(), any())).thenReturn(pageSubmission);

	CloudHttpConfiguration config = new CloudHttpConfiguration();

	_zkHosts = System.getProperty("solr.zkhosts", "localhost:9983");

	_solrClient = createSolrClient();

	config.setZkHosts(_zkHosts);

	HttpClientConfiguration httpClientConfiguration = new HttpClientConfiguration();
	CloseableHttpClient httpClient = HttpClientFactory.getInstance().createHttpClient(httpClientConfiguration);

	config.setHttpClient(httpClient);

	SolrClientFactory factory = new SolrClientFactory();
	CloudHttpSolrClient cloudHttpSolrClient = (CloudHttpSolrClient) factory.createCloudHttp(config);

	when(getiTmgrGlossaryConnector().getClient()).thenReturn(cloudHttpSolrClient);

	// Perform restore operation
	getiRestoreProcessorV2().restore();

	/*
	 * deleteAll() method should be invoked two times because there are no specified
	 * project short codes in index.project.codes.for.reindexed property. Rebuild
	 * index on all projects.
	 */
	verify(getiTmgrGlossaryUpdater(), times(2)).deleteAll();

    }

    private static CloudHttpSolrClient createSolrClient() {
	if (_solrClient == null) {
	    HttpClientConfiguration httpClientConfiguration = new HttpClientConfiguration();
	    CloseableHttpClient httpClient = HttpClientFactory.getInstance().createHttpClient(httpClientConfiguration);

	    CloudHttpConfiguration config = new CloudHttpConfiguration();
	    config.setZkHosts(_zkHosts);
	    config.setHttpClient(httpClient);

	    SolrClientFactory factory = new SolrClientFactory();
	    _solrClient = (CloudHttpSolrClient) factory.createCloudHttp(config);
	}
	return _solrClient;
    }

    private DbSubmissionTermEntryService getDbSubmissionTermEntryService() {
	return _dbSubmissionTermEntryService;
    }

    private DbTermEntryService getDbTermEntryService() {
	return _dbTermEntryService;
    }

    private RecodeOrCloneTermsProcessor getRecodeOrCloneTermsProcessor() {
	return _recodeOrCloneTermsProcessor;
    }

    private IRestoreProcessorV2 getiRestoreProcessorV2() {
	return _iRestoreProcessorV2;
    }

    private ITmgrGlossaryConnector getiTmgrGlossaryConnector() {
	return _iTmgrGlossaryConnector;
    }

    private ITmgrGlossaryUpdater getiTmgrGlossaryUpdater() {
	return _iTmgrGlossaryUpdater;
    }
}

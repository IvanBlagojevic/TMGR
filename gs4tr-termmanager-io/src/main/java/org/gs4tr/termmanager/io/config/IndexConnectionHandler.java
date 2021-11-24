package org.gs4tr.termmanager.io.config;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation3.solr.impl.CloudHttpSolrClient;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.solr.TmgrConnectionInfo;
import org.gs4tr.termmanager.persistence.solr.TmgrSolrConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IndexConnectionHandler {

    private static final Log LOGGER = LogFactory.getLog(IndexConnectionHandler.class);

    @Value("${index.batchSize:500}")
    private Integer _batchSize;

    private ITmgrGlossaryConnector _connector;

    @Autowired
    @Qualifier("indexClient")
    private CloudHttpSolrClient _indexClient;

    public ITmgrGlossaryConnector connect(String collection) {
	try {
	    TmgrConnectionInfo info = new TmgrConnectionInfo();
	    info.setBatchSize(getBatchSize());
	    info.setCollectionName(collection);
	    info.setName(collection);

	    _connector = new TmgrSolrConnector(info, getIndexClient());
	    _connector.connect();
	} catch (Exception e) {
	    throw new UserException("Unable to load term list. Contact administrator.", e);
	}

	LogHelper.trace(LOGGER, String.format("Created new connection %s", _connector.getConnectionInfo().getName()));

	return _connector;
    }

    public void disconnect() {
	if (Objects.nonNull(_connector)) {
	    _connector.disconnect();
	    _connector = null;
	}
    }

    private Integer getBatchSize() {
	return _batchSize;
    }

    private CloudHttpSolrClient getIndexClient() {
	return _indexClient;
    }
}

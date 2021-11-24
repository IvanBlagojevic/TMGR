package org.gs4tr.termmanager.service.solr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation3.solr.impl.CloudHttpSolrClient;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.solr.TmgrConnectionInfo;
import org.gs4tr.termmanager.persistence.solr.TmgrSolrConnector;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("glossaryConnectionManager")
public class GlossaryConnectionManager implements InitializingBean, DisposableBean {

    private static final Log LOGGER = LogFactory.getLog(GlossaryConnectionManager.class);

    @Value("${index.batchSize:100}")
    private Integer _batchSize;

    private ITmgrGlossaryConnector _connector;

    @Autowired
    @Qualifier("solrClient")
    private CloudHttpSolrClient _solrClient;

    @Override
    public void afterPropertiesSet() throws Exception {
	closeConnection();
    }

    @Override
    public void destroy() throws Exception {
	closeConnection();
    }

    public Integer getBatchSize() {
	return _batchSize;
    }

    public ITmgrGlossaryConnector getConnector(String collection) {
	return connectInternal(collection);
    }

    private void closeConnection() {
	if (_connector != null) {
	    _connector.disconnect();
	    _connector = null;
	}
    }

    private ITmgrGlossaryConnector connectInternal(String collection) {
	try {
	    CloudHttpSolrClient client = getSolrClient();
	    TmgrConnectionInfo connectionInfo = createConnectionInfo(collection);
	    _connector = new TmgrSolrConnector(connectionInfo, client);
	    _connector.connect();
	} catch (Exception e) {
	    throw new UserException(Messages.getString("GlossaryConnectionManager.3"), //$NON-NLS-1$
		    Messages.getString("GlossaryConnectionManager.4"), e); //$NON-NLS-1$
	}

	LogHelper.trace(LOGGER, String.format(Messages.getString("GlossaryConnectionManager.5"), //$NON-NLS-1$
		_connector.getConnectionInfo().getName()));

	return _connector;
    }

    private TmgrConnectionInfo createConnectionInfo(String collection) {
	TmgrConnectionInfo info = new TmgrConnectionInfo();
	info.setBatchSize(getBatchSize());
	info.setCollectionName(collection);
	info.setName(collection);

	return info;
    }

    private CloudHttpSolrClient getSolrClient() {
	return _solrClient;
    }
}

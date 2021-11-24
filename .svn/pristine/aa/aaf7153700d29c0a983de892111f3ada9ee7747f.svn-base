package org.gs4tr.termmanager.persistence;

import org.gs4tr.foundation3.solr.impl.CloudHttpSolrClient;
import org.gs4tr.tm3.api.ConnectionInfo;
import org.gs4tr.tm3.api.TmException;

public interface ITmgrGlossaryConnector
	extends ITmgrGlossaryBrowserProvider, ITmgrGlossaryUpdaterProvider, ITmgrGlossarySearcherProvider {

    void connect() throws TmException;

    void disconnect();

    CloudHttpSolrClient getClient();

    ConnectionInfo getConnectionInfo();
}

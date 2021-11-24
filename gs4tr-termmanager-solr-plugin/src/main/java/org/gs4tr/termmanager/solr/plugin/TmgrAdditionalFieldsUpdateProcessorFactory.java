package org.gs4tr.termmanager.solr.plugin;

import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorFactory;

public class TmgrAdditionalFieldsUpdateProcessorFactory extends UpdateRequestProcessorFactory {

    @Override
    public UpdateRequestProcessor getInstance(SolrQueryRequest req, SolrQueryResponse rsp,
	    UpdateRequestProcessor next) {
	return new TmgrAdditionalFieldsUpdateProcessor(next);
    }
}

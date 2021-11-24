package org.gs4tr.termmanager.solr.plugin;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.foundation3.solr.util.SolrDocumentBuilder;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class TmgrPreUpdateProcessor extends UpdateRequestProcessor {

    public TmgrPreUpdateProcessor(UpdateRequestProcessor next) {
	super(next);
    }

    @Override
    public void processAdd(AddUpdateCommand cmd) throws IOException {
	SolrInputDocument doc = cmd.getSolrInputDocument();

	SolrDocumentBuilder builder = SolrDocumentBuilder.newInstance(doc);

	if (builder.getValue(SolrConstants.ID_FIELD) == null) {
	    builder.set(SolrConstants.ID_FIELD, UUID.randomUUID().toString());
	}

	if (builder.getValue(SolrParentDocFields.TYPE_INDEX) == null) {
	    builder.set(SolrParentDocFields.TYPE_INDEX, SolrParentDocFields.PARENT);
	}

	if (builder.getValue(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE) == null) {
	    builder.set(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, new Date().getTime());
	}

	cmd.solrDoc = builder.get();
	super.processAdd(cmd);
    }
}

package org.gs4tr.termmanager.solr.plugin;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.gs4tr.foundation3.solr.util.SolrDocumentBuilder;
import org.gs4tr.termmanager.solr.plugin.utils.ProcessorUtils;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class TmgrUserLatestChangesProcessor extends UpdateRequestProcessor {

    public TmgrUserLatestChangesProcessor(UpdateRequestProcessor next) {
	super(next);
    }

    @Override
    public void processAdd(AddUpdateCommand cmd) throws IOException {
	SolrInputDocument doc = cmd.getSolrInputDocument();

	SolrDocumentBuilder builder = SolrDocumentBuilder.newInstance(doc);

	Collection<Object> values = builder.getValues(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI);
	List<String> languageIds = SolrDocHelper.getStringValues(values);

	if (CollectionUtils.isNotEmpty(languageIds)) {
	    for (String languageId : languageIds) {
		removeUserLatestChange(builder, languageId);

		// synonyms
		Collection<String> idFieldNames = ProcessorUtils.getIdFields(doc, languageId);
		Set<Integer> nums = SolrDocHelper.extractNumbers(idFieldNames);
		for (Integer num : nums) {
		    String languageKey = languageId.concat(num.toString());
		    removeUserLatestChange(builder, languageKey);
		}
	    }
	}

	cmd.solrDoc = builder.get();
	super.processAdd(cmd);
    }

    private void removeUserLatestChange(SolrDocumentBuilder builder, String languageId) {
	String fieldName = SolrDocHelper.createDynamicFieldName(languageId, SolrParentDocFields.DYN_USER_LATEST_CHANGE_STORE);
	if (builder.hasValue(fieldName)) {
	    builder.remove(fieldName);
	}
    }
}

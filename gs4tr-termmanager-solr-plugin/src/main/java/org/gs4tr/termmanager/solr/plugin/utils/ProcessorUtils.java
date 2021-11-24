package org.gs4tr.termmanager.solr.plugin.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

public class ProcessorUtils {

    public static Collection<String> getIdFields(SolrInputDocument doc, String languageId) {
	String idField = SolrParentDocFields.DYN_TERM_ID_STORE;
	Set<String> idsFieldNames = new HashSet<String>();
	Collection<String> fieldNames = doc.getFieldNames();
	for (String fieldName : fieldNames) {
	    if (fieldName.startsWith(languageId) && fieldName.endsWith(idField)) {
		idsFieldNames.add(fieldName);
	    }
	}
	return idsFieldNames;
    }

}

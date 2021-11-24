package org.gs4tr.termmanager.solr.plugin;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.gs4tr.foundation3.solr.util.SolrDocumentBuilder;
import org.gs4tr.termmanager.solr.plugin.utils.ProcessorUtils;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class TmgrFieldsCorrectorProcessor extends UpdateRequestProcessor {

    public TmgrFieldsCorrectorProcessor(UpdateRequestProcessor next) {
	super(next);
    }

    @Override
    public void processAdd(AddUpdateCommand cmd) throws IOException {
	SolrInputDocument doc = cmd.getSolrInputDocument();

	SolrDocumentBuilder builder = SolrDocumentBuilder.newInstance(doc);

	Collection<Object> values = builder.getValues(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI);
	List<String> languageIds = SolrDocHelper.getStringValues(values);

	if (CollectionUtils.isNotEmpty(languageIds)) {
	    Map<String, Set<String>> deletedTermsMap = new HashMap<>();
	    for (String languageId : languageIds) {
		fixTermTextField(builder, languageId);
		fixTempTermTextField(builder, languageId);

		// main term, collect deleted term ids
		String fieldName = SolrDocHelper.createDynamicFieldName(languageId, SolrParentDocFields.DYN_DISABLED_STORE);
		Object fieldValue = doc.getFieldValue(fieldName);
		Boolean disabled = SolrDocHelper.getBooleanValue(fieldValue);
		if (disabled) {
		    collectDeletedTermIds(deletedTermsMap, builder, languageId, true);
		    clearDisabledTermFields(builder, languageId, true);
		}

		// synonyms, collect deleted term ids
		Collection<String> idFieldNames = ProcessorUtils.getIdFields(doc, languageId);
		Set<Integer> nums = SolrDocHelper.extractNumbers(idFieldNames);
		for (Integer num : nums) {
		    String languageKey = languageId.concat(num.toString());

		    fieldName = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_DISABLED_STORE);
		    fieldValue = doc.getFieldValue(fieldName);
		    disabled = SolrDocHelper.getBooleanValue(fieldValue);
		    if (disabled) {
			collectDeletedTermIds(deletedTermsMap, builder, languageKey, false);
			clearDisabledTermFields(builder, languageKey, false);
		    }
		}
	    }
	    addDeletedTermsField(builder, deletedTermsMap);
	}
	cmd.solrDoc = builder.get();
	super.processAdd(cmd);
    }

    private void addDeletedTermsField(SolrDocumentBuilder builder, Map<String, Set<String>> deletedTermsMap) {
	for (Entry<String, Set<String>> entry : deletedTermsMap.entrySet()) {
	    String languageId = entry.getKey();
	    Set<String> ids = entry.getValue();

	    String deletedTermsField = SolrDocHelper.createDynamicFieldName(languageId, SolrParentDocFields.DYN_DELETED_TERMS);
	    builder.add(deletedTermsField, ids);
	}
    }

    private void clearDisabledTermFields(SolrDocumentBuilder builder, String languageKey, boolean isFirst) {
	String languageCode = SolrDocHelper.extractLocaleCode(languageKey);

	// disabled
	String disabledField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_DISABLED_STORE);
	builder.setClear(disabledField);

	// id
	String dynTermId = isFirst ? SolrParentDocFields.DYN_TERM_ID_SORT : SolrParentDocFields.DYN_TERM_ID_STORE;
	String idField = SolrDocHelper.createDynamicFieldName(languageKey, dynTermId);
	builder.setClear(idField);

	// status
	String dynStatus = isFirst ? SolrParentDocFields.DYN_STATUS_SORT : SolrParentDocFields.DYN_STATUS_STORE;
	String statusField = SolrDocHelper.createDynamicFieldName(languageKey, dynStatus);
	builder.setClear(statusField);

	// status old
	String statusOldField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_STATUS_OLD_STORE);
	builder.setClear(statusOldField);

	// userCreated
	String dynUserCreated = isFirst ? SolrParentDocFields.DYN_USER_CREATED_SORT : SolrParentDocFields.DYN_USER_CREATED_STORE;
	String userCreatedField = SolrDocHelper.createDynamicFieldName(languageKey, dynUserCreated);
	builder.setClear(userCreatedField);

	// userModified
	String dynUserModified = isFirst ? SolrParentDocFields.DYN_USER_MODIFIED_SORT : SolrParentDocFields.DYN_USER_MODIFIED_STORE;
	String userModifiedField = SolrDocHelper.createDynamicFieldName(languageKey, dynUserModified);
	builder.setClear(userModifiedField);

	// dateCreated
	String dynDateCreated = isFirst ? SolrParentDocFields.DYN_DATE_CREATED_SORT : SolrParentDocFields.DYN_DATE_CREATED_STORE;
	String dateCreatedField = SolrDocHelper.createDynamicFieldName(languageKey, dynDateCreated);
	builder.setClear(dateCreatedField);

	// dateModified
	String dynDateModified = isFirst ? SolrParentDocFields.DYN_DATE_MODIFIED_SORT : SolrParentDocFields.DYN_DATE_MODIFIED_STORE;
	String dateModifiedField = SolrDocHelper.createDynamicFieldName(languageKey, dynDateModified);
	builder.setClear(dateModifiedField);

	// forbidden
	String forbiddenField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_FORBIDDEN_STORE);
	builder.setClear(forbiddenField);

	// userLatestChange
	String userLatestChangeField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_USER_LATEST_CHANGE_STORE);
	builder.setClear(userLatestChangeField);

	// termName
	String termNameField = isFirst
		? SolrDocHelper.createDynamicFieldName(languageCode, SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW)
		: SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TERM_NAME_STORE);
	builder.setClear(termNameField);

	// attributes
	String attributeField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE);
	builder.setClear(attributeField);

	// notes
	String noteField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_NOTE_MULTI_STORE);
	builder.setClear(noteField);

	// in translation as source
	String inTranslationAsSourceField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE);
	builder.setClear(inTranslationAsSourceField);

	if (builder.getValue(SolrParentDocFields.PARENT_ID_STORE) != null) {
	    clrearDisabledTermSubmissionFields(builder, languageKey, isFirst);
	}
    }

    private void clrearDisabledTermSubmissionFields(SolrDocumentBuilder builder, String languageKey, boolean isFirst) {
	// assignee
	String dynAssigneeField = SolrParentDocFields.DYN_ASSIGNEE_SORT;
	String assingeeField = SolrDocHelper.createDynamicFieldName(languageKey, dynAssigneeField);
	builder.setClear(assingeeField);

	// commited
	String dynCommitedField = SolrParentDocFields.DYN_COMMITED_STORE;
	String commentsField = SolrDocHelper.createDynamicFieldName(languageKey, dynCommitedField);
	builder.setClear(commentsField);

	// dateCompleted
	String dynDateCompletedField = isFirst ? SolrParentDocFields.DYN_DATE_COMPLETED_SORT
		: SolrParentDocFields.DYN_DATE_COMPLETED_STORE;
	String dateCompletedField = SolrDocHelper.createDynamicFieldName(languageKey, dynDateCompletedField);
	builder.setClear(dateCompletedField);

	// dateSubmitted
	String dynDateSubmittedField = isFirst ? SolrParentDocFields.DYN_DATE_SUBMITTED_SORT
		: SolrParentDocFields.DYN_DATE_SUBMITTED_STORE;
	String dateSubmittedField = SolrDocHelper.createDynamicFieldName(languageKey, dynDateSubmittedField);
	builder.setClear(dateSubmittedField);

	// submitter
	builder.setClear(SolrParentDocFields.SUBMITTER_SORT);

	// temp name
	String tempTermField = isFirst
		? SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW)
		: SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TEMP_TERM_NAME_STORE);
	builder.setClear(tempTermField);
    }

    private void collectDeletedTermIds(Map<String, Set<String>> deletedTermsMap, SolrDocumentBuilder builder,
	    String languageKey, boolean isFirst) {
	String dynTermId = isFirst ? SolrParentDocFields.DYN_TERM_ID_SORT : SolrParentDocFields.DYN_TERM_ID_STORE;
	String idField = SolrDocHelper.createDynamicFieldName(languageKey, dynTermId);
	Object item = builder.getValue(idField);

	String termId = SolrDocHelper.getStringValue(item);

	String languageId = SolrDocHelper.extractLocaleCode(languageKey);
	Set<String> ids = deletedTermsMap.computeIfAbsent(languageId, k -> new HashSet<>());
	ids.add(termId);
    }

    private void fixTempTermTextField(SolrDocumentBuilder builder, String languageId) {
	String tempTermFieldOld = SolrDocHelper.createDynamicFieldName(languageId,
		SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX);
	Object fieldValue = builder.getValue(tempTermFieldOld);
	if (fieldValue != null) {
	    String tempTermNameFieldNew = SolrDocHelper.createDynamicFieldName(languageId,
		    SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW);
	    builder.set(tempTermNameFieldNew, fieldValue);
	    builder.remove(tempTermFieldOld);
	}
    }

    private void fixTermTextField(SolrDocumentBuilder builder, String languageId) {
	String termNameFieldOld = SolrDocHelper.createDynamicFieldName(languageId,
		SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX);
	Object fieldValue = builder.getValue(termNameFieldOld);
	if (fieldValue != null) {
	    String termNameFieldNew = SolrDocHelper.createDynamicFieldName(languageId,
		    SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW);
	    builder.set(termNameFieldNew, fieldValue);
	    builder.remove(termNameFieldOld);
	}
    }
}
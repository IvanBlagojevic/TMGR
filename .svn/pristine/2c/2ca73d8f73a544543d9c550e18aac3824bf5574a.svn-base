package org.gs4tr.termmanager.solr.plugin;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.termmanager.solr.plugin.utils.ChecksumHelper;
import org.gs4tr.termmanager.solr.plugin.utils.ProcessorUtils;
import org.gs4tr.termmanager.solr.plugin.utils.SolrChildDocFileds;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

import com.google.common.base.CharMatcher;

public class TmgrAdditionalFieldsUpdateProcessor extends UpdateRequestProcessor {

    private static final String RS = "\u241E";

    private static final String SPACE = " ";

    private static final String STATUS_PROCESSED = "PROCESSED";

    public TmgrAdditionalFieldsUpdateProcessor(UpdateRequestProcessor next) {
	super(next);
    }

    @Override
    public void processAdd(AddUpdateCommand cmd) throws IOException {
	SolrInputDocument doc = cmd.getSolrInputDocument();

	if (doc.hasChildDocuments()) {
	    doc.getChildDocuments().clear();
	}

	Collection<String> languageIds = SolrDocHelper.extractLocaleCodes(doc.getFieldNames());

	for (String languageId : languageIds) {
	    addChildDocument(doc, languageId, true);

	    // extracting number from termID field (e.g. 'en-US3'
	    // returns 3)

	    Collection<String> idFieldNames = ProcessorUtils.getIdFields(doc, languageId);

	    Set<Integer> nums = SolrDocHelper.extractNumbers(idFieldNames);
	    for (Integer num : nums) {
		String langKey = languageId.concat(num.toString());
		addChildDocument(doc, langKey, false);
	    }

	    // check synonym order in order to avoid missing language
	    // index in parent document fields
	    idFieldNames = ProcessorUtils.getIdFields(doc, languageId);
	    nums = SolrDocHelper.extractNumbers(idFieldNames);
	    boolean isAnyFirst = isAnyFirstTermForLanguage(doc.getFieldNames(), languageId);
	    MutableInt index = new MutableInt(decideLanguageIndex(isAnyFirst));
	    for (Integer num : nums) {
		String langKey = languageId.concat(num.toString());
		if (num.intValue() != index.intValue() || !isAnyFirst) {
		    // re-create synonym order in parent document
		    reCreateTermFieldsFromParentDocument(doc, langKey, index);

		    // clear synonym fields in parent document
		    clearDisabledTermFields(doc, langKey, false);
		}
		index.increment();
	    }
	}

	if (!doc.hasChildDocuments()) {
	    /*
	     * Delete all child documents with specified _root_. For more info, check:
	     * org.apache.solr.update.DirectUpdateHandler2.addDoc0( AddUpdateCommand cmd);
	     */
	    cmd.updateTerm = new org.apache.lucene.index.Term("_root_", cmd.getIndexedId());
	    doc.removeField(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI);
	} else {
	    /* Re-Set languageIds field in parent document */
	    Set<String> langIds = new HashSet<>();
	    List<SolrInputDocument> childDocs = doc.getChildDocuments();
	    for (SolrInputDocument childDoc : childDocs) {
		String languageId = (String) childDoc.getFieldValue(SolrChildDocFileds.LANGUAGE_ID_INDEX);
		langIds.add(languageId);
	    }

	    doc.setField(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, langIds);
	}

	super.processAdd(cmd);
    }

    // child document from term
    private void addChildDocument(SolrInputDocument doc, String languageKey, boolean isFirst) {
	// First, check if term is disabled, then skip child doc creation and
	// delete all parent fields.
	String disabledField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_DISABLED_STORE);
	Object disabledValue = doc.getFieldValue(disabledField);
	Boolean disabled = disabledValue != null ? Boolean.valueOf(disabledValue.toString()) : Boolean.FALSE;

	if (disabled) {
	    clearDisabledTermFields(doc, languageKey, isFirst);
	    return;
	}

	// id
	String dynTermId = isFirst ? SolrParentDocFields.DYN_TERM_ID_SORT : SolrParentDocFields.DYN_TERM_ID_STORE;
	String idField = SolrDocHelper.createDynamicFieldName(languageKey, dynTermId);
	// must contains id field
	Object idValue = doc.getFieldValue(idField);
	if (idValue == null) {
	    clearDisabledTermFields(doc, languageKey, isFirst);
	    return;
	}

	SolrInputDocument childDoc = new SolrInputDocument();

	// id
	childDoc.setField(SolrConstants.ID_FIELD, idValue);

	// disabled
	childDoc.setField(SolrChildDocFileds.DISABLED_INDEX, disabled);

	// Facet pivot fields
	childDoc.setField(SolrChildDocFileds.TERM_PROJECT_ID_INDEX,
		doc.getFieldValue(SolrParentDocFields.PROJECT_ID_INDEX_STORE));

	// languageId
	String languageCode = SolrDocHelper.extractLocaleCode(languageKey);
	childDoc.setField(SolrChildDocFileds.LANGUAGE_ID_INDEX, languageCode);

	// status
	String dynStatus = isFirst ? SolrParentDocFields.DYN_STATUS_SORT : SolrParentDocFields.DYN_STATUS_STORE;
	String statusField = SolrDocHelper.createDynamicFieldName(languageKey, dynStatus);
	if (doc.getFieldValue(statusField) == null) {
	    doc.setField(statusField, STATUS_PROCESSED);
	}
	childDoc.setField(SolrChildDocFileds.STATUS_INDEX, doc.getFieldValue(statusField));

	// userCreated
	String dynUserCreated = isFirst ? SolrParentDocFields.DYN_USER_CREATED_SORT
		: SolrParentDocFields.DYN_USER_CREATED_STORE;
	String userCreatedField = SolrDocHelper.createDynamicFieldName(languageKey, dynUserCreated);
	childDoc.setField(SolrParentDocFields.USER_CREATED_INDEX, doc.getFieldValue(userCreatedField));

	// userModified
	String dynUserModified = isFirst ? SolrParentDocFields.DYN_USER_MODIFIED_SORT
		: SolrParentDocFields.DYN_USER_MODIFIED_STORE;
	String userModifiedField = SolrDocHelper.createDynamicFieldName(languageKey, dynUserModified);
	childDoc.setField(SolrChildDocFileds.USER_MODIFIED_INDEX, doc.getFieldValue(userModifiedField));

	// dateCreated
	String dynDateCreated = isFirst ? SolrParentDocFields.DYN_DATE_CREATED_SORT
		: SolrParentDocFields.DYN_DATE_CREATED_STORE;
	String dateCreatedField = SolrDocHelper.createDynamicFieldName(languageKey, dynDateCreated);
	Object dateCreatedValue = doc.getFieldValue(dateCreatedField);
	if (dateCreatedValue == null) {
	    dateCreatedValue = doc.getFieldValue(SolrParentDocFields.DATE_CREATED_INDEX_STORE);
	    doc.setField(dateCreatedField, dateCreatedValue);
	}
	childDoc.setField(SolrChildDocFileds.DATE_CREATED_INDEX, dateCreatedValue);

	// dateModified
	String dynDateModified = isFirst ? SolrParentDocFields.DYN_DATE_MODIFIED_SORT
		: SolrParentDocFields.DYN_DATE_MODIFIED_STORE;
	String dateModifiedField = SolrDocHelper.createDynamicFieldName(languageKey, dynDateModified);
	Object dateModifiedValue = doc.getFieldValue(dateModifiedField);
	if (dateModifiedValue == null) {
	    dateModifiedValue = doc.getFieldValue(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE);
	    doc.setField(dateModifiedField, dateModifiedValue);
	}
	childDoc.setField(SolrChildDocFileds.DATE_MODIFIED_INDEX, doc.getFieldValue(dateModifiedField));

	// forbidden
	String forbiddenField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_FORBIDDEN_STORE);
	childDoc.setField(SolrChildDocFileds.FORBIDDEN_INDEX, doc.getFieldValue(forbiddenField));

	// userLatestChange
	String userLatestChangeField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_USER_LATEST_CHANGE_STORE);
	childDoc.setField(SolrChildDocFileds.USER_LATEST_CHANGE_INDEX, doc.getFieldValue(userLatestChangeField));

	// termName
	addTermText(isFirst, languageCode, languageKey, doc, childDoc);

	// attributes
	String attributeParentField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE);
	String attributeChildField = SolrDocHelper.createDynamicFieldName(SolrParentDocFields.ATTR_PREFIX, languageCode,
		SolrChildDocFileds.NGRAM_INDEX_MULTI_SUFFIX);
	addDescriptionFieldToChildDocument(doc, childDoc, attributeParentField, attributeChildField);

	// notes
	String noteParentField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_NOTE_MULTI_STORE);
	String noteChildField = SolrDocHelper.createDynamicFieldName(SolrChildDocFileds.NOTE_PREFIX, languageCode,
		SolrChildDocFileds.NGRAM_INDEX_MULTI_SUFFIX);
	addDescriptionFieldToChildDocument(doc, childDoc, noteParentField, noteChildField);

	// termEntry attributes
	String termEntryAttributeParentField = SolrParentDocFields.ATTRIBUTE_MULTI_STORE;
	addDescriptionFieldToChildDocument(doc, childDoc, termEntryAttributeParentField, attributeChildField);

	if (doc.getFieldValue(SolrParentDocFields.PARENT_ID_STORE) != null) {
	    addSubmissionTermFieldToChildDocument(doc, childDoc, languageKey, isFirst);
	}

	doc.addChildDocument(childDoc);
    }

    private void addDescriptionFieldToChildDocument(SolrInputDocument doc, SolrInputDocument childDoc,
	    String parentField, String childField) {
	Collection<Object> attributes = doc.getFieldValues(parentField);
	if (attributes != null) {
	    for (Object attribute : attributes) {
		String typeValue = (String) attribute;
		if (typeValue != null) {
		    typeValue = CharMatcher.anyOf(RS).replaceFrom(typeValue, SPACE);
		    childDoc.addField(childField, typeValue);
		}
	    }
	}
    }

    private void addSubmissionTermFieldToChildDocument(SolrInputDocument doc, SolrInputDocument childDoc,
	    String languageId, boolean isFirst) {
	// assignee
	String dynAssigneeField = SolrParentDocFields.DYN_ASSIGNEE_SORT;
	String assingeeField = SolrDocHelper.createDynamicFieldName(languageId, dynAssigneeField);
	childDoc.setField(SolrChildDocFileds.ASSIGNEE_INDEX, doc.getFieldValue(assingeeField));

	// committed
	String dynCommittedField = SolrParentDocFields.DYN_COMMITED_STORE;
	String commentsField = SolrDocHelper.createDynamicFieldName(languageId, dynCommittedField);
	childDoc.setField(SolrChildDocFileds.COMMITED_INDEX, doc.getFieldValue(commentsField));

	// dateCompleted
	String dynDateCompletedField = isFirst ? SolrParentDocFields.DYN_DATE_COMPLETED_SORT
		: SolrParentDocFields.DYN_DATE_COMPLETED_STORE;
	Object dateCompletedFieldValue = doc
		.getFieldValue(SolrDocHelper.createDynamicFieldName(languageId, dynDateCompletedField));
	if (dateCompletedFieldValue != null) {
	    childDoc.setField(SolrChildDocFileds.DATE_COMPLETED_INDEX, dateCompletedFieldValue);
	}

	// dateSubmitted
	String dynDateSubmittedField = isFirst ? SolrParentDocFields.DYN_DATE_SUBMITTED_SORT
		: SolrParentDocFields.DYN_DATE_SUBMITTED_STORE;
	String dateSubmittedField = SolrDocHelper.createDynamicFieldName(languageId, dynDateSubmittedField);
	childDoc.setField(SolrChildDocFileds.DATE_SUBMITTED_INDEX, doc.getFieldValue(dateSubmittedField));

	// submitter
	childDoc.setField(SolrChildDocFileds.SUBMITTER_INDEX, doc.getFieldValue(SolrParentDocFields.SUBMITTER_SORT));

	// temp name
	String tempTermField = isFirst
		? SolrDocHelper.createDynamicFieldName(languageId,
			SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW)
		: SolrDocHelper.createDynamicFieldName(languageId, SolrParentDocFields.DYN_TEMP_TERM_NAME_STORE);

	String term = (String) doc.getFieldValue(tempTermField);
	if (term != null) {
	    String languageCode = SolrDocHelper.extractLocaleCode(languageId);
	    childDoc.setField(SolrDocHelper.createDynamicFieldName(SolrChildDocFileds.TEMP_TERM_NAME_PREFIX,
		    languageCode, SolrChildDocFileds.NGRAM_INDEX_SUFFIX), term);
	    childDoc.setField(SolrDocHelper.createDynamicFieldName(SolrChildDocFileds.TEMP_TERM_NAME_PREFIX,
		    languageCode, SolrChildDocFileds.SUB_FUZZY_SUFFIX), term);
	}
    }

    private void addTermText(boolean isFirst, String languageCode, String languageKey, SolrInputDocument doc,
	    SolrInputDocument childDoc) {
	String termNameField = isFirst
		? SolrDocHelper.createDynamicFieldName(languageCode, SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW)
		: SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TERM_NAME_STORE);
	String term = (String) doc.getFieldValue(termNameField);
	if (term != null) {
	    childDoc.setField(SolrDocHelper.createDynamicFieldName(SolrChildDocFileds.TERM_NAME_PREFIX, languageCode,
		    SolrChildDocFileds.NGRAM_INDEX_SUFFIX), term);
	    childDoc.setField(SolrDocHelper.createDynamicFieldName(SolrChildDocFileds.TERM_NAME_PREFIX, languageCode,
		    SolrChildDocFileds.SUB_FUZZY_SUFFIX), term);
	    childDoc.setField(SolrDocHelper.createDynamicFieldName(SolrChildDocFileds.TERM_NAME_PREFIX, languageCode,
		    SolrChildDocFileds.SUB_SUFFIX), term);
	    childDoc.setField(SolrChildDocFileds.TERM_CHECKSUM, ChecksumHelper.makeChecksum(term));
	}
    }

    private void clearDisabledTermFields(SolrInputDocument doc, String languageKey, boolean isFirst) {
	String languageCode = SolrDocHelper.extractLocaleCode(languageKey);

	// disabled
	String disabledField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_DISABLED_STORE);
	doc.removeField(disabledField);

	// id
	String dynTermId = isFirst ? SolrParentDocFields.DYN_TERM_ID_SORT : SolrParentDocFields.DYN_TERM_ID_STORE;
	String idField = SolrDocHelper.createDynamicFieldName(languageKey, dynTermId);
	doc.removeField(idField);

	// status
	String dynStatus = isFirst ? SolrParentDocFields.DYN_STATUS_SORT : SolrParentDocFields.DYN_STATUS_STORE;
	String statusField = SolrDocHelper.createDynamicFieldName(languageKey, dynStatus);
	doc.removeField(statusField);

	// status old
	String statusOldField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_STATUS_OLD_STORE);
	doc.removeField(statusOldField);

	// userCreated
	String dynUserCreated = isFirst ? SolrParentDocFields.DYN_USER_CREATED_SORT
		: SolrParentDocFields.DYN_USER_CREATED_STORE;
	String userCreatedField = SolrDocHelper.createDynamicFieldName(languageKey, dynUserCreated);
	doc.removeField(userCreatedField);

	// userModified
	String dynUserModified = isFirst ? SolrParentDocFields.DYN_USER_MODIFIED_SORT
		: SolrParentDocFields.DYN_USER_MODIFIED_STORE;
	String userModifiedField = SolrDocHelper.createDynamicFieldName(languageKey, dynUserModified);
	doc.removeField(userModifiedField);

	// dateCreated
	String dynDateCreated = isFirst ? SolrParentDocFields.DYN_DATE_CREATED_SORT
		: SolrParentDocFields.DYN_DATE_CREATED_STORE;
	String dateCreatedField = SolrDocHelper.createDynamicFieldName(languageKey, dynDateCreated);
	doc.removeField(dateCreatedField);

	// dateModified
	String dynDateModified = isFirst ? SolrParentDocFields.DYN_DATE_MODIFIED_SORT
		: SolrParentDocFields.DYN_DATE_MODIFIED_STORE;
	String dateModifiedField = SolrDocHelper.createDynamicFieldName(languageKey, dynDateModified);
	doc.removeField(dateModifiedField);

	// forbidden
	String forbiddenField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_FORBIDDEN_STORE);
	doc.removeField(forbiddenField);

	// userLatestChange
	String userLatestChangeField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_USER_LATEST_CHANGE_STORE);
	doc.removeField(userLatestChangeField);

	// termName
	String termNameField = isFirst
		? SolrDocHelper.createDynamicFieldName(languageCode, SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX)
		: SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TERM_NAME_STORE);
	doc.removeField(termNameField);

	// attributes
	String attributeField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE);
	doc.removeField(attributeField);

	// notes
	String noteField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_NOTE_MULTI_STORE);
	doc.removeField(noteField);

	// in translation as source
	String inTranslationAsSourceField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE);
	doc.removeField(inTranslationAsSourceField);

	if (doc.getFieldValue(SolrParentDocFields.PARENT_ID_STORE) != null) {
	    clearDisabledTermSubmissionFields(doc, languageKey, isFirst);
	}
    }

    private void clearDisabledTermSubmissionFields(SolrInputDocument doc, String languageKey, boolean isFirst) {
	// assignee
	String dynAssigneeField = SolrParentDocFields.DYN_ASSIGNEE_SORT;
	String assingeeField = SolrDocHelper.createDynamicFieldName(languageKey, dynAssigneeField);
	doc.removeField(assingeeField);

	// committed
	String dynCommittedField = SolrParentDocFields.DYN_COMMITED_STORE;
	String commentsField = SolrDocHelper.createDynamicFieldName(languageKey, dynCommittedField);
	doc.removeField(commentsField);

	// dateCompleted
	String dynDateCompletedField = isFirst ? SolrParentDocFields.DYN_DATE_COMPLETED_SORT
		: SolrParentDocFields.DYN_DATE_COMPLETED_STORE;
	String dateCompletedField = SolrDocHelper.createDynamicFieldName(languageKey, dynDateCompletedField);
	doc.removeField(dateCompletedField);

	// dateSubmitted
	String dynDateSubmittedField = isFirst ? SolrParentDocFields.DYN_DATE_SUBMITTED_SORT
		: SolrParentDocFields.DYN_DATE_SUBMITTED_STORE;
	String dateSubmittedField = SolrDocHelper.createDynamicFieldName(languageKey, dynDateSubmittedField);
	doc.removeField(dateSubmittedField);

	// submitter
	doc.removeField(SolrParentDocFields.SUBMITTER_SORT);

	// temp name
	String tempTermField = isFirst
		? SolrDocHelper.createDynamicFieldName(languageKey,
			SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW)
		: SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TEMP_TERM_NAME_STORE);
	doc.removeField(tempTermField);
    }

    // If there is no first term for language create one
    private int decideLanguageIndex(boolean isAnyFirst) {
	return isAnyFirst ? 1 : 0;
    }

    // Document with ID_STRING_BASIC_SORT field is first term
    private boolean isAnyFirstTermForLanguage(Collection<String> fieldNames, String languageId) {
	String dynBasicSort = SolrParentDocFields.DYN_TERM_ID_SORT;
	for (String fieldName : fieldNames) {
	    if (fieldName.startsWith(languageId) && fieldName.endsWith(dynBasicSort)) {
		return true;
	    }
	}
	return false;
    }

    private void reCreateSubmissionTermFieldsFromParentDocument(SolrInputDocument doc, String languageKey,
	    MutableInt index) {
	String languageCode = SolrDocHelper.extractLocaleCode(languageKey);
	String newLangKey = languageCode.concat(index.toString());

	// assignee
	String assingeeField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_ASSIGNEE_SORT);
	String newAssingeeField = SolrDocHelper.createDynamicFieldName(newLangKey,
		SolrParentDocFields.DYN_ASSIGNEE_SORT);
	doc.setField(newAssingeeField, doc.getFieldValue(assingeeField));

	// comments
	String commentsField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_COMMITED_STORE);
	String newCommentsField = SolrDocHelper.createDynamicFieldName(newLangKey,
		SolrParentDocFields.DYN_COMMITED_STORE);
	doc.setField(newCommentsField, doc.getFieldValues(commentsField));

	// dateCompleted
	String dateCompletedField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_DATE_COMPLETED_STORE);
	String newDateCompletedField = SolrDocHelper.createDynamicFieldName(newLangKey,
		SolrParentDocFields.DYN_DATE_COMPLETED_STORE);
	if (doc.getFieldValue(dateCompletedField) != null) {
	    doc.setField(newDateCompletedField, doc.getFieldValue(dateCompletedField));
	}

	// dateSubmitted
	String dateSubmittedField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_DATE_SUBMITTED_STORE);
	String newDateSubmittedField = SolrDocHelper.createDynamicFieldName(newLangKey,
		SolrParentDocFields.DYN_DATE_SUBMITTED_STORE);
	doc.setField(newDateSubmittedField, doc.getFieldValue(dateSubmittedField));

	// temp name
	String tempTermField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_TEMP_TERM_NAME_STORE);
	String newTempTermField = SolrDocHelper.createDynamicFieldName(newLangKey,
		SolrParentDocFields.DYN_TEMP_TERM_NAME_STORE);

	if (doc.getFieldValue(tempTermField) != null) {
	    doc.setField(newTempTermField, doc.getFieldValue(tempTermField));
	}
    }

    private void reCreateTermFieldsFromParentDocument(SolrInputDocument doc, String languageKey, MutableInt index) {
	String languageCode = SolrDocHelper.extractLocaleCode(languageKey);
	int indexVal = index.intValue();
	boolean isFirst = indexVal < 1;
	String newLangKey = isFirst ? languageCode : languageCode.concat(Integer.toString(indexVal));

	// id
	String termIdName = isFirst ? SolrParentDocFields.DYN_TERM_ID_SORT : SolrParentDocFields.DYN_TERM_ID_STORE;
	String idField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TERM_ID_STORE);
	String newIdField = SolrDocHelper.createDynamicFieldName(newLangKey, termIdName);
	doc.setField(newIdField, doc.getFieldValue(idField));

	// disabled
	String newDisabledField = SolrDocHelper.createDynamicFieldName(newLangKey,
		SolrParentDocFields.DYN_DISABLED_STORE);
	doc.setField(newDisabledField, Boolean.FALSE);

	// status
	String statusFieldName = isFirst ? SolrParentDocFields.DYN_STATUS_SORT : SolrParentDocFields.DYN_STATUS_STORE;
	String statusField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_STATUS_STORE);
	String newStatusField = SolrDocHelper.createDynamicFieldName(newLangKey, statusFieldName);
	doc.setField(newStatusField, doc.getFieldValue(statusField));

	// statusOld
	String statusOldField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_STATUS_OLD_STORE);
	if (doc.getFieldValue(statusOldField) != null) {
	    String newStatusOldField = SolrDocHelper.createDynamicFieldName(newLangKey,
		    SolrParentDocFields.DYN_STATUS_OLD_STORE);
	    doc.setField(newStatusOldField, doc.getFieldValue(statusOldField));
	}

	// userCreated
	String userCreatedFieldName = isFirst ? SolrParentDocFields.DYN_USER_CREATED_SORT
		: SolrParentDocFields.DYN_USER_CREATED_STORE;
	String userCreatedField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_USER_CREATED_STORE);
	String newUserCreatedField = SolrDocHelper.createDynamicFieldName(newLangKey, userCreatedFieldName);
	doc.setField(newUserCreatedField, doc.getFieldValue(userCreatedField));

	// userModified
	String userModifiedFieldName = isFirst ? SolrParentDocFields.DYN_USER_MODIFIED_SORT
		: SolrParentDocFields.DYN_USER_MODIFIED_STORE;
	String userModifiedField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_USER_MODIFIED_STORE);
	String newUserModifiedField = SolrDocHelper.createDynamicFieldName(newLangKey, userModifiedFieldName);
	doc.setField(newUserModifiedField, doc.getFieldValue(userModifiedField));

	// dateCreated
	String dateCreatedFieldName = isFirst ? SolrParentDocFields.DYN_DATE_CREATED_SORT
		: SolrParentDocFields.DYN_DATE_CREATED_STORE;
	String dateCreatedField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_DATE_CREATED_STORE);
	String newDateCreatedField = SolrDocHelper.createDynamicFieldName(newLangKey, dateCreatedFieldName);
	doc.setField(newDateCreatedField, doc.getFieldValue(dateCreatedField));

	// dateModified
	String dateModifiedFieldName = isFirst ? SolrParentDocFields.DYN_DATE_MODIFIED_SORT
		: SolrParentDocFields.DYN_DATE_MODIFIED_STORE;
	String dateModifiedField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_DATE_MODIFIED_STORE);
	String newDateModifiedField = SolrDocHelper.createDynamicFieldName(newLangKey, dateModifiedFieldName);
	doc.setField(newDateModifiedField, doc.getFieldValue(dateModifiedField));

	// forbidden
	String forbiddenField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_FORBIDDEN_STORE);
	String newForbiddenField = SolrDocHelper.createDynamicFieldName(newLangKey,
		SolrParentDocFields.DYN_FORBIDDEN_STORE);
	doc.setField(newForbiddenField, doc.getFieldValue(forbiddenField));

	// userLatestChange
	String userLatestChangeField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_USER_LATEST_CHANGE_STORE);
	if (doc.getFieldValue(userLatestChangeField) != null) {
	    String newUserLatestChangeField = SolrDocHelper.createDynamicFieldName(newLangKey,
		    SolrParentDocFields.DYN_USER_LATEST_CHANGE_STORE);
	    doc.setField(newUserLatestChangeField, doc.getFieldValue(userLatestChangeField));
	}

	// termName
	String termName = isFirst ? SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW
		: SolrParentDocFields.DYN_TERM_NAME_STORE;

	String termNameField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_TERM_NAME_STORE);
	if (doc.getFieldValue(termNameField) != null) {
	    String newTermNameField = SolrDocHelper.createDynamicFieldName(newLangKey, termName);
	    doc.setField(newTermNameField, doc.getFieldValue(termNameField));
	}

	// attributes
	String attributeField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE);
	if (doc.getFieldValues(attributeField) != null) {
	    String newAttributeField = SolrDocHelper.createDynamicFieldName(newLangKey,
		    SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE);
	    doc.setField(newAttributeField, doc.getFieldValues(attributeField));
	}

	// notes
	String noteField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_NOTE_MULTI_STORE);
	if (doc.getFieldValues(noteField) != null) {
	    String newNoteField = SolrDocHelper.createDynamicFieldName(newLangKey,
		    SolrParentDocFields.DYN_NOTE_MULTI_STORE);
	    doc.setField(newNoteField, doc.getFieldValues(noteField));
	}

	// inTranslationAsSource
	String inTranslationField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE);
	if (doc.getFieldValues(inTranslationField) != null) {
	    String newNoteField = SolrDocHelper.createDynamicFieldName(newLangKey,
		    SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE);
	    doc.setField(newNoteField, doc.getFieldValues(inTranslationField));
	}

	if (doc.getFieldValue(SolrParentDocFields.PARENT_ID_STORE) != null) {
	    reCreateSubmissionTermFieldsFromParentDocument(doc, languageKey, index);
	}
    }
}

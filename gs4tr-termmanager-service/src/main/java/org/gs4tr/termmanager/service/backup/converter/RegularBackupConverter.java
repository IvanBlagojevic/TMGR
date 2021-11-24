package org.gs4tr.termmanager.service.backup.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.foundation3.solr.model.update.CommandEnum;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryDescription;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class RegularBackupConverter {

    public static List<DbTermEntry> convertToDbTermEntries(List<SolrInputDocument> docs) {
	List<DbTermEntry> entries = new ArrayList<>();

	if (CollectionUtils.isNotEmpty(docs)) {
	    for (SolrInputDocument doc : docs) {
		entries.add(convertToDbTermEntry(doc));
	    }
	}

	return entries;
    }

    public static DbTermEntry convertToDbTermEntry(SolrInputDocument doc) {
	DbTermEntry entry = new DbTermEntry();

	String id = CommonBackupConverter.getStringValue(SolrConstants.ID_FIELD, doc);
	entry.setUuId(id);

	String action = CommonBackupConverter.getStringValue(SolrParentDocFields.HISTORY_ACTION, doc);
	if (action == null) {
	    action = Action.NOT_AVAILABLE.name();
	}
	entry.setAction(action);

	Long projectId = CommonBackupConverter.getLongValue(SolrParentDocFields.PROJECT_ID_INDEX_STORE, doc);
	entry.setProjectId(projectId);

	String shortcode = CommonBackupConverter.getStringValue(SolrParentDocFields.SHORTCODE_INDEX_STORE, doc);
	entry.setShortCode(shortcode);

	String projectName = CommonBackupConverter.getStringValue(SolrParentDocFields.PROJECT_NAME_INDEX_STORE, doc);
	entry.setProjectName(projectName);

	Long dateCreated = CommonBackupConverter.getLongValue(SolrParentDocFields.DATE_CREATED_INDEX_STORE, doc);
	entry.setDateCreated(new Date(dateCreated));

	Long dateModified = CommonBackupConverter.getLongValue(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, doc);
	entry.setDateModified(new Date(dateModified));

	String userCreated = CommonBackupConverter.getStringValue(SolrParentDocFields.USER_CREATED_INDEX, doc);
	entry.setUserCreated(userCreated);
	if (entry.getUserCreated() == null) {
	    userCreated = CommonBackupConverter.getStringValue(SolrParentDocFields.USER_CREATED_INDEX, doc);
	    entry.setUserCreated(userCreated);
	}

	String userModified = CommonBackupConverter.getStringValue(SolrParentDocFields.USER_MODIFIED_SORT, doc);
	entry.setUserModified(userModified);

	Long submissionId = CommonBackupConverter.getLongValue(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE, doc);
	entry.setSubmissionId(submissionId);

	String submissionName = CommonBackupConverter.getStringValue(SolrParentDocFields.SUBMISSION_NAME_STORE, doc);
	entry.setSubmissionName(submissionName);

	String submitter = CommonBackupConverter.getStringValue(SolrParentDocFields.SUBMITTER_SORT, doc);
	entry.setSubmitter(submitter);

	Collection<Object> attributes = doc.getFieldValues(SolrParentDocFields.ATTRIBUTE_MULTI_STORE);

	if (attributes != null) {
	    entry.setDescriptions(convertTermEntryDescriptions(entry, attributes));
	}

	List<TermField> termFields = createTermFields(doc);

	Set<DbTerm> terms = new HashSet<>();

	for (TermField termField : termFields) {
	    DbTerm term = convertToTerm(entry, termField);
	    if (term != null) {
		terms.add(term);
	    }

	    Set<DbTerm> deletedTerms = convertToDeletedTerms(doc, entry, termField.getLanguageId());
	    if (deletedTerms != null) {
		terms.addAll(deletedTerms);
	    }
	}
	int revisionId = CommonBackupConverter.getIntegerValue(SolrParentDocFields.REVISION, doc);
	entry.setRevisionId(revisionId);
	entry.setTerms(terms);

	return entry;
    }

    private static void addTermDescription(String baseType, String value, DbTerm term) {
	if (!value.contains(SolrGlossaryAdapter.RS)) {
	    return;
	}
	Iterator<String> iterator = SolrDocHelper.split(SolrGlossaryAdapter.RS, value);

	DbTermDescription description = new DbTermDescription();
	description.setBaseType(baseType);
	description.setType(iterator.next());
	description.setValueAsBytes(iterator.next());
	description.setUuid(iterator.next());
	description.setTermUuid(term.getUuId());

	Set<DbTermDescription> descriptions = term.getDescriptions();
	if (descriptions == null) {
	    descriptions = new HashSet<>();
	    term.setDescriptions(descriptions);
	}

	descriptions.add(description);
    }

    private static void addTermEntryDescription(DbTermEntry termEntry, String typeValue,
	    Set<DbTermEntryDescription> descriptions) {
	if (!typeValue.contains(SolrGlossaryAdapter.RS)) {
	    return;
	}
	Iterator<String> iterator = SolrDocHelper.split(SolrGlossaryAdapter.RS, typeValue);

	DbTermEntryDescription description = new DbTermEntryDescription();
	description.setType(iterator.next());
	description.setValueAsBytes(iterator.next());
	description.setUuid(iterator.next());
	description.setTermEntryUuid(termEntry.getUuId());

	descriptions.add(description);
    }

    private static Set<DbTermEntryDescription> convertTermEntryDescriptions(DbTermEntry termEntry,
	    Collection<Object> collection) {
	Set<DbTermEntryDescription> descriptions = new HashSet<>();
	for (Object item : collection) {
	    if (item instanceof String) {
		String typeValue = (String) item;
		addTermEntryDescription(termEntry, typeValue, descriptions);
	    } else if (item instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Set<String>> setMap = (Map<String, Set<String>>) item;
		Set<String> setValues = setMap.get(CommandEnum.SET.name().toLowerCase());
		if (setValues != null) {
		    for (String typeValue : setValues) {
			addTermEntryDescription(termEntry, typeValue, descriptions);
		    }
		}
	    }
	}
	return descriptions;
    }

    private static Set<DbTerm> convertToDeletedTerms(SolrInputDocument doc, DbTermEntry termEntry, String languageId) {

	String deletedTermsField = SolrDocHelper.createDynamicFieldName(languageId,
		SolrParentDocFields.DYN_DELETED_TERMS);
	Collection<Object> deletedTerms = doc.getFieldValues(deletedTermsField);
	if (deletedTerms == null) {
	    return null;
	}

	Set<DbTerm> terms = new HashSet<>();

	for (Object item : deletedTerms) {
	    String deletedId = CommonBackupConverter.getStringValue(item);

	    DbTerm deletedTerm = new DbTerm();
	    deletedTerm.setUuId(deletedId);
	    deletedTerm.setLanguageId(languageId);
	    deletedTerm.setDisabled(Boolean.TRUE);
	    deletedTerm.setNameAsBytes(deletedId);
	    deletedTerm.setTermEntryUuid(termEntry.getUuId());

	    // fake values
	    deletedTerm.setDateCreated(new Date());
	    deletedTerm.setDateModified(new Date());
	    deletedTerm.setFirst(Boolean.FALSE);
	    deletedTerm
		    .setUserCreated(CommonBackupConverter.getStringValue(SolrParentDocFields.USER_CREATED_INDEX, doc));
	    deletedTerm
		    .setUserModified(CommonBackupConverter.getStringValue(SolrParentDocFields.USER_MODIFIED_SORT, doc));
	    deletedTerm.setStatus(ItemStatusTypeHolder.PROCESSED.getName());

	    terms.add(deletedTerm);
	}

	return terms;
    }

    private static DbTerm convertToTerm(DbTermEntry termEntry, TermField termField) {
	String key = termField.getKey();
	String languageId = termField.getLanguageId();

	int num = SolrDocHelper.extractNumber(key);
	boolean first = num == 0;

	DbTerm term = new DbTerm();
	term.setLanguageId(languageId);
	term.setFirst(first);

	term.setProjectId(termEntry.getProjectId());
	term.setTermEntryUuid(termEntry.getUuId());

	Map<String, Object> fieldValues = termField.getFieldNames();

	// ID
	String suffix = first ? SolrParentDocFields.DYN_TERM_ID_SORT : SolrParentDocFields.DYN_TERM_ID_STORE;
	String uuidKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String uuId = CommonBackupConverter.getStringValue(uuidKey, fieldValues);
	// need this because deleted terms doesn't have id field
	if (uuId == null) {
	    return null;
	}
	term.setUuId(uuId);

	// DATE_CREATED
	suffix = first ? SolrParentDocFields.DYN_DATE_CREATED_SORT : SolrParentDocFields.DYN_DATE_CREATED_STORE;
	String dateCreatedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long dateCreated = CommonBackupConverter.getLongValue(dateCreatedKey, fieldValues);
	if (dateCreated != null) {
	    term.setDateCreated(new Date(dateCreated));
	}

	// DATE_MODIFIED
	suffix = first ? SolrParentDocFields.DYN_DATE_MODIFIED_SORT : SolrParentDocFields.DYN_DATE_MODIFIED_STORE;
	String dateModifiedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long dateModified = CommonBackupConverter.getLongValue(dateModifiedKey, fieldValues);
	if (dateModified != null) {
	    term.setDateModified(new Date(dateModified));
	}

	// ATTRIBUTES
	suffix = SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE;
	String attributesKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Object attributeValue = fieldValues.get(attributesKey);
	if (attributeValue != null) {
	    if (attributeValue instanceof List) {
		@SuppressWarnings("unchecked")
		List<String> attributes = (List<String>) attributeValue;
		for (String value : attributes) {
		    addTermDescription(Description.ATTRIBUTE, value, term);
		}
	    } else if (attributeValue instanceof String) {
		String value = (String) attributeValue;
		addTermDescription(Description.ATTRIBUTE, value, term);
	    }
	}

	// NOTES
	suffix = SolrParentDocFields.DYN_NOTE_MULTI_STORE;
	String notesKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Object noteValue = fieldValues.get(notesKey);
	if (noteValue != null) {
	    if (noteValue instanceof List) {
		@SuppressWarnings("unchecked")
		List<String> notes = (List<String>) noteValue;
		for (String value : notes) {
		    addTermDescription(Description.NOTE, value, term);
		}
	    } else if (noteValue instanceof String) {
		String value = (String) noteValue;
		addTermDescription(Description.NOTE, value, term);
	    }

	}

	// DISABLED
	suffix = SolrParentDocFields.DYN_DISABLED_STORE;
	String disabledKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean disabled = CommonBackupConverter.getBooleanValue(disabledKey, fieldValues);
	term.setDisabled(disabled);

	// FORBIDDEN
	suffix = SolrParentDocFields.DYN_FORBIDDEN_STORE;
	String forbiddenKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean forbidden = CommonBackupConverter.getBooleanValue(forbiddenKey, fieldValues);
	term.setForbidden(forbidden);

	// NAME
	if (first) {
	    String termNameFieldOld = SolrDocHelper.createDynamicFieldName(key,
		    SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX);
	    String termTextOld = CommonBackupConverter.getStringValue(termNameFieldOld, fieldValues);

	    if (termTextOld != null) {
		term.setNameAsBytes(termTextOld);
	    } else {
		String termNameFieldNew = SolrDocHelper.createDynamicFieldName(key,
			SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW);
		String termTextNew = CommonBackupConverter.getStringValue(termNameFieldNew, fieldValues);
		term.setNameAsBytes(termTextNew);
	    }
	} else {
	    String termNameKey = SolrDocHelper.createDynamicFieldName(key, SolrParentDocFields.DYN_TERM_NAME_STORE);
	    String termName = CommonBackupConverter.getStringValue(termNameKey, fieldValues);
	    term.setNameAsBytes(termName);
	}

	// STATUS
	suffix = first ? SolrParentDocFields.DYN_STATUS_SORT : SolrParentDocFields.DYN_STATUS_STORE;
	String statusKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String status = CommonBackupConverter.getStringValue(statusKey, fieldValues);
	term.setStatus(status);

	// STATUS_OLD
	suffix = SolrParentDocFields.DYN_STATUS_OLD_STORE;
	String statusOldKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String statusOld = CommonBackupConverter.getStringValue(statusOldKey, fieldValues);
	term.setStatusOld(statusOld);

	// USER_CREATED
	suffix = first ? SolrParentDocFields.DYN_USER_CREATED_SORT : SolrParentDocFields.DYN_USER_CREATED_STORE;
	String userCreatedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String userCreated = CommonBackupConverter.getStringValue(userCreatedKey, fieldValues);
	term.setUserCreated(userCreated);

	// USER_MODIFIED
	suffix = first ? SolrParentDocFields.DYN_USER_MODIFIED_SORT : SolrParentDocFields.DYN_USER_MODIFIED_STORE;
	String userModifiedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String userModified = CommonBackupConverter.getStringValue(userModifiedKey, fieldValues);
	term.setUserModified(userModified);

	// USER_LATEST_CHANGES
	suffix = SolrParentDocFields.DYN_USER_LATEST_CHANGE_STORE;
	String userLatestChangeKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long userLatestChange = CommonBackupConverter.getLongValue(userLatestChangeKey, fieldValues);
	term.setUserLatestChange(userLatestChange);

	suffix = SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE;
	String inTranslationAsSourceKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean inTranslationAsSource = CommonBackupConverter.getBooleanValue(inTranslationAsSourceKey, fieldValues);
	term.setInTranslationAsSource(inTranslationAsSource);

	return term;
    }

    private static List<TermField> createTermFields(SolrInputDocument doc) {
	List<TermField> list = new ArrayList<>();

	Collection<String> fieldNames = doc.getFieldNames();
	for (String fieldName : fieldNames) {
	    String key = SolrDocHelper.getBeforeUnderscore(fieldName);
	    String languageId = SolrDocHelper.extractLocaleCode(fieldName);
	    if (!Locale.checkLocale(languageId)) {
		continue;
	    }

	    SolrInputField field = doc.get(fieldName);
	    Object fieldValue = field.getValue();

	    TermField termField = new TermField(key, languageId);

	    if (list.contains(termField)) {
		// find item in the list
		for (TermField item : list) {
		    if (item.equals(termField)) {
			Map<String, Object> namesMap = item.getFieldNames();
			namesMap.put(fieldName, fieldValue);
			break;
		    }
		}
	    } else {
		// add new term field item
		Map<String, Object> namesMap = termField.getFieldNames();
		namesMap.put(fieldName, fieldValue);
		list.add(termField);
	    }
	}

	return list;
    }
}

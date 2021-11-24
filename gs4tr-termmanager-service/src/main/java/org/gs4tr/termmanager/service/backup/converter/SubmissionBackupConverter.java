package org.gs4tr.termmanager.service.backup.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.foundation3.solr.model.update.CommandEnum;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbComment;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryDescription;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class SubmissionBackupConverter {

    public static DbSubmissionTermEntry convertToDbSubmissionTermEntry(SolrInputDocument doc) {
	DbSubmissionTermEntry entry = new DbSubmissionTermEntry();

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
	if (userModified == null) {
	    userModified = userCreated;
	}
	entry.setUserModified(userModified);

	Long submissionId = CommonBackupConverter.getLongValue(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE, doc);
	entry.setSubmissionId(submissionId);

	String submissionName = CommonBackupConverter.getStringValue(SolrParentDocFields.SUBMISSION_NAME_STORE, doc);
	entry.setSubmissionName(submissionName);

	String submitter = CommonBackupConverter.getStringValue(SolrParentDocFields.SUBMITTER_SORT, doc);
	entry.setSubmitter(submitter);

	String parentId = CommonBackupConverter.getStringValue(SolrParentDocFields.PARENT_ID_STORE, doc);
	entry.setParentUuId(parentId);

	Collection<Object> attributes = doc.getFieldValues(SolrParentDocFields.ATTRIBUTE_MULTI_STORE);

	if (attributes != null) {
	    entry.setDescriptions(convertTermEntryDescriptions(entry, attributes));
	}

	List<TermField> termFields = createTermFields(doc);

	Set<DbSubmissionTerm> terms = new HashSet<>();

	for (TermField termField : termFields) {
	    DbSubmissionTerm term = convertToTerm(entry, termField);
	    if (term != null) {
		terms.add(term);
	    }
	}

	entry.setSubmissionTerms(terms);

	int revisionId = CommonBackupConverter.getIntegerValue(SolrParentDocFields.REVISION, doc);
	entry.setRevisionId(revisionId);
	boolean isRoolbackAction = doc.containsKey(SolrParentDocFields.ROLLBACK);
	entry.setActionRollback(isRoolbackAction);
	return entry;
    }

    private static void addTermComment(DbSubmissionTerm term, Set<DbComment> termComments, String value) {
	if (StringUtils.isEmpty(value)) {
	    return;
	}

	if (value.contains(SolrGlossaryAdapter.RS)) {
	    Iterator<String> iterator = SolrDocHelper.split(SolrGlossaryAdapter.RS, value);

	    DbComment comment = new DbComment();
	    comment.setUser(iterator.next());
	    comment.setText(iterator.next());
	    comment.setSubmissionTermUuid(term.getUuId());
	    comment.setUuid(UUID.randomUUID().toString());

	    termComments.add(comment);
	}
    }

    private static void addTermDescription(String baseType, String value, DbSubmissionTerm term) {
	if (!value.contains(SolrGlossaryAdapter.RS)) {
	    return;
	}
	Iterator<String> iterator = SolrDocHelper.split(SolrGlossaryAdapter.RS, value);

	DbSubmissionTermDescription description = new DbSubmissionTermDescription();
	description.setBaseType(baseType);
	description.setType(iterator.next());
	description.setValueAsBytes(iterator.next());
	description.setUuid(iterator.next());
	description.setSubmissionTermUuid(term.getUuId());

	Set<DbSubmissionTermDescription> descriptions = term.getDescriptions();
	if (descriptions == null) {
	    descriptions = new HashSet<>();
	    term.setDescriptions(descriptions);
	}

	descriptions.add(description);
    }

    private static void addTermEntryDescription(DbSubmissionTermEntry termEntry, String typeValue,
	    Set<DbSubmissionTermEntryDescription> descriptions) {
	if (!typeValue.contains(SolrGlossaryAdapter.RS)) {
	    return;
	}
	Iterator<String> iterator = SolrDocHelper.split(SolrGlossaryAdapter.RS, typeValue);

	DbSubmissionTermEntryDescription description = new DbSubmissionTermEntryDescription();
	description.setType(iterator.next());
	description.setValueAsBytes(iterator.next());
	description.setUuid(iterator.next());
	description.setSubmissionTermEntryUuid(termEntry.getUuId());

	descriptions.add(description);
    }

    private static Set<DbSubmissionTermEntryDescription> convertTermEntryDescriptions(DbSubmissionTermEntry termEntry,
	    Collection<Object> collection) {
	Set<DbSubmissionTermEntryDescription> descriptions = new HashSet<>();
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

    private static DbSubmissionTerm convertToTerm(DbSubmissionTermEntry termEntry, TermField termField) {
	String key = termField.getKey();
	String languageId = termField.getLanguageId();

	int num = SolrDocHelper.extractNumber(key);
	boolean first = num == 0;

	DbSubmissionTerm term = new DbSubmissionTerm();
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
	} else {
	    term.setDateCreated(termEntry.getDateCreated());
	}

	// DATE_MODIFIED
	suffix = first ? SolrParentDocFields.DYN_DATE_MODIFIED_SORT : SolrParentDocFields.DYN_DATE_MODIFIED_STORE;
	String dateModifiedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long dateModified = CommonBackupConverter.getLongValue(dateModifiedKey, fieldValues);
	if (dateModified != null) {
	    term.setDateModified(new Date(dateModified));
	} else {
	    term.setDateModified(termEntry.getDateModified());
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

	suffix = SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE;
	String inTranslationAsSourceKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean inTranslationAsSource = CommonBackupConverter.getBooleanValue(inTranslationAsSourceKey, fieldValues);
	term.setInTranslationAsSource(inTranslationAsSource);

	// USER_CREATED
	suffix = first ? SolrParentDocFields.DYN_USER_CREATED_SORT : SolrParentDocFields.DYN_USER_CREATED_STORE;
	String userCreatedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String userCreated = CommonBackupConverter.getStringValue(userCreatedKey, fieldValues);
	if (userCreated != null) {
	    term.setUserCreated(userCreated);
	} else {
	    term.setUserCreated(termEntry.getUserCreated());
	}

	// USER_MODIFIED
	suffix = first ? SolrParentDocFields.DYN_USER_MODIFIED_SORT : SolrParentDocFields.DYN_USER_MODIFIED_STORE;
	String userModifiedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String userModified = CommonBackupConverter.getStringValue(userModifiedKey, fieldValues);
	if (userModified != null) {
	    term.setUserModified(userModified);
	} else {
	    term.setUserModified(termEntry.getUserModified());
	}

	// PARENT_ID
	suffix = SolrParentDocFields.DYN_PARENT_ID_STORE;
	String parentIdKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String parentId = CommonBackupConverter.getStringValue(parentIdKey, fieldValues);
	term.setParentUuId(parentId);

	// ASSIGNEE
	suffix = SolrParentDocFields.DYN_ASSIGNEE_SORT;
	String assigneKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String assignee = CommonBackupConverter.getStringValue(assigneKey, fieldValues);
	term.setAssignee(assignee);

	// CANCELED
	suffix = SolrParentDocFields.DYN_CANCELED_STORE;
	String canceledKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean canceled = CommonBackupConverter.getBooleanValue(canceledKey, fieldValues);
	term.setCanceled(canceled);

	// COMMITED
	suffix = SolrParentDocFields.DYN_COMMITED_STORE;
	String commitedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean commited = CommonBackupConverter.getBooleanValueDefault(commitedKey, fieldValues, Boolean.TRUE);
	term.setCommited(commited);

	// DATE_COMPLITED
	suffix = first ? SolrParentDocFields.DYN_DATE_COMPLETED_SORT : SolrParentDocFields.DYN_DATE_COMPLETED_STORE;
	String dateCompletedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long dateCompleted = CommonBackupConverter.getLongValue(dateCompletedKey, fieldValues);
	if (dateCompleted != null) {
	    term.setDateCompleted(new Date(dateCompleted));
	}

	// DATE_SUBMITTED
	suffix = first ? SolrParentDocFields.DYN_DATE_SUBMITTED_SORT : SolrParentDocFields.DYN_DATE_SUBMITTED_STORE;
	String dateSubmittedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long dateSubmitted = CommonBackupConverter.getLongValue(dateSubmittedKey, fieldValues);
	if (dateSubmitted != null) {
	    term.setDateSubmitted(new Date(dateSubmitted));
	}

	// REVIEW_REQUIRED
	suffix = SolrParentDocFields.DYN_REVIEW_REQUIRED_STORE;
	String reviewRequiredKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean reviewRequired = CommonBackupConverter.getBooleanValue(reviewRequiredKey, fieldValues);
	term.setReviewRequired(reviewRequired);

	// TEMP_TEXT
	if (first) {
	    String tempTermNameFieldOld = SolrDocHelper.createDynamicFieldName(key,
		    SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX);
	    String tempTermTextOld = CommonBackupConverter.getStringValue(tempTermNameFieldOld, fieldValues);

	    if (tempTermTextOld != null) {
		term.setTempText(tempTermTextOld);
	    } else {
		String termNameFieldNew = SolrDocHelper.createDynamicFieldName(key,
			SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW);
		String tempTermTextNew = CommonBackupConverter.getStringValue(termNameFieldNew, fieldValues);
		term.setTempText(tempTermTextNew);
	    }
	} else {
	    String tempTermNameKey = SolrDocHelper.createDynamicFieldName(key,
		    SolrParentDocFields.DYN_TEMP_TERM_NAME_STORE);
	    String termName = CommonBackupConverter.getStringValue(tempTermNameKey, fieldValues);
	    term.setTempText(termName);
	}

	// COMMENTS
	suffix = SolrParentDocFields.DYN_COMMENT_MULTI_STORE;
	String commentsKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Object commentValue = fieldValues.get(commentsKey);
	if (commentValue != null) {
	    Set<DbComment> termComments = term.getComments();
	    if (termComments == null) {
		termComments = new HashSet<>();
	    }

	    if (commentValue instanceof List) {
		@SuppressWarnings("unchecked")
		List<String> comments = (List<String>) commentValue;
		for (String value : comments) {
		    addTermComment(term, termComments, value);
		}
	    } else if (commentValue instanceof String) {
		String value = (String) noteValue;
		addTermComment(term, termComments, value);
	    }

	    term.setComments(termComments);
	}

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

package org.gs4tr.termmanager.persistence.solr.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Comment;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class TermEntryConverter {

    private static final String DYN_ATTR_SUFIX = SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE.substring(1);
    private static final String DYN_NOTE_SUFIX = SolrParentDocFields.DYN_NOTE_MULTI_STORE.substring(1);

    public static List<TermEntry> convertToHistory(SolrDocumentList list) {
	List<TermEntry> history = new ArrayList<>();

	if (list == null) {
	    return history;
	}

	for (final SolrDocument doc : list) {
	    TermEntry revision = convertToTermEntry(false, new DocumentCallBack() {
		@Override
		public Collection<String> getFieldNames() {
		    return doc.getFieldNames();
		}

		@Override
		public Object getFieldValue(String fieldName) {
		    if (isTermDescriptionField(fieldName)) {
			/*
			 * [issue TERII-4013]: Get all the values for term description fields.
			 */
			return getFieldValues(fieldName);
		    }
		    return doc.getFieldValue(fieldName);
		}

		@Override
		public Collection<Object> getFieldValues(String fieldName) {
		    return doc.getFieldValues(fieldName);
		}

		private boolean isTermDescriptionField(final String fieldName) {
		    return fieldName.contains(DYN_ATTR_SUFIX) || fieldName.contains(DYN_NOTE_SUFIX);
		}
	    });
	    history.add(revision);
	}

	return history;
    }

    public static List<TermEntry> convertToTermEntries(SolrDocumentList list) {
	return convertToTermEntries(list, null, false);
    }

    public static List<TermEntry> convertToTermEntries(SolrDocumentList list, Set<String> languageIds,
	    boolean fetchDeleted) {
	List<TermEntry> entries = new ArrayList<TermEntry>();

	if (list != null) {
	    Iterator<SolrDocument> iterator = list.iterator();
	    while (iterator.hasNext()) {
		entries.add(convertToTermEntry(iterator.next(), languageIds, fetchDeleted));
	    }
	}

	return entries;
    }

    public static TermEntry convertToTermEntry(boolean isHistory, DocumentCallBack callback) {

	TermEntry termEntry = convertBasicTermEntry(callback);

	List<TermField> termFields = createTermFields(callback);

	Map<String, Set<Term>> languageTerms = new HashMap<>();

	for (TermField termField : termFields) {
	    Term term = convertToTerm(termEntry, termField);
	    if (term.getUuId() == null && !isHistory) {
		continue;
	    }

	    String languageId = termField.getLanguageId();
	    Set<Term> terms = languageTerms.get(languageId);
	    if (terms == null) {
		terms = new HashSet<>();
		languageTerms.put(languageId, terms);
	    }
	    terms.add(term);

	}

	termEntry.setLanguageTerms(languageTerms);

	return termEntry;
    }

    public static TermEntry convertToTermEntry(SolrDocument doc) {
	return convertToTermEntry(doc, null, false);
    }

    public static TermEntry convertToTermEntry(final SolrDocument doc, Set<String> languageIds, boolean fetchDeleted) {
	TermEntry termEntry = convertToTermEntry(false, new DocumentCallBack() {
	    @Override
	    public Collection<String> getFieldNames() {
		return doc.getFieldNames();
	    }

	    @Override
	    public Object getFieldValue(String fieldName) {
		return doc.getFieldValue(fieldName);
	    }

	    @Override
	    public Collection<Object> getFieldValues(String fieldName) {
		return doc.getFieldValues(fieldName);
	    }
	});

	if (fetchDeleted) {
	    fetchDeletedTerms(doc, termEntry, languageIds);
	}

	return termEntry;
    }

    private static void addDescription(String baseType, String value, Term term) {
	if (!value.contains(SolrGlossaryAdapter.RS)) {
	    return;
	}
	Iterator<String> iterator = SolrDocHelper.split(SolrGlossaryAdapter.RS, value);

	Description description = new Description(baseType, iterator.next(), iterator.next());
	description.setUuid(iterator.next());
	term.addDescription(description);
    }

    private static TermEntry convertBasicTermEntry(DocumentCallBack callback) {
	TermEntry entity = new TermEntry();

	String id = getStringValue(SolrConstants.ID_FIELD, callback);
	entity.setUuId(id);

	Long revisionId = getLongValue(SolrParentDocFields.REVISION, callback);
	entity.setRevisionId(revisionId);

	String action = getStringValue(SolrParentDocFields.HISTORY_ACTION, callback);
	if (StringUtils.isNotEmpty(action)) {
	    entity.setAction(Action.valueOf(action));
	}

	Long projectId = getLongValue(SolrParentDocFields.PROJECT_ID_INDEX_STORE, callback);
	entity.setProjectId(projectId);

	String shortcode = getStringValue(SolrParentDocFields.SHORTCODE_INDEX_STORE, callback);
	entity.setShortCode(shortcode);

	String projectName = getStringValue(SolrParentDocFields.PROJECT_NAME_INDEX_STORE, callback);
	entity.setProjectName(projectName);

	Long dateCreated = getLongValue(SolrParentDocFields.DATE_CREATED_INDEX_STORE, callback);
	entity.setDateCreated(dateCreated);

	Long dateModified = getLongValue(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, callback);
	entity.setDateModified(dateModified);

	String userCreated = getStringValue(SolrParentDocFields.USER_CREATED_INDEX, callback);
	entity.setUserCreated(userCreated);

	String userModified = getStringValue(SolrParentDocFields.USER_MODIFIED_SORT, callback);
	entity.setUserModified(userModified);

	Long submissionId = getLongValue(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE, callback);
	entity.setSubmissionId(submissionId);

	String submissionName = getStringValue(SolrParentDocFields.SUBMISSION_NAME_STORE, callback);
	entity.setSubmissionName(submissionName);

	String submitter = getStringValue(SolrParentDocFields.SUBMITTER_SORT, callback);
	entity.setSubmitter(submitter);

	String parentId = getStringValue(SolrParentDocFields.PARENT_ID_STORE, callback);
	entity.setParentUuId(parentId);

	if (callback.getFieldValues(SolrParentDocFields.ATTRIBUTE_MULTI_STORE) != null) {
	    Collection<Object> attributes = callback.getFieldValues(SolrParentDocFields.ATTRIBUTE_MULTI_STORE);
	    entity.setDescriptions(convertDescriptions(attributes));
	}

	return entity;
    }

    private static Set<Description> convertDescriptions(Collection<Object> collection) {
	Set<Description> descriptions = new HashSet<>();
	for (Object item : collection) {
	    String typeValue = (String) item;
	    Iterator<String> iterator = SolrDocHelper.split(SolrGlossaryAdapter.RS, typeValue);
	    Description description = new Description();
	    description.setType(iterator.next());
	    description.setValue(iterator.next());
	    description.setUuid(iterator.next());

	    descriptions.add(description);
	}
	return descriptions;
    }

    private static void convertSubmissionTerm(TermField termField, Term term) {
	String key = termField.getKey();
	String languageId = termField.getLanguageId();

	boolean first = term.isFirst();

	Map<String, Object> fieldValues = termField.getFieldNames();

	// PARENT_ID
	String suffix = SolrParentDocFields.DYN_PARENT_ID_STORE;
	String parentIdKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String parentId = getStringValue(parentIdKey, fieldValues);
	term.setParentUuId(parentId);

	// ASSIGNEE
	suffix = SolrParentDocFields.DYN_ASSIGNEE_SORT;
	String assigneKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String assignee = getStringValue(assigneKey, fieldValues);
	term.setAssignee(assignee);

	// CANCELED
	suffix = SolrParentDocFields.DYN_CANCELED_STORE;
	String canceledKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean canceled = getBooleanValue(canceledKey, fieldValues);
	term.setCanceled(canceled);

	// COMMENTS
	suffix = SolrParentDocFields.DYN_COMMENT_MULTI_STORE;
	String commentsKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	if (fieldValues.get(commentsKey) != null) {
	    @SuppressWarnings("unchecked")
	    List<String> comments = (List<String>) fieldValues.get(commentsKey);
	    for (String value : comments) {
		if (value.contains(SolrGlossaryAdapter.RS)) {
		    Iterator<String> iterator = SolrDocHelper.split(SolrGlossaryAdapter.RS, value);

		    Comment comment = new Comment();
		    comment.setUser(iterator.next());
		    comment.setText(iterator.next());
		    term.addComment(comment);
		}
	    }
	}

	// COMMITED
	suffix = SolrParentDocFields.DYN_COMMITED_STORE;
	String commitedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean commited = getBooleanValueDefault(commitedKey, fieldValues, Boolean.TRUE);
	term.setCommited(commited);

	// DATE_COMPLITED
	suffix = first ? SolrParentDocFields.DYN_DATE_COMPLETED_SORT : SolrParentDocFields.DYN_DATE_COMPLETED_STORE;
	String dateCompletedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long dateCompleted = getLongValue(dateCompletedKey, fieldValues);
	term.setDateCompleted(dateCompleted);

	// DATE_SUBMITTED
	suffix = first ? SolrParentDocFields.DYN_DATE_SUBMITTED_SORT : SolrParentDocFields.DYN_DATE_SUBMITTED_STORE;
	String dateSubmittedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long dateSubmitted = getLongValue(dateSubmittedKey, fieldValues);
	term.setDateSubmitted(dateSubmitted);

	// IN_TRANSLATION_AS_SOURCE
	suffix = SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE;
	String inTranslationAsSourceKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean inTranslationAsSource = getBooleanValue(inTranslationAsSourceKey, fieldValues);
	term.setInTranslationAsSource(inTranslationAsSource);

	// REVIEW_REQUIRED
	suffix = SolrParentDocFields.DYN_REVIEW_REQUIRED_STORE;
	String reviewRequiredKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean reviewRequired = getBooleanValue(reviewRequiredKey, fieldValues);
	term.setReviewRequired(reviewRequired);

	// TEMP_TEXT
	suffix = first ? SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW
		: SolrParentDocFields.DYN_TEMP_TERM_NAME_STORE;
	String tempTermNameKey = first ? SolrDocHelper.createDynamicFieldName(languageId, suffix)
		: SolrDocHelper.createDynamicFieldName(key, suffix);
	String tempTermName = getStringValue(tempTermNameKey, fieldValues);
	term.setTempText(tempTermName);
    }

    private static Term convertToTerm(TermEntry termEntry, TermField termField) {
	String key = termField.getKey();
	String languageId = termField.getLanguageId();

	int num = SolrDocHelper.extractNumber(key);
	boolean first = num == 0;

	Term term = new Term();
	term.setLanguageId(languageId);
	term.setFirst(first);

	term.setTermEntry(termEntry);
	term.setProjectId(termEntry.getProjectId());
	term.setTermEntryId(termEntry.getUuId());
	term.setSubmissionId(termEntry.getSubmissionId());
	term.setSubmissionName(termEntry.getSubmissionName());
	term.setSubmitter(termEntry.getSubmitter());

	Map<String, Object> fieldValues = termField.getFieldNames();

	// ID
	String suffix = first ? SolrParentDocFields.DYN_TERM_ID_SORT : SolrParentDocFields.DYN_TERM_ID_STORE;
	String uuidKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String uuId = getStringValue(uuidKey, fieldValues);
	term.setUuId(uuId);

	// DATE_CREATED
	suffix = first ? SolrParentDocFields.DYN_DATE_CREATED_SORT : SolrParentDocFields.DYN_DATE_CREATED_STORE;
	String dateCreatedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long dateCreated = getLongValue(dateCreatedKey, fieldValues);
	term.setDateCreated(dateCreated);

	// DATE_MODIFIED
	suffix = first ? SolrParentDocFields.DYN_DATE_MODIFIED_SORT : SolrParentDocFields.DYN_DATE_MODIFIED_STORE;
	String dateModifiedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long dateModified = getLongValue(dateModifiedKey, fieldValues);
	term.setDateModified(dateModified);

	// ATTRIBUTES
	suffix = SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE;
	String attributesKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Object attributeValue = fieldValues.get(attributesKey);
	if (attributeValue != null) {
	    if (attributeValue instanceof Collection) {
		@SuppressWarnings("unchecked")
		Collection<String> attributes = (Collection<String>) attributeValue;
		for (String value : attributes) {
		    addDescription(Description.ATTRIBUTE, value, term);
		}
	    } else if (attributeValue instanceof String) {
		String value = (String) attributeValue;
		addDescription(Description.ATTRIBUTE, value, term);
	    }
	}

	// NOTES
	suffix = SolrParentDocFields.DYN_NOTE_MULTI_STORE;
	String notesKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Object noteValue = fieldValues.get(notesKey);
	if (noteValue != null) {
	    if (noteValue instanceof Collection) {
		@SuppressWarnings("unchecked")
		Collection<String> notes = (Collection<String>) noteValue;
		for (String value : notes) {
		    addDescription(Description.NOTE, value, term);
		}
	    } else if (noteValue instanceof String) {
		String value = (String) noteValue;
		addDescription(Description.NOTE, value, term);
	    }

	}

	// DISABLED
	suffix = SolrParentDocFields.DYN_DISABLED_STORE;
	String disabledKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean disabled = getBooleanValue(disabledKey, fieldValues);
	term.setDisabled(disabled);

	// FORBIDDEN
	suffix = SolrParentDocFields.DYN_FORBIDDEN_STORE;
	String forbiddenKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Boolean forbidden = getBooleanValue(forbiddenKey, fieldValues);
	term.setForbidden(forbidden);

	// NAME
	suffix = first ? SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW : SolrParentDocFields.DYN_TERM_NAME_STORE;
	String termNameKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String termName = getStringValue(termNameKey, fieldValues);
	term.setName(termName);

	// STATUS
	suffix = first ? SolrParentDocFields.DYN_STATUS_SORT : SolrParentDocFields.DYN_STATUS_STORE;
	String statusKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String status = getStringValue(statusKey, fieldValues);
	term.setStatus(status);

	// STATUS_OLD
	suffix = SolrParentDocFields.DYN_STATUS_OLD_STORE;
	String statusOldKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String statusOld = getStringValue(statusOldKey, fieldValues);
	term.setStatusOld(statusOld);

	// USER_CREATED
	suffix = first ? SolrParentDocFields.DYN_USER_CREATED_SORT : SolrParentDocFields.DYN_USER_CREATED_STORE;
	String userCreatedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String userCreated = getStringValue(userCreatedKey, fieldValues);
	term.setUserCreated(userCreated);

	// USER_MODIFIED
	suffix = first ? SolrParentDocFields.DYN_USER_MODIFIED_SORT : SolrParentDocFields.DYN_USER_MODIFIED_STORE;
	String userModifiedKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	String userModified = getStringValue(userModifiedKey, fieldValues);
	term.setUserModified(userModified);

	// USER_LATEST_CHANGES
	suffix = SolrParentDocFields.DYN_USER_LATEST_CHANGE_STORE;
	String userLatestChangeKey = SolrDocHelper.createDynamicFieldName(key, suffix);
	Long userLatestChange = getLongValue(userLatestChangeKey, fieldValues);
	term.setUserLatestChange(userLatestChange);

	convertSubmissionTerm(termField, term);

	return term;
    }

    private static List<TermField> createTermFields(DocumentCallBack callback) {
	List<TermField> list = new ArrayList<TermField>();

	Collection<String> fieldNames = callback.getFieldNames();
	for (String fieldName : fieldNames) {
	    String key = SolrDocHelper.getBeforeUnderscore(fieldName);
	    String languageId = SolrDocHelper.extractLocaleCode(fieldName);
	    if (!Locale.checkLocale(languageId)) {
		continue;
	    }

	    Object fieldValue = callback.getFieldValue(fieldName);

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

    private static void fetchDeletedTerms(SolrDocument doc, TermEntry termEntry, Set<String> languageIds) {

	if (languageIds == null) {
	    return;
	}

	for (String languageId : languageIds) {
	    String deletedTermsField = SolrDocHelper.createDynamicFieldName(languageId,
		    SolrParentDocFields.DYN_DELETED_TERMS);
	    Collection<Object> deletedTerms = doc.getFieldValues(deletedTermsField);
	    if (deletedTerms == null) {
		continue;
	    }

	    for (Object item : deletedTerms) {
		String deletedId = (String) item;

		Term deletedTerm = new Term();
		deletedTerm.setUuId(deletedId);
		deletedTerm.setLanguageId(languageId);
		deletedTerm.setDisabled(Boolean.TRUE);
		deletedTerm.setName(deletedId);

		termEntry.addTerm(deletedTerm);
	    }
	}
    }

    private static Boolean getBooleanValue(String fieldName, Map<String, Object> fieldValues) {
	return fieldValues.get(fieldName) != null ? Boolean.valueOf(fieldValues.get(fieldName).toString())
		: Boolean.FALSE;
    }

    private static Boolean getBooleanValueDefault(String fieldName, Map<String, Object> fieldValues,
	    Boolean defaultValue) {
	return fieldValues.get(fieldName) != null ? Boolean.valueOf(fieldValues.get(fieldName).toString())
		: defaultValue;
    }

    private static Long getLongValue(String fieldName, DocumentCallBack callback) {
	return callback.getFieldValue(fieldName) != null ? Long.valueOf(callback.getFieldValue(fieldName).toString())
		: null;
    }

    private static Long getLongValue(String fieldName, Map<String, Object> fieldValues) {
	return fieldValues.get(fieldName) != null ? Long.valueOf(fieldValues.get(fieldName).toString()) : null;
    }

    private static String getStringValue(String fieldName, DocumentCallBack callback) {
	return callback.getFieldValue(fieldName) != null ? (String) callback.getFieldValue(fieldName) : null;
    }

    private static String getStringValue(String fieldName, Map<String, Object> fieldValues) {
	return fieldValues.get(fieldName) != null ? (String) fieldValues.get(fieldName) : null;
    }

    public interface DocumentCallBack {

	Collection<String> getFieldNames();

	Object getFieldValue(String fieldName);

	Collection<Object> getFieldValues(String fieldName);
    }

    public static class TermField {

	private Map<String, Object> _fieldNames;

	private String _key;

	private String _languageId;

	public TermField(String key, String languageId) {
	    _key = key;
	    _languageId = languageId;
	    _fieldNames = new HashMap<>();
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    TermField other = (TermField) obj;
	    if (_key == null) {
		if (other._key != null)
		    return false;
	    } else if (!_key.equals(other._key))
		return false;
	    if (_languageId == null) {
		return other._languageId == null;
	    } else
		return _languageId.equals(other._languageId);
	}

	public Map<String, Object> getFieldNames() {
	    return _fieldNames;
	}

	public String getKey() {
	    return _key;
	}

	public String getLanguageId() {
	    return _languageId;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((_key == null) ? 0 : _key.hashCode());
	    result = prime * result + ((_languageId == null) ? 0 : _languageId.hashCode());
	    return result;
	}

	public void setFieldNames(Map<String, Object> fieldNames) {
	    _fieldNames = fieldNames;
	}

	public void setKey(String key) {
	    _key = key;
	}

	public void setLanguageId(String languageId) {
	    _languageId = languageId;
	}
    }
}

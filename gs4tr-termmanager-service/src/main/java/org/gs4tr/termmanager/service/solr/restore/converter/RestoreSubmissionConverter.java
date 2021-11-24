package org.gs4tr.termmanager.service.solr.restore.converter;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.foundation3.solr.util.SolrDocumentBuilder;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbComment;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbPriority;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryDescription;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class RestoreSubmissionConverter {

    private static final String RS = "\u241E";

    public static SolrInputDocument convertFromDbTermEntryToSolrDoc(DbSubmissionTermEntry entry) {
	SolrDocumentBuilder builder = SolrDocumentBuilder.newInstance();

	builder.set(SolrParentDocFields.HISTORY_ACTION, entry.getAction());

	String termEntryId = entry.getUuId();
	if (termEntryId != null) {
	    builder.set(SolrConstants.ID_FIELD, termEntryId);
	}

	// if it's a submission term entry
	String parentUuId = entry.getParentUuId();
	if (parentUuId != null) {
	    builder.set(SolrParentDocFields.PARENT_ID_STORE, parentUuId);
	}

	Long submissionId = entry.getSubmissionId();
	if (submissionId != null) {
	    builder.set(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE, submissionId);
	}

	String submissionName = entry.getSubmissionName();
	if (submissionName != null) {
	    builder.set(SolrParentDocFields.SUBMISSION_NAME_STORE, submissionName);
	}

	String submitter = entry.getSubmitter();
	if (submitter != null) {
	    builder.set(SolrParentDocFields.SUBMITTER_SORT, submitter);
	}

	Long projectId = entry.getProjectId();
	if (projectId != null) {
	    builder.set(SolrParentDocFields.PROJECT_ID_INDEX_STORE, projectId);
	}

	String shortCode = entry.getShortCode();
	if (shortCode != null) {
	    builder.set(SolrParentDocFields.SHORTCODE_INDEX_STORE, shortCode);
	}

	String projectName = entry.getProjectName();
	if (projectName != null) {
	    builder.set(SolrParentDocFields.PROJECT_NAME_INDEX_STORE, projectName);
	}

	Date dateCreated = entry.getDateCreated();
	if (dateCreated != null) {
	    builder.set(SolrParentDocFields.DATE_CREATED_INDEX_STORE, dateCreated.getTime());
	}

	Date dateModified = entry.getDateModified();
	if (dateModified != null) {
	    builder.set(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, dateModified.getTime());
	}

	String userCreated = entry.getUserCreated();
	if (userCreated != null) {
	    builder.set(SolrParentDocFields.USER_CREATED_INDEX, userCreated);
	}

	String userModified = entry.getUserModified();
	if (userModified != null) {
	    builder.set(SolrParentDocFields.USER_MODIFIED_SORT, userModified);
	}

	Set<DbSubmissionTermEntryDescription> descriptions = entry.getDescriptions();
	Set<String> descs = new HashSet<>();
	if (CollectionUtils.isNotEmpty(descriptions)) {
	    for (DbSubmissionTermEntryDescription description : descriptions) {
		addTermEntryDescription(descs, description);
	    }
	}

	String descFiled = SolrParentDocFields.ATTRIBUTE_MULTI_STORE;
	if (!descs.isEmpty()) {
	    builder.set(descFiled, descs);
	}

	Set<String> languageIds = new HashSet<String>();
	Set<DbSubmissionTerm> terms = entry.getSubmissionTerms();
	if (CollectionUtils.isNotEmpty(terms)) {
	    Map<String, Set<DbSubmissionTerm>> grouped = groupTermsByLanguageId(terms);
	    languageIds.addAll(grouped.keySet());

	    for (Entry<String, Set<DbSubmissionTerm>> groupedEntry : grouped.entrySet()) {
		String languageId = groupedEntry.getKey();
		addTermFieldsToParent(builder, languageId, groupedEntry.getValue());
	    }

	    builder.set(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, languageIds);
	}
	builder.set(SolrParentDocFields.REVISION, entry.getRevisionId());

	return builder.get();
    }

    private static void addCommentFieldToParent(DbComment comment, Set<String> comments) {
	String user = comment.getUser();
	String text = comment.getText();

	String userText = user + RS + text;
	comments.add(userText);
    }

    private static void addSubmissionTermFieldsToParent(SolrDocumentBuilder builder, String languageKey,
	    DbSubmissionTerm term) {

	Boolean first = term.getFirst();

	// if it's a submission term
	String parentUuId = term.getParentUuId();
	if (parentUuId != null) {
	    String dynField = SolrParentDocFields.DYN_PARENT_ID_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), parentUuId);
	} else {
	    return;
	}

	String assignee = term.getAssignee();
	if (assignee != null) {
	    String dynField = SolrParentDocFields.DYN_ASSIGNEE_SORT;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), assignee);
	}

	Set<DbComment> comments = term.getComments();
	Set<String> comm = new HashSet<>();
	if (comments != null) {
	    for (DbComment comment : comments) {
		addCommentFieldToParent(comment, comm);
	    }
	}

	String commentField = SolrDocHelper.createDynamicFieldName(languageKey,
		SolrParentDocFields.DYN_COMMENT_MULTI_STORE);
	if (!comm.isEmpty()) {
	    builder.set(commentField, comm);
	}
	Boolean commited = term.getCommited();
	if (commited != null) {
	    String dynField = SolrParentDocFields.DYN_COMMITED_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), commited);
	}

	Boolean canceled = term.getCanceled();
	if (canceled != null) {
	    String dynField = SolrParentDocFields.DYN_CANCELED_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), canceled);
	}

	Date dateCompleted = term.getDateCompleted();
	if (dateCompleted != null) {
	    String dynField = first ? SolrParentDocFields.DYN_DATE_COMPLETED_SORT
		    : SolrParentDocFields.DYN_DATE_COMPLETED_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), dateCompleted.getTime());
	}

	Date dateSubmitted = term.getDateSubmitted();
	if (dateSubmitted != null) {
	    String dynField = first ? SolrParentDocFields.DYN_DATE_SUBMITTED_SORT
		    : SolrParentDocFields.DYN_DATE_SUBMITTED_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), dateSubmitted.getTime());
	}

	Boolean reviewRequired = term.getReviewRequired();
	if (reviewRequired != null) {
	    String dynField = SolrParentDocFields.DYN_REVIEW_REQUIRED_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), reviewRequired);
	}

	DbPriority priority = term.getPriority();
	if (priority != null) {
	    // assignee priority
	    String dynField1 = first ? SolrParentDocFields.DYN_ASSIGNEE_PRIORITY_SORT
		    : SolrParentDocFields.DYN_ASSIGNEE_PRIORITY_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField1), priority.getAssigneePriority());
	    // submitter priority
	    String dynField2 = first ? SolrParentDocFields.DYN_SUBMITTER_PRIORITY_SORT
		    : SolrParentDocFields.DYN_SUBMITTER_PRIORITY_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField2), priority.getSubmitterPriority());
	}

	String tempText = term.getTempText();
	if (tempText != null) {
	    String tempTermNameField = first
		    ? SolrDocHelper.createDynamicFieldName(term.getLanguageId(),
			    SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW)
		    : SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TEMP_TERM_NAME_STORE);
	    builder.set(tempTermNameField, tempText);
	}
    }

    private static void addTermDescription(Set<String> descs, DbSubmissionTermDescription description) {
	String type = description.getType();
	String value = description.getValueAsString();
	String uuId = description.getUuid() != null ? description.getUuid() : UUID.randomUUID().toString();

	String builder = type + RS + value + RS + uuId;
	descs.add(builder);
    }

    private static void addTermEntryDescription(Set<String> descs, DbSubmissionTermEntryDescription description) {
	String type = description.getType();
	String value = description.getValueAsString();
	String uuId = description.getUuid() != null ? description.getUuid() : UUID.randomUUID().toString();

	String builder = type + RS + value + RS + uuId;
	descs.add(builder);
    }

    private static void addTermFieldsToParent(SolrDocumentBuilder builder, String languageId,
	    Set<DbSubmissionTerm> terms) {
	int index = 0;

	String languageKey;

	for (DbSubmissionTerm term : terms) {
	    boolean first = term.getFirst();
	    if (first) {
		languageKey = term.getLanguageId();
	    } else {
		index++;
		languageKey = term.getLanguageId() + index;
	    }

	    String termId = term.getUuId();
	    if (termId != null) {
		String idField = first ? SolrParentDocFields.DYN_TERM_ID_SORT : SolrParentDocFields.DYN_TERM_ID_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, idField), termId);
	    }

	    String status = term.getStatus();
	    if (status != null) {
		String statusField = first ? SolrParentDocFields.DYN_STATUS_SORT : SolrParentDocFields.DYN_STATUS_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, statusField), status);
	    }

	    String statusOld = term.getStatusOld();
	    if (statusOld != null) {
		String statusField = SolrParentDocFields.DYN_STATUS_OLD_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, statusField), statusOld);
	    }

	    Boolean inTranslationAsSource = term.getInTranslationAsSource();
	    if (inTranslationAsSource != null) {
		String dynField = SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), inTranslationAsSource);
	    }

	    String userCreated = term.getUserCreated();
	    if (userCreated != null) {
		String userCreatedField = first ? SolrParentDocFields.DYN_USER_CREATED_SORT
			: SolrParentDocFields.DYN_USER_CREATED_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, userCreatedField), userCreated);
	    }

	    String userModified = term.getUserModified();
	    if (userModified != null) {
		String userModifiedField = first ? SolrParentDocFields.DYN_USER_MODIFIED_SORT
			: SolrParentDocFields.DYN_USER_MODIFIED_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, userModifiedField), userModified);
	    }

	    Date dateCreated = term.getDateCreated();
	    if (dateCreated != null) {
		String dateCreatedField = first ? SolrParentDocFields.DYN_DATE_CREATED_SORT
			: SolrParentDocFields.DYN_DATE_CREATED_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dateCreatedField), dateCreated.getTime());
	    }

	    Date dateModified = term.getDateModified();
	    if (dateModified != null) {
		String dateModifiedField = first ? SolrParentDocFields.DYN_DATE_MODIFIED_SORT
			: SolrParentDocFields.DYN_DATE_MODIFIED_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dateModifiedField),
			dateModified.getTime());
	    }

	    String name = term.getNameAsString();
	    if (name != null) {
		String termNameField = first
			? SolrDocHelper.createDynamicFieldName(languageId,
				SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW)
			: SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TERM_NAME_STORE);
		builder.set(termNameField, name);
	    }

	    Boolean forbidden = term.getForbidden();
	    if (forbidden != null) {
		String forbiddenField = SolrParentDocFields.DYN_FORBIDDEN_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, forbiddenField), forbidden);
	    }

	    String disabledField = SolrParentDocFields.DYN_DISABLED_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, disabledField), Boolean.FALSE);

	    Set<String> attr = new HashSet<>();
	    Set<String> notes = new HashSet<>();
	    Set<DbSubmissionTermDescription> termDescriptions = term.getDescriptions();
	    if (CollectionUtils.isNotEmpty(termDescriptions)) {
		for (DbSubmissionTermDescription description : termDescriptions) {
		    if (DbSubmissionTermDescription.NOTE.equals(description.getBaseType())) {
			addTermDescription(notes, description);
		    } else {
			addTermDescription(attr, description);
		    }
		}
	    }

	    String attributeField = SolrDocHelper.createDynamicFieldName(languageKey,
		    SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE);
	    if (!attr.isEmpty()) {
		builder.set(attributeField, attr);
	    }

	    String noteField = SolrDocHelper.createDynamicFieldName(languageKey,
		    SolrParentDocFields.DYN_NOTE_MULTI_STORE);
	    if (!notes.isEmpty()) {
		builder.set(noteField, notes);
	    }
	    addSubmissionTermFieldsToParent(builder, languageKey, term);
	}
    }

    private static Map<String, Set<DbSubmissionTerm>> groupTermsByLanguageId(Set<DbSubmissionTerm> terms) {
	Map<String, Set<DbSubmissionTerm>> grouped = new HashMap<>();

	for (DbSubmissionTerm term : terms) {
	    String languageId = term.getLanguageId();

	    Set<DbSubmissionTerm> dbTerms = grouped.computeIfAbsent(languageId, k -> new HashSet<>());

	    dbTerms.add(term);
	}

	return grouped;
    }
}

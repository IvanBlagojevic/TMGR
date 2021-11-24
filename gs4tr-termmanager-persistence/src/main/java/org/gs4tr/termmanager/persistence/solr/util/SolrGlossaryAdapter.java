package org.gs4tr.termmanager.persistence.solr.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.foundation3.solr.util.SolrDocumentBuilder;
import org.gs4tr.termmanager.model.glossary.Comment;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Priority;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class SolrGlossaryAdapter {

    public static final String RS = "\u241E";

    private int _exportBatchSize;

    private int _importBatchSize;

    public SolrInputDocument buildInputDocumentFromTermEntry(TermEntry termEntry) {
	SolrDocumentBuilder builder = buildDocument(termEntry);
	return builder.get();
    }

    public int getExportBatchSize() {
	return _exportBatchSize;
    }

    public int getImportBatchSize() {
	return _importBatchSize;
    }

    public void setExportBatchSize(int exportBatchSize) {
	_exportBatchSize = exportBatchSize;
    }

    public void setImportBatchSize(int importBatchSize) {
	_importBatchSize = importBatchSize;
    }

    private void addCommentFieldToParent(Comment comment, Set<String> comments) {
	String user = comment.getUser();
	String text = comment.getText();

	StringBuilder builder = new StringBuilder();
	builder.append(user);
	builder.append(RS);
	builder.append(text);
	String userText = builder.toString();
	comments.add(userText);
    }

    private void addDescriptionFieldToParent(Set<String> descs, Description description) {
	String type = description.getType();
	String value = description.getValue();
	String uuId = description.getUuid() != null ? description.getUuid() : UUID.randomUUID().toString();

	StringBuilder builder = new StringBuilder();
	builder.append(type);
	builder.append(RS);
	builder.append(value);
	builder.append(RS);
	builder.append(uuId);

	descs.add(builder.toString());
    }

    private void addSubmissionTermFieldsToParent(SolrDocumentBuilder builder, String languageKey, Term term) {
	/*
	 * 16-November-2016, as per [Bug#TERII-4444]: After rebuild index from V2
	 * backup, for migrated submissions, regular terms that are in translation as
	 * source does not have parentUuId. For these terms, we need to update this
	 * status flag before checking that parentUuid exist.
	 */
	Boolean inTranslationAsSource = term.getInTranslationAsSource();
	if (inTranslationAsSource != null) {
	    String dynField = SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), inTranslationAsSource);
	}

	Boolean first = term.isFirst();

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

	Set<Comment> comments = term.getComments();
	Set<String> comm = new HashSet<String>();
	if (comments != null) {
	    for (Comment comment : comments) {
		addCommentFieldToParent(comment, comm);
	    }
	}

	String commentField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_COMMENT_MULTI_STORE);
	if (comm.isEmpty()) {
	    builder.setClear(commentField);
	} else {
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

	Long dateCompleted = term.getDateCompleted();
	if (dateCompleted != null) {
	    String dynField = first ? SolrParentDocFields.DYN_DATE_COMPLETED_SORT : SolrParentDocFields.DYN_DATE_COMPLETED_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), dateCompleted);
	}

	Long dateSubmitted = term.getDateSubmitted();
	if (dateSubmitted != null) {
	    String dynField = first ? SolrParentDocFields.DYN_DATE_SUBMITTED_SORT : SolrParentDocFields.DYN_DATE_SUBMITTED_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), dateSubmitted);
	}

	Boolean reviewRequired = term.getReviewRequired();
	if (reviewRequired != null) {
	    String dynField = SolrParentDocFields.DYN_REVIEW_REQUIRED_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), reviewRequired);
	}

	Priority priority = term.getPriority();
	if (priority != null) {
	    // assignee priority
	    String dynField1 = first ? SolrParentDocFields.DYN_ASSIGNEE_PRIORITY_SORT : SolrParentDocFields.DYN_ASSIGNEE_PRIORITY_STORE;
	    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField1), priority.getAssigneePriority());
	    // submitter priority
	    String dynField2 = first ? SolrParentDocFields.DYN_SUBMITTER_PRIORITY_SORT : SolrParentDocFields.DYN_SUBMITTER_PRIORITY_STORE;
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

    private void addTermFieldsToParent(SolrDocumentBuilder builder, String languageCode, Set<Term> terms) {

	String languageKey = languageCode;
	if (CollectionUtils.isNotEmpty(terms)) {
	    int index = 0;
	    for (Term term : terms) {
		boolean first = term.isFirst();
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

		Long dateCreated = term.getDateCreated();
		if (dateCreated != null) {
		    String dateCreatedField = first ? SolrParentDocFields.DYN_DATE_CREATED_SORT
			    : SolrParentDocFields.DYN_DATE_CREATED_STORE;
		    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dateCreatedField), dateCreated);
		}

		Long dateModified = term.getDateModified();
		if (dateModified != null) {
		    String dateModifiedField = first ? SolrParentDocFields.DYN_DATE_MODIFIED_SORT
			    : SolrParentDocFields.DYN_DATE_MODIFIED_STORE;
		    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dateModifiedField), dateModified);
		}

		String name = term.getName();
		if (name != null) {
		    String termNameField = first
			    ? SolrDocHelper.createDynamicFieldName(languageCode,
				    SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW)
			    : SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TERM_NAME_STORE);
		    builder.set(termNameField, name);
		}

		Boolean forbidden = term.isForbidden();
		if (forbidden != null) {
		    String forbiddenField = SolrParentDocFields.DYN_FORBIDDEN_STORE;
		    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, forbiddenField), forbidden);
		}

		Boolean disabled = term.isDisabled();
		if (disabled != null) {
		    String disabledField = SolrParentDocFields.DYN_DISABLED_STORE;
		    builder.set(SolrDocHelper.createDynamicFieldName(languageKey, disabledField), disabled);
		}

		String userLatestChangeField = SolrDocHelper.createDynamicFieldName(languageKey,
			SolrParentDocFields.DYN_USER_LATEST_CHANGE_STORE);

		Long userLatestChange = term.getUserLatestChange();
		if (userLatestChange == null) {
		    // 7-October-2016, as per [TERII-4346]:
		    builder.setClear(userLatestChangeField);
		} else {
		    builder.set(userLatestChangeField, userLatestChange);
		}

		Set<String> attr = new HashSet<String>();
		Set<String> notes = new HashSet<String>();
		Set<Description> termDescriptions = term.getDescriptions();
		if (termDescriptions != null && !termDescriptions.isEmpty()) {
		    for (Description description : termDescriptions) {
			if (Description.NOTE.equals(description.getBaseType())) {
			    addDescriptionFieldToParent(notes, description);
			} else {
			    addDescriptionFieldToParent(attr, description);
			}
		    }
		}

		String attributeField = SolrDocHelper.createDynamicFieldName(languageKey,
			SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE);
		if (attr.isEmpty()) {
		    builder.setClear(attributeField);
		} else {
		    builder.set(attributeField, attr);
		}

		String noteField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_NOTE_MULTI_STORE);
		if (notes.isEmpty()) {
		    builder.setClear(noteField);
		} else {
		    builder.set(noteField, notes);
		}

		addSubmissionTermFieldsToParent(builder, languageKey, term);
	    }
	}
    }

    private SolrDocumentBuilder buildDocument(TermEntry termEntry) {
	SolrDocumentBuilder builder = SolrDocumentBuilder.newInstance();

	builder.set(SolrParentDocFields.HISTORY_ACTION, termEntry.getAction().name());

	String termEntryId = termEntry.getUuId();
	if (termEntryId != null) {
	    builder.set(SolrConstants.ID_FIELD, termEntryId);
	}

	// if it's a submission term entry
	String parentUuId = termEntry.getParentUuId();
	if (parentUuId != null) {
	    builder.set(SolrParentDocFields.PARENT_ID_STORE, parentUuId);
	}

	Long projectId = termEntry.getProjectId();
	if (projectId != null) {
	    builder.set(SolrParentDocFields.PROJECT_ID_INDEX_STORE, projectId);
	}

	Long submissionId = termEntry.getSubmissionId();
	if (submissionId != null) {
	    builder.set(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE, submissionId);
	}

	String submissionName = termEntry.getSubmissionName();
	if (submissionName != null) {
	    builder.set(SolrParentDocFields.SUBMISSION_NAME_STORE, submissionName);
	}

	String submitter = termEntry.getSubmitter();
	if (submitter != null) {
	    builder.set(SolrParentDocFields.SUBMITTER_SORT, submitter);
	}

	String shortCode = termEntry.getShortCode();
	if (shortCode != null) {
	    builder.set(SolrParentDocFields.SHORTCODE_INDEX_STORE, shortCode);
	}

	String projectName = termEntry.getProjectName();
	if (projectName != null) {
	    builder.set(SolrParentDocFields.PROJECT_NAME_INDEX_STORE, projectName);
	}

	Long dateCreated = termEntry.getDateCreated();
	if (dateCreated != null) {
	    builder.set(SolrParentDocFields.DATE_CREATED_INDEX_STORE, dateCreated);
	}

	Long dateModified = termEntry.getDateModified();
	if (dateModified != null) {
	    builder.set(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, dateModified);
	}

	String userCreated = termEntry.getUserCreated();
	if (userCreated != null) {
	    builder.set(SolrParentDocFields.USER_CREATED_INDEX, userCreated);
	}

	/*
	 * 1-November-2016, as per [Bug#TERII-4410]: We must update modification user on
	 * save/update(i.e. when translation added/edited/removed, attribute/note
	 * added/edited/removed, status edited)
	 */
	// builder.set(SolrParentDocFields.USER_MODIFIED_SORT,
	// TmUserProfile.getCurrentUserName());
	// TODO: investigate if this change reproduces TERII-4410
	builder.set(SolrParentDocFields.USER_MODIFIED_SORT, termEntry.getUserModified());

	Set<Description> descriptions = termEntry.getDescriptions();
	Set<String> descs = new HashSet<String>();
	if (descriptions != null && !descriptions.isEmpty()) {
	    for (Description description : descriptions) {
		addDescriptionFieldToParent(descs, description);
	    }
	}

	String descFiled = SolrParentDocFields.ATTRIBUTE_MULTI_STORE;
	if (descs.isEmpty()) {
	    builder.setClear(descFiled);
	} else {
	    builder.set(descFiled, descs);
	}

	// terms
	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	if (languageTerms != null && !languageTerms.isEmpty()) {
	    // store termEntry languages
	    builder.set(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, languageTerms.keySet());
	    for (Entry<String, Set<Term>> entry : languageTerms.entrySet()) {
		String languageId = entry.getKey();
		addTermFieldsToParent(builder, languageId, entry.getValue());
	    }
	}
	return builder;
    }
}

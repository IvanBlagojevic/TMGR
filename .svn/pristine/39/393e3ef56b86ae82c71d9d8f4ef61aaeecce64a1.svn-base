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
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryDescription;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class RestoreRegularConverter {

    private static final String RS = "\u241E";

    public static SolrInputDocument convertFromDbTermEntryToSolrDoc(DbTermEntry entry) {
	SolrDocumentBuilder builder = SolrDocumentBuilder.newInstance();

	builder.set(SolrParentDocFields.HISTORY_ACTION, entry.getAction());

	String termEntryId = entry.getUuId();
	if (termEntryId != null) {
	    builder.set(SolrConstants.ID_FIELD, termEntryId);
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

	Set<DbTermEntryDescription> descriptions = entry.getDescriptions();
	Set<String> descs = new HashSet<String>();
	if (CollectionUtils.isNotEmpty(descriptions)) {
	    for (DbTermEntryDescription description : descriptions) {
		addTermEntryDescription(descs, description);
	    }
	}

	String descFiled = SolrParentDocFields.ATTRIBUTE_MULTI_STORE;
	if (!descs.isEmpty()) {
	    builder.set(descFiled, descs);
	}

	Set<String> languageIds = new HashSet<String>();
	Set<DbTerm> terms = entry.getTerms();
	if (CollectionUtils.isNotEmpty(terms)) {
	    Map<String, Set<DbTerm>> grouped = groupTermsByLanguageId(terms);
	    languageIds.addAll(grouped.keySet());

	    for (Entry<String, Set<DbTerm>> groupedEntry : grouped.entrySet()) {
		String languageId = groupedEntry.getKey();
		addTermFieldsToParent(builder, languageId, groupedEntry.getValue());
	    }

	    builder.set(SolrParentDocFields.LANGUAGE_ID_INDEX_STORE_MULTI, languageIds);
	}

	builder.set(SolrParentDocFields.REVISION, entry.getRevisionId());

	return builder.get();
    }

    private static void addTermDescription(Set<String> descs, DbTermDescription description) {
	String type = description.getType();
	String value = description.getValueAsString();
	String uuId = description.getUuid() != null ? description.getUuid() : UUID.randomUUID().toString();

	StringBuilder builder = new StringBuilder();
	builder.append(type);
	builder.append(RS);
	builder.append(value);
	builder.append(RS);
	builder.append(uuId);

	descs.add(builder.toString());
    }

    private static void addTermEntryDescription(Set<String> descs, DbTermEntryDescription description) {
	String type = description.getType();
	String value = description.getValueAsString();
	String uuId = description.getUuid() != null ? description.getUuid() : UUID.randomUUID().toString();

	StringBuilder builder = new StringBuilder();
	builder.append(type);
	builder.append(RS);
	builder.append(value);
	builder.append(RS);
	builder.append(uuId);

	descs.add(builder.toString());
    }

    private static void addTermFieldsToParent(SolrDocumentBuilder builder, String languageId, Set<DbTerm> terms) {
	int index = 0;

	String languageKey = languageId;

	for (DbTerm term : terms) {
	    if (term.getDisabled() != null && term.getDisabled()) {
		continue;
	    }
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
		String statusOldField = SolrParentDocFields.DYN_STATUS_OLD_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, statusOldField), statusOld);
	    }

	    String userCreated = term.getUserCreated();
	    if (userCreated != null) {
		String userCreatedField = first ? SolrParentDocFields.DYN_USER_CREATED_SORT : SolrParentDocFields.DYN_USER_CREATED_STORE;
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
		String dateCreatedField = first ? SolrParentDocFields.DYN_DATE_CREATED_SORT : SolrParentDocFields.DYN_DATE_CREATED_STORE;
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
			? SolrDocHelper.createDynamicFieldName(languageId, SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW)
			: SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_TERM_NAME_STORE);
		builder.set(termNameField, name);
	    }

	    Boolean forbidden = term.getForbidden();
	    if (forbidden != null) {
		String forbiddenField = SolrParentDocFields.DYN_FORBIDDEN_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, forbiddenField), forbidden);
	    }

	    Boolean disabled = term.getDisabled();
	    if (disabled != null) {
		String disabledField = SolrParentDocFields.DYN_DISABLED_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, disabledField), disabled);
	    }

	    Boolean inTranslationAsSource = term.getInTranslationAsSource();
	    if (inTranslationAsSource != null) {
		String dynField = SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE;
		builder.set(SolrDocHelper.createDynamicFieldName(languageKey, dynField), inTranslationAsSource);
	    }

	    Set<String> attr = new HashSet<String>();
	    Set<String> notes = new HashSet<String>();
	    Set<DbTermDescription> termDescriptions = term.getDescriptions();
	    if (CollectionUtils.isNotEmpty(termDescriptions)) {
		for (DbTermDescription description : termDescriptions) {
		    if (DbTermDescription.NOTE.equals(description.getBaseType())) {
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

	    String noteField = SolrDocHelper.createDynamicFieldName(languageKey, SolrParentDocFields.DYN_NOTE_MULTI_STORE);
	    if (!notes.isEmpty()) {
		builder.set(noteField, notes);
	    }
	}
    }

    private static Map<String, Set<DbTerm>> groupTermsByLanguageId(Set<DbTerm> terms) {
	Map<String, Set<DbTerm>> grouped = new HashMap<String, Set<DbTerm>>();

	for (DbTerm term : terms) {
	    String languageId = term.getLanguageId();

	    Set<DbTerm> dbTerms = grouped.get(languageId);
	    if (dbTerms == null) {
		dbTerms = new HashSet<DbTerm>();
		grouped.put(languageId, dbTerms);
	    }

	    dbTerms.add(term);
	}

	return grouped;
    }

}

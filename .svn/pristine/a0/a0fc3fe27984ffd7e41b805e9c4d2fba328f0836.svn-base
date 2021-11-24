package org.gs4tr.termmanager.persistence.solr.util;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;

public class TmgrUpdateConstants {

    public static final List<String> FL_FIELDS = new ArrayList<>();
    private static final String STAR = "*_";

    static {
	FL_FIELDS.add(SolrParentDocFields.REVISION);
	// termEntry fields
	FL_FIELDS.add(SolrConstants.ID_FIELD);
	FL_FIELDS.add(SolrParentDocFields.HISTORY_ACTION);
	FL_FIELDS.add(SolrParentDocFields.PROJECT_ID_INDEX_STORE);
	FL_FIELDS.add(SolrParentDocFields.SHORTCODE_INDEX_STORE);
	FL_FIELDS.add(SolrParentDocFields.PROJECT_NAME_INDEX_STORE);
	FL_FIELDS.add(SolrParentDocFields.DATE_CREATED_INDEX_STORE);
	FL_FIELDS.add(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE);
	FL_FIELDS.add(SolrParentDocFields.USER_CREATED_INDEX);
	FL_FIELDS.add(SolrParentDocFields.USER_MODIFIED_SORT);
	FL_FIELDS.add(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE);
	FL_FIELDS.add(SolrParentDocFields.SUBMISSION_NAME_STORE);
	FL_FIELDS.add(SolrParentDocFields.SUBMITTER_SORT);
	FL_FIELDS.add(SolrParentDocFields.PARENT_ID_STORE);
	FL_FIELDS.add(SolrParentDocFields.ATTRIBUTE_MULTI_STORE);

	// term fields
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_TERM_ID_SORT);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_TERM_ID_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_DATE_CREATED_SORT);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_DATE_CREATED_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_DATE_MODIFIED_SORT);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_DATE_MODIFIED_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_NOTE_MULTI_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_FORBIDDEN_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_DISABLED_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_TERM_NAME_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_STATUS_SORT);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_STATUS_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_STATUS_OLD_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_USER_CREATED_SORT);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_USER_CREATED_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_USER_MODIFIED_SORT);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_USER_MODIFIED_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_PARENT_ID_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_ASSIGNEE_SORT);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_CANCELED_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_COMMITED_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_DATE_COMPLETED_SORT);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_DATE_COMPLETED_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_DATE_SUBMITTED_SORT);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_DATE_SUBMITTED_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_REVIEW_REQUIRED_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_TEMP_TERM_NAME_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_COMMENT_MULTI_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_USER_LATEST_CHANGE_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_IN_TRANSLATION_AS_SOURCE_STORE);
	FL_FIELDS.add(STAR + SolrParentDocFields.DYN_DELETED_TERMS);
    }
}

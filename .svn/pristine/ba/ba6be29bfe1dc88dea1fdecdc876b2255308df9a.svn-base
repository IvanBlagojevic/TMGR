import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;


SolrDocument document=new SolrDocument()
document.setField(SolrParentDocFields.REVISION, 8)
document.setField(SolrParentDocFields.HISTORY_ACTION, "ADDED")
document.setField(SolrParentDocFields.DATE_CREATED_INDEX_STORE, new Date().getTime())
document.setField(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, new Date().getTime())
document.setField(SolrParentDocFields.USER_MODIFIED_SORT, "userModified")
document.setField(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE, 1L)
document.setField(SolrParentDocFields.SUBMISSION_NAME_STORE, "submissionName")
document.setField(SolrParentDocFields.SUBMITTER_SORT, "submitter")

document.setField("en-US_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "termName")
document.setField("en-US_" + SolrParentDocFields.DYN_USER_CREATED_SORT, "userCreated")
document.setField("en-US_" + SolrParentDocFields.DYN_USER_MODIFIED_SORT, "userModified")
document.setField("en-US_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT, 9999999L)
document.setField("en-US_" + SolrParentDocFields.DYN_TERM_ID_SORT, UUID.randomUUID().toString())
document.setField("en-US_" + SolrParentDocFields.ATTRIBUTE_MULTI_STORE,"ATTRIBUTE"+SolrGlossaryAdapter.RS+"value"+SolrGlossaryAdapter.RS+UUID.randomUUID().toString()+SolrGlossaryAdapter.RS+UUID.randomUUID().toString())
document.setField("en-US_" + SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW,"temp text")

document.setField("de-DE_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "germanTermName")
document.setField("de-DE_" + SolrParentDocFields.DYN_USER_CREATED_SORT, "germanUserCreated")
document.setField("de-DE_" + SolrParentDocFields.DYN_USER_MODIFIED_SORT, "germanUserModified")
document.setField("de-DE_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT,9999999L)
document.setField("de-DE_" + SolrParentDocFields.DYN_TERM_ID_SORT, UUID.randomUUID().toString())
document.setField("de-DE_" + SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW,"german temp text")

currentDocument=document


SolrDocument emptyRevision=new SolrDocument()
emptyRevision.setField(SolrParentDocFields.REVISION, 1)
emptyRevision.setField(SolrParentDocFields.HISTORY_ACTION, "")
emptyRevision.setField(SolrParentDocFields.DATE_CREATED_INDEX_STORE, "")
emptyRevision.setField(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, "")
emptyRevision.setField(SolrParentDocFields.USER_CREATED_SORT, "")
emptyRevision.setField(SolrParentDocFields.USER_MODIFIED_SORT, "")
emptyRevision.setField(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE, "")
emptyRevision.setField(SolrParentDocFields.SUBMISSION_NAME_STORE, "")
emptyRevision.setField(SolrParentDocFields.SUBMITTER_SORT, "")

emptyRevision.setField("en-US_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "")
emptyRevision.setField("en-US_" + SolrParentDocFields.DYN_USER_CREATED_SORT, "")
emptyRevision.setField("en-US_" + SolrParentDocFields.DYN_USER_MODIFIED_SORT, "")
emptyRevision.setField("en-US_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT, "")
emptyRevision.setField("en-US_" + SolrParentDocFields.DYN_TERM_ID_SORT, "")
emptyRevision.setField("en-US_" + SolrParentDocFields.ATTRIBUTE_MULTI_STORE,"")
emptyRevision.setField("en-US_" + SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW,"")

emptyRevision.setField("de-DE_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "")
emptyRevision.setField("de-DE_" + SolrParentDocFields.DYN_USER_CREATED_SORT, "")
emptyRevision.setField("de-DE_" + SolrParentDocFields.DYN_USER_MODIFIED_SORT, "")
emptyRevision.setField("de-DE_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT,"")
emptyRevision.setField("de-DE_" + SolrParentDocFields.DYN_TERM_ID_SORT, "")
emptyRevision.setField("de-DE_" + SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW,"")


SolrDocument firstRevision = new SolrDocument()
firstRevision.setField("en-US_" + SolrParentDocFields.DYN_TEMP_TERM_NAME_SORT_SUFFIX_NEW, "temp text revision 7")
firstRevision.setField(SolrParentDocFields.REVISION, 7)
firstRevision.setField("en-US_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT, 845888L)


SolrDocumentList revisions=new SolrDocumentList()
revisions.add(firstRevision)
revisions.add(emptyRevision)

oneRevision=revisions


SolrDocument attributeRevision = new SolrDocument()
attributeRevision.setField(SolrParentDocFields.REVISION, 7)
attributeRevision.setField("en-US_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT, 845888L)
attributeRevision.setField("en-US_" + SolrParentDocFields.ATTRIBUTE_MULTI_STORE,"ATTRIBUTE"+SolrGlossaryAdapter.RS+"value2"+SolrGlossaryAdapter.RS+UUID.randomUUID().toString()+SolrGlossaryAdapter.RS+UUID.randomUUID().toString())

SolrDocumentList attRevisions=new SolrDocumentList()
attRevisions.add(attributeRevision)
attRevisions.add(emptyRevision)

attrbiuteRevisions=attRevisions


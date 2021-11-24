import org.apache.solr.common.SolrDocument;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.apache.solr.common.SolrDocumentList;


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

document.setField("de-DE_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "germanTermName")
document.setField("de-DE_" + SolrParentDocFields.DYN_USER_CREATED_SORT, "germanUserCreated")
document.setField("de-DE_" + SolrParentDocFields.DYN_USER_MODIFIED_SORT, "germanUserModified")
document.setField("de-DE_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT,9999999L)
document.setField("de-DE_" + SolrParentDocFields.DYN_TERM_ID_SORT, UUID.randomUUID().toString())

currentDocument=document

revision = new SolrDocument()
revision.setField(SolrParentDocFields.REVISION, 1)
revision.setField(SolrParentDocFields.HISTORY_ACTION, "")
revision.setField(SolrParentDocFields.DATE_CREATED_INDEX_STORE, 5555L)
revision.setField(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, 5555L)
revision.setField(SolrParentDocFields.USER_CREATED_SORT, "")
revision.setField(SolrParentDocFields.USER_MODIFIED_SORT, "")
revision.setField(SolrParentDocFields.SUBMISSION_ID_INDEX_STORE, "")
revision.setField(SolrParentDocFields.SUBMISSION_NAME_STORE, "")
revision.setField(SolrParentDocFields.SUBMITTER_SORT, "")

revision.setField("en-US_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "")
revision.setField("en-US_" + SolrParentDocFields.DYN_USER_CREATED_SORT, "")
revision.setField("en-US_" + SolrParentDocFields.DYN_USER_MODIFIED_SORT, "")
revision.setField("en-US_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT, 5555L)
revision.setField("en-US_" + SolrParentDocFields.DYN_TERM_ID_SORT, "")

revision.setField("de-DE_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "")
revision.setField("de-DE_" + SolrParentDocFields.DYN_USER_CREATED_SORT, "")
revision.setField("de-DE_" + SolrParentDocFields.DYN_USER_MODIFIED_SORT, "")
revision.setField("de-DE_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT,5555L)
revision.setField("de-DE_" + SolrParentDocFields.DYN_TERM_ID_SORT, "")
emptyRevision=revision

SolrDocument fRevision = new SolrDocument()
fRevision.setField("en-US_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "termName changed")
fRevision.setField(SolrParentDocFields.REVISION, 7)
fRevision.setField("en-US_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT, 888888L)

firstRevision=fRevision

SolrDocumentList oneRevision=new SolrDocumentList();
oneRevision.add(firstRevision);
oneRevision.add(emptyRevision);

listOneRevision=oneRevision

SolrDocument sRevision = new SolrDocument()
sRevision.setField("en-US_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "termName revision2")
sRevision.setField(SolrParentDocFields.REVISION, 2)
sRevision.setField("en-US_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT, 888888L)

secondRevision=sRevision

SolrDocument fourRevision = new SolrDocument()
fourRevision.setField("en-US_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "termName revision4")
fourRevision.setField(SolrParentDocFields.REVISION, 4)
fourRevision.setField("en-US_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT, 888888L)

fourthRevision=fRevision

SolrDocument fifRevision = new SolrDocument()
fifRevision.setField("en-US_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "termName revision5")
fifRevision.setField(SolrParentDocFields.REVISION, 5)
fifRevision.setField("en-US_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT, 888888L)

fifthRevision=fifRevision

SolrDocument siRevision = new SolrDocument()
siRevision.setField("de-DE_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW, "german term revision6")
siRevision.setField(SolrParentDocFields.REVISION, 6)
siRevision.setField("de-DE_" + SolrParentDocFields.DYN_DATE_MODIFIED_SORT, 885568L)

sixRevision=siRevision
SolrDocumentList revisions=new SolrDocumentList()
revisions.add(sixRevision)
revisions.add(fifthRevision)
revisions.add(fourthRevision)
revisions.add(secondRevision)
revisions.add(emptyRevision)

deletedRevisions=revisions
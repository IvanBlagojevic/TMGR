package groovy.manualtask
import java.sql.Timestamp

import org.gs4tr.termmanager.model.ProjectLanguageUserDetail
import org.gs4tr.termmanager.model.event.DetailState


date = new Date();
timesatmp = new Timestamp(date.getTime());

itemStatusType = builder.itemStatusType([name: "intranslationreview"])
priority = builder.entityStatusPriority([dateCompleted: null, priority: 2, priorityAssignee: 3,
    status: itemStatusType])

statusWainting = builder.itemStatusType(name: "waiting")
date = new Date();

organizationInfo = builder.organizationInfo([name: "My First Organization", domain: "DOMAIN", enabled: true,
    theme: "THEME", currencyCode: null])

Set<ProjectLanguageUserDetail> userDetails = new HashSet<ProjectLanguageUserDetail>();

projectLanguageDetail1 = builder.projectLanguageDetail([languageId: "en-US", userDetails: userDetails])
projectLanguageDetail2 = builder.projectLanguageDetail([languageId: "de-DE", userDetails: userDetails])
projectLanguageDetail3 = builder.projectLanguageDetail([languageId: "fr-FR", userDetails: userDetails])

languageDetails = [
    projectLanguageDetail1,
    projectLanguageDetail2,
    projectLanguageDetail3] as Set

detailState = new DetailState();

languageDetailState = ["en-US": detailState, "de-DE": detailState, "fr-FR": detailState] as Map;

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 0L, completedSubmissionCount: 0L,
    dateModified: date, forbiddenTermCount: 0, languageCount: 3L, languageDetails: languageDetails,
    project: null, projectDetailId: 0L, termCount: 1L, termEntryCount: 1L, termInSubmissionCount: 0L, userDetails: null])


projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
    metadata: null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
    projectLanguages: null])

/* Export document WS */
searchRequest = builder.termEntrySearchRequest([sourceLocale: "en-US", targetLocales: ["de-DE"] as List])

termUuId01 = "export-term00000001";
termUuId02 = "export-term00000002";
termUuId03 = "export-term00000003";
termUuId04 = "export-term00000004";
termUuId05 = "export-term00000005";

term01 = builder.term([uuId: termUuId01, name: "term test 1 source", languageId: "en-US", dateModified: 1403509560L, status: "PROCESSED"])
term02 = builder.term([uuId: termUuId02, name: "term test 2 target", languageId: "de-DE", dateModified: 1403509561L, status: "PROCESSED"])
term03 = builder.term([uuId: termUuId03, name: "term test 3 source", languageId: "en-US", dateModified: 1403509562L, status: "PROCESSED"])
term04 = builder.term([uuId: termUuId04, name: "term test 4 target", languageId: "de-DE", dateModified: 1403509563L, status: "PROCESSED"])
term05 = builder.term([uuId: termUuId05, name: "term test 5 target", languageId: "de-DE", dateModified: 1403509564L, status: "BLACKLISTED",
    forbidden: true])

termEntryMap1 = ["en-US" : [term01] as Set, "de-DE" : [term02] as Set] as Map
termEntryMap2 = ["en-US" : [term03] as Set, "de-DE" : [term04, term05] as Set] as Map

termEntryUuId01 = "export-termentry001";
termEntryUuId02 = "export-termentry002";

termEntry01 = builder.termEntry([uuId: termEntryUuId01, languageTerms: termEntryMap1])
termEntry02 = builder.termEntry([uuId: termEntryUuId02, languageTerms: termEntryMap2])

termentries = [termEntry01, termEntry02] as List

termNameAndSearchType = builder.termNameAndSearchType([value1: "term", value2:"DEFAULT"])
entityType = builder.entityType([value1: ["TERM", "ATTRIBUTES"] as List, value2:["SOURCE", "TARGET"] as List])
exportSearchFilter = builder.exportSearchFilter([termNameAndSearchType: termNameAndSearchType, entityType: entityType, ascending: true, sortProperty:"someField"])


exportCommand = builder.exportCommand([projectId: 1L, exportFormat: "csv", sourceLocale: "en-US", targetLocales: ["de-DE"] as List, searchFilter: exportSearchFilter])



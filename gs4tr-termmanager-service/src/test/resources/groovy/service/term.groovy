package groovy.service

import org.gs4tr.termmanager.model.Attribute
import org.gs4tr.termmanager.model.ProjectLanguageUserDetail
import org.gs4tr.termmanager.model.event.DetailState
import org.gs4tr.termmanager.model.glossary.Description
import org.gs4tr.termmanager.model.glossary.Term
import org.gs4tr.termmanager.model.glossary.TermEntry

import java.sql.Timestamp

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

uCommand1 = builder.updateCommand([value: "some value1", itemType: "term", languageId: "en-US", command: "add"]);
uCommand2 = builder.updateCommand([value: "some value2", itemType: "term", languageId: "de-DE", command: "add"]);

sourceCommandList = [uCommand1] as List
targetCommandList = [uCommand2] as List

updateCommand = builder.updateCommand([value: "some updated value", itemType: "term", languageId: "en-US", command: "update"]);
updateCommands = [updateCommand] as List


tu = builder.translationUnit([sourceTermUpdateCommands: sourceCommandList, targetTermUpdateCommands: targetCommandList])

tuList =[tu] as List

termId_01 = "474e93ae-7264-4088-9d54-term00000001";
termId_02 = "474e93ae-7264-4088-9d54-term00000002";
termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";

term1 = builder.term([uuId: termId_01, termEntryId: termEntryId_01, forbidden: false, status: "WAITING", statusOld: "PROCESSED", languageId: "en-US", projectId: 1L])
term2 = builder.term([uuId: termId_02, termEntryId: termEntryId_01, forbidden: false, status: "WAITING", statusOld: "WAITING", languageId: "de-DE", projectId: 1L])

termEntry1 = builder.termEntry([uuId: termEntryId_01])

termEntry1.addTerm(term1);
termEntry1.addTerm(term2);

terms = [term1, term2] as List

termIds = [
    termId_01,
    termId_02] as List

emptyTermIds = [] as List<String>

emptyTerms = [] as List<Term>

termEntryList = [termEntry1] as List<TermEntry>

type = ["contex", "definition"] as List<String>

languageIds = ["en-US", "de-DE", "fr-FR"] as List<String>


termId_One = "474e93ae-3003-4088-9d54-termOne";
termId_Two = "474e93ae-1908-4088-9d54-termTwo";
termEntryId_One = "474e93ae-7264-4088-9d54-termEntryOne";

descriptionOne = builder.description([baseType: "ATTRIBUTE", type: "contex"])
descriptionTwo = builder.description([baseType: "NOTE", type: "definition"])

descriptionsOne = [descriptionOne] as Set<Description>
descriptionsTwo = [
    descriptionOne,
    descriptionTwo] as Set<Description>

termEntryOne = builder.termEntry([uuId: termEntryId_One])

termOne = builder.term([uuId: termId_One, termEntryId: termEntryId_One, name: "dog", forbidden: false, status: "PROCESSED", statusOld: "WAITING", languageId: "en-US", descriptions: descriptionsOne])
termTwo = builder.term([uuId: termId_Two, termEntryId: termEntryId_One, name: "cat", forbidden: true, status: "BLACKLISTED", statusOld: "WAITING", languageId: "en-US", descriptions: descriptionsTwo])

termEntryOne.addTerm(termOne);
termEntryOne.addTerm(termTwo);

termId_Three = "474e93ae-3003-4088-9d54-termThree";
termId_Four = "474e93ae-7264-4088-9d54-termFour";
termEntryId_Two = "474e93ae-4321-4088-9d54-termEntryTwo";

descriptionThree = builder.description([baseType: "ATTRIBUTE", type: "contex"])
descriptionFour = builder.description([baseType: "NOTE", type: "custom"])

descriptionsThree = [
    descriptionThree,
    descriptionFour
]
descriptionsFour = [descriptionFour]

termEntryTwo = builder.termEntry([uuId: termEntryId_Two])

termThree = builder.term([uuId: termId_Three, termEntryId: termEntryId_Two, name: "lion", forbidden: false, status: "PROCESSED", statusOld: "WAITING", languageId: "en-US", descriptions: descriptionsThree])
termFour = builder.term([uuId: termId_Four, termEntryId: termEntryId_Two, forbidden: true, status: "BLACKLISTED", statusOld: "WAITING", languageId: "de-DE", descriptions: descriptionsFour])

termEntryTwo.addTerm(termThree);
termEntryTwo.addTerm(termFour);

termEntries = [termEntryOne, termEntryTwo] as List<TermEntry>

attributeOne = builder.attribute([name: "custom", renameValue: "Part of speech"])
attributes = [attributeOne] as List<Attribute>

termSetOne = [termOne, termTwo] as Set<Term>
termSetTwo = [termOne, termTwo, termThree] as Set<Term>


// TERII-3536: Term history - Wrong modification user if actions are performed on same term but in different windows with different users
termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";
termId_01 = "474e93ae-7264-4088-9d54-term00000001";
termId_02 = "474e93ae-7264-4088-9d54-term00000002";
termId_03 = "474e93ae-7264-4088-9d54-term00000002";

term1 = builder.term([uuId: termId_01, name: "source test term", projectId: 1L, termEntryId: termEntryId_01,
    forbidden: false, status: "PROCESSED", languageId: "en-US", inTranslationAsSource: false, userCreated: "sdulin",
    userModified: "sdulin"])
term2 = builder.term([uuId: termId_02, name: "Germany target term", projectId: 1L, termEntryId: termEntryId_01,
    forbidden: false, status: "PROCESSED", languageId: "de-DE", inTranslationAsSource: false,  userCreated: "sdulin",
    userModified: "sdulin"])
term3 = builder.term([uuId: termId_03, name: "French target term", projectId: 1L, termEntryId: termEntryId_01,
    forbidden: false, status: "WAITING", languageId: "fr-FR", inTranslationAsSource: false,  userCreated: "sdulin",
    userModified: "sdulin"])

termSet1 = [term1] as Set
termSet2 = [term2] as Set
languageTerms1 = ["en-US": termSet1, "de-DE": termSet2] as Map
termSet3 = [term1] as Set
termSet4 = [term3] as Set
languageTerms2 = ["en-US": termSet3, "de-DE": termSet4] as Map


termEntry_01 = builder.termEntry([dateCreated: 1403509560L, dateModified:1303509560L, userCreated:"sdulin", userModified: "sdulin",
    uuId: termEntryId_01, languageTerms: languageTerms1, descriptions: null, projectId: 1L])

termEntry_02 = builder.termEntry([dateCreated: 1403509569L, dateModified:1303509569L, userCreated:"sdulin", userModified: "sdulin",
    uuId: termEntryId_01, languageTerms: languageTerms2, descriptions: null, projectId: 1L])

termIds_01 = [termId_02] as List
termIds_02 = [termId_03] as List

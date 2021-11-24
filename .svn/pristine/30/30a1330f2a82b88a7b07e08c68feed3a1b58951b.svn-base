package groovy.manualtask
import java.sql.Timestamp
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectLanguageUserDetail;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.event.DetailState;
import org.gs4tr.termmanager.service.model.command.ForbidTermCommand;
import org.gs4tr.termmanager.model.glossary.Term;


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

termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";
termEntryId_02 = "474e93ae-7264-4088-9d54-termentry002";
termEntryId_03 = "474e93ae-7264-4088-9d54-termentry003";
termEntryId_04 = "474e93ae-7264-4088-9d54-termentry004";

termId_01 = "474e93ae-7264-4088-9d54-term00000001";
termId_02 = "474e93ae-7264-4088-9d54-term00000002";
termId_03 = "474e93ae-7264-4088-9d54-term00000003";
termId_04 = "474e93ae-7264-4088-9d54-term00000004";
termId_05 = "474e93ae-7264-4088-9d54-term00000005";
termId_06 = "474e93ae-7264-4088-9d54-term00000006";

sourceTermId = "1eef61ed-9de3-431f-99e0-05039110c1ba";
germanyTermId = "2a3d2ba5-533b-4595-bc7e-8d714250797a";
frenchTermId = "71767670-4686-42f5-a95a-104493a12b6f";

term1 = builder.term([uuId: termId_01, name: "test term 1", projectId: 1L, termEntryId: termEntryId_01,
    forbidden: false, status: "WAITING", languageId: "en-US", inTranslationAsSource: false])
term2 = builder.term([uuId: termId_02, name: "test term 2", projectId: 1L, termEntryId: termEntryId_01,
    forbidden: true, status: "BLACKLISTED", languageId: "de-DE", inTranslationAsSource: false])
term3 = builder.term([uuId: termId_03, name: "test term 3", projectId: 1L, termEntryId: termEntryId_02,
    forbidden: false, status: "PROCESSED", languageId: "en-US", inTranslationAsSource: false])
term4 = builder.term([uuId: termId_04, name: "test term 4", projectId: 2L, termEntryId: termEntryId_03,
    forbidden: false, status: "PROCESSED", languageId: "en-US", inTranslationAsSource: false])
term5 = builder.term([uuId: termId_05, name: "test term 5", projectId: 2L, termEntryId: termEntryId_03,
    forbidden: false, status: "PROCESSED", languageId: "en-US", inTranslationAsSource: false])
term6 = builder.term([uuId: termId_06, name: "test term 6", projectId: 2L, termEntryId: termEntryId_03,
    forbidden: false, status: "PROCESSED", languageId: "de-DE", inTranslationAsSource: false])

sourceTerm = builder.term([uuId: sourceTermId, name: "birthday reminder", projectId: 1L, termEntryId: termEntryId_04,
    forbidden: false, status: "PROCESSED", languageId: "en-US", inTranslationAsSource: true])
germanyTargetTerm = builder.term([uuId: germanyTermId, name: "Erinnerungsmeldung für Geburtstage", projectId: 1L, termEntryId: termEntryId_04,
    forbidden: false, status: "INTRANSLATIONREVIEW", languageId: "de-DE", inTranslationAsSource: false])
frenchTargetTerm = builder.term([uuId: frenchTermId, name: "rappel d'anniversaire", projectId: 1L, termEntryId: termEntryId_04,
    forbidden: false, status: "PROCESSED", languageId: "fr-FR", inTranslationAsSource: false])

termList1 = [term1] as Set
termList2 = [term2] as Set
languageTerms1 = ["en-US": termList1, "de-DE": termList2] as Map

termList3 = [term3] as Set
languageTerms2 = ["en-US": termList3] as Map

termList4 = [term4, term5] as Set
languageTerms3 = ["en-US": termList4, "de-DE": term6] as Map

termEntry1 = builder.termEntry([uuId: termEntryId_01, languageTerms: languageTerms1])
termEntry2 = builder.termEntry([uuId: termEntryId_02, languageTerms: languageTerms2])
termEntry3 = builder.termEntry([uuId: termEntryId_03, languageTerms: languageTerms3])

terms = [
    term1,
    term2,
    term3,
    term4,
    term5,
    term6] as List

testTerms = [
    sourceTerm,
    germanyTargetTerm,
    frenchTargetTerm
] as List

testTermIds = [
    sourceTermId,
    germanyTermId,
    frenchTermId
] as List

postTestTerms = [
    germanyTargetTerm,
    frenchTargetTerm] as List

postTestTermIds = [
    germanyTermId,
    frenchTermId] as List

sourceIds = [sourceTermId] as List

termIds = [
    termId_01,
    termId_02,
    termId_03,
    termId_04,
    termId_05,
    termId_06] as List

forbidCommand = builder.forbidTermCommand([termIds: termIds, sourceLanguage: "en-US", approveTerm: true])
getForbidCommand = builder.forbidTermCommand([termIds: testTermIds, sourceLanguage: null, approveTerm: false, sourceIds: sourceIds, wholeTermEntry: false])
postForbidCommand = builder.forbidTermCommand([termIds: postTestTermIds, sourceLanguage: null, approveTerm: false, sourceIds: null, wholeTermEntry: false])






package groovy.service
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

enDetailState = new DetailState();
enDetailState.setTermCount(1l);
enDetailState.setApprovedTermCount(1l);

deDetailState = new DetailState();
deDetailState.setTermCount(1l);
deDetailState.setApprovedTermCount(1l);
deDetailState.setCompletedSubmissionCount(1l);

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 2L, completedSubmissionCount: 1L,
    dateModified: date, forbiddenTermCount: 0L, languageCount: 3L, languageDetails: languageDetails,
    project: null, projectDetailId: 1L, termCount: 2L, termEntryCount: 1L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
    metadata: null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
    projectLanguages: null])

termId_01 = "474e93ae-7264-4088-9d54-term00000001";
termId_02 = "474e93ae-7264-4088-9d54-term00000002";
termId_03 = "474e93ae-7264-4088-9d54-term00000003";

termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";

term1 = builder.term([uuId: termId_01, termEntryId: termEntryId_01, forbidden: false, status: "PROCESSED", statusOld: "PROCESSED",
    languageId: "en-US", name: "regular term text 1", inTranslationAsSource: true, commited: false])
term2 = builder.term([uuId: termId_02, termEntryId: termEntryId_01, forbidden: false, status: "PROCESSED", statusOld: "WAITING",
    languageId: "de-DE", name: "regular term text 2", inTranslationAsSource: false, commited: false])
term3 = builder.term([uuId: termId_03, termEntryId: termEntryId_01, forbidden: false, commited: true, name: "regular term text 3",
    tempText: "some text in german language", status: "INTRANSLATIONREVIEW", statusOld: "PROCESSED", languageId: "de-DE"])

termEntryEntity = builder.termEntry([uuId: termEntryId_01])

terms = [term1, term2] as List

termIds = [
    termId_01,
    termId_02] as List

sourceTerms = [term1] as List

submissionTermId_01 = "474e93ae-7264-4088-9d54-sub-term0001";
submissionTermId_02 = "474e93ae-7264-4088-9d54-sub-term0002";
submissionTermEntryId_01 = "474e93ae-7264-4088-9d54-termentryS01";

submissionTermEntry1 = builder.termEntry([uuId: submissionTermEntryId_01, parentUuId: termEntryId_01])

submissionTerm1 = builder.term([uuId: submissionTermId_01, termEntryId: submissionTermEntryId_01, forbidden: false,
    status: "INTRANSLATIONREVIEW", statusOld: "PROCESSED", languageId: "en-US", canceled: false, tempText: "temp text 1"])

submissionTerm2 = builder.term([uuId: submissionTermId_02, termEntryId: submissionTermEntryId_01, forbidden: false,
    status: "INTRANSLATIONREVIEW", statusOld: "PROCESSED", languageId: "de-DE", canceled: false, tempText: "temp text 2"])

submissionTermEntry1.addTerm(submissionTerm1);
submissionTermEntry1.addTerm(submissionTerm2);

submissionTermIds = [
    submissionTermId_02,
    submissionTermId_01] as List

submissionTerms = [
    submissionTerm1,
    submissionTerm2] as List

submissionLanguage1 = builder.submissionLanguage([assignee: "marko"])
submissionLanguage2 = builder.submissionLanguage([assignee: "marko"])

submissionLanguages = [
    submissionLanguage1,
    submissionLanguage2] as Set

termsForComment = [
    submissionTerm1,
    submissionTerm2] as List

/* Create canceled submission term entry */
canceledSubmissionTermId_01 = "474e93ae-7264-4088-9d54-sub-canceledSubmissionTerm0001";
canceledSubmissionTermId_02 = "474e93ae-7264-4088-9d54-sub-canceledSubmissionTerm0002";
canceledSubmissionTermEntryId_01 = "474e93ae-7264-4088-9d54-canceledSubmissionTermEntryS01";

canceledSubmissionTerm1 = builder.term([uuId: canceledSubmissionTermId_01, termEntryId: canceledSubmissionTermEntryId_01, forbidden: false,
    status: "PROCESSED", statusOld: "PROCESSED", languageId: "en-US", name:"canceled submission source term" ,canceled: true, tempText: "canceled submission source term",
    commited: true, dateCompleted: date.getTime(), dateModified: date.getTime()])

canceledSubmissionTerm2 = builder.term([uuId: canceledSubmissionTermId_02, termEntryId: canceledSubmissionTermEntryId_01, forbidden: false,
    status: "PROCESSED", statusOld: "PROCESSED", languageId: "de-DE", name:"canceled submission target term" ,canceled: true, tempText: "canceled submission target term",
    commited: true, dateCompleted: date.getTime(), dateModified: date.getTime()])

enTermSet = [canceledSubmissionTerm1] as Set
deTermSet = [canceledSubmissionTerm2] as Set
languageTerms = ["en-US": enTermSet, "de-DE": deTermSet] as Map

canceledSubmissionTermEntry = builder.termEntry([uuId: canceledSubmissionTermEntryId_01, languageTerms: languageTerms])

termEntries = [canceledSubmissionTermEntry] as List

/* Commit submission action */
submissionTermEntryCommitId_01 = "474e93ae-submission-entry-term-sc01";
submissionTermCommitId_01 = "474e93ae-7264-4088-9d54-sub-term-c01";
submissionTermCommitId_02 = "474e93ae-7264-4088-9d54-sub-term-c02";

submissionCommitTermEntry1 = builder.termEntry([uuId: submissionTermEntryCommitId_01, parentUuId: termEntryId_01])

submissionTermCommit1 = builder.term([uuId: submissionTermCommitId_01, termEntryId: submissionTermEntryCommitId_01, forbidden: false,
    status: "INTRANSLATIONREVIEW", statusOld: "PROCESSED", languageId: "en-US", canceled: false, tempText: "regular term text 1",
    parentUuId: termId_01, reviewRequired: false, inTranslationAsSource: true, name: "regular term text 1",
    commited: false])

submissionTermCommit2 = builder.term([uuId: submissionTermCommitId_02, termEntryId: submissionTermEntryCommitId_01, forbidden: false,
    status: "INTRANSLATIONREVIEW", statusOld: "PROCESSED", languageId: "de-DE", canceled: false, tempText: "some changed target text",
    parentUuId: termId_02, reviewRequired: false, inTranslationAsSource: false, name: "regular term text 2",
    commited: false])

submissionTermCommitIds = [
    submissionTermCommitId_01,
    submissionTermCommitId_02] as List

submissionCommitTerms = [
    submissionTermCommit1,
    submissionTermCommit2] as List

submissionLanguageCommit1 = builder.submissionLanguage([assignee: "marko"])
submissionLanguageCommit2 = builder.submissionLanguage([assignee: "marko"])

submissionLanguagesCommit = [
    submissionLanguageCommit1,
    submissionLanguageCommit2] as Set

/* Re-submit action (term count test) */
resubmitTermEntryId_01 = "474e93ae-resubmit-entry-term-rs01";
englishSourceTermId = "474e93ae-7264-4088-9d54-englishSource-term-st02";
inFinalReviewTargetTermId = "474e93ae-7264-4088-9d54-resubmit-term-rs01";
completedSynTermId = "474e93ae-7264-4088-9d54-completed-term-ct02";

resubmitTermIds = [inFinalReviewTargetTermId] as List

englishSourceTerm = builder.term([uuId: englishSourceTermId, termEntryId: resubmitTermEntryId_01, forbidden: false,
    status: "PROCESSED", inTranslationAsSource: true, languageId: "en-US", canceled: false, tempText: "English source term value", parentUuId: englishSourceTermId])

inFinalReviewTargetTerm = builder.term([uuId: inFinalReviewTargetTermId, termEntryId: resubmitTermEntryId_01, forbidden: false,
    status: "INFINALREVIEW", statusOld: "PROCESSED", languageId: "de-DE", canceled: false, tempText: "Germany target term value", parentUuId: inFinalReviewTargetTermId])

completedSynTerm = builder.term([uuId: completedSynTermId, termEntryId: resubmitTermEntryId_01, forbidden: false,
    status: "PROCESSED", languageId: "de-DE", canceled: false, tempText: "Germany synonym term value", parentUuId: completedSynTermId])

termList1 = [englishSourceTerm] as Set
termList2 = [
    inFinalReviewTargetTerm,
    completedSynTerm] as Set
languageTerms1 = ["en-US": termList1, "de-DE": termList2] as Map

resubmitTermEntry = builder.termEntry([uuId: resubmitTermEntryId_01, languageTerms: languageTerms1, parentUuId: termEntryId_01])

submissionState = builder.submissionState([canceledCount: 0L, completedCount: 1L, inFinalReviewCount: 1L, inTranslationCount: 0L])

submissionLanguageState = ["de-DE" : submissionState] as Map



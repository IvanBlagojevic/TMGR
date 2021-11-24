import org.gs4tr.termmanager.model.ProjectLanguageUserDetail
import org.gs4tr.termmanager.model.ProjectUserDetail
import org.gs4tr.termmanager.model.event.DetailState

import java.sql.Timestamp

date = new Date();
timesatmp = new Timestamp(date.getTime());

itemStatusType = builder.itemStatusType([name: "intranslationreview"])
priority = builder.entityStatusPriority([dateCompleted: null, priority: 2, priorityAssignee: 3,
                                         status       : itemStatusType])

statusWainting = builder.itemStatusType(name: "waiting")
date = new Date();

organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
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
                                       dateModified         : date, forbiddenTermCount: 0, languageCount: 3L, languageDetails: languageDetails, project: null, projectDetailId: 0L, termCount: 1L, termEntryCount: 1L, termInSubmissionCount: 0L, userDetails: new HashSet<ProjectUserDetail>()])


projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "My First Project", shortCode: "MYF000001"])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

tmProject = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusWainting,
                               metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                               projectLanguages: null])

termId_01 = "474e93ae-7264-4088-9d54-term00000001";
termId_02 = "474e93ae-7264-4088-9d54-term00000002";
termId_03 = "474e93ae-7264-4088-9d54-term00000003";

termEntryId_01 = "474e93ae-7264-4088-9d54-termentry001";

termEntry1 = builder.termEntry([uuId: termEntryId_01])

term1 = builder.term([uuId    : termId_01, termEntryId: termEntryId_01, forbidden: false, commited: true, name: "voicemail (voicemail message)",
                      tempText: "some text in english language", status: "PROCESSED", inTranslationAsSource: true, statusOld: "PROCESSED", languageId: "en-US"])
term2 = builder.term([uuId    : termId_02, termEntryId: termEntryId_01, forbidden: false, commited: true, name: "message vocal",
                      tempText: "some text in french language", status: "INTRANSLATIONREVIEW", statusOld: "PROCESSED", languageId: "de-DE"])
term3 = builder.term([uuId    : termId_03, termEntryId: termEntryId_01, forbidden: false, commited: true, name: "message vocal synonym",
                      tempText: "some text in german language", status: "INTRANSLATIONREVIEW", statusOld: "PROCESSED", languageId: "de-DE"])

termUpdated1 = builder.term([uuId  : termId_01, termEntryId: termEntryId_01, forbidden: false, commited: false, tempText: "some changed text",
                             status: "INTRANSLATIONREVIEW", statusOld: "PROCESSED", languageId: "en-US"])

termEntry1.addTerm(term1);
termEntry1.addTerm(term2);

submissionTermId_01 = "474e93ae-7264-4088-9d54-sub-term0001";
submissionTermId_02 = "474e93ae-7264-4088-9d54-sub-term0002";
submissionTermId_03 = "474e93ae-7264-4088-9d54-sub-term0003";

submissionTermEntryId_01 = "474e93ae-7264-4088-9d54-termentryS01";

submissionTermEntry1 = builder.termEntry([uuId: submissionTermEntryId_01, parentUuId: termEntryId_01])

submissionTerm1 = builder.term([uuId  : submissionTermId_01, termEntryId: submissionTermEntryId_01, forbidden: false, name: "voicemail (voicemail message)",
                                status: "PROCESSED", statusOld: "PROCESSED", inTranslationAsSource: true, languageId: "en-US", canceled: false, parentUuId: termId_01])

submissionTerm2 = builder.term([uuId  : submissionTermId_02, termEntryId: submissionTermEntryId_01, forbidden: false, name: "message vocal",
                                status: "INFINALREVIEW", statusOld: "PROCESSED", languageId: "de-DE", canceled: false, parentUuId: termId_02])

submissionTerm3 = builder.term([uuId  : submissionTermId_03, termEntryId: submissionTermEntryId_01, forbidden: false, name: "message", tempText: "Add new description test",
                                status: "INTRANSLATIONREVIEW", statusOld: "PROCESSED", languageId: "de-DE", canceled: false, parentUuId: termId_03])

regularTermEntryIds = [termEntryId_01] as Set<String>
regularTermEntries = [termEntry1] as List

submissionTermEntry1.addTerm(submissionTerm1);
submissionTermEntry1.addTerm(submissionTerm2);
submissionTermEntry1.addTerm(submissionTerm3);

submissionTermIds = [
        submissionTermId_01,
        submissionTermId_02,
        submissionTermId_03] as List

submissionTerms = [
        submissionTerm1,
        submissionTerm2,
        submissionTerm3] as List

subTermEntries = [submissionTermEntry1] as List

sourceTermId = "474e93ae-7264-4088-9d54-sourceTermId00000001";
targetTermId_01 = "474e93ae-7264-4088-9d54-targetTermId_0100000002";
targetTermId_02 = "474e93ae-7264-4088-9d54-targetTermId_0200000002";

regularTermEntryId = "474e93ae-7264-4088-9d54-termentry001";

regularTermEntry = builder.termEntry([uuId: regularTermEntryId])

sourceTerm = builder.term([uuId  : sourceTermId, termEntryId: regularTermEntryId, forbidden: false, commited: true, name: "birthday reminder",
                           status: "PROCESSED", inTranslationAsSource: true, statusOld: "PROCESSED", languageId: "en-US", projectId: 1L])
targetTerm_01 = builder.term([uuId  : targetTermId_01, termEntryId: regularTermEntryId, forbidden: false, commited: true, name: "Erinnerungsmeldung für Geburtstage",
                              status: "INFINALREVIEW", statusOld: "PROCESSED", languageId: "de-DE", projectId: 1L])
targetTerm_02 = builder.term([uuId    : targetTermId_02, termEntryId: regularTermEntryId, forbidden: false, commited: true, name: "rappel d'anniversaire",
                              tempText: "rappel d'anniversaire", status: "PROCESSED", statusOld: "PROCESSED", languageId: "fr-FR", projectId: 1L])

regularTermEntry.addTerm(sourceTerm);
regularTermEntry.addTerm(targetTerm_01);
regularTermEntry.addTerm(targetTerm_02);

sourceSubmissionTermId = "474e93ae-7264-4088-9d54-sourceSubmissionTermId-0001";
submissionTargetTermId_01 = "474e93ae-7264-4088-9d54-sub-submissionTargetTermId0002";

submissionTermEntryId = "474e93ae-7264-4088-9d54-submissionTermEntryIdS01";

submissionTermEntry = builder.termEntry([uuId: submissionTermEntryId, parentUuId: regularTermEntryId])

submissionSourceTerm = builder.term([uuId  : sourceSubmissionTermId, termEntryId: submissionTermEntryId, forbidden: false, commited: true, name: "birthday reminder",
                                     status: "PROCESSED", inTranslationAsSource: true, statusOld: "PROCESSED", languageId: "en-US", parentUuId: sourceTermId])
submissionTargetTerm_01 = builder.term([uuId    : submissionTargetTermId_01, termEntryId: submissionTermEntryId, forbidden: false, name: "Erinnerungsmeldung für Geburtstage",
                                        tempText: "Erinnerungsmeldung für Geburtstage", status: "INFINALREVIEW", statusOld: "PROCESSED", languageId: "de-DE", canceled: false, parentUuId: targetTermId_01])

submissionTermEntry.addTerm(submissionSourceTerm);
submissionTermEntry.addTerm(submissionTargetTerm_01);

submissionTargetTermIds = [submissionTargetTermId_01] as List


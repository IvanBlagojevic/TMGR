import org.gs4tr.foundation3.core.utils.IdEncrypter
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.LanguageAlignmentEnum
import org.gs4tr.termmanager.model.MultilingualTerm;

statusWainting = builder.itemStatusType(name: "waiting")
inTranslation = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName();
alingment = LanguageAlignmentEnum.LTR
date = new Date().getTime();

/* First multilingual term */
firstTerm1 = builder.term([dateCreated: date, dateModified: date, forbidden: false, first: true, languageId: "en-US",
    name: "cat", projectId: 1L, status: statusWainting, statusOld: statusWainting, canceled: false,
    descriptions: null, uuId: "some-random-uu-id-001", userModified: "pera", userCreated: "pera", userLatestChange: null,
    assignee: "assignee marko"])
firstTerm2 = builder.term([dateCreated: date, dateModified: date, forbidden: false, first: true, languageId: "de-DE",
    name: "catz", projectId: 1L, status: inTranslation, statusOld: statusWainting, canceled: false,
    descriptions: null, uuId: "some-random-uu-id-002", userModified: "pera", userCreated: "pera", userLatestChange: null,
    assignee: "assignee marko", submitter: "submitter marko"])
firstTerm3 = builder.term([dateCreated: date, dateModified: date, forbidden: false, first: false, languageId: "de-DE",
    name: "catz synonym", projectId: 1L, status: inTranslation, statusOld: statusWainting, canceled: false,
    descriptions: null, uuId: "some-random-uu-id-003", userModified: "pera", userCreated: "pera", userLatestChange: null,
    assignee: "assignee marko", submitter: "submitter marko"])

firstSourceTermHolder = builder.termHolder([alignment: alingment, source: true, languageId: "en-US", submissionId: 1L,
    submissionName: "sub name 1", submitter: "marko", terms: [firstTerm1] as Set, termTooltip: null])
firstTargetTermHolder = builder.termHolder([alignment: alingment, source: true, languageId: "de-DE", submissionId: 1L,
    submissionName: "sub name 1", submitter: "marko", terms: [firstTerm2, firstTerm3] as Set, termTooltip: null])

firstTermHolders = [firstSourceTermHolder, firstTargetTermHolder] as List

multilingualTerm1 = builder.multilingualTerm([projectName: "My first project", projectId: 1L, terms: firstTermHolders,
    sourceLanguage: "en-US" ])
package groovy.manualtask
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.foundation.locale.Locale;

descriptionId1 = UUID.randomUUID().toString();
descriptionId2 = UUID.randomUUID().toString();
descriptionId3 = UUID.randomUUID().toString();
descriptionId4 = UUID.randomUUID().toString();

description1 = builder.description([
    uuid: descriptionId1,
    baseType: "NOTE",
    type: "Custom1",
    value: "note1"
])
description2 = builder.description([
    uuid: descriptionId2,
    baseType: Description.NOTE,
    type: "abbreviatedFormFor",
    value: "note3"
])
description3 = builder.description([
    uuid: descriptionId3,
    baseType: Description.NOTE,
    type: "Custom1",
    value: "note2"
])
description4 = builder.description([
    uuid: descriptionId4,
    baseType: "NOTE",
    type: "abbreviatedFormFor",
    value: "note4"
])

description5 = builder.description([
    uuid: UUID.randomUUID().toString(),
    baseType: Description.NOTE,
    type: "category",
    value: "food related"
])

description6 = builder.description([
    uuid: UUID.randomUUID().toString(),
    baseType: Description.NOTE,
    type: "comment",
    value: "food safety"
])

descriptions1 = [description1, description2] as Set
descriptions2 = [description3, description4] as Set
descriptions3 = [description5, description6] as Set

termEntryId1 = UUID.randomUUID().toString();
termEntryId2 = UUID.randomUUID().toString();

termId1 = UUID.randomUUID().toString();
termId2 = UUID.randomUUID().toString();
termId3 = UUID.randomUUID().toString();

term1 = builder.term([uuId: termId1, name: "source test term ", projectId: 1L, termEntryId: termEntryId1,
    forbidden: false, status: "PROCESSED", languageId: Locale.FRANCE.getCode(), inTranslationAsSource: false, descriptions: descriptions1])
term2 = builder.term([uuId: termId2, name: "target test term", projectId: 1L, termEntryId: termEntryId1,
    forbidden: false, status: "PROCESSED", languageId: Locale.GERMANY.getCode(), inTranslationAsSource: false, descriptions: descriptions2])

term3 = builder.term([uuId: termId3, name: "barbeque sauce", languageId: Locale.US.getCode(), dateModified: 1403509560L, status: "PROCESSED",
    forbidden: false, descriptions: descriptions3])

terms1 = [term1] as Set
terms2 = [term2] as Set
terms3 = [term3] as Set

languageTerms1 = ["fr-FR": terms1, "de-DE": terms2] as Map
languageTerms2 = ["fr-FR": terms1, "de-DE": terms2, "en-US": terms3] as Map

termEntry1 = builder.termEntry([uuId: termEntryId1, languageTerms: languageTerms1, projectId: 1L])
termEntry2 = builder.termEntry([uuId: termEntryId2, languageTerms: languageTerms2, projectId: 1L])

previewCommand1 = builder.termEntryPreviewCommand([termEntryId: termEntryId1, gridLanguages: [] as List, showAllLanguages: true])

previewCommand2 = builder.termEntryPreviewCommand([termEntryId: termEntryId2, gridLanguages: [Locale.GERMANY.getCode()] as List,
    showAllLanguages: true])
previewCommand3 = builder.termEntryPreviewCommand([termEntryId: termEntryId2, gridLanguages: [Locale.FRANCE.getCode()] as List,
    showAllLanguages: false])

previewCommand4 = builder.termEntryPreviewCommand([termEntryId: termEntryId2, gridLanguages: [
	Locale.FRANCE.getCode(),
	Locale.GERMANY.getCode()
    ]as List,
    showAllLanguages: true])

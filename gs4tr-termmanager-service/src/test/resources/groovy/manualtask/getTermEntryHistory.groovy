package groovy.manualtask

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gs4tr.foundation.locale.Locale;

import org.gs4tr.termmanager.model.glossary.Description;

termEntryHistoryCommand1 = builder.termEntryHistoryCommand([termEntryId: UUID.randomUUID().toString(), gridLanguages: [
	Locale.FRANCE.getCode(),
	Locale.GERMANY.getCode()
    ]as List, showAllLanguages: false])

termEntryHistoryCommand2 = builder.termEntryHistoryCommand([termEntryId: UUID.randomUUID().toString(), gridLanguages: [
	Locale.FRANCE.getCode(),
	Locale.GERMANY.getCode()
    ]as List, showAllLanguages: true])

termEntryHistoryCommand3 = builder.termEntryHistoryCommand([termEntryId: UUID.randomUUID().toString(), gridLanguages: [Locale.US.getCode(),]as List, showAllLanguages: true])

englishTermUuid = UUID.randomUUID().toString();
germanyTermUuid = UUID.randomUUID().toString();
frenchTermUuid = UUID.randomUUID().toString();

term1 = builder.term([uuId: englishTermUuid, name: "barbeque sauce", languageId: Locale.US.getCode(), dateModified: 1403509560L, userModified: "beko",
    status: "PROCESSED", forbidden: false])
term2 = builder.term([uuId: frenchTermUuid, name: "sauce barbecue", languageId: Locale.FRENCH.getCode(), dateModified: 1403509561L, userModified: "beko",
    status: "PROCESSED", forbidden: false])
term3 = builder.term([uuId: germanyTermUuid, name: "Barbecue Soße", languageId: Locale.GERMANY.getCode(), dateModified: 1403509561L, userModified: "beko",
    status: "PROCESSED", forbidden: false])
term4 = builder.term([uuId: englishTermUuid, name: "BBQ sauce", languageId: Locale.US.getCode(), dateModified: 1403509560L, userModified: "beko",
    status: "BLACKLISTED", forbidden: false])
term5 = builder.term([uuId: frenchTermUuid, name: "sauce barbecue", languageId: Locale.FRENCH.getCode(), dateModified: 1403509561L, userModified: "beko",
    status: "BLACKLISTED",  forbidden: false])
term6 = builder.term([uuId: germanyTermUuid, name: "Barbecue Soße", languageId: Locale.GERMANY.getCode(), dateModified: 1403509561L, userModified: "beko",
    status: "BLACKLISTED", forbidden: false])

description1 = builder.description([
    uuid: UUID.randomUUID().toString(),
    baseType: Description.ATTRIBUTE,
    type: "category",
    value: "food related"
])

languageTerms1 = ["en-US" : [term1] as Set, "fr-FR" : [term2] as Set] as Map
languageTerms2 = ["en-US" : [term4] as Set, "fr-FR" : [term2] as Set, "de-DE" : [term3] as Set] as Map
languageTerms3 = ["en-US" : [term4] as Set, "fr-FR" : [term5] as Set, "de-DE" : [term6] as Set] as Map

revision1 = builder.termEntry([uuId: UUID.randomUUID().toString(), dateCreated: 1403509560L, dateModified: 1303509560L,
    userCreated: "beko", userModified: "beko", languageTerms: languageTerms1, descriptions: [description1] as Set])
revision2 = builder.termEntry([uuId: UUID.randomUUID().toString(), dateCreated: 1403509561L, dateModified: 1303509561L,
    userCreated: "beko", userModified: "beko", languageTerms: languageTerms2, descriptions: [description1] as Set])
revision3 = builder.termEntry([uuId: UUID.randomUUID().toString(), dateCreated: 1403509562L, dateModified: 1303509562L,
    userCreated: "beko", userModified: "beko", languageTerms: languageTerms3, descriptions: [description1] as Set])

history1 = [revision1] as List
history2 = [revision2, revision1] as List
history3 = [
    revision3,
    revision2,
    revision1] as List

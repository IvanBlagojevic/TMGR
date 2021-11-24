package groovy.manualtask

import org.gs4tr.foundation.locale.Locale
import org.gs4tr.termmanager.model.ImportSummary
import org.gs4tr.termmanager.model.ImportSummary.CountWrapper
import org.gs4tr.termmanager.service.impl.ImportProgressInfo

importThreadName1 = UUID.randomUUID().toString()
importThreadName2 = UUID.randomUUID().toString()

importThreadNames = [importThreadName1] as List
batchImportThreadNames = [
        importThreadName1,
        importThreadName2] as List

importProgressInfo1 = new ImportProgressInfo(501)
importProgressInfo1.setPercentage(70)
importProgressInfo2 = new ImportProgressInfo(501)
importProgressInfo2.setPercentage(30)



english1 = new CountWrapper()
english1.getTermCount().add(44)
english1.getAddedApprovedCount().add(44)
english1.getAddedTermCount().add(49)
english1.getDeletedTerms().add(5)
//english1.getUpdatedTerms().add(15)
//english1.getUnchangedTermCount().add(5)

english2 = new CountWrapper()
english2.getTermCount().add(84)
english2.getAddedApprovedCount().add(84)
english2.getAddedTermCount().add(99)
english2.getDeletedTerms().add(15)
//english2.getUpdatedTerms().add(25)
//english2.getUnchangedTermCount().add(4)

germany1 = new CountWrapper()
germany1.getTermCount().add(39)
germany1.getAddedApprovedCount().add(39)
germany1.getAddedTermCount().add(49)
germany1.getDeletedTerms().add(10)
//germany1.getUpdatedTerms().add(10)
//germany1.getUnchangedTermCount().add(10)

germany2 = new CountWrapper()
germany2.getTermCount().add(10)
germany2.getAddedTermCount().add(15)
germany2.getAddedApprovedCount().add(10)
germany2.getDeletedTerms().add(5)
//germany2.getUpdatedTerms().add(10)
//germany2.getUnchangedTermCount().add(5)

germany3 = new CountWrapper()
germany3.getTermCount().add(1)
germany3.getAddedTermCount().add(1)
germany3.getAddedApprovedCount().add(1)

french1 = new CountWrapper()
french1.getTermCount().add(34)
french1.getAddedApprovedCount().add(34)
french1.getAddedTermCount().add(49)
french1.getDeletedTerms().add(15)
//french1.getUpdatedTerms().add(30)
//french1.getUnchangedTermCount().add(15)

french2 = new CountWrapper()
french2.getTermCount().add(100)
french2.getAddedApprovedCount().add(100)
french2.getAddedTermCount().add(115)
french2.getDeletedTerms().add(15)
//french2.getUpdatedTerms().add(30)
//french2.getUnchangedTermCount().add(8)

summary1 = new ImportSummary()
summary1.setImportId("20857cf1-e9d1-49dd-9100-3faa003b0b23␞Skype_SKY000001.tbx")
summary1.addImportedTargetLanguage(Locale.ENGLISH.getCode())
summary1.addImportedTargetLanguage(Locale.GERMANY.getCode())
summary1.addImportedTargetLanguage(Locale.FRANCE.getCode())

summary1.addImportedTermDescription("context")
summary1.addImportedTermEntryAttribute("definition")

summary1.getNoImportedTermEntryAttributes().add(49)
summary1.getNoImportedTermAttributes().add(115)
summary1.getNoImportedTermEntries().add(49)
summary1.getNoDeletedTermEntries().add(5)

summary1.getNoImportedTerms().add(117)

summary1.getNoImportedTermsPerLanguage().put(Locale.ENGLISH.getCode(), english1)
summary1.getNoImportedTermsPerLanguage().put(Locale.GERMANY.getCode(), germany1)
summary1.getNoImportedTermsPerLanguage().put(Locale.FRANCE.getCode(), french1)

summary2 = new ImportSummary()
summary2.setImportId("20857cf1-e9d1-49dd-9100-3faa003b0b23␞Nikon_NIK000001.tbx")
summary2.addImportedTargetLanguage(Locale.ENGLISH.getCode())
summary2.addImportedTargetLanguage(Locale.GERMANY.getCode())
summary2.addImportedTargetLanguage(Locale.FRANCE.getCode())

summary2.addImportedTermDescription("context")
summary2.addImportedTermEntryAttribute("definition")

summary2.getNoImportedTermEntryAttributes().add(49)
summary2.getNoImportedTermAttributes().add(215)
summary2.getNoImportedTermEntries().add(99)
summary2.getNoDeletedTermEntries().add(10)
summary2.getNoImportedTerms().add(194)

summary2.getNoImportedTermsPerLanguage().put(Locale.ENGLISH.getCode(), english2)
summary2.getNoImportedTermsPerLanguage().put(Locale.GERMANY.getCode(), germany2)
summary2.getNoImportedTermsPerLanguage().put(Locale.FRANCE.getCode(), french2)

summary3 = new ImportSummary()
summary3.setImportId("20857cf1-e9d1-49dd-9100-3faa003b0b23␞TERII_5767.xls")
summary3.addImportedTargetLanguage(Locale.GERMANY.getCode())
summary3.getNoImportedTerms().add(1)
summary3.getNoDuplicatedTerms().add(1)
summary3.getNoTermEntryForImport().add(1)
summary3.getUpdatedTermEntrieIds().add("20857cf1-e9d1-49dd-9100-3faa003b0b23")
summary3.getNoImportedTermsPerLanguage().put(Locale.GERMANY.getCode(), germany3)

preNumberOfTermsByLanguage = ["en": 1L, "de-DE": 1L, "fr-FR": 1L]
preNumberOfTermsByLanguage1 = ["en": 1L, "de-DE": 0L]

languages = [
        Locale.ENGLISH.getCode(),
        Locale.GERMANY.getCode(),
        Locale.FRANCE.getCode()] as List

languages1 = [
        Locale.ENGLISH.getCode(),
        Locale.GERMANY.getCode(),] as List

postNumberOfTermsByLanguage1 = ["en": 49L, "de-DE": 49L, "fr-FR": 39L]
postNumberOfTermsByLanguage2 = ["en": 137L, "de-DE": 64L, "fr-FR": 108L]
postNumberOfTermsByLanguage3 = ["en": 1L, "de-DE": 1L]

preImportCounts = builder.projectTerminologyCounts([projectId              : 1L, languages: languages, numberOfTermEntries: 1,
                                                    numberOfTermsByLanguage: preNumberOfTermsByLanguage])
preImportCounts1 = builder.projectTerminologyCounts([projectId              : 1L, languages: languages1, numberOfTermEntries: 1,
                                                     numberOfTermsByLanguage: preNumberOfTermsByLanguage1])
postImportCounts1 = builder.projectTerminologyCounts([projectId              : 1L, languages: languages, numberOfTermEntries: 45,
                                                      numberOfTermsByLanguage: postNumberOfTermsByLanguage1])
postImportCounts2 = builder.projectTerminologyCounts([projectId              : 1L, languages: languages, numberOfTermEntries: 110,
                                                      numberOfTermsByLanguage: postNumberOfTermsByLanguage2])
postImportCounts3 = builder.projectTerminologyCounts([projectId              : 1L, languages: languages1, numberOfTermEntries: 1,
                                                      numberOfTermsByLanguage: postNumberOfTermsByLanguage3])

summary1.setPreImportCounts(preImportCounts)
summary2.setPreImportCounts(preImportCounts)
summary3.setPreImportCounts(preImportCounts1)

importProgressInfo3 = new ImportProgressInfo(49)
importProgressInfo3.setImportSummary(summary1)
importProgressInfo3.setPercentage(100)

importProgressInfo4 = new ImportProgressInfo(99)
importProgressInfo4.setImportSummary(summary2)
importProgressInfo4.setPercentage(100)

importProgressInfo5 = new ImportProgressInfo(470)
importProgressInfo5.setCanceled(true)
importProgressInfo5.setPercentage(30)

importProgressInfo6 = new ImportProgressInfo(1)
importProgressInfo6.setImportSummary(summary3)
importProgressInfo6.setPercentage(100)


importInfos1 = [importThreadName: importProgressInfo1] as Map
importInfos2 = [importThreadName: importProgressInfo3] as Map
batchImportInfos1 = [importThreadName1: importProgressInfo1, importThreadName2: importProgressInfo2] as Map
batchImportInfos2 = [importThreadName1: importProgressInfo3, importThreadName2: importProgressInfo4] as Map
batchImportInfos3 = [importThreadName1: importProgressInfo5] as Map
batchImportInfos4 = [importThreadName1: importProgressInfo6] as Map

importProgressCommand = builder.checkImportProgressCommand([importThreadNames: importThreadNames])
batchImportProgressCommand = builder.checkImportProgressCommand([importThreadNames: batchImportThreadNames])



package groovy.manualtask

import org.gs4tr.foundation.locale.Locale
import org.gs4tr.termmanager.model.AttributeLevelEnum
import org.gs4tr.termmanager.model.BaseTypeEnum

command1 = builder.generateImportTemplateCommand([projectId: 1L, languages: Collections.singletonList(Locale.US.getCode())])

languages = [
        Locale.ENGLISH.getCode(),
        Locale.GERMANY.getCode(),
        Locale.FRENCH.getCode()] as List

command2 = builder.generateImportTemplateCommand([projectId: 1L, languages: languages])

organizationInfo = builder.organizationInfo([name: "TPT", domain: "DOMAIN", enabled: true])
tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKY000001"])

english = builder.projectLanguage([language: Locale.ENGLISH.getCode()])
germany = builder.projectLanguage([language: Locale.GERMANY.getCode()])
french = builder.projectLanguage([language: Locale.FRENCH.getCode()])
projectLanguages = [english, germany, french] as Set

definition = builder.attribute([
        attributeLevel: AttributeLevelEnum.TERMENTRY,
        baseTypeEnum  : BaseTypeEnum.DESCRIPTION,
        name          : "Definition",
        value         : "Term entry attribute value"
])
context = builder.attribute([
        attributeLevel: AttributeLevelEnum.TERMENTRY,
        baseTypeEnum  : BaseTypeEnum.DESCRIPTION,
        name          : "Context",
        value         : "Term entry context value"
])

techPubsFiltration = builder.attribute([
        attributeLevel: AttributeLevelEnum.LANGUAGE,
        baseTypeEnum  : BaseTypeEnum.DESCRIPTION,
        name          : "TechPubs-Filtration",
        value         : "TechPubs-Filtration value"
])
techPubsElectrical = builder.attribute([
        attributeLevel: AttributeLevelEnum.LANGUAGE,
        baseTypeEnum  : BaseTypeEnum.DESCRIPTION,
        name          : "TechPubs-Electrical",
        value         : "TechPubs-Electrical value"
])

softwareElectrical = builder.attribute([
        attributeLevel: null,
        baseTypeEnum  : BaseTypeEnum.NOTE,
        name          : "Software-Electrical",
        value         : "Software-Electrical value"
])
softwareFiltration = builder.attribute([
        attributeLevel: null,
        baseTypeEnum  : BaseTypeEnum.NOTE,
        name          : "Software-Filtration",
        value         : "Software-Filtration value"
])

attributes = [
        definition,
        context,
        techPubsFiltration,
        techPubsElectrical,
        softwareElectrical,
        softwareFiltration] as List


tmProject1 = builder.tmProject([organization: tmOrganization, projectId: 1L, projectInfo: projectInfo, projectLanguages: projectLanguages,
                                attributes  : [definition, context] as List])

tmProject2 = builder.tmProject([organization: tmOrganization, projectId: 1L, projectInfo: projectInfo, projectLanguages: projectLanguages,
                                attributes  : [
                                        techPubsFiltration,
                                        softwareElectrical] as List])
tmProject3 = builder.tmProject([organization: tmOrganization, projectId: 1L, projectInfo: projectInfo, projectLanguages: projectLanguages,
                                attributes  : attributes])

expectedHeaders1 = ["Context",
                    "Definition",
                    "en-US",
                    "en-US:Status"] as List

expectedHeaders2 = ["en-US",
                    "en-US:Status",
                    "en-US:TechPubs-Filtration",
                    "en-US:Note:Software-Electrical"] as List

expectedHeaders3 = ["Context",
                    "Definition",
                    "en",
                    "en:Status",
                    "en:TechPubs-Filtration",
                    "en:TechPubs-Electrical",
                    "en:Note:Software-Filtration",
                    "en:Note:Software-Electrical",
                    "de-DE",
                    "de-DE:Status",
                    "de-DE:TechPubs-Filtration",
                    "de-DE:TechPubs-Electrical",
                    "de-DE:Note:Software-Filtration",
                    "de-DE:Note:Software-Electrical",
                    "fr",
                    "fr:Status",
                    "fr:TechPubs-Filtration",
                    "fr:TechPubs-Electrical",
                    "fr:Note:Software-Filtration",
                    "fr:Note:Software-Electrical"] as List

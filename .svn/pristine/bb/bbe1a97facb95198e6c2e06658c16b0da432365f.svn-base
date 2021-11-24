package groovy.webservice

import org.gs4tr.termmanager.model.AttributeLevelEnum
import org.gs4tr.termmanager.model.BaseTypeEnum
import org.gs4tr.termmanager.model.InputFieldTypeEnum
import org.gs4tr.termmanager.model.TermEntryAttributeTypeEnum

termEntryDescription = builder.description([type: "definition", value: "term entry definition", baseType: "ATTRIBUTE"])
termEntryDescriptions = [termEntryDescription] as Collection

termDescription = builder.description([type: "context", value: "term context", baseType: "ATTRIBUTE"])
termDescription01 = builder.description([type: "context", value: "term context", baseType: "ATTRIBUTE", uuid: "474e93ae-7256-1724-9d71-desc00000001"])
termComboAttributeDescription = builder.description([type: "comboAttribute", value: "val1", baseType: "ATTRIBUTE"])
termDescriptions = [termDescription] as Collection
termDescriptions01 = [termDescription01] as Collection

termDescriptionNullValue = builder.description([value: null, type: "definition", baseType: "ATTRIBUTE"])
termDescriptionNullType = builder.description([value: "term context", type: null, baseType: "ATTRIBUTE"])
termDescriptionNullBaseType = builder.description([value: "term context", type: "definition", baseType: null])

termDescriptionEmptyStringValue = builder.description([value: "", type: "definition", baseType: "ATTRIBUTE"])
termDescriptionEmptyStringType = builder.description([value: "term context", type: "", baseType: "ATTRIBUTE"])
termDescriptionEmptyStringBaseType = builder.description([value: "term context", type: "definition", baseType: ""])

termNullValueDescriptions = [termDescriptionNullValue] as Collection
termNullTypeDescriptions = [termDescriptionNullType] as Collection
termNullBaseTypeDescriptions = [termDescriptionNullBaseType] as Collection

termEmptyStringValueDescriptions = [termDescriptionEmptyStringValue] as Collection
termEmptyStringTypeDescriptions = [termDescriptionEmptyStringType] as Collection
termEmptyStringBaseTypeDescriptions = [termDescriptionEmptyStringBaseType] as Collection


term = builder.termV2ModelExtended([locale      : "en-US", forbidden: false, termText: "Added term",
                                    descriptions: termDescriptions])
term01 = builder.termV2ModelExtended([locale      : "en-US", forbidden: false, termText: "Edited term",
                                      descriptions: termDescriptions01, termId: "474e93ae-7256-9588-9d71-term00000001"])
terms = [term] as Collection
terms01 = [term, term01] as Collection

validAddTermsCommand = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q",
                                                termEntryDescriptions: termEntryDescriptions, terms: terms])

updateTermEntryRestV2 = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q", termEntryId: "474e93ae-7264-4088-9d54-entry00000001",
                                                 termEntryDescriptions: termEntryDescriptions, terms: terms01])

nullValueAddTermsCommand = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q",
                                                    termEntryDescriptions: termNullValueDescriptions, terms: terms])
nullTypeAddTermsCommand = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q",
                                                   termEntryDescriptions: termNullTypeDescriptions, terms: terms])
nullBaseTypeAddTermsCommand = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q",
                                                       termEntryDescriptions: termNullBaseTypeDescriptions, terms: terms])

emptyStringValueAddTermsCommand = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q",
                                                           termEntryDescriptions: termEmptyStringValueDescriptions, terms: terms])
emptyStringTypeAddTermsCommand = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q",
                                                          termEntryDescriptions: termEmptyStringTypeDescriptions, terms: terms])
emptyStringBaseTypeAddTermsCommand = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q",
                                                              termEntryDescriptions: termEmptyStringBaseTypeDescriptions, terms: terms])

statusProcessed = builder.itemStatusType(name: "processed")
date = new Date();

organizationInfo = builder.organizationInfo([name : "My First Organization", domain: "DOMAIN", enabled: true,
                                             theme: "THEME", currencyCode: null])

tmOrganization = builder.tmOrganization([organizationId: 1L, organizationInfo: organizationInfo, parentOrganization: null])

projectDetail = builder.projectDetail([activeSubmissionCount: 0L, approvedTermCount: 15L, completedSubmissionCount: 0L,
                                       dateModified         : date, forbiddenTermCount: 0, languageCount: 2, languageDetails: null,
                                       project              : null, projectDetailId: 1L, termCount: 15L, termEntryCount: 7L, termInSubmissionCount: 0L, userDetails: null])

projectInfo = builder.projectInfo([clientIdentifier: null, enabled: true, name: "Skype", shortCode: "SKYPE0001"])

projectLanguage1 = builder.projectLanguage([language: "en-US"])
projectLanguage2 = builder.projectLanguage([language: "de-DE"])

projectLanguages = [
        projectLanguage1,
        projectLanguage2] as Set

termEntryAttribute = builder.attribute([attributeLevel        : AttributeLevelEnum.TERMENTRY, name: "definition", value: "Term entry definition value",
                                        termEntryAttributeType: TermEntryAttributeTypeEnum.TEXT, inputFieldTypeEnum: InputFieldTypeEnum.TEXT,
                                        baseTypeEnum          : BaseTypeEnum.DESCRIPTION])
termAttribute = builder.attribute([attributeLevel        : AttributeLevelEnum.LANGUAGE, name: "context", value: "Term context",
                                   termEntryAttributeType: TermEntryAttributeTypeEnum.TEXT, inputFieldTypeEnum: InputFieldTypeEnum.TEXT,
                                   baseTypeEnum          : BaseTypeEnum.DESCRIPTION])

attributes = [
        termEntryAttribute,
        termAttribute] as List

project = builder.tmProject([addApprovedTerms: false, announcements: null, attributes: null, defaultTermStatus: statusProcessed,
                             metadata        : null, organization: tmOrganization, projectDetail: projectDetail, projectId: 1L, projectInfo: projectInfo,
                             projectLanguages: projectLanguages, attributes: attributes])

term1 = builder.termV2ModelExtended([termText: "source test term", locale: "en-US", forbidden: false, descriptions: termDescriptions])
term2 = builder.termV2ModelExtended([termText: "target test term", locale: "de-DE", forbidden: false, descriptions: termDescriptions])
term3 = builder.termV2ModelExtended([termText: "target test term", locale: "fr-FR", forbidden: false, descriptions: termDescriptions])

term4 = builder.termV2ModelExtended([termText: "target test term 4", locale: "en-US", forbidden: false, descriptions: [termComboAttributeDescription] as Collection])
term5 = builder.termV2ModelExtended([termText: "target test term 5", locale: "de-DE", forbidden: false, descriptions: [termComboAttributeDescription] as Collection])
term6 = builder.termV2ModelExtended([termText: "source test term", locale: "en-US", forbidden: false, status: "Approved"])

terms1 = [term1, term2, term3] as Collection
terms2 = [term1, term2] as Collection
terms3 = [term6] as Collection

termEntryContextDescription = builder.description([type: "context", value: "Term entry context value", baseType: "ATTRIBUTE"])
termEntryDescriptions1 = [termEntryContextDescription] as Collection

addTermsCommand = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q",
                                           termEntryDescriptions: termEntryDescriptions, terms: terms1])
addTermsCommand1 = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q",
                                            termEntryDescriptions: termEntryDescriptions, terms: terms2])
addTermsCommand2 = builder.addTermsCommand([projectTicket        : "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q",
                                            termEntryDescriptions: termEntryDescriptions1, terms: terms1])

addApprovedTermsCommand = builder.addTermsCommand([projectTicket: "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q", terms : terms3])

addComboAttributesTermsCommand = builder.addTermsCommand([projectTicket: "4YESyxwCtA1Wj8ujUmCA18npTWF1uB/q", terms: [term4, term5] as Collection])

projectAttribute1 = builder.attribute([baseTypeEnum: BaseTypeEnum.DESCRIPTION, name: "definition"])
projectAttribute2 = builder.attribute([baseTypeEnum: BaseTypeEnum.DESCRIPTION, name: "context"])
projectComboAttribute = builder.attribute([attributeLevel: AttributeLevelEnum.LANGUAGE, inputFieldTypeEnum: InputFieldTypeEnum.COMBO, baseTypeEnum: BaseTypeEnum.DESCRIPTION, name: "comboAttribute", comboValues: "val1|val2"])

attributesList = [projectAttribute1, projectAttribute2] as List

projectAttributesMap = [1L: attributesList] as Map

projectComboAttributesMap = [1L: [projectComboAttribute] as List]
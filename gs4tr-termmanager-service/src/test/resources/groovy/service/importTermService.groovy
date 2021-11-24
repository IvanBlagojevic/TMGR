
projectUserLanguage1= builder.projectUserLanguage([language: "en-US"])
projectUserLanguage2= builder.projectUserLanguage([language: "de-DE"])
projectUserLanguage3= builder.projectUserLanguage([language: "fr-FR"])

statistics1 =  builder.statistics([statisticsId: 1L, projectUserLanguage: projectUserLanguage1, reportType: "weekly"])
statistics2 =  builder.statistics([statisticsId: 2L, projectUserLanguage: projectUserLanguage2, reportType: "weekly"])
statistics3 =  builder.statistics([statisticsId: 3L, projectUserLanguage: projectUserLanguage3, reportType: "weekly"])

statistics4 =  builder.statistics([statisticsId: 4L, projectUserLanguage: projectUserLanguage1, reportType: "daily"])
statistics5 =  builder.statistics([statisticsId: 5L, projectUserLanguage: projectUserLanguage2, reportType: "daily"])
statistics6 =  builder.statistics([statisticsId: 6L, projectUserLanguage: projectUserLanguage3, reportType: "daily"])


statistics = [
    statistics1,
    statistics2,
    statistics3,
    statistics4,
    statistics5,
    statistics6
]as List



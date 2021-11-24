package groovy.projectTerminologyCounts

import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts.LanguageTermCount
import org.gs4tr.foundation.locale.Locale;

languages = [
    Locale.ENGLISH.getCode(),
    Locale.GERMANY.getCode(),
    Locale.FRANCE.getCode()] as List

englishTermCount = new LanguageTermCount();
englishTermCount.setApproved(50)
englishTermCount.setTermCount(50)

germanyTermCount = new LanguageTermCount();
germanyTermCount.setApproved(50)
germanyTermCount.setTermCount(50)

frenchTermCount = new LanguageTermCount();
frenchTermCount.setApproved(50)
frenchTermCount.setTermCount(50)

termCountByLanguage = ["en": englishTermCount, "de-DE": germanyTermCount, "fr-FR": frenchTermCount] as Map;

termCounts = builder.facetTermCounts([termCountByLanguage: termCountByLanguage])
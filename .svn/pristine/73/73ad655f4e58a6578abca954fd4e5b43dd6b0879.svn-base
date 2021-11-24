package groovy.projectTerminologyCounts

import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts.LanguageTermCount
import org.gs4tr.foundation.locale.Locale;

languages = [
    Locale.ENGLISH.getCode(),
    Locale.GERMANY.getCode(),
    Locale.FRANCE.getCode()] as List

englishTermCount = new LanguageTermCount();
englishTermCount.setApproved(0)
englishTermCount.setTermCount(0)

germanyTermCount = new LanguageTermCount();
germanyTermCount.setApproved(0)
germanyTermCount.setTermCount(0)

frenchTermCount = new LanguageTermCount();
frenchTermCount.setApproved(0)
frenchTermCount.setTermCount(0)

termCountByLanguage = ["en": englishTermCount, "de-DE": germanyTermCount, "fr-FR": frenchTermCount] as Map;

termCounts = builder.facetTermCounts([termCountByLanguage: termCountByLanguage])
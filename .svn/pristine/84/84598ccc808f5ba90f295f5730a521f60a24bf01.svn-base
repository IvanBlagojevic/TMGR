package groovy.fileAnalysis

import org.springframework.http.MediaType;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;

userProjectLanguages = [
    Locale.US.getCode(),
    Locale.ITALIAN.getCode(),
    Locale.FRANCE.getCode()
] as Set

directory = UUID.randomUUID().toString();

starwoodResourceInfo = builder.resourceInfo([mimeType: MediaType.APPLICATION_OCTET_STREAM_VALUE, name: "Starwood.xls", path: directory])
starwood = builder.repositoryItem([resourceInfo: starwoodResourceInfo]);

projectAttributeNames = [
    "context",
    "definition"] as Set

attributesByLevel = new HashMap<Level, Set<String>>(1);
attributesByLevel.put(Level.TERM_ATTRIBUTE, projectAttributeNames);
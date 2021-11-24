package groovy.fileAnalysis

import org.gs4tr.foundation.locale.Locale;
import org.springframework.http.MediaType;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;

userProjectLanguages = [
    Locale.ENGLISH.getCode(),
    Locale.GERMANY.getCode(),
    Locale.FRENCH.getCode()
] as Set

directory = UUID.randomUUID().toString();

skypeResourceInfo = builder.resourceInfo([mimeType: MediaType.APPLICATION_OCTET_STREAM_VALUE, name: "Skype.xls", path: directory])
medraResourceInfo = builder.resourceInfo([mimeType: MediaType.APPLICATION_OCTET_STREAM_VALUE, name: "Medra.xls", path: directory])

skype = builder.repositoryItem([resourceInfo: skypeResourceInfo]);
medra = builder.repositoryItem([resourceInfo: medraResourceInfo]);

projectAttributeNames = [
    "context",
    "definition"] as Set

attributesByLevel = new HashMap<Level, Set<String>>();
attributesByLevel.put(Level.TERM_ATTRIBUTE, projectAttributeNames);
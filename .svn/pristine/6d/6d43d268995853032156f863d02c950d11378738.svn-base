package groovy.fileAnalysis

import org.springframework.http.MediaType;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;

userProjectLanguages = [
    Locale.ENGLISH.getCode(),
    Locale.GERMANY.getCode(),
    Locale.FRENCH.getCode()
] as Set

directory = UUID.randomUUID().toString();

zipResourceInfo = builder.resourceInfo([mimeType: MediaType.APPLICATION_OCTET_STREAM_VALUE, name: "xls_tbx.zip", path: directory])
zip = builder.repositoryItem([resourceInfo: zipResourceInfo]);


projectAttributeNames = [
    "Usage",
    "definition"] as Set

attributesByLevel = new HashMap<Level, Set<String>>(1);
attributesByLevel.put(Level.TERM_ATTRIBUTE, projectAttributeNames);
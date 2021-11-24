package groovy.fileAnalysis

import org.gs4tr.foundation.locale.Locale;
import org.springframework.http.MediaType;

userProjectLanguages = [
    Locale.ENGLISH.getCode(),
    Locale.GERMANY.getCode(),
    Locale.FRENCH.getCode()
] as Set

directory = UUID.randomUUID().toString();

skypeResourceInfo = builder.resourceInfo([mimeType: MediaType.APPLICATION_OCTET_STREAM_VALUE, name: "Skype.xls", path: directory])
nikonResourceInfo = builder.resourceInfo([mimeType: MediaType.APPLICATION_OCTET_STREAM_VALUE, name: "Nikon.xls", path: directory])

skype = builder.repositoryItem([resourceInfo: skypeResourceInfo]);
nikon = builder.repositoryItem([resourceInfo: nikonResourceInfo]);
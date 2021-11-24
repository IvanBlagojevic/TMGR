package groovy.fileAnalysis

import org.gs4tr.foundation.locale.Locale
import org.springframework.http.MediaType

userProjectLanguages = [
        Locale.ENGLISH.getCode()
] as Set

directory = UUID.randomUUID().toString();

invalidResourceInfo = builder.resourceInfo([mimeType: MediaType.APPLICATION_OCTET_STREAM_VALUE, name: "Invalid.xls", path: directory])

invalid = builder.repositoryItem([resourceInfo: invalidResourceInfo]);
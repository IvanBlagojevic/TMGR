package groovy.fileAnalysis

import java.util.HashMap
import java.util.Map

import org.gs4tr.foundation.locale.Locale;
import org.springframework.http.MediaType;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;

userProjectLanguages = [
    Locale.US.getCode(),
    Locale.GERMANY.getCode(),
    Locale.FRENCH.getCode()
] as Set

directory = UUID.randomUUID().toString();

hiltonResourceInfo = builder.resourceInfo([mimeType: MediaType.APPLICATION_OCTET_STREAM_VALUE, name: "Hilton.xls", path: directory])
medronicResourceInfo = builder.resourceInfo([mimeType: MediaType.APPLICATION_OCTET_STREAM_VALUE, name: "Medronic.xls", path: directory])


hilton = builder.repositoryItem([resourceInfo: hiltonResourceInfo]);
medronic = builder.repositoryItem([resourceInfo: medronicResourceInfo]);

projectAttributeNames = [
    "CONTEXT",
    "definition",
    "combo"] as Set

attributesByLevel = new HashMap<Level, Set<String>>(1);
attributesByLevel.put(Level.TERM_ATTRIBUTE, projectAttributeNames);

comboValuesPerAttribute = new HashMap<String, Set<String>>(1);
comboValues = [
    "combo value1",
    "COMBO value2"] as Set

comboValuesPerAttribute.put("combo", comboValues);

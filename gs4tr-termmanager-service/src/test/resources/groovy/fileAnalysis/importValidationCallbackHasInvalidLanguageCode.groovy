package groovy.fileAnalysis

import org.gs4tr.termmanager.service.impl.ImportValidationCallback;


importValidationCallback = new ImportValidationCallback(1);

languageMap = importValidationCallback.getLanguageMap();
languageMap.put("en-MK", 2);
languageMap.put("de-DE", 4);
languageMap.put("fr-FR", 2);

importValidationCallback.setFileName("import.xls");

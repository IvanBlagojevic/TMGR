package org.gs4tr.termmanager.model;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public enum ExportLanguageCriteriaEnum {

    BILINGUAL("Bilingual"), MONOLINGUAL("Monolingual"), MULTILINGUAL("Multilingual");

    public static ExportLanguageCriteriaEnum getInstance(String sourceLocale, List<String> targetLocales) {
	if (CollectionUtils.isEmpty(targetLocales)) {
	    return MONOLINGUAL;
	} else if (sourceLocale != null && targetLocales != null && targetLocales.size() == 1) {
	    return BILINGUAL;
	} else {
	    return MULTILINGUAL;
	}
    }

    private String _typeName;

    private ExportLanguageCriteriaEnum(String typeName) {
	_typeName = typeName;
    }

    public String getTypeName() {
	return _typeName;
    }

}
